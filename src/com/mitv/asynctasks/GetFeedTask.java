
package com.mitv.asynctasks;



import android.os.AsyncTask;



public class GetFeedTask 
	extends AsyncTask<Void, Void, Boolean> 
{
	@SuppressWarnings("unused")
	private static final String	TAG	= "GetFeedTask";
	
	
	private int startIndex;
	private int step;
	
	
	public GetFeedTask(
			int startIndex,
			int step)
	{
		this.startIndex = startIndex;
		this.step = step;
	}
	
	
	
	protected void onPostExecute(Boolean result) 
	{
		// TODO
//		Log.d(TAG, "oN POST EXECUTE");
//		if (result) 
//		{
//			if (activityFeed != null) 
//			{
//				if (activityFeed.isEmpty() != true) 
//				{
//					MiTVStore.getInstance().setActivityFeed(activityFeed);
//
//					Log.d(TAG, "//////////////");
//					updateUI(REQUEST_STATUS.SUCCESSFUL);
//					mStartIndex = mStartIndex + mStep;
//				} 
//				else 
//				{
//					Log.d(TAG, "EMPTY");
//					updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
//				}
//			}
//
//			schedule the next feed update
//			scheduleFeedRefresh();
//
//		} 
//		else 
//		{
//			Log.d(TAG, "No backend response");
//			updateUI(REQUEST_STATUS.BAD_REQUEST);
//		}
	}

	
	
	@Override
	protected Boolean doInBackground(Void... params) {
//		boolean result = false;
//		try {
//			HttpClient client = new DefaultHttpClient();
//
//			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//			SchemeRegistry registry = new SchemeRegistry();
//			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//
//			SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
//			socketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//			registry.register(new Scheme("https", socketFactory, 443));
//			SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
//
//			DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
//			// Set verifier
//			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
//
//			List<NameValuePair> urlParams = new LinkedList<NameValuePair>();
//			urlParams.add(new BasicNameValuePair(Consts.API_SKIP, String.valueOf(startIndex)));
//			urlParams.add(new BasicNameValuePair(Consts.API_LIMIT, String.valueOf(step)));
//
//			URI uri = new URI(Consts.URL_ACTIVITY_FEED + "?" + URLEncodedUtils.format(urlParams, "utf-8"));
//
//			HttpGet httpGet = new HttpGet(uri);
//			
//			httpGet.setHeader("Authorization", "Bearer " + SecondScreenApplication.getInstance().getAccessToken());
//			// header to accept the json in a correct encoding
//			httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
//			HttpResponse response = httpClient.execute(httpGet);
//
//			if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
//				// Log.d(TAG, "" + response.getFirstHeader("Age"));
//				// Log.d(TAG, "" + response.getFirstHeader("Cache-Control"));
//				//
//				// mRequestAge = Integer.valueOf(response.getFirstHeader("Age").getValue());
//				// mRequestMaxAge = Integer.valueOf(response.getFirstHeader("Cache-Control").getValue().substring(8));
//				// mNextRequestTime = mRequestMaxAge - mRequestAge;
//				//
//				// Log.d(TAG, "AGE: " + mRequestAge + " Max Age: " + mRequestMaxAge + " Next time: " + mNextRequestTime);
//
//				Log.d(TAG, "GOOD RESPONSE");
//				HttpEntity entityHttp = response.getEntity();
//				InputStream inputStream = entityHttp.getContent();
//				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//				StringBuilder sb = new StringBuilder();
//				String line = null;
//				while ((line = reader.readLine()) != null) {
//					sb.append(line + "\n");
//				}
//				inputStream.close();
//				String jsonString = sb.toString();
//
//				if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) {
//					JSONArray feedListJsonArray;
//					try {
//						feedListJsonArray = new JSONArray(jsonString);
//
//						int size = feedListJsonArray.length();
//						Log.d(TAG, "FEED ITEMS SIZE: " + String.valueOf(size));
//						int endIndex = 0;
//						if (startIndex + step < size) endIndex = startIndex + step;
//						else endIndex = size;
//						Log.d(TAG, "endIndex:" + endIndex + " mStartIndex: " + startIndex + " mStep: " + step);
//
//						for (int i = startIndex; i < endIndex; i++) 
//						{
//							// TODO
//							//activityFeed.add(ContentParser.parseFeedItem(feedListJsonArray.getJSONObject(i)));
//							result = true;
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
//				Log.d(TAG, "Get Activity Feed: Invalid");
//				ApiClient.forceLogin();
//			} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
//				Log.d(TAG, "Get Activity Feed: Missing token");
//				ApiClient.forceLogin();
//			}
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//		return result;
		return null;
	}
}
