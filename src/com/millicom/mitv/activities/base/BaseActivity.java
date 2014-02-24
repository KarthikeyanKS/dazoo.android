
package com.millicom.mitv.activities.base;



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
import com.google.analytics.tracking.android.EasyTracker;
import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.FeedActivity;
import com.millicom.mitv.activities.HomeActivity;
import com.millicom.mitv.activities.MyProfileActivity;
import com.millicom.mitv.activities.SearchPageActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.TabSelectedEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.utilities.GenericUtils;
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.R;
import com.mitv.manager.GATrackingManager;



public abstract class BaseActivity 
	extends ActionBarActivity
	implements ActivityCallbackListener, OnClickListener 
{
	private static final String TAG = BaseActivity.class.getName();

	
	protected RelativeLayout tabTvGuide;
	protected RelativeLayout tabActivity;
	protected RelativeLayout tabProfile;
	protected View tabDividerLeft;
	protected View tabDividerRight;
	
	private View requestEmptyLayout;
	private View requestFailedLayout;
	private View requestLoadingLayout;
	
	private Button requestFailedButton;
	private Button requestBadButton;
	private View requestBadLayout;
	protected ActionBar actionBar;

	private TabSelectedEnum tabSelectedEnum = TabSelectedEnum.NOT_SET;
	
	
	/* Abstract Methods */
	
	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);
	
	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();
	
	/* This method implementation should deal with changes after the data has been fetched */
	protected abstract void onDataAvailable(FetchRequestResultEnum fetchRequestResult);	

	
	
	public void initTabViews() 
	{
		tabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		
		if(tabTvGuide != null)
		{
			tabTvGuide.setOnClickListener(this);
		}
		
		tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		
		if(tabActivity != null)
		{
			tabActivity.setOnClickListener(this);
		}

		tabProfile = (RelativeLayout) findViewById(R.id.tab_me);
	
		if(tabProfile != null)
		{
			tabProfile.setOnClickListener(this);
		}
		
		tabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		
		if(tabDividerLeft != null)
		{
			tabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		}
		
		tabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		if(tabDividerRight != null)
		{
			tabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		}

		if(tabSelectedEnum != TabSelectedEnum.NOT_SET) {
			switch (tabSelectedEnum) 
			{
				case TV_GUIDE: 
				{
					tabSelectedWasTVGuide();
					break;
				}
				
				case ACTIVITY_FEED:
				{
					tabSelectedWasActivityFeed();
					break;
				}
				
				case MY_PROFILE: 
				{
					tabSelectedWasMyProfile();
					break;
				}
				
				default:
				{
					Log.w(TAG, "Unknown tab selected");
				}
			}
		} else {
			/* Just created the activity */
			if(this instanceof HomeActivity) {
				tabSelectedWasTVGuide();
			} else if(this instanceof FeedActivity) {
				tabSelectedWasActivityFeed();
			} else if(this instanceof MyProfileActivity) {
				tabSelectedWasMyProfile();
			}
		}
	}
	
	
	
	private void tabSelectedWasTVGuide() 
	{
		if(tabTvGuide != null)
		{
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		}
		
		if(tabActivity != null)
		{
			tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
		
		if(tabProfile != null)
		{
			tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}
	
	
	
	private void tabSelectedWasActivityFeed() 
	{
		if(tabTvGuide != null)
		{
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
		
		if(tabActivity != null)
		{
			tabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		}
		
		if(tabProfile != null)
		{
			tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}
	
	
	
	private void tabSelectedWasMyProfile() 
	{
		if(tabTvGuide != null)
		{
			tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
		
		if(tabActivity != null)
		{
			tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
		
		if(tabProfile != null)
		{
			tabProfile.setBackgroundColor(getResources().getColor(R.color.red));
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
					tabSelectedEnum = TabSelectedEnum.TV_GUIDE;
					Intent intentActivity = new Intent(this, HomeActivity.class);
					startActivity(intentActivity);
				}
				break;
			}
			
			case R.id.tab_activity: 
			{
				if (!(this instanceof FeedActivity))
				{
					tabSelectedEnum = TabSelectedEnum.ACTIVITY_FEED;
					Intent intentActivity = new Intent(this, FeedActivity.class);
					startActivity(intentActivity);
				}
				break;
			}
			
			case R.id.tab_me: 
			{
				if (!(this instanceof MyProfileActivity)) 
				{
					tabSelectedEnum = TabSelectedEnum.MY_PROFILE;
					Intent intentMe = new Intent(this, MyProfileActivity.class);
					startActivity(intentMe);
				}
				break;
			}
			
			case R.id.request_failed_reload_button: 
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
		}
	}
	


	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) 
	{
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
		
		EasyTracker.getInstance(this).activityStart(this);

		String className = this.getClass().getName();
		
		GATrackingManager.sendView(className);
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		initCallbackLayouts();
		
		initTabViews();
	}
	
	
	
	@Override
	protected void onPause() 
	{
		super.onPause();
	}

	
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
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
	 * This method checks for Internet connectivity on the background thread
	 */
	protected void loadDataWithConnectivityCheck()
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().checkNetworkConnectivity(this);
	}
	
	
	
	@Override
	public final void onResult(FetchRequestResultEnum fetchRequestResult) 
	{
		switch(fetchRequestResult)
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
			
			default:
			{
				// The remaining cases should be handled by the subclasses
				onDataAvailable(fetchRequestResult);
				break;
			}
		}
	}
	
	
	
	protected void updateUIBaseElements(UIStatusEnum status) 
	{
		boolean activityNotNullOrFinishing = GenericUtils.isActivityNotNullOrFinishing(this);
		
		if (activityNotNullOrFinishing) 
		{
			hideRequestStatusLayouts();

			switch (status) 
			{	
				case LOADING:
				{
					if(requestLoadingLayout != null)
					{
						requestLoadingLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
							
				case NO_CONNECTION_AVAILABLE:
				{
					if(requestBadLayout != null)
					{
						requestBadLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
				
				case FAILED:
				{
					if(requestFailedLayout != null)
					{
						requestFailedLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
				
				case SUCCEEDED_WITH_EMPTY_DATA:
				{
					if(requestEmptyLayout != null)
					{
						requestEmptyLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
				
				case SUCCEEDED_WITH_DATA:
				default:
				{
					// Success or other cases should be handled by the subclasses
					break;
				}
			}
		}
		else
		{
			Log.w(TAG, "Activity is null or finishing. No UI elements will be changed.");
		}
	}

	
	
	public void hideRequestStatusLayouts() 
	{
		if (requestFailedLayout != null)
		{
			requestFailedLayout.setVisibility(View.GONE);
		}
		
		if (requestLoadingLayout != null)
		{
			requestLoadingLayout.setVisibility(View.GONE);
		}
		
		if (requestBadLayout != null)
		{
			requestBadLayout.setVisibility(View.GONE);
		}
	}

	
	
	public void initCallbackLayouts() 
	{
		requestFailedLayout = (RelativeLayout) findViewById(R.id.request_failed_main_layout);
		
		requestFailedButton = (Button) findViewById(R.id.request_failed_reload_button);
		
		if (requestFailedButton != null) 
		{
			requestFailedButton.setOnClickListener(this);
		}

		requestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_main_layout);

		requestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);

		requestBadLayout = (RelativeLayout) findViewById(R.id.bad_request_main_layout);
		
		requestBadButton = (Button) findViewById(R.id.bad_request_reload_button);
		
		if (requestBadButton != null) 
		{
			requestBadButton.setOnClickListener(this);
		}
	}
}
