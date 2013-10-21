package com.millicom.secondscreen.like;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.SecondScreenApplication;

public class LikeService {

	private static final String	TAG	= "LikeService";

	public static void showSetLikeToast(Activity activity, String likedContentName) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.layout_notification_set_toast, (ViewGroup) activity.findViewById(R.id.notification_set_toast_container));

		final Toast toast = new Toast(activity.getApplicationContext());

		TextView text = (TextView) layout.findViewById(R.id.notification_set_toast_tv);
		text.setText(likedContentName + activity.getResources().getString(R.string.reminder_text_set));

		toast.setGravity(Gravity.BOTTOM, 0, 200);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static boolean addLike(String token, String entityId, String entityType) {
	
		AddLikeTask addLikeTask = new AddLikeTask();
		int result = 0;
		try {
			result = addLikeTask.execute(token, entityId, entityType).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Consts.GOOD_RESPONSE == result) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean removeLike(String token, String entityId){
		DeleteLikeTask deleteLikeTask = new DeleteLikeTask();
		int isDeleted = 0;
		try {
			isDeleted = deleteLikeTask.execute(token, entityId).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Consts.GOOD_RESPONSE_LIKE_IS_DELETED == isDeleted){
			return true;
		} else {
			return false;
		}
	}

	private static class GetLikesTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet();
				httpGet.setHeader("Authorization", "Bearer " + params[0]);
				httpGet.setURI(new URI(Consts.MILLICOM_SECONDSCREEN_LIKES_URL));
				HttpResponse response = httpClient.execute(httpGet);
				if (Consts.GOOD_RESPONSE == response.getStatusLine().getStatusCode()) {
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
					Log.d(TAG, "Get My Likes: Invalid token");
					return Consts.ERROR_STRING;
				} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == response.getStatusLine().getStatusCode()) {
					Log.d(TAG, "Get My Likes: Missing token");
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

	private static class DeleteLikeTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_LIKES_URL);
				httpPost.setHeader("Authorization", "Bearer " + params[0]);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_ENTITY_ID), Arrays.asList(params[1]));
				Log.d(TAG, "Remove like holder: " + holder);
				StringEntity entity = new StringEntity(holder.toString());
				httpPost.setEntity(entity);
				HttpResponse response = client.execute(httpPost);
				return response.getStatusLine().getStatusCode();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Consts.BAD_RESPONSE;
		}
	}

	private static class AddLikeTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_LIKES_URL);
				httpPost.setHeader("Authorization", "Bearer " + params[0]);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_ENTITY_ID, Consts.MILLICOM_SECONDSCREEN_API_ENTITY_TYPE),
						Arrays.asList(params[1], params[2]));
				Log.d(TAG, "Add like holder: " + holder);
				StringEntity entity = new StringEntity(holder.toString());
				httpPost.setEntity(entity);
				HttpResponse response = client.execute(httpPost);
				return response.getStatusLine().getStatusCode();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Consts.BAD_RESPONSE;
		}
	}
}
