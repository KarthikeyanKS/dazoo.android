package com.mitv.authentication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;

public class SignInOrSignupWithFacebookActivity extends SSSignInSignupBaseActivity {

	private ActionBar	mActionBar;
	private RelativeLayout	mFacebookContainer, mSignUpContainer;
	private Button			mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_signin_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

		mActionBar.setTitle(getResources().getString(R.string.sign_in));

		mFacebookContainer = (RelativeLayout) findViewById(R.id.signin_facebook_container);
		mFacebookContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignInOrSignupWithFacebookActivity.this, FacebookLoginActivity.class);
				startActivity(intent);
				finish();
			}
		});

		mSignUpContainer = (RelativeLayout) findViewById(R.id.signin_signup_email_container);
		mSignUpContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignInOrSignupWithFacebookActivity.this, SignUpWithEmailActivity.class);
				startActivity(intent);
			}
		});

		mLoginButton = (Button) findViewById(R.id.signin_login_btn);
		mLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignInOrSignupWithFacebookActivity.this, MiTVLoginActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
