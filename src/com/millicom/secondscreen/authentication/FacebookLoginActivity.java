package com.millicom.secondscreen.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.*;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;

import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

public class FacebookLoginActivity extends FragmentActivity implements OnClickListener {

	private static final String		TAG				= "FacebookLoginActivity";

	private Button					mFacebookLoginButton;
	private String facebookToken = "";

	private static final int		SPLASH			= 0;
	private static final int		SELECTION		= 1;
	private static final int		FRAGMENT_COUNT	= SELECTION + 1;
	private Fragment[]				fragments		= new Fragment[FRAGMENT_COUNT];

	// flag to indicate a visible activity. It is used to enable session state change checks
	private boolean					isResumed		= false;

	// use the UiLifecycleHelper to track the session and trigger a session state change listener
	private UiLifecycleHelper		uiHelper;

	private Session.StatusCallback	callback		= new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_login_activity);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		mFacebookLoginButton = (Button) findViewById(R.id.login_activity_facebook_login_button);
		mFacebookLoginButton.setOnClickListener(this);

		// FragmentManager fm = getSupportFragmentManager();
		// fragments[SPLASH] = fm.findFragmentById(R.id.layout_facebook_login_splash_fragment);
		// fragments[SELECTION] = fm.findFragmentById(R.id.layout_facebook_login_selection_fragment);

		// FragmentTransaction transaction = fm.beginTransaction();
		// for(int i = 0; i < fragments.length; i++) {
		// transaction.hide(fragments[i]);
		// }
		// transaction.commit();
	}

	// method to show the relevant fragment based on the person's authenticated state.
	// Only handle UI changes when activity is visible by making use of the isResumed flag.
	// The fragment back stack is first cleared before the showFragment() method is called with the appropriate fragment info
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		// // Only make changes if the activity is visible
		// if (isResumed) {
		// FragmentManager manager = getSupportFragmentManager();
		// // Get the number of entries in the back stack
		// int backStackSize = manager.getBackStackEntryCount();
		// // Clear the back stack
		// for (int i = 0; i < backStackSize; i++) {
		// manager.popBackStack();
		// }
		// if (state.isOpened()) {
		// // If the session state is open:
		// // Show the authenticated fragment
		// showFragment(SELECTION, false);
		// } else if (state.isClosed()) {
		// // If the session state is closed:
		// // Show the login fragment
		// showFragment(SPLASH, false);
		// }
		// }
	}

	// handle the case where fragments are newly instantiated and the authenticated versus nonauthenticated UI needs to be properly set.
	// onResumeFragments() method to handle the session changes
	// @Override
	// protected void onResumeFragments() {
	// super.onResumeFragments();
	// Session session = Session.getActiveSession();
	//
	// if (session != null && session.isOpened()) {
	// // if the session is already open,
	// // try to show the selection fragment
	// showFragment(SELECTION, false);
	// } else {
	// // otherwise present the splash screen
	// // and ask the person to login.
	// showFragment(SPLASH, false);
	// }
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		final Session session = Session.getActiveSession();
		// If the session is open, make an API call to get user data and define new callback to handle the response
		if (session != null && session.isOpened()) {
			Log.d(TAG, "sessionToken: " + session.getAccessToken());
			Log.d(TAG, "sessionTokenDueDate: " + session.getExpirationDate().toLocaleString());
		}

		super.onResume();
		// uiHelper.onResume();
		// isResumed = true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.login_activity_facebook_login_button:
			FacebookLoginTask facebookLoginTask = new FacebookLoginTask();
			boolean isSuccess = false;
			try {
				isSuccess = facebookLoginTask.execute(facebookToken).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG,"FACEBOOK SUCCESS");
			break;
		}
	}
	
	private class DazooLoginTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

				HttpClient client = new DefaultHttpClient();

				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("https", socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

				// Set verifier
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_DAZOO_LOGIN_URL);
				httpPost.setHeader("Content-type", "application/json; charset=utf-8");

				String email = params[0];
				String password = params[1];
				
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
				nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_EMAIL, email));
				nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_PASSWORD, password));

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

				// execute http post request
				HttpResponse response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					return true;
				}
				else if(response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE){
					Toast.makeText(getApplicationContext(), "This combination of password and email does not exist at ours", Toast.LENGTH_LONG).show();
				}
			} catch (ClientProtocolException e) {
				System.out.println("CPE" + e);
			} catch (IOException e) {
				System.out.println("IOE" + e);
			}
			return false;
		}
		
	}

	private class FacebookLoginTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

				HttpClient client = new DefaultHttpClient();

				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("https", socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

				// Set verifier
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_FACEBOOK_TOKEN_URL);
				httpPost.setHeader("Content-type", "application/json; charset=utf-8");

				String token = params[0];
				
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
				nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_FACEBOOK_TOKEN, token));

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

				// execute http post request
				HttpResponse response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					return true;
				}
				else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE){
					Toast.makeText(getApplicationContext(), "Invalid Token", Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (ClientProtocolException e) {
				System.out.println("CPE" + e);
			} catch (IOException e) {
				System.out.println("IOE" + e);
			}
			return false;
		}
	}
	
	
	// @Override
	// public void onPause() {
	// super.onPause();
	// uiHelper.onPause();
	// isResumed = false;
	// }

	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// uiHelper.onDestroy();
	// }

	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// uiHelper.onSaveInstanceState(outState);
	// }

	// private void showFragment(int fragmentIndex, boolean addToBackStack) {
	// FragmentManager fm = getSupportFragmentManager();
	// FragmentTransaction transaction = fm.beginTransaction();
	// for (int i = 0; i < fragments.length; i++) {
	// if (i == fragmentIndex) {
	// transaction.show(fragments[i]);
	// } else {
	// transaction.hide(fragments[i]);
	// }
	// }
	// if (addToBackStack) {
	// transaction.addToBackStack(null);
	// }
	// transaction.commit();
	// }
}
