package com.millicom.secondscreen.authentication;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.myprofile.MyChannelsActivity;
//import com.testflightapp.lib.TestFlight;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class FacebookDazooLoginActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG	= "FacebookDazooLoginActivity";
	private ActionBar			mActionBar;
	private RelativeLayout		mSetChannels, mConnectWithFriends, mTakeTour, mSkip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_facebook_dazoo_login_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
		initViews();
		//TestFlight.passCheckpoint("FIRST TIME WITH THE FACEBOOK LOGIN");
	}

	private void initViews() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		final int actionBarColor = getResources().getColor(R.color.blue1);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setTitle(getResources().getString(R.string.app_name));

		mSetChannels = (RelativeLayout) findViewById(R.id.facebook_dazoo_set_channels_container);
		mSetChannels.setOnClickListener(this);
		mConnectWithFriends = (RelativeLayout) findViewById(R.id.facebook_dazoo_connect_with_friends_container);
		mConnectWithFriends.setOnClickListener(this);
		mTakeTour = (RelativeLayout) findViewById(R.id.facebook_dazoo_take_tour_container);
		mTakeTour.setOnClickListener(this);
		mSkip = (RelativeLayout) findViewById(R.id.facebook_dazoo_skip_container);
		mSkip.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.facebook_dazoo_set_channels_container:
			Intent intent = new Intent(FacebookDazooLoginActivity.this, MyChannelsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.facebook_dazoo_connect_with_friends_container:
			// NOT IN IN MVP
			// nothing here yet
			break;
		case R.id.facebook_dazoo_take_tour_container:
			// TOUR: to be determined
			break;
		case R.id.facebook_dazoo_skip_container:
			// go directly to Start page
			Intent intentHome = new Intent(FacebookDazooLoginActivity.this, HomeActivity.class);
			startActivity(intentHome);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			finish();
			break;
		}

	}
}
