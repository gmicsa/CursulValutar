var URL = document.location + 'services/rates/';
var selectedDateFormatted, selectedCurrency;
var aggregatedBankRates = {};
var bnrReferenceRate = {};
var globalCache = {}, globalBNRCache = {};
var cookieColumn = 'column-index', cookieLang = 'i18next';
var referenceBeginDate = new Date(2012, 2, 1);
var langRO = "ro-RO", langEN = "en-EN", lang = langRO;

$(function(){
    initLang();
    $("input:submit, button").button();
    $( "#refresh" ).click(function() {
        retrieveRates();
    });
    $( "#about" ).click(function() {
            var dialog = $("#helpPanel");
	    var windowWidth = $(window).width();
            dialog.dialog({
                width: windowWidth<=600?windowWidth-15:600,
                title: $.t('app.about_title'),
                modal: true
            });
    });
    $('#datepicker').datepicker({
        dateFormat: 'dd/mm/yy', 
        firstDay: 1,
        beforeShowDay: $.datepicker.noWeekends,
        minDate: referenceBeginDate,
        maxDate: "+0d"
    });    
    $('#datepicker').datepicker("setDate", getBucharestLastNonWeekendDate());
    $("#resultsTable").addClass("ui-widget");
    $("#resultsTable thead").addClass("ui-widget-header");
    $("#resultsTable tbody").addClass("ui-widget-content");
    addCurrencyEntries();
    tableSorter = $("#resultsTable").tablesorter();
    addEvents();
    retrieveRates();
});

function initLang(langLocal){
    var currentLang = langRO;
    if(langLocal){
        currentLang = langLocal;
    }else{
        var cookieLangValue = $.cookies.get(cookieLang);
        if(cookieLangValue){
            currentLang = cookieLangValue;
        }
    }
    lang = currentLang;
    $.i18n.init({
        lng: currentLang,
        useLocalStorage: false,
        debug: true,
        cookieExpirationTime: 30 * 24 * 60 // 30 days
    }, function(t) {
            $("html").i18n();
            var langSelectedClass = "langSelected";
            var langNonSelectedClass = "lang";
            if(currentLang == langRO){
                $("#roLangImage").addClass(langSelectedClass);
                $("#roLangImage").removeClass(langNonSelectedClass);
                $("#enLangImage").addClass(langNonSelectedClass);
                $("#enLangImage").removeClass(langSelectedClass);
            }else{
                $("#enLangImage").addClass(langSelectedClass);
                $("#enLangImage").removeClass(langNonSelectedClass);
                $("#roLangImage").addClass(langNonSelectedClass);
                $("#roLangImage").removeClass(langSelectedClass);
            }
    });
}

function addEvents(){
    $( "#appTitle" ).click(function() {
        window.location = '/';
    });
    $( "#alerts" ).click(function() {
        window.location = '/html/alerts.html';
    });
    $('#loadingImage')
    .ajaxStart(function() {
        $(this).show();
        $("#refresh" ).button("disable");
    })
    .ajaxStop(function() {
        $(this).hide();
        $("#refresh" ).button("enable");
    });
    $("#resultsTable").bind("sortEnd",function() { 
        if($.cookies.test()) {
            var date = new Date();
            // 7 day cookie
            date.setTime(date.getTime() + (7 * 24 * 60 * 60 * 1000));
            $.cookies.set(cookieColumn, this.config.sortList, {expiresAt: date});
        }
    });
    $("#roLangImage").click(function (){
            if(lang != langRO){
                    initLang(langRO);
            }
    });
    $("#enLangImage").click(function (){
            if(lang != langEN){
                    initLang(langEN);
            }
    });
}

function showDialog(title, message, type){
    var dialog = $("#dialog" );
    dialog.html('<div class=\''+ type + '\'>'+ message +'</div>');

    dialog.dialog({
        height: 170,
        title: title,
        modal: true
    });
}

function showErrorDialog(httpStatus){
    var errorMsg = $.t('error.request_error');
    var title = $.t('error.error');
    if(httpStatus == 404){
        errorMsg = $.t('error.no_rates_found');
        title = '';
    }
    showDialog(title, errorMsg, 'error');
}

function addCurrencyEntries(){
    
    // available currencies are EUR, USD, CHF, GBP, AUD, DKK, HUF, JPY, NOK, SEK
    var currencyArray = ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'];
    var currencyOptions = '';
    for(var i = 1; i < currencyArray.length + 1; i++) {
        currencyOptions = currencyOptions + '<option value="' + i + '">' + currencyArray[i - 1] + '</option>';
    }
    
    $("#currencypicker").append(currencyOptions);
}

