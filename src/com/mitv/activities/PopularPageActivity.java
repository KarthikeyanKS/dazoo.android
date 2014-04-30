
package com.mitv.activities;



import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.PopularListAdapter;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class PopularPageActivity 
	extends BaseContentActivity 
	implements OnClickListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = PopularPageActivity.class.getName();

	
	private ListView listView;
	private PopularListAdapter adapter;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (super.isRestartNeeded()) {
			return;
		}

		setContentView(R.layout.layout_popular_list_activity);

		initViews();
	}

	
	
	private void initViews() 
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		
		actionBar.setTitle(getString(R.string.popular));
		listView = (ListView) findViewById(R.id.popular_list_listview);
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
		updateUI(UIStatusEnum.LOADING);
		
		String loadingMessage = getString(R.string.loading_message_popular);
		
		setLoadingLayoutDetailsMessage(loadingMessage);
		
		ContentManager.sharedInstance().getElseFetchFromServicePopularBroadcasts(this, false);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasPopularBroadcasts();
	}
	
	

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch (fetchRequestResult) 
		{
			case SUCCESS: 
			{
				popularBroadcasts = ContentManager.sharedInstance().getFromCachePopularBroadcasts();
	
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
				break;
			}
			
			case SUCCESS_WITH_NO_CONTENT: 
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
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
			case SUCCESS_WITH_CONTENT: 
			{
				adapter = new PopularListAdapter(this, popularBroadcasts);
				listView.setAdapter(adapter);
				listView.setVisibility(View.VISIBLE);
				break;
			}
			
			case SUCCESS_WITH_NO_CONTENT: 
			{
				listView.setVisibility(View.GONE);
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