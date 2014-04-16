
package com.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;
import com.mitv.utilities.RegularExpressionUtils;



public class ResetPasswordSendEmailActivity 
	extends BaseActivity 
	implements OnClickListener 
{
	private static final String TAG = ResetPasswordSendEmailActivity.class.getName();

	
	private RelativeLayout resetPasswordButton;
	private ProgressBar resetPasswordButtonProgressBar;
	private FontTextView resetPasswordButtonTextView;
	
	private EditText emailResetPasswordEditText;
	private TextView errorTextView;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_resetpassword_activity);

		initViews();
		
		registerAsListenerForRequest(RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_EMAIL);
	}

	
	
	@Override
	protected void loadData() 
	{	
		String email = emailResetPasswordEditText.getText().toString();
		
		if(!TextUtils.isEmpty(email) && RegularExpressionUtils.checkEmail(email)) 
		{
			emailResetPasswordEditText.setEnabled(false);
		
			updateUI(UIStatusEnum.LOADING);
			
			boolean isConnected = NetworkUtils.isConnected();

			if (isConnected) 
			{
				ContentManager.sharedInstance().performResetPassword(this, email);
			}
			else
			{
				updateUI(UIStatusEnum.FAILED);
			}
		}
		else
		{
			emailResetPasswordEditText.setEnabled(true);
			
			errorTextView.setText(getString(R.string.signup_with_email_error_email_incorrect));
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
			updateUI(UIStatusEnum.FAILED);
		}
	}

	
	
	@Override
	protected void updateUI(UIStatusEnum status)
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{
			case LOADING:
			{
				disableFields();
				showLoadingSpinner();
				break;
			}
		
			case SUCCESS_WITH_CONTENT: 
			{
				enableFields();
				hideLoadingSpinner();
				Intent intent = new Intent(ResetPasswordSendEmailActivity.this, ResetPasswordConfirmationActivity.class);
				startActivity(intent);
				break;
			}
			
			case FAILED:
			{
				enableFields();
				hideLoadingSpinner();
				emailResetPasswordEditText.setEnabled(true);
				
				String message;
				
				boolean isConnected = NetworkUtils.isConnected();

				if (isConnected) 
				{
					message = getString(R.string.reset_password__email_does_not_exists);
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
				hideLoadingSpinner();
				break;
			}
		}
	}
	
	
	private void enableFields()
	{
		emailResetPasswordEditText.setEnabled(true);
		resetPasswordButton.setEnabled(true);
		
		actionBar.setHomeButtonEnabled(true);
	}
	
	
	
	private void disableFields()
	{
		emailResetPasswordEditText.setEnabled(false);
		resetPasswordButton.setEnabled(false);

		actionBar.setHomeButtonEnabled(false);
	}
	
	
	private void showLoadingSpinner() 
	{
		resetPasswordButtonProgressBar.setVisibility(View.VISIBLE);
		resetPasswordButtonTextView.setText(getString(R.string.reset_password_loading));
	}
	
	
	
	private void hideLoadingSpinner()
	{
		resetPasswordButtonProgressBar.setVisibility(View.GONE);
		resetPasswordButtonTextView.setText(getString(R.string.submit));
	}

	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getString(R.string.reset_password));

		resetPasswordButton = (RelativeLayout) findViewById(R.id.mitv_reset_password_button);
		resetPasswordButton.setOnClickListener(this);
		resetPasswordButtonProgressBar = (ProgressBar) findViewById(R.id.mitv_reset_password_progressbar);
		resetPasswordButtonTextView = (FontTextView) findViewById(R.id.mitv_reset_password_button_tv);
		
		emailResetPasswordEditText = (EditText) findViewById(R.id.resetpassword_email_edittext);

		errorTextView = (TextView) findViewById(R.id.resetpassword_error_tv);
	}

	
	
	@Override
	public void onClick(View v) 
	{
		/*
		 * IMPORTANT always call super.onClick for subclasses of BaseActivity,
		 * else tabs wont work!
		 */
		super.onClick(v);
		
		int id = v.getId();

		switch (id) 
		{
			case R.id.mitv_reset_password_button:
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