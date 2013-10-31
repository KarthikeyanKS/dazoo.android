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
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.homepage.HomePageActivity;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.utilities.PatternCheck;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG	= "SignUpActivity";
	private ActionBar			mActionBar;
	private EditText			mFirstNameEditText, mLastNameEditText, mPasswordRegisterEditText, mEmailRegisterEditText, mEmailResetPasswordEditText;
	private Button				mDazooRegisterButton;
	private String				userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister, dazooToken;
	private TextView			mErrorTextView;
	private Drawable 			mFirstNameDrawable, mLastNameDrawable, mPasswordRegisterDrawable, mEmailRegisterDrawable;
	private TextDrawable		mEmailTextDrawable, mPasswordTextDrawable;
	
	
	private final int 			REGISTER_FIRSTNAME_MISSING = 0;
	private final int 			REGISTER_LASTNAME_MISSING = 1;
	private final int 			REGISTER_EMAIL_WRONG = 2;
	private final int 			PASSWORD_LENGTH_WRONG = 3;
	private final int 			PASSWORD_ILLEGAL_CHARACTERS = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_signup_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
		mErrorTextView = (TextView) findViewById(R.id.signup_error_textview);
		mDazooRegisterButton = (Button) findViewById(R.id.signup_register_button);
		mDazooRegisterButton.setOnClickListener(this);
		
		mFirstNameDrawable = mFirstNameEditText.getBackground();
		mLastNameDrawable = mLastNameEditText.getBackground();
		mEmailRegisterDrawable = mEmailRegisterEditText.getBackground();
		mPasswordRegisterDrawable = mPasswordRegisterEditText.getBackground();
		
		setTextWatchers();
	}
	
	//Sets the TextWatchers for the extra drawable hints.
	private void setTextWatchers() {
		mPasswordTextDrawable = new TextDrawable(this);
		mPasswordTextDrawable.setText(getResources().getString(R.string.signup_characters));
		mPasswordTextDrawable.setTextColor(getResources().getColor(R.color.light_gray));
		mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mPasswordTextDrawable, null);
		mPasswordRegisterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (mPasswordRegisterEditText.getText().toString().equals("")) {
					mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mPasswordTextDrawable, null);
				}
				else {
					mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		mEmailTextDrawable = new TextDrawable(this);
		mEmailTextDrawable.setText(getResources().getString(R.string.signup_email_example));
		mEmailTextDrawable.setTextColor(getResources().getColor(R.color.light_gray));
		mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mEmailTextDrawable, null);
		mEmailRegisterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (mEmailRegisterEditText.getText().toString().equals("")) {
					mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mEmailTextDrawable, null);
				}
				else {
					mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	private boolean verifyRegisterInput() {
		String emailInput = mEmailRegisterEditText.getText().toString();
		String firstNameInput = mFirstNameEditText.getText().toString();
		String lastNameInput = mLastNameEditText.getText().toString();
		String passwordInput = mPasswordRegisterEditText.getText().toString();
		if ((!firstNameInput.equals("")) && (!lastNameInput.equals("")) && (!passwordInput.equals("")) && (!emailInput.equals("")) && (passwordInput.length() >= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN)
				&& (passwordInput.length() <= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MAX) && (!passwordInput.matches("[%,#/|<>]+")) && (PatternCheck.checkEmail(emailInput) == true)) {
			return true;
		} else return false;
	}

	private int findWrongRegisterInput() {
		String emailInput = mEmailRegisterEditText.getText().toString();
		String firstNameInput = mFirstNameEditText.getText().toString();
		String lastNameInput = mLastNameEditText.getText().toString();
		String passwordInput = mPasswordRegisterEditText.getText().toString();
		if ((firstNameInput.equals(""))) {
			return REGISTER_FIRSTNAME_MISSING;
		}
		if ((lastNameInput.equals(""))) {
			return REGISTER_LASTNAME_MISSING;
		}
		else if ((emailInput.equals("")) || (PatternCheck.checkEmail(emailInput) == false)) {
			return REGISTER_EMAIL_WRONG;
		}
		else if ((passwordInput.length() <= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN)
				|| (passwordInput.length() >= Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MAX)) {
			return PASSWORD_LENGTH_WRONG;
		}
		else if (passwordInput.matches("[%,#/|<>]+")) {
			return PASSWORD_ILLEGAL_CHARACTERS;
		}
		return -1;
	}

	@SuppressLint("NewApi")
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
								Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
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
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				int errorId = findWrongRegisterInput();
				//TODO: Fix compatibility with highlighting for older API versions
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						if (mFirstNameEditText.getBackground() != mFirstNameDrawable) {
							mFirstNameEditText.setBackground(mFirstNameDrawable);
						}
						if (mLastNameEditText.getBackground() != mLastNameDrawable) {
							mLastNameEditText.setBackground(mLastNameDrawable);
						}
						if (mEmailRegisterEditText.getBackground() != mEmailRegisterDrawable) {
							mEmailRegisterEditText.setBackground(mEmailRegisterDrawable);
						}
						if (mPasswordRegisterEditText.getBackground() != mPasswordRegisterDrawable) {
							mPasswordRegisterEditText.setBackground(mPasswordRegisterDrawable);
						}
					}
					switch (errorId) {
						case REGISTER_FIRSTNAME_MISSING:
							mFirstNameEditText.setActivated(true);
							if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
								Drawable bg = getResources().getDrawable(R.drawable.edittext_activated);
								mFirstNameEditText.setBackground(bg);
								mFirstNameEditText.requestFocus();
							}
							break;
						case REGISTER_LASTNAME_MISSING:
							mLastNameEditText.setActivated(true);
							if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
								Drawable bg = getResources().getDrawable(R.drawable.edittext_activated);
								mLastNameEditText.setBackground(bg);
								mLastNameEditText.requestFocus();
							}
							break;
						case REGISTER_EMAIL_WRONG:
							mEmailRegisterEditText.setActivated(true);
							if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
								Drawable bg = getResources().getDrawable(R.drawable.edittext_activated);
								mEmailRegisterEditText.setBackground(bg);
								mEmailRegisterEditText.requestFocus();
							}
							break;
						case PASSWORD_LENGTH_WRONG:
							mPasswordRegisterEditText.setActivated(true);
							if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
								Drawable bg = getResources().getDrawable(R.drawable.edittext_activated);
								mPasswordRegisterEditText.setBackground(bg);
								mPasswordRegisterEditText.requestFocus();
							}
							break;
						case PASSWORD_ILLEGAL_CHARACTERS:
							mPasswordRegisterEditText.setActivated(true);
							if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
								Drawable bg = getResources().getDrawable(R.drawable.edittext_activated);
								mPasswordRegisterEditText.setBackground(bg);
								mPasswordRegisterEditText.requestFocus();
							}
							break;
					}
				}
				switch (errorId) {
					case REGISTER_FIRSTNAME_MISSING:
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_firstname));
						break;
					case REGISTER_LASTNAME_MISSING:
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_lastname));
						break;
					case REGISTER_EMAIL_WRONG:
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email));
						break;
					case PASSWORD_LENGTH_WRONG:
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_passwordlength) +
											   " " + Consts.MILLICOM_SECONSCREEN_PASSWORD_LENGTH_MIN + " " + 
										       getResources().getString(R.string.signup_with_email_characters));
						break;
					case PASSWORD_ILLEGAL_CHARACTERS:
						mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_passwordcharacters));
						break;
				}
				mErrorTextView.setVisibility(TextView.VISIBLE);
				mFirstNameEditText.setEnabled(true);
				mLastNameEditText.setEnabled(true);
				mEmailRegisterEditText.setEnabled(true);
				mPasswordRegisterEditText.setEnabled(true);
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