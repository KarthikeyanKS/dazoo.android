package com.millicom.secondscreen.content.homepage;

import java.util.ArrayList;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.ActionBarDropDownDateListAdapter;
import com.millicom.secondscreen.content.SSPageFragmentActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.content.tvguide.TVHolderFragment;
import com.millicom.secondscreen.content.tvguide.TVHolderFragment.OnViewPagerIndexChangedListener;
import com.millicom.secondscreen.http.NetworkUtils;
import com.millicom.secondscreen.manager.AppConfigurationManager;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.DateUtilities;

public class HomeActivity extends SSPageFragmentActivity implements OnClickListener, ActionBar.OnNavigationListener {

	private static final String					TAG					= "HomeActivity";
	private RelativeLayout						mTabTvGuide, mTabPopular, mTabFeed;
	private View 								mTabDividerLeft, mTabDividerRight;
	private ActionBar							mActionBar;
	private ActionBarDropDownDateListAdapter	mDayAdapter;
	public static int							mBroadcastSelection	= -1;
	private int									mTabSelectedIndex	= 0, mDateSelectedIndex;
	private ArrayList<TvDate>					mTvDates			= new ArrayList<TvDate>();
	private ArrayList<Channel>					mChannels;
	private String								mDate;
	private TvDate								mTvDateSelected;
	private boolean								mIsReady			= false, mFirstHit = true, mIsChannelListChanged, mStateChanged = false;

	private Fragment							mActiveFragment;

	private int									mStartingPosition	= 0;
	private boolean								mChannelUpdate		= false;
	
