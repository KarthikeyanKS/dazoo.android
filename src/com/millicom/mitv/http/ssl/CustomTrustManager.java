
package com.millicom.mitv.http.ssl;



import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;



public class CustomTrustManager 
    implements X509TrustManager
{
    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() 
    {
        return null;
    }
    
    
    @Override
    public void checkClientTrusted(
            X509Certificate[] xcs, 
            String string) 
                throws CertificateException {}

    
    @Override
    public void checkServerTrusted(
            X509Certificate[] xcs, 
            String string) 
            throws CertificateException {}
}
