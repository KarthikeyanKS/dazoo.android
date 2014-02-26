
package com.millicom.mitv.activities;



import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.R;
import com.mitv.adapters.UpcomingOrRepeatingBroadcastsListAdapter;


public class UpcomingEpisodesPageActivity 
	extends BaseActivity 
{
	@SuppressWarnings("unused")
	private static final String TAG = UpcomingEpisodesPageActivity.class.getName();
		
	private ListView listView;
	private UpcomingOrRepeatingBroadcastsListAdapter listAdapter;
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.layout_upcoming_episodes_list_activity);
		TVBroadcastWithChannelInfo runningBroadcast = ContentManager.sharedInstance().getFromStorageSelectedBroadcastWithChannelInfo();
		upcomingBroadcasts = ContentManager.sharedInstance().getFromStorageUpcomingBroadcasts(runningBroadcast);
		initViews();
		loadData();
	}
	
	private void initViews() 
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.upcoming_episodes));
		listView = (ListView) findViewById(R.id.upcoming_episodes_list_listview);
	}
		
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
	}
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
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
	public void onBackPressed() 
	{
		super.onBackPressed();
		finish();
	}
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				listAdapter = new UpcomingOrRepeatingBroadcastsListAdapter(this, upcomingBroadcasts, true);
				listView.setAdapter(listAdapter);
				listView.setVisibility(View.VISIBLE);
				break;
			}
	
			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}
	}
}