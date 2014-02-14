
package com.millicom.asynctasks;



import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.millicom.interfaces.AdCallBackInterface;
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.Consts;
import com.mitv.manager.AppConfigurationManager;
import com.mitv.model.OldAdzerkAd;



public class AdzerkAdTask
	extends AsyncTask<String, Void, Void> 
{
	private final String TAG = "GetAdzerkAdTask";
	
	private String divId;
	private AdCallBackInterface adCallBack = null;
	private List<Integer> adFormats;
	
	
	
	public AdzerkAdTask(String divId, List<Integer> adFormats, AdCallBackInterface adCallBack) 
	{
		this.divId = divId;
		this.adFormats = adFormats;
		this.adCallBack = adCallBack;
	}
	
	//TODO test using the 'page' pattern, but how should we enter the params to the HTTP POST (not get!)
//	@Override
//	protected Void doInBackground(String... params) {
//		SSAdzerkAdPage.getInstance().getPage(divId, new SSPageCallback() {
//			@Override
//			public void onGetPageResult(SSPageGetResult aPageGetResult) {
//				AdzerkAd ad = SSAdzerkAdPage.getInstance().getAd();
//				
//				if(ad != null) {
//					GetAdzerkAdTask.this.adCallBack.onAdResult(ad);
//				}
//			}
//		});
//
//		return null;
//	}
	
	protected Void doInBackground(String... params)
	{
		OldAdzerkAd ad = null;
		
		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(Consts.ADS_POST_URL);
			
			int networkId = AppConfigurationManager.getInstance().getAdzerkNetworkId();
			int siteId = AppConfigurationManager.getInstance().getAdzerkSiteId();
			
			AdzerkJSONObjectPlacement placement = new AdzerkJSONObjectPlacement(divId, networkId, siteId, adFormats);
			
			List<AdzerkJSONObjectPlacement> placements = Arrays.asList(placement);
			
			AdzerkJSONObjectRequest adRequestJSONObject = new AdzerkJSONObjectRequest(placements, true);
			
			String jsonString = new Gson().toJson(adRequestJSONObject);
			
			StringEntity stringEntity = new StringEntity(jsonString);

			httpPost.setEntity(stringEntity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse response = client.execute(httpPost);

			if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) 
			{
				HttpEntity entity = response.getEntity();
				
				String result = null;
				
				if (entity != null) 
				{
					InputStream instream = entity.getContent();
					
					result = NetworkUtils.convertStreamToString(instream, Charset.forName("UTF-8"));
					
					instream.close();
				}

				JSONObject jsonObj = new JSONObject(result);

				ad = new OldAdzerkAd(divId, jsonObj);
			} 
			else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE)
			{
				Log.d(TAG, "Invalid Token!");
			}
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
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		if(ad != null) 
		{
			this.adCallBack.onAdResult(ad);
		}
		
		return null;
	}
	
	
	private class AdzerkJSONObjectRequest 
	{
		private AdzerkJSONObjectUser user;
		private boolean isMobile;
		private List<AdzerkJSONObjectPlacement> placements;
		

		public AdzerkJSONObjectRequest(List<AdzerkJSONObjectPlacement> placements, boolean isMobile, AdzerkJSONObjectUser user)
		{
			this.user = user;
			this.placements = placements;
			this.isMobile = isMobile;
		}
		
		public AdzerkJSONObjectRequest(List<AdzerkJSONObjectPlacement> placements, boolean isMobile)
		{
			this(placements, isMobile, null);
		}
	}
	
	
	
	private class AdzerkJSONObjectPlacement
	{
		private String divName;
		private Integer networkId;
		private Integer siteId;
		private List<Integer> adTypes;
		
		public AdzerkJSONObjectPlacement(String divName, Integer networkId, Integer siteId, List<Integer> adTypes)
		{
			this.divName = divName;
			this.networkId = networkId;
			this.siteId = siteId;
			this.adTypes = adTypes;
		}
	}
	
	
	
	private class AdzerkJSONObjectUser
	{
		private String userKey;
		
		public AdzerkJSONObjectUser(String userKey)
		{
			this.userKey = userKey;
		}
	}
}



