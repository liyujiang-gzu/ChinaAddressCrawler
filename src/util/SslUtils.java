package util;

import logger.MyLog;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class SslUtils {

    public static void ignoreSsl() {
        try {
            TrustManager[] trustManagers = new TrustManager[1];
            trustManagers[0] = new TrustAllCerts();
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((urlHostName, session) -> {
                MyLog.debug("Warning: URL Host " + urlHostName + " vs. Peer Host " + session.getPeerHost());
                return true;
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static class TrustAllCerts implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

}

