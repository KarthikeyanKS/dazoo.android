
package com.mitv.activities;



import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.UpcomingOrRepeatingBroadcastsListAdapter;
import com.mitv.models.TVBroadcastWithChannelInfo;


public abstract class RepetitionsOrUpcomingPage 
	extends BaseActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = RepetitionsOrUpcomingPage.class.getName();

	
	private ListView listView;
	private UpcomingOrRepeatingBroadcastsListAdapter listAdapter;
	private ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	private boolean usedForUpcomingEpisodes; /* else used for repeating... */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.layout_repeating_list_activity);
		TVBroadcastWithChannelInfo runningBroadcast = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
		if(this instanceof RepetitionsPageActivity) {
			broadcasts = ContentManager.sharedInstance().getFromCacheRepeatingBroadcastsVerifyCorrect(runningBroadcast);
			usedForUpcomingEpisodes = false;
		} else {
			broadcasts = ContentManager.sharedInstance().getFromCacheUpcomingBroadcastsVerifyCorrect(runningBroadcast);
			usedForUpcomingEpisodes = true;
		}
		
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
		
		String title;
		if(usedForUpcomingEpisodes) {
			title = getString(R.string.upcoming_episodes);
		} else {
			title = getString(R.string.repetitions);
		}
		actionBar.setTitle(title);
		listView = (ListView) findViewById(R.id.repeating_list_listview);
	}
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return true;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
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
			case SUCCESS_WITH_CONTENT:
			{
				listAdapter = new UpcomingOrRepeatingBroadcastsListAdapter(this, broadcasts, usedForUpcomingEpisodes);
				listView.setAdapter(listAdapter);
				listView.setVisibility(View.VISIBLE);
				break;
			}
	
			default:
			{
				// Do nothing
				break;
			}
		}
	}
}