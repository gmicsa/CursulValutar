package test.ro.micsa.exchange.retrievers;

import org.junit.Test;
import ro.micsa.exchange.retrievers.INGDataRetriever;

import static org.fest.assertions.api.Assertions.assertThat;

public class TestINGRetriever {

    @Test
    public void testRetrieve() throws Exception {
        INGDataRetriever bank = new INGDataRetriever();

        assertThat(bank.getBankName()).isEqualTo("ING");
        assertThat(bank.getExchangeRates()).hasSize(20);
    }
}
