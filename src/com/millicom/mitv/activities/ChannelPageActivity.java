
package com.millicom.mitv.activities;



import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVDate;
import com.millicom.mitv.models.gson.TVChannelGuide;
import com.mitv.R;
import com.mitv.adapters.ChannelPageListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class ChannelPageActivity 
	extends TVDateSelectionActivity 
{
	@SuppressWarnings("unused")
	private static final String TAG = ChannelPageActivity.class.getName();

	
//	private ActionBar actionBar;
//	private ActionBarDropDownDateListAdapter dayAdapter;
	private ListView followingBroadcastsLv;
	private ImageView channelIconIv;
	private ChannelPageListAdapter followingBroadcastsListAdapter;
//	private BroadcastReceiver broadcastReceiverDate;
	// private String mDate, mTvGuideDate; //, token; mChannelId
	private TVChannelId channelId;
//	private TVDate dateTvGuide; // tvDateSelected;
	private TVChannelGuide channelGuide;
	private TVChannel channel;
	private ArrayList<TVBroadcast> followingBroadcasts; // , mBroadcasts;
	private ArrayList<TVDate> tvDates;
	private int selectedTVDateIndex = -1;
	private int indexOfNearestBroadcast;
	// private int selectdTVHour;
	// private boolean isReady = false;

	// private boolean isToday = false;
	// private boolean mIsLoggedIn = false,
	// private Handler handler;

	
	@Override
	protected void setActivityCallbackListener()
	{
		activityCallbackListener = this;
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_channelpage_activity);

		// LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiverContent, new
		// IntentFilter(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE));

		// get the info about the individual channel guide to be displayed from tv-guide listview
		// Intent intent = getIntent();
		// mChannelId = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_ID);
		
		// TODO TMP DATA intercommunication
		// mDateTvGuide = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHOSEN_DATE_TVGUIDE);
		// mHour = intent.getIntExtra(Consts.INTENT_EXTRA_TV_GUIDE_HOUR, 6);

		// selectdTVHour = ContentManager.sharedInstance().getFromStorageSelectedHour();
		tvDates = ContentManager.sharedInstance().getFromStorageTVDates();
		channelId = ContentManager.sharedInstance().getFromStorageSelectedTVChannelId();
		channel = ContentManager.sharedInstance().getFromStorageTVChannelById(channelId);

		// mChannelGuide = mitvStore.getChannelGuide(mDateTvGuide.getDate(), mChannelId);		
	}
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		updateIsToday();
		
		initViews();
	}
	
	

//	private void initBroadcastReceivers() {
//		broadcastReceiverDate = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				// mDate = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_SORTING_VALUE);
//				// Log.d(TAG, "mDate" + mDate);
//
//				// reload the page with the content to the new date
//				updateUI(REQUEST_STATUS.LOADING);
//				// TODO NewArc what to do here?
//				// reloadPage();
//			}
//		};
//	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		registerBroadcastReceivers();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		unregisterBroadcastReceivers();
//	}
//
//	private void registerBroadcastReceivers() {
//		if (broadcastReceiverDate != null) {
//			LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverDate, new IntentFilter(Consts.INTENT_EXTRA_CHANNEL_SORTING));
//		}
//	}
//
//	private void unregisterBroadcastReceivers() {
//		if (broadcastReceiverDate != null) {
//			LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverDate);
//		}
//	}

	// BroadcastReceiver mBroadcastReceiverContent = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// mIsReady = intent.getBooleanExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE_VALUE, false);
	// if (mIsReady) {
	//
	// //TODO handle this
	// // mChannelGuide = mitvStore.getChannelGuide(mTvDateSelected.getDate(), mChannelId);
	//
	// mBroadcasts = mChannelGuide.getBroadcasts();
	// mFollowingBroadcasts = null;
	// mFollowingBroadcasts = TVBroadcast.getBroadcastsFromPosition(mBroadcasts, mIndexOfNearestBroadcast);
	// setFollowingBroadcasts();
	// updateUI(REQUEST_STATUS.SUCCESSFUL);
	// }
	// }
	// };

