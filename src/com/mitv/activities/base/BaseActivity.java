
package com.mitv.activities.base;



import java.util.Calendar;
import java.util.List;
import java.util.Stack;
import java.util.Timer;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.FeedActivity;
import com.mitv.activities.HomeActivity;
import com.mitv.activities.SearchPageActivity;
import com.mitv.activities.UserProfileActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.FontManager;
import com.mitv.managers.ImageLoaderManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.managers.TrackingManager;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;



public abstract class BaseActivity 
	extends ActionBarActivity 
	implements ViewCallbackListener, OnClickListener 
{
	private static final String TAG = BaseActivity.class.getName();
	
	
	
	private static final int SELECTED_TAB_FONT_SIZE = 12;
	
	private static Stack<BaseActivity> activityStack = new Stack<BaseActivity>();

	
	
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

	private RelativeLayout requestSuccessfulLayout;
	private RelativeLayout requestEmptyLayout;
	private FontTextView requestEmptyLayoutDetails;
	private FontTextView requestLoadingLayoutDetails;
	private RelativeLayout requestLoadingLayout;
	private RelativeLayout requestNoInternetConnectionLayout;
	private RelativeLayout requestFailedLayout;
	private Button requestNoInternetConnectionRetryButton;
	private Button requestFailedRetryButton;

	protected ActionBar actionBar;

	private boolean userHasJustLoggedIn;
	private boolean userHasJustLoggedOut;

	protected RequestIdentifierEnum latestRequest;
	
	private boolean isFromSplashScreen = false;
	
	/* Initially null, but set to the current device time 
	 * The assignment is done in the cases SUCCESS_WITH_NO_CONTENT or SUCCESS_WITH_CONTENT of the updateUIBaseElements **/
	private Calendar lastDataUpdatedCalendar;
	
	/* Timer for re-fetching data in the background while the user is on the same activity */
	private Timer backgroundLoadTimer;

	/* Time value for the background timer.
	 * The initial value is -1 if not used */
	private int backgroundLoadTimerValueInMinutes;
	

	private boolean loadedFromBackground;

	
	
	/* Abstract Methods */

	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();
	
	/* This method implementation is OPTIONAL */
	protected abstract void loadDataInBackground();

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
		
		TrackingManager.sharedInstance().reportActivityStart(this);
		
		isFromSplashScreen = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_IS_FROM_SPLASHSCREEN, false);
		
		lastDataUpdatedCalendar = null;
		
		backgroundLoadTimer = new Timer();
		
		backgroundLoadTimerValueInMinutes = -1;
	}
	
	
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e)
	{
	    switch(keycode) 
	    {
	        case KeyEvent.KEYCODE_MENU:
	        {
	        	TrackingGAManager.sharedInstance().sendUserPressedMenuButtonEvent();
	          
	        	return true;
	        }
	        
	        default:
	        {
	        	return super.onKeyDown(keycode, e);
	        }
	    }
	}
		

	
	protected void registerAsListenerForRequest(RequestIdentifierEnum requestIdentifier)
	{
		ContentManager.sharedInstance().registerListenerForRequest(requestIdentifier, this);
	}
	
	
	
	protected void unregisterListenerFromAllRequests()
	{
		ContentManager.sharedInstance().unregisterListenerFromAllRequests(this);
	}

	
	
	private void hockeyAppCheckForCrashes() 
	{
		CrashManager.register(this, Constants.HOCKEY_APP_TOKEN);
	}

	
	
	private void hockeyAppCheckForUpdates() 
	{
		UpdateManager.register(this, Constants.HOCKEY_APP_TOKEN);
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		setBackgroundLoadingTimer();
		
		ImageLoaderManager.sharedInstance(this).resume();
		
		TrackingManager.sharedInstance().onResume(this);
		
		if(Constants.USE_HOCKEY_APP_CRASH_REPORTS)
		{
			hockeyAppCheckForCrashes();
		}
		
		if(Constants.USE_HOCKEY_APP_UPDATE_NOTIFICATIONS)
		{
			hockeyAppCheckForUpdates();
		}
		
		/* IMPORTANT add activity to activity stack */
		pushActivityToStack(this);

		setTabViews();
		
		handleTimeAndDayOnResume();

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
			sb.append(getString(R.string.hello));
			sb.append(" ");
			sb.append(ContentManager.sharedInstance().getFromCacheUserFirstname());

			ToastHelper.createAndShowShortToast(sb.toString());
		} 
		else 
		{
			if (userHasJustLoggedOut) 
			{
				StringBuilder sb = new StringBuilder();
				
				sb.append(getString(R.string.logout_succeeded));

				ToastHelper.createAndShowShortToast(sb.toString());
			}
		}
	}
	
	
	
	private void handleTimeAndDayOnResume() 
	{
		/* Handle day */
		int indexOfTodayFromTVDates = getIndexOfTodayFromTVDates();
		
		/*
		 * Index is not 0, that means that the actual day has changed since the application was launched for last time
		 * In this case, the data in cache is no longer accurate and we must re-fetch it from the service
		 */
		if (indexOfTodayFromTVDates > 0) 
		{
			boolean isTimeOffSync = ContentManager.sharedInstance().isLocalDeviceCalendarOffSync();

			if(isTimeOffSync == false) 
			{
				ContentManager.sharedInstance().clearGuideCacheData();
			}
		} 
	}
	
	
	
	private int getIndexOfTodayFromTVDates() 
	{
		int indexOfTodayFromTVDates = -1;

		List<TVDate> tvDates = ContentManager.sharedInstance().getFromCacheTVDates();
		
		if(tvDates != null)
		{
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

	
	
	private void killAllActivitiesIncludingThis() 
	{
		for(Activity activity : activityStack) 
		{
			activity.finish();
		}
	}


	
	private static void pushActivityToStack(BaseActivity activity) 
	{
		/*
		 * If we got to this activity using the backpress button, then the Android OS will resume the latest activity,
		 * since we are pushing the activity to the stack in this method, we need to make sure that we are pushing
		 * ourselves to the stack if we already are in top of it.
		 */
		if (activityStack.isEmpty()) 
		{
			activityStack.push(activity);
		} 
		else if (activityStack.peek() != activity) 
		{
			activityStack.push(activity);
		}

		printActivityStack();
	};

	
	
	/* Used for debugging only */
	private static void printActivityStack()
	{
		Log.d(TAG, ".");
		Log.d(TAG, ".");
		Log.d(TAG, "---<<<*** ActivityStack ***>>>---");

		for (int i = activityStack.size() - 1; i >= 0; --i)
		{
			Activity activityInStack = activityStack.get(i);
			
			Log.d(TAG, activityInStack.getClass().getSimpleName());
		}
	}


	
	/**
	 * This if e.g. singleTask Activity HomeActivity gets destroyed by OS, remove all occurrences in the activity stack
	 */
	private static void removeFromStackOnDestroy(Activity activity) 
	{
		for (int i = 0; i < activityStack.size(); ++i) 
		{
			if (activityStack.contains(activity)) 
			{
				activityStack.remove(activity);
			}
		}

		printActivityStack();
	}

	
	
	private static void removeActivitiesThatRequiresLoginFromStack(Activity activity)
	{
		if (activity instanceof BaseActivityLoginRequired)
		{
			removeFromStackOnDestroy(activity);
		}
	}

	
	
	public static Activity getMostRecentTabActivity() 
	{
		Activity mostRecentTabActivity = null;

		/* Iterate through stack, start at top of stack */
		for (int i = activityStack.size() - 1; i >= 0; --i) 
		{
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

		if (tabTvGuide != null) 
		{
			tabTvGuide.setOnClickListener(this);
		}

		tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		tabActivityIcon = (FontTextView) findViewById(R.id.element_tab_icon_activity);
		tabActivityText = (FontTextView) findViewById(R.id.element_tab_text_activity);

		if (tabActivity != null) 
		{
			tabActivity.setOnClickListener(this);
		}

		tabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		tabProfileIcon = (FontTextView) findViewById(R.id.element_tab_icon_me);
		tabProfileText = (FontTextView) findViewById(R.id.element_tab_text_me);

		if (tabProfile != null) 
		{
			tabProfile.setOnClickListener(this);
		}
		
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if(tabProfileText != null)
		{
			if(isLoggedIn)
			{
				String username = ContentManager.sharedInstance().getFromCacheUserFirstname();
			
				tabProfileText.setText(username);
			}
			else
			{
				String defaultTabText = getString(R.string.tab_me);
				
				tabProfileText.setText(defaultTabText);
			}
		}

		Activity mostRecentTabActivity = getMostRecentTabActivity();

		if(mostRecentTabActivity == null)
		{
			setSelectedTabAsTVGuide();
		}
		else if (mostRecentTabActivity instanceof HomeActivity) 
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
			tabTvGuideIcon.setTextColor(getResources().getColor(R.color.white));
			tabTvGuideText.setTextColor(getResources().getColor(R.color.white));
			tabTvGuideIcon.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));
			tabTvGuideText.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));

			Typeface bold = FontManager.getFontBold(getApplicationContext());
			tabTvGuideText.setTypeface(bold);
			
			tabTvGuideText.setTextSize(SELECTED_TAB_FONT_SIZE);
		}

		if (tabActivity != null) {
			tabActivityIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabActivityText.setTextColor(getResources().getColor(R.color.tab_unselected));
			
			Typeface regular = FontManager.getFontRegular(getApplicationContext());
			tabActivityText.setTypeface(regular);
			
			tabActivityIcon.setShadowLayer(0, 0, 0, 0);
			tabActivityText.setShadowLayer(0, 0, 0, 0);
		}

		if (tabProfile != null)
		{
			tabProfileIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabProfileText.setTextColor(getResources().getColor(R.color.tab_unselected));
			
			Typeface regular = FontManager.getFontRegular(getApplicationContext());
			tabProfileText.setTypeface(regular);
			
			tabProfileIcon.setShadowLayer(0, 0, 0, 0);
			tabProfileText.setShadowLayer(0, 0, 0, 0);
		}
	}

	
	
	protected void setSelectedTabAsActivityFeed() 
	{
		if (tabTvGuide != null) 
		{
			tabTvGuideIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabTvGuideText.setTextColor(getResources().getColor(R.color.tab_unselected));
			
			Typeface regular = FontManager.getFontRegular(getApplicationContext());
			tabTvGuideText.setTypeface(regular);
			
			tabTvGuideIcon.setShadowLayer(0, 0, 0, 0);
			tabTvGuideText.setShadowLayer(0, 0, 0, 0);
		}

		if (tabActivity != null) 
		{
			tabActivityIcon.setTextColor(getResources().getColor(R.color.white));
			tabActivityText.setTextColor(getResources().getColor(R.color.white));
			tabActivityIcon.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));
			tabActivityText.setShadowLayer(2, 2, 2, getResources().getColor(R.color.tab_dropshadow));

			Typeface bold = FontManager.getFontBold(getApplicationContext());
			tabActivityText.setTypeface(bold);
			
			tabActivityText.setTextSize(SELECTED_TAB_FONT_SIZE);
		}

		if (tabProfile != null) 
		{
			tabProfileIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabProfileText.setTextColor(getResources().getColor(R.color.tab_unselected));
			
			Typeface regular = FontManager.getFontRegular(getApplicationContext());
			tabProfileText.setTypeface(regular);
			
			tabProfileIcon.setShadowLayer(0, 0, 0, 0);
			tabProfileText.setShadowLayer(0, 0, 0, 0);
		}
	}

	
	protected void setSelectedTabAsUserProfile()
	{
		if (tabTvGuide != null) 
		{
			tabTvGuideIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabTvGuideText.setTextColor(getResources().getColor(R.color.tab_unselected));
			
			Typeface regular = FontManager.getFontRegular(getApplicationContext());
			tabTvGuideText.setTypeface(regular);
			
			tabTvGuideIcon.setShadowLayer(0, 0, 0, 0);
			tabTvGuideText.setShadowLayer(0, 0, 0, 0);
		}

		if (tabActivity != null)
		{
			tabActivityIcon.setTextColor(getResources().getColor(R.color.tab_unselected));
			tabActivityText.setTextColor(getResources().getColor(R.color.tab_unselected));

			Typeface regular = FontManager.getFontRegular(getApplicationContext());
			tabActivityText.setTypeface(regular);
			
			tabActivityIcon.setShadowLayer(0, 0, 0, 0);
			tabActivityText.setShadowLayer(0, 0, 0, 0);
		}

		if (tabProfile != null)
		{
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
	public void onClick(View v) 
	{
		int id = v.getId();

		switch (id) 
		{
			case R.id.tab_tv_guide: 
			{
				if (!(this instanceof HomeActivity)) 
				{
					Intent intentActivity = new Intent(this, HomeActivity.class);
					
					startActivity(intentActivity);
				}
				
				break;
			}
	
			case R.id.tab_activity:
			{
				if (!(this instanceof FeedActivity))
				{
					Intent intentActivity = new Intent(this, FeedActivity.class);
					
					startActivity(intentActivity);
				}
				
				break;
			}
	
			case R.id.tab_me:
			{
				if (!(this instanceof UserProfileActivity))
				{
					Intent intentMe = new Intent(this, UserProfileActivity.class);
					
					startActivity(intentMe);
				}
				
				break;
			}
	
			case R.id.no_connection_reload_button:
			{
				TrackingGAManager.sharedInstance().sendUserNoConnectionRetryLayoutButtomPressed(getClass().getSimpleName());
				
				loadDataWithConnectivityCheck();
				
				break;
			}
			
			case R.id.request_failed_reload_button: 
			{
				TrackingGAManager.sharedInstance().sendUserNoDataRetryLayoutButtomPressed(getClass().getSimpleName());
				
				loadDataWithConnectivityCheck();
				
				break;
			}
	
			default: 
			{
				Log.w(TAG, "Unknown onClick action");
			}
		}
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		
		/* The login, register and sign up pages will have the action bar menu without the search option */
		if (!(this instanceof BaseActivityWithoutSearchOption))
		{
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
		}

		return super.onCreateOptionsMenu(menu);
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int itemId = item.getItemId();
		
		switch(itemId) 
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
	protected void onPause() 
	{
		super.onPause();
 
		if(backgroundLoadTimer != null)
		{
			backgroundLoadTimer.cancel();
		}
		
		TrackingManager.sharedInstance().onPause(this);
		
		ImageLoaderManager.sharedInstance(this).pause();
	}
	
	
	
	@Override
	protected void onStop() 
	{
		super.onStop();

		TrackingManager.sharedInstance().reportActivityStop(this);
	}

	

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		
		removeFromStackOnDestroy(this);
		
		unregisterListenerFromAllRequests();
	}
	
	
	
	@Override
	public void onBackPressed() 
	{		
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.general_back_press_loading_message);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		super.onBackPressed();
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
				if (isFromSplashScreen) 
				{
					isFromSplashScreen = false;
					updateUI(UIStatusEnum.FAILED);
				}
				else {
				updateUI(UIStatusEnum.LOADING);
				
				ContentManager.sharedInstance().fetchFromServiceInitialCall(this, null);
				}
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
	public final void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier)
	{
		this.latestRequest = requestIdentifier;
		
		switch (fetchRequestResult) 
		{
			case INTERNET_CONNECTION_AVAILABLE: 
			{
				loadData();
				break;
			}
	
			case INTERNET_CONNECTION_NOT_AVAILABLE: 
			{
				updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
				break;
			}
	
			case API_VERSION_TOO_OLD: 
			{
				updateUI(UIStatusEnum.API_VERSION_TOO_OLD);
				break;
			}
			
			case FORBIDDEN:
			{
				ContentManager.sharedInstance().performLogout(this, true);
				
				updateUI(UIStatusEnum.USER_TOKEN_EXPIRED);
				break;
			}
			
			case SUCCESS:
			case SUCCESS_WITH_NO_CONTENT:
			{
				if (requestIdentifier == RequestIdentifierEnum.TV_GUIDE_INITIAL_CALL) 
				{
					loadData();
				} 
				else 
				{
					if (requestIdentifier == RequestIdentifierEnum.USER_LOGOUT) 
					{
						/* When logged out go through stack and delete any activity that requires us to be logged in */
						removeActivitiesThatRequiresLoginFromStack(this);
					}
	
					boolean isConnected = NetworkUtils.isConnected();
	
					if (hasEnoughDataToShowContent() && isConnected == false) 
					{
						ToastHelper.createAndShowNoInternetConnectionToast();
					}
	
					onDataAvailable(fetchRequestResult, requestIdentifier);
				}
				break;
			}
			
			default: 
			{				
				boolean isConnected = NetworkUtils.isConnected();
				
				if (hasEnoughDataToShowContent() && isConnected == false) 
				{
					ToastHelper.createAndShowNoInternetConnectionToast();
				}

				onDataAvailable(fetchRequestResult, requestIdentifier);
				break;
			}
		}
	}

	
	
	protected void updateUIBaseElements(UIStatusEnum status) 
	{
		boolean activityNotNullAndNotFinishing = GenericUtils.isActivityNotNullAndNotFinishingAndNotDestroyed(this);

		if (activityNotNullAndNotFinishing) 
		{
			hideRequestStatusLayouts();

			switch (status) 
			{
				case LOADING: 
				{
					if (requestLoadingLayout != null) 
					{
						requestLoadingLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
	
				case API_VERSION_TOO_OLD: 
				{
					DialogHelper.showMandatoryAppUpdateDialog(this);
					break;
				}
				
				case USER_TOKEN_EXPIRED:
				{
					DialogHelper.showPromptTokenExpiredDialog(this);
					break;
				}
	
				case FAILED:
				{
					if (requestFailedLayout != null) 
					{
						requestFailedLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
				
				case NO_CONNECTION_AVAILABLE: 
				{
					if (requestNoInternetConnectionLayout != null) 
					{
						requestNoInternetConnectionLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
	
				case SUCCESS_WITH_NO_CONTENT: 
				{
					if (requestEmptyLayout != null) 
					{
						requestEmptyLayout.setVisibility(View.VISIBLE);
					}
					
					lastDataUpdatedCalendar = DateUtils.getNowWithGMTTimeZone();
					
					break;
				}
	
				case SUCCESS_WITH_CONTENT:
				default: 
				{
					if (requestSuccessfulLayout != null) 
					{
						requestSuccessfulLayout.setVisibility(View.VISIBLE);
					}
					
					if(loadedFromBackground)
					{
						String message = getString(R.string.generic_content_updated);
						
						ToastHelper.createAndShowShortToast(message);
						
						loadedFromBackground = false;
					}
					
					lastDataUpdatedCalendar = DateUtils.getNowWithGMTTimeZone();
					
					break;
				}
			}
		} 
		else 
		{
			Log.w(TAG, "Activity is null or finishing. No UI elements will be changed.");
		}
	}

	
	
	private void hideRequestStatusLayouts() 
	{
		if (requestSuccessfulLayout != null) 
		{
			requestSuccessfulLayout.setVisibility(View.GONE);
		}
		
		if (requestLoadingLayout != null) 
		{
			requestLoadingLayout.setVisibility(View.GONE);
		}

		if (requestNoInternetConnectionLayout != null) 
		{
			requestNoInternetConnectionLayout.setVisibility(View.GONE);
		}

		if (requestEmptyLayout != null)
		{
			requestEmptyLayout.setVisibility(View.GONE);
		}
		
		if(requestFailedLayout != null) 
		{
			requestFailedLayout.setVisibility(View.GONE);
		}
	}

	
	
	private void initCallbackLayouts() 
	{
		requestSuccessfulLayout = (RelativeLayout) findViewById(R.id.request_successful_layout);
		
		requestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_not_transparent);

		requestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);

		requestEmptyLayoutDetails = (FontTextView) findViewById(R.id.request_empty_details_tv);
		
		requestLoadingLayoutDetails = (FontTextView) findViewById(R.id.request_loading_details_tv);

		requestNoInternetConnectionLayout = (RelativeLayout) findViewById(R.id.no_connection_layout);
		
		requestFailedLayout = (RelativeLayout) findViewById(R.id.request_failed_main_layout);
		
		requestFailedRetryButton = (Button) findViewById(R.id.request_failed_reload_button);
		
		if(requestFailedRetryButton != null) 
		{
			requestFailedRetryButton.setOnClickListener(this);
		}

		requestNoInternetConnectionRetryButton = (Button) findViewById(R.id.no_connection_reload_button);

		if (requestNoInternetConnectionRetryButton != null) 
		{
			requestNoInternetConnectionRetryButton.setOnClickListener(this);
		}
	}

	
	
	protected void setEmptyLayoutDetailsMessage(String message) 
	{
		if (requestEmptyLayoutDetails != null) 
		{
			requestEmptyLayoutDetails.setText(message);
			requestEmptyLayoutDetails.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	protected void setLoadingLayoutDetailsMessage(String message) 
	{
		if (requestLoadingLayoutDetails != null)
		{
			requestLoadingLayoutDetails.setText(message);
			requestLoadingLayoutDetails.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	protected boolean wasActivityDataUpdatedMoreThan(int minutes)
	{
		boolean wasDataUpdatedMoreThan = false;
		
		if(lastDataUpdatedCalendar != null)
		{
			Calendar lastDataUpdatedCalendarWithincrement = (Calendar) lastDataUpdatedCalendar.clone();
			lastDataUpdatedCalendarWithincrement.add(Calendar.MINUTE, minutes);
			
			Calendar now = DateUtils.getNowWithGMTTimeZone();
			
			wasDataUpdatedMoreThan = lastDataUpdatedCalendarWithincrement.before(now);
		}
		
		return wasDataUpdatedMoreThan;
	}
	
	
	
	private void setBackgroundLoadingTimer()
	{
		if(backgroundLoadTimerValueInMinutes > -1)
		{
			int backgroundTimerValue = (int) (backgroundLoadTimerValueInMinutes*DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
			
			backgroundLoadTimer.schedule(new java.util.TimerTask()
			{
				@Override
				public void run()
				{
					loadedFromBackground = true;
					
					loadDataInBackground();
				}
			}, backgroundTimerValue, backgroundTimerValue);
		}
	}
	
	
	
	protected void setBackgroundLoadTimerValueInMinutes(int value)
	{
		backgroundLoadTimerValueInMinutes = value;
	}
}
