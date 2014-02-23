package com.millicom.mitv.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.millicom.mitv.enums.TabSelectedEnum;
import com.millicom.mitv.utilities.GenericUtils;
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.manager.GATrackingManager;

public abstract class BaseActivity extends ActionBarActivity implements OnClickListener{

	private static final String TAG = BaseActivity.class.getName();

	protected RelativeLayout tabTvGuide;
	protected RelativeLayout tabActivity;
	protected RelativeLayout tabProfile;
	protected View tabDividerLeft;
	protected View tabDividerRight;
	
	private String viewName;
	private View requestEmptyLayout;
	private View requestFailedLayout;
	private View requestLoadingLayout;
	private Button requestFailedButton;
	private Button requestBadButton;
	private View requestBadLayout;
	protected ActionBar actionBar;
	private Activity activity;
	private TabSelectedEnum tabSelectedEnum = TabSelectedEnum.TV_GUIDE;
	
	protected abstract void updateUI(REQUEST_STATUS status);

	protected abstract void loadPage();
	
	public void initTabViews() {
		tabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		tabTvGuide.setOnClickListener(this);

		tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		tabActivity.setOnClickListener(this);

		tabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		tabProfile.setOnClickListener(this);

		tabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		tabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		tabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		tabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));

		switch (tabSelectedEnum) {
		case TV_GUIDE: {
			tabSelectedWasTVGuide();
			break;
		}
		case ACTIVITY_FEED: {
			tabSelectedWasActivityFeed();
			break;
		}
		case MY_PROFILE: {
			tabSelectedWasMyProfile();
			break;
		}
		}
	}
	
	private void tabSelectedWasTVGuide() {
		tabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
	}
	
	private void tabSelectedWasActivityFeed() {
		tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
	}
	
	private void tabSelectedWasMyProfile() {
		tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabProfile.setBackgroundColor(getResources().getColor(R.color.red));
	}
	

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.tab_tv_guide: {
			if (!(this instanceof HomeActivity)) {
				tabSelectedEnum = TabSelectedEnum.TV_GUIDE;
				Intent intentActivity = new Intent(this, HomeActivity.class);
				startActivity(intentActivity);
			}
			break;
		}
		case R.id.tab_activity: {
			if (!(this instanceof ActivityActivity)) {
				tabSelectedEnum = TabSelectedEnum.ACTIVITY_FEED;
				Intent intentActivity = new Intent(this, ActivityActivity.class);
				startActivity(intentActivity);
				break;
			}
		}
		case R.id.tab_me: {
			if (!(this instanceof MyProfileActivity)) {
				tabSelectedEnum = TabSelectedEnum.MY_PROFILE;
				Intent intentMe = new Intent(this, MyProfileActivity.class);
				startActivity(intentMe);
				break;
			}
		}
		case R.id.request_failed_reload_button: {
			if (!NetworkUtils.isConnectedAndHostIsReachable(activity)) {
				updateUI(REQUEST_STATUS.FAILED);
			} else {
				loadPage();
			}
			break;
		}
		case R.id.bad_request_reload_button: {
			if (!NetworkUtils.isConnectedAndHostIsReachable(activity)) {
				updateUI(REQUEST_STATUS.FAILED);
			} else {
				loadPage();
			}
			break;
		}
		}
	}
	
	
	

	@Override
	protected void onResume() {
		super.onResume();
		initTabViews();
	}


	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		PackageInfo packageInfo = GenericUtils.getPackageInfo(this);

		int flags = packageInfo.applicationInfo.flags;

		boolean isDebugMode = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

		if(isDebugMode) 
		{
			// TODO Enable strict mode
//			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//			.detectDiskReads()
//			.detectDiskWrites()
//			.detectNetwork()   // or .detectAll() for all detectable problems
//			.penaltyLog()
//			.build());
//
//			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//			.detectLeakedSqlLiteObjects()
//			.detectLeakedClosableObjects()
//			.penaltyLog()
//			.penaltyDeath()
//			.build());
		}
		// No need for else

		super.onCreate(savedInstanceState);
		
	
		// add to the list of running activities
		SecondScreenApplication.sharedInstance().getActivityList().add(this);
		
		

		/* Google Analytics tracking */
		this.viewName = this.getClass().getName();

		EasyTracker.getInstance(this).activityStart(this);

		GATrackingManager.sendView(viewName);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);

		MenuItem searchIcon = menu.findItem(R.id.action_start_search);
		View seachIconView = MenuItemCompat.getActionView(searchIcon);
		seachIconView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent toSearchPage = new Intent(BaseActivity.this, SearchPageActivity.class);
				startActivity(toSearchPage);

			}
		});

		MenuItem searchFieldItem = menu.findItem(R.id.searchfield);
		searchFieldItem.setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items

		// hide search for beta release
		switch (item.getItemId()) {
		case R.id.action_start_search: // Might be dead with actionView instead of icon...
			Intent toSearchPage = new Intent(BaseActivity.this, SearchPageActivity.class);
			startActivity(toSearchPage);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		/* Google Analytics tracking */
		GATrackingManager.stopTrackingView(this.viewName);
		EasyTracker.getInstance(this).activityStop(this);
	}

	// Init the callback layouts for this page
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		activity = this;
		actionBar = getSupportActionBar();
	
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue1)));

		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		initCallbackLayouts();
	}

	// Check if activity is still running
	public boolean activityIsAlive() {
		return ((this != null && !this.isFinishing()));
	}

	protected boolean requestIsSuccesfull(REQUEST_STATUS status) {

		if (activityIsAlive()) {

			// Set initial state of layouts
			hideRequestStatusLayouts();

			switch (status) {
			case BAD_REQUEST:
				requestBadLayout.setVisibility(View.VISIBLE);
				break;

			case EMPTY_RESPONSE:
				requestEmptyLayout.setVisibility(View.VISIBLE);
				break;
			case FAILED:
				requestFailedLayout.setVisibility(View.VISIBLE);
				break;

			case LOADING:
				requestLoadingLayout.setVisibility(View.VISIBLE);
				break;

			case SUCCESSFUL:
				return true;
			}
		}
		return false;
	}

	public void hideRequestStatusLayouts() {
		if (requestFailedLayout != null)
			requestFailedLayout.setVisibility(View.GONE);
		if (requestLoadingLayout != null)
			requestLoadingLayout.setVisibility(View.GONE);
		if (requestBadLayout != null)
			requestBadLayout.setVisibility(View.GONE);
	}

	public void initCallbackLayouts() {

		requestFailedLayout = (RelativeLayout) findViewById(R.id.request_failed_main_layout);
		requestFailedButton = (Button) findViewById(R.id.request_failed_reload_button);
		if (requestFailedButton != null) {
			requestFailedButton.setOnClickListener(this);
		}

		requestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_main_layout);

		requestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);

		requestBadLayout = (RelativeLayout) findViewById(R.id.bad_request_main_layout);
		requestBadButton = (Button) findViewById(R.id.bad_request_reload_button);
		if (requestBadButton != null) {
			requestBadButton.setOnClickListener(this);
		}
	}
}
