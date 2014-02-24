
package com.millicom.mitv.http.ssl;



import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;



public class CustomSSLSocketFactory
    extends SSLSocketFactory
{
    SSLContext sslContext = SSLContext.getInstance("TLS");

    
    public CustomSSLSocketFactory(KeyStore truststore)
            throws NoSuchAlgorithmException, 
                   KeyManagementException, 
                   KeyStoreException, 
                   UnrecoverableKeyException 
    {
        super();

        CustomTrustManager tm = new CustomTrustManager();

        sslContext.init(null, new TrustManager[] { tm }, null);
    }
    

    @Override
    public Socket createSocket(
            Socket socket, 
            String host, 
            int port, 
            boolean autoClose) 
            throws 
            IOException, 
            UnknownHostException 
    {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    
    @Override
    public Socket createSocket() 
            throws IOException 
    {
        return sslContext.getSocketFactory().createSocket();
    }

    
    @Override
    public String[] getDefaultCipherSuites()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getSupportedCipherSuites()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(String string, int i) throws IOException, UnknownHostException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException, UnknownHostException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(InetAddress ia, int i) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
