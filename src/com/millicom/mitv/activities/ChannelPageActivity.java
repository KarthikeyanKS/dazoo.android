package com.millicom.mitv.activities;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.millicom.mitv.models.gson.TVChannelId;
import com.mitv.Consts;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.adapters.ActionBarDropDownDateListAdapter;
import com.mitv.adapters.ChannelPageListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class ChannelPageActivity extends BaseActivity implements OnClickListener, ActionBar.OnNavigationListener {

	private static final String					TAG	= "ChannelPageActivity";

	private ActionBar							mActionBar;
	private ActionBarDropDownDateListAdapter	mDayAdapter;
	private RelativeLayout						mTabTvGuide, mTabActivity, mTabProfile;
	private View								mTabDividerLeft, mTabDividerRight;
	private ListView							mFollowingBroadcastsLv;
	private ImageView							mChannelIconIv;
	private ChannelPageListAdapter				mFollowingBroadcastsListAdapter;
//	private String								mDate, mTvGuideDate; //, token; mChannelId
	private TVChannelId							mChannelId;
	private TVDate								mTvDateSelected, mDateTvGuide;
	private TVChannelGuide								mChannelGuide;
	private TVChannel								channel;
	private ArrayList<TVBroadcast>				mFollowingBroadcasts; //, mBroadcasts;
	private ArrayList<TVDate>					mTvDates;
	private int									mSelectedIndex	= -1, mIndexOfNearestBroadcast, mHour;
//	private MiTVStore							mitvStore;
	private boolean								mIsReady = false, mFirstHit = true, mIsToday = false; //mIsLoggedIn		= false, 
	private Handler								mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_channelpage_activity);

		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverDate, new IntentFilter(Consts.INTENT_EXTRA_CHANNEL_SORTING));
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverContent, new IntentFilter(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE));

		// get the info about the individual channel guide to be displayed from tv-guide listview
//		Intent intent = getIntent();
//		mChannelId = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_ID);
		//TODO TMP DATA intercommunication
//		mDateTvGuide = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHOSEN_DATE_TVGUIDE);
//		mHour = intent.getIntExtra(Consts.INTENT_EXTRA_TV_GUIDE_HOUR, 6);
		mHour = ContentManager.sharedInstance().getFromStorageSelectedHour();
		mChannelId = ContentManager.sharedInstance().getFromStorageSelectedTVChannelId();
		channel = ContentManager.sharedInstance().getFromStorageTVChannelById(mChannelId);
		
//		mChannelGuide = mitvStore.getChannelGuide(mDateTvGuide.getDate(), mChannelId);
		
		updateIsToday();

		super.initCallbackLayouts();

		initViews();
		loadPage();
	}

	BroadcastReceiver	mBroadcastReceiverDate		= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			mDate = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_SORTING_VALUE);
