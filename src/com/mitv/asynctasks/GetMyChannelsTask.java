
package com.mitv.asynctasks;



import android.os.AsyncTask;



public class GetMyChannelsTask 
	extends AsyncTask<String, Void, String> 
{
	private static final String	TAG	= "GetMyChannelsTask";
	
	
	
	@Override
	protected String doInBackground(String... params)
	{
//		try
//		{
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpGet httpGet = new HttpGet();
//			httpGet.setHeader("Authorization", "Bearer " + SecondScreenApplication.getInstance().getAccessToken());
//			httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
//			httpGet.setURI(new URI(Consts.URL_MY_CHANNEL_IDS));
//
//			HttpResponse response = httpClient.execute(httpGet);
//			
//			if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode())
//			{
//				HttpEntity entityHttp = response.getEntity();
//				InputStream inputStream = entityHttp.getContent();
//				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//				StringBuilder sb = new StringBuilder();
//				String line = null;
//				
//				while ((line = reader.readLine()) != null) 
//				{
//					sb.append(line + "\n");
//				}
//				
//				inputStream.close();
//				
//				Log.d(TAG, "Good response on GET ");
//				
//				return sb.toString();
//			} 
//			else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) 
//			{
//				Log.d(TAG, "Get my channels: Invalid token");
//				ApiClient.forceLogin();
//				
//				return Consts.ERROR_STRING;
//			} 
//			else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) 
//			{
//				Log.d(TAG, "Get my channels: Missing token");
//				ApiClient.forceLogin();
//				
//				return Consts.ERROR_STRING;
//			}
//		} 
//		catch (UnsupportedEncodingException e) 
//		{
//			e.printStackTrace();
//		} 
//		catch (ClientProtocolException e)
//		{
//			e.printStackTrace();
//		} 
//		catch (Exception ex) 
//		{
//			ex.printStackTrace();
//		}
//		
//		return Consts.ERROR_STRING;
		return null;
	}
}
