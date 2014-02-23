
package com.millicom.mitv.activities;



import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ListView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
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

		// add the activity to the list of running activities
		SecondScreenApplication.sharedInstance().getActivityList().add(this);

		runningBroadcast = ContentManager.sharedInstance().getFromStorageSelectedBroadcastWithChannelInfo();
		upcomingBroadcasts = ContentManager.sharedInstance().getFromStorageUpcomingBroadcasts(runningBroadcast);

		initViews();

		super.initCallbackLayouts();
		
		loadData();
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

	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		if (upcomingBroadcasts != null && upcomingBroadcasts.isEmpty() != true) 
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		} 
		else 
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_EMPTY_DATA);
		}
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

	}
}