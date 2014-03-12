
package com.mitv.activities;



import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannelId;
import com.mitv.models.TVProgram;
import com.mitv.populators.BroadcastMainBlockPopulator;
import com.mitv.populators.BroadcastRepetitionsBlockPopulator;
import com.mitv.populators.BroadcastUpcomingBlockPopulator;



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
	boolean isLiked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_broadcastpage_activity);

		initViews();
	}
	
	@Override
	protected void onResume() {
		
		Intent intent = getIntent();

		boolean needToDownloadBroadcastWithChannelInfo = intent.getBooleanExtra(Constants.INTENT_EXTRA_NEED_TO_DOWNLOAD_BROADCAST_WITH_CHANNEL_INFO, false);
		
		/* Used for when starting this activity from notification center in device or if you click on it from reminder list */
		if (needToDownloadBroadcastWithChannelInfo)
		{
			beginTimeInMillis = intent.getLongExtra(Constants.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);
			
			String channelIdAsString = intent.getStringExtra(Constants.INTENT_EXTRA_CHANNEL_ID);
			
			channelId = new TVChannelId(channelIdAsString);
		} else {
			broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
		}

		super.onResume();
	}
	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceBroadcastPageData(this, false, broadcastWithChannelInfo, channelId, beginTimeInMillis);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasEnoughDataToShowContent = false;
		if(broadcastWithChannelInfo != null) {
			hasEnoughDataToShowContent = true;
		}
		return hasEnoughDataToShowContent;
	}
	
	

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		if (fetchRequestResult.wasSuccessful()) {
			if (requestIdentifier == RequestIdentifierEnum.BROADCAST_PAGE_DATA) {
				broadcastWithChannelInfo = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
				repeatingBroadcasts = ContentManager.sharedInstance().getFromCacheRepeatingBroadcastsVerifyCorrect(broadcastWithChannelInfo);
				
				for(TVBroadcastWithChannelInfo broadcastWithoutProgramInfo : repeatingBroadcasts) {
					broadcastWithoutProgramInfo.setProgram(broadcastWithChannelInfo.getProgram());
				}
				
				upcomingBroadcasts = ContentManager.sharedInstance().getFromCacheUpcomingBroadcastsVerifyCorrect(broadcastWithChannelInfo);

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
//				purgeView();
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
	
	private void purgeView() {
//		if(hasPopulatedViews) {
//			scrollView = (ScrollView) findViewById(R.id.broadcast_scroll);
//			scrollView.updateViewLayout(scrollView, null);
//		}
	}
	
	private void populateBlocks()
	{
		hasPopulatedViews = true;
		BroadcastMainBlockPopulator mainBlockPopulator = new BroadcastMainBlockPopulator(this);
		
		mainBlockPopulator.createBlock(scrollView, broadcastWithChannelInfo);

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
			 repeatitionsBlock.createBlock(repeatingBroadcasts);
		 }
		
		 /* upcoming episodes */
		 if (upcomingBroadcasts != null && !upcomingBroadcasts.isEmpty()) {
			 BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(this, scrollView, true, broadcastWithChannelInfo);
			 upcomingBlock.createBlock(upcomingBroadcasts);
		 }
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}
}
