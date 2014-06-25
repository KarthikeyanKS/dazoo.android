
package com.mitv.activities;




import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.competition.EventPageActivity;
import com.mitv.adapters.list.ChannelPageListAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class ChannelPageActivity 
	extends TVDateSelectionActivity 
{
	private static final String TAG = ChannelPageActivity.class.getName();

	
	private ListView listView;
	private ImageView channelIconIv;
	private ChannelPageListAdapter listAdapter;
	
	private TVChannel channel;
	private List<TVBroadcast> currentAndUpcomingbroadcasts;


	
	@Override
	protected void setActivityCallbackListener()
	{
		activityCallbackListener = this;
	}
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_channelpage_activity);

		initLayout();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		/* It is necessary to set the correct channel, in order for the hasEnoughDataToShowContent and loadData to work properly */
		TVChannelId channelId = ContentManager.sharedInstance().getCacheManager().getSelectedTVChannelId();
		
		channel = ContentManager.sharedInstance().getCacheManager().getTVChannelById(channelId);
		
		super.onResume();
	}


	
	private void initLayout() 
	{
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listview);

		View header = getLayoutInflater().inflate(R.layout.block_channelpage_header, null);
		listView.addHeaderView(header);

		channelIconIv = (ImageView) header.findViewById(R.id.channelpage_channel_icon_iv);
		
		/* If there are no content to show, this message will appear */
		String message = getString(R.string.general_no_content_available);
		setEmptyLayoutDetailsMessage(message);
	}

	
	
	private void setFollowingBroadcasts(final List<TVBroadcast> currentAndUpcomingbroadcasts) 
	{
		listAdapter = new ChannelPageListAdapter(this, currentAndUpcomingbroadcasts);
		
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			{
				// We take one position less as we have a header view
				int adjustedPosition = position - 1;
				
				if (adjustedPosition < 0)
				{
					adjustedPosition = 0;
				}
				
				if (position == 0) {
					/* The first item in the list should not be clickable, it is just the channel logo */
					
				} else {

					TVBroadcast broadcastSelected = currentAndUpcomingbroadcasts.get(adjustedPosition);
					
					TVBroadcastWithChannelInfo broadcastWithChannelInfo = new TVBroadcastWithChannelInfo(broadcastSelected);
					
					broadcastWithChannelInfo.setChannel(channel);
	
					boolean isCompetitionEvent = false;
					long competitionId = 0;
					long eventId = 0;
					
					Intent intent;
					
					List<String> tags = broadcastSelected.getProgram().getTags();

					if (tags != null && !tags.isEmpty()) 
					{
						for (int i = 0; i < tags.size(); i++) 
						{
							if (tags.get(i).equals(Constants.FIFA_TAG_ID))
							{
								isCompetitionEvent = true;
								
								eventId = broadcastSelected.getEventId();

								/*
								 * TODO: Hard coded competition ID used here.
								 * 
								 */
								competitionId = Constants.FIFA_COMPETITION_ID;
								
								break;
							}
						}
					}
					
					if(isCompetitionEvent)
					{
						if (competitionId > 0 && eventId > 0) 
						{
							intent = new Intent(ChannelPageActivity.this, EventPageActivity.class);

							intent.putExtra(Constants.INTENT_COMPETITION_ID, competitionId);
							intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, eventId);
						}
						else
						{
							Log.w(TAG, "Competition search result had values competitionId: " + competitionId + " and eventId: " + eventId);
							
							intent = new Intent(ChannelPageActivity.this, BroadcastPageActivity.class);
							
							ContentManager.sharedInstance().getCacheManager().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
						}
					}
					else
					{
						intent = new Intent(ChannelPageActivity.this, BroadcastPageActivity.class);
						
						ContentManager.sharedInstance().getCacheManager().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					}
					
					ContentManager.sharedInstance().getCacheManager().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					
					ContentManager.sharedInstance().getCacheManager().setSelectedTVChannelId(channel.getChannelId());
					
					TrackingGAManager.sharedInstance().sendUserPressedBroadcastInChannelActivity(channel, broadcastSelected, position);
	
					startActivity(intent);
				}
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
		/* The data is fetched from the cache. Since there is no callback to the onDataAvailable function, we will invoke it manually */
		onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_CHANNEL);
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		if (channel != null) 
		{
			return ContentManager.sharedInstance().getCacheManager().containsTVChannelGuideUsingTVChannelIdForSelectedDay(channel.getChannelId());
		}
		return false;
	}

	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier)
	{
		TVChannelGuide channelGuide = ContentManager.sharedInstance().getCacheManager().getTVChannelGuideUsingTVChannelIdForSelectedDay(channel.getChannelId());
		
		ImageAware imageAware = new ImageViewAware(channelIconIv, false);
		
		if(channelGuide != null) 
		{
			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(channelGuide.getImageUrl(), imageAware);
			
			currentAndUpcomingbroadcasts = channelGuide.getCurrentAndUpcomingBroadcastsUsingCurrentTime();
		}
		
		if(currentAndUpcomingbroadcasts != null) 
		{
			if(!currentAndUpcomingbroadcasts.isEmpty())
			{	
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
			}
			else
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
			}
		}
		else 
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUI(status);
		
		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				setFollowingBroadcasts(currentAndUpcomingbroadcasts);
				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	
	
	@Override
	protected void attachFragment(){}
	
	
	@Override
	protected void removeActiveFragment(){}
}