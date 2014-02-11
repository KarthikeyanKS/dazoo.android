
package com.millicom.asynctasks;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;
import com.mitv.Consts;
import com.mitv.utilities.JSONUtilities;



public class AddLikeTask
	extends AsyncTask<String, Void, Integer> 
{
	private static final String	TAG	= "AddLikeTask";
	
	
	
	@Override
	protected Integer doInBackground(String... params) 
	{
		try 
		{
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(Consts.URL_LIKES);
			httpPost.setHeader("Authorization", "Bearer " + params[0]);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(
					Arrays.asList(Consts.API_LIKETYPE, Consts.API_ENTITY_ID),
					Arrays.asList(params[2], params[1]));
			
			Log.d(TAG, "Add like holder: " + holder);
			
			StringEntity entity = new StringEntity(holder.toString());
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			
			Log.d(TAG, "RESPONSE: " + response.getStatusLine().getStatusCode());
			
			return response.getStatusLine().getStatusCode();
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return Consts.BAD_RESPONSE;
	}
}