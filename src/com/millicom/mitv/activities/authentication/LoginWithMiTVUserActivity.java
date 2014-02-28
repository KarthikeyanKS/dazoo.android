
package com.millicom.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseLoginActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.utilities.RegularExpressionUtils;
import com.millicom.mitv.utilities.ToastHelper;
import com.mitv.Consts;
import com.mitv.R;



public class LoginWithMiTVUserActivity 
	extends BaseLoginActivity 
	implements OnClickListener 
{
	private static final String TAG = LoginWithMiTVUserActivity.class.getName();
	
	private RelativeLayout facebookContainer;
	
	private EditText emailEditText;
	private EditText passwordEditText;
	
	private Button loginButton;
	private Button forgetPasswordButton;
	
	private TextView emailErrorTextView;
	private TextView passwordErrorTextView;
	
	private boolean isInvalidEmail;
	private boolean isInvalidPassword;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_mitvlogin_activity);
		
		isInvalidEmail = true;
		isInvalidPassword = true;

		initViews();
		
		clearErrorFields();
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
		
		String username = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		
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

		clearErrorFields();
		
		switch (status)
		{	
			case LOADING:
			{
				disableFields();
				break;
			}
			
			case FAILED_VALIDATION:
			{
				if(isInvalidEmail)
				{
					emailErrorTextView.setVisibility(View.VISIBLE);
				}
				else if(isInvalidPassword)
				{
					passwordErrorTextView.setVisibility(View.VISIBLE);
				}
				else
				{
					Log.w(TAG, "Failed validation for unknown reasons.");
				}
				
				enableFields();
				
				break;
			}
		
			case SUCCEEDED_WITH_DATA:
			{
				enableFields();
				
				Intent intent = new Intent(LoginWithMiTVUserActivity.this, getMostRecentTabActivity().getClass());

				intent.putExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, true);

				startActivity(intent);
				
				finish();
				
				break;
			}
			
			case FAILED:
			{
				enableFields();
				// TODO NewArc - Display appropriate failure to the user
				ToastHelper.createAndShowToast(this, "Login was unsuccessful.");
				break;
			}
	
			default:
			{
				enableFields();
				Log.w(TAG, "Unhandled UI status.");
				break;
			}
		}
	}
	
	
	
	private void clearErrorFields()
	{
		emailErrorTextView.setVisibility(View.INVISIBLE);
		passwordErrorTextView.setVisibility(View.INVISIBLE);
	}
	
	
	
	private void enableFields()
	{
		emailEditText.setEnabled(true);
		passwordEditText.setEnabled(true);
		loginButton.setEnabled(true);
	}
	
	
	
	private void disableFields()
	{
		emailEditText.setEnabled(false);
		passwordEditText.setEnabled(false);
		loginButton.setEnabled(false);
	}

	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.login));

		emailEditText = (EditText) findViewById(R.id.mitvlogin_login_email_edittext);
		passwordEditText = (EditText) findViewById(R.id.mitvlogin_login_password_edittext);

		loginButton = (Button) findViewById(R.id.mitvlogin_login_button);
		loginButton.setOnClickListener(this);
		
		emailErrorTextView = (TextView) findViewById(R.id.mitvlogin_login_email_error_tv);
		passwordErrorTextView = (TextView) findViewById(R.id.mitvlogin_login_password_error_tv);		
		
		emailErrorTextView.setText(getResources().getString(R.string.login_with_wrong_format_email));
		passwordErrorTextView.setText(getResources().getString(R.string.login_with_wrong_format_password));
		
		forgetPasswordButton = (Button) findViewById(R.id.mitvlogin_forgot_password_button);
		forgetPasswordButton.setOnClickListener(this);
		
		facebookContainer = (RelativeLayout) findViewById(R.id.mitvlogin_facebook_container);
		facebookContainer.setOnClickListener(this);
	}


	
	private boolean isEmailValid() 
	{
		String email = emailEditText.getText().toString();
		
		boolean isValid = RegularExpressionUtils.checkEmail(email);
		
		return isValid;
	}
	
	
	
	private boolean isPasswordValid() 
	{
		String password = passwordEditText.getText().toString();
		
		boolean isValid = RegularExpressionUtils.checkPassword(password);
		
		return isValid;
	}
	
	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.mitvlogin_facebook_container:
			{
				Intent intent = new Intent(LoginWithMiTVUserActivity.this, LoginWithFacebookActivity.class);
				startActivity(intent);
				finish();
				break;
			}
			
			case R.id.mitvlogin_forgot_password_button:
			{
				Intent intent = new Intent(LoginWithMiTVUserActivity.this, ResetPasswordActivity.class);
				startActivity(intent);
				break;
			}
			
			case R.id.mitvlogin_login_button:
			{
				loadData();
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled onClick.");
				break;
			}
		}
	}
}