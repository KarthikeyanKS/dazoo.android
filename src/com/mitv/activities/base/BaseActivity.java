
package com.mitv.activities.base;



import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.mitv.FontManager;
import com.mitv.GATrackingManager;
import com.mitv.R;
import com.mitv.activities.FeedActivity;
import com.mitv.activities.HomeActivity;
import com.mitv.activities.SearchPageActivity;
import com.mitv.activities.SplashScreenActivity;
import com.mitv.activities.UserProfileActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.TVDate;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.elements.UndoBarController;
import com.mitv.ui.elements.UndoBarController.UndoListener;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;



public abstract class BaseActivity 
	extends ActionBarActivity 
	implements ViewCallbackListener, OnClickListener, UndoListener 
{
	private static final String TAG = BaseActivity.class.getName();
	
	private static final int TV_DATE_NOT_FOUND = -1;

	private static final int SELECTED_TAB_FONT_SIZE = 12;
	
	private static Stack<Activity> activityStack = new Stack<Activity>();

	
	
	protected RelativeLayout tabTvGuide;
	protected FontTextView tabTvGuideIcon;
	protected FontTextView tabTvGuideText;
	protected RelativeLayout tabActivity;
	protected FontTextView tabActivityIcon;
	protected FontTextView tabActivityText;
	protected RelativeLayout tabProfile;
	protected FontTextView tabProfileIcon;
	protected FontTextView tabProfileText;
	protected View tabDividerLeft;
	protected View tabDividerRight;
	protected View undoBarlayoutView;

	private RelativeLayout requestEmptyLayout;
	private TextView requestEmptyLayoutDetails;
	private RelativeLayout requestLoadingLayout;
	private RelativeLayout requestNoInternetConnectionLayout;
	private RelativeLayout requestFailedLayout;
	private Button requestNoInternetConnectionRetryButton;
	private Button requestFailedRetryButton;

	protected ActionBar actionBar;
	private UndoBarController undoBarController;

	private boolean userHasJustLoggedIn;
	private boolean userHasJustLoggedOut;

	protected RequestIdentifierEnum latestRequest;

	
	
	/* Abstract Methods */

	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();

	/*
	 * This method implementation should return true if all the data necessary to show the content view can be obtained
	 * from cache without the need of external service calls
	 */
	protected abstract boolean hasEnoughDataToShowContent();

	/* This method implementation should deal with changes after the data has been fetched */
	protected abstract void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier);

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		/* Google Analytics Tracking */
		EasyTracker.getInstance(this).activityStart(this);

		String className = this.getClass().getName();

		GATrackingManager.sendView(className);
	}
	
	protected void registerAsListenerForRequest(RequestIdentifierEnum requestIdentifier) {
		ContentManager.sharedInstance().registerListenerForRequest(requestIdentifier, this);
	}

	
	@Override
	public void onUndo(Parcelable token) 
	{
		if (undoBarController != null) 
		{
			undoBarController.hideUndoBar(true);
			undoBarController = new UndoBarController(undoBarlayoutView, this);
		} 
		else 
		{
			Log.w(TAG, "Undo bar component is null.");
		}
		
		loadDataWithConnectivityCheck();
	}

	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		/* IMPORTANT add activity to activity stack */
		pushActivityToStack(this);

		setTabViews();

		handleTimeAndDayOnResume();

		Intent intent = getIntent();

		/* Log in states */
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();

		if (isLoggedIn) {
			if (intent.hasExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN)) {
				userHasJustLoggedIn = intent.getExtras().getBoolean(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, false);

				intent.removeExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN);
			} else {
				userHasJustLoggedIn = false;
			}
		} else {
			userHasJustLoggedIn = false;

			if (intent.hasExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT)) {
				userHasJustLoggedOut = intent.getExtras().getBoolean(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT, false);

				intent.removeExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT);
			} else {
				userHasJustLoggedOut = false;
			}
		}

		if (isLoggedIn && userHasJustLoggedIn) {
			StringBuilder sb = new StringBuilder();
			sb.append(getResources().getString(R.string.hello));
			sb.append(" ");
			sb.append(ContentManager.sharedInstance().getFromCacheUserFirstname());

			ToastHelper.createAndShowToast(this, sb.toString());
		} else {
			if (userHasJustLoggedOut) 
			{
				StringBuilder sb = new StringBuilder();
				sb.append(getString(R.string.logout_succeeded));

				ToastHelper.createAndShowToast(this, sb.toString());
			}
		}
	}

	
	private void handleTimeAndDayOnResume() 
	{
		/* Handle time */
		int currentHour = DateUtils.getCurrentHourOn24HourFormat();
		ContentManager.sharedInstance().setSelectedHour(currentHour);


		
		/* Handle day */
		int indexOfTodayFromTVDates = getIndexOfTodayFromTVDates();
		
		/*
		 * Index is not 0, means that the day have changed since the app was launched last time => refetch all the data
		 */
		if (indexOfTodayFromTVDates != TV_DATE_NOT_FOUND) {
			if (indexOfTodayFromTVDates != 0) {
				restartTheApp();
			}
		} else {
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	private void restartTheApp() {		
		killAllActivitiesExceptThis();
		Intent intent = new Intent(this, SplashScreenActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
	
	private void killAllActivitiesExceptThis() {
		//TODO NewArc Clear cache??
		for(Activity activity : activityStack) {
			if(!activity.equals(this)) {
				activity.finish();
			}
		}
	}

	private int getIndexOfTodayFromTVDates() {
		int indexOfTodayFromTVDates = TV_DATE_NOT_FOUND;

		List<TVDate> tvDates = ContentManager.sharedInstance().getFromCacheTVDates();
		
		if(tvDates != null) {
			for(int i = 0; i < tvDates.size(); ++i) 
			{
				TVDate tvDate = tvDates.get(i);
				
				boolean isTVDateNow = DateUtils.isTodayUsingTVDate(tvDate);
				
				if(isTVDateNow) 
				{
					indexOfTodayFromTVDates = i;
					break;
				}
			}
		}

		return indexOfTodayFromTVDates;
	}

	private static void pushActivityToStack(Activity activity) {

		/*
		 * If we got to this activity using the backpress button, then the Android OS will resume the latest activity,
		 * since we are pushing the activity to the stack in this method, we need to make sure that we are pushing
		 * ourselves to the stack if we already are in top of it.
		 */
		if (activityStack.isEmpty()) {
			activityStack.push(activity);
		} else if (activityStack.peek() != activity) {
			activityStack.push(activity);
		}

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
	private static void removeFromStack(Activity activity) {

		if (activityStack.contains(activity)) {
			if (activityStack.peek() == activity) {

				int positionToRemove = activityStack.size() - 1;
				activityStack.removeElementAt(positionToRemove);
			}
		}
	}

	/**
	 * This if e.g. singleTask Activity HomeActivity gets destroyed by OS, remove all occurences in the activity stack
	 */
	private static void removeFromStackOnDestroy(Activity activity) {

		for (int i = 0; i < activityStack.size(); ++i) {
			if (activityStack.contains(activity)) {
				activityStack.remove(activity);
			}
		}

		printActivityStack();
	}

	private static void removeActivitiesThatRequiresLoginFromStack(Activity activity) {
		if (activity instanceof BaseActivityLoginRequired) {
			removeFromStackOnDestroy(activity);
		}
	}

	public static Activity getMostRecentTabActivity() {
		Activity mostRecentTabActivity = null;

		/* Iterate through stack, start at top of stack */
		for (int i = activityStack.size() - 1; i >= 0; --i) {
			Activity activityInStack = activityStack.get(i);

			/* Check if activityInStack is any of the three TabActivities */
			if (isTabActivity(activityInStack)) {
				mostRecentTabActivity = activityInStack;
				break;
			}
		}

		return mostRecentTabActivity;
	}

	
	
	private boolean isTabActivity() 
	{
		return isTabActivity(this);
	}

	
	
	private static boolean isTabActivity(Activity activity) 
	{
		boolean isTabActivity = (activity instanceof HomeActivity || activity instanceof FeedActivity || activity instanceof UserProfileActivity);
		return isTabActivity;
	}

	
	
	public void setTabViews()
	{
		tabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		tabTvGuideIcon = (FontTextView) findViewById(R.id.element_tab_icon_guide);
		tabTvGuideText = (FontTextView) findViewById(R.id.element_tab_text_guide);

		if (tabTvGuide != null) {
			tabTvGuide.setOnClickListener(this);
		}

		tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		tabActivityIcon = (FontTextView) findViewById(R.id.element_tab_icon_activity);
		tabActivityText = (FontTextView) findViewById(R.id.element_tab_text_activity);

		if (tabActivity != null) {
			tabActivity.setOnClickListener(this);
		}

		tabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		tabProfileIcon = (FontTextView) findViewById(R.id.element_tab_icon_me);
		tabProfileText = (FontTextView) findViewById(R.id.element_tab_text_me);

		if (tabProfile != null) {
			tabProfile.setOnClickListener(this);
		}

		Activity mostRecentTabActivity = getMostRecentTabActivity();

		if (mostRecentTabActivity instanceof HomeActivity) {
			setSelectedTabAsTVGuide();
		} else if (mostRecentTabActivity instanceof FeedActivity) {
			setSelectedTabAsActivityFeed();
		} else if (mostRecentTabActivity instanceof UserProfileActivity) {
			setSelectedTabAsUserProfile();
		} else {
			Log.w(TAG, "Unknown activity tab");
		}
	}

	protected void setSelectedTabAsTVGuide() {
		if (tabTvGuide != null) {
			// tabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
			tabTvGuideIcon.setTextColor(getResources().getColor(R.color.white));
			tabTvGuideText.setTextColor(getResources().getColor(R.color.white));
			tabTvGuideIcon.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));
			tabTvGuideText.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));

			Typeface bold = FontManager.getFontBold(getApplicationContext());
			tabTvGuideText.setTypeface(bold);
			tabTvGuideText.setTextSize(SELECTED_TAB_FONT_SIZE);
		}

		if (tabActivity != null) {
			// tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
			tabActivityIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabActivityText.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabActivityIcon.setShadowLayer(0, 0, 0, 0);
			tabActivityText.setShadowLayer(0, 0, 0, 0);
		}

		if (tabProfile != null) {
			// tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
			tabProfileIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabProfileText.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabProfileIcon.setShadowLayer(0, 0, 0, 0);
			tabProfileText.setShadowLayer(0, 0, 0, 0);
		}
	}

	protected void setSelectedTabAsActivityFeed() {
		if (tabTvGuide != null) {
			// tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
			tabTvGuideIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabTvGuideText.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabTvGuideIcon.setShadowLayer(0, 0, 0, 0);
			tabTvGuideText.setShadowLayer(0, 0, 0, 0);
		}

		if (tabActivity != null) {
			// tabActivity.setBackgroundColor(getResources().getColor(R.color.red));
			tabActivityIcon.setTextColor(getResources().getColor(R.color.white));
			tabActivityText.setTextColor(getResources().getColor(R.color.white));
			tabActivityIcon.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));
			tabActivityText.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));

			Typeface bold = FontManager.getFontBold(getApplicationContext());
			tabActivityText.setTypeface(bold);
			tabActivityText.setTextSize(SELECTED_TAB_FONT_SIZE);
		}

		if (tabProfile != null) {
			// tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
			tabProfileIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabProfileText.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabProfileIcon.setShadowLayer(0, 0, 0, 0);
			tabProfileText.setShadowLayer(0, 0, 0, 0);
		}
	}

	protected void setSelectedTabAsUserProfile() {
		if (tabTvGuide != null) {
			// tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
			tabTvGuideIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabTvGuideText.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabTvGuideIcon.setShadowLayer(0, 0, 0, 0);
			tabTvGuideText.setShadowLayer(0, 0, 0, 0);
		}

		if (tabActivity != null) {
			// tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
			tabActivityIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabActivityText.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabActivityIcon.setShadowLayer(0, 0, 0, 0);
			tabActivityText.setShadowLayer(0, 0, 0, 0);
		}

		if (tabProfile != null) {
			// tabProfile.setBackgroundColor(getResources().getColor(R.color.red));
			tabProfileIcon.setTextColor(getResources().getColor(R.color.white));
			tabProfileText.setTextColor(getResources().getColor(R.color.white));
			tabProfileIcon.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));
			tabProfileText.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));

			Typeface bold = FontManager.getFontBold(getApplicationContext());
			tabProfileText.setTypeface(bold);
			tabProfileText.setTextSize(SELECTED_TAB_FONT_SIZE);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.tab_tv_guide: {
			if (!(this instanceof HomeActivity)) {
				Intent intentActivity = new Intent(this, HomeActivity.class);
				startActivity(intentActivity);
			}
			break;
		}

		case R.id.tab_activity: {
			if (!(this instanceof FeedActivity)) {
				Intent intentActivity = new Intent(this, FeedActivity.class);
				startActivity(intentActivity);
			}
			break;
		}

		case R.id.tab_me: {
			if (!(this instanceof UserProfileActivity)) {
				Intent intentMe = new Intent(this, UserProfileActivity.class);
				startActivity(intentMe);
			}
			break;
		}

		case R.id.no_connection_reload_button: {
			loadDataWithConnectivityCheck();

			break;
		}
		
		case R.id.request_failed_reload_button: {
			updateUI(UIStatusEnum.LOADING);
			ContentManager.sharedInstance().fetchFromServiceInitialCall(this, null);
			break;
		}

		default: {
			Log.w(TAG, "Unknown onClick action");
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
	protected void onDestroy() {
		removeFromStackOnDestroy(this);

		super.onDestroy();
	}
	
	
	
	@Override
	public void onBackPressed() 
	{
		if(isTabActivity())
		{
		    Intent intent = new Intent(Intent.ACTION_MAIN);
		    intent.addCategory(Intent.CATEGORY_HOME);
		    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		}
		else
		{
			super.onBackPressed();
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
	 * This method checks for Internet connectivity before loading data
	 */
	protected void loadDataWithConnectivityCheck() 
	{
		boolean isConnected = NetworkUtils.isConnected();

		if (isConnected) 
		{
			boolean hasInitialData = ContentManager.sharedInstance().getFromCacheHasInitialData();

			if (hasInitialData) 
			{
				loadData();
			} 
			else 
			{
				updateUI(UIStatusEnum.LOADING);
				ContentManager.sharedInstance().fetchFromServiceInitialCall(this, null);
			}
		} 
		else 
		{
			if (hasEnoughDataToShowContent()) 
			{
				loadData();
			} 
			else 
			{
				updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
			}
		}
	}
	
	
	@Override
	public final void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		Log.d(TAG, String.format("onDataAvailable FetchRequestResult: %s requestId: %s", fetchRequestResult.getDescription(), requestIdentifier.getDescription()));
		
		this.latestRequest = requestIdentifier;
		switch (fetchRequestResult) {
		case INTERNET_CONNECTION_AVAILABLE: {
			loadData();
			break;
		}

		case INTERNET_CONNECTION_NOT_AVAILABLE: {
			updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
			break;
		}

		case API_VERSION_TOO_OLD: {
			break;
		}

		case SUCCESS:
		default: {
			if (requestIdentifier == RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL) {
				loadData();
			} else {
				if (requestIdentifier == RequestIdentifierEnum.USER_LOGOUT) {
					/* When logged out go through stack and delete any activity that requires us to be logged in */
					removeActivitiesThatRequiresLoginFromStack(this);
				}

				boolean isConnected = NetworkUtils.isConnected();

				if (hasEnoughDataToShowContent() && isConnected == false) {
					if (undoBarController != null) {
						undoBarController.showUndoBar(false, getString(R.string.dialog_prompt_check_internet_connection), null);
					} else {
						Log.w(TAG, "Undo bar component is null.");
					}
				}

				onDataAvailable(fetchRequestResult, requestIdentifier);
			}
			break;
		}
		}
	}

	protected void updateUIBaseElements(UIStatusEnum status) {
		Log.d(TAG, String.format("updateUIBaseElements, status: %s", status.getDescription()));

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

			case API_VERSION_TOO_OLD: {
				DialogHelper.showMandatoryAppUpdateDialog(this);
				break;
			}

			case FAILED:{
				if (requestFailedLayout != null) {
					requestFailedLayout.setVisibility(View.VISIBLE);
				}
				break;
			}
			
			case NO_CONNECTION_AVAILABLE: {
				if (requestNoInternetConnectionLayout != null) {
					requestNoInternetConnectionLayout.setVisibility(View.VISIBLE);
				}
				break;
			}

			case SUCCESS_WITH_NO_CONTENT: {
				if (requestEmptyLayout != null) {
					requestEmptyLayout.setVisibility(View.VISIBLE);
				}
				break;
			}

			case SUCCESS_WITH_CONTENT:
			default: {
				// Success or other cases should be handled by the subclasses
				break;
			}
			}
		} else {
			Log.w(TAG, "Activity is null or finishing. No UI elements will be changed.");
		}
	}

	private void hideRequestStatusLayouts() {
		if (requestLoadingLayout != null) {
			requestLoadingLayout.setVisibility(View.GONE);
		}

		if (requestNoInternetConnectionLayout != null) {
			requestNoInternetConnectionLayout.setVisibility(View.GONE);
		}

		if (requestEmptyLayout != null) {
			requestEmptyLayout.setVisibility(View.GONE);
		}
		
		if(requestFailedLayout != null) {
			requestFailedLayout.setVisibility(View.GONE);
		}
	}

	private void initCallbackLayouts() {
		requestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_main_layout);

		requestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);

		requestEmptyLayoutDetails = (TextView) findViewById(R.id.request_empty_details_tv);

		requestNoInternetConnectionLayout = (RelativeLayout) findViewById(R.id.no_connection_layout);
		
		requestFailedLayout = (RelativeLayout) findViewById(R.id.request_failed_main_layout);
		
		requestFailedRetryButton = (Button) findViewById(R.id.request_failed_reload_button);
		
		if(requestFailedRetryButton != null) {
			requestFailedRetryButton.setOnClickListener(this);
		}

		requestNoInternetConnectionRetryButton = (Button) findViewById(R.id.no_connection_reload_button);

		if (requestNoInternetConnectionRetryButton != null) {
			requestNoInternetConnectionRetryButton.setOnClickListener(this);
		}

		undoBarlayoutView = findViewById(R.id.undobar);

		if (undoBarlayoutView != null) {
			undoBarController = new UndoBarController(undoBarlayoutView, this);
		} else {
			Log.w(TAG, "Undo bar element not present.");
		}
	}

	protected void setEmptyLayoutDetailsMessage(String message) {
		if (requestEmptyLayoutDetails != null) {
			requestEmptyLayoutDetails.setText(message);
		}
	}
}
