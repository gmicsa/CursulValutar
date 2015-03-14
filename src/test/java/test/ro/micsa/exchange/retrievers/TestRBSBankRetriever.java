/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.ro.micsa.exchange.retrievers;

import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.retrievers.RBSBankRetriever;

/**
 *
 * @author Alex
 */
public class TestRBSBankRetriever {

    @BeforeClass
    public static void setup() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    @Test
    public void testRetrieve() throws Exception {
        RBSBankRetriever piraeusRetriever = new RBSBankRetriever();
        Assert.assertTrue(piraeusRetriever.getBankName().equals("RBSBank"));

        List<ExchangeRate> retrieved = piraeusRetriever.getExchangeRates();
        Assert.assertTrue(retrieved.size() > 0);
    }
}
