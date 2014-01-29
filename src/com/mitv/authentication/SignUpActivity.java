package com.mitv.authentication;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
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

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.nsd.NsdManager.RegistrationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.content.SSActivity;
import com.mitv.homepage.HomeActivity;
import com.mitv.utilities.JSONUtilities;
import com.mitv.utilities.PatternCheck;
import com.mitv.utilities.TextDrawable;
import com.nostra13.universalimageloader.utils.IoUtils;

public class SignUpActivity extends SSSignInSignupBaseActivity implements OnClickListener {

	private static final String	TAG							= "SignUpActivity";
	private ActionBar			mActionBar;
	private EditText			mFirstNameEditText, mLastNameEditText, mPasswordRegisterEditText, mEmailRegisterEditText, mEmailResetPasswordEditText;
	private Button				mDazooRegisterButton;
	private String				userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister, dazooToken;
	private TextView			mErrorTextView;
	private TextDrawable		mEmailTextDrawable, mPasswordTextDrawable;
	private String 				mBadResponseString;

	private final int			REGISTER_FIRSTNAME_MISSING	= 0;
	private final int			REGISTER_LASTNAME_MISSING	= 1;
	private final int			REGISTER_EMAIL_WRONG		= 2;
	private final int			PASSWORD_LENGTH_WRONG		= 3;
	private final int			PASSWORD_ILLEGAL_CHARACTERS	= 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_signup_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		initViews();
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
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);


		final int actionBarColor = getResources().getColor(R.color.blue1);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setTitle(getResources().getString(R.string.sign_up_with_email));

		mFirstNameEditText = (EditText) findViewById(R.id.signup_firstname_edittext);
		mLastNameEditText = (EditText) findViewById(R.id.signup_lastname_edittext);
		mEmailRegisterEditText = (EditText) findViewById(R.id.signup_email_edittext);
		mPasswordRegisterEditText = (EditText) findViewById(R.id.signup_password_edittext);
		mErrorTextView = (TextView) findViewById(R.id.signup_error_textview);
		mDazooRegisterButton = (Button) findViewById(R.id.signup_register_button);
		mDazooRegisterButton.setOnClickListener(this);

		//		setTextWatchers();

		// TODO: Static drawable background is not properly set, causing a flickering effect. Quickfix!
		mFirstNameEditText.setBackgroundResource(R.drawable.edittext_standard);
		mLastNameEditText.setBackgroundResource(R.drawable.edittext_standard);
		mEmailRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
		mPasswordRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
	}

	// Sets the TextWatchers for the extra drawable hints.
	private void setTextWatchers() {
		mPasswordTextDrawable = new TextDrawable(this);
		mPasswordTextDrawable.setText(getResources().getString(R.string.signup_characters));
		mPasswordTextDrawable.setTextColor(getResources().getColor(R.color.grey2));
		mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mPasswordTextDrawable, null);
		mPasswordRegisterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (mPasswordRegisterEditText.getText().toString().equals("")) {
					mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mPasswordTextDrawable, null);
				} else {
					mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
		mEmailTextDrawable = new TextDrawable(this);
		mEmailTextDrawable.setText(getResources().getString(R.string.signup_email_example));
		mEmailTextDrawable.setTextColor(getResources().getColor(R.color.grey2));
		mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mEmailTextDrawable, null);
		mEmailRegisterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (mEmailRegisterEditText.getText().toString().equals("")) {
					mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mEmailTextDrawable, null);
				} else {
					mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.signup_register_button:
			mFirstNameEditText.setEnabled(false);
			mLastNameEditText.setEnabled(false);
			mEmailRegisterEditText.setEnabled(false);
			mPasswordRegisterEditText.setEnabled(false);

			mFirstNameEditText.setBackgroundResource(R.drawable.edittext_standard);
			mLastNameEditText.setBackgroundResource(R.drawable.edittext_standard);
			mEmailRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
			mPasswordRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);

			userEmailRegister = mEmailRegisterEditText.getText().toString();
			userPasswordRegister = mPasswordRegisterEditText.getText().toString();
			userFirstNameRegister = mFirstNameEditText.getText().toString();
			userLastNameRegister = mLastNameEditText.getText().toString();

			DazooRegistrationTask dazooRegisterTask = new DazooRegistrationTask();
			try {
				// dazooToken = dazooRegisterTask.execute(userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister).get();
				String responseStr = dazooRegisterTask.execute(userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister).get();
				// if (responseStr != null && responseStr.isEmpty() != true) {
				if (responseStr != null && TextUtils.isEmpty(responseStr) != true) {
					JSONObject dazooRegJSON = new JSONObject(responseStr);
					dazooToken = dazooRegJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
					Log.d(TAG, "DazooToken: " + dazooToken + "is saved");

					// if (dazooToken.isEmpty() != true && dazooToken.length() > 0) {
					if (dazooToken != null && TextUtils.isEmpty(dazooToken) != true) {
						((SecondScreenApplication) getApplicationContext()).setAccessToken(dazooToken);
						if (AuthenticationService.storeUserInformation(this, dazooRegJSON)) {
							//Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
							Log.d(TAG, "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName());

							// go to Start page
							Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
							startActivity(intent);
							finish();

						} else {
							//Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend.", Toast.LENGTH_SHORT).show();
							Log.d(TAG, "!!! Failed to fetch the user information from backend !!!");
						}
					} else {
						//Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with Dazoo. Please, try again later!", Toast.LENGTH_LONG).show();
						Log.d(TAG, "Error! Something went wrong while creating an account with Dazoo. Please, try again later!");
					}
				} else {
					//						Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with us. Please, try again later!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Error! Dazoo Login: level response from backend");
					mFirstNameEditText.setEnabled(true);
					mLastNameEditText.setEnabled(true);
					mEmailRegisterEditText.setEnabled(true);
					mPasswordRegisterEditText.setEnabled(true);

					// Set error textview 
					if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_EMAIL_ALREADY_TAKEN)) {
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_already_registered));
						mEmailRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						mEmailRegisterEditText.requestFocus();
					}
					else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_NOT_REAL_EMAIL)) {
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_incorrect));
						mEmailRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						mEmailRegisterEditText.requestFocus();
					}
					else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_PASSWORD_TOO_SHORT)) {
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_passwordlength) + " " 
								+ Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN + " "
								+ getResources().getString(R.string.signup_with_email_characters));
						mPasswordRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						mPasswordRegisterEditText.requestFocus();
					}
					else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_FIRSTNAME_NOT_SUPPLIED)) {
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_firstname));
						mFirstNameEditText.setBackgroundResource(R.drawable.edittext_activated);
						mFirstNameEditText.requestFocus();
					}

					mErrorTextView.setVisibility(View.VISIBLE);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	private class DazooRegistrationTask extends AsyncTask<String, Void, String> {

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
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_DAZOO_REGISTER_URL);

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_EMAIL, Consts.MILLICOM_SECONDSCREEN_API_PASSWORD,
						Consts.MILLICOM_SECONDSCREEN_API_FIRSTNAME, Consts.MILLICOM_SECONDSCREEN_API_LASTNAME), Arrays.asList(params[0], params[1], params[2], params[3]));

				StringEntity entity = new StringEntity(holder.toString());

				httpPost.setEntity(entity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				// HttpResponse response = client.execute(httpPost);
				HttpResponse response = client.execute(httpPost);

				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					String responseBody = EntityUtils.toString(response.getEntity());
					return responseBody;
				} else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) {
					Log.d(TAG, "Invalid Token!");

					// Get response string (reason) from bad response
					HttpEntity httpentity = response.getEntity();
					mBadResponseString = EntityUtils.toString(httpentity);
					Log.d(TAG, "Registration error: " + mBadResponseString);

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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
