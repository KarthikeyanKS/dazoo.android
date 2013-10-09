package com.millicom.secondscreen.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.facebook.*;
import com.facebook.Session.OpenRequest;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.utilities.PatternCheck;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;

import com.millicom.secondscreen.SecondScreenApplication;

public class LoginActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG	= "LoginActivity";

	private Button				mFacebookLoginButton, mDazooLoginButton, mDazooLogoutButton, mFacebookLogoutButton, mDazooRegisterButton;
	private String				facebookToken	= "", facebookSessionToken = "", dazooToken = "", userToken = "", userId = "", userEmailLogin, userPasswordLogin, userEmailRegister,
			userPasswordRegister, userFirstNameRegister, userLastNameRegister;
	private EditText			mEmailLoginEditText, mPasswordLoginEditText, mFirstNameEditText, mLastNameEditText, mPasswordRegisterEditText, mPasswordRegisterVerifyEditText, mEmailRegisterEditText;
	private ActionBar			mActionBar;
	private Context				context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_login_activity);

		mFacebookLoginButton = (Button) findViewById(R.id.login_activity_facebook_login_button);
		mFacebookLoginButton.setOnClickListener(this);
		mFacebookLogoutButton = (Button) findViewById(R.id.login_activity_facebook_logout_button);
		mFacebookLogoutButton.setOnClickListener(this);

		mDazooLoginButton = (Button) findViewById(R.id.login_activity_dazoo_login_login_button);
		mDazooLoginButton.setOnClickListener(this);
		mEmailLoginEditText = (EditText) findViewById(R.id.login_activity_dazoo_login_email_edittext);
		mPasswordLoginEditText = (EditText) findViewById(R.id.login_activity_dazoo_login_password_edittext);

		mDazooLogoutButton = (Button) findViewById(R.id.login_activity_dazoo_login_logout_button);
		mDazooLogoutButton.setOnClickListener(this);

		mFirstNameEditText = (EditText) findViewById(R.id.login_activity_dazoo_register_firstname_edittext);
		mLastNameEditText = (EditText) findViewById(R.id.login_activity_dazoo_register_lastname_edittext);
		mEmailRegisterEditText = (EditText) findViewById(R.id.login_activity_dazoo_register_email_edittext);
		mPasswordRegisterEditText = (EditText) findViewById(R.id.login_activity_dazoo_register_password_edittext);
		mPasswordRegisterVerifyEditText = (EditText) findViewById(R.id.login_activity_dazoo_register_password_verify_edittext);
		mDazooRegisterButton = (Button) findViewById(R.id.login_activity_dazoo_login_register_button);
		mDazooRegisterButton.setOnClickListener(this);

		mActionBar = getSupportActionBar();
		SpannableString s = new SpannableString(getResources().getString(R.string.login));
		// s.setSpan(new TypefaceSpan(this, "AvenirBlack"),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.layout_actionbar_mepage);

		final TextView title = (TextView) findViewById(R.id.actionbar_mepage_title_tv);
		title.setText(s);

		final ImageView searchButton = (ImageView) findViewById(R.id.actionbar_mepage_search_icon);
		searchButton.setVisibility(View.GONE);

		context = getBaseContext();

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private boolean verifyLoginInput() {
		String emailInput = mEmailLoginEditText.getText().toString();
		String passwordInput = mPasswordLoginEditText.getText().toString();
		if ((passwordInput != null) && (emailInput != null) && (passwordInput.length() >= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN)
				&& (passwordInput.length() <= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MAX) && (!passwordInput.matches("[%,#/|<>]+")) && (PatternCheck.checkEmail(emailInput) == true)) {
			return true;
		} else return false;
	}

	private boolean verifyRegisterInput() {
		String emailInput = mEmailRegisterEditText.getText().toString();
		String firstNameInput = mFirstNameEditText.getText().toString();
		String lastNameInput = mLastNameEditText.getText().toString();
		String passwordInput = mPasswordRegisterEditText.getText().toString();
		String passwordVerifyInput = mPasswordRegisterVerifyEditText.getText().toString();

		if ((firstNameInput != null) && (lastNameInput != null) && (passwordInput != null) && (emailInput != null) && (passwordInput.length() >= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN)
				&& (passwordInput.length() <= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MAX) && (!passwordInput.matches("[%,#/|<>]+")) && (PatternCheck.checkEmail(emailInput) == true)
				&& (passwordInput.equals(passwordVerifyInput))) {
			return true;
		} else return false;
	}

	private boolean storeUserInformation(String jsonString) {
		if (jsonString != null && jsonString.isEmpty() != true) {
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

	public void getDazooToken() {
		if (facebookSessionToken.length() > 0) {
			FacebookLoginTask facebookLoginTask = new FacebookLoginTask();
			try {
				String responseStr = facebookLoginTask.execute(facebookSessionToken).get();
				if (responseStr != null && responseStr.isEmpty() != true) {
					JSONObject fbJSON = new JSONObject(responseStr);
					facebookToken = fbJSON.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);

					if (facebookToken.isEmpty() != true && facebookToken.length() > 0) {
						// save access token in the application
						((SecondScreenApplication) getApplicationContext()).setAccessToken(facebookToken);
						Log.d(TAG, "Token: " + facebookToken + " is saved");

						// Get the information about the user
						String userDataString = fbJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER);
						if (storeUserInformation(userDataString)) {
							Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(getApplicationContext(), "Error! Something went wrong while authorization via Facebook. Please, try again!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Error! Something went wrong while authorization via Facebook. Please, try again!");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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

						// OpenRequest openRequest = new Session.OpenRequest((Activity) context);
						// openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
						// openRequest.setCallback(null);

						// List<String> permissions = new ArrayList<String>();
						// permissions.add("email");

						// openRequest.setPermissions(permissions);

						facebookSessionToken = session.getAccessToken();
						// Toast.makeText(getApplicationContext(), "FacebookSessionToken:" + facebookSessionToken, Toast.LENGTH_LONG).show();
						Log.d(TAG, "FacebookSessionToken:" + facebookSessionToken);
						getDazooToken();
					} else {
						// Toast.makeText(getApplicationContext(), "Public Facebook profile is not available!", Toast.LENGTH_LONG).show();
						Log.d(TAG, "!!!!!===Public Facebook profile is not available!===!!!!");
					}
				}
			});

			break;
		case R.id.login_activity_dazoo_login_login_button:
			if (verifyLoginInput()) {
				mEmailLoginEditText.setEnabled(false);
				mPasswordLoginEditText.setEnabled(false);

				userEmailLogin = mEmailLoginEditText.getText().toString();
				userPasswordLogin = mPasswordLoginEditText.getText().toString();
				DazooLoginTask dazooLoginTask = new DazooLoginTask();
				try {
					// dazooToken = dazooLoginTask.execute(userEmailLogin, userPasswordLogin).get();
					String responseStr = dazooLoginTask.execute(userEmailLogin, userPasswordLogin).get();
					if (responseStr != null && responseStr.isEmpty() != true) {
						JSONObject dazooJSON = new JSONObject(responseStr);
						dazooToken = dazooJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);

						if (dazooToken.isEmpty() != true && dazooToken.length() > 0) {
							((SecondScreenApplication) getApplicationContext()).setAccessToken(dazooToken);
							Log.d(TAG, "DazooToken: " + dazooToken + "is saved");

							// Get the information about the user
							String userDataString = dazooJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER);
							if (storeUserInformation(userDataString)) {
								Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with us. Please, try again later!", Toast.LENGTH_SHORT).show();
						}
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
			break;

		case R.id.login_activity_dazoo_login_logout_button:
			((SecondScreenApplication) getApplicationContext()).setAccessToken("");
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			// clear the activity stack
			finish();

			// check if the token was really cleared
			String dazooToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
			if (dazooToken.isEmpty() == true) {
				Toast.makeText(getApplicationContext(), "Logged out from Dazoo", Toast.LENGTH_SHORT).show();
			} else {
				Log.d(TAG, "Log out from Dazoo failed");
			}

			break;
		case R.id.login_activity_facebook_logout_button:
			((SecondScreenApplication) getApplicationContext()).setAccessToken("");
			Session session = Session.getActiveSession();
			if (session != null) {
				session.closeAndClearTokenInformation();
			}
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			// clear the activity stack
			finish();

			// check if the token was really cleared
			String facebookToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
			if (facebookToken.isEmpty() == true) {
				Toast.makeText(getApplicationContext(), "Logged out from Facebook", Toast.LENGTH_SHORT).show();
			} else {
				Log.d(TAG, "Log out from Facebook failed");
			}

			break;

		case R.id.login_activity_dazoo_login_register_button:
			if (verifyRegisterInput()) {
				mFirstNameEditText.setEnabled(false);
				mLastNameEditText.setEnabled(false);
				mEmailRegisterEditText.setEnabled(false);
				mPasswordRegisterEditText.setEnabled(false);
				mPasswordRegisterVerifyEditText.setEnabled(false);

				userEmailRegister = mEmailRegisterEditText.getText().toString();
				userPasswordRegister = mPasswordRegisterEditText.getText().toString();
				userFirstNameRegister = mFirstNameEditText.getText().toString();
				userLastNameRegister = mLastNameEditText.getText().toString();

				DazooRegistrationTask dazooRegisterTask = new DazooRegistrationTask();
				try {
					// dazooToken = dazooRegisterTask.execute(userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister).get();
					String responseStr = dazooRegisterTask.execute(userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister).get();
					if (responseStr != null && responseStr.isEmpty() != true) {
						JSONObject dazooRegJSON = new JSONObject(responseStr);
						dazooToken = dazooRegJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
						Log.d(TAG, "DazooToken: " + dazooToken + "is saved");

						if (dazooToken.isEmpty() != true && dazooToken.length() > 0) {
							((SecondScreenApplication) getApplicationContext()).setAccessToken(dazooToken);
							// Get the information about the user
							String userDataString = dazooRegJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER);
							if (storeUserInformation(userDataString)) {
								Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with Dazoo. Please, try again later!", Toast.LENGTH_LONG).show();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), "check if email/password/name were input right", Toast.LENGTH_LONG).show();
				mFirstNameEditText.setEnabled(true);
				mLastNameEditText.setEnabled(true);
				mEmailRegisterEditText.setEnabled(true);
				mPasswordRegisterEditText.setEnabled(true);
				mPasswordRegisterVerifyEditText.setEnabled(true);
			}
			break;
		}
	}

	private class DazooLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_FACEBOOK_TOKEN_URL);

				String email = params[0];
				String password = params[1];

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
				nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_EMAIL, email));
				nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_PASSWORD, password));

				ArrayList<String> keys = new ArrayList<String>();
				keys.add(Consts.MILLICOM_SECONDSCREEN_API_EMAIL);
				keys.add(Consts.MILLICOM_SECONDSCREEN_API_PASSWORD);

				ArrayList<String> values = new ArrayList<String>();
				values.add(email);
				values.add(password);

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(keys, values);
				Log.d(TAG, "JSON Holder:" + holder.toString());

				StringEntity entity = new StringEntity(holder.toString());

				httpPost.setEntity(entity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				HttpResponse response = client.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					String responseBody = EntityUtils.toString(response.getEntity());
					return responseBody;
				} else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) {
					Log.d(TAG, "Invalid Token!");
					return "";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}
	}

	/*
	 * private class DazooLoginTask extends AsyncTask<String, Void, String> {
	 * 
	 * @Override protected String doInBackground(String... params) { try { HttpClient client = new DefaultHttpClient();
	 * 
	 * // TODO: UNCOMMENT WHEN BACKEND MAKE SECURE REQUESTS WITH HTTPS
	 * 
	 * // HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER; // SchemeRegistry registry = new SchemeRegistry(); // SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory(); // socketFactory.setHostnameVerifier((X509HostnameVerifier)
	 * hostnameVerifier); // registry.register(new Scheme("http", socketFactory, 443)); // SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); // DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
	 * 
	 * // // Set verifier // HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	 * 
	 * HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_DAZOO_LOGIN_URL); // httpPost.setHeader("Content-type", "application/json; charset=utf-8");
	 * 
	 * String email = params[0]; String password = params[1];
	 * 
	 * List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2); nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_EMAIL, email)); nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_PASSWORD, password));
	 * 
	 * httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
	 * 
	 * // HttpResponse response = httpClient.execute(httpPost); HttpResponse response = client.execute(httpPost); // <-- REMOVE WHEN HTTPS
	 * 
	 * if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) { String responseBody = EntityUtils.toString(response.getEntity()); JSONObject jsonResponse = new JSONObject(responseBody); String token = jsonResponse.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN); return token; } else
	 * if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) { // Toast.makeText(getApplicationContext(), "This combination of password and email does not exist at ours", Toast.LENGTH_LONG).show(); Log.d(TAG, "Invalid token"); return ""; } } catch (ClientProtocolException e) {
	 * System.out.println("CPE" + e); } catch (IOException e) { System.out.println("IOE" + e); } catch (JSONException e) { System.out.println("JSONE" + e); } return ""; } }
	 */

	private class DazooRegistrationTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_DAZOO_REGISTER_URL);

				String email = params[0];
				String password = params[1];
				String firstName = params[2];
				String lastName = params[3];

				ArrayList<String> keys = new ArrayList<String>();
				keys.add(Consts.MILLICOM_SECONDSCREEN_API_EMAIL);
				keys.add(Consts.MILLICOM_SECONDSCREEN_API_PASSWORD);
				keys.add(Consts.MILLICOM_SECONDSCREEN_API_FIRSTNAME);
				keys.add(Consts.MILLICOM_SECONDSCREEN_API_LASTNAME);

				ArrayList<String> values = new ArrayList<String>();
				values.add(email);
				values.add(password);
				values.add(firstName);
				values.add(lastName);

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(keys, values);
				Log.d(TAG, "JSON Holder:" + holder.toString());

				StringEntity entity = new StringEntity(holder.toString());

				httpPost.setEntity(entity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				HttpResponse response = client.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					String responseBody = EntityUtils.toString(response.getEntity());
					return responseBody;
				} else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) {
					Log.d(TAG, "Invalid Token!");
					return "";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}
	}

	/*
	 * private class DazooRegistrationTask extends AsyncTask<String, Void, String> {
	 * 
	 * @Override protected String doInBackground(String... params) { try {
	 * 
	 * HttpClient client = new DefaultHttpClient();
	 * 
	 * // TODO: UNCOMMENT WHEN BACKEND MAKE SECURE REQUESTS WITH HTTPS
	 * 
	 * // HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER; // SchemeRegistry registry = new SchemeRegistry(); // SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory(); // socketFactory.setHostnameVerifier((X509HostnameVerifier)
	 * hostnameVerifier); // registry.register(new Scheme("http", socketFactory, 443)); // SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); // DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
	 * 
	 * // // Set verifier // HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	 * 
	 * HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_DAZOO_REGISTER_URL); httpPost.setHeader("Accept", "application/json");
	 * 
	 * String email = params[0]; String password = params[1]; String firstName = params[2]; String lastName = params[3];
	 * 
	 * List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4); nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_EMAIL, email)); nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_PASSWORD, password)); nameValuePair.add(new
	 * BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_FIRSTNAME, firstName)); nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_LASTNAME, lastName));
	 * 
	 * httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
	 * 
	 * // HttpResponse response = httpClient.execute(httpPost); HttpResponse response = client.execute(httpPost); // <-- REMOVE WHEN HTTPS
	 * 
	 * if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) { String responseBody = EntityUtils.toString(response.getEntity()); JSONObject jObj = new JSONObject(responseBody); String responseToken = jObj.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN); return responseToken; } else
	 * if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) { Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_LONG).show(); return ""; } } catch (ClientProtocolException e) { System.out.println("CPE" + e); } catch (IOException e) { System.out.println("IOE"
	 * + e); } catch (JSONException e) { System.out.println("JSONE" + e); } return ""; } }
	 */

	private class FacebookLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_FACEBOOK_TOKEN_URL);

				ArrayList<String> keys = new ArrayList<String>();
				keys.add(Consts.MILLICOM_SECONDSCREEN_API_FACEBOOK_TOKEN);

				String token = params[0];
				ArrayList<String> values = new ArrayList<String>();
				values.add(token);

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(keys, values);
				Log.d(TAG, "JSON Holder:" + holder.toString());

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
					return "";
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}

	}

	/*
	 * private class FacebookLoginTask extends AsyncTask<String, Void, String> {
	 * 
	 * @Override protected String doInBackground(String... params) { try { HttpClient client = new DefaultHttpClient();
	 * 
	 * // TODO : UNCOMMENT WHEN BACKEND MAKE SECURE REQUESTS WITH HTTPS
	 * 
	 * // HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER; // SchemeRegistry registry = new SchemeRegistry(); // SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory(); // socketFactory.setHostnameVerifier((X509HostnameVerifier)
	 * hostnameVerifier); // registry.register(new Scheme("http", socketFactory, 443)); // SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); // DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
	 * 
	 * // // Set verifier // HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	 * 
	 * HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_FACEBOOK_TOKEN_URL); httpPost.setHeader("Accept", "application/json"); String token = params[0];
	 * 
	 * List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1); nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_FACEBOOK_TOKEN, token));
	 * 
	 * httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
	 * 
	 * // HttpResponse response = httpClient.execute(httpPost); HttpResponse response = client.execute(httpPost); // <-- REMOVE WHEN HTTPS
	 * 
	 * if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) { String responseBody = EntityUtils.toString(response.getEntity()); JSONObject jObj = new JSONObject(responseBody); String responseToken = jObj.getString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN); return responseToken; } else
	 * if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) { Log.d(TAG, "Invalid Token!"); return ""; } } catch (ClientProtocolException e) { System.out.println("CPE" + e); } catch (IOException e) { System.out.println("IOE" + e); } catch (JSONException e) {
	 * System.out.println("JSONE" + e); } return ""; } }
	 */
}
