/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.model.ExchangeRateEntity;

/**
 *
 * @author georgian
 */
@Repository
@Transactional
public class ExchangeRateDAOImpl implements ExchangeRateDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly=true)
    public List<ExchangeRateEntity> getDayExchangeRates(String day) {
        Query query = entityManager.createQuery("select e from ExchangeRateEntity e where e.date = :day");
        query.setParameter("day", day);
        List<ExchangeRateEntity> result = getAsArrayList(query.getResultList());
        return result;
    }
    
    @Override
    @Transactional(readOnly=true)
    public List<ExchangeRateEntity> getDayCurrencyExchangeRates(String day, CurrencyType currencyType){
        Query query = entityManager.createQuery("select e from ExchangeRateEntity e where e.date = :day and e.currencyType = :currencyType");
        query.setParameter("day", day);
        query.setParameter("currencyType", currencyType);
        List<ExchangeRateEntity> result = getAsArrayList(query.getResultList());
        return result;
    }

    @Override
    public ExchangeRateEntity saveExchangeRate(ExchangeRateEntity exchangeRate) {
        entityManager.persist(exchangeRate);
        return exchangeRate;
    }
    
    @Override
    public ExchangeRateEntity updateExchangeRate(ExchangeRateEntity exchangeRate) {
        exchangeRate = entityManager.merge(exchangeRate);
        return exchangeRate;
    }

    private List<ExchangeRateEntity> getAsArrayList(List<ExchangeRateEntity> list){
        ArrayList a = new ArrayList();
        if(list!=null){
            a.addAll(list);
        }
        return a;
    }
}
