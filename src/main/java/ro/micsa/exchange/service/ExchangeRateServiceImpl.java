/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ro.micsa.exchange.dao.ExchangeRateDAO;
import ro.micsa.exchange.dto.*;
import ro.micsa.exchange.model.ExchangeRateEntity;
import ro.micsa.exchange.utils.Converter;
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.EmailSender;

/**
 *
 * @author georgian
 */
@Component
@Path("/rates/")
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final Logger log = Logger.getLogger("ExchangeRateService");
    
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private MemcacheStore memcacheStore;
    @Autowired
    private ExchangeRateDAO exchangeRateDAO;

    @GET
    @Produces({"application/json","application/xml"})
    @Path("/{day:((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])}/")
    @Override
    public Response getDayExchangeRates(@PathParam("day") String day) {
        log.log(Level.FINE, "Get day exchange rates: {0}", day);
        List<ExchangeRateEntity> entities = memcacheStore.get(day);
        if (entities != null && !entities.isEmpty()) {
            ExchangeRateContainer container = getAsContainer(entities);
            return Response.status(HttpURLConnection.HTTP_OK).entity(container).build();
        }
        return Response.status(HttpURLConnection.HTTP_NOT_FOUND).entity(new ErrorEntity(ErrorEntity.ErrorCode.NO_RATES_FOUND)).build();
    }
    
    @GET
    @Produces({"application/json","application/xml"})
    @Path("/{day:((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])}/{currencyType}/")
    @Override
    public Response getDayCurrencyExchangeRates(@PathParam("day") String day, @PathParam("currencyType") CurrencyType currencyType){
        log.log(Level.FINE, "Get day exchange rates: {0} for currency {1}", new Object[]{day, currencyType});
        List<ExchangeRateEntity> entities = memcacheStore.get(day, currencyType);
        if (entities != null && !entities.isEmpty()) {
            ExchangeRateContainer container = getAsContainer(entities);
            return Response.status(HttpURLConnection.HTTP_OK).entity(container).build();
        }
        return Response.status(HttpURLConnection.HTTP_NOT_FOUND).entity(new ErrorEntity(ErrorEntity.ErrorCode.NO_RATES_FOUND)).build();
    }

    @GET
    @Produces({"application/json","application/xml"})
    @Path("/{day:((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])}/{currencyType}/{bank}/history")
    @Override
    public Response getHistoryCurrencyExchangeRates(@PathParam("day") String day, @PathParam("currencyType") CurrencyType currencyType,@PathParam("bank") String bank){
        log.log(Level.FINE, "Get history exchange rates: {0} for currency {1} for bank {2}", new Object[]{day, currencyType, bank});
        ExchangeRateHistoryContainer container = new ExchangeRateHistoryContainer();
        for(String prevDay : DateUtils.getLastNDays(day, 10)){
            List<ExchangeRateEntity> entities = memcacheStore.get(prevDay, currencyType);
            if (entities != null && !entities.isEmpty()) {
                ExchangeRateHistory history = new ExchangeRateHistory();
                ExchangeRateEntity exchangeRateSell, exchangeRateBuy;
                
                if(!bank.equals("BNR")) { // for BNR buy and sell properties don't make sense
                    exchangeRateSell = getByDayBankCurrencyTransactionType(entities,
                        prevDay, bank, currencyType, TransactionType.SELL);
                    exchangeRateBuy = getByDayBankCurrencyTransactionType(entities,
                        prevDay, bank, currencyType, TransactionType.BUY);
                    
                    if(exchangeRateSell == null || exchangeRateBuy == null) {
                        continue;
                    }
                    
                    history.setSell(exchangeRateSell.getValue());
                    history.setBuy(exchangeRateBuy.getValue());
                }
                
                ExchangeRateEntity exchangeRateRef = getByDayBankCurrencyTransactionType(entities,
                        prevDay, "BNR", currencyType, TransactionType.REF);
                if(exchangeRateRef == null){
                    continue;
                }
                
                history.setDate(prevDay);                
                history.setRef(exchangeRateRef.getValue());
                container.add(history);
            }
        }
        return Response.status(HttpURLConnection.HTTP_OK).entity(container).build();
    }

    @POST
    @Consumes({"application/json","application/xml"})
    @Override
    public Response saveExchangeRate(ExchangeRate exchangeRate) {
        Response validation = validateExchangeRate(exchangeRate);
        if (validation != null) {
            log.log(Level.WARNING, "Invalid exchange rate: {0}", exchangeRate);
            return validation;
        } else {
            List<ExchangeRateEntity> entitites = memcacheStore.get(exchangeRate.getDate());
            ExchangeRateEntity existantExchangeRate = getByDayBankCurrencyTransactionType(entitites, exchangeRate.getDate(), exchangeRate.getBankName(), exchangeRate.getCurrencyType(), exchangeRate.getTransactionType());
            if (existantExchangeRate == null) {
                log.log(Level.INFO, "Save exchange rate: {0}", exchangeRate);
                ExchangeRateEntity entity = Converter.toEntity(exchangeRate);
                exchangeRateDAO.saveExchangeRate(entity);                
                memcacheStore.remove(exchangeRate.getDate()); // must remove from mem cache for current day
                
                URI uri = null;
                try {
                    uri = new URI(exchangeRate.getDate());
                } catch (URISyntaxException ex) {
                    log.warning(ex.toString());
                }
                emailSender.sendExchangeRateMail(entity, true);
                return Response.created(uri).build();
            } else {
                log.log(Level.INFO, "Update exchange rate: {0}", exchangeRate);
                existantExchangeRate.setValue(exchangeRate.getValue());
                existantExchangeRate.setEvolution(exchangeRate.getEvolution());
                exchangeRateDAO.updateExchangeRate(existantExchangeRate);
                memcacheStore.remove(exchangeRate.getDate()); // must remove from mem cache for current day
                
                emailSender.sendExchangeRateMail(existantExchangeRate, false);
                return Response.ok().build();
            }         
            
        }
    }

    private Response validateExchangeRate(ExchangeRate exchangeRate) {
        if (!StringUtils.hasText(exchangeRate.getBankName())) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new ErrorEntity(ErrorEntity.ErrorCode.INVALID_BANK)).build();
        }
        if (!StringUtils.hasText(exchangeRate.getDate())) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new ErrorEntity(ErrorEntity.ErrorCode.INVALID_DATE)).build();
        }
        if (exchangeRate.getValue() <= 0) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new ErrorEntity(ErrorEntity.ErrorCode.INVALID_VALUE)).build();
        }
        try {
            DateUtils.DATE_SDF.parse(exchangeRate.getDate());
        } catch (ParseException ex) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new ErrorEntity(ErrorEntity.ErrorCode.INVALID_DATE)).build();
        }
        return null;
    }

    public static ExchangeRateEntity getByDayBankCurrencyTransactionType(List<ExchangeRateEntity> entitites, String date, String bank, CurrencyType currencyType, TransactionType transactionType) {
        for (ExchangeRateEntity er : entitites) {
            if (er.getDate().equals(date) && er.getBankName().equals(bank)
                    && er.getCurrencyType().equals(currencyType) && er.getTransactionType().equals(transactionType)) {
                return er;
            }
        }
        return null;
    }

    public static ExchangeRateContainer getAsContainer(List<ExchangeRateEntity> entitites) {
        ExchangeRateContainer container = new ExchangeRateContainer();
        for (ExchangeRateEntity entity : entitites) {
            container.add(Converter.toDTO(entity));
        }
        return container;
    }
}
