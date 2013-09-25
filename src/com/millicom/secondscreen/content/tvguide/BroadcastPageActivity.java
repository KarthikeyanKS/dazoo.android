package com.millicom.secondscreen.content.tvguide;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;

public class BroadcastPageActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_broadcastpage_activity);
		
		// get the info about the program to be displayed from tv-guide listview
		Intent intent = getIntent(); 
		
		initViews();
	}

	private void initViews() {
		// styling the Action Bar
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setCustomView(R.layout.layout_actionbar_programpage);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}
