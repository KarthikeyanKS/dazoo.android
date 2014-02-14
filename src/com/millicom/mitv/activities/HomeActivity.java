
package com.millicom.mitv.activities;



import java.util.ArrayList;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.mitv.fragments.TVHolderFragment;
import com.millicom.mitv.fragments.TVHolderFragment.OnViewPagerIndexChangedListener;
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.Consts;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.SecondScreenApplication.AppConfigurationListener;
import com.mitv.SecondScreenApplication.CheckApiVersionListener;
import com.mitv.adapters.ActionBarDropDownDateListAdapter;
import com.mitv.content.SSPageFragmentActivity;
import com.mitv.manager.AppConfigurationManager;
import com.mitv.manager.ApiClient;
import com.mitv.model.OldTVDate;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.DateUtilities;



public class HomeActivity 
	extends SSPageFragmentActivity 
	implements OnClickListener, ActionBar.OnNavigationListener, CheckApiVersionListener, AppConfigurationListener 
{
	private static final String					TAG					= "HomeActivity";
	private RelativeLayout						mTabTvGuide, mTabPopular, mTabFeed;
	private View 								mTabDividerLeft, mTabDividerRight;
	private ActionBar							mActionBar;
	private ActionBarDropDownDateListAdapter	mDayAdapter;
	public static int							mBroadcastSelection	= -1;
	private int									mTabSelectedIndex	= 0, mDateSelectedIndex;
	private ArrayList<OldTVDate>					mTvDates			= new ArrayList<OldTVDate>();
	private OldTVDate								mTvDateSelected;
	private boolean								mIsReady			= false, mIsFirstLoad = true, mIsChannelListChanged, mChannelsHasBeenChanged = false;

	private Fragment							mActiveFragment;

	private int									mStartingPosition	= 0;
	private boolean								mChannelUpdate		= false;

	private String 								mWelcomeToast = "";
	private boolean 							showWelcomeToast = true;

	private boolean 							mIsFromLogin, mIsFromSignup;
	private BroadcastReceiver					mBroadcastReceiverBadRequest, mBroadcastReceiverMyChannels, mBroadcastReceiverContent, mBroadcastReceiverDate;
	private int 								mLastDateSelectedIndex = 0;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "Homepageactivity oncreate");

		setContentView(R.layout.layout_home_activity);

		initReceivers();

		// If homeactivity is launched from login, fetch flag and later make toast.
		Intent intent =  getIntent();
		
		if (intent.hasExtra(Consts.INTENT_EXTRA_LOG_IN_ACTION)) 
		{
			mIsFromLogin = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_LOG_IN_ACTION);
			
			Log.d(TAG, "Extra INTENT_EXTRA_LOG_IN_ACTION is present");
		}
		else if (intent.hasExtra(Consts.INTENT_EXTRA_SIGN_UP_ACTION)) 
		{
			mIsFromSignup = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_SIGN_UP_ACTION);
			
			Log.d(TAG, "Extra INTENT_EXTRA_SIGN_UP_ACTION is present");
		}
		else
		{
			Log.d(TAG, "No extra is present");
		}

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		SecondScreenApplication.getInstance().setSelectedHour(Integer.valueOf(DateUtilities.getCurrentHourString()));
		SecondScreenApplication.getInstance().setCheckApiVersionListener(this);
		SecondScreenApplication.getInstance().setAppConfigurationListener(this);

		initViews();
		
		// HOCKEY-APP
		// checkForUpdates();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		registerReceivers();
		
		Log.d(TAG, "We have resumed!");
		
		if(ApiClient.ismShouldRefreshGuide() && !mIsFirstLoad)
		{
			loadPage();
		}
		
		
//		if (mChannelsHasBeenChanged)
//		{
//			removeActiveFragment();
//			MiTVStore.getInstance().clearAndReinitializeForMyChannels();
//			mChannelUpdate = true;
//			ApiClient.getGuide(mDateSelectedIndex, false);
			