	private String 								mWelcomeToast = "";
	private boolean 							showWelcomeToast = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_home_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		// broadcast receiver for date selection
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverDate, new IntentFilter(Consts.INTENT_EXTRA_TVGUIDE_SORTING));

		// broadcast receiver for content availability
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverContent, new IntentFilter(Consts.INTENT_EXTRA_GUIDE_AVAILABLE));

		// broadcast receiver for my channels have changed
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverMyChannels, new IntentFilter(Consts.INTENT_EXTRA_MY_CHANNELS_CHANGED));

		// broadcast receiver for request timeout
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverBadRequest, new IntentFilter(Consts.INTENT_EXTRA_BAD_REQUEST));

		SecondScreenApplication.getInstance().setSelectedHour(Integer.valueOf(DateUtilities.getCurrentHourString()));
		
		initViews();
		
        boolean wasPreinstalled = SecondScreenApplication.getInstance().getWasPreinstalled();

		// HOCKEY-APP
		// checkForUpdates();

		if (!NetworkUtils.checkConnection(this)) {
			updateUI(REQUEST_STATUS.FAILED);
		} else {
			if (checkApiVersion() == true) {
				showUpdateDialog();
			}
			else {
				loadPage();
			}
		}
	}

	private void checkForCrashes() {
		CrashManager.register(this, Consts.HOCKEY_APP_TOKEN);
	}

	private void checkForUpdates() {
		// Remove this for store builds!
		UpdateManager.register(this, Consts.HOCKEY_APP_TOKEN);
	}
	
	private boolean checkApiVersion() {
		String apiVersion = SecondScreenApplication.getInstance().getApiVersion();
		if (apiVersion != null && !apiVersion.equals(Consts.API_VERSION)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void showUpdateDialog() {
		final Dialog dialog = new Dialog(this, R.style.remove_notification_dialog);
		dialog.setContentView(R.layout.dialog_prompt_update);
		dialog.setCancelable(false);
		
		Button okButton = (Button) dialog.findViewById(R.id.dialog_prompt_update_button);
		okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				final String appPackageName = getPackageName(); 
				final String appPackageName = "com.google.android.apps.maps";
				try {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
				}
				
//				dialog.dismiss();
			}
		});
		dialog.show();
	}

	BroadcastReceiver	mBroadcastReceiverBadRequest	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// bad request to the backend: timeout or anything similar
			updateUI(REQUEST_STATUS.BAD_REQUEST);
		}
	};

	BroadcastReceiver	mBroadcastReceiverMyChannels	= new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "CHANNELS HAVE CHANGED!!!!");
			mStateChanged = true;
		}
	};

	BroadcastReceiver	mBroadcastReceiverContent		= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, " ON RECEIVE CONTENT");

			mIsReady = intent.getBooleanExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, false);

			Log.d(TAG, "content for TvGuide TABLE is ready: " + mIsReady);
			Log.d(TAG, "mDateSelectedIndex: " + mDateSelectedIndex);
			Log.d(TAG, "mChannelUpdate: " + mChannelUpdate);
			Log.d(TAG, "mFirstHit " + mFirstHit);

			if (mIsReady && (mDateSelectedIndex == 0) && !mChannelUpdate && mFirstHit) {
				if (!pageHoldsData()) {

					updateUI(REQUEST_STATUS.FAILED);
				}
			} else if (mIsReady && (mDateSelectedIndex != 0) && mChannelUpdate) {
				attachFragment();
				mChannelUpdate = false;
			} else if (mIsReady && (mDateSelectedIndex == 0) && mChannelUpdate && !mFirstHit) {
				attachFragment();
				mChannelUpdate = false;
			} else if (mIsReady && (mDateSelectedIndex != 0) && !mChannelUpdate && !mFirstHit) {
				attachFragment();
				mChannelUpdate = false;
			} else if (mIsReady && (mDateSelectedIndex == 0) && !mChannelUpdate && !mFirstHit) {
				attachFragment();
				mChannelUpdate = false;
			}
		}
	};

	private void attachFragment() {

		mActiveFragment = TVHolderFragment.newInstance(mStartingPosition, mDateSelectedIndex, new OnViewPagerIndexChangedListener() {

			@Override
			public void onIndexSelected(int position) {

				Log.d(TAG, "we updated the position : " + position);

				mStartingPosition = position;
			}
		});

		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mActiveFragment).commitAllowingStateLoss();
	}

	private void removeActiveFragment() {

		try {

			if (mActiveFragment != null) {
				Log.d(TAG, "remove the active fragment");
				getSupportFragmentManager().beginTransaction().remove(mActiveFragment).commitAllowingStateLoss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	BroadcastReceiver	mBroadcastReceiverDate	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.d(TAG, "ON TVGUIDE SORTING VALUE CHANGED");

			mDate = intent.getStringExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE);
			mDateSelectedIndex = intent.getIntExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION, 0);

			removeActiveFragment();

			// reload the page with the new date
			reloadPage();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (mStateChanged) {
			removeActiveFragment();
			DazooStore.getInstance().clearAndReinitializeForMyChannels();
			mChannelUpdate = true;
			DazooCore.getGuide(mDateSelectedIndex, false);
			mStateChanged = false;
		} else {
			Log.d(TAG, "We have resumed!");
			if (!NetworkUtils.checkConnection(this)) {
				updateUI(REQUEST_STATUS.FAILED);
			} else {
				// update current hour
				int hour = Integer.valueOf(DateUtilities.getCurrentHourString());
				((SecondScreenApplication) getApplicationContext()).setSelectedHour(hour);
				reloadPage();
			}
		}
		checkForCrashes();
	}
	
	private void initViews() {
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

		final int actionBarColor = getResources().getColor(R.color.blue1);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		Log.d(TAG, "INIT VIEWS");
	}

	@Override
	protected void loadPage() {
		// The the initial state to be loading
		updateUI(REQUEST_STATUS.LOADING);
		Log.d(TAG, "UI: LOADING");

		DazooCore.getInstance(this, mDateSelectedIndex).fetchContent();
	}

	private void reloadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		DazooCore.getGuide(mDateSelectedIndex, false);
	}

	@Override
	protected boolean pageHoldsData() {
		boolean result = false;

		Log.d(TAG, "pageHoldsData()");
		// mTags = null;
		mTvDates = DazooStore.getInstance().getTvDates();
		// mTags = DazooStore.getInstance().getTags();

		if (mTvDates != null) {
			if (mTvDates.isEmpty()) {
				updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
			} else {
				Log.d(TAG, "SUCCESSFUL");
				updateUI(REQUEST_STATUS.SUCCESSFUL);
				result = true;
			}
		}
		return result;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {

		if (super.requestIsSuccesfull(status)) {

			mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

			mDayAdapter = new ActionBarDropDownDateListAdapter(mTvDates);
			mDayAdapter.setSelectedIndex(mDateSelectedIndex);
			mActionBar.setListNavigationCallbacks(mDayAdapter, this);

			attachFragment();
			if (showWelcomeToast) {
				mWelcomeToast = AppConfigurationManager.getInstance().getWelcomeToast();
				Toast.makeText(getApplicationContext(), mWelcomeToast, Toast.LENGTH_LONG).show();
				showWelcomeToast = false;
			}
			// createFragments();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		// Stop listening to broadcast events
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverDate);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverContent);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverMyChannels);

		// clear the clock selection setting
		((SecondScreenApplication) getApplicationContext()).setSelectedHour(6);
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		if (mFirstHit) {
			mDayAdapter.setSelectedIndex(0);
			mActionBar.setSelectedNavigationItem(0);
			mTvDateSelected = mTvDates.get(0);
			mFirstHit = false;
			return true;
		} else {
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
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
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
}
