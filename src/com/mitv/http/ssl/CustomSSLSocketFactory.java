
package com.mitv.http.ssl;



import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLSocketFactory;



public class CustomSSLSocketFactory
    extends SSLSocketFactory
{
    SSLContext sslContext = SSLContext.getInstance("TLS");

    
    
    
    public CustomSSLSocketFactory(SSLContext context) 
    		throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException
    {
        super(null);
        
        sslContext = context;
    }
    
    
    
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException 
    {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    
    
    @Override
    public Socket createSocket() throws IOException 
    {
        return sslContext.getSocketFactory().createSocket();
    }
}