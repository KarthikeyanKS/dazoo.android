package com.millicom.secondscreen.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.*;
import com.facebook.Session.StatusCallback;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.PatternCheck;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

public class LoginActivity extends Activity implements OnClickListener {

	private static final String	TAG	= "LoginActivity";

	private Button				mFacebookLoginButton, mDazooLoginButton;
	private String				facebookToken	= "", facebookSessionToken = "", dazooToken = "", userToken = "", userId = "", userEmail, userPassword;
	private EditText			mEmailEditText, mPasswordEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_login_activity);

		mFacebookLoginButton = (Button) findViewById(R.id.login_activity_facebook_login_button);
		mFacebookLoginButton.setOnClickListener(this);
		mDazooLoginButton = (Button) findViewById(R.id.login_activity_dazoo_login_button);
		mDazooLoginButton.setOnClickListener(this);
		mEmailEditText = (EditText) findViewById(R.id.login_activity_dazoo_email_edittext);
		mPasswordEditText = (EditText) findViewById(R.id.login_activity_dazoo_password_edittext);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	private boolean verifyUserInput() {
		String emailInput = mEmailEditText.getText().toString();
		String passwordInput = mPasswordEditText.getText().toString();
		if ((passwordInput != null) && (emailInput != null) && (passwordInput.length() >= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN)
				&& (passwordInput.length() <= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MAX) && (!passwordInput.matches("[%,#/|<>]+")) && (PatternCheck.checkEmail(emailInput) == true)) {
			return true;
		} else return false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.login_activity_facebook_login_button:
			// start Facebook Login
			Session.openActiveSession(this, true, new Session.StatusCallback() {

				// callback when session changes state
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					Log.d(TAG, "Session state: " + session.isOpened() + "   " + session.getState());
					if (session.isOpened() && session != null) {
						facebookSessionToken = session.getAccessToken();
						Log.d(TAG, "FacebookSessionToken:" + facebookSessionToken);
					}
				}
			});

			FacebookLoginTask facebookLoginTask = new FacebookLoginTask();
			try {
				facebookToken = facebookLoginTask.execute(facebookSessionToken).get();
				Log.d(TAG,"FacebookTokenFrånBackend: " + facebookToken);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			break;
		case R.id.login_activity_dazoo_login_button:
			boolean isSuccess = verifyUserInput();
			if (isSuccess == true) {
				mEmailEditText.setEnabled(false);
				mPasswordEditText.setEnabled(false);
				DazooLoginTask dazooLoginTask = new DazooLoginTask();
				dazooLoginTask.execute(userEmail, userPassword);
			} else {
				Toast.makeText(getApplicationContext(), "check if email/password were input right", Toast.LENGTH_LONG).show();
				mEmailEditText.setEnabled(true);
				mPasswordEditText.setEnabled(true);
			}
			break;
		}
	}

	private class GetUserIdTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet();

				httpGet.setURI(new URI(params[1]));
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					HttpEntity entityHttp = response.getEntity();
					InputStream inputStream = entityHttp.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					inputStream.close();
					JSONObject jObj = new JSONObject(sb.toString());
					Log.d(TAG, "JSONresponse: " + jObj);
					userToken = jObj.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
					userId = jObj.getString(Consts.MILLICOM_SECONDSCREEN_API_USER_ID);
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return true;
		}

	}

	private class DazooLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

				HttpClient client = new DefaultHttpClient();

				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("http", socketFactory, 443));
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

				HttpResponse response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					JSONObject jsonResponse = new JSONObject(response.getEntity().toString());
					String token = jsonResponse.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
					return token;
				} else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) {
					Toast.makeText(getApplicationContext(), "This combination of password and email does not exist at ours", Toast.LENGTH_LONG).show();
					return "";
				}
			} catch (ClientProtocolException e) {
				System.out.println("CPE" + e);
			} catch (IOException e) {
				System.out.println("IOE" + e);
			} catch (JSONException e) {
				System.out.println("JSONE" + e);
			}
			return "";
		}

	}

	private class FacebookLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

				HttpClient client = new DefaultHttpClient();

				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("http", socketFactory, 443));
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

				HttpResponse response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					JSONObject jObj = new JSONObject(response.getEntity().toString());
					String responseToken = jObj.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
					return responseToken;
				} else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) {
					Toast.makeText(getApplicationContext(), "Invalid Token", Toast.LENGTH_LONG).show();
					return "";
				}
			} catch (ClientProtocolException e) {
				System.out.println("CPE" + e);
			} catch (IOException e) {
				System.out.println("IOE" + e);
			} catch (JSONException e) {
				System.out.println("JSONE" + e);
			}
			return "";
		}
	}
}
