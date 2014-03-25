
package com.mitv.activities.authentication;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseActivityWithoutSearchOption;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;
import com.mitv.utilities.RegularExpressionUtils;



public class SignUpWithEmailActivity 
	extends BaseActivityWithoutSearchOption 
	implements OnClickListener
{
	private static final String TAG = SignUpWithEmailActivity.class.getName();
	
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private EditText passwordEditText;
	
	private RelativeLayout signUpButton;
	private ProgressBar signUpButtonProgressBar;
	private FontTextView signUpButtonTextView;
	
	private TextView firstnameErrorTextView;
	private TextView lastnameErrorTextView;
	private TextView emailErrorTextView;
	private TextView passwordErrorTextView;
	
	private boolean isInvalidFirstname;
	private boolean isInvalidLastname;
	private boolean isInvalidEmail;
	private boolean isInvalidPassword;
	
	private FetchRequestResultEnum fetchRequestResult;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_signup_activity);
		
		isInvalidFirstname = true;
		isInvalidLastname = true;
		isInvalidEmail = true;
		isInvalidPassword = true;
		
		initViews();
		
		clearErrorFields();
		
		registerAsListenerForRequest(RequestIdentifierEnum.USER_SIGN_UP);
	}
	

	
	@Override
	protected void loadData() 
	{
		isInvalidFirstname = (isFirstnameValid() == false);
		isInvalidLastname = (isLastnameValid() == false);
		isInvalidEmail = (isEmailValid() == false);
		isInvalidPassword = (isPasswordValid() == false);
		
		if(isInvalidFirstname || isInvalidLastname || isInvalidEmail || isInvalidPassword)
		{
			updateUI(UIStatusEnum.FAILED_VALIDATION);
			
			return;
		}
		
		updateUI(UIStatusEnum.LOADING);
		
		String firstname = firstNameEditText.getText().toString();
		String lastname = lastNameEditText.getText().toString();
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		
		boolean isConnected = NetworkUtils.isConnected();

		if (isConnected) 
		{
			ContentManager.sharedInstance().performSignUp(this, email, password, firstname, lastname);
		}
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}
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
			this.fetchRequestResult = fetchRequestResult;
			
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
				showLoadingSpinner();
				break;
			}
			
			case FAILED_VALIDATION:
			{
				hideLoadingSpinner();
				
				if(isInvalidFirstname)
				{
					firstnameErrorTextView.setVisibility(View.VISIBLE);
					firstNameEditText.setBackgroundResource(R.drawable.edittext_activated);
					firstNameEditText.requestFocus();
				}
				else if(isInvalidLastname)
				{
					lastnameErrorTextView.setVisibility(View.VISIBLE);
					lastNameEditText.setBackgroundResource(R.drawable.edittext_activated);
					lastNameEditText.requestFocus();
				}
				else if(isInvalidEmail)
				{
					emailErrorTextView.setVisibility(View.VISIBLE);
					emailErrorTextView.setText(getString(R.string.signup_with_email_login_wrong_format_email));
					emailEditText.setBackgroundResource(R.drawable.edittext_activated);
					emailEditText.requestFocus();
				}
				else if(isInvalidPassword)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(getString(R.string.signup_with_email_error_passwordlength));
					sb.append(" ");
					sb.append(Constants.PASSWORD_LENGTH_MIN);
					sb.append(" ");
					sb.append(getString(R.string.signup_with_email_characters));
					
					passwordErrorTextView.setText(sb.toString());
					passwordErrorTextView.setVisibility(View.VISIBLE);
					passwordEditText.setBackgroundResource(R.drawable.edittext_activated);
					passwordEditText.requestFocus();
				}
				else
				{
					Log.w(TAG, "Failed validation for unknown reasons.");
				}
				
				enableFields();
				
				break;
			}
			
			case SUCCESS_WITH_CONTENT:
			{
				enableFields();
				hideLoadingSpinner();
				if(!ContentManager.sharedInstance().tryStartReturnActivity(this)) 
				{
					Activity mostRecentTabActivity = getMostRecentTabActivity();
					Intent intent = new Intent(SignUpWithEmailActivity.this, mostRecentTabActivity.getClass());
					intent.putExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, true);
					startActivity(intent);
				}

				finish();
				
				break;
			}
			
			case FAILED:
			{
				hideLoadingSpinner();
				
				boolean isConnected = NetworkUtils.isConnected();

				if (isConnected) 
				{
					switch (fetchRequestResult) 
					{
						case USER_SIGN_UP_EMAIL_ALREADY_TAKEN:
						{
							emailErrorTextView.setVisibility(View.VISIBLE);
							emailErrorTextView.setText(getString(R.string.signup_with_email_error_email_already_registered));
							
							emailEditText.setBackgroundResource(R.drawable.edittext_activated);
							emailEditText.requestFocus();
							break;
						}
						
						case USER_SIGN_UP_EMAIL_IS_INVALID:
						{
							emailErrorTextView.setVisibility(View.VISIBLE);
							emailErrorTextView.setText(getString(R.string.signup_with_email_error_email_incorrect));
							
							emailEditText.setBackgroundResource(R.drawable.edittext_activated);
							emailEditText.requestFocus();
							break;
						}
						
						case USER_SIGN_UP_PASSWORD_TOO_SHORT:
						{
							passwordErrorTextView.setVisibility(View.VISIBLE);
							
							StringBuilder sb = new StringBuilder();
							sb.append(getString(R.string.signup_with_email_error_passwordlength));
							sb.append(" ");
							sb.append(Constants.PASSWORD_LENGTH_MIN);
							sb.append(" ");
							sb.append(getString(R.string.signup_with_email_characters));
							
							passwordErrorTextView.setText(sb.toString());
							
							passwordEditText.setBackgroundResource(R.drawable.edittext_activated);	
							passwordEditText.requestFocus();
							break;
						}
						
						case USER_SIGN_UP_FIRST_NAME_NOT_SUPLIED:
						{
							firstnameErrorTextView.setVisibility(View.VISIBLE);
							
							firstNameEditText.setBackgroundResource(R.drawable.edittext_activated);
							firstNameEditText.requestFocus();
							
							break;
						}
						
						default:
						{
							Log.w(TAG, "Unhandled fetch request result status.");
							
							String message = getString(R.string.signup_with_email_failed);
							
							ToastHelper.createAndShowShortToast(message);
							break;
						}
					}
				}
				else
				{
					ToastHelper.createAndShowNoInternetConnectionToast();
				}
				
				enableFields();
				break;
			}
	
			default:
			{
				enableFields();
				hideLoadingSpinner();
				break;
			}
		}
	}
	
	
	
	private void clearErrorFields()
	{
		firstnameErrorTextView.setVisibility(View.INVISIBLE);
		lastnameErrorTextView.setVisibility(View.INVISIBLE);
		emailErrorTextView.setVisibility(View.INVISIBLE);
		passwordErrorTextView.setVisibility(View.INVISIBLE);
		
		firstNameEditText.setBackgroundResource(R.drawable.edittext_standard);
		lastNameEditText.setBackgroundResource(R.drawable.edittext_standard);
		emailEditText.setBackgroundResource(R.drawable.edittext_standard);
		passwordEditText.setBackgroundResource(R.drawable.edittext_standard);
	}
	
	
	
	private void enableFields()
	{
		firstNameEditText.setEnabled(true);
		lastNameEditText.setEnabled(true);
		emailEditText.setEnabled(true);
		passwordEditText.setEnabled(true);
		signUpButton.setEnabled(true);
		
		actionBar.setHomeButtonEnabled(true);
	}
	
	
	
	private void disableFields()
	{
		firstNameEditText.setEnabled(false);
		lastNameEditText.setEnabled(false);
		emailEditText.setEnabled(false);
		passwordEditText.setEnabled(false);
		signUpButton.setEnabled(false);
		
		actionBar.setHomeButtonEnabled(false);
	}
	
	
	
	private void showLoadingSpinner() 
	{
		signUpButtonProgressBar.setVisibility(View.VISIBLE);
		signUpButtonTextView.setText(getString(R.string.sign_up_loading));
	}
	
	
	
	private void hideLoadingSpinner()
	{
		signUpButtonProgressBar.setVisibility(View.GONE);
		signUpButtonTextView.setText(getString(R.string.sign_up));
	}
	
	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getString(R.string.sign_up));

		firstNameEditText = (EditText) findViewById(R.id.signup_firstname_edittext);
		lastNameEditText = (EditText) findViewById(R.id.signup_lastname_edittext);
		emailEditText = (EditText) findViewById(R.id.signup_email_edittext);
		passwordEditText = (EditText) findViewById(R.id.signup_password_edittext);
		
		firstnameErrorTextView = (TextView) findViewById(R.id.signup_error_firstname_textview);
		lastnameErrorTextView = (TextView) findViewById(R.id.signup_error_lastname_textview);
		emailErrorTextView = (TextView) findViewById(R.id.signup_error_email_textview);
		passwordErrorTextView = (TextView) findViewById(R.id.signup_error_password_textview);
		
		firstnameErrorTextView.setText(getString(R.string.signup_with_email_error_firstname));
		lastnameErrorTextView.setText(getString(R.string.signup_with_email_error_lastname));
		emailErrorTextView.setText(getString(R.string.signup_with_email_error_email_already_registered));
		passwordErrorTextView.setText(getString(R.string.signup_with_email_error_passwordlength));
		
		signUpButton = (RelativeLayout) findViewById(R.id.signup_register_button);
		signUpButton.setOnClickListener(this);
		signUpButtonProgressBar = (ProgressBar) findViewById(R.id.mitv_sign_up_progressbar);
		signUpButtonTextView = (FontTextView) findViewById(R.id.mitv_sign_up_button_tv);

		// Show software keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}
	
	
	
	private boolean isFirstnameValid() 
	{
		String name = firstNameEditText.getText().toString();
		
		boolean isValid = RegularExpressionUtils.checkUserFirstname(name);
		
		return isValid;
	}
	
	
	
	private boolean isLastnameValid() 
	{
		String name = lastNameEditText.getText().toString();
		
		boolean isValid = RegularExpressionUtils.checkUserLastname(name);
		
		return isValid;
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
		/* If we ever want to add tabs to this view, tabs wont work if not calling super. Does not break anything if we dont have tabs though. */
		super.onClick(v);
		
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.signup_register_button:
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