
package com.mitv.activities.base;



import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
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
import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.GATrackingManager;
import com.mitv.R;
import com.mitv.activities.FeedActivity;
import com.mitv.activities.HomeActivity;
import com.mitv.activities.SearchPageActivity;
import com.mitv.activities.UserProfileActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;



public abstract class BaseActivity 
	extends ActionBarActivity
	implements ActivityCallbackListener, OnClickListener 
{
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
	protected void onCreate(android.os.Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		boolean enableStrictMode = Constants.ENABLE_STRICT_MODE;
		
		if (enableStrictMode) 
		{
			//enableStrictMode();
		}

		/* Google Analytics Tracking */
		EasyTracker.getInstance(this).activityStart(this);
		String className = this.getClass().getName();
		GATrackingManager.sendView(className);

		initCallbackLayouts();
	}
	

	
	@Override
	protected void onResume() 
	{
		super.onResume();

		/* IMPORTANT add activity to activity stack */
		pushActivityToStack(this);

		setTabViews();

		Intent intent = getIntent();
		
		/* Log in states */
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if (isLoggedIn) 
		{
			if (intent.hasExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN)) 
			{
				userHasJustLoggedIn = intent.getExtras().getBoolean(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, false);

				intent.removeExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN);
			} 
			else 
			{
				userHasJustLoggedIn = false;
			}
		}
		else 
		{
			userHasJustLoggedIn = false;
			
			if (intent.hasExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT)) 
			{
				userHasJustLoggedOut = intent.getExtras().getBoolean(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT, false);

				intent.removeExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT);
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
				// TODO NewArc - Hardcoded string for logout action
				sb.append("logout");

				ToastHelper.createAndShowToast(this, sb.toString());
			}
		}
	}
	

	
	private static void pushActivityToStack(Activity activity) {
		/* If activity is tab activity, move it to the top */
		if (isTabActivity(activity)) 
		{
			removeFromStack(activity);
		}
		activityStack.push(activity);
		
		printActivityStack();
	};
	
	/* Used for debugging only */
	private static void printActivityStack() {
		Log.d(TAG, ".");
		Log.d(TAG, ".");
		Log.d(TAG, "---<<<*** ActivityStack ***>>>---");
		for (int i = activityStack.size() - 1; i >= 0; --i) {
            Activity activityInStack = activityStack.get(i);
            Log.d(TAG, activityInStack.getClass().getSimpleName());
        }
	}
	
	
	/* Remove activity from activitStack */
	private static void removeFromStack(Activity activity) 
	{
		if(activityStack.contains(activity))
		{
			activityStack.remove(activity);
		}
	}
	

	
//	public static Activity getMostRecentTabActivity() 
//	{
//		int mostRecentTabActivityIndex = getMostRecentTabActivityIndex();
//		
//		Activity mostRecentTabActivity = null;
//		
//		if(mostRecentTabActivityIndex >= 0 && mostRecentTabActivityIndex < activityStack.size())
//		{
//			mostRecentTabActivity = activityStack.get(mostRecentTabActivityIndex);
//		}
//
//		return mostRecentTabActivity;
//	}
	
	 public static Activity getMostRecentTabActivity() {
	        Activity mostRecentTabActivity = null;

	        /* Iterate through stack, start at top of stack */
	        for (int i = activityStack.size() - 1; i >= 0; --i) {
	            Activity activityInStack = activityStack.get(i);

	            /* Check if activityInStack is any of the three TabActivities */
	            if (isTabActivity(activityInStack))  
	            {
	                mostRecentTabActivity = activityInStack;
	                break;
	            }
	        }

	        return mostRecentTabActivity;
	    }
	
	
	
