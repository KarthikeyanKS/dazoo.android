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

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.homepage.HomePageActivity;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.utilities.PatternCheck;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG	= "SignUpActivity";
	private ActionBar			mActionBar;
	private EditText			mFirstNameEditText, mLastNameEditText, mPasswordRegisterEditText, mPasswordRegisterVerifyEditText, mEmailRegisterEditText, mEmailResetPasswordEditText;
	private Button				mDazooRegisterButton;
	private String				userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister, dazooToken;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_signup_activity);
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

		mActionBar.setTitle(getResources().getString(R.string.sign_up_with_email));

		mFirstNameEditText = (EditText) findViewById(R.id.signup_firstname_edittext);
		mLastNameEditText = (EditText) findViewById(R.id.signup_lastname_edittext);
		mEmailRegisterEditText = (EditText) findViewById(R.id.signup_email_edittext);
		mPasswordRegisterEditText = (EditText) findViewById(R.id.signup_password_edittext);
		mPasswordRegisterVerifyEditText = (EditText) findViewById(R.id.signup_password_verify_edittext);
		mDazooRegisterButton = (Button) findViewById(R.id.signup_register_button);
		mDazooRegisterButton.setOnClickListener(this);
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
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.signup_register_button:
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
					// if (responseStr != null && responseStr.isEmpty() != true) {
					if (responseStr != null && TextUtils.isEmpty(responseStr) != true) {
						JSONObject dazooRegJSON = new JSONObject(responseStr);
						dazooToken = dazooRegJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_TOKEN);
						Log.d(TAG, "DazooToken: " + dazooToken + "is saved");

						// if (dazooToken.isEmpty() != true && dazooToken.length() > 0) {
						if (dazooToken != null && TextUtils.isEmpty(dazooToken) != true) {
							((SecondScreenApplication) getApplicationContext()).setAccessToken(dazooToken);
							// Get the information about the user
							String userDataString = dazooRegJSON.optString(Consts.MILLICOM_SECONDSCREEN_API_USER);
							if (JSONUtilities.storeUserInformation(this, userDataString)) {
								Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
								
								// go to Start page
								Intent intent = new Intent(SignUpActivity.this, HomePageActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
								finish();
							
							} else {
								Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with Dazoo. Please, try again later!", Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with us. Please, try again later!", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "Error! Dazoo Login: level response from backend");
						mFirstNameEditText.setEnabled(true);
						mLastNameEditText.setEnabled(true);
						mEmailRegisterEditText.setEnabled(true);
						mPasswordRegisterEditText.setEnabled(true);
						mPasswordRegisterVerifyEditText.setEnabled(true);
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
	
	private class DazooRegistrationTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_DAZOO_REGISTER_URL);

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_EMAIL, Consts.MILLICOM_SECONDSCREEN_API_PASSWORD,
						Consts.MILLICOM_SECONDSCREEN_API_FIRSTNAME, Consts.MILLICOM_SECONDSCREEN_API_LASTNAME), Arrays.asList(params[0], params[1], params[2], params[3]));

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
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		finish();
	}
}