//			mChannelsHasBeenChanged = false;
//		} 
//		else 
//		{
//			if (NetworkUtils.isConnectedAndHostIsReachable(this))
//			{
//				// update current hour
//				int hour = Integer.valueOf(DateUtilities.getCurrentHourString());
//				
//				((SecondScreenApplication) getApplicationContext()).setSelectedHour(hour);
//				
//				reloadPage();
//			} 
//			else 
//			{
//				updateUI(REQUEST_STATUS.FAILED);
//			}
//		}

		checkForCrashes();
	}

	
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		
		Log.d(TAG, "onPause");
		
		unregisterReceivers();
	}
	
	
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		
		Log.d(TAG, "onDestroy");
		
		unregisterReceivers();
		
		// clear the clock selection setting
		((SecondScreenApplication) getApplicationContext()).setSelectedHour(6);
	}
	
	
	
	private void loadContent()
	{
		if (NetworkUtils.isConnectedAndHostIsReachable(this)) 
		{
			String signupTitle = String.format("%s %s", getResources().getString(R.string.success_account_created_title), SecondScreenApplication.getInstance().getUserFirstName());

			if (mIsFromLogin) 
			{
				Toast toast = Toast.makeText(this, signupTitle, Toast.LENGTH_LONG);
				((TextView) ((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
				toast.show();
			}
			else if (mIsFromSignup) 
			{
				String signupText = getResources().getString(R.string.success_account_created_text);
				Toast toast = Toast.makeText(this, signupTitle + "\n" + signupText, Toast.LENGTH_LONG);
				((TextView) ((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
				toast.show();
			}
	
			SecondScreenApplication.getInstance().setisFirstStart(false);
			
			loadPage();
		} 
		else 
		{
			updateUI(REQUEST_STATUS.FAILED);
		}
	}
	
	

	private void initReceivers() 
	{
		mBroadcastReceiverBadRequest = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				// bad request to the backend: timeout or anything similar
				updateUI(REQUEST_STATUS.BAD_REQUEST);
			}
		};

		mBroadcastReceiverMyChannels = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context context, Intent intent)
			{
				Log.d(TAG, "CHANNELS HAVE CHANGED!!!!");
				
				mChannelsHasBeenChanged = true;
			}
		};

		mBroadcastReceiverContent = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				Log.d(TAG, " ON RECEIVE CONTENT");

				mIsReady = intent.getBooleanExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, false);
				

				Log.d(TAG, "content for TvGuide TABLE is ready: " + mIsReady);
				Log.d(TAG, "mDateSelectedIndex: " + mDateSelectedIndex);
				Log.d(TAG, "mChannelUpdate: " + mChannelUpdate);
				Log.d(TAG, "mFirstHit " + mIsFirstLoad);

				if (mIsReady) 
				{

					if (mIsFirstLoad) 
					{
						if (!pageHoldsData()) 
						{
							updateUI(REQUEST_STATUS.FAILED);
						}
					}
					else if (mChannelUpdate) 
					{
						attachFragment();
						
						mChannelUpdate = false;
					}
					else if (mLastDateSelectedIndex != mDateSelectedIndex) 
					{
						attachFragment();
						
						mLastDateSelectedIndex = mDateSelectedIndex;
					}
				}

//				if (mIsReady && (mDateSelectedIndex == 0) && !mChannelUpdate && mIsFirstLoad) {
//					if (!pageHoldsData()) {
//
//						updateUI(REQUEST_STATUS.FAILED);
//					}
//				} else if (mIsReady && (mDateSelectedIndex != 0) && mChannelUpdate) {
//					attachFragment();
//					mChannelUpdate = false;
//				} else if (mIsReady && (mDateSelectedIndex == 0) && mChannelUpdate && !mIsFirstLoad) {
//					attachFragment();
//					mChannelUpdate = false;
//				} else if (mIsReady && (mDateSelectedIndex != 0) && !mChannelUpdate && !mIsFirstLoad) {
//					attachFragment();
//					mChannelUpdate = false;
//				} else if (mIsReady && (mDateSelectedIndex == 0) && !mChannelUpdate && !mIsFirstLoad) {
//					attachFragment();
//					mChannelUpdate = false;
//				}
			}
		};

		mBroadcastReceiverDate	= new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				Log.d(TAG, "ON TVGUIDE SORTING VALUE CHANGED");

				mDateSelectedIndex = intent.getIntExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION, 0);

				removeActiveFragment();

				// reload the page with the new date
				reloadPage();
			}
		};
	}
	
	
	
	private void registerReceivers() 
	{
		// broadcast receiver for request timeout
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverBadRequest, new IntentFilter(Consts.INTENT_EXTRA_BAD_REQUEST));

		// broadcast receiver for my channels have changed
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverMyChannels, new IntentFilter(Consts.INTENT_EXTRA_MY_CHANNELS_CHANGED));

		// broadcast receiver for content availability
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverContent, new IntentFilter(Consts.INTENT_EXTRA_GUIDE_AVAILABLE));

		// broadcast receiver for date selection
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverDate, new IntentFilter(Consts.INTENT_EXTRA_TVGUIDE_SORTING));
	}

	
	
	private void unregisterReceivers() 
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverBadRequest);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverMyChannels);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverContent);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverDate);
	}

	
	
	private void checkForCrashes() 
	{
		CrashManager.register(this, Consts.HOCKEY_APP_TOKEN);
	}

	
	
	private void checkForUpdates() 
	{
		// Remove this for store builds!
		UpdateManager.register(this, Consts.HOCKEY_APP_TOKEN);
	}

	
	
	public void showUpdateDialog()
	{
		final Dialog dialog = new Dialog(this, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_prompt_update);
		
		dialog.setCancelable(false);

		Button okButton = (Button) dialog.findViewById(R.id.dialog_prompt_update_button);
		
		okButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				final String appPackageName = getPackageName(); 
				
				try
				{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} 
				catch (android.content.ActivityNotFoundException anfe)
				{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
				}
			}
		});
		
		dialog.show();
	}

	
	
	private void attachFragment()
	{
		mActiveFragment = TVHolderFragment.newInstance(mStartingPosition, mDateSelectedIndex, new OnViewPagerIndexChangedListener()
		{
			@Override
			public void onIndexSelected(int position)
			{
				Log.d(TAG, "we updated the position : " + position);

				mStartingPosition = position;
			}
		});

		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mActiveFragment, (Integer.toString(mStartingPosition) + Integer.toString(mDateSelectedIndex))).commitAllowingStateLoss();
	}
	
	

	private void removeActiveFragment()
	{
		try
		{
			if (mActiveFragment != null)
			{
				Log.d(TAG, "remove the active fragment");
				getSupportFragmentManager().beginTransaction().remove(mActiveFragment).commitAllowingStateLoss();
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	
	private void initViews() 
	{
		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);

		mTabPopular = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabPopular.setOnClickListener(this);

		mTabFeed = (RelativeLayout) findViewById(R.id.tab_me);
		mTabFeed.setOnClickListener(this);

		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		mTabPopular.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabFeed.setBackgroundColor(getResources().getColor(R.color.yellow));

		mActionBar = getSupportActionBar();

		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		Log.d(TAG, "INIT VIEWS");
	}

	
	
	@Override
	protected void loadPage()
	{
		// The the initial state to be loading
		updateUI(REQUEST_STATUS.LOADING);
		
		Log.d(TAG, "UI: LOADING");

		ApiClient.getInstance(this, mDateSelectedIndex).fetchContent();
	}

	
	
	private void reloadPage() 
	{
		if(ApiClient.ismIsRefreshingGuide() == false)
		{
			updateUI(REQUEST_STATUS.LOADING);
			
			ApiClient.getGuide(mDateSelectedIndex, false);
		}
		// TODO: Do something
	}

	
	
	@Override
	protected boolean pageHoldsData()
	{
		boolean result = false;

		Log.d(TAG, "pageHoldsData()");
		
		mTvDates = MiTVStore.getInstance().getTvDates();

		if (mTvDates != null) 
		{
			if (mTvDates.isEmpty())
			{
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			} 
			else
			{
				Log.d(TAG, "SUCCESSFUL");
				
				updateUI(REQUEST_STATUS.SUCCESSFUL);
				
				result = true;
			}
		}
		
		return result;
	}

	
	
	@Override
	protected void updateUI(REQUEST_STATUS status) 
	{
		if (super.requestIsSuccesfull(status))
		{
			mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

			mDayAdapter = new ActionBarDropDownDateListAdapter(mTvDates);
			mDayAdapter.setSelectedIndex(mDateSelectedIndex);
			mActionBar.setListNavigationCallbacks(mDayAdapter, this);

			attachFragment();
		}
	}

	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	
	
	@Override
	public boolean onNavigationItemSelected(int position, long id) 
	{
		if (mIsFirstLoad)
		{
			mDayAdapter.setSelectedIndex(0);
			mActionBar.setSelectedNavigationItem(0);
			mTvDateSelected = mTvDates.get(0);
			mIsFirstLoad = false;
			return true;
		}
		else 
		{
			mDayAdapter.setSelectedIndex(position);
			
			mTvDateSelected = mTvDates.get(position);
			
			Log.d(TAG, "ON NAVIGATION ITEM SELECTED: " + position);
			
			LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
					new Intent(Consts.INTENT_EXTRA_TVGUIDE_SORTING).putExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE, mTvDateSelected.getDate()).putExtra(
							Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION, position));
			
			return true;
		}
	}

	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		// No call for super(). Bug on API Level > 11.
	}

	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.tab_tv_guide:
				// we are already here, nothing happens
				break;
				
			case R.id.tab_activity:
				// tab to activity page
				Intent intentActivity = new Intent(HomeActivity.this, ActivityActivity.class);
				startActivity(intentActivity);
				break;
				
			case R.id.tab_me:
				// tab to activity page
				Intent intentMe = new Intent(HomeActivity.this, MyProfileActivity.class);
				startActivity(intentMe);
				break;
		}
	}

	
	
	@Override
	public void onApiVersionChecked(boolean needsUpdate) 
	{
		if (needsUpdate) 
		{
			showUpdateDialog();
		} 
		else 
		{
			loadContent();
		}
	}

	
	
	@Override
	public void onAppConfigurationListener() 
	{
		if (showWelcomeToast) 
		{
			mWelcomeToast = AppConfigurationManager.getInstance().getWelcomeToast();
			
			if(mWelcomeToast != null && !TextUtils.isEmpty(mWelcomeToast)) 
			{
				Toast.makeText(getApplicationContext(), mWelcomeToast, Toast.LENGTH_LONG).show();
			}
			
			showWelcomeToast = false;
		}
	}
}
