package com.millicom.secondscreen.like;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
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
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.content.model.DazooLikeEntity;
import com.millicom.secondscreen.manager.ContentParser;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.SecondScreenApplication;

public class LikeService {

	private static final String	TAG	= "LikeService";

	public static String getLikeType(String programType) {
		if (programType.equals(Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE)) {
			return Consts.DAZOO_LIKE_TYPE_SERIES;
		} else if (programType.equals(Consts.DAZOO_PROGRAM_TYPE_SPORT)) {
			// TODO: add the support for the sport types later when backend is available
			return Consts.DAZOO_LIKE_TYPE_PROGRAM;
			//return Consts.DAZOO_LIKE_TYPE_SPORT_TYPE;
		} else {
			return Consts.DAZOO_LIKE_TYPE_PROGRAM;
		}
	}

	public static boolean isLiked(String token, String programId) {
		ArrayList<DazooLike> likesList = new ArrayList<DazooLike>();
		likesList = LikeService.getLikesList(token);
		ArrayList<String> likeEntityIds = new ArrayList<String>();
		for (int i = 0; i < likesList.size(); i++) {
			String likeType = likesList.get(i).getLikeType();
			if(Consts.DAZOO_LIKE_TYPE_SERIES.equals(likeType)){
				likeEntityIds.add(likesList.get(i).getEntity().getSeriesId());
			} else if (Consts.DAZOO_LIKE_TYPE_PROGRAM.equalsIgnoreCase(likeType)){
				likeEntityIds.add(likesList.get(i).getEntity().getProgramId());
			} 
			// add later support for sport types
			// else if()
		}

		if (likeEntityIds.contains(programId)) return true;
		else return false;
	}

	public static void showSetLikeToast(Activity activity, String likedContentName) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_like_set, (ViewGroup) activity.findViewById(R.id.like_set_toast_container));

		final Toast toast = new Toast(activity.getApplicationContext());

		TextView text = (TextView) layout.findViewById(R.id.like_set_toast_tv);
		text.setText(likedContentName + activity.getResources().getString(R.string.like_set_text));

		toast.setGravity(Gravity.BOTTOM, 0, 200);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static ArrayList<DazooLike> getLikesList(String token) {
		ArrayList<DazooLike> dazooLikesList = new ArrayList<DazooLike>();
		GetLikesTask getLikesTask = new GetLikesTask();
		String jsonString = "";
		try {
			jsonString = getLikesTask.execute(token).get();
			if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) {
				JSONArray likesListJson = new JSONArray(jsonString);
				int size = likesListJson.length();
				for (int i = 0; i < size; i++) {
					dazooLikesList.add(ContentParser.parseDazooLike(likesListJson.getJSONObject(i)));
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dazooLikesList;
	}

	public static boolean addLike(String token, String entityId, String likeType) {

		AddLikeTask addLikeTask = new AddLikeTask();
		int result = 0;
		try {
			result = addLikeTask.execute(token, entityId, likeType).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if (Consts.GOOD_RESPONSE == result) {
			return true;
		} else if (Consts.BAD_RESPONSE_PROGRAM_SERIES_NOT_FOUND == result){
			Log.d(TAG,"Program/Series not found");
			return false;
		} else if (Consts.BAD_RESPONSE_MISSING_TOKEN == result){
			Log.d(TAG, "Missing token");
			return false;
		} else if (Consts.BAD_RESPONSE_INVALID_TOKEN == result){
			Log.d(TAG,"Invalid token");
			return false;
		}
		else {
			return false;
		}
	}

	public static boolean removeLike(String token, String entityId, String likeType) {
		DeleteLikeTask deleteLikeTask = new DeleteLikeTask();
		int isDeleted = 0;
		try {
			isDeleted = deleteLikeTask.execute(token, entityId, likeType).get();
			Log.d(TAG, "delete code: " + isDeleted);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if (Consts.GOOD_RESPONSE_LIKE_IS_DELETED == isDeleted) {
			return true;
		} else {
			return false;
		}
	}

	private static class GetLikesTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				// HttpClient httpClient = new DefaultHttpClient();
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
				// HttpClient client = new DefaultHttpClient();
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

				Log.d(TAG,Consts.MILLICOM_SECONDSCREEN_LIKES_URL + "/" + params[2] + "/" + params[1]);
				HttpDelete httpDelete = new HttpDelete(Consts.MILLICOM_SECONDSCREEN_LIKES_URL + "/" + params[2] + "/" + params[1]);
				httpDelete.setHeader("Authorization", "Bearer " + params[0]);

				// HttpResponse response = client.execute(httpDelete);
				HttpResponse response = httpClient.execute(httpDelete);

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

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_LIKETYPE, Consts.MILLICOM_SECONDSCREEN_API_ENTITY_ID),
						Arrays.asList(params[2], params[1]));
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
