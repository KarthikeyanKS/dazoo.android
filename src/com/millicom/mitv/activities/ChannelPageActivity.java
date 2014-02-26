
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
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.Consts;
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

		TVChannelId channelId = ContentManager.sharedInstance().getFromStorageSelectedTVChannelId();
		channel = ContentManager.sharedInstance().getFromStorageTVChannelById(channelId);
		
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
				intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, this.getClass().getName());

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
		channelGuide = ContentManager.sharedInstance().getFromStorageTVChannelGuideUsingTVChannelIdForSelectedDay(channel.getChannelId());
		
		ImageAware imageAware = new ImageViewAware(channelIconIv, false);
		ImageLoader.getInstance().displayImage(channelGuide.getImageUrl(), imageAware);

		
		ArrayList<TVBroadcast> broadcasts = channelGuide.getBroadcasts();
		int indexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndex(broadcasts, 0);
		if (indexOfNearestBroadcast >= 0) 
		{
			ArrayList<TVBroadcast> currentAndUpcomingbroadcasts = TVBroadcast.getBroadcastsFromPosition(broadcasts, indexOfNearestBroadcast);
			setFollowingBroadcasts(currentAndUpcomingbroadcasts);
		}
		
		updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
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