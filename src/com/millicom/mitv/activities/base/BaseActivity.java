
package com.millicom.mitv.activities.base;

import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.FeedActivity;
import com.millicom.mitv.activities.HomeActivity;
import com.millicom.mitv.activities.SearchPageActivity;
import com.millicom.mitv.activities.UserProfileActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.TabSelectedEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.utilities.GenericUtils;
import com.millicom.mitv.utilities.NetworkUtils;
import com.millicom.mitv.utilities.ToastHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.manager.GATrackingManager;

public abstract class BaseActivity extends ActionBarActivity implements ActivityCallbackListener, OnClickListener {
	private static final String TAG = BaseActivity.class.getName();
	private static Stack<Activity> activityStack = new Stack<Activity>();

	protected RelativeLayout tabTvGuide;
	protected RelativeLayout tabActivity;
	protected RelativeLayout tabProfile;
	protected View tabDividerLeft;
	protected View tabDividerRight;

	private View requestEmptyLayout;
	private TextView requestEmptyLayoutDetails;
	private View requestFailedLayout;
	private View requestLoadingLayout;

	private Button requestFailedButton;
	private Button requestBadButton;
	private View requestBadLayout;
	protected ActionBar actionBar;

	private TabSelectedEnum tabSelectedEnum = TabSelectedEnum.NOT_SET;

	private boolean userHasJustLoggedIn;
	private boolean userHasJustLoggedOut;
	
	

	/* Abstract Methods */

	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();