function retrieveRates(){
    var selectedDate = $('#datepicker').datepicker("getDate");
    
    var timestamp = Date.parse(selectedDate);
    if(!isNaN(timestamp)) {
        selectedDateFormatted =  $.datepicker.formatDate('yy-mm-dd', selectedDate);
    } else {
        showDialog($.t('error.error'), $.t('error.bad_date'), 'error');
        return;
    }
    
    var selectedDateAsDate = new Date(timestamp);
    if(selectedDateAsDate.getDay() == 6 || selectedDateAsDate.getDay() == 0) {
        showDialog($.t('error.info'), $.t('error.weekend'), 'info');
        return;
    }
    if(selectedDateAsDate < referenceBeginDate) {
        showDialog($.t('error.info'), $.t('error.start'), 'info');
        return;
    }
    
    selectedCurrency = $('#currencypicker option:selected').text();
    var completeURL = URL+selectedDateFormatted+'/'+selectedCurrency;
    
    var cachedRates = getFromCache(selectedDateFormatted, selectedCurrency);
    var cachedBNRRate = getFromBNRCache(selectedDateFormatted, selectedCurrency);
    
    if(cachedRates == null){
        // retrieve data from REST service
        $.ajax({
            url: completeURL,
            error: function(jqXHR, textStatus, errorThrown){
                showErrorDialog(jqXHR.status);
            },
            success: function(data){
                aggregatedBankRates = {};
                for(var i=0; i<data.rates.length; i++) {
                    var rate = data.rates[i];
                    if(rate.bankName == 'BNR'){
                        bnrReferenceRate = {};
                        bnrReferenceRate.bankName = rate.bankName;
                        bnrReferenceRate.currencyType = rate.currencyType;
                        bnrReferenceRate.lastChangedAt = rate.lastChangedAt;
                        bnrReferenceRate.date = rate.date;
                        bnrReferenceRate.value = rate.value.toFixed(4);
                        bnrReferenceRate.variance = getNumberWithSign(rate.evolution.toFixed(4));
                        bnrReferenceRate.variancePercent = getNumberWithSign((100*bnrReferenceRate.variance/(bnrReferenceRate.value-bnrReferenceRate.variance)).toFixed(4))+"%";                    
                    }else{
                        var bankName = rate.bankName;
                        if(aggregatedBankRates[bankName] === undefined){
                            aggregatedBankRates[bankName] = new Object();
                        }
                        aggregatedBankRates[bankName].bankName = rate.bankName;
                        aggregatedBankRates[bankName].currencyType = rate.currencyType;
                        aggregatedBankRates[bankName].lastChangedAt = rate.lastChangedAt;
                        aggregatedBankRates[bankName].date = rate.date;
                        if(rate.transactionType == "BUY"){
                            aggregatedBankRates[bankName].buyValue = rate.value.toFixed(4);
                            aggregatedBankRates[bankName].buyVariance = getNumberWithSign(rate.evolution.toFixed(4));
                            aggregatedBankRates[bankName].buyPercentVariance = getNumberWithSign((100*rate.evolution/(rate.value-rate.evolution)).toFixed(4))+"%";
                        }else{
                            aggregatedBankRates[bankName].sellValue = rate.value.toFixed(4);
                            aggregatedBankRates[bankName].sellVariance = getNumberWithSign(rate.evolution.toFixed(4));
                            aggregatedBankRates[bankName].sellPercentVariance = getNumberWithSign((100*rate.evolution/(rate.value-rate.evolution)).toFixed(4))+"%";
                        }
                    }
                }
                putInCache(selectedDateFormatted, selectedCurrency, aggregatedBankRates);
                putInBNRCache(selectedDateFormatted, selectedCurrency, bnrReferenceRate);
                fillRatesTable();
                updateBNRReferenceRate();
                $("#bnrReferencePanel, #resultsTable").i18n();
            }
        });
    }else{
        //use cached data
        aggregatedBankRates = cachedRates;
        bnrReferenceRate = cachedBNRRate;
        fillRatesTable();
        updateBNRReferenceRate();
        $("#bnrReferencePanel, #resultsTable").i18n();
    }
}

function putInCache(day, currency, values){
    if(globalCache[day] === undefined){
       globalCache[day] = {}; 
    }
    var cacheDay = globalCache[day];
    cacheDay.requestedAt = getBucharestDate(new Date());
    cacheDay[currency] = values;
}

function putInBNRCache(day, currency, value){
    if(globalBNRCache[day] === undefined){
       globalBNRCache[day] = {};
    }
    var cacheDay = globalBNRCache[day];
    cacheDay[currency] = value;
}

