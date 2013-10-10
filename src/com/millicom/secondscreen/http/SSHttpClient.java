package com.millicom.secondscreen.http;

import java.io.InputStream;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.damnhandy.uri.template.UriTemplate;

public class SSHttpClient<T_Result> {

	private static final String				TAG					= "SSHttpClient";

	// private static HttpClient sHttpClient = null;
	private HttpClient						sHttpClient			= null;
	private static HttpContext				sHttpContext		= null;

	private SSHttpClientGetTask				mHttpClientGetTask	= null;
	private SSHttpClientCallback<T_Result>	mHttpClientCallback	= null;

	public static CookieStore getCookieStore() {
		Log.d(TAG, "getCookieStore");
		CookieStore cookieStore = null;

		// If we have a http context
		if (sHttpContext != null) {
			Log.d(TAG, "Get the cooike store");

			// Get the cookie store
			cookieStore = (CookieStore) sHttpContext.getAttribute(ClientContext.COOKIE_STORE);
		}
		return cookieStore;
	}

	public void cancelRequest() {
		// If we have a get task
		if (mHttpClientGetTask != null) {
			// Cancel it

			Log.d(TAG, "will cancel request");
			mHttpClientGetTask.cancelRequest();
		}
	}

	@TargetApi(11)
	public boolean doHttpGet(String uri, SSHttpClientCallback<T_Result> httpClientCallback) {

		mHttpClientCallback = httpClientCallback;
		mHttpClientGetTask = new SSHttpClientGetTask();
		// Only downloads, can be changed from anywhere
		UriTemplate template = UriTemplate.fromTemplate(uri);

		Log.d(TAG, "Get uri : " + template.expand());

		// Do http get in background
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mHttpClientGetTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, template.expand());
		} else {
			mHttpClientGetTask.execute(template.expand());
		}
		return true;
	}

	private class SSHttpClientGetTask extends AsyncTask<String, Void, T_Result> {

		protected HttpGet		mHttpGet	= null;
		protected ReentrantLock	mLock		= new ReentrantLock();

		public void cancelRequest() {
			Log.d(TAG, "cancel request!!");
			mLock.lock();
			// Critical section
			try {
				// Cancel the task
				cancel(true);
				// If we have a http get request
				if (mHttpGet != null) {
					Log.d(TAG, "abort request!!!");
					// Abort it
					mHttpGet.abort();
				}
			} finally {
				mLock.unlock();
			}
		}

		@Override
		protected T_Result doInBackground(String... params) {

			SSHttpClientGetResult httpClientGetResult = new SSHttpClientGetResult();
			T_Result result = null;

			// If we have any input
			if (params[0] != null) {

				Log.d(TAG, "Create http client");

				// Get the http client
				HttpClient httpClient = getHttpClient();
				try {
					Log.d(TAG, "Create http get request");
					// Remember the Uri
					httpClientGetResult.setUri(params[0]);
					// If we can create the http get request
					if (createHttpGet(httpClientGetResult.getUri())) {

						Log.d(TAG, "Do http get request");

						// Do http get request, with our context to keep track of cookies
						HttpResponse httpResponse = httpClient.execute(mHttpGet, sHttpContext);

						Log.d(TAG, "Get http response, status code: " + httpResponse.getStatusLine().getStatusCode());

						// Get http response entity
						HttpEntity httpResponseEntity = httpResponse.getEntity();
						if (httpResponseEntity != null) {

							Log.d(TAG, "Get http response stream");

							// Get response content as stream
							InputStream responseStream = httpResponseEntity.getContent();
							try {
								// Get the response from the stream
								getResponseFromStream(httpClientGetResult, responseStream);
								// Set result
								httpClientGetResult.setResult(httpResponse.getStatusLine().getStatusCode());
								// If we have a callback and the task isn't cancelled
								if ((mHttpClientCallback != null) && !isCancelled()) {

									Log.d(TAG, "Let callback handle result");

									// Give it the possibility to handle the result in background as well
									result = mHttpClientCallback.onHandleHttpGetResultInBackground(httpClientGetResult);
								}
							} finally {
								// If we have a stream close it
								if (responseStream != null) {
									responseStream.close();
								}
								Log.d(TAG, "Release the response content");
								// Release the response content
								httpResponseEntity.consumeContent();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Return the result, is input parameter of onPostExecute()
			return result;
		}

		@Override
		protected void onPostExecute(T_Result result) {

			Log.d(TAG, "In onPostExecute");

			// If we have a callback
			if (mHttpClientCallback != null) {

				Log.d(TAG, "Let callback handle final result : " + result);

				// Tell it about the result
				mHttpClientCallback.onHttpGetResultFinal(result);
			}
		}

		protected boolean createHttpGet(String uri) {
			mLock.lock();
			// Critical section
			try {
				// If the task isn't cancelled
				if (!isCancelled()) {
					// Create http get request
					mHttpGet = new HttpGet(uri);
				}
			} finally {
				mLock.unlock();
			}
			return mHttpGet != null;
		}

		protected HttpClient getHttpClient() {

			// If we haven't created the one and only http client
			if (sHttpClient == null) {
				// Create the android http client
				// sHttpClient = AndroidHttpClient.newInstance("Android");

				sHttpClient = new DefaultHttpClient();

				// Create the http context to be used by the client while executing requests
				sHttpContext = new BasicHttpContext();
				// Create and bind a cookie store to the http context
				CookieStore cookieStore = new BasicCookieStore();
				sHttpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			}
			return sHttpClient;
		}
	}

	protected void getResponseFromStream(SSHttpClientGetResult httpClientGetResult, InputStream responseStream) throws Exception {
		Log.d(TAG, "Get response stream as string");
		// Get response stream as a string
		String response = NetworkUtils.convertStreamToString(responseStream);
		Log.d(TAG, "Save response stream as json object in result");
		// Set response string as a json object in result

		// httpClientGetResult.setJson(new JSONObject(response));
		try {
			httpClientGetResult.setJsonArray(new JSONArray(response));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}