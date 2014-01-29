package com.millicom.secondscreen.content.myprofile;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.manager.LoginManager;

public class SettingsActivity extends SSActivity implements OnClickListener {

	private static final String	TAG			= "SettingsActivity";
	private ActionBar			mActionBar;
	private boolean				mIsChange	= false;
	private Button				mContactButton, mTermsButton, mHelpButton, mLogoutButton;
	private RelativeLayout		mTabTvGuide, mTabActivity, mTabProfile;
	private View mTabDividerLeft, mTabDividerRight;
	private TextView			mVersionTextView;
	private String				mToken;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_settings_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		mToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		initLayout();
		super.initCallbackLayouts();
		populateViews();
	}

	private void initLayout() {
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(getResources().getString(R.string.settings));
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mContactButton = (Button) findViewById(R.id.settings_contact_button);
		mContactButton.setOnClickListener(this);
		mTermsButton = (Button) findViewById(R.id.settings_terms_button);
		mTermsButton.setOnClickListener(this);
		mHelpButton = (Button) findViewById(R.id.settings_help_button);
		mHelpButton.setOnClickListener(this);
		mVersionTextView = (TextView) findViewById(R.id.settings_version_textview);

		mLogoutButton = (Button) findViewById(R.id.settings_logout_button);
		mLogoutButton.setOnClickListener(this);

		if (mToken == null || mToken.length() <= 0) {
			mLogoutButton.setVisibility(View.GONE);
		}

		// styling bottom navigation tabs

		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		mTabProfile.setOnClickListener(this);
		
		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));
	}

	private void populateViews() {
		try {
			String versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
			mVersionTextView.setText(getResources().getString(R.string.settings_version) + " " + versionName);
		} catch (NameNotFoundException e) {
			mVersionTextView.setText("");
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		if (mIsChange) {
			Intent mIntent = new Intent();
			setResult(Consts.INFO_UPDATE_LOGOUT, mIntent);
		}
		super.onBackPressed();
		
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.settings_contact_button:
			Intent intentContact = new Intent(SettingsActivity.this, ContactActivity.class);
			startActivityForResult(intentContact, 0);
			
			break;
		case R.id.settings_terms_button:
			Intent intentTerms = new Intent(SettingsActivity.this, TermsActivity.class);
			startActivityForResult(intentTerms, 0);
			
			break;
		case R.id.settings_help_button:
			Intent intentHelp = new Intent(SettingsActivity.this, HelpActivity.class);
			startActivityForResult(intentHelp, 0);
			
			break;
		case R.id.settings_logout_button:
			LoginManager.logout();
			// clear all the running activities and start the application from the whole beginning
			SecondScreenApplication.getInstance().clearActivityBacktrace();

			// LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Consts.INTENT_EXTRA_LOG_OUT_ACTION));
			startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
			//
			// mIsChange = true;
			// // check if the token was really cleared
			// String dazooToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
			// // if (dazooToken.isEmpty() == true) {
			// if (TextUtils.isEmpty(dazooToken) == true) {
			// Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
			// } else {
			// Log.d(TAG, "Log out from Dazoo failed");
			// }
			//
			//
			break;
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(SettingsActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			// tab to home page
			Intent intentActivity = new Intent(SettingsActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.tab_me:
			Intent returnIntent = new Intent();
			if (mIsChange == true) {
				setResult(Consts.INFO_UPDATE_LOGOUT, returnIntent);
			}
			
			finish();
			break;
		}
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {

	}

	@Override
	protected void loadPage() {

	}
}