function invalidateCacheIfNeeded(day){
    var now = new Date();
    var today =  $.datepicker.formatDate('yy-mm-dd', now);
    if(day != today){
        //dont invalidate cache for older days than today (data does not change any more)
        return;
        
    }
    var cache = globalCache[day];
    if(cache === undefined){
       return;
    }
    var lastRequestedAt = cache.requestedAt;
    var nowBucharest = getBucharestDate(now);
    var hour08 = getBucharestDateCronExecuted(now, 8);
    if(lastRequestedAt < hour08 && nowBucharest>=hour08){
        globalCache[day] = {};
        globalBNRCache[day] = {};
        return;
    }
    var hour10 = getBucharestDateCronExecuted(now, 10);
    if(lastRequestedAt < hour10 && nowBucharest>=hour10){
        globalCache[day] = {};
        globalBNRCache[day] = {};
        return;
    }
    var hour12 = getBucharestDateCronExecuted(now, 12);
    if(lastRequestedAt < hour12 && nowBucharest>=hour12){
        globalCache[day] = {};
        globalBNRCache[day] = {};
        return;
    }
    var hour14 = getBucharestDateCronExecuted(now, 14);
    if(lastRequestedAt < hour14 && nowBucharest>=hour14){
        globalCache[day] = {};
        globalBNRCache[day] = {};
        return;
    }
    var hour16 = getBucharestDateCronExecuted(now, 16);
    if(lastRequestedAt < hour16 && nowBucharest>=hour16){
        globalCache[day] = {};
        globalBNRCache[day] = {};
        return;
    }
    var hour18 = getBucharestDateCronExecuted(now, 18);
    if(lastRequestedAt < hour18 && nowBucharest>=hour18){
        globalCache[day] = {};
        globalBNRCache[day] = {}; 
        return;
    }
}

function getFromCache(day, currency){
    invalidateCacheIfNeeded(day);
    if(globalCache[day] === undefined){
       return null; 
    }
    var cacheDay = globalCache[day];
    if(cacheDay[currency] === undefined){
       return null; 
    }
    return cacheDay[currency];
}

function getFromBNRCache(day, currency){
    if(globalBNRCache[day] === undefined){
       return null;
    }
    var cacheDay = globalBNRCache[day];
    if(cacheDay[currency] === undefined){
       return null;
    }
    return cacheDay[currency];
}

function getBucharestDate(d) {
    var offset = 2;
    var utc = d.getTime() + (d.getTimezoneOffset() * 60000);
    var nd = new Date(utc + (3600000*offset));
    return nd;
}

function getBucharestLastNonWeekendDate() {
    var dayToday = getBucharestDate(new Date());
    var day_of_week = dayToday.getDay();
    
    var one_day_milliseconds = 24 * 3600 * 1000, offset = 0;
    
    if(day_of_week == 6) {
        offset = 1;
    }
    if(day_of_week == 0) {  // Sunday is considered day 0
        offset = 2;
    }
    if(offset != 0) {
        dayToday.setTime(dayToday.getTime() - one_day_milliseconds * offset);
    }
    
    return dayToday;
}

function getBucharestDateCronExecuted(d,h) {
    var date = getBucharestDate(d);
    date.setHours(h, 2);// assume that the cron is executed in max 2 minutes
    return date;
}

function fillRatesTable(){
    $("#resultsTable tbody tr").remove();
    var tableBody = $("#resultsTable tbody");
    var rateString = '';
    for (var rateKey in aggregatedBankRates) {
        var rate = aggregatedBankRates[rateKey];
        rateString += '<tr><td style=\'text-align:right;\'>';
        rateString += rate.bankName;
        rateString += '<a class=\'smallText hoverLink\' data-i18n="[title]button.history_tooltip" onclick="retrieveHistory(\''+ rate.bankName + '\')"><img src=\'../img/chart.png\'/><span data-i18n=\'button.history\'/></a>';
        rateString += '</td><td>';
        rateString += formatTableData(rate.buyValue, rate.buyVariance, rate.buyPercentVariance);
        rateString += '</td><td>';
        rateString += formatTableData(rate.sellValue, rate.sellVariance, rate.sellPercentVariance);
        rateString += '</td><td class=\'lastColumn\'>';
        rateString += rate.lastChangedAt;
        rateString += '</td></tr>';
    }
    tableBody.append(rateString);

    $("#resultsTable").trigger("update");
    
    /** In case something gets weird with the cookie, set it to default sort list **/
    //$.cookies.set(cookieColumn, [[0, 0]]);

    setTimeout(function(){
        var sorting = [[0,0]];
        var sort_cookie = null;
        if($.cookies.test()) {      // test if cookies are working in the browser
            sort_cookie = $.cookies.get(cookieColumn);
            if(sort_cookie) {
                sorting = sort_cookie;
            }
        }
        $("#resultsTable").trigger("sorton",[sorting]);
    }, 10);
}

