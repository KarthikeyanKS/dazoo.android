package com.millicom.secondscreen.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.OpenRequest;
import com.facebook.model.GraphUser;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.homepage.HomePageActivity;
import com.millicom.secondscreen.utilities.JSONUtilities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class FacebookLoginActivity extends ActionBarActivity{
	
	private static final String TAG = "FacebookLoginActivity";
	private String				facebookToken	= "", facebookSessionToken = "", dazooToken = "";
	private Activity mActivity;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActivity = this;
		openFacebookSession(this, true, statusCallback);
	}
	
	private static Session openFacebookSession(Activity activity, boolean allowLoginUI, Session.StatusCallback statusCallback) {
		OpenRequest openRequest = new OpenRequest(activity);
		openRequest.setPermissions(Arrays.asList("email"));
		openRequest.setCallback(statusCallback);
		Session session = new Session.Builder(activity).build();
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
			Session.setActiveSession(session);
			session.openForRead(openRequest);
			return session;
		}
		return null;
	}

	Session.StatusCallback	statusCallback	= new Session.StatusCallback() {
		@Override
		public void call(final Session session, SessionState state, Exception exception) {
			if (session.isOpened()) {
				Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							facebookSessionToken = session.getAccessToken();
							if(getDazooToken()){
								// go to start page
								Intent intent = new Intent(FacebookLoginActivity.this, HomePageActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
							}
						}
					}
				}).executeAsync();
			}
		}
	};

	public boolean getDazooToken() {
		if (facebookSessionToken.length() > 0) {
			FacebookLoginTask facebookLoginTask = new FacebookLoginTask();
			try {
				String responseStr = facebookLoginTask.execute(facebookSessionToken).get();
				// if (responseStr != null && responseStr.isEmpty() != true) {
				if (responseStr != null && TextUtils.isEmpty(responseStr) != true) {
					JSONObject fbJSON = new JSONObject(responseStr);
					facebookToken = fbJSON.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);

					// if (facebookToken.isEmpty() != true && facebookToken.length() > 0) {
					if (facebookToken != null && TextUtils.isEmpty(facebookToken) != true) {
						// save access token in the application
						((SecondScreenApplication) getApplicationContext()).setAccessToken(facebookToken);
						Log.d(TAG, "Token: " + facebookToken + " is saved");

						// Get the information about the user
						String userDataString = fbJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER);
						if (storeUserInformation(userDataString)) {
							Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
							return true;
						} else {
							Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend", Toast.LENGTH_SHORT).show();
							return false;
						}
					}
				} else {
					Toast.makeText(getApplicationContext(), "Error! Something went wrong while authorization via Facebook. Please, try again!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Error! Something went wrong while authorization via Facebook. Please, try again!");
					return false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Error! Something went wrong while authorization via Facebook. Please, try again!", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Error! Facebook authorization: level get Token from Facebook");
			return false;
		}
		return false;
	}

	private class FacebookLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_FACEBOOK_TOKEN_URL);
				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_FACEBOOK_TOKEN), Arrays.asList(params[0]));

				StringEntity entity = new StringEntity(holder.toString());

				httpPost.setEntity(entity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				HttpResponse response = client.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					String responseBody = EntityUtils.toString(response.getEntity());
					// JSONObject jObj = new JSONObject(responseBody);
					// String responseToken = jObj.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
					// return responseToken;
					return responseBody;
				} else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) {
					Log.d(TAG, "Invalid Token!");
					return Consts.EMPTY_STRING;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Consts.EMPTY_STRING;
		}
	}

	private boolean storeUserInformation(String jsonString) {
		if (jsonString != null && TextUtils.isEmpty(jsonString) != true) {
			// if (jsonString != null && jsonString.isEmpty() != true) {
			JSONObject userJSON;
			try {
				userJSON = new JSONObject(jsonString);

				String userFirstName = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_FIRSTNAME);
				((SecondScreenApplication) getApplicationContext()).setUserFirstName(userFirstName);
				Log.d(TAG, "First Name: " + userFirstName + " is saved");

				String userLastName = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_LASTNAME);
				((SecondScreenApplication) getApplicationContext()).setUserLastName(userLastName);
				Log.d(TAG, "Last Name: " + userLastName + " is saved");

				String userId = userJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER_ID);
				((SecondScreenApplication) getApplicationContext()).setUserId(userId);
				Log.d(TAG, "User Id: " + userId + " is saved");

				boolean userExistingFlag = userJSON.optBoolean(Consts.MILLICOM_SECONDSCREEN_API_CREATED);
				((SecondScreenApplication) getApplicationContext()).setUserExistringFlag(userExistingFlag);
				Log.d(TAG, "User login first time: " + userExistingFlag);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		} else return false;
	}
	
}
