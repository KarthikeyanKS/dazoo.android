
package com.millicom.mitv.activities;



import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ListView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.R;
import com.mitv.adapters.UpcomingEpisodesListAdapter;



public class UpcomingEpisodesPageActivity 
	extends BaseActivity 
{
	@SuppressWarnings("unused")
	private static final String TAG = UpcomingEpisodesPageActivity.class.getName();
		
	private ActionBar actionBar;
	private ListView listView;
	private UpcomingEpisodesListAdapter adapter;
	private TVBroadcastWithChannelInfo runningBroadcast;
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.layout_upcoming_episodes_list_activity);

		runningBroadcast = ContentManager.sharedInstance().getFromStorageSelectedBroadcastWithChannelInfo();
		
		upcomingBroadcasts = ContentManager.sharedInstance().getFromStorageUpcomingBroadcasts(runningBroadcast);

		initViews();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	
	
	private void initViews() 
	{
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.upcoming_episodes));
		listView = (ListView) findViewById(R.id.upcoming_episodes_list_listview);
	}
	
	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
	}
	
	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		// TODO NewArc - Do something here
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
				adapter = new UpcomingEpisodesListAdapter(this, upcomingBroadcasts, runningBroadcast);
				listView.setAdapter(adapter);
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