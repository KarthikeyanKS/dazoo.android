
package com.mitv.activities;




import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.ChannelPageListAdapter;
import com.mitv.managers.ContentManager;
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
	@SuppressWarnings("unused")
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
		
		setContentView(R.layout.layout_channelpage_activity);

		initLayout();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		/* It is necessary to set the correct channel, in order for the hasEnoughDataToShowContent and loadData to work properly */
		TVChannelId channelId = ContentManager.sharedInstance().getFromCacheSelectedTVChannelId();
		
		channel = ContentManager.sharedInstance().getFromCacheTVChannelById(channelId);
		
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
	}

	
	
	private void setFollowingBroadcasts(final List<TVBroadcast> currentAndUpcomingbroadcasts) 
	{
		listAdapter = new ChannelPageListAdapter(this, currentAndUpcomingbroadcasts);
		
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			{
				// Open the detail view for the individual broadcast
				Intent intent = new Intent(ChannelPageActivity.this, BroadcastPageActivity.class);

				// We take one position less as we have a header view
				int adjustedPosition = position - 1;
				
				if (adjustedPosition < 0)
				{
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
		/* The data is fetched from the cache. Since there is no callback to the onDataAvailable function, we will invoke it manually */
		onResult(FetchRequestResultEnum.SUCCESS, RequestIdentifierEnum.TV_CHANNEL);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasTVChannelGuideUsingTVChannelIdForSelectedDay(channel.getChannelId());
	}

	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier)
	{
		TVChannelGuide channelGuide = ContentManager.sharedInstance().getFromCacheTVChannelGuideUsingTVChannelIdForSelectedDay(channel.getChannelId());
		
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