
package com.millicom.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.HomeActivity;
import com.millicom.mitv.activities.base.BaseLoginActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.utilities.RegularExpressionUtils;
import com.mitv.Consts;
import com.mitv.R;



public class MiTVUserLoginActivity 
	extends BaseLoginActivity 
	implements OnClickListener 
{
	private static final String TAG = MiTVUserLoginActivity.class.getName();
	
	
	private ActionBar actionBar;
	private Button miTVLoginButton;
	private Button forgetPasswordButton;
	private EditText emailLoginEditText;
	private EditText passwordLoginEditText;
	private RelativeLayout facebookContainer;
	private TextView passwordErrorTv;
	private TextView emailErrorTv;
		
	private Class<?> returnActivity;
	private boolean isInvalidEmail;
	private boolean isInvalidPassword;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_mitvlogin_activity);
		
		Intent intent = getIntent();
		
		if (intent.hasExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME)) 
		{
			String returnActivityClassName = intent.getExtras().getString(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME);
			
			try 
			{
				returnActivity = Class.forName(returnActivityClassName);
			} 
			catch (ClassNotFoundException cnfex) 
			{
				Log.e(TAG, cnfex.getMessage(), cnfex);
				
				returnActivity = HomeActivity.class;
			}
		}
		else
		{
			returnActivity = HomeActivity.class;
		}
		
		isInvalidEmail = true;
		isInvalidPassword = true;

		initViews();
		
		// TODO NewArc - Remove this after tests
		emailLoginEditText.setText("junit_test@mi.tv");
		passwordLoginEditText.setText("junit_test");
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	
	
	@Override
	protected void loadData() 
	{
		isInvalidEmail = (isEmailValid() == false);
		isInvalidPassword= (isPasswordValid() == false);
		
		if(isInvalidEmail || isInvalidPassword)
		{
			updateUI(UIStatusEnum.FAILED_VALIDATION);
			
			return;
		}
		
		updateUI(UIStatusEnum.LOADING);
		
		String username = emailLoginEditText.getText().toString();
		String password = passwordLoginEditText.getText().toString();
		
		ContentManager.sharedInstance().performLogin(this, username, password);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		} 
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		emailErrorTv.setVisibility(View.INVISIBLE);
		passwordErrorTv.setVisibility(View.INVISIBLE);
		
		switch (status)
		{	
			case LOADING:
			{
				emailLoginEditText.setEnabled(false);
				passwordLoginEditText.setEnabled(false);
				miTVLoginButton.setEnabled(false);
				break;
			}
			
			case FAILED_VALIDATION:
			{
				if(isInvalidEmail)
				{
					emailErrorTv.setText(getResources().getString(R.string.login_with_wrong_format_password));
					emailErrorTv.setVisibility(View.VISIBLE);
					passwordErrorTv.setVisibility(View.INVISIBLE);
				}
				else if(isInvalidPassword)
				{
					passwordErrorTv.setText(getResources().getString(R.string.login_with_wrong_format_email));
					passwordErrorTv.setVisibility(View.VISIBLE);			
					emailErrorTv.setVisibility(View.INVISIBLE);
				}
				else
				{
					Log.e(TAG, "Failed validation for unknown reasons.");
				}
				
				emailLoginEditText.setEnabled(true);
				passwordLoginEditText.setEnabled(true);
				miTVLoginButton.setEnabled(true);
				
				break;
			}
		
			case SUCCEEDED_WITH_DATA:
			{
				emailLoginEditText.setEnabled(true);
				passwordLoginEditText.setEnabled(true);
				miTVLoginButton.setEnabled(true);
				
				Intent intent = new Intent(MiTVUserLoginActivity.this, returnActivity);

				intent.putExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, true);

				startActivity(intent);
				
				break;
			}
	
			default:
			{
				emailLoginEditText.setEnabled(true);
				passwordLoginEditText.setEnabled(true);
				miTVLoginButton.setEnabled(true);
				
				passwordErrorTv.setText(getResources().getString(R.string.login_with_wrong_info));
				passwordErrorTv.setVisibility(View.VISIBLE);
				
				emailLoginEditText.setEnabled(true);
				passwordLoginEditText.setEnabled(true);
				break;
			}
		}
	}

	
	
	private void initViews() 
	{
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.login));

		facebookContainer = (RelativeLayout) findViewById(R.id.mitvlogin_facebook_container);
		facebookContainer.setOnClickListener(this);

		miTVLoginButton = (Button) findViewById(R.id.mitvlogin_login_button);
		miTVLoginButton.setOnClickListener(this);
		emailLoginEditText = (EditText) findViewById(R.id.mitvlogin_login_email_edittext);
		passwordLoginEditText = (EditText) findViewById(R.id.mitvlogin_login_password_edittext);

		passwordErrorTv = (TextView) findViewById(R.id.mitvlogin_login_password_error_tv);
		emailErrorTv = (TextView) findViewById(R.id.mitvlogin_login_email_error_tv);

		forgetPasswordButton = (Button) findViewById(R.id.mitvlogin_forgot_password_button);
		forgetPasswordButton.setOnClickListener(this);
	}


	
	private boolean isEmailValid() 
	{
		String email = emailLoginEditText.getText().toString();
		
		boolean isValid = RegularExpressionUtils.checkEmail(email);
		
		return isValid;
	}
	
	
	
	private boolean isPasswordValid() 
	{
		String password = passwordLoginEditText.getText().toString();
		
		boolean isValid = RegularExpressionUtils.checkPassword(password);
		
		return isValid;
	}
	
	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
	}

	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.mitvlogin_facebook_container:
			{
				Intent intent = new Intent(MiTVUserLoginActivity.this, FacebookLoginActivity.class);
				startActivity(intent);
				break;
			}
			
			case R.id.mitvlogin_forgot_password_button:
			{
				Intent intent = new Intent(MiTVUserLoginActivity.this, ResetPasswordActivity.class);
				startActivity(intent);
				break;
			}
			
			case R.id.mitvlogin_login_button:
			{
				loadData();
				break;
			}		
		}
	}
}