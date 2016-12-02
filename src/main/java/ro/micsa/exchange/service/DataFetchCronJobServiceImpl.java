/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.micsa.exchange.dao.ExchangeRateDAO;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.model.ExchangeRateEntity;
import ro.micsa.exchange.retrievers.BankDataRetriever;
import ro.micsa.exchange.utils.Converter;
import ro.micsa.exchange.utils.DateUtils;
import ro.micsa.exchange.utils.EmailSender;

/**
 *
 * @author georgian
 */
@Component
@Path("/fetch/")
public class DataFetchCronJobServiceImpl implements RESTService{
    
    private static final Logger log = Logger.getLogger("ExchangeRateService");
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private ExchangeRateDAO exchangeRateDAO;
    @Autowired
    private MemcacheStore memcacheStore;
    @Autowired
    private List<BankDataRetriever> dataRetrievers;

    @GET
    @Override
    public Response execute() {
        // no need to run this on Saturdays or Sundays because the rates do not change in that interval
        if(true){
            log.info("Executing cron job...");
            
            Date now = new Date();
            String currentDate = DateUtils.DATE_SDF.format(now);
            String previousDate = DateUtils.DATE_SDF.format(DateUtils.getPreviousBusinessDate(now));
            
            List<ExchangeRateEntity> currentDBRates = memcacheStore.get(currentDate);
            List<ExchangeRateEntity> previousDBRates = memcacheStore.get(previousDate);
            log.info(String.format("Current date is %s, previous working date is %s", currentDate, previousDate));
            
            Map<String, Throwable> errors = new HashMap<String, Throwable>();
            List<String> banksProcessedSuccessfuly = new ArrayList<String>(); 
            Long start = System.currentTimeMillis();
            for(BankDataRetriever retriever : dataRetrievers){
                try{
                    log.log(Level.FINE, "Retrieve data for {0}", retriever.getBankName());
                    List<ExchangeRate> rates = retriever.getExchangeRates();
                    if(rates == null || rates.size() == 0){
                        throw new Exception("No rates found for " + retriever.getBankName());
                    }
                    for(ExchangeRate rate : rates){
                        rate.setBankName(retriever.getBankName());
                        rate.setDate(currentDate);
                        ExchangeRateEntity existantExchangeRate = ExchangeRateServiceImpl.getByDayBankCurrencyTransactionType
                                (currentDBRates, currentDate, retriever.getBankName(), rate.getCurrencyType(), rate.getTransactionType());
                        ExchangeRateEntity previousExchangeRate = ExchangeRateServiceImpl.getByDayBankCurrencyTransactionType
                                (previousDBRates, previousDate, retriever.getBankName(), rate.getCurrencyType(), rate.getTransactionType());
                        if (existantExchangeRate == null) {
                            log.log(Level.FINE, "Save exchange rate: {0}", rate);
                            ExchangeRateEntity entity = Converter.toEntity(rate);
                            if(previousExchangeRate != null){
                                entity.setEvolution(substract(entity.getValue(), previousExchangeRate.getValue()));
                            }else{
                                entity.setEvolution(0);
                            }
                            exchangeRateDAO.saveExchangeRate(entity);
                        } else {
                            log.log(Level.FINE, "Update exchange rate: {0}", rate);
                            existantExchangeRate.setValue(rate.getValue());
                            if(previousExchangeRate != null){
                                existantExchangeRate.setEvolution(substract(rate.getValue(), previousExchangeRate.getValue()));
                            }else{
                                existantExchangeRate.setEvolution(0);
                            }
                            existantExchangeRate.setLastChangedAt(rate.getLastChangedAt());
                            exchangeRateDAO.updateExchangeRate(existantExchangeRate);
                        }
                    }
                    banksProcessedSuccessfuly.add(retriever.getBankName());
                }catch(Throwable e){
                    errors.put(retriever.getBankName(), e);
                    log.log(Level.SEVERE, e.toString(), e);
                }
            }
            // remove from mem cache all rates for present day
            // will be restored at next cron job or GET query
            memcacheStore.remove(currentDate);
            
            Long durationInMs = System.currentTimeMillis() - start;
            if(errors.size() > 0){
                // send email only in case of errors during data retrieval
                emailSender.sendCronResultsMail(durationInMs, banksProcessedSuccessfuly, errors);
            }
            String okMsg = String.format("Bank rates fetching was successfull and took %s ms.", durationInMs);
            log.log(Level.INFO, okMsg);
            return Response.ok(okMsg).build();
        }
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Service can not be called during weekends!").build();
    }
    
    
    public static double substract(double a, double b){
        BigDecimal first = new BigDecimal(a);
        BigDecimal second = new BigDecimal(b);
        return first.subtract(second, new MathContext(4)).doubleValue();
    }
    
}
