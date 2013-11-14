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
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.myprofile.SettingsActivity;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.utilities.PatternCheck;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DazooLoginActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG	= "DazooLoginActivity";
	private ActionBar			mActionBar;
	private Button				mDazooLoginButton, mForgetPasswordButton;
	private EditText			mEmailLoginEditText, mPasswordLoginEditText;
	private RelativeLayout		mFacebookContainer;

	private String				dazooToken	= "", userToken = "", userId = "", userEmailLogin, userPasswordLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dazoologin_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		initViews();
	}

	private void initViews() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setTitle(getResources().getString(R.string.login));

		mFacebookContainer = (RelativeLayout) findViewById(R.id.dazoologin_facebook_container);
		mFacebookContainer.setOnClickListener(this);

		mDazooLoginButton = (Button) findViewById(R.id.dazoologin_login_button);
		mDazooLoginButton.setOnClickListener(this);
		mEmailLoginEditText = (EditText) findViewById(R.id.dazoologin_login_email_edittext);
		mPasswordLoginEditText = (EditText) findViewById(R.id.dazoologin_login_password_edittext);

		mForgetPasswordButton = (Button) findViewById(R.id.dazoologin_forgot_password_button);
		mForgetPasswordButton.setOnClickListener(this);
	}

	private boolean verifyLoginInput() {
		String emailInput = mEmailLoginEditText.getText().toString();
		String passwordInput = mPasswordLoginEditText.getText().toString();
		if ((passwordInput != null) && (emailInput != null) && (passwordInput.length() >= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN)
				&& (passwordInput.length() <= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MAX) && (!passwordInput.matches("[%,#/|<>]+")) && (PatternCheck.checkEmail(emailInput) == true)) {
			return true;
		} else return false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.dazoologin_facebook_container:
			Intent intentFacebook = new Intent(DazooLoginActivity.this, FacebookLoginActivity.class);
			startActivity(intentFacebook);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;

		case R.id.dazoologin_forgot_password_button:
			Intent intentReset = new Intent(DazooLoginActivity.this, ResetPasswordActivity.class);
			startActivity(intentReset);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.dazoologin_login_button:
			if (verifyLoginInput()) {
				mEmailLoginEditText.setEnabled(false);
				mPasswordLoginEditText.setEnabled(false);

				userEmailLogin = mEmailLoginEditText.getText().toString();
				userPasswordLogin = mPasswordLoginEditText.getText().toString();
				DazooLoginTask dazooLoginTask = new DazooLoginTask();
				try {
					// dazooToken = dazooLoginTask.execute(userEmailLogin, userPasswordLogin).get();
					String responseStr = dazooLoginTask.execute(userEmailLogin, userPasswordLogin).get();
					// if (responseStr != null && responseStr.isEmpty() != true) {
					if (responseStr != null && TextUtils.isEmpty(responseStr) != true) {
						JSONObject dazooJSON = new JSONObject(responseStr);
						dazooToken = dazooJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);

						// if (dazooToken.isEmpty() != true && dazooToken.length() > 0) {
						if (dazooToken != null && TextUtils.isEmpty(dazooToken) != true) {
							((SecondScreenApplication) getApplicationContext()).setAccessToken(dazooToken);
							Log.d(TAG, "DazooToken: " + dazooToken + "is saved");

							// Get the information about the user
							String userDataString = dazooJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER);
							if (JSONUtilities.storeUserInformation(this, userDataString)) {
								Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();

								DazooStore.getInstance().clearAll();
								DazooStore.getInstance().reinitializeAll();
								DazooCore.resetAll();
								// clear all the running before activities and start the application from the whole beginning
								SecondScreenApplication.getInstance().clearActivityBacktrace();

								startActivity(new Intent(DazooLoginActivity.this, HomeActivity.class));
							} else {
								Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with us. Please, try again later!", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with us. Please, try again later!", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "Error! Dazoo Login: level response from backend");
						mEmailLoginEditText.setEnabled(true);
						mPasswordLoginEditText.setEnabled(true);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), "check if email/password were input right", Toast.LENGTH_LONG).show();
				mEmailLoginEditText.setEnabled(true);
				mPasswordLoginEditText.setEnabled(true);
			}
		}
	}

	private class DazooLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {

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

				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_DAZOO_LOGIN_URL);
				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_EMAIL, Consts.MILLICOM_SECONDSCREEN_API_PASSWORD),
						Arrays.asList(params[0], params[1]));

				StringEntity entity = new StringEntity(holder.toString());

				httpPost.setEntity(entity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				HttpResponse response = httpClient.execute(httpPost);

				// HttpResponse response = client.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					String responseBody = EntityUtils.toString(response.getEntity());
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
