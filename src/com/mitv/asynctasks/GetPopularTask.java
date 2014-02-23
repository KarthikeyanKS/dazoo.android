package com.mitv.asynctasks;

public class GetPopularTask
{}

//class GetPopularTask extends AsyncTask<Void, Void, Boolean> {
	//
	// protected void onPostExecute(Boolean result) {
	// if (result) {
	// if (mPopularBroadcasts != null) {
	// if (mPopularBroadcasts.isEmpty() != true) {
	// MiTVStore.getInstance().setPopularFeed(mPopularBroadcasts);
	// updateUI(REQUEST_STATUS.SUCCESSFUL);
	// } else {
	// updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
	// }
	// }
	// } else {
	// updateUI(REQUEST_STATUS.FAILED);
	// }
	// }
	//
	// @Override
	// protected Boolean doInBackground(Void... params) {
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
	// urlParams.add(new BasicNameValuePair(Consts.API_POPULAR_COUNT, String.valueOf(Consts.API_POPULAR_COUNT_DEFAULT)));
	//
	// URI uri = new URI(Consts.URL_POPULAR + "?" + URLEncodedUtils.format(urlParams, "utf-8"));
	//
	// HttpGet httpGet = new HttpGet(uri);
	// httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
	// HttpResponse response = httpClient.execute(httpGet);
	//
	// if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
	// Log.d(TAG, "GOOD RESPONSE");
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
	// if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) {
	// JSONArray jsonArray = new JSONArray(jsonString);
	// if (jsonArray != null) {
	// int size = jsonArray.length();
	// for (int i = 0; i < size; i++) {
	// mPopularBroadcasts.add(ContentParser.parseBroadcast(jsonArray.getJSONObject(i)));
	// }
	// return true;
	// }
	// }
	// } else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
	// Log.d(TAG, "Get Activity Feed: Invalid");
	// ApiClient.forceLogin();
	// } else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
	// Log.d(TAG, "Get Activity Feed: Missing token");
	// ApiClient.forceLogin();
	// }
	//
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return result;
	// }
	// }
