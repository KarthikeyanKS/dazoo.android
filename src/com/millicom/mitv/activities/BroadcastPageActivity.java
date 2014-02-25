
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.activities.base.BaseContentActivity;
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
import com.mitv.tvguide.BroadcastRepetitionsBlockPopulator;
import com.mitv.tvguide.BroadcastUpcomingBlockPopulator;



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
	private boolean needToDownloadBroadcastWithChannelInfo = false;
	private boolean isFromActivity = false;
	private boolean mIsBroadcast = false;
	private boolean mIsUpcoming = false;
	private boolean mIsRepeat = false;
	private boolean isFromProfile = false;
	private TVBroadcastWithChannelInfo broadcastWithChannelInfo;
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
		needToDownloadBroadcastWithChannelInfo = intent.getBooleanExtra(Consts.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, false);
		
		/* Used for when starting this activity from notification center in device or if you click on it from reminder list */
		if (needToDownloadBroadcastWithChannelInfo)
		{
			beginTimeInMillis = intent.getLongExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);
			
			String channelIdAsString = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_ID);
			
			channelId = new TVChannelId(channelIdAsString);
			
//			String tvDateAsString = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE);
			
//			tvDate = new TVDate(tvDateAsString);
//			broadcastPageUrl = intent.getStringExtra(Consts.INTENT_EXTRA_BROADCAST_URL);
			
//			channelLogoUrl = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_LOGO_URL);
		} 
		else
		{
			// TODO NewArc handle this, read date from ContentManager
			broadcastWithChannelInfo = ContentManager.sharedInstance().getFromStorageSelectedBroadcastWithChannelInfo();
			channelId = broadcastWithChannelInfo.getChannel().getChannelId();
		}

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
		if(needToDownloadBroadcastWithChannelInfo) {
			//TODO NewArc fetch TVBroadcastWithChannelInfo version of TVBroadcast object if came from Notification
//			getIndividualBroadcast(broadcastPageUrl);
			ContentManager.sharedInstance().fetchFromServiceIndividualBroadcast(this, channelId, beginTimeInMillis);
		}
		updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		
	
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

		 mainBlockPopulator.createBlock(broadcastWithChannelInfo);

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

		// TODO NewArc fix this
		 // repetitions
		 if (repeatingBroadcasts != null && !repeatingBroadcasts.isEmpty()) {
			 BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(this, scrollView, broadcastWithChannelInfo);
			 repeatitionsBlock.createBlock(repeatingBroadcasts, broadcastWithChannelInfo.getProgram());
		 }
		
		 // upcoming episodes
		 if (upcomingBroadcasts != null && !upcomingBroadcasts.isEmpty()) {
			 //TODO NewArc finish this
//			 BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(this, scrollView, isSer, broadcastWithChannelInfo);
//			 upcomingBlock.createBlock(upcomingBroadcasts, null);
		 }
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
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
		ContentManager.sharedInstance().getElseFetchFromServiceUpcomingBroadcasts(this, false, broadcast);
	}

	// task to get the broadcasts of the same program
	private void getRepetitionBroadcasts(TVBroadcastWithChannelInfo broadcast) {
		ContentManager.sharedInstance().getElseFetchFromServiceRepeatingBroadcasts(this, false, broadcast);
	}
}