function retrieveHistory(bankName){
	var windowWidth = $(window).width();
	var windowHeight = $(window).height();
	if(windowHeight > windowWidth){
		showDialog($.t('error.error'), $.t('graphic.info-display'), 'info');
		return;
	}
	if(windowWidth >= 800){
		windowWidth = 800;
	}else{
		windowWidth = windowWidth - 20;
	}
	if(windowHeight >= 600){
		windowHeight = 600;
	}else{
		windowHeight = windowHeight - 20;
	}
    // retrieve history from REST service
    var completeURL = URL+selectedDateFormatted+'/'+selectedCurrency+'/'+bankName+'/history';
    $.ajax({
        url: completeURL,
        error: function(jqXHR, textStatus, errorThrown){
            showErrorDialog(jqXHR.status);
        },
        success: function(data){
            var dialog = $("#chartPanel");
            dialog.dialog({
                height: windowHeight,
                width: windowWidth,
                title: $.t('graphic.history') + bankName,
                modal: true
            });
            
            if(bankName == 'BNR') {
                fillHistoryDialog(data, true);
            } else {
                fillHistoryDialog(data);
            }
        }
    });
}

function mean(data) {
    var a = 0.0, i = 0;
    
    for(; i < data.length; i++) {
        a += data[i];
    }
    if(data.length) {
        a /= data.length;
    }
    return a;
}

// returns an array of two values fulfilling the regression line equation
// the regression line equation is of the form y = a + b * x
function regressionLineEquation(xData, yData) {
    var coefficients = [0.0, 0.0, 0.0]; // first is the free parameter, second x parameter
                                        // and third last day index in series
    
    // creating the arrays needed for first degree coefficient calculation
    var productMean = [], xSquareMean = [];
    var i = 1, aux = 1;
    
    // for transforming dates into days
    var xDataDayNum = [];
    xDataDayNum[0] = 1;
    for(; i < xData.length; i++) {
        aux = 1;
        if(new Date(xData[i - 1]).getDay() == 5) {
            aux = 3;
        }
        xDataDayNum[i] = xDataDayNum[i - 1] + aux;
    }
    coefficients[2] = xDataDayNum[i - 1];
    i = 0;
    
    for(; i< xData.length; i++) {
        productMean[i] = xDataDayNum[i] * yData[i];
        xSquareMean[i] = xDataDayNum[i] * xDataDayNum[i];
    }
    
    var meanXData = mean(xDataDayNum),
        covariance = mean(productMean) - meanXData * mean(yData),
        variance = mean(xSquareMean) - meanXData * meanXData;
        
    coefficients[1] = variance ? (covariance / variance) : 0;
    coefficients[0] = mean(yData) - coefficients[1] * meanXData;
    
    return coefficients;
}

// returns the Y coordinate of the estimated point on the regression line
function regressionLineY(xCoordinate, xData, yData) {
    var coef = regressionLineEquation(xData, yData),
        xCoordinateDayNum = 0, aux = 1;
        
    if(xCoordinate.getDay() == 1) {
        aux = 3;
    }
    
    // dependent on the last date index in the series, loaded from coef
    xCoordinateDayNum = coef[2] + aux;
    return coef[0] + coef[1] * xCoordinateDayNum;
}

function roundNumber(number, digits) {
    var multiple = Math.pow(10, digits);
    return Math.round(number * multiple) / multiple;
}

function isSameDay(firstDay, secondDay) {
    if(firstDay.getDate() != secondDay.getDate()) {
        return 0;
    }
    if(firstDay.getMonth() != secondDay.getMonth()) {
        return 0;
    }
    if(firstDay.getFullYear() != secondDay.getFullYear()) {
        return 0;
    }
    return 1;
}