//	private static int getMostRecentTabActivityIndex() 
//	{
//		int mostRecentTabActivityIndex = -1;
//
//		/* Iterate through stack, start at top of stack */
//		for (int i = activityStack.size() - 1; i >= 0; --i) 
//		{
//			Activity activityInStack = activityStack.get(i);
//
//			/* Check if activityInStack is any of the three TabActivities */
//			if (isTabActivity(activityInStack)) 
//			{
//				mostRecentTabActivityIndex = i;
//				break;
//			}
//		}
//
//		return mostRecentTabActivityIndex;
//	}
	
	private boolean thisIsTabActivity() {
		return isTabActivity(this);
	}
	
	private static boolean isTabActivity(Activity activity) {
		boolean isTabActivity = (activity instanceof HomeActivity || 
				activity instanceof FeedActivity || 
				activity instanceof UserProfileActivity);
		return isTabActivity;
	}

	
	public void setTabViews() 
	{
		tabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);

		if (tabTvGuide != null) 
		{
			tabTvGuide.setOnClickListener(this);
		}

		tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);

		if (tabActivity != null) 
		{
			tabActivity.setOnClickListener(this);
		}

		tabProfile = (RelativeLayout) findViewById(R.id.tab_me);

		if (tabProfile != null) 
		{
			tabProfile.setOnClickListener(this);
		}

		tabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);

		if (tabDividerLeft != null) 
		{
			tabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		}

		tabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		if (tabDividerRight != null) 
		{
			tabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		}


				Activity mostRecentTabActivity = getMostRecentTabActivity();
				
				if (mostRecentTabActivity instanceof HomeActivity) 
				{
					setSelectedTabAsTVGuide();
				}
				else if (mostRecentTabActivity instanceof FeedActivity) 
				{
					setSelectedTabAsActivityFeed();
				} 
				else if (mostRecentTabActivity instanceof UserProfileActivity)
				{
					setSelectedTabAsUserProfile();
				}
				else
				{
					Log.w(TAG, "Unknown activity tab");
				}
	}
	
	

	protected void setSelectedTabAsTVGuide() 
	{
		if (tabTvGuide != null) 
		{
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		}

		if (tabActivity != null) 
		{
			tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		if (tabProfile != null) 
		{
			tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}

	
	
	protected void setSelectedTabAsActivityFeed()
	{
		if (tabTvGuide != null) 
		{
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		if (tabActivity != null)
		{
			tabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		}

		if (tabProfile != null)
		{	
			tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}

	
	
	protected void setSelectedTabAsUserProfile() 
	{
		if (tabTvGuide != null) 
		{
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		if (tabActivity != null) 
		{
			tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		if (tabProfile != null) 
		{
			tabProfile.setBackgroundColor(getResources().getColor(R.color.red));
		}
	}

	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();

		boolean changedTab = false;
		
		switch (id) 
		{
			case R.id.tab_tv_guide: 
			{
				if (!(this instanceof HomeActivity)) 
				{
					Intent intentActivity = new Intent(this, HomeActivity.class);
					startActivity(intentActivity);
					changedTab = true;
				}
				break;
			}
	
			case R.id.tab_activity: 
			{
				if (!(this instanceof FeedActivity))
				{
					Intent intentActivity = new Intent(this, FeedActivity.class);
					startActivity(intentActivity);
					changedTab = true;
				}
				break;
			}
	
			case R.id.tab_me:
			{
				if (!(this instanceof UserProfileActivity))
				{
					Intent intentMe = new Intent(this, UserProfileActivity.class);
					startActivity(intentMe);
					changedTab = true;
				}
				break;
			}
	
			case R.id.request_failed_reload_button:
			case R.id.bad_request_reload_button:
			{
				if (!NetworkUtils.isConnectedAndHostIsReachable())
				{
					updateUI(UIStatusEnum.FAILED);
				}
				else
				{
					loadData();
				}
	
				break;
			}
			
			default: 
			{
				Log.w(TAG, "Unknown tab selected");
			}
		}
		
		/* If we changed tabs, and the activity we came from (this) was not a tabActivity (i.e. this is an instance of BroadcastPage or ChannelPage etc) then finish the activity! */
		if(!thisIsTabActivity() && changedTab) {
			this.finish();
		}
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.actionbar_menu, menu);

		MenuItem searchIcon = menu.findItem(R.id.action_start_search);

		View seachIconView = MenuItemCompat.getActionView(searchIcon);

		seachIconView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent toSearchPage = new Intent(BaseActivity.this, SearchPageActivity.class);
				
				startActivity(toSearchPage);
			}
		});

		MenuItem searchFieldItem = menu.findItem(R.id.searchfield);

		searchFieldItem.setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) 
		{
			case android.R.id.home: 
			{
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
	
			default: 
			{
				return super.onOptionsItemSelected(item);
			}
		}
	}
	
	

	@Override
	protected void onStop()
	{
		super.onStop();

		String className = this.getClass().getName();

		GATrackingManager.stopTrackingView(className);

		EasyTracker.getInstance(this).activityStop(this);
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		removeFromStack(this);
	}
	
	
	
	@Override
	protected void onDestroy() 
	{
		removeFromStack(this);
	
		super.onDestroy();
	}

	
	
	@Override
	public void setContentView(int layoutResID) 
	{
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
	 * This method checks for Internet connectivity before loading data
	 */
	protected void loadDataWithConnectivityCheck() 
	{
		boolean isConnected = NetworkUtils.isConnected();
		
		if(isConnected)
		{
			loadData();
		}
		else 
		{
			updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
		}
	}

	
	
	@Override
	public final void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		switch (fetchRequestResult) 
		{
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

	
	
	protected void updateUIBaseElements(UIStatusEnum status) 
	{
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
	
	

	private void hideRequestStatusLayouts() 
	{
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

	
	
	private void initCallbackLayouts() 
	{
		requestFailedLayout = (RelativeLayout) findViewById(R.id.request_failed_main_layout);

		requestFailedButton = (Button) findViewById(R.id.request_failed_reload_button);

		if (requestFailedButton != null) 
		{
			requestFailedButton.setOnClickListener(this);
		}

		requestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_main_layout);

		requestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);

		requestEmptyLayoutDetails = (TextView) findViewById(R.id.request_empty_details_tv);
		
		requestBadLayout = (RelativeLayout) findViewById(R.id.bad_request_main_layout);

		requestBadButton = (Button) findViewById(R.id.bad_request_reload_button);

		if (requestBadButton != null) 
		{
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