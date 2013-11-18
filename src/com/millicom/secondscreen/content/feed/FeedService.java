package com.millicom.secondscreen.content.feed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.manager.ContentParser;

public class FeedService {

	private static final String	TAG	= "FeedService";

	public static ArrayList<FeedItem> getActivityFeed(String token, int startIndex, int step) {
		ArrayList<FeedItem> dazooActivityFeedList = new ArrayList<FeedItem>();
		GetActivityFeedTask getActivityFeedTask = new GetActivityFeedTask();
		String jsonString = "";
		try {
			jsonString = getActivityFeedTask.execute(token, String.valueOf(startIndex), String.valueOf(step)).get();
			if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) {
				JSONObject feedListJson = new JSONObject(jsonString);
				JSONArray feedLisJsonArray = feedListJson.optJSONArray(Consts.DAZOO_FEED_ITEMS);

				int size = feedLisJsonArray.length();
				Log.d(TAG, "FEED ITEMS SIZE: " + String.valueOf(size));
				// TODO: UPDATE WHEN THE PAGINATION IS DONE BY THE BACKEND
				int endIndex = 0;
				if (startIndex + step < size) endIndex = startIndex + step;
				else endIndex = size;

				for (int i = startIndex; i < endIndex; i++) {
					dazooActivityFeedList.add(ContentParser.parseFeedItem(feedLisJsonArray.getJSONObject(i)));
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dazooActivityFeedList;
	}

	private static class GetActivityFeedTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				// HttpClient httpClient = new DefaultHttpClient();
				String token = params[0];
				String skip = params[1];
				String limit = params[2];

				HttpClient client = new DefaultHttpClient();
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				registry.register(new Scheme("https", socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);

				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
				// Set verifier
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

				
				List<NameValuePair> urlParams = new LinkedList<NameValuePair>();
				urlParams.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_SKIP,skip));
				urlParams.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_LIMIT,limit));
				
				URI uri = new URI(Consts.MILLICOM_SECONDSCREEN_ACTIVITY_FEED_URL + "?" + URLEncodedUtils.format(urlParams, "utf-8"));
				
				HttpGet httpGet = new HttpGet(uri);
				httpGet.setHeader("Authorization", "Bearer " + token);		
				HttpResponse response = httpClient.execute(httpGet);
				
				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "GOOD RESPONSE");
					HttpEntity entityHttp = response.getEntity();
					InputStream inputStream = entityHttp.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					inputStream.close();
					return sb.toString();
				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get Activity Feed: Invalid");
					return Consts.ERROR_STRING;
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get Activity Feed: Missing token");
					return Consts.ERROR_STRING;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return Consts.ERROR_STRING;
		}
	}

}