function fillHistoryDialog(data, isBNRHistory){
    var days = [];
    var refs = [];
    var sells = [];
    var buys = [];
    var index = 0;
    for(; index<data.elements.length; index++) {
        var history = data.elements[index];
        days[index] = history.date;
        refs[index] = history.ref;
        if(!isBNRHistory){
            sells[index] = history.sell;
            buys[index] = history.buy;
        }
    }
    
    // making a prediction about tomorrow based on the simple linear regression
    var lastDataDay = getBucharestDate(new Date(data.elements[index - 1].date));
    var offset = 1;
    
    // checking if last day in data is today, in this case the estimation being available
    var dayToday = getBucharestDate(new Date());
    if(isSameDay(dayToday, lastDataDay)) {
        
        if(lastDataDay.getDay() == 5) { // if friday next working day is monday
            offset = 3;
        }
        lastDataDay.setTime(lastDataDay.getTime() + 24 * 3600 * 1000 * offset);

        var estimatedValueYRefs = regressionLineY(lastDataDay, days, refs), digitsDisplayed = 4;
        if(!isBNRHistory){
            var estimatedValueYSell = regressionLineY(lastDataDay, days, sells), estimatedValueYBuy = regressionLineY(lastDataDay, days, buys);
            sells[index] = roundNumber(estimatedValueYSell, digitsDisplayed);
            buys[index] = roundNumber(estimatedValueYBuy, digitsDisplayed);
        }
        refs[index] = roundNumber(estimatedValueYRefs, digitsDisplayed);
        days[index] = $.t('graphic.appraisal');        
    }
	
    if(isBNRHistory){
        var chartSeries = [{name: 'BNR', data: refs}];
    }else{
        var chartSeries = [{
            name: $.t('graphic.sell'),
            data: sells
        }, {
            name: $.t('graphic.buy'),
            data: buys
        }, {
            name: 'BNR',
            data: refs
        }];
    }
    var chartHeight = $('#chartPanel').height() - 18;
    var chartWidth = $('#chartPanel').width();
    $('#chartCenter').width(chartWidth);
    $('#chartCenter').height(chartHeight);
    var chart = new Highcharts.Chart({
            chart: {
                    renderTo: 'chartCenter',
                    type: 'line',
                    marginRight: 20,
                    marginBottom: 20
            },
            title: {
                    text: $.t('graphic.description') + selectedCurrency,
                    x: -20 //center
            },
            subtitle: {
                    text: $.t('graphic.exchange'),
                    x: -20
            },
            xAxis: {
                    categories: days,
                    labels : {y : -26, rotation: -45, align: 'right'}
            },
            yAxis: {
                    title: {
                            text: 'RON'
                    },
                    plotLines: [{
                            value: 0,
                            width: 1,
                            color: '#808080'
                    }]
            },
            tooltip: {
                    formatter: function() {
                                    return '<b>'+ (this.x==$.t('graphic.appraisal')?$.t('graphic.appraisal-future'):this.x) +'</b><br/>'+
                                    this.series.name +': '+ this.y + ' RON';
                    }
            },
            legend: {
                    layout: 'vertical',
                    align: 'right',
                    verticalAlign: 'top',
                    x: -10,
                    y: 100,
                    borderWidth: 0
            },
            series: chartSeries
    });
    
//    setTimeout(function (){
//        chart.setSize(chartWidth, chartHeight);
//    }, 10);
}

function updateBNRReferenceRate(){
    $("#bnrRateValue").html(bnrReferenceRate.value);
    $("#bnrRateVariance").html(bnrReferenceRate.variance);
    $("#bnrRateVariancePercent").html(bnrReferenceRate.variancePercent);  
    if(bnrReferenceRate.variance >= 0){
        $("#bnrRateVariance").addClass("greenSmall");
        $("#bnrRateVariancePercent").addClass("greenSmall");
        $("#bnrRateVariance").removeClass("redSmall");
        $("#bnrRateVariancePercent").removeClass("redSmall");
    }else{
        $("#bnrRateVariance").addClass("redSmall");
        $("#bnrRateVariancePercent").addClass("redSmall");
        $("#bnrRateVariance").removeClass("greenSmall");
        $("#bnrRateVariancePercent").removeClass("greenSmall");
    }
    $("#bnrRateHistory").html('<a class=\'smallText hoverLink\' data-i18n="[title]button.history_tooltip" onclick="retrieveHistory(\'BNR\')"><img src=\'../img/chart.png\'/><span data-i18n=\'button.history\'/></a>');
}

function getNumberWithSign(theNumber)
{
    if(theNumber > 0){
        return "+" + theNumber;
    }else{
        return theNumber.toString();
    }
}

function formatTableData(value, variance, percentVariance){
    var rateString = '';
    rateString += '<span>'
    rateString += value;
    rateString += '</span>';
    if(variance >= 0){
        rateString += '<span class="greenSmall">'
    }else{
        rateString += '<span class="redSmall">'
    }
    rateString += variance;
    rateString += '</span>';
    if(variance >= 0){
        rateString += '<span class="greenSmall">'
    }else{
        rateString += '<span class="redSmall">'
    }
    rateString += percentVariance;
    rateString += '</span>';
    return rateString;
}
