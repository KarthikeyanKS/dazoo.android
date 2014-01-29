package com.mitv.mychannels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mitv.Consts;
import com.mitv.SecondScreenApplication;
import com.mitv.manager.ContentParser;
import com.mitv.manager.LoginManager;
import com.mitv.storage.DazooStore;

public class MyChannelsService {

	private static final String	TAG	= "MyChannelsService";

	public static boolean updateMyChannelsList(String userToken, String channelsJSON) {
		UpdateMyChannelsTask addChannelToMyChannelsTask = new UpdateMyChannelsTask();

		try {
			boolean isAdded = addChannelToMyChannelsTask.execute(userToken, channelsJSON).get();
			if (isAdded == true) {
				Log.d(TAG, "Channels are updated!");
				return true;
			} else {
				Log.d(TAG, "Error! MY CHANNELS are not updated");
				return false;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean getMyChannels(String userToken) {
		GetMyChannelsTask getMyChannelsTask = new GetMyChannelsTask();

		String responseStr;
		try {
			responseStr = getMyChannelsTask.execute(userToken).get();
			Log.d(TAG, "List of My Channels: " + responseStr);

			if (responseStr != null && TextUtils.isEmpty(responseStr) != true && responseStr != Consts.ERROR_STRING) {
				// the extra check for ERROR_STRING was added to distinguish between empty response (there are no stored channels to this user) and empty response in case of error
				ArrayList<String> channelIds = new ArrayList<String>();
				channelIds = ContentParser.parseChannelIds(new JSONArray(responseStr));
				DazooStore.getInstance().setMyChannelIds(channelIds);

				return true;
			} else return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	// fetch the "My channels" of the logged in user
	private static class GetMyChannelsTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet();
				httpGet.setHeader("Authorization", "Bearer " + params[0]);
				httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
				httpGet.setURI(new URI(Consts.MILLICOM_SECONDSCREEN_MY_CHANNELS_URL));

				HttpResponse response = httpClient.execute(httpGet);
				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
					HttpEntity entityHttp = response.getEntity();
					InputStream inputStream = entityHttp.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					inputStream.close();
					Log.d(TAG, "Good response on GET ");
					return sb.toString();
				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get my channels: Invalid token");
					LoginManager.forceLogin();
					return Consts.ERROR_STRING;
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get my channels: Missing token");
					LoginManager.forceLogin();
					return Consts.ERROR_STRING;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return Consts.ERROR_STRING;
		}
	}

	// add the channel to the "My channel"
	private static class UpdateMyChannelsTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_MY_CHANNELS_URL);
				httpPost.setHeader("Authorization", "Bearer " + params[0]);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-Type", "application/json");
				StringEntity jsonEntity = new StringEntity(params[1]);
				httpPost.setEntity(jsonEntity);

				HttpResponse response = client.execute(httpPost);

				if (Consts.GOOD_RESPONSE_CHANNELS_ARE_ADDED == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Update MY CHANNELS: SUCCESS");
					return true;
				} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Update MY CHANNELS: Invalid token");
					LoginManager.forceLogin();
					return false;
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Update MY CHANNELS: Missing token");
					LoginManager.forceLogin();
					return false;
				} else {
					Log.d(TAG, "Error, but not identified");
				}
			} catch (ClientProtocolException e) {
				System.out.println("CPE" + e);
			} catch (IOException e) {
				System.out.println("IOE" + e);
			}
			return false;
		}
	}

}
