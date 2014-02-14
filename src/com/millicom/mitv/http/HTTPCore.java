
package com.millicom.mitv.http;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
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
	
	
	/*
	public HTTPCoreResponse httpGet(
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerData,
			final Context context)
	{
		StringBuilder messageSB = new StringBuilder();
		messageSB.append("HTTP Get request for url: ");
        messageSB.append(url);
        messageSB.append(urlParameters.toString());
        Log.d(TAG, messageSB.toString());

        HTTPCoreResponse response = executeRequest(
        		url,
				urlParameters,
				headerData,
				Consts.JSON_MIME_TYPE,
				Consts.JSON_MIME_TYPE,
				HTTPRequestTypeEnum.HTTP_GET,
				null,
				context.getResources().getInteger(R.integer.api_connection_timeout_in_miliseconds),
				context.getResources().getInteger(R.integer.api_socket_timeout_in_miliseconds),
				false,
				new String(),
				0,
				new String(),
				new String(),
				false);

		return response;
	}
	
	
	
	public HTTPCoreResponse httpDelete(
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerData,
			final Context context)
	{
		StringBuilder messageSB = new StringBuilder();
        messageSB.append("HTTP Get request for url: ");
        messageSB.append(url);
        Log.d(TAG, messageSB.toString());

        HTTPCoreResponse response = executeRequest(
        		url,
        		urlParameters,
        		headerData,
        		Consts.JSON_MIME_TYPE,
        		Consts.JSON_MIME_TYPE,
				HTTPRequestTypeEnum.HTTP_DELETE,
				null,
				context.getResources().getInteger(R.integer.api_connection_timeout_in_miliseconds),
				context.getResources().getInteger(R.integer.api_socket_timeout_in_miliseconds),
				false,
				new String(),
				0,
				new String(),
				new String(),
				false);

		return response;
	}
	
	
	
	public HTTPCoreResponse httpPut(
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerData,
			final Context context)
	{
		StringBuilder messageSB = new StringBuilder();
		messageSB.append("HTTP Get request for url: ");
        messageSB.append(url);
        messageSB.append(urlParameters.toString());
        Log.d(TAG, messageSB.toString());

        HTTPCoreResponse response = executeRequest(
        		url,
				urlParameters,
				headerData,
				Consts.JSON_MIME_TYPE,
				Consts.JSON_MIME_TYPE,
				HTTPRequestTypeEnum.HTTP_GET,
				null,
				context.getResources().getInteger(R.integer.api_connection_timeout_in_miliseconds),
				context.getResources().getInteger(R.integer.api_socket_timeout_in_miliseconds),
				false,
				new String(),
				0,
				new String(),
				new String(),
				false);

		return response;
	}
	
	
	
	
	public HTTPCoreResponse httpPost(
			final String url,
			final URLParameters urlParameters,
			final Map<String, String> headerData,
			final String bodyContentData,
			final Context context)
	{
		StringBuilder messageSB = new StringBuilder();
        messageSB.append("HTTP Get request for url: ");
        messageSB.append(url);
        Log.d(TAG, messageSB.toString());

        HTTPCoreResponse response = executeRequest(
        		url,
        		urlParameters,
        		headerData,
        		Consts.JSON_MIME_TYPE,
        		Consts.JSON_MIME_TYPE,
				HTTPRequestTypeEnum.HTTP_POST,
				bodyContentData,
				context.getResources().getInteger(R.integer.api_connection_timeout_in_miliseconds),
				context.getResources().getInteger(R.integer.api_socket_timeout_in_miliseconds),
				false,
				new String(),
				0,
				new String(),
				new String(),
				false);

		return response;
	}
	*/
	
	
	
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
		int httpStatusResult = DEFAULT_HTTP_STATUS_RESULT;
		
		String httpBodyContentResult = new String();

		HttpParams myParams = new BasicHttpParams();
		
		HttpContext localContext = new BasicHttpContext();
		
		HttpConnectionParams.setConnectionTimeout(myParams, connectionTimeout);
		
		HttpConnectionParams.setSoTimeout(myParams, socketTimeout);
		
		DefaultHttpClient httpClient = new DefaultHttpClient(myParams);

		if(url.startsWith(Consts.HTTPS_SCHEME) && ignoreInvalidSSLCertificates)
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
		// No need for else
	
		if(useProxy)
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
		// No need for else

		
		StringBuilder serviceUrl = new StringBuilder();
		serviceUrl.append(url);
		serviceUrl.append(urlParameters.toString());
		
		HttpResponse response;
		
		StringEntity tmp;
		
		HttpRequestBase request;
		
		switch(httpRequestType)
		{
			case HTTP_POST:
			{
				request = new HttpPost(serviceUrl.toString());
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
				request = new HttpPut(serviceUrl.toString());
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
				request = new HttpDelete(serviceUrl.toString());
			}
			break;
			
			default:
			case HTTP_GET:
			{
				request = new HttpGet(serviceUrl.toString());
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

		try 
		{
			response = httpClient.execute(request, localContext);

			if (response != null) 
			{               
				if(response.getStatusLine() != null)
				{
					httpStatusResult = response.getStatusLine().getStatusCode();

					HttpEntity entity = response.getEntity();

					if (entity != null)
					{
						InputStream content = entity.getContent();

						if(content != null)
						{
							BufferedReader br = new BufferedReader(new InputStreamReader((content), DEAFULT_ENCODING));

							String output = new String();
							StringBuilder temp = new StringBuilder();

							while ((output = br.readLine()) != null) 
							{
								temp.append(output);
							}

							httpBodyContentResult = temp.toString();

							Log.v(TAG, "WebService body content: " + httpBodyContentResult);
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
		}
		catch (Exception e) 
		{
			StringBuilder errorMessageSB = new StringBuilder();
			errorMessageSB.append("Error invoking WebService: ");
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
			// No need for else
			Log.e(TAG, errorMessageSB.toString());
		}

		HTTPCoreResponse httpCoreResponse = new HTTPCoreResponse(
				httpStatusResult,
				httpBodyContentResult);

		return httpCoreResponse;
	}
}
