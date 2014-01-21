package com.millicom.secondscreen.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.utilities.JSONUtilities;
//import com.testflightapp.lib.TestFlight;

public class FacebookLoginActivity extends SSActivity {

	private static final String	TAG	= "FacebookLoginActivity";
	private String				facebookToken	= "", facebookSessionToken = "", dazooToken = "";
	private ActionBar			mActionBar;
	private static Activity		mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_facebooklogin_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		mActivity = this;
		initViews();

		// generation of the ssh key for the facebook
		//PackageInfo info;
		//try {
		//	info = getPackageManager().getPackageInfo("com.millicom.secondscreen", PackageManager.GET_SIGNATURES);
		//	for (Signature signature : info.signatures) {
		//		MessageDigest md = MessageDigest.getInstance("SHA");
		//		md.update(signature.toByteArray());
		//		Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		//	}
		//} catch (NameNotFoundException e) {
		//	e.printStackTrace();
		//} catch (NoSuchAlgorithmException e) {
		//	e.printStackTrace();
		//}

		openFacebookSession(this, true, statusCallback);
	}
	
	@Override
	protected void updateUI(REQUEST_STATUS status) {
		/* Have to have this method here since SSActivity has this method abstract */
	}

	@Override
	protected void loadPage() {
		/* Have to have this method here since SSActivity has this method abstract */
	}

	private void initViews() {
		mActionBar = getSupportActionBar();
		mActionBar.hide();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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

			if (state.isOpened()) {

				Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							facebookSessionToken = session.getAccessToken();
							Log.d(TAG, "facebook:" + facebookSessionToken);
							if (getDazooToken()) {

								// for beta-release - direct to the HomePage even if it is first time user
								boolean firstTime = ((SecondScreenApplication) getApplicationContext()).getUserExistringFlag();
								// if (firstTime) {
								// // first time registration/login
								// // go to facebook dazoo login success page
								// Intent intent = new Intent(FacebookLoginActivity.this, FacebookDazooLoginActivity.class);
								// startActivity(intent);
								// //finish();
								// } else {
								// returning client
								// go to start page
								Intent intent = new Intent(FacebookLoginActivity.this, HomeActivity.class);
								startActivity(intent);
								finish();
								// }
							}
						} else {
							Log.d(TAG, "user has not agreeed");
						}
					}
				}).executeAsync();
			} else if (state.isClosed()) {
				Intent intent = new Intent(FacebookLoginActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}
	};

	public boolean getDazooToken() {
		if (facebookSessionToken.length() > 0) {
			FacebookLoginTask facebookLoginTask = new FacebookLoginTask();
			try {
				String responseStr = facebookLoginTask.execute(facebookSessionToken).get();
				if (responseStr != null && TextUtils.isEmpty(responseStr) != true) {
					JSONObject fbJSON = new JSONObject(responseStr);
					facebookToken = fbJSON.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
					if (facebookToken != null && TextUtils.isEmpty(facebookToken) != true) {
						// save access token in the application
						((SecondScreenApplication) getApplicationContext()).setAccessToken(facebookToken);
						Log.d(TAG, "Token: " + facebookToken + " is saved");

						if (AuthenticationService.storeUserInformation(this, fbJSON)) {
							return true;
						} else {
							return false;
						}
					}
				} else {
					// Toast.makeText(getApplicationContext(), "Error! Something went wrong while authorization via Facebook. Please, try again!", Toast.LENGTH_SHORT).show();
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
			// Toast.makeText(getApplicationContext(), "Error! Something went wrong while authorization via Facebook. Please, try again!", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Error! Facebook authorization: level get Token from Facebook");
			return false;
		}
		return false;
	}

	private class FacebookLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
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

				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_FACEBOOK_TOKEN_URL);
				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_FACEBOOK_TOKEN), Arrays.asList(params[0]));

				StringEntity entity = new StringEntity(holder.toString());

				httpPost.setEntity(entity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				// HttpResponse response = client.execute(httpPost);
				HttpResponse response = httpClient.execute(httpPost);
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
}
