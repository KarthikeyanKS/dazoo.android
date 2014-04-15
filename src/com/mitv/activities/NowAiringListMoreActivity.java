
package com.mitv.activities;



import java.util.ArrayList;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.NowAiringListAdapter;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class NowAiringListMoreActivity 
	extends BaseActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = NowAiringListMoreActivity.class.getName();

	
	private ListView listView;
	private NowAiringListAdapter listAdapter;
	private ArrayList<TVBroadcastWithChannelInfo> broadcasts;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_now_airing_more_list);
		
		TVBroadcastWithChannelInfo runningBroadcast = ContentManager.sharedInstance().getFromCacheSelectedBroadcastWithChannelInfo();
		
		broadcasts = ContentManager.sharedInstance().getFromCacheBroadcastsAiringNowOnDifferentChannels(runningBroadcast, false);
			
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
		
		String title = getString(R.string.similar_broadcasts_airing_now);

		actionBar.setTitle(title);
		
		listView = (ListView) findViewById(R.id.broadcasts_now_airing_more_list_listview);
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
				listAdapter = new NowAiringListAdapter(this, broadcasts);
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
