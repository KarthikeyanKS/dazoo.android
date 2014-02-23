package com.millicom.mitv.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.fragments.TVHolderFragment;
import com.millicom.mitv.fragments.TVHolderFragment.OnViewPagerIndexChangedListener;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ActivityWithTabs;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.utilities.OldDateUtilities;

public class HomeActivity extends TVDateSelectionActivity implements ActivityCallbackListener, ActivityWithTabs {
	private static final String TAG = HomeActivity.class.getName();

//	private ActionBarDropDownDateListAdapter dayAdapter;
	private int selectedTagIndex = 0;

	private Fragment activeFragment;

	private String welcomeMessage = "";
	private boolean hasShowWelcomeToast = false;

//	private BroadcastReceiver broadcastReceiverDate;

	@Override
	protected void setActivityCallbackListener(){
		activityCallbackListener = this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_home_activity);

//		initReceivers();

		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		ContentManager.sharedInstance().setSelectedHour(Integer.valueOf(OldDateUtilities.getCurrentHourString()));

		initViews();

		tryShowWelcomeToast();
		attachFragment();

		// HOCKEY-APP
		// checkForUpdates();
	}

	@Override
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

		tabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

	}

	private void tryShowWelcomeToast() {
		if (!hasShowWelcomeToast) {
			welcomeMessage = ContentManager.sharedInstance().getFromStorageWelcomeMessage();

			if (welcomeMessage != null && !TextUtils.isEmpty(welcomeMessage)) {
				Toast.makeText(getApplicationContext(), welcomeMessage, Toast.LENGTH_LONG).show();
			}

			hasShowWelcomeToast = true;
		}
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		registerReceivers();
		// if(ApiClient.ismShouldRefreshGuide() && !mIsFirstLoad)
		// {
		// loadPage();
		// }

		// if (mChannelsHasBeenChanged)
		// {
		// removeActiveFragment();
		// MiTVStore.getInstance().clearAndReinitializeForMyChannels();
		// mChannelUpdate = true;
		// ApiClient.getGuide(mDateSelectedIndex, false);

		// mChannelsHasBeenChanged = false;
		// }
		// else
		// {
		// if (NetworkUtils.isConnectedAndHostIsReachable(this))
		// {
		// // update current hour
		// int hour = Integer.valueOf(DateUtilities.getCurrentHourString());
		//
		// ((SecondScreenApplication) getApplicationContext()).setSelectedHour(hour);
		//
		// reloadPage();
		// }
		// else
		// {
		// updateUI(REQUEST_STATUS.FAILED);
		// }
		// }

		// checkForCrashes();
//	}

//	@Override
//	protected void onPause() {
//		super.onPause();
//		unregisterReceivers();
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		unregisterReceivers();
//	}

	// private void loadContent()
	// {
	// if (NetworkUtils.isConnectedAndHostIsReachable(this))
	// {
	// String signupTitle = String.format("%s %s", getResources().getString(R.string.success_account_created_title),
	// SecondScreenApplication.getInstance().getUserFirstName());
	//
	// if (mIsFromLogin)
	// {
	// Toast toast = Toast.makeText(this, signupTitle, Toast.LENGTH_LONG);
	// ((TextView) ((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
	// toast.show();
	// }
	// else if (mIsFromSignup)
	// {
	// String signupText = getResources().getString(R.string.success_account_created_text);
	// Toast toast = Toast.makeText(this, signupTitle + "\n" + signupText, Toast.LENGTH_LONG);
	// ((TextView) ((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
	// toast.show();
	// }
	//
	// SecondScreenApplication.getInstance().setisFirstStart(false);
	//
	// loadPage();
	// }
	// else
	// {
	// updateUI(REQUEST_STATUS.FAILED);
	// }
	// }

//	private void initReceivers() {
//		broadcastReceiverDate = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				selectedDayIndex = intent.getIntExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION, 0);
//
//				/* Here we fetch guide for new date */
//				handleTVDateIndexSelect(selectedDayIndex);
//				removeActiveFragment();
//			}
//		};
//	}

	// mBroadcastReceiverBadRequest = new BroadcastReceiver()
	// {
	// @Override
	// public void onReceive(Context context, Intent intent)
	// {
	// // bad request to the backend: timeout or anything similar
	// updateUI(REQUEST_STATUS.BAD_REQUEST);
	// }
	// };
	//
	// mBroadcastReceiverMyChannels = new BroadcastReceiver()
	// {
	//
	// @Override
	// public void onReceive(Context context, Intent intent)
	// {
	// Log.d(TAG, "CHANNELS HAVE CHANGED!!!!");
	//
	// mChannelsHasBeenChanged = true;
	// }
	// };

	// mBroadcastReceiverContent = new BroadcastReceiver()
	// {
	// @Override
	// public void onReceive(Context context, Intent intent)
	// {
	// Log.d(TAG, " ON RECEIVE CONTENT");
	//
	// mIsReady = intent.getBooleanExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, false);
	//
	//
	// Log.d(TAG, "content for TvGuide TABLE is ready: " + mIsReady);
	// Log.d(TAG, "mDateSelectedIndex: " + mDateSelectedIndex);
	// Log.d(TAG, "mChannelUpdate: " + mChannelUpdate);
	// Log.d(TAG, "mFirstHit " + mIsFirstLoad);
	//
	// if (mIsReady)
	// {
	//
	// if (mIsFirstLoad)
	// {
	// if (!pageHoldsData())
	// {
	// updateUI(REQUEST_STATUS.FAILED);
	// }
	// }
	// else if (mChannelUpdate)
	// {
	// attachFragment();
	//
	// mChannelUpdate = false;
	// }
	// else if (mLastDateSelectedIndex != mDateSelectedIndex)
	// {
	// attachFragment();
	//
	// mLastDateSelectedIndex = mDateSelectedIndex;
	// }
	// }
	//
	// // if (mIsReady && (mDateSelectedIndex == 0) && !mChannelUpdate && mIsFirstLoad) {
	// // if (!pageHoldsData()) {
	// //
	// // updateUI(REQUEST_STATUS.FAILED);
	// // }
	// // } else if (mIsReady && (mDateSelectedIndex != 0) && mChannelUpdate) {
	// // attachFragment();
	// // mChannelUpdate = false;
	// // } else if (mIsReady && (mDateSelectedIndex == 0) && mChannelUpdate && !mIsFirstLoad) {
	// // attachFragment();
	// // mChannelUpdate = false;
	// // } else if (mIsReady && (mDateSelectedIndex != 0) && !mChannelUpdate && !mIsFirstLoad) {
	// // attachFragment();
	// // mChannelUpdate = false;
	// // } else if (mIsReady && (mDateSelectedIndex == 0) && !mChannelUpdate && !mIsFirstLoad) {
	// // attachFragment();
	// // mChannelUpdate = false;
	// // }
	// }
	// };

//	private void handleTVDateIndexSelect(int index) {
//		dayAdapter.setSelectedIndex(index);
//		ContentManager.sharedInstance().setTVDateSelectedUsingIndexAndFetchGuideForDay(this, index);
//	}
//
//	@Override
//	public boolean onNavigationItemSelected(int position, long id) {
//		handleTVDateIndexSelect(position);
//		return true;

		// if (mIsFirstLoad)
		// {
		// mDayAdapter.setSelectedIndex(0);
		// mActionBar.setSelectedNavigationItem(0);
		// mTvDateSelected = mTvDates.get(0);
		// mIsFirstLoad = false;
		// return true;
		// }
		// else
		// {
		// mDayAdapter.setSelectedIndex(position);
		//
		// mTvDateSelected = mTvDates.get(position);
		//
		// Log.d(TAG, "ON NAVIGATION ITEM SELECTED: " + position);
		//
		// LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
		// new Intent(Consts.INTENT_EXTRA_TVGUIDE_SORTING).putExtra(Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE,
		// mTvDateSelected.getDate()).putExtra(
		// Consts.INTENT_EXTRA_TVGUIDE_SORTING_VALUE_POSITION, position));
		//
		// return true;
		// }
//	}

//	private void registerReceivers() {
//		// broadcast receiver for date selection
//		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverDate, new IntentFilter(Consts.INTENT_EXTRA_TVGUIDE_SORTING));
//	}
//
//	private void unregisterReceivers() {
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverDate);
//	}

	private void attachFragment() {
		activeFragment = TVHolderFragment.newInstance(selectedTagIndex, new OnViewPagerIndexChangedListener() {
			@Override
			public void onIndexSelected(int position) {
				selectedTagIndex = position;
			}
		});
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, activeFragment, null).commitAllowingStateLoss();
	}

	private void removeActiveFragment() {
		try {
			if (activeFragment != null) {
				Log.d(TAG, "remove the active fragment");
				getSupportFragmentManager().beginTransaction().remove(activeFragment).commitAllowingStateLoss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initViews() {
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		
	}

	@Override
	protected void loadPage() {
		// TODO remove me!
	}

	// @Override
	// protected void loadPage()
	// {
	// // The the initial state to be loading
	// updateUI(REQUEST_STATUS.LOADING);
	//
	// Log.d(TAG, "UI: LOADING");
	//
	// ApiClient.getInstance(this, mDateSelectedIndex).fetchContent();
	// }

	// @Override
	// protected boolean pageHoldsData()
	// {
	// boolean result = false;
	//
	// Log.d(TAG, "pageHoldsData()");
	//
	// mTvDates = MiTVStore.getInstance().getTvDates();
	//
	// if (mTvDates != null)
	// {
	// if (mTvDates.isEmpty())
	// {
	// updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
	// }
	// else
	// {
	// Log.d(TAG, "SUCCESSFUL");
	//
	// updateUI(REQUEST_STATUS.SUCCESSFUL);
	//
	// result = true;
	// }
	// }
	//
	// return result;
	// }

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {

//			ArrayList<TVDate> tvDates = ContentManager.sharedInstance().getFromStorageTVDates();
//			dayAdapter = new ActionBarDropDownDateListAdapter(tvDates);
//			dayAdapter.setSelectedIndex(selectedDayIndex);
//			actionBar.setListNavigationCallbacks(dayAdapter, this);

			attachFragment();
		}
	}

	// @Override
	// public void onApiVersionChecked(boolean needsUpdate)
	// {
	// if (needsUpdate)
	// {
	// showUpdateDialog();
	// }
	// else
	// {
	// loadContent();
	// }
	// }

	// @Override
	// public void onAppConfigurationListener()
	// {
	// if (showWelcomeToast)
	// {
	// mWelcomeToast = AppConfigurationManager.getInstance().getWelcomeToast();
	//
	// if(mWelcomeToast != null && !TextUtils.isEmpty(mWelcomeToast))
	// {
	// Toast.makeText(getApplicationContext(), mWelcomeToast, Toast.LENGTH_LONG).show();
	// }
	//
	// showWelcomeToast = false;
	// }
	// }

	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult) {
		if (fetchRequestResult.wasSuccessful()) {
			attachFragment();
		} else {
			updateUI(REQUEST_STATUS.FAILED);
		}
	}
}
