
package com.millicom.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseLoginActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.utilities.RegularExpressionUtils;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.FontTextView;
import com.mitv.customviews.TextDrawable;
import com.mitv.customviews.ToastHelper;



public class SignUpWithEmailActivity 
	extends BaseLoginActivity 
	implements OnClickListener
{
	private static final String TAG = SignUpWithEmailActivity.class.getName();

	
	private FontTextView termsOfService;
	private TextDrawable emailTextDrawable;
	private TextDrawable passwordTextDrawable;
	
	
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private EditText passwordEditText;
	
	private Button signUpButton;
	
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
		
		ContentManager.sharedInstance().performSignUp(this, email, password, firstname, lastname);
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
				break;
			}
			
			case FAILED_VALIDATION:
			{
				if(isInvalidEmail)
				{
					emailErrorTextView.setVisibility(View.VISIBLE);
					emailErrorTextView.setText(getResources().getString(R.string.signup_with_email_login_wrong_format_email));
					emailEditText.setBackgroundResource(R.drawable.edittext_activated);
					emailEditText.requestFocus();
				}
				else if(isInvalidPassword)
				{
					passwordErrorTextView.setVisibility(View.VISIBLE);
					passwordErrorTextView.setBackgroundResource(R.drawable.edittext_activated);
					passwordErrorTextView.requestFocus();
				}
				else if(isInvalidFirstname)
				{
					firstnameErrorTextView.setVisibility(View.VISIBLE);
					firstnameErrorTextView.setBackgroundResource(R.drawable.edittext_activated);
					firstnameErrorTextView.requestFocus();
				}
				else if(isInvalidLastname)
				{
					lastnameErrorTextView.setVisibility(View.VISIBLE);
					lastnameErrorTextView.setBackgroundResource(R.drawable.edittext_activated);
					lastnameErrorTextView.requestFocus();
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
				
				Intent intent = new Intent(SignUpWithEmailActivity.this, getReturnActivity());

				intent.putExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, true);

				startActivity(intent);
				
				finish();
				
				break;
			}
			
			case FAILED:
			{
				switch (fetchRequestResult) 
				{
					case USER_SIGN_UP_EMAIL_ALREADY_TAKEN:
					{
						emailErrorTextView.setVisibility(View.VISIBLE);
						emailErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_already_registered));
						
						emailEditText.setBackgroundResource(R.drawable.edittext_activated);
						emailEditText.requestFocus();
						break;
					}
					
					case USER_SIGN_UP_EMAIL_IS_INVALID:
					{
						emailErrorTextView.setVisibility(View.VISIBLE);
						emailErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_incorrect));
						
						emailEditText.setBackgroundResource(R.drawable.edittext_activated);
						emailEditText.requestFocus();
						break;
					}
					
					case USER_SIGN_UP_PASSWORD_TOO_SHORT:
					{
						passwordErrorTextView.setVisibility(View.VISIBLE);
						
						StringBuilder sb = new StringBuilder();
						sb.append(getResources().getString(R.string.signup_with_email_error_passwordlength));
						sb.append(" ");
						sb.append(Consts.PASSWORD_LENGTH_MIN);
						sb.append(" ");
						sb.append(getResources().getString(R.string.signup_with_email_characters));
						
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
						ToastHelper.createAndShowToast(this, "Login was unsuccessful.");
						break;
					}
				}
				
				enableFields();
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
	}
	
	
	
	private void disableFields()
	{
		firstNameEditText.setEnabled(false);
		lastNameEditText.setEnabled(false);
		emailEditText.setEnabled(false);
		passwordEditText.setEnabled(false);
		signUpButton.setEnabled(false);
	}
	
	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.sign_up));

		firstNameEditText = (EditText) findViewById(R.id.signup_firstname_edittext);
		lastNameEditText = (EditText) findViewById(R.id.signup_lastname_edittext);
		emailEditText = (EditText) findViewById(R.id.signup_email_edittext);
		passwordEditText = (EditText) findViewById(R.id.signup_password_edittext);
		
		firstnameErrorTextView = (TextView) findViewById(R.id.signup_error_firstname_textview);
		lastnameErrorTextView = (TextView) findViewById(R.id.signup_error_lastname_textview);
		emailErrorTextView = (TextView) findViewById(R.id.signup_error_email_textview);
		passwordErrorTextView = (TextView) findViewById(R.id.signup_error_password_textview);
		
		firstnameErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_firstname));
		lastnameErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_lastname));
		emailErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_already_registered));
		passwordErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_passwordlength));
		
		signUpButton = (Button) findViewById(R.id.signup_register_button);
		signUpButton.setOnClickListener(this);

		termsOfService = (FontTextView) findViewById(R.id.signup_terms_link);

		String linkText = getString(R.string.terms_link);
		
		termsOfService.setText(Html.fromHtml(linkText));
		termsOfService.setMovementMethod(LinkMovementMethod.getInstance());
		
		stripUnderlines(termsOfService);

		setTextWatchers();
	}
	
	
	
	private boolean isFirstnameValid() 
	{
		String email = firstNameEditText.getText().toString();
		
		boolean isValid = RegularExpressionUtils.checkUserFirstname(email);
		
		return isValid;
	}
	
	
	
	private boolean isLastnameValid() 
	{
		return true;
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
	
	
	
	private class URLSpanWithoutUnderline extends URLSpan 
	{
		public URLSpanWithoutUnderline(String url) 
		{
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds)
		{
			super.updateDrawState(ds);
			
			ds.setUnderlineText(false);
		}
	}

	
	
	// TODO: What is this and why is it needed?
	private void stripUnderlines(TextView textView) 
	{
		Spannable s = (Spannable) textView.getText();
		
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		
		for (URLSpan span : spans) 
		{
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			
			s.removeSpan(span);
			
			span = new URLSpanWithoutUnderline(span.getURL());
			
			s.setSpan(span, start, end, 0);
		}
		
		textView.setText(s);
	}

	
	
	private void setTextWatchers() 
	{
		passwordTextDrawable = new TextDrawable(this);
		passwordTextDrawable.setText(getResources().getString(R.string.signup_characters));
		passwordTextDrawable.setTextColor(getResources().getColor(R.color.grey2));
		
		passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, passwordTextDrawable, null);
		
		passwordEditText.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void afterTextChanged(Editable s) 
			{
				if (passwordEditText.getText().toString().equals(""))
				{
					passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, passwordTextDrawable, null);
				} 
				else 
				{
					passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		
		emailTextDrawable = new TextDrawable(this);
		emailTextDrawable.setText(getResources().getString(R.string.signup_email_example));
		emailTextDrawable.setTextColor(getResources().getColor(R.color.grey2));
		
		emailEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, emailTextDrawable, null);
		
		emailEditText.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void afterTextChanged(Editable s) 
			{
				if (emailEditText.getText().toString().equals(""))
				{
					emailEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, emailTextDrawable, null);
				} 
				else 
				{
					emailEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
	}

	
	
	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.signup_register_button:
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