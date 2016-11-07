var URL = 'http://' + document.location.host + '/services/notification/';
var accessToken;
var langRO = "ro-RO", langEN = "en-EN", lang = langRO;
var cookieLang = 'i18next';
var bankCurrencies = {
    'BNR' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'],
    'AlphaBank' : ['EUR', 'USD', 'CHF', 'GBP'],
    'BancaCarpatica' : ['EUR', 'USD', 'CHF', 'GBP'],
    'BancaRomaneasca' : ['EUR', 'USD', 'CHF', 'GBP'],
    'Bancpost' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'SEK'],
    'BCR' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'],
    'BRD' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'],
    'BT' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'],
    'CECBank' : ['EUR', 'USD', 'CHF', 'GBP'],
    'CityBank' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'JPY', 'SEK'],
    'CreditEurope' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'JPY', 'SEK'],
    'EmporikiBank' : ['EUR', 'USD', 'CHF', 'GBP'],
    'GarantiBank' : ['EUR', 'USD', 'CHF', 'GBP', 'JPY'],
    'ING' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'],
    'IntesaSanPaoloBank' : ['EUR', 'USD', 'CHF', 'GBP', 'DKK', 'HUF', 'NOK', 'SEK'],
    'LeumiBank' : ['EUR', 'USD', 'CHF', 'GBP', 'HUF'],
    'LibraBank' : ['EUR', 'USD', 'CHF', 'GBP'],
    'MilleniumBank' : ['EUR', 'USD', 'CHF', 'GBP'],
    'NexteBank' : ['EUR', 'USD', 'CHF', 'GBP'],
    'OTPBank' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'],
    'PiraeusBank' : ['EUR', 'USD', 'CHF', 'GBP'],
    'ProCreditBank' : ['EUR', 'USD'],
    'RaiffeisenBank' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'],
    'RBSBank' : ['EUR', 'USD', 'CHF', 'GBP', 'DKK', 'HUF', 'JPY', 'NOK', 'SEK'],
    'UniCreditTiriac' : ['EUR', 'USD', 'CHF', 'GBP', 'AUD', 'DKK', 'NOK', 'SEK'],
    'Volksbank' : ['EUR', 'USD', 'CHF', 'GBP']
};

$(function(){
    initLang();
    $("input:submit, button").button();
    $("#save").button("disable");
    addCurrencyEntries('BNR');
    addBankEntries();
    addEvents();
    initFacebook();
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
        resGetPath: '../locales/__lng__/__ns__.json',
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
    });
}

function getThresholdAsNumber() {
    var threshold = $("#threshold").val();
    threshold = threshold.replace(",", ".");
    return parseFloat(threshold);
}

function addEvents(){
    $('#loadingImage')
        .ajaxStart(function() {
            showLoader(true);
        })
        .ajaxStop(function() {
            showLoader(false);
    });
    $("#save").click(function() {
        var invalid = false;
        if(!$("#threshold").val()){
            invalid = true;
        }
        if(!invalid){
            var threshold = getThresholdAsNumber();
            if(!threshold || threshold <= 0){
                invalid = true;
            }
        }
        if(invalid){
            $("#feedback").html($.t('error.invalid_value'));
            $('#feedback').removeClass();
            $('#feedback').addClass("error");
            $("#feedback").show();
            return;
        }
        saveNotification();
    });
    $("#bankpicker").change(function (){
        var bankName = $(this).find(":selected").text();
        if(bankName == "BNR"){
            $("#transactionPanel").hide();
       }else{
           $("#transactionPanel").show();
       }
        addCurrencyEntries(bankName);
    });
    $( "#appTitle" ).click(function() {
        window.location = '/';
    });
}

function addCurrencyEntries(bankName){
    $("#currencypickerAlerts").empty();
    var currencyArray = bankCurrencies[bankName];
    var currencyOptions = '';
    for(var i = 1; i < currencyArray.length + 1; i++) {
        currencyOptions = currencyOptions + '<option value="' + currencyArray[i - 1] + '">' + currencyArray[i - 1] + '</option>';
    }
    $("#currencypickerAlerts").append(currencyOptions);
}

function addBankEntries(){
    var array = ['BNR', 'AlphaBank', 'BancaCarpatica', 'BancaRomaneasca', 'Bancpost', 'BCR', 'BRD', 'BT', 'CECBank', 'CityBank', 'CreditEurope', 'EmporikiBank', 'GarantiBank',
        'ING', 'IntesaSanPaoloBank', 'LeumiBank', 'LibraBank', 'MilleniumBank', 'NexteBank', 'OTPBank', 'PiraeusBank', 'ProCreditBank', 'RaiffeisenBank', 'RBSBank', 'UniCreditTiriac', 'Volksbank'];
    var options = '';
    for(var i = 1; i < array.length + 1; i++) {
        options = options + '<option value="' + array[i - 1] + '">' + array[i - 1] + '</option>';
    }
    $("#bankpicker").append(options);
}

function retrieveNotification() {
    $.ajax({
        url: URL,
        data : {"accessToken" : accessToken},
        error: function(jqXHR, textStatus, errorThrown){
            if(jqXHR.status == 404){
                $("#save").show();
                $("#save" ).button("enable");
                $("#feedback").show();
                $("#feedback").html($.t('alerts.info'));
            } else {
                $("#save" ).button("disable");
                $("#feedback").show();
                $("#feedback").html($.t('error.request_error'));
            }

        },
        success: function(data){
            $("#save").show();
            $("#save").button("enable");
            $("#feedback").hide();
            fillForm(data);
        }
    });
}

function fillForm(data) {
    $("#bankpicker").val(data.bankName);
    addCurrencyEntries(data.bankName);
    if(data.bankName == "BNR"){
        $("#transactionPanel").hide();
    }else{
        $("#transactionPanel").show();
    }
    if(data.transactionType == "BUY"){
        $('#buyRadio').attr('checked', 'checked');
    }
    $("#currencypickerAlerts").val(data.currencyType);
    $("#comparatorPicker").val(data.comparator);
    $("#threshold").val(data.threshold);
    $('#activeCheck').attr('checked', data.enabled);
}

function getDataFromForm() {
    var data = {
        'bankName': 'BNR',
        'transactionType': 'REF',
        'currencyType': 'EUR',
        'comparator': 'GREATER_OR_EQUAL',
        'threshold': 0,
        'enabled': true
    };
    data.bankName = $("#bankpicker").val();
    if (data.bankName != "BNR") {
        if ($('#buyRadio').attr('checked') == 'checked') {
            data.transactionType = "BUY";
        } else {
            data.transactionType = "SELL";
        }
    }
    data.currencyType = $("#currencypickerAlerts").val();
    data.comparator = $("#comparatorPicker").val();
    data.enabled = $('#activeCheck').attr('checked') == 'checked';
    data.threshold = getThresholdAsNumber();
    data.language = lang == langEN ? 'EN' : 'RO';
    return data;
}

function saveNotification() {
    $("#save").button("disable");
    $.ajax({
        url: URL + '?accessToken='+accessToken,
        data : JSON.stringify(getDataFromForm()),
        type: 'POST',
        headers: {
            'Accept' : 'application/json',
            'Content-Type': 'application/json'
        },
        error: function(jqXHR, textStatus, errorThrown){
            $('#feedback').removeClass();
            $('#feedback').addClass("error");
            $('#feedback').text($.t('error.request_error'));
            $("#feedback").show();
            $("#save").button("enable");
        },
        success: function(data){
            $('#feedback').removeClass();
            $('#feedback').addClass("success");
            $('#feedback').text($.t('alerts.request_ok'));
            $("#feedback").show();
            $("#save").button("enable");
        }
    });
}

function showLoader(show){
    if (show)
        $('#loadingImage').show();
    else
        $('#loadingImage').hide();
}

function initFacebook(){
    var loginButton;
    var logoutButton;
    var facebookUser;
    var facebookPhoto;
    var logoutPanel;

    window.fbAsyncInit = function () {
        FB.init({ appId: '565443610134456',
            status: true,
            cookie: true,
            xfbml: true,
            oauth: true});

        showLoader(true);

        function updateButton(response) {
            loginButton = $("#loginFacebookButton");
            logoutButton = $("#logoutButton");
            facebookUser = $("#facebookUser");
            facebookPhoto = $("#facebookPhoto");
            logoutPanel = $("#logoutPanel");

            if (response.authResponse) {
                //user is already logged in and connected
                FB.api('/me', function (info) {
                    login(response, info);
                });

                logoutButton.click(function () {
                    FB.logout(function (response) {
                        logout(response);
                    });
                });
            } else {
                //user is not connected to your app or logged out
                loginButton.click(function () {
                    showLoader(true);
                    FB.login(function (response) {
                        if (response.authResponse) {
                            FB.api('/me', function (info) {
                                login(response, info);
                            });
                        } else {
                            //user cancelled login or did not grant authorization
                            showLoader(false);
                        }
                    }, {scope: 'user_about_me,email'});
                });
                showLoader(false);
            }
        }

        // run once with current status and whenever the status changes
        FB.getLoginStatus(updateButton);
        FB.Event.subscribe('auth.statusChange', updateButton);
    };

    (function() {
        var e = document.createElement('script'); e.async = true;
        e.src = document.location.protocol + '//connect.facebook.net/en_US/all.js';
        document.getElementById('fb-root').appendChild(e);
    }());


    function login(response, info){
        if (response.authResponse) {
            logoutPanel.show();
            loginButton.hide();
            facebookUser.html(info.name);
            facebookPhoto.html('<img src="https://graph.facebook.com/' + info.id + '/picture">');
            if(!accessToken){
                accessToken = response.authResponse.accessToken;
                retrieveNotification();
            }
        }
    }

    function logout(response){
        accessToken = null;
        loginButton.show();
        logoutPanel.hide();
        $("#save").hide();
        $("#feedback").hide();
        showLoader(false);
    }
}