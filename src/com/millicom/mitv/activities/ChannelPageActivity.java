
package com.millicom.mitv.activities;



import java.util.ArrayList;

import android.content.Intent;
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
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.TVDate;
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

	private ListView listView;
	private ImageView channelIconIv;
	private ChannelPageListAdapter listAdapter;
	private TVChannelId channelId;
	private TVChannelGuide channelGuide;
	private TVChannel channel;
	private ArrayList<TVBroadcast> currentAndUpcomingbroadcasts;
	private ArrayList<TVDate> tvDates;
	private int selectedTVDateIndex = -1;
	
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

		initViews();
	}
	
	private void initViews() 
	{
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listview);

		View header = getLayoutInflater().inflate(R.layout.block_channelpage_header, null);
		listView.addHeaderView(header);

		channelIconIv = (ImageView) header.findViewById(R.id.channelpage_channel_icon_iv);
	}

	
	
	private void setFollowingBroadcasts() 
	{
		listAdapter = new ChannelPageListAdapter(this, currentAndUpcomingbroadcasts);
		
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() 
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

				TVBroadcast broadcastSelected = currentAndUpcomingbroadcasts.get(adjustedPosition);
				
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
		channelId = ContentManager.sharedInstance().getFromStorageSelectedTVChannelId();
		channel = ContentManager.sharedInstance().getFromStorageTVChannelById(channelId);

		tvDates = ContentManager.sharedInstance().getFromStorageTVDates();
		channelGuide = ContentManager.sharedInstance().getFromStorageTVChannelGuideUsingTVChannelIdForSelectedDay(channelId);
		
		ImageAware imageAware = new ImageViewAware(channelIconIv, false);
		ImageLoader.getInstance().displayImage(channelGuide.getImageUrl(), imageAware);

		
		ArrayList<TVBroadcast> broadcasts = channelGuide.getBroadcasts();
		int indexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndex(broadcasts, 0);
		if (indexOfNearestBroadcast >= 0) 
		{
			currentAndUpcomingbroadcasts = TVBroadcast.getBroadcastsFromPosition(broadcasts, indexOfNearestBroadcast);
			setFollowingBroadcasts();
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
	
	@Override
	protected void attachFragment(){/*Do nothing*/}
	@Override
	protected void removeActiveFragment(){/*Do nothing*/}
}