package com.mitv.authentication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.content.SSActivity;

public class ResetPasswordFinalActivity extends SSSignInSignupBaseActivity implements OnClickListener {

	private static final String	TAG	= "ResetPasswordFinalActivity";
	private ActionBar			mActionBar;
	private Button				mLoginBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_resetpasswordfinal_activity);

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

		final int actionBarColor = getResources().getColor(R.color.blue1);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setTitle(getResources().getString(R.string.reset_password));

		mLoginBtn = (Button) findViewById(R.id.resetpassword_already_login_btn);
		mLoginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.resetpassword_already_login_btn:
			Intent intentSignIn = new Intent(ResetPasswordFinalActivity.this, SignInActivity.class);
			startActivity(intentSignIn);
			break;
		}

	}

}