

package com.mitv.asynctasks;



public class GetFeedMoreTask 
{
}

//class GetFeedMoreTask extends AsyncTask<Void, Void, Boolean> {
	//
	// ArrayList<TVFeedItem> moreFeedItems = new ArrayList<TVFeedItem>();
	//
	// protected void onPostExecute(Boolean result) {
	// Log.d(TAG, "result: " + result);
	// if (result) {
	// MiTVStore.getInstance().addItemsToActivityFeed(moreFeedItems);
	// mAdapter.addItems(moreFeedItems);
	// mStartIndex = mStartIndex + mNextStep;
	// } else {
	// if (mNoMoreItems) {
	// mListView.removeFooterView(mListFooterView);
	// }
	// }
	// moreFeedItems.clear();
	// mNoTask = true;
	// showScrollSpinner(false);
	// }

	// @Override
	// protected Boolean doInBackground(Void... arg0) {
	// boolean result = false;
	// try {
	// HttpClient client = new DefaultHttpClient();
	//
	// HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
	// SchemeRegistry registry = new SchemeRegistry();
	// registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	//
	// SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
	// socketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	// registry.register(new Scheme("https", socketFactory, 443));
	// SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
	//
	// DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
	// // Set verifier
	// HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	//
	// List<NameValuePair> urlParams = new LinkedList<NameValuePair>();
	// urlParams.add(new BasicNameValuePair(Consts.API_SKIP, String.valueOf(mStartIndex)));
	// urlParams.add(new BasicNameValuePair(Consts.API_LIMIT, String.valueOf(mNextStep)));
	//
	// URI uri = new URI(Consts.URL_ACTIVITY_FEED + "?" + URLEncodedUtils.format(urlParams, "utf-8"));
	//
	// Log.d(TAG, "mStartIndex: " + String.valueOf(mStartIndex) + " mNextStep: " + String.valueOf(mNextStep));
	// Log.d(TAG, "Feed more items: " + uri.toString());
	//
	// HttpGet httpGet = new HttpGet(uri);
	// httpGet.setHeader("Authorization", "Bearer " + SecondScreenApplication.getInstance().getAccessToken());
	// // header to accept the json in a correct encoding
	// httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
	// HttpResponse response = httpClient.execute(httpGet);
	//
	// if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
	// Log.d(TAG, "GOOD RESPONSE");
	//
	// // comment for beta
	// // if (response.getFirstHeader("Age") != null) {
	// // mRequestAge = Integer.valueOf(response.getFirstHeader("Age").getValue());
	// // }
	// // if (response.getFirstHeader("Cache-Control").getValue().substring(8) != null) {
	// // mRequestMaxAge = Integer.valueOf(response.getFirstHeader("Cache-Control").getValue().substring(8));
	// // }
	// // mNextRequestTime = mRequestMaxAge - mRequestAge;
	//
	// HttpEntity entityHttp = response.getEntity();
	// InputStream inputStream = entityHttp.getContent();
	// BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
	// StringBuilder sb = new StringBuilder();
	// String line = null;
	// while ((line = reader.readLine()) != null) {
	// sb.append(line + "\n");
	// }
	// inputStream.close();
	// String jsonString = sb.toString();
	//
	// if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) {
	// JSONArray feedListJsonArray;
	// try {
	// feedListJsonArray = new JSONArray(jsonString);
	// int size = feedListJsonArray.length();
	// Log.d(TAG, "FEED MORE ITEMS SIZE: " + String.valueOf(size));
	//
	// if (size == 0) {
	// mNoMoreItems = true;
	// return result;
	// }
	// int endIndex = 0;
	// if (mNextStep < size) endIndex = mNextStep;
	// else endIndex = size;
	// Log.d(TAG, "endIndex:" + endIndex + " mStartIndex: " + mStartIndex + " mStep: " + mNextStep);
	//
	// for (int i = 0; i < endIndex; i++) {
	// moreFeedItems.add(ContentParser.parseFeedItem(feedListJsonArray.getJSONObject(i)));
	// result = true;
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// } else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
	// Log.d(TAG, "Get Activity Feed: Invalid");
	// ApiClient.forceLogin();
	// } else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
	// Log.d(TAG, "Get Activity Feed: Missing token");
	// ApiClient.forceLogin();
	// }
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// }
	// return result;
	// }
	//
	// }

	// private void scheduleFeedRefresh() {
	// if (mNextRequestTime != 0) {
	//
	// final Handler handler = new Handler();
	// handler.postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// MiTVStore.getInstance().reinitializeFeed();
	// activityFeed.clear();
	// activityFeed = new ArrayList<TVFeedItem>();
	// loadPage();
	// }
	// }, mNextRequestTime * 1000);
	// }
	// }
