package test.ro.micsa.exchange.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ro.micsa.exchange.dto.CurrencyType;
import ro.micsa.exchange.dto.Language;
import ro.micsa.exchange.dto.TransactionType;
import ro.micsa.exchange.retrievers.BNRDataRetriever;
import ro.micsa.exchange.service.TemplateService;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Georgian
 * Date: 24.02.2013
 * Time: 21:13
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:services.xml"})
public class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    @Test
    public void testGetAlertsMailSubject(){
        assertThat(templateService.getAlertsMailSubject(Language.EN)).isEqualTo("CursRapid.ro notification");
        assertThat(templateService.getAlertsMailSubject(Language.RO)).isEqualTo("Notificare CursRapid.ro");
    }

    @Test
    public void getAlertsMailContent_BNR_LESS(){
        assertThat(templateService.getAlertsMailContent(Language.EN, "Georgian Micsa", TransactionType.REF, CurrencyType.EUR, BNRDataRetriever.BNR, 4, 4.01, 0.002))
                .isEqualTo("<p style='font-size:16px;font-family: tahoma,arial,helvetica,sans-serif;'>Dear Georgian Micsa,<br/><br/>The exchange rate for EUR for bank BNR is greater than 4.0 RON.<br/>Current value is <span style='color:red;font-weight:bold;'>4.01 RON</span> and the difference compared to previous day is 0.0020 RON.<br/><br/>Best regards,<br/>CursRapid.ro</p>");
        assertThat(templateService.getAlertsMailContent(Language.RO, "Georgian Micsa", TransactionType.REF, CurrencyType.EUR, BNRDataRetriever.BNR, 4, 4.01, 0.002))
                .isEqualTo("<p style='font-size:16px;font-family: tahoma,arial,helvetica,sans-serif;'>Dragă Georgian Micsa,<br/><br/>Cursul de schimb pentru EUR al băncii BNR este mai mare decât 4.0 RON.<br/>Valoarea curentă este <span style='color:red;font-weight:bold;'>4.01 RON</span> iar diferența față de ziua precedentă este 0.0020 RON.<br/><br/>Numai bine,<br/>CursRapid.ro</p>");
    }

    @Test
    public void getAlertsMailContent_ING_SELL_EQUAL(){
        assertThat(templateService.getAlertsMailContent(Language.EN, "Georgian Micsa", TransactionType.SELL, CurrencyType.EUR, "ING", 4.00, 4.00, 0.002))
                .isEqualTo("<p style='font-size:16px;font-family: tahoma,arial,helvetica,sans-serif;'>Dear Georgian Micsa,<br/><br/>The exchange rate for selling EUR for bank ING is equal to 4.0 RON.<br/>Current value is <span style='color:red;font-weight:bold;'>4.0 RON</span> and the difference compared to previous day is 0.0020 RON.<br/><br/>Best regards,<br/>CursRapid.ro</p>");
        assertThat(templateService.getAlertsMailContent(Language.RO, "Georgian Micsa", TransactionType.SELL, CurrencyType.EUR, "ING", 4.00, 4.00, 0.002))
                .isEqualTo("<p style='font-size:16px;font-family: tahoma,arial,helvetica,sans-serif;'>Dragă Georgian Micsa,<br/><br/>Cursul de schimb pentru vânzarea EUR al băncii ING este egal cu 4.0 RON.<br/>Valoarea curentă este <span style='color:red;font-weight:bold;'>4.0 RON</span> iar diferența față de ziua precedentă este 0.0020 RON.<br/><br/>Numai bine,<br/>CursRapid.ro</p>");
    }
}