//	@Override
//	public boolean onNavigationItemSelected(int position, long id) {
//		if (isFirstHit) {
//			dayAdapter.setSelectedIndex(selectedTVDateIndex);
//			actionBar.setSelectedNavigationItem(selectedTVDateIndex);
//			isFirstHit = false;
//			return true;
//		} else {
//			dayAdapter.setSelectedIndex(position);
//			ContentManager.sharedInstance().setTVDateSelectedUsingIndexAndFetchGuideForDay(this, position);
//			selectedTVDateIndex = position;
//			return true;
//		}
//	}

	// private void reloadPage() {
	// updateIsToday();
	// mChannelGuide = null;
	// mBroadcasts = null;
	//
	// // mChannelGuide = mitvStore.getChannelGuide(mTvDateSelected.getDate(), mChannelId);
	// mChannelGuide = ContentManager.sharedInstance().getFromStorageTVChannelGuideUsingTVChannelIdForSelectedDay(mChannelId);
	//
	// if (mChannelGuide != null) {
	// mBroadcasts = mChannelGuide.getBroadcasts();
	// if (mIsToday)
	// {
	// mIndexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndex(mBroadcasts, mHour, mTvDateSelected, 0);
	// }
	// else
	// {
	// mIndexOfNearestBroadcast = 0;
	// }
	// if (mIndexOfNearestBroadcast >= 0) {
	// mFollowingBroadcasts = null;
	// mFollowingBroadcasts = TVBroadcast.getBroadcastsFromPosition(mBroadcasts, mIndexOfNearestBroadcast);
	// }
	// setFollowingBroadcasts();
	//
	// mFollowingBroadcastsListAdapter.notifyDataSetChanged();
	//
	// updateUI(REQUEST_STATUS.SUCCESSFUL);
	// } else {
	// //TODO get guide from ContentManager
	// // ApiClient.getGuide(mSelectedIndex, true);
	// Log.d(TAG, "get guide");
	// }
	// }

	

	private void initViews() 
	{
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		followingBroadcastsLv = (ListView) findViewById(R.id.listview);

		View header = getLayoutInflater().inflate(R.layout.block_channelpage_header, null);
		followingBroadcastsLv.addHeaderView(header);

		channelIconIv = (ImageView) header.findViewById(R.id.channelpage_channel_icon_iv);
	}

	
	
	private void setFollowingBroadcasts() 
	{
		followingBroadcastsListAdapter = new ChannelPageListAdapter(this, followingBroadcasts);
		
		followingBroadcastsLv.setAdapter(followingBroadcastsListAdapter);

		followingBroadcastsLv.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			{
				// open the detail view for the individual broadcast
				Intent intent = new Intent(ChannelPageActivity.this, BroadcastPageActivity.class);

				// we take one position less as we have a header view
				int adjustedPosition = position - 1;
				
				if (adjustedPosition < 0)
				{
					/* Don't allow negative values */
					adjustedPosition = 0;
				}

				TVBroadcast broadcastSelected = followingBroadcasts.get(adjustedPosition);
				
				TVBroadcastWithChannelInfo broadcastWithChannelInfo = new TVBroadcastWithChannelInfo(broadcastSelected);
				
				broadcastWithChannelInfo.setChannel(channel);

				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				
				ContentManager.sharedInstance().setSelectedTVChannelId(channel.getChannelId());

				startActivity(intent);

			}
		});
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}

	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		channelGuide = ContentManager.sharedInstance().getFromStorageTVChannelGuideUsingTVChannelIdForSelectedDay(channelId);
		
		ImageAware imageAware = new ImageViewAware(channelIconIv, false);
		
		ImageLoader.getInstance().displayImage(channelGuide.getImageUrl(), imageAware);

		ArrayList<TVBroadcast> broadcasts = channelGuide.getBroadcasts();
		
		tvDates = ContentManager.sharedInstance().getFromStorageTVDates();

		indexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndex(broadcasts, 0);

		if (indexOfNearestBroadcast >= 0) 
		{
			followingBroadcasts = TVBroadcast.getBroadcastsFromPosition(broadcasts, indexOfNearestBroadcast);
			setFollowingBroadcasts();
		}

	}

	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult)
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		} 
		else 
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				// TODO NewArc - Do something here?
				break;
			}
	
			default:
			{
				// TODO NewArc handle fail? done in super class already
				break;
			}
		}
	}
	
	
	
	// TODO Dummy method
	private void updateIsToday() 
	{
		// String tvDateToday = DateUtilities.todayDateAsTvDate();
		// String tvDate = null;
		// if(mTvDateSelected != null) {
		// tvDate = mTvDateSelected.getDate();
		// } else {
		// tvDate = mDateTvGuide.getDate();
		// }
		//
		// if (tvDate.equals(tvDateToday)) {
		// mIsToday = true;
		// } else {
		// mIsToday = false;
		// }
	}
}