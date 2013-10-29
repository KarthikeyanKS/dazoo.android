package com.millicom.secondscreen.content.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.SSPageFragmentActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.content.search.SearchPageActivity;

public class ActivityActivity extends SSPageFragmentActivity implements OnClickListener {

	private static final String	TAG	= "ActivityActivity";
	private TextView			mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private ActionBar			mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		initViews();

		// TO BE DETERMINED LATER
		// loadPage();
	}

	private void initViews() {
		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.orange));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));

		mActionBar = getSupportActionBar();

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.activity_title));

		// DO THE CALLBACK AND LOADING FUNCTIONALITY AND BEHAVIOR
		// super.initCallbackLayouts();
	}

	@Override
	protected void loadPage() {
	}

	@Override
	protected boolean pageHoldsData() {
		return false;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_search:
			Intent toSearchPage = new Intent(ActivityActivity.this, SearchPageActivity.class);
			startActivity(toSearchPage);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_homepage, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.show_tvguide:
			// tab to home page
			Intent intentHome = new Intent(ActivityActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.show_activity:
			// we are here: do nothing
			break;
		case R.id.show_me:
			// tab to activity page
			Intent intentMe = new Intent(ActivityActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			break;
		}
	}
}
