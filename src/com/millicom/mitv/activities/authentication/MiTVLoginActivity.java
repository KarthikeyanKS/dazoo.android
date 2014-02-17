
package com.millicom.mitv.activities.authentication;



import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.asynctasks.LoginTask;
import com.millicom.mitv.activities.ActivityActivity;
import com.millicom.mitv.activities.HomeActivity;
import com.mitv.AuthenticationService;
import com.mitv.Consts;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.utilities.PatternCheck;



public class MiTVLoginActivity 
	extends SSSignInSignupBaseActivity 
	implements OnClickListener 
{
	private static final String	TAG	= "MiTVLoginActivity";
	
	private ActionBar			mActionBar;
	private Button				mMiTVLoginButton, mForgetPasswordButton;
	private EditText			mEmailLoginEditText, mPasswordLoginEditText;
	private RelativeLayout		mFacebookContainer;
	private TextView			mPasswordErrorTv;
	private TextView			mEmailErrorTv;
	private boolean 			mIsFromActivity;

	private String				miTVToken	= "", userEmailLogin, userPasswordLogin;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_mitvlogin_activity);
		
		Intent intent = getIntent();
		
		if (intent.hasExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY)) 
		{
			mIsFromActivity = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_FROM_ACTIVITY);
		}

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

		mActionBar.setTitle(getResources().getString(R.string.login));

		mFacebookContainer = (RelativeLayout) findViewById(R.id.mitvlogin_facebook_container);
		mFacebookContainer.setOnClickListener(this);

		mMiTVLoginButton = (Button) findViewById(R.id.mitvlogin_login_button);
		mMiTVLoginButton.setOnClickListener(this);
		mEmailLoginEditText = (EditText) findViewById(R.id.mitvlogin_login_email_edittext);
		mPasswordLoginEditText = (EditText) findViewById(R.id.mitvlogin_login_password_edittext);

		mPasswordErrorTv = (TextView) findViewById(R.id.mitvlogin_login_password_error_tv);
		mEmailErrorTv = (TextView) findViewById(R.id.mitvlogin_login_email_error_tv);


		mForgetPasswordButton = (Button) findViewById(R.id.mitvlogin_forgot_password_button);
		mForgetPasswordButton.setOnClickListener(this);
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
	}

	
	
	private boolean verifyPasswordInput() 
	{
		String passwordInput = mPasswordLoginEditText.getText().toString();
		if ((passwordInput != null) && (passwordInput.length() >= Consts.PASSWORD_LENGTH_MIN)
				&& (passwordInput.length() <= Consts.PASSWORD_LENGTH_MAX) && (!passwordInput.matches("[%,#/|<>]+"))) {
			return true;
		} else {
			return false;
		}
	}

	
	
	private boolean verifyEmailInput() 
	{
		String emailInput = mEmailLoginEditText.getText().toString();
		
		if ((emailInput != null) && (PatternCheck.checkEmail(emailInput) == true)) {
			return true;
		} 
		else
		{
			return false;
		}
	}

	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
		
		switch (id) 
		{
		case R.id.mitvlogin_facebook_container:
			Intent intentFacebook = new Intent(MiTVLoginActivity.this, FacebookLoginActivity.class);
			startActivity(intentFacebook);
			break;

		case R.id.mitvlogin_forgot_password_button:
			Intent intentReset = new Intent(MiTVLoginActivity.this, ResetPasswordActivity.class);
			startActivity(intentReset);
			break;
			
		case R.id.mitvlogin_login_button:
			mEmailErrorTv.setVisibility(View.INVISIBLE);
			mPasswordErrorTv.setVisibility(View.INVISIBLE);
			
			if (verifyEmailInput()) 
			{
				if (verifyPasswordInput()) 
				{
					mEmailLoginEditText.setEnabled(false);
					mPasswordLoginEditText.setEnabled(false);

					userEmailLogin = mEmailLoginEditText.getText().toString();
					userPasswordLogin = mPasswordLoginEditText.getText().toString();
					
					LoginTask loginTask = new LoginTask();
					
					try 
					{
						// mitvToken = mitvLoginTask.execute(userEmailLogin, userPasswordLogin).get();
						String responseStr = loginTask.execute(userEmailLogin, userPasswordLogin).get();
						// if (responseStr != null && responseStr.isEmpty() != true) {
						
						if (responseStr != null && TextUtils.isEmpty(responseStr) != true) 
						{
							JSONObject mitvJSON = new JSONObject(responseStr);
							miTVToken = mitvJSON.optString(Consts.API_TOKEN);

							// if (mitvToken.isEmpty() != true && mitvToken.length() > 0) {
							if (miTVToken != null && TextUtils.isEmpty(miTVToken) != true) 
							{
								((SecondScreenApplication) getApplicationContext()).setAccessToken(miTVToken);
								
								Log.d(TAG, "MitvToken: " + miTVToken + "is saved");

								if (AuthenticationService.storeUserInformation(this, mitvJSON))
								{
									//Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
									Log.d(TAG, "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName()); 

//									MiTVStore.getInstance().clearAll();
//									MiTVStore.getInstance().reinitializeAll();

									// clear all the running before activities and start the application from the whole beginning
									
									SecondScreenApplication.getInstance().clearActivityBacktrace();

									Intent intent;
									
									if (mIsFromActivity) 
									{
										intent = new Intent(MiTVLoginActivity.this, ActivityActivity.class);
									}
									else 
									{
										intent = new Intent(MiTVLoginActivity.this, HomeActivity.class);
									}
									
									intent.putExtra(Consts.INTENT_EXTRA_LOG_IN_ACTION, true);

//									ContentManager.updateContent();
									
									startActivity(intent);
								}
								else
								{
									//Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend", Toast.LENGTH_SHORT).show();
									Log.d(TAG, "!!! Failed to fetch the user information from backend !!!");
								}
							} 
							else 
							{
								//Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with us. Please, try again later!", Toast.LENGTH_SHORT).show();
								Log.d(TAG,"!!! Error! Something went wrong while creating an account with us. Please, try again later! !!!");
							}
						}
						else
						{
							// On bad response, email or password wrong
							mPasswordErrorTv.setText(getResources().getString(R.string.login_with_wrong_info));
							mPasswordErrorTv.setVisibility(View.VISIBLE);
							//						Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with us. Please, try again later!", Toast.LENGTH_SHORT).show();
							Log.d(TAG, "Error! MiTV Login: level response from backend");
							mEmailLoginEditText.setEnabled(true);
							mPasswordLoginEditText.setEnabled(true);
						}

					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					catch (ExecutionException e)
					{
						e.printStackTrace();
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
				else 
				{
					// Wrong password input
					mPasswordErrorTv.setText(getResources().getString(R.string.login_with_wrong_format_email));
					mPasswordErrorTv.setVisibility(View.VISIBLE);
					mEmailLoginEditText.setEnabled(true);
					mPasswordLoginEditText.setEnabled(true);
				}
			} 
			else
			{
				//Wrong email input
				mEmailErrorTv.setText(getResources().getString(R.string.login_with_wrong_format_password));
				mEmailErrorTv.setVisibility(View.VISIBLE);
				
				//				Toast.makeText(getApplicationContext(), "check if email/password were input right", Toast.LENGTH_LONG).show();
				mEmailLoginEditText.setEnabled(true);
				mPasswordLoginEditText.setEnabled(true);
			}
		}
	}
}
