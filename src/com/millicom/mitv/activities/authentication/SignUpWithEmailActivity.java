package com.millicom.mitv.activities.authentication;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
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

import com.millicom.mitv.activities.base.BaseLoginActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.asynctasks.MiTVRegistrationTask;
import com.mitv.customviews.FontTextView;
import com.mitv.customviews.TextDrawable;

public class SignUpWithEmailActivity extends BaseLoginActivity implements OnClickListener {
	private static final String TAG = SignUpWithEmailActivity.class.getName();

	private TextView firstnameErrorTextView;
	private TextView lastnameErrorTextView;
	private TextView emailErrorTextView;
	private TextView passwordErrorTextView;
	private FontTextView termsWebLink;
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText passwordRegisterEditText;
	private EditText emailRegisterEditText;
	private Button miTVRegisterButton;
	private TextDrawable emailTextDrawable;
	private TextDrawable passwordTextDrawable;

	private String userEmailRegister;
	private String userPasswordRegister;
	private String userFirstNameRegister;
	private String userLastNameRegister;
	private String mBadResponseString;
	private boolean isFromActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_signup_activity);

		Intent intent = getIntent();

		if (intent.hasExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY)) {
			isFromActivity = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_FROM_ACTIVITY);
		}

		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void loadData() {
		// TODO NewArc - Do something here?
	}

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		if (fetchRequestResult.wasSuccessful()) {
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		} else {
			updateUI(UIStatusEnum.FAILED);
		}
	}

	@Override
	protected void updateUI(UIStatusEnum status) {
		super.updateUIBaseElements(status);

		switch (status) {
		case SUCCEEDED_WITH_DATA: {
			// TODO NewArc - Do something here?
			break;
		}

		default: {
			// TODO NewArc - Do something here?
			break;
		}
		}
	}

	private class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);

			ds.setUnderlineText(false);
		}
	}

	private void stripUnderlines(TextView textView) {
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

	private void initViews() {
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.sign_up));

		firstNameEditText = (EditText) findViewById(R.id.signup_firstname_edittext);
		lastNameEditText = (EditText) findViewById(R.id.signup_lastname_edittext);
		emailRegisterEditText = (EditText) findViewById(R.id.signup_email_edittext);
		passwordRegisterEditText = (EditText) findViewById(R.id.signup_password_edittext);
		firstnameErrorTextView = (TextView) findViewById(R.id.signup_error_firstname_textview);
		lastnameErrorTextView = (TextView) findViewById(R.id.signup_error_lastname_textview);
		emailErrorTextView = (TextView) findViewById(R.id.signup_error_email_textview);
		passwordErrorTextView = (TextView) findViewById(R.id.signup_error_password_textview);
		miTVRegisterButton = (Button) findViewById(R.id.signup_register_button);
		miTVRegisterButton.setOnClickListener(this);

		termsWebLink = (FontTextView) findViewById(R.id.signup_terms_link);

		String linkText = getString(R.string.terms_link);
		termsWebLink.setText(Html.fromHtml(linkText));
		termsWebLink.setMovementMethod(LinkMovementMethod.getInstance());
		stripUnderlines(termsWebLink);

		setTextWatchers();

		// TODO: Static drawable background is not properly set, causing a flickering effect. Quickfix!
		firstNameEditText.setBackgroundResource(R.drawable.edittext_standard);
		lastNameEditText.setBackgroundResource(R.drawable.edittext_standard);
		emailRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
		passwordRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
	}

	// Sets the TextWatchers for the extra drawable hints.
	private void setTextWatchers() {
		passwordTextDrawable = new TextDrawable(this);
		passwordTextDrawable.setText(getResources().getString(R.string.signup_characters));
		passwordTextDrawable.setTextColor(getResources().getColor(R.color.grey2));
		passwordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, passwordTextDrawable, null);

		passwordRegisterEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (passwordRegisterEditText.getText().toString().equals("")) {
					passwordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, passwordTextDrawable, null);
				} else {
					passwordRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

		emailTextDrawable = new TextDrawable(this);
		emailTextDrawable.setText(getResources().getString(R.string.signup_email_example));
		emailTextDrawable.setTextColor(getResources().getColor(R.color.grey2));
		emailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, emailTextDrawable, null);

		emailRegisterEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (emailRegisterEditText.getText().toString().equals("")) {
					emailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, emailTextDrawable, null);
				} else {
					emailRegisterEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.signup_register_button:
			firstNameEditText.setEnabled(false);
			lastNameEditText.setEnabled(false);
			emailRegisterEditText.setEnabled(false);
			passwordRegisterEditText.setEnabled(false);

			emailErrorTextView.setText("");
			firstnameErrorTextView.setText("");
			lastnameErrorTextView.setText("");
			passwordErrorTextView.setText("");

			firstNameEditText.setBackgroundResource(R.drawable.edittext_standard);
			lastNameEditText.setBackgroundResource(R.drawable.edittext_standard);
			emailRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);
			passwordRegisterEditText.setBackgroundResource(R.drawable.edittext_standard);

			userEmailRegister = emailRegisterEditText.getText().toString();
			userPasswordRegister = passwordRegisterEditText.getText().toString();
			userFirstNameRegister = firstNameEditText.getText().toString();
			userLastNameRegister = lastNameEditText.getText().toString();

			MiTVRegistrationTask mitvRegisterTask = new MiTVRegistrationTask();


			//TODO NewArc use new architechure
			try {
				// mitvToken = mitvRegisterTask.execute(userEmailRegister, userPasswordRegister, userFirstNameRegister,
				// userLastNameRegister).get();
				String responseStr = mitvRegisterTask.execute(userEmailRegister, userPasswordRegister, userFirstNameRegister, userLastNameRegister).get();
				// if (responseStr != null && responseStr.isEmpty() != true) {
				if (responseStr != null && TextUtils.isEmpty(responseStr) != true) {
					JSONObject mitvRegJSON = new JSONObject(responseStr);
//					String mitvToken = mitvRegJSON.optString(Consts.API_TOKEN);

					// TODO do anything here
					// if (mitvToken.isEmpty() != true && mitvToken.length() > 0) {
					// if (mitvToken != null && TextUtils.isEmpty(mitvToken) != true) {
					// ((SecondScreenApplication) getApplicationContext()).setAccessToken(mitvToken);
					// if (AuthenticationService.storeUserInformation(this, mitvRegJSON)) {
					// //Toast.makeText(getApplicationContext(), "Hello, " + ((SecondScreenApplication)
					// getApplicationContext()).getUserFirstName(), Toast.LENGTH_SHORT).show();
					// Log.d(TAG, "Hello, " + ((SecondScreenApplication) getApplicationContext()).getUserFirstName());
					//
					// // go to Start page
					// Intent intent;
					// if (mIsFromActivity) {
					// intent = new Intent(SignUpWithEmailActivity.this, ActivityActivity.class);
					// }
					// else {
					// intent = new Intent(SignUpWithEmailActivity.this, HomeActivity.class);
					// }
					// intent.putExtra(Consts.INTENT_EXTRA_SIGN_UP_ACTION, true);
					// startActivity(intent);
					// finish();
					//
					// } else {
					// //Toast.makeText(getApplicationContext(), "Failed to fetch the user information from backend.",
					// Toast.LENGTH_SHORT).show();
					// Log.d(TAG, "!!! Failed to fetch the user information from backend !!!");
					// }
					// } else {
					// //Toast.makeText(getApplicationContext(),
					// "Error! Something went wrong while creating an account with MiTV. Please, try again later!",
					// Toast.LENGTH_LONG).show();
					// Log.d(TAG, "Error! Something went wrong while creating an account with MiTV. Please, try again later!");
					// }
				} else {
					// Toast.makeText(getApplicationContext(),
					// "Error! Something went wrong while creating an account with us. Please, try again later!",
					// Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Error! MiTV Login: level response from backend");
					firstNameEditText.setEnabled(true);
					lastNameEditText.setEnabled(true);
					emailRegisterEditText.setEnabled(true);
					passwordRegisterEditText.setEnabled(true);

					// Set error textviews and highlighting
					if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_EMAIL_ALREADY_TAKEN)) {
						emailErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_already_registered));
						emailRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						emailRegisterEditText.requestFocus();
					} else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_NOT_REAL_EMAIL)) {
						emailErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_incorrect));
						emailRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						emailRegisterEditText.requestFocus();
					} else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_PASSWORD_TOO_SHORT)) {
						passwordErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_passwordlength) + " " + Consts.PASSWORD_LENGTH_MIN + " "
								+ getResources().getString(R.string.signup_with_email_characters));
						passwordRegisterEditText.setBackgroundResource(R.drawable.edittext_activated);
						passwordRegisterEditText.requestFocus();
					} else if (mBadResponseString.equals(Consts.BAD_RESPONSE_STRING_FIRSTNAME_NOT_SUPPLIED)) {
						firstnameErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_firstname));
						firstNameEditText.setBackgroundResource(R.drawable.edittext_activated);
						firstNameEditText.requestFocus();
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
	public void onBackPressed() {
		super.onBackPressed();

		finish();
	}
}