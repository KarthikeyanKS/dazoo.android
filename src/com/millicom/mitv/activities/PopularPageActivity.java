
package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.R;
import com.mitv.adapters.PopularListAdapter;



public class PopularPageActivity 
	extends BaseActivity 
	implements OnClickListener, ActivityCallbackListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = PopularPageActivity.class.getName();

	
	private ActionBar mActionBar;
	private ListView mListView;
	private PopularListAdapter mAdapter;
	private ArrayList<TVBroadcastWithChannelInfo> mPopularBroadcasts;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_popular_list_activity);	
		
		initViews();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	
	
	private void initViews() 
	{
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.popular));
		mListView = (ListView) findViewById(R.id.popular_list_listview);
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
		
		ContentManager.sharedInstance().getElseFetchFromServicePopularBroadcasts(this, false);
	}

	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult) 
	{
		super.onResult(fetchRequestResult);
		
		switch(fetchRequestResult)
		{
			case SUCCESS:
			{
				mPopularBroadcasts = ContentManager.sharedInstance().getFromStoragePopularBroadcasts();
				
				updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
				
				break;
			}
			
			default:
			{
				updateUI(UIStatusEnum.FAILED);
				
				break;
			}
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
				Collections.sort(mPopularBroadcasts, new TVBroadcast.BroadcastComparatorByTime());
				mAdapter = new PopularListAdapter(this, mPopularBroadcasts);
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

	

	// @Override
	// protected void loadPage()
	// {
	// updateUI(REQUEST_STATUS.LOADING);
	//
	// if (NetworkUtils.isConnectedAndHostIsReachable(this))
	// {
	// if (MiTVStore.getInstance().getPopularFeed().size() > 0)
	// {
	// Log.d(TAG, "RESTORED POPULAR");
	//
	// // mPopularBroadcasts = MiTVStore.getInstance().getPopularFeed();
	// mPopularBroadcasts = ContentManager.sharedInstance().getFromStoragePo
	//
	// updateUI(REQUEST_STATUS.SUCCESSFUL);
	// }
	// else
	// {
	// GetPopularTask getPopularTask = new GetPopularTask();
	//
	// getPopularTask.execute();
	// }
	// }
	// else
	// {
	// updateUI(REQUEST_STATUS.FAILED);
	// }
	// }

	
	
	
}