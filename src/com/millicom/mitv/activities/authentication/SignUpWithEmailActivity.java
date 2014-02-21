
package com.millicom.mitv.activities.authentication;



import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.asynctasks.MiTVRegistrationTask;
import com.mitv.customviews.FontTextView;
import com.mitv.customviews.TextDrawable;



public class SignUpWithEmailActivity 
	extends SSSignInSignupBaseActivity 
	implements OnClickListener
{
	private static final String TAG = SignUpWithEmailActivity.class.getName();

	private ActionBar			mActionBar;
	private TextView			mFirstnameErrorTextView;
	private TextView			mLastnameErrorTextView;
	private TextView			mEmailErrorTextView;
	private TextView			mPasswordErrorTextView;
	private FontTextView		mTermsWebLink;
	private EditText			mFirstNameEditText;
	private EditText			mLastNameEditText;
	private EditText			mPasswordRegisterEditText;
	private EditText			mEmailRegisterEditText;
	private Button				mMiTVRegisterButton;
	private TextDrawable		mEmailTextDrawable;
	private TextDrawable		mPasswordTextDrawable;
	
	private String				userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister, mitvToken;
	private String 				mBadResponseString;
	private boolean				mIsFromActivity;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_signup_activity);
		
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
	protected void updateUI(REQUEST_STATUS status) 
	{
		/* Have to have this method here since SSActivity has this method abstract */
	}

	
	
	@Override
	protected void loadPage() 
	{
		/* Have to have this method here since SSActivity has this method abstract */
	}

	
	
	private class URLSpanNoUnderline extends URLSpan 
	{
		public URLSpanNoUnderline(String url) 
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

	
	
	private void stripUnderlines(TextView textView) 
	{
		Spannable s = (Spannable) textView.getText();
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		for (URLSpan span : spans) {
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			s.removeSpan(span);
			span = new URLSpanNoUnderline(span.getURL());
			s.setSpan(span, start, end, 0);
		}
		textView.setText(s);
	}

	
	
	private void initViews() 
	{
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mActionBar.setTitle(getResources().getString(R.string.sign_up));

		mFirstNameEditText = (EditText) findViewById(R.id.signup_firstname_edittext);
		mLastNameEditText = (EditText) findViewById(R.id.signup_lastname_edittext);
		mEmailRegisterEditText = (EditText) findViewById(R.id.signup_email_edittext);
		mPasswordRegisterEditText = (EditText) findViewById(R.id.signup_password_edittext);
		mFirstnameErrorTextView = (TextView) findViewById(R.id.signup_error_firstname_textview);
		mLastnameErrorTextView = (TextView) findViewById(R.id.signup_error_lastname_textview);
		mEmailErrorTextView = (TextView) findViewById(R.id.signup_error_email_textview);
		mPasswordErrorTextView = (TextView) findViewById(R.id.signup_error_password_textview);
		mMiTVRegisterButton = (Button) findViewById(R.id.signup_register_button);
		mMiTVRegisterButton.setOnClickListener(this);
		
		mTermsWebLink = (FontTextView) findViewById(R.id.signup_terms_link);

		String linkText = getString(R.string.terms_link);
		mTermsWebLink.setText(Html.fromHtml(linkText));
		mTermsWebLink.setMovementMethod(LinkMovementMethod.getInstance());
		stripUnderlines(mTermsWebLink);

		// setTextWatchers();

		// TODO: Static drawable background is not properly set, causing a flickering effect. Quickfix!
		mFirstNameEditText.setBackgroundResource(R.drawable.edittext_standard);
		mLastNameEditText.setBackgroundResource(R.drawable.edittext_standard);
		mEmailRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
		mPasswordRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
	}

	
	
	// Sets the TextWatchers for the extra drawable hints.
	private void setTextWatchers() 
	{
		mPasswordTextDrawable = new TextDrawable(this);
		mPasswordTextDrawable.setText(getResources().getString(R.string.signup_characters));
		mPasswordTextDrawable.setTextColor(getResources().getColor(R.color.grey2));
		mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mPasswordTextDrawable, null);
		
		mPasswordRegisterEditText.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void afterTextChanged(Editable s) {
				if (mPasswordRegisterEditText.getText().toString().equals(""))
				{
					mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mPasswordTextDrawable, null);
				} 
				else 
				{
					mPasswordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		
		mEmailTextDrawable = new TextDrawable(this);
		mEmailTextDrawable.setText(getResources().getString(R.string.signup_email_example));
		mEmailTextDrawable.setTextColor(getResources().getColor(R.color.grey2));
		mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mEmailTextDrawable, null);
		
		mEmailRegisterEditText.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void afterTextChanged(Editable s) 
			{
				if (mEmailRegisterEditText.getText().toString().equals(""))
				{
					mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mEmailTextDrawable, null);
				} 
				else 
				{
					mEmailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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
			mFirstNameEditText.setEnabled(false);
			mLastNameEditText.setEnabled(false);
			mEmailRegisterEditText.setEnabled(false);
			mPasswordRegisterEditText.setEnabled(false);
			
			mEmailErrorTextView.setText("");
			mFirstnameErrorTextView.setText("");
			mLastnameErrorTextView.setText("");
			mPasswordErrorTextView.setText("");

			mFirstNameEditText.setBackgroundResource(R.drawable.edittext_standard);
			mLastNameEditText.setBackgroundResource(R.drawable.edittext_standard);
			mEmailRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
			mPasswordRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);

			userEmailRegister = mEmailRegisterEditText.getText().toString();
			userPasswordRegister = mPasswordRegisterEditText.getText().toString();
			userFirstNameRegister = mFirstNameEditText.getText().toString();
			userLastNameRegister = mLastNameEditText.getText().toString();

			MiTVRegistrationTask mitvRegisterTask = new MiTVRegistrationTask();
			
			try 
			{
				// mitvToken = mitvRegisterTask.execute(userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister).get();
				String responseStr = mitvRegisterTask.execute(userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister).get();
				// if (responseStr != null && responseStr.isEmpty() != true) {
				if (responseStr != null && TextUtils.isEmpty(responseStr) != true) {
					JSONObject mitvRegJSON = new JSONObject(responseStr);
					mitvToken = mitvRegJSON.optString(Consts.API_TOKEN);
					Log.d(TAG, "mitvToken: " + mitvToken + "is saved");

					//TODO do anything here
					// if (mitvToken.isEmpty() != true && mitvToken.length() > 0) {
//					if (mitvToken != null && TextUtils.isEmpty(mitvToken) != true) {
//						((SecondScreenApplication) getApplicationContext()).setAccessToken(mitvToken);
//						if (AuthenticationService.storeUserInformation(this, mitvRegJSON)) {
//							//Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
//							Log.d(TAG, "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName());
//
//							// go to Start page
//							Intent intent;
//							if (mIsFromActivity) {
//								intent = new Intent(SignUpWithEmailActivity.this, ActivityActivity.class);
//							}
//							else {
//								intent = new Intent(SignUpWithEmailActivity.this, HomeActivity.class);
//							}
//							intent.putExtra(Consts.INTENT_EXTRA_SIGN_UP_ACTION, true);
//							startActivity(intent);
//							finish();
//
//						} else {
//							//Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend.", Toast.LENGTH_SHORT).show();
//							Log.d(TAG, "!!! Failed to fetch the user information from backend !!!");
//						}
//					} else {
//						//Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with MiTV. Please, try again later!", Toast.LENGTH_LONG).show();
//						Log.d(TAG, "Error! Something went wrong while creating an account with MiTV. Please, try again later!");
//					}
				} 
				else 
				{
					//						Toast.makeText(getApplicationContext(), "Error! Something went wrong while creating an account with us. Please, try again later!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Error! MiTV Login: level response from backend");
					mFirstNameEditText.setEnabled(true);
					mLastNameEditText.setEnabled(true);
					mEmailRegisterEditText.setEnabled(true);
					mPasswordRegisterEditText.setEnabled(true);

					// Set error textviews and highlighting
					if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_EMAIL_ALREADY_TAKEN)) {
						mEmailErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_already_registered));
						mEmailRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						mEmailRegisterEditText.requestFocus();
					}
					else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_NOT_REAL_EMAIL)) {
						mEmailErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_incorrect));
						mEmailRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						mEmailRegisterEditText.requestFocus();
					}
					else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_PASSWORD_TOO_SHORT)) {
						mPasswordErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_passwordlength) + " " 
							+ Consts.PASSWORD_LENGTH_MIN + " "
							+ getResources().getString(R.string.signup_with_email_characters));
						mPasswordRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						mPasswordRegisterEditText.requestFocus();
					}
					else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_FIRSTNAME_NOT_SUPPLIED)) {
						mFirstnameErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_firstname));
						mFirstNameEditText.setBackgroundResource(R.drawable.edittext_activated);
						mFirstNameEditText.requestFocus();
					}
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

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		
		finish();
	}
}