//			Log.d(TAG, "mDate" + mDate);

			// reload the page with the content to the new date
			updateUI(REQUEST_STATUS.LOADING);
			reloadPage();
		}
	};

	BroadcastReceiver	mBroadcastReceiverContent	= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mIsReady = intent.getBooleanExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE_VALUE, false);
			if (mIsReady) {

				//TODO handle this
//				mChannelGuide = mitvStore.getChannelGuide(mTvDateSelected.getDate(), mChannelId);
				
				mBroadcasts = mChannelGuide.getBroadcasts();
				mFollowingBroadcasts = null;
				mFollowingBroadcasts = TVBroadcast.getBroadcastsFromPosition(mBroadcasts, mIndexOfNearestBroadcast);
				setFollowingBroadcasts();
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
		}
	};

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		if (mFirstHit) {
			mDayAdapter.setSelectedIndex(mSelectedIndex);
			mActionBar.setSelectedNavigationItem(mSelectedIndex);
			mTvDateSelected = mTvDates.get(mSelectedIndex);
			mFirstHit = false;
			return true;
		} else {
			mDayAdapter.setSelectedIndex(position);
			mTvDateSelected = mTvDates.get(position);
			mSelectedIndex = position;
			
			Intent intent = new Intent(Consts.INTENT_EXTRA_CHANNEL_SORTING);
//			intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_SORTING_VALUE, mTvDateSelected.getDate());
			
			LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
			
			return true;
		}
	}

	private void reloadPage() {
		updateIsToday();
		mChannelGuide = null;
		mBroadcasts = null;

//		mChannelGuide = mitvStore.getChannelGuide(mTvDateSelected.getDate(), mChannelId);
		mChannelGuide = ContentManager.sharedInstance().getFromStorageTVChannelGuideUsingTVChannelIdForSelectedDay(mChannelId);

		if (mChannelGuide != null) {
			mBroadcasts = mChannelGuide.getBroadcasts();
			if (mIsToday) 
			{
				mIndexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndex(mBroadcasts, mHour, mTvDateSelected, 0);
			} 
			else 
			{
				mIndexOfNearestBroadcast = 0;
			}
			if (mIndexOfNearestBroadcast >= 0) {
				mFollowingBroadcasts = null;
				mFollowingBroadcasts = TVBroadcast.getBroadcastsFromPosition(mBroadcasts, mIndexOfNearestBroadcast);
			}
			setFollowingBroadcasts();
			
			mFollowingBroadcastsListAdapter.notifyDataSetChanged();

			updateUI(REQUEST_STATUS.SUCCESSFUL);
		} else {
			//TODO get guide from ContentManager
//			ApiClient.getGuide(mSelectedIndex, true);
			Log.d(TAG, "get guide");
		}
	}
	
	private void updateIsToday() {
		//TODO handle this
//		String tvDateToday = DateUtilities.todayDateAsTvDate();
//		String tvDate = null;
//		if(mTvDateSelected != null) {
//			tvDate = mTvDateSelected.getDate();
//		} else {
//			tvDate = mDateTvGuide.getDate();
//		}
//		
//		if (tvDate.equals(tvDateToday)) {
//			mIsToday = true;
//		} else {
//			mIsToday = false;
//		}
	}

	private void initViews() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mDayAdapter = new ActionBarDropDownDateListAdapter(mTvDates);

		int dateIndex = 0;
		for (int i = 0; i < mTvDates.size(); i++) {
			if (mTvDates.get(i).equals(mDateTvGuide)) {
				dateIndex = i;
				break;
			}
		}
		mSelectedIndex = dateIndex;

		mDayAdapter.setSelectedIndex(mSelectedIndex);
		mActionBar.setListNavigationCallbacks(mDayAdapter, this);

		mFollowingBroadcastsLv = (ListView) findViewById(R.id.listview);

		View header = getLayoutInflater().inflate(R.layout.block_channelpage_header, null);
		mFollowingBroadcastsLv.addHeaderView(header);

		mChannelIconIv = (ImageView) header.findViewById(R.id.channelpage_channel_icon_iv);

		// styling bottom navigation tabs
		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		mTabProfile.setOnClickListener(this);

		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
	
		// the highlighted tab in the Channel activity is TV Guide
		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		
		ImageAware imageAware = new ImageViewAware(mChannelIconIv, false);
		ImageLoader.getInstance().displayImage(mChannelGuide.getImageUrl(), imageAware);
	}

	private void setFollowingBroadcasts() {
		mFollowingBroadcastsListAdapter = new ChannelPageListAdapter(this, mFollowingBroadcasts);
		mFollowingBroadcastsLv.setAdapter(mFollowingBroadcastsListAdapter);

		mFollowingBroadcastsLv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// open the detail view for the individual broadcast
				Intent intent = new Intent(ChannelPageActivity.this, BroadcastPageActivity.class);

				// we take one position less as we have a header view
				int adjustedPosition = position - 1;
				if(adjustedPosition < 0) {
					/* Don't allow negative values */
					adjustedPosition = 0;
				}
				
				TVBroadcast broadcastSelected = mFollowingBroadcasts.get(adjustedPosition);
				TVBroadcastWithChannelInfo broadcastWithChannelInfo = new TVBroadcastWithChannelInfo(broadcastSelected);
				broadcastWithChannelInfo.setChannel(channel);
				
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				ContentManager.sharedInstance().setSelectedTVChannelId(channel.getChannelId());
				
				startActivity(intent);
				
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop listening to broadcast events
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverDate);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverContent);
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(ChannelPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.tab_activity:
			// tab to activity page
			Intent intentActivity = new Intent(ChannelPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			break;
		case R.id.tab_me:
			// tab to activity page
			Intent intentMe = new Intent(ChannelPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			break;
		}
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) 
	{
		if (super.requestIsSuccesfull(status)) 
		{
			Log.d(TAG, "succesfull!!!!! ");
		}
	}

	
	
	@Override
	protected void loadPage() 
	{
		mChannelGuide = ContentManager.sharedInstance().getFromStorageTVChannelGuideUsingTVChannelIdForSelectedDay(mChannelId);

		mBroadcasts = mChannelGuide.getBroadcasts();
		mTvDates = ContentManager.sharedInstance().getFromStorageTVDates();
		
		mIndexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndex(mBroadcasts, 0);
		
		if (mIndexOfNearestBroadcast >= 0)
		{
			mFollowingBroadcasts = TVBroadcast.getBroadcastsFromPosition(mBroadcasts, mIndexOfNearestBroadcast);
			setFollowingBroadcasts();
		}
		
	}
}