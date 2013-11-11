package com.millicom.secondscreen.content.myprofile;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.authentication.LoginActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG			= "SettingsActivity";
	private ActionBar			mActionBar;
	private boolean				mIsChange	= false;
	private Button				mContactButton, mTermsButton, mHelpButton, mLogoutButton;
	private View				mTabSelectorContainerView;
	private TextView			mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed, mVersionTextView;
	private String 				mToken;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_settings_activity);
		mToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		initLayout();
		populateViews();
	}

	private void initLayout() {
		mActionBar = getSupportActionBar();
		SpannableString s = new SpannableString(getResources().getString(R.string.settings));
		// s.setSpan(new TypefaceSpan(this, "AvenirBlack"),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.settings));
		
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
		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);

		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.orange));
	}
	
	private void populateViews() {
		try {
			String versionName = getApplicationContext().getPackageManager()
				    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
			mVersionTextView.setText(getResources().getString(R.string.settings_version) + " " + versionName);
		} 
		catch (NameNotFoundException e) {
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
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
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
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.settings_terms_button:
			Intent intentTerms = new Intent(SettingsActivity.this, TermsActivity.class);
			startActivityForResult(intentTerms, 0);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.settings_help_button:
			Intent intentHelp = new Intent(SettingsActivity.this, HelpActivity.class);
			startActivityForResult(intentHelp, 0);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.settings_logout_button:
			((SecondScreenApplication) getApplicationContext()).setAccessToken(Consts.EMPTY_STRING);
			((SecondScreenApplication) getApplicationContext()).setUserFirstName(Consts.EMPTY_STRING);
			((SecondScreenApplication) getApplicationContext()).setUserLastName(Consts.EMPTY_STRING);
			((SecondScreenApplication) getApplicationContext()).setUserEmail(Consts.EMPTY_STRING);
			((SecondScreenApplication) getApplicationContext()).setUserId(Consts.EMPTY_STRING);
			((SecondScreenApplication) getApplicationContext()).setUserExistringFlag(false);

			startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			// clear the activity stack
			finish();
			mIsChange = true;
			// check if the token was really cleared
			String dazooToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
			// if (dazooToken.isEmpty() == true) {
			if (TextUtils.isEmpty(dazooToken) == true) {
				Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
			} else {
				Log.d(TAG, "Log out from Dazoo failed");
			}
			break;
		case R.id.show_tvguide:
			// tab to home page
			Intent intentHome = new Intent(SettingsActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.show_activity:
			// tab to home page
			Intent intentActivity = new Intent(SettingsActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			break;
		case R.id.show_me:
			Intent returnIntent = new Intent();
			if (mIsChange == true) {
				setResult(Consts.INFO_UPDATE_LOGOUT, returnIntent);
			}
			finish();
			break;
		}
	}
}
