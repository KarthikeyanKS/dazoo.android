
package com.millicom.mitv.http;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.ssl.HttpClientWraper;
import com.mitv.Consts;
import android.util.Log;



public class HTTPCore
{
	private static final String TAG = HTTPCore.class.getName();
	
	
	private static final String DEAFULT_ENCODING = "UTF-8";
	private static final int DEFAULT_HTTP_STATUS_RESULT = 500;
	
	private static HTTPCore sharedInstance;
	
	
	
	private HTTPCore(){}
	
	
	
	public static HTTPCore sharedInstance() 
	{
		if (sharedInstance == null) 
		{
			sharedInstance = new HTTPCore();
		}
		// No need for else
		
		return sharedInstance;
	}
	

	
	public HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerParameters,
			final String bodyContentData)
	{
		StringBuilder messageSB = new StringBuilder();
        messageSB.append("HTTP Get request for url: ");
        messageSB.append(url);
        Log.d(TAG, messageSB.toString());

        HTTPCoreResponse response = executeRequest(
        		httpRequestType,
        		url,
        		urlParameters,
        		headerParameters,
        		Consts.JSON_MIME_TYPE,
        		Consts.JSON_MIME_TYPE,
				bodyContentData,
				Consts.HTTP_CORE_CONNECTION_TIMEOUT_IN_MILISECONDS,
				Consts.HTTP_CORE_SOCKET_TIMEOUT_IN_MILISECONDS,
				false,
				new String(),
				0,
				new String(),
				new String(),
				false);

		return response;
	}

	
	
	private HTTPCoreResponse executeRequest(
			final HTTPRequestTypeEnum httpRequestType,
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerParameters,
			final String acceptType,
			final String contentType,
			final String httpBodyData,
			final int connectionTimeout,
			final int socketTimeout,
			final boolean useProxy,
			final String proxyAddress,
			final int proxyPort,
			final String proxyCredentialUsername,
			final String proxyCredentialPassword,
			final boolean ignoreInvalidSSLCertificates) 
	{
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
		HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);
		
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

		if(url.startsWith(Consts.HTTPS_SCHEME) && ignoreInvalidSSLCertificates)
		{
			setIgnoreInvalidSSLCertificates(httpClient);
		}
		// No need for else
	
		if(useProxy)
		{
			setProxyParameters(httpClient, proxyAddress, proxyPort, proxyCredentialUsername, proxyCredentialPassword);
		}
		// No need for else

		
		StringBuilder serviceUrl = new StringBuilder();
		serviceUrl.append(url);
		serviceUrl.append(urlParameters.toString());
				
		HttpRequestBase request = initRequest(httpClient, httpRequestType, serviceUrl.toString(), urlParameters, headerParameters, acceptType, contentType, httpBodyData);

		HttpContext httpContext = new BasicHttpContext();
		
		HttpResponse response;
		
		try 
		{
			response = httpClient.execute(request, httpContext);
		}
		catch(IOException ioex)
		{
			StringBuilder errorMessageSB = new StringBuilder();
			errorMessageSB.append("Error invoking service: ");
			errorMessageSB.append(ioex.getMessage());
			errorMessageSB.append("\n");
			errorMessageSB.append("Stack Trace: ");
			if(ioex.getStackTrace() != null)
			{
				for(StackTraceElement element : ioex.getStackTrace())
				{
					errorMessageSB.append(element.toString());
					errorMessageSB.append("\n");
				}
			}
			// No need for else
			
			Log.e(TAG, errorMessageSB.toString());
			
			response = null;
		}
		
		HTTPCoreResponse httpCoreResponse;
		
		if(response != null)
		{
			httpCoreResponse = parseResponse(url, response);
		}
		else
		{
			httpCoreResponse = new HTTPCoreResponse(url, DEFAULT_HTTP_STATUS_RESULT);
		}
		
		return httpCoreResponse;
	}
	
	
	
	private HttpRequestBase initRequest(
			DefaultHttpClient httpClient,
			final HTTPRequestTypeEnum httpRequestType,
			final String serviceUrl,
			final URLParameters urlParameters,
			final Map<String, String> headerParameters,
			final String acceptType,
			final String contentType,
			final String httpBodyData)
	{
		HttpRequestBase request;
		
		StringEntity tmp;
		
		switch(httpRequestType)
		{
			case HTTP_POST:
			{
				request = new HttpPost(serviceUrl);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
				
				try 
		        {
					if(httpBodyData != null)
					{
						tmp = new StringEntity(httpBodyData, DEAFULT_ENCODING);
		            
		            	((HttpPost) request).setEntity(tmp);
					}
					else
					{
						Log.e(TAG, "Http boy data is null for POST Request");
					}
		        } 
		        catch(UnsupportedEncodingException uex) 
		        {
		            StringBuilder errorMessageSB = new StringBuilder();
		            errorMessageSB.append("Unsupported Encoding: ");
		            errorMessageSB.append(uex.getMessage());
		            errorMessageSB.append("\n");
		            errorMessageSB.append("Stack Trace: ");
		            errorMessageSB.append(uex.getStackTrace());
		            Log.e(TAG, errorMessageSB.toString());
		        }
			}
			break;
			
			case HTTP_PUT:
			{
				request = new HttpPut(serviceUrl);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
				
				try
		        {
					if(httpBodyData != null)
					{
						tmp = new StringEntity(httpBodyData, DEAFULT_ENCODING);
		            
		            	((HttpPut) request).setEntity(tmp);
					}
					else
					{
						Log.e(TAG, "Http boy data is null for PUT Request");
					}
		        } 
		        catch(UnsupportedEncodingException uex)
		        {
		            StringBuilder errorMessageSB = new StringBuilder();
		            errorMessageSB.append("Unsupported Encoding: ");
		            errorMessageSB.append(uex.getMessage());
		            errorMessageSB.append("\n");
		            errorMessageSB.append("Stack Trace: ");
		            errorMessageSB.append(uex.getStackTrace());
		            Log.e(TAG, errorMessageSB.toString());
		        }
			}
			break;
			
			case HTTP_DELETE:
			{
				request = new HttpDelete(serviceUrl);
			}
			break;
			
			default:
			case HTTP_GET:
			{
				request = new HttpGet(serviceUrl);
			}
			break;
		}
		
		if(acceptType != null)
		{
			request.setHeader("Accept", acceptType);
		}
		// No need for else (do not add header)

		if (contentType != null) 
		{
			request.setHeader("Content-Type", contentType);
		}
		// No need for else (do not add header)

		for(Entry<String, String> entry : headerParameters.entrySet())
		{
			request.setHeader(entry.getKey(), entry.getValue());
		}
		
		return request;
	}
	
	
	
	private HTTPCoreResponse parseResponse(
			final String requestURL,
			final HttpResponse response)
	{
		int httpStatusResult = DEFAULT_HTTP_STATUS_RESULT;
		
		String httpBodyContentResult = new String();
		
		if (response != null) 
		{               
			StatusLine statusLine = response.getStatusLine();
			
			if(statusLine != null)
			{
				httpStatusResult = response.getStatusLine().getStatusCode();

				HttpEntity entity = response.getEntity();

				if (entity != null)
				{
					InputStream content;
					
					try 
					{
						content = entity.getContent();
					} 
					catch (IllegalStateException isex) 
					{
						Log.e(TAG, isex.getMessage(), isex);
						content = null;
					}
					catch (IOException ioex) 
					{
						Log.e(TAG, ioex.getMessage(), ioex);
						content = null;
					}

					if(content != null)
					{
						BufferedReader br;
						
						try 
						{
							br = new BufferedReader(new InputStreamReader((content), DEAFULT_ENCODING));
						} 
						catch (UnsupportedEncodingException unencex) 
						{
							Log.e(TAG, unencex.getMessage(), unencex);
							br = null;
						}
						
						if(br != null)
						{
							String output = new String();
							StringBuilder temp = new StringBuilder();
	
							try 
							{
								while ((output = br.readLine()) != null) 
								{
									temp.append(output);
								}
							} 
							catch (IOException ioex) 
							{
								Log.e(TAG, ioex.getMessage(), ioex);
							}
	
							httpBodyContentResult = temp.toString();
	
							Log.v(TAG, "WebService body content: " + httpBodyContentResult);
						}
					}
					else
					{
						Log.v(TAG, "WebService had null body content");
					}
				}
				else
				{
					Log.v(TAG, "WebService had null entity");
				}
			}
			else
			{
				Log.v(TAG, "WebService result: Returned status code was null");
			}
		}
		else
		{
			Log.v(TAG, "WebService result: Response = null");
		}

		HTTPCoreResponse httpCoreResponse = new HTTPCoreResponse(
				requestURL,
				httpStatusResult,
				httpBodyContentResult);
		
		return httpCoreResponse;
	}
	
	
	
	private void setProxyParameters(
			DefaultHttpClient httpClient,
			final String proxyAddress,
			final int proxyPort,
			final String proxyCredentialUsername,
			final String proxyCredentialPassword)
	{
		HttpHost proxy = new HttpHost(proxyAddress, proxyPort);

		httpClient.getCredentialsProvider().setCredentials(
				AuthScope.ANY,
				new UsernamePasswordCredentials(
						proxyCredentialUsername,
						proxyCredentialPassword));

		httpClient.getParams().setParameter(
				ConnRoutePNames.DEFAULT_PROXY, 
				proxy);
	}
	
	
	
	private void setIgnoreInvalidSSLCertificates(
			DefaultHttpClient httpClient)
	{
		Log.w(TAG, "Ignoring all invalid SSL certificates!");

		try
		{
			httpClient = HttpClientWraper.wrapClient(httpClient);
		}
		catch(Exception e)
		{
			StringBuilder errorMessageSB = new StringBuilder();
			errorMessageSB.append("Failed to ignore invalid certificates: ");
			errorMessageSB.append(e.getMessage());
			errorMessageSB.append("\n");
			errorMessageSB.append("Stack Trace: ");
			
			if(e.getStackTrace() != null)
			{
				for(StackTraceElement element : e.getStackTrace())
				{
					errorMessageSB.append(element.toString());
					errorMessageSB.append("\n");
				}
			}
			Log.e(TAG, errorMessageSB.toString());
		}
	}
}