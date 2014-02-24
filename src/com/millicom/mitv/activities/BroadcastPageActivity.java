
package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVDate;
import com.millicom.mitv.models.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.tvguide.BroadcastMainBlockPopulator;



public class BroadcastPageActivity 
	extends BaseActivity
{
	private static final String TAG = BroadcastPageActivity.class.getName();

	
	private TVBroadcast broadcast;
	private String channelLogoUrl;
	private ActionBar actionBar;
	private TVChannel channel;
	private TVChannelId channelId;
	private TVDate tvDate;
	private String broadcastPageUrl;
	private long beginTimeInMillis;
	private boolean isFromNotification = false;
	private boolean isFromActivity = false;
	private boolean mIsBroadcast = false;
	private boolean mIsUpcoming = false;
	private boolean mIsRepeat = false;
	private boolean isFromProfile = false;
	private Intent intent;
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;
	private ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts;
	private ScrollView scrollView;
	private int activityCardNumber;
	public static Toast toast;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_broadcastpage_activity);

		intent = getIntent();

		isFromActivity = intent.getBooleanExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, false);
		isFromProfile = intent.getBooleanExtra(Consts.INTENT_EXTRA_FROM_PROFILE, false);
		isFromNotification = intent.getBooleanExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, false);
		
		if (isFromNotification)
		{
			beginTimeInMillis = intent.getLongExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);
			
			String channelIdAsString = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_ID);
			
			channelId = new TVChannelId(channelIdAsString);
			
			String tvDateAsString = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE);
			
			//TODO NewArc use constructor from Felipe to construct TVDate using string representation e.g.: "2014-02-21"
			//tvDate = new TVDate(tvDateAsString);
			broadcastPageUrl = intent.getStringExtra(Consts.INTENT_EXTRA_BROADCAST_URL);
			
			channelLogoUrl = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_LOGO_URL);
			
			activityCardNumber = intent.getIntExtra(Consts.INTENT_EXTRA_ACTIVITY_CARD_NUMBER, -1);
		} 
		else
		{
			// TODO handle this, read date from ContentManager, the nonpersistent tmp set data....
			TVBroadcastWithChannelInfo broadcastWithChannelInfo = ContentManager.sharedInstance().getFromStorageSelectedBroadcastWithChannelInfo();
		}

		Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Log.d(TAG, "BeginTimeInMillis: " + String.valueOf(beginTimeInMillis));
		Log.d(TAG, "ChannelId: " + channelId);
		Log.d(TAG, "TvDate: " + tvDate.getId());
		Log.d(TAG, "BroadcastPageUrl: " + broadcastPageUrl);
		Log.d(TAG, "ChannelLogoUrl" + channelLogoUrl);
		Log.d(TAG, "from notification: " + isFromNotification);
		Log.d(TAG, "from Activity: " + isFromActivity);
		Log.d(TAG, "Activity card#" + String.valueOf(activityCardNumber));
		Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		initViews();
		
		/* Notify backend that we have entered the broadcast page for this broadcast, observe: no tracking will be performed if broadcast was created from notification */
		ContentManager.sharedInstance().performInternalTracking(broadcast);
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		//TODO NewArc fetch TVBroadcastWithChannelInfo version of TVBroadcast object (instance: variable "broadcast") here? or make Backend send TVBroadcastWithChannelInfo version with TVGuide!
//		getIndividualBroadcast(broadcastPageUrl);
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
				populateBlocks();
				break;
			}
	
			default:
			{
				// Do nothing
				break;
			}
		}
	}

	
	
	private void loadStartPage() 
	{
		//TODO NewArc fetch TVBroadcastWithChannelInfo version of TVBroadcast object (instance: variable "broadcast") here? or make Backend send TVBroadcastWithChannelInfo version with TVGuide!
		
//		boolean loadIndividualBroadcast = true;
//		boolean useStandardChannel = true;
//
//		if (NetworkUtils.isConnectedAndHostIsReachable(activity)) {
//			if (broadcastPageUrl == null)
//				broadcastPageUrl = Consts.URL_NOTIFY_BROADCAST_PREFIX + channelId + Consts.NOTIFY_BROADCAST_URL_MIDDLE + beginTimeInMillis;
//
//			if (!isFromActivity) {
//				if (!isFromNotification) {
//					if (ContentManager.sharedInstance().isLoggedIn()) {
//						Log.d(TAG, "LOGGED IN!");
//						broadcast = mitvStore.getBroadcast(mTvDate, channelId, beginTimeInMillis);
//						channel = mitvStore.getChannelById(channelId);
//
//						if (broadcast != null) {
//							loadIndividualBroadcast = false;
//							mIsBroadcast = true;
//
//							if (channel != null) {
//								broadcast.setChannel(channel);
//							} else {
//								TVChannel channel = new TVChannel();
//								channel.setChannelId(channelId);
//								broadcast.setChannel(channel);
//							}
//
//							if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(broadcast.getProgram().getProgramType())) {
//								getUpcomingSeriesBroadcasts(broadcast.getProgram().getSeries().getSeriesId());
//							} else {
//								mIsUpcoming = true;
//							}
//
//							getRepetitionBroadcasts(broadcast.getProgram().getProgramId());
//
//							updateUI(REQUEST_STATUS.SUCCESSFUL);
//						}
//					}
//
//				} else {
//					useStandardChannel = false;
//				}
//			}
//
//			if (loadIndividualBroadcast) {
//				Log.d(TAG, "NOT LOGGED IN");
//				if (useStandardChannel) {
//					channel = mitvStore.getChannelById(channelId);
//				}
//
//				getIndividualBroadcast(broadcastPageUrl);
//			}
//		} else {
//			updateUI(REQUEST_STATUS.FAILED);
//		}
	}

//	private void getIndividualBroadcast(String broadcastPageUrl) {
//		SSBroadcastPage.getInstance().getPage(broadcastPageUrl, new SSPageCallback() {
//			@Override
//			public void onGetPageResult(SSPageGetResult pageGetResult) {
//				broadcast = SSBroadcastPage.getInstance().getBroadcast();
//
//				if (broadcast != null) {
//					mIsBroadcast = true;
//
//					if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(broadcast.getProgram().getProgramType())) {
//						getUpcomingSeriesBroadcasts(broadcast.getProgram().getSeries().getSeriesId());
//					} else {
//						mIsUpcoming = true;
//					}
//
//					getRepetitionBroadcasts(broadcast.getProgram().getProgramId());
//
//					// if we have the data in the singleton about the channel - set it completely
//					if (broadcast.getChannel() == null) {
//						channel = mitvStore.getChannelById(channelId);
//						if (channel != null) {
//							broadcast.setChannel(channel);
//
//						} else {
//							// otherwise - just use the id that we got with the notification intent
//							TVChannel channel = new TVChannel();
//							channel.setChannelId(channelId);
//							if (channelLogoUrl != null) {
//								channel.setAllImageUrls(channelLogoUrl);
//							}
//
//							broadcast.setChannel(channel);
//						}
//					}
//
//					updateUI(REQUEST_STATUS.SUCCESSFUL);
//				}
//			}
//		});
//	}

	
	
	private void initViews()
	{
		actionBar = getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.broadcast_info));
		actionBar.setDisplayHomeAsUpEnabled(true);

		scrollView = (ScrollView) findViewById(R.id.broadcast_scroll);
	}

	
	
	private void populateBlocks()
	{
		BroadcastMainBlockPopulator mainBlockPopulator = new BroadcastMainBlockPopulator(this, scrollView);

		// TODO fix this
		// mainBlockPopulator.createBlock(mBroadcast);

		// Remove upcoming broadcasts with season 0 and episode 0
		LinkedList<TVBroadcast> upcomingToRemove = new LinkedList<TVBroadcast>();
		
		if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(broadcast.getProgram().getProgramType())) 
		{
			TVProgram program = broadcast.getProgram();
			
			if (program.getSeason().getNumber().equals("0") && program.getEpisodeNumber() == 0) 
			{
				for (int i = 0; i < upcomingBroadcasts.size(); i++) 
				{
					TVBroadcast b = upcomingBroadcasts.get(i);
					
					TVProgram p = b.getProgram();
					
					if (p.getSeason().getNumber().equals("0") && p.getEpisodeNumber() == 0)
					{
						upcomingToRemove.add(b);
					}
				}
			}
		}
		
		for (TVBroadcast b : upcomingToRemove) 
		{
			upcomingBroadcasts.remove(b);
		}

		// TODO fix this
		// // repetitions
		// if (mRepeatBroadcasts != null && mRepeatBroadcasts.isEmpty() != true) {
		// BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(mActivity, mScrollView,
		// mBroadcast);
		// repeatitionsBlock.createBlock(mRepeatBroadcasts, mBroadcast.getProgram());
		// }
		//
		// // upcoming episodes
		// if (mUpcomingBroadcasts != null && mUpcomingBroadcasts.isEmpty() != true) {
		// BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(mActivity, mScrollView, mIsSeries,
		// mBroadcast);
		// upcomingBlock.createBlock(mUpcomingBroadcasts, null);
		// }
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}

	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			// Log.d(TAG,"UP INTENT: " + upIntent);
			// if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
			// // This activity is NOT part of this app's task, so create a new task
			// // when navigating up, with a synthesized back stack.
			// TaskStackBuilder.create(this)
			// // Add all of this activity's parents to the back stack
			// .addNextIntentWithParentStack(upIntent)
			// // Navigate up to the closest parent
			// .startActivities();
			// } else {
			// Log.d(TAG,"GO UP TO TVGUIDE");
			// // This activity is part of this app's task, so simply
			// // navigate up to the logical parent activity.
			// NavUtils.navigateUpTo(this, upIntent);
			// }

			if (isFromActivity) {
				NavUtils.navigateUpTo(this, new Intent(BroadcastPageActivity.this, FeedActivity.class));
			} else if (isFromProfile) {
				NavUtils.navigateUpTo(this, new Intent(BroadcastPageActivity.this, MyProfileActivity.class));
			}

			else {
				// This activity is part of this app's task, so simply
				// navigate up to the logical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}

			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	// task to get the upcoming broadcasts from series
	private void getUpcomingSeriesBroadcasts(TVBroadcastWithChannelInfo broadcast) {
//		SSBroadcastsFromSeriesPage.getInstance().getPage(id, new SSPageCallback() {
//			@Override
//			public void onGetPageResult(SSPageGetResult aPageGetResult) {
//				upcomingBroadcasts = SSBroadcastsFromSeriesPage.getInstance().getSeriesUpcomingBroadcasts();
//
//				mIsSeries = true;
//				mIsUpcoming = true;
//				updateUI(REQUEST_STATUS.SUCCESSFUL);
//			}
//		});
		ContentManager.sharedInstance().getElseFetchFromServiceUpcomingBroadcasts(this, false, broadcast);
	}

	// task to get the broadcasts of the same program
	private void getRepetitionBroadcasts(TVBroadcastWithChannelInfo broadcast) {
//		SSBroadcastsFromProgramPage.getInstance().getPage(id, new SSPageCallback() {
//			@Override
//			public void onGetPageResult(SSPageGetResult aPageGetResult) {
//				repeatBroadcasts = SSBroadcastsFromProgramPage.getInstance().getProgramBroadcasts();
//				int hour;
//				OldTVDate tvDate;
//				if (isFromNotification) {
//					hour = Integer.valueOf(OldDateUtilities.getCurrentHourString());
//					tvDate = new OldTVDate();
//					tvDate.setDate(mTvDate);
//					// Log.d(TAG, "hour: " + hour + " TvDate: " + tvDate.getDate());
//				} else {
//					hour = ContentManager.sharedInstance().getFromStorageSelectedHour();
//					tvDate = MiTVStore.getInstance().getDate(mTvDate);
//					// Log.d(TAG, "hour: " + hour + " TvDate: " + tvDate.getDate());
//				}
//
//				int indexOfNearestBroadcast = 0;
//				if (tvDate != null) {
//					indexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndexFromTime(repeatBroadcasts, hour, tvDate);
//				} else {
//					Log.e(TAG, "TvDate was null");
//					indexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndex(repeatBroadcasts);
//				}
//
//				if (indexOfNearestBroadcast >= 0) {
//					repeatBroadcasts = TVBroadcast.getBroadcastsStartingFromPosition(indexOfNearestBroadcast, repeatBroadcasts, repeatBroadcasts.size());
//					// Log.d(TAG, "broadcasts from program: " + mRepeatBroadcasts.size());
//				}
//				mIsRepeat = true;
//				updateUI(REQUEST_STATUS.SUCCESSFUL);
//
//			}
//		});
		ContentManager.sharedInstance().getElseFetchFromServiceRepeatingBroadcasts(this, false, broadcast);
	}
}