	/* This method implementation should deal with changes after the data has been fetched */
	protected abstract void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier);

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Enable debug mode */
		PackageInfo packageInfo = GenericUtils.getPackageInfo(this);
		int flags = packageInfo.applicationInfo.flags;
		boolean isDebugMode = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		if (isDebugMode) {
			// TODO NewArc Enable strict mode
			// enableStrictMode();
		}

		/* Google Analytics Tracking */
		EasyTracker.getInstance(this).activityStart(this);
		String className = this.getClass().getName();
		GATrackingManager.sendView(className);

		/* IMPORTANT add activity to activity stack */
		activityStack.push(this);
	}

	public static Activity getMostRecentTabActivity() {
		Activity mostRecentTabActivity = null;

		/* Iterate through stack, start at top of stack */
		for (int i = activityStack.size() - 1; i >= 0; --i) {
			Activity activityInStack = activityStack.get(i);

			/* Check if activityInStack is any of the three TabActivities */
			if (activityInStack instanceof HomeActivity || activityInStack instanceof FeedActivity || activityInStack instanceof UserProfileActivity) {
				mostRecentTabActivity = activityInStack;
				break;
			}
		}

		return mostRecentTabActivity;
	}
	

	
	public void initTabViews() {

		Activity mostRecentTabActivity = getMostRecentTabActivity();
		tabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);

		if (tabTvGuide != null) {
			tabTvGuide.setOnClickListener(this);
		}

		tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);

		if (tabActivity != null) {
			tabActivity.setOnClickListener(this);
		}

		tabProfile = (RelativeLayout) findViewById(R.id.tab_me);

		if (tabProfile != null) {
			tabProfile.setOnClickListener(this);
		}

		tabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);

		if (tabDividerLeft != null) {
			tabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		}

		tabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		if (tabDividerRight != null) {
			tabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		}

		if (tabSelectedEnum != TabSelectedEnum.NOT_SET) {
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

			default: {
				Log.w(TAG, "Unknown tab selected");
			}
			}
		} else {
			/* Just created the activity */
			if (mostRecentTabActivity instanceof HomeActivity) {
				tabSelectedWasTVGuide();
			} else if (mostRecentTabActivity instanceof FeedActivity) {
				tabSelectedWasActivityFeed();
			} else if (mostRecentTabActivity instanceof UserProfileActivity) {
				tabSelectedWasMyProfile();
			}
		}
	}

	private void tabSelectedWasTVGuide() {
		if (tabTvGuide != null) {
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		}

		if (tabActivity != null) {
			tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		if (tabProfile != null) {
			tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}

	private void tabSelectedWasActivityFeed() {
		if (tabTvGuide != null) {
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		if (tabActivity != null) {
			tabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		}

		if (tabProfile != null) {
			tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}

	private void tabSelectedWasMyProfile() {
		if (tabTvGuide != null) {
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		if (tabActivity != null) {
			tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		if (tabProfile != null) {
			tabProfile.setBackgroundColor(getResources().getColor(R.color.red));
		}
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
			if (!(this instanceof FeedActivity)) {
				tabSelectedEnum = TabSelectedEnum.ACTIVITY_FEED;
				Intent intentActivity = new Intent(this, FeedActivity.class);
				startActivity(intentActivity);
			}
			break;
		}

		case R.id.tab_me: {
			if (!(this instanceof UserProfileActivity)) {
				tabSelectedEnum = TabSelectedEnum.MY_PROFILE;
				Intent intentMe = new Intent(this, UserProfileActivity.class);
				startActivity(intentMe);
			}
			break;
		}

		case R.id.request_failed_reload_button: {
			if (!NetworkUtils.isConnectedAndHostIsReachable()) {
				updateUI(UIStatusEnum.FAILED);
			} else {
				loadData();
			}
		}
		case R.id.bad_request_reload_button: {
			if (!NetworkUtils.isConnectedAndHostIsReachable()) {
				updateUI(UIStatusEnum.FAILED);
			} else {
				loadData();
			}

			break;
		}
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();

		initCallbackLayouts();

		initTabViews();

		Intent intent = getIntent();
		
		/* Log in states */
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if (isLoggedIn) {

			if (intent.hasExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN)) {
				userHasJustLoggedIn = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, false);

				intent.removeExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN);
			} else {
				userHasJustLoggedIn = false;
			}
		}
		else 
		{
			userHasJustLoggedIn = false;
			
			if (intent.hasExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT)) 
			{
				userHasJustLoggedOut = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT, false);

				intent.removeExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT);
			} 
			else 
			{
				userHasJustLoggedOut = false;
			}
			
		}
		
		if (isLoggedIn && userHasJustLoggedIn) 
		{
			StringBuilder sb = new StringBuilder();
			sb.append(getResources().getString(R.string.hello));
			sb.append(" ");
			sb.append(ContentManager.sharedInstance().getFromCacheUserFirstname());

			ToastHelper.createAndShowToast(this, sb.toString());
		}
		else
		{
			if(userHasJustLoggedOut)
			{
				StringBuilder sb = new StringBuilder();
				// TODO - Hardcoded string
				sb.append("logout");

				ToastHelper.createAndShowToast(this, sb.toString());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
		switch (item.getItemId()) {
		case android.R.id.home: {
			/* Pressing the Home, which here is used as "up", should be same as pressing back */
			onBackPressed();
			return true;
		}

		case R.id.action_start_search: // Might be dead with actionView instead of icon...
		{
			Intent toSearchPage = new Intent(BaseActivity.this, SearchPageActivity.class);
			startActivity(toSearchPage);

			return true;
		}

		default: {
			return super.onOptionsItemSelected(item);
		}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		String className = this.getClass().getName();

		GATrackingManager.stopTrackingView(className);

		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		removeThisActivityFromStackIfTopOfStack();
	}
	
	
	
	@Override
	protected void onDestroy() {
		/* If we have not been removed from the activityStack, remove us */
		removeThisActivityFromStackIfTopOfStack();
		super.onDestroy();
	}

	private void removeThisActivityFromStackIfTopOfStack() {
		/* Remove activity from activitStack */
		
		/* Discussion: not sure if needed, but better safe than sorry */
		Activity activityInTopOfStack = activityStack.peek();
		if(activityInTopOfStack == this) {
			/* We are in the top of the stack, remove us from the stack */
			activityStack.pop();
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue1)));
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		initCallbackLayouts();
	}

	/*
	 * This method checks for Internet connectivity on the background thread
	 */
	protected void loadDataWithConnectivityCheck() {
		updateUI(UIStatusEnum.LOADING);

		ContentManager.sharedInstance().checkNetworkConnectivity(this);
	}

	@Override
	public final void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		switch (fetchRequestResult) {
		case INTERNET_CONNECTION_AVAILABLE: {
			loadData();
			break;
		}

		case INTERNET_CONNECTION_NOT_AVAILABLE: {
			updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
			break;
		}

		default: {
			// The remaining cases should be handled by the subclasses
			onDataAvailable(fetchRequestResult, requestIdentifier);
			break;
		}
		}
	}

	protected void updateUIBaseElements(UIStatusEnum status) {
		boolean activityNotNullOrFinishing = GenericUtils.isActivityNotNullOrFinishing(this);

		if (activityNotNullOrFinishing) {
			hideRequestStatusLayouts();

			switch (status) {
			case LOADING: {
				if (requestLoadingLayout != null) {
					requestLoadingLayout.setVisibility(View.VISIBLE);
				}
				break;
			}

			case NO_CONNECTION_AVAILABLE: {
				if (requestBadLayout != null) {
					requestBadLayout.setVisibility(View.VISIBLE);
				}
				break;
			}

			case FAILED: {
				if (requestFailedLayout != null) {
					requestFailedLayout.setVisibility(View.VISIBLE);
				}
				break;
			}

			case SUCCESS_WITH_NO_CONTENT: {
				if (requestEmptyLayout != null) {
					requestEmptyLayout.setVisibility(View.VISIBLE);
				}
				break;
			}

			case SUCCEEDED_WITH_DATA:
			default: {
				// Success or other cases should be handled by the subclasses
				break;
			}
			}
		} else {
			Log.w(TAG, "Activity is null or finishing. No UI elements will be changed.");
		}
	}

	public void hideRequestStatusLayouts() {
		if (requestFailedLayout != null) {
			requestFailedLayout.setVisibility(View.GONE);
		}

		if (requestLoadingLayout != null) {
			requestLoadingLayout.setVisibility(View.GONE);
		}

		if (requestBadLayout != null) {
			requestBadLayout.setVisibility(View.GONE);
		}

		if (requestEmptyLayout != null) {
			requestEmptyLayout.setVisibility(View.GONE);
		}
	}

	public void initCallbackLayouts() {
		requestFailedLayout = (RelativeLayout) findViewById(R.id.request_failed_main_layout);

		requestFailedButton = (Button) findViewById(R.id.request_failed_reload_button);

		if (requestFailedButton != null) {
			requestFailedButton.setOnClickListener(this);
		}

		requestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_main_layout);

		requestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);

		requestEmptyLayoutDetails = (TextView) findViewById(R.id.request_empty_details_tv);
		
		requestBadLayout = (RelativeLayout) findViewById(R.id.bad_request_main_layout);

		requestBadButton = (Button) findViewById(R.id.bad_request_reload_button);

		if (requestBadButton != null) {
			requestBadButton.setOnClickListener(this);
		}
	}
	
	
	
	protected void setEmptyLayoutDetailsMessage(String message)
	{
		if(requestEmptyLayoutDetails != null)
		{
			requestEmptyLayoutDetails.setText(message);
		}
	}
}
