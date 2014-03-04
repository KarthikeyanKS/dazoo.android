
package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.Toast;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.tvguide.BroadcastMainBlockPopulator;
import com.mitv.tvguide.BroadcastRepetitionsBlockPopulator;
import com.mitv.tvguide.BroadcastUpcomingBlockPopulator;



public class BroadcastPageActivity 
	extends BaseContentActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = BroadcastPageActivity.class.getName();

	private TVChannelId channelId;
	private long beginTimeInMillis;
	private boolean hasPopulatedViews = false;
	private TVBroadcastWithChannelInfo broadcastWithChannelInfo;
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;
	private ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts;
	private ScrollView scrollView;
	public static Toast toast;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_broadcastpage_activity);

		Intent intent = getIntent();

		boolean needToDownloadBroadcastWithChannelInfo = intent.getBooleanExtra(Consts.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, false);
		
		/* Used for when starting this activity from notification center in device or if you click on it from reminder list */
		if (needToDownloadBroadcastWithChannelInfo)
		{
			beginTimeInMillis = intent.getLongExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);
			
			String channelIdAsString = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_ID);
			
			channelId = new TVChannelId(channelIdAsString);
		} else {
			broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
		}


		initViews();
	}
	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		ContentManager.sharedInstance().getElseFetchFromServiceBroadcastPageData(this, false, broadcastWithChannelInfo, channelId, beginTimeInMillis);
	}
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		if (fetchRequestResult.wasSuccessful()) {
			if (requestIdentifier == RequestIdentifierEnum.BROADCAST_PAGE_DATA) {
				broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
				repeatingBroadcasts = ContentManager.sharedInstance().getFromCacheRepeatingBroadcasts();
				upcomingBroadcasts = ContentManager.sharedInstance().getFromCacheUpcomingBroadcasts();

				updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
			}
		} else {
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
				/* Now we have broadcastWithChannelInfo object => notify backend that we have entered the broadcast page for this broadcast, observe: no tracking will be performed if broadcast was created from notification */
				ContentManager.sharedInstance().performInternalTracking(broadcastWithChannelInfo);
				if(!hasPopulatedViews) {
					populateBlocks();
				}
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
		actionBar.setTitle(getResources().getString(R.string.broadcast_info));
		actionBar.setDisplayHomeAsUpEnabled(true);
		scrollView = (ScrollView) findViewById(R.id.broadcast_scroll);
	}

	private boolean isProgramIrrelevantAndShouldBeDeleted(TVProgram program) {
		boolean isProgramIrrelevantAndShouldBeDeleted = (program.getSeason().getNumber() == 0 && program.getEpisodeNumber() == 0);
		
		return isProgramIrrelevantAndShouldBeDeleted;
	}
	
	private void populateBlocks()
	{
		hasPopulatedViews = true;
		BroadcastMainBlockPopulator mainBlockPopulator = new BroadcastMainBlockPopulator(this, scrollView);

		 mainBlockPopulator.createBlock(broadcastWithChannelInfo);

		 //TODO NewArc should we remove those irrelevant broadcasts in the AsynkTask (GetTVBroadcastsFromSeries) instead?
		/* Remove upcoming broadcasts with season 0 and episode 0 */
		LinkedList<TVBroadcast> upcomingBroadcastsToRemove = new LinkedList<TVBroadcast>();
		
		ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();
		switch (programType) {
		case TV_EPISODE:{
			TVProgram program = broadcastWithChannelInfo.getProgram();
			
			if (isProgramIrrelevantAndShouldBeDeleted(program)) 
			{
				for (TVBroadcast upcomingBroadcast : upcomingBroadcasts) {
					TVProgram programFromUpcomingBroadcast = upcomingBroadcast.getProgram();
					
					if (isProgramIrrelevantAndShouldBeDeleted(programFromUpcomingBroadcast))
					{
						upcomingBroadcastsToRemove.add(upcomingBroadcast);
					}
				}
			}
			break;
		}
		default: {
			/* Do nothing if it is not a TV Episode */
			break;
		}
		}
		
		for (TVBroadcast upcomingBroadcastToRemove : upcomingBroadcastsToRemove) 
		{
			upcomingBroadcasts.remove(upcomingBroadcastToRemove);
		}

		
		 /* Repetitions */
		 if (repeatingBroadcasts != null && !repeatingBroadcasts.isEmpty()) {
			 BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(this, scrollView, broadcastWithChannelInfo);
			 repeatitionsBlock.createBlock(repeatingBroadcasts, broadcastWithChannelInfo.getProgram());
		 }
		
		 /* upcoming episodes */
		 if (upcomingBroadcasts != null && !upcomingBroadcasts.isEmpty()) {
			 BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(this, scrollView, true, broadcastWithChannelInfo);
			 upcomingBlock.createBlock(upcomingBroadcasts, null);
		 }
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}
}
