
package com.millicom.mitv.http.ssl;



import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;



public abstract class HttpClientWraper
{
	private static String TLS_PROTOCOL = "TLS";
	private static String HTTPS_SCHEMA = "https";
	private static int HTTPS_PORT = 443;
	
	
    public static DefaultHttpClient wrapClient(DefaultHttpClient base) 
    {
        try
        {
            SSLContext ctx = SSLContext.getInstance(TLS_PROTOCOL);

            X509TrustManager tm = new CustomTrustManager();

            ctx.init(null, new TrustManager[]{tm}, null);

            // X509HostnameVerifier verifier = new CustomHostnameVerifier();
            
            SSLSocketFactory ssf = SSLSocketFactory.getSocketFactory();

            // new SSLSocketFactory(ctx, verifier);
            
            ClientConnectionManager ccm = base.getConnectionManager();

            SchemeRegistry schemeRegistry = ccm.getSchemeRegistry();

            Scheme scheme = new Scheme(HTTPS_SCHEMA, ssf, HTTPS_PORT);
            
            schemeRegistry.register(scheme);
            
            return new DefaultHttpClient(ccm, base.getParams());
        }
        catch(Exception e)
        {
            return base;
        }
    }
}
