
package com.mitv.asynctasks;



import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import com.mitv.Consts;
import com.mitv.SecondScreenApplication;
import com.mitv.manager.ApiClient;



public class UpdateMyChannelsTask 
	extends AsyncTask<String, Void, Boolean> 
{
	private static final String	TAG	= "UpdateMyChannelsTask";
	
	
	
	@Override
	protected Boolean doInBackground(String... params) 
	{
//		try 
//		{
//			HttpClient client = new DefaultHttpClient();
//
//			HttpPost httpPost = new HttpPost(Consts.URL_MY_CHANNEL_IDS);
//			httpPost.setHeader("Authorization", "Bearer " + SecondScreenApplication.getInstance().getAccessToken());
//			httpPost.setHeader("Accept", "application/json");
//			httpPost.setHeader("Content-Type", "application/json");
//			StringEntity jsonEntity = new StringEntity(params[0]);
//			httpPost.setEntity(jsonEntity);
//
//			HttpResponse response = client.execute(httpPost);
//
//			if (Consts.GOOD_RESPONSE_CHANNELS_ARE_ADDED == response.getStatusLine().getStatusCode() ||
//					Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) 
//			{
//				Log.d(TAG, "Update MY CHANNELS: SUCCESS");
//				return true;
//			} 
//			else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode())
//			{
//				Log.d(TAG, "Update MY CHANNELS: Invalid token");
//				ApiClient.forceLogin();
//				return false;
//			} 
//			else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode())
//			{
//				Log.d(TAG, "Update MY CHANNELS: Missing token");
//				ApiClient.forceLogin();
//				return false;
//			} 
//			else 
//			{
//				Log.d(TAG, "Error, but not identified");
//			}
//		} 
//		catch (ClientProtocolException e) 
//		{
//			System.out.println("CPE" + e);
//		} 
//		catch (IOException e) 
//		{
//			System.out.println("IOE" + e);
//		}
//		
		return false;
	}
}
