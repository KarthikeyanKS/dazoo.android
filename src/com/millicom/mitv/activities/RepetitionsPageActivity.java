
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
	
	private ActionBar mActionBar;
	private ListView mListView;
	private RepetitionsListAdapter mAdapter;
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
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	
	
	private void initViews() 
	{
		tabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		tabTvGuide.setOnClickListener(this);
		tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		tabActivity.setOnClickListener(this);
		tabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		tabProfile.setOnClickListener(this);

		tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

		tabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		tabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		tabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		tabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.repetitions));
		mListView = (ListView) findViewById(R.id.repeating_list_listview);
	}

	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();

		switch (id) 
		{
			case R.id.tab_tv_guide:
			{
				Intent intentHome = new Intent(RepetitionsPageActivity.this, HomeActivity.class);
				
				intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				startActivity(intentHome);
				
				break;
			}
			
			default:
			{
				// TODO Do something
			}
		}
	}
	
	
	
	public void onBackPressed() 
	{
		super.onBackPressed();
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
				mAdapter = new RepetitionsListAdapter(this, repeatingBroadcasts, repeatingProgram, runningBroadcast);
				mListView.setAdapter(mAdapter);
				mListView.setVisibility(View.VISIBLE);
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