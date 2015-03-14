/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.dao;

import java.util.List;
import static org.fest.assertions.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import ro.micsa.exchange.dao.ExchangeRateDAO;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.TransactionType;
import ro.micsa.exchange.model.ExchangeRateEntity;

/**
 *
 * @author georgian.micsa
 */
public class TestExchangeRateDAO extends AbstractDAOTest<ExchangeRateDAO> {

    private ExchangeRateEntity rateDay1EUR;
    private ExchangeRateEntity rateDay1USD;
    private ExchangeRateEntity rateDay2EUR;
    private ExchangeRateEntity rateDay2USD;
    private static final String DAY_1 = "2012-10-18";
    private static final String DAY_2 = "2012-10-19";
    private static final String DAY_X = "2012-10-20";
    
    public TestExchangeRateDAO(){
        super(ExchangeRateDAO.class);
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        rateDay1EUR = saveExchangeRate(CurrencyType.EUR, DAY_1);
        rateDay2EUR = saveExchangeRate(CurrencyType.EUR, DAY_2);
        rateDay1USD = saveExchangeRate(CurrencyType.USD, DAY_1);
        rateDay2USD = saveExchangeRate(CurrencyType.USD, DAY_2);
    }

    @Test
    public void testGetRatesByDate() {
        final List<ExchangeRateEntity> ratesDay1 = repository.getDayExchangeRates(DAY_1);
        assertThat(ratesDay1).containsOnly(rateDay1EUR, rateDay1USD);
    }
    
    @Test
    public void testGetRatesByInexistentDate() {
        final List<ExchangeRateEntity> ratesDayX = repository.getDayExchangeRates(DAY_X);
        assertThat(ratesDayX).isEmpty();
    }
    
    @Test
    public void testGetRatesByDateAndCurrency() {
        final List<ExchangeRateEntity> ratesDay2USD = repository.getDayCurrencyExchangeRates(DAY_2, CurrencyType.USD);
        assertThat(ratesDay2USD).containsOnly(rateDay2USD);
    }
    
    @Test
    public void testGetRatesByInexistentDateAndCurrency() {
        final List<ExchangeRateEntity> ratesDayXUSD = repository.getDayCurrencyExchangeRates(DAY_X, CurrencyType.USD);
        assertThat(ratesDayXUSD).isEmpty();
    }
    
    @Test
    public void testUpdateExchangeRate() {
        final double expectedValue = 5.0;
        final double expectedEvolution = 0.5;
        rateDay1EUR.setValue(expectedValue);
        rateDay1EUR.setEvolution(expectedEvolution);
        rateDay1EUR = repository.updateExchangeRate(rateDay1EUR);
        final List<ExchangeRateEntity> ratesDay2USD = repository.getDayCurrencyExchangeRates(DAY_1, CurrencyType.EUR);
        assertThat(ratesDay2USD).hasSize(1);
        assertThat(ratesDay2USD.get(0).getValue()).isEqualTo(expectedValue);
        assertThat(ratesDay2USD.get(0).getEvolution()).isEqualTo(expectedEvolution);
    }

    private ExchangeRateEntity saveExchangeRate(CurrencyType currencyType, String date) {
        ExchangeRateEntity rate = new ExchangeRateEntity();
        rate.setBankName("BCR");
        rate.setCurrencyType(currencyType);
        rate.setDate(date);
        rate.setLastChangedAt(date + " 10:55");
        rate.setTransactionType(TransactionType.SELL);
        rate.setEvolution(-0.2);
        rate.setValue(4.58);
        repository.saveExchangeRate(rate);
        return rate;
    }
}
