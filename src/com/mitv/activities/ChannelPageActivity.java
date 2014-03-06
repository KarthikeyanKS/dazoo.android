
package com.mitv.activities;



import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.ChannelPageListAdapter;
import com.mitv.models.TVBroadcast;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannel;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVChannelId;
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
	private TVChannelGuide channelGuide;
	private TVChannel channel;
	
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

		TVChannelId channelId = ContentManager.sharedInstance().getFromCacheSelectedTVChannelId();
		channel = ContentManager.sharedInstance().getFromCacheTVChannelById(channelId);
		
		initViews();
	}
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listview);

		View header = getLayoutInflater().inflate(R.layout.block_channelpage_header, null);
		listView.addHeaderView(header);

		channelIconIv = (ImageView) header.findViewById(R.id.channelpage_channel_icon_iv);
	}

	
	
	private void setFollowingBroadcasts(final ArrayList<TVBroadcast> currentAndUpcomingbroadcasts) 
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
		channelGuide = ContentManager.sharedInstance().getFromCacheTVChannelGuideUsingTVChannelIdForSelectedDay(channel.getChannelId());
		
		ImageAware imageAware = new ImageViewAware(channelIconIv, false);
		ImageLoader.getInstance().displayImage(channelGuide.getImageUrl(), imageAware);

		ArrayList<TVBroadcast> currentAndUpcomingbroadcasts = channelGuide.getCurrentAndUpcomingBroadcastsUsingCurrentTime();
		
		if(currentAndUpcomingbroadcasts != null && !currentAndUpcomingbroadcasts.isEmpty()) {
			setFollowingBroadcasts(currentAndUpcomingbroadcasts);
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		} else {
			updateUI(UIStatusEnum.FAILED);
		}
	}

	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier)
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			loadData();
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
	protected void removeActiveFragment(){}
}