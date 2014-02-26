
package com.millicom.mitv.activities;



import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVProgram;
import com.mitv.R;
import com.mitv.adapters.RepetitionsListAdapter;


public class RepetitionsPageActivity 
	extends BaseActivity 
	implements OnClickListener
{
	@SuppressWarnings("unused")
	private static final String TAG = RepetitionsPageActivity.class.getName();

	private RelativeLayout tabTvGuide;
	private RelativeLayout tabProfile;
	private RelativeLayout tabActivity;
	
	private View tabDividerLeft;
	private View tabDividerRight;
	
	private ListView listView;
	private RepetitionsListAdapter listAdapter;
	private ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts;
	private TVProgram repeatingProgram;
	private TVBroadcastWithChannelInfo runningBroadcast;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.layout_repeating_list_activity);

		runningBroadcast = ContentManager.sharedInstance().getFromStorageSelectedBroadcastWithChannelInfo();
		repeatingBroadcasts = ContentManager.sharedInstance().getFromStorageRepeatingBroadcastsVerifyCorrect(runningBroadcast);

		initViews();
	}
		
	private void initViews() 
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.repetitions));
		listView = (ListView) findViewById(R.id.repeating_list_listview);
	}
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		// TODO Load data
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
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				listAdapter = new RepetitionsListAdapter(this, repeatingBroadcasts, repeatingProgram, runningBroadcast);
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