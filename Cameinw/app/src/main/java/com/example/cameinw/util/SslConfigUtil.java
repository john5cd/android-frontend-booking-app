package com.example.cameinw.util;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for configuring SSL settings in an OkHttpClient.Builder.
 */
public class SslConfigUtil {

    /**
     * Configures SSL settings for an OkHttpClient.Builder to trust all certificates
     * and hostnames.
     *
     * @param builder The OkHttpClient.Builder instance to configure.
     * @return The configured OkHttpClient.Builder instance.
     * @throws RuntimeException if there is an error during SSL configuration.
     */
    public static OkHttpClient.Builder configureSsl(OkHttpClient.Builder builder) {
        try {
            // Create an SSL socket factory with the all-trusting manager
            final SSLSocketFactory sslSocketFactory = createSslSocketFactory();
            configureSslSocketFactory(builder, sslSocketFactory);
            configureHostnameVerifier(builder);
            configureProtocols(builder);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an SSL socket factory that trusts all certificates.
     *
     * @return The SSL socket factory instance.
     * @throws Exception if there is an error during SSL socket factory creation.
     */
    // Install the all-trusting trust manager
    private static SSLSocketFactory createSslSocketFactory() throws Exception {
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return sslContext.getSocketFactory();
    }

    /**
     * Configures the SSL socket factory for the OkHttpClient.Builder.
     *
     * @param builder The OkHttpClient.Builder instance to configure.
     * @param sslSocketFactory The SSL socket factory to set.
     */
    private static void configureSslSocketFactory(OkHttpClient.Builder builder,
                                                  SSLSocketFactory sslSocketFactory) {
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)
                trustAllCerts[0]);
    }

    /**
     * Configures the hostname verifier to trust all hostnames.
     *
     * @param builder The OkHttpClient.Builder instance to configure.
     */
    private static void configureHostnameVerifier(OkHttpClient.Builder builder) {
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    /**
     * Configures the list of supported protocols for the OkHttpClient.Builder.
     *
     * @param builder The OkHttpClient.Builder instance to configure.
     */
    private static void configureProtocols(OkHttpClient.Builder builder) {
        List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        builder.protocols(protocols);
    }

    /**
     * An array of TrustManagers that trust all X.509 certificates. This TrustManager
     * implementation is used to create an SSL socket factory that trusts all
     certificates
     * without validation.
     */
    private static final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[]
                                                       chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[]
                                                       chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };

}
