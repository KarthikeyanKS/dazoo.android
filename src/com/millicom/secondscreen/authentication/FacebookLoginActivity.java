package com.millicom.secondscreen.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.OpenRequest;
import com.facebook.model.GraphUser;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.utilities.JSONUtilities;
//import com.testflightapp.lib.TestFlight;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class FacebookLoginActivity extends ActionBarActivity {

	private static final String	TAG	= "FacebookLoginActivity";
	private String				facebookToken	= "", facebookSessionToken = "", dazooToken = "";
	private ActionBar			mActionBar;
	private static Activity mActivity;

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
		//	info = getPackageManager().getPackageInfo("com.millicom.secondscreen",  PackageManager.GET_SIGNATURES);
		//	for (Signature signature : info.signatures)
		//   {
		//       MessageDigest md = MessageDigest.getInstance("SHA");
		//        md.update(signature.toByteArray());
		//        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		//    }	
		//} catch (NameNotFoundException e) {
		//	e.printStackTrace();
		//} catch (NoSuchAlgorithmException e) {
		//	e.printStackTrace();
		//}

		openFacebookSession(this, true, statusCallback);
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
			
			//Toast.makeText(mActivity, "Established session with facebook", Toast.LENGTH_SHORT).show();
			
			Session.setActiveSession(session);
			session.openForRead(openRequest);
			
			//Toast.makeText(mActivity, "Facebook session state: " + session.getState(), Toast.LENGTH_SHORT).show();
			//Log.d(TAG,"SESSION;" + session.getState());
			return session;
		}
		return null;
	}

	Session.StatusCallback	statusCallback	= new Session.StatusCallback() {
		@Override
		public void call(final Session session, SessionState state, Exception exception) {
			
			//Toast.makeText(mActivity, "FB session call ", Toast.LENGTH_SHORT).show();
			
			if (state.isOpened()) {
				
				//Toast.makeText(mActivity, "FB session is OPENED", Toast.LENGTH_SHORT).show();
				
				Request.newMeRequest(session, new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						//Toast.makeText(mActivity, "FB requests a user", Toast.LENGTH_SHORT).show();
						
						if (user != null) {
							
							//Toast.makeText(mActivity, "User is not null!", Toast.LENGTH_SHORT).show();
							
							facebookSessionToken = session.getAccessToken();
							
							//Toast.makeText(mActivity, "got facebook token: " + facebookSessionToken, Toast.LENGTH_SHORT).show();
							
							//Log.d(TAG,"facebook:" + facebookSessionToken );
							if (getDazooToken()) {
								Log.d(TAG,"DAZOO TOKEN");
								boolean firstTime = ((SecondScreenApplication) getApplicationContext()).getUserExistringFlag();
								if (firstTime) {
									// first time registration/login
									// go to facebook dazoo login success page
									Intent intent = new Intent(FacebookLoginActivity.this, FacebookDazooLoginActivity.class);
									startActivity(intent);
									overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
									//finish();
								} else {
									// returning client
									// go to start page
									Intent intent = new Intent(FacebookLoginActivity.this, HomeActivity.class);
									startActivity(intent);
									overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
									finish();
								}
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
				//Log.d(TAG,"BACKEND EXECUTION");
				//Toast.makeText(mActivity, "Request to Dazoo backend", Toast.LENGTH_SHORT).show();
				
				String responseStr = facebookLoginTask.execute(facebookSessionToken).get();
				//Log.d(TAG,"BACKEND EXECUTION: DONE not known");
				//Toast.makeText(mActivity, "Request to Dazoo backend: executed", Toast.LENGTH_SHORT).show();
				
				if (responseStr != null && TextUtils.isEmpty(responseStr) != true) {
					//Log.d(TAG,"BACKEND EXECUTION: DONE");
					//Toast.makeText(mActivity, "Request to Dazoo backend: execution DONE", Toast.LENGTH_SHORT).show();
					
					JSONObject fbJSON = new JSONObject(responseStr);
					facebookToken = fbJSON.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
					
					//Toast.makeText(mActivity, "DAZOO TOKEN: " + facebookToken, Toast.LENGTH_SHORT).show();
					
					if (facebookToken != null && TextUtils.isEmpty(facebookToken) != true) {
						// save access token in the application
						((SecondScreenApplication) getApplicationContext()).setAccessToken(facebookToken);
						//Log.d(TAG, "Token: " + facebookToken + " is saved");
						
						if (AuthenticationService.storeUserInformation(this, fbJSON)) {
							//Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
							return true;
						} else {
							//Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend", Toast.LENGTH_SHORT).show();
							return false;
						}
					}
				} else {
					//Toast.makeText(getApplicationContext(), "Error! Something went wrong while authorization via Facebook. Please, try again!", Toast.LENGTH_SHORT).show();
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
			//Toast.makeText(getApplicationContext(), "Error! Something went wrong while authorization via Facebook. Please, try again!", Toast.LENGTH_SHORT).show();
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
