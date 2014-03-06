
package com.mitv.http.ssl;



import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ssl.X509HostnameVerifier;



public class CustomHostnameVerifier 
    implements X509HostnameVerifier
{
    @Override
    public boolean verify(String hostname, SSLSession session) 
    {
        return Boolean.TRUE;
    }
    
    

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts) 
            throws SSLException 
    {}
    
    

    @Override
    public void verify(String host, X509Certificate cert) 
            throws SSLException 
    {}
    
    

    @Override
    public void verify(String host, SSLSocket ssl) 
            throws IOException 
    {}
}
