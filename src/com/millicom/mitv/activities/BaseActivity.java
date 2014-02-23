
package com.millicom.mitv.activities;



import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.utilities.GenericUtils;
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
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
	
	private ActionBar actionBar;
	private Activity activity;
	private OnClickListener onRequestFailedClickListener;

	
	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
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
		
		// add to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
		
		registerListeners();
		
		EasyTracker.getInstance(this).activityStart(this);

		String className = this.getClass().getName();
		
		GATrackingManager.sendView(className);
	}
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		loadData();
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

		activity = this;
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
		
		ContentManager.sharedInstance().checkNetworkConnectivity(this, this);
		
		NetworkUtils.isConnectedAndHostIsReachable(activity);
	}
	
	
	
	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult) 
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
				break;
			}
		}
	}
	
	
	
	protected void updateUIBaseElements(UIStatusEnum status) 
	{
		boolean activityNotNullOrFinishing = GenericUtils.isActivityNotNullOrFinishing(this);
		
		if (activityNotNullOrFinishing == false) 
		{
			hideRequestStatusLayouts();

			switch (status) 
			{	
				case LOADING:
				{
					requestLoadingLayout.setVisibility(View.VISIBLE);
					break;
				}
			
				case NO_CONNECTION_AVAILABLE:
				{
					requestBadLayout.setVisibility(View.VISIBLE);
					break;
				}
				
				case FAILED:
				{
					requestFailedLayout.setVisibility(View.VISIBLE);
					break;
				}
				
				case SUCCEEDED_WITH_DATA:
				{
					break;
				}
				
				case SUCCEEDED_WITH_EMPTY_DATA:
				{
					requestEmptyLayout.setVisibility(View.VISIBLE);
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
			requestFailedButton.setOnClickListener(onRequestFailedClickListener);
		}

		requestLoadingLayout = (RelativeLayout) findViewById(R.id.request_loading_main_layout);

		requestEmptyLayout = (RelativeLayout) findViewById(R.id.request_empty_main_layout);

		requestBadLayout = (RelativeLayout) findViewById(R.id.bad_request_main_layout);
		requestBadButton = (Button) findViewById(R.id.bad_request_reload_button);
	
		if (requestBadButton != null) 
		{
			requestBadButton.setOnClickListener(onRequestFailedClickListener);
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
				if ((this instanceof HomeActivity) == false) 
				{
					Intent intentActivity = new Intent(this, HomeActivity.class);
					
					startActivity(intentActivity);
				}
				break;
			}
			
			case R.id.tab_activity: 
			{
				if ((this instanceof ActivityActivity) == false)
				{
					Intent intentActivity = new Intent(this, ActivityActivity.class);
					
					startActivity(intentActivity);
					
					break;
				}
			}
			
			case R.id.tab_me: 
			{
				if ((this instanceof MyProfileActivity) == false) 
				{
					Intent intentMe = new Intent(this, MyProfileActivity.class);
					
					startActivity(intentMe);
					
					break;
				}
			}
			
			default:
			{
				Log.w(TAG, "Unknown activity id: " + id);
				
				break;
			}
		}
	}

	
	
	private void registerListeners() 
	{
		onRequestFailedClickListener = new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				int viewId = v.getId();
				
				switch (viewId) 
				{
					case R.id.request_failed_reload_button:
					{
						if (!NetworkUtils.isConnectedAndHostIsReachable(activity)) 
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
						if (!NetworkUtils.isConnectedAndHostIsReachable(activity)) 
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
						break;
					}
				}
			}
		};
	}	
}
