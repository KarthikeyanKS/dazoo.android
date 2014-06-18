
package com.mitv.activities.authentication;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.mitv.activities.base.BaseActivityWithoutSearchOption;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.RateAppManager;
import com.mitv.managers.ContentManager;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;
import com.mitv.utilities.RegularExpressionUtils;



public class LoginWithMiTVUserActivity 
	extends BaseActivityWithoutSearchOption 
	implements OnClickListener 
{
	private static final String TAG = LoginWithMiTVUserActivity.class.getName();
	
	private RelativeLayout facebookContainer;
	
	private EditText emailEditText;
	private EditText passwordEditText;
	
	private RelativeLayout loginButton;
	private ProgressBar loginButtonProgressBar;
	private FontTextView loginButtonTextView;
	private RelativeLayout forgetPasswordButton;
	
	private TextView emailErrorTextView;
	private TextView passwordErrorTextView;
	
	private boolean isInvalidEmail;
	private boolean isInvalidPassword;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_mitvlogin_activity);
		
		isInvalidEmail = true;
		isInvalidPassword = true;

		initViews();
		
		clearErrorFields();
		
		registerAsListenerForRequest(RequestIdentifierEnum.USER_LOGIN);
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
		
		boolean isConnected = NetworkUtils.isConnected();

		if (isConnected) 
		{
			RateAppManager.significantEvent(this);
			ContentManager.sharedInstance().performLogin(this, username, password);
		}
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return true;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
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
				showLoginSpinner();
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
				hideLoginSpinner();
				break;
			}
		
			case SUCCESS_WITH_CONTENT:
			{
				enableFields();
				hideLoginSpinner();
				
				if(!ContentManager.sharedInstance().tryStartReturnActivity(this)) 
				{
					String activityToReturnAfterLogin = getIntent().getStringExtra(Constants.INTENT_EXTRA_ACTIVITY_TO_RETURN_AFTER_LOGIN);
					
					Class<?> activityClassToReturn;
					
					if(activityToReturnAfterLogin != null)
					{
						try 
						{
							activityClassToReturn = Class.forName(activityToReturnAfterLogin);
						} 
						catch (ClassNotFoundException cnfex) 
						{
							Log.w(TAG, cnfex.getMessage());
							
							activityClassToReturn = HomeActivity.class;
						}
					}
					else
					{
						Activity mostRecentTabActivity = getMostRecentTabActivity();
						
						if(mostRecentTabActivity != null)
						{
							activityClassToReturn = mostRecentTabActivity.getClass();
						}
						else
						{
							activityClassToReturn = HomeActivity.class;
						}
					}
					
					Intent intent = new Intent(LoginWithMiTVUserActivity.this, activityClassToReturn);
					
					final Bundle bundle = getIntent().getExtras();
					
					if(bundle != null)
					{
						intent.putExtras(bundle);
						intent.removeExtra(Constants.INTENT_EXTRA_ACTIVITY_TO_RETURN_AFTER_LOGIN);
					}
					
					intent.putExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, true);
					
					startActivity(intent);
				}				
				finish();
				
				break;
			}
			
			case FAILED:
			{
				enableFields();
				hideLoginSpinner();
				
				String message;
				
				boolean isConnected = NetworkUtils.isConnected();

				if (isConnected) 
				{
					message = getString(R.string.login_with_email_failed);
				}
				else
				{
					message = getString(R.string.toast_internet_connection);
				}
				
				ToastHelper.createAndShowShortToast(message);
				break;
			}
	
			default:
			{
				enableFields();
				hideLoginSpinner();
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
		
		forgetPasswordButton.setEnabled(true);
		facebookContainer.setEnabled(true);
		
		actionBar.setHomeButtonEnabled(true);
	}
	
	
	
	private void disableFields()
	{
		emailEditText.setEnabled(false);
		passwordEditText.setEnabled(false);
		loginButton.setEnabled(false);
		
		forgetPasswordButton.setEnabled(false);
		facebookContainer.setEnabled(false);
		
		actionBar.setHomeButtonEnabled(false);
	}
	
	
	
	private void showLoginSpinner() 
	{
		loginButtonProgressBar.setVisibility(View.VISIBLE);
		loginButtonTextView.setText(getString(R.string.loading_message_login_button));
	}
	
	
	
	private void hideLoginSpinner()
	{
		loginButtonProgressBar.setVisibility(View.GONE);
		loginButtonTextView.setText(getString(R.string.login_with_mitv));
	}

	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getString(R.string.login));

		emailEditText = (EditText) findViewById(R.id.mitvlogin_login_email_edittext);
		passwordEditText = (EditText) findViewById(R.id.mitvlogin_login_password_edittext);

		loginButton = (RelativeLayout) findViewById(R.id.mitvlogin_login_button);
		loginButton.setOnClickListener(this);
		loginButtonProgressBar = (ProgressBar) findViewById(R.id.mitvlogin_progressbar);
		loginButtonTextView = (FontTextView) findViewById(R.id.mitvlogin_login_button_tv);
		
		emailErrorTextView = (TextView) findViewById(R.id.mitvlogin_login_email_error_tv);
		passwordErrorTextView = (TextView) findViewById(R.id.mitvlogin_login_password_error_tv);		
		
		emailErrorTextView.setText(getString(R.string.login_with_wrong_format_email));
		passwordErrorTextView.setText(getString(R.string.login_with_wrong_format_password));
		
		forgetPasswordButton = (RelativeLayout) findViewById(R.id.mitvlogin_forgot_password_button);
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
				GenericUtils.hideKeyboard(this);
				
				Intent intent = new Intent(LoginWithMiTVUserActivity.this, LoginWithFacebookActivity.class);
				startActivity(intent);
				finish();
				
				break;
			}
			
			case R.id.mitvlogin_forgot_password_button:
			{
				GenericUtils.hideKeyboard(this);
				
				Intent intent = new Intent(LoginWithMiTVUserActivity.this, ResetPasswordSendEmailActivity.class);
				startActivity(intent);
				
				break;
			}
			
			case R.id.mitvlogin_login_button:
			{
				GenericUtils.hideKeyboard(this);
				
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