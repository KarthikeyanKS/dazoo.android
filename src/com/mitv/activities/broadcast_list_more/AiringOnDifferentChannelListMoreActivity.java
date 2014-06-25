
package com.mitv.activities.broadcast_list_more;



import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.adapters.list.AiringOnDifferentChannelListAdapter;
import com.mitv.asynctasks.other.RemoveAlreadyEndedBroadcastsTask;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public class AiringOnDifferentChannelListMoreActivity 
	extends BaseActivity
{
	private static final String TAG = AiringOnDifferentChannelListMoreActivity.class.getName();

	
	private ListView listView;
	private AiringOnDifferentChannelListAdapter listAdapter;
	private ArrayList<TVBroadcastWithChannelInfo> broadcasts;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_now_airing_more_list);
		
		TVBroadcastWithChannelInfo runningBroadcast = ContentManager.sharedInstance().getCacheManager().getLastSelectedBroadcastWithChannelInfo();
		
		broadcasts = ContentManager.sharedInstance().getCacheManager().getBroadcastsAiringOnDifferentChannels(runningBroadcast, false);
			
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
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
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
		ContentManager.sharedInstance().getCacheManager().popFromSelectedBroadcastWithChannelInfo();
		
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
				setData();
				break;
			}
	
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	
	
	public void setData() 
	{
		final Activity activity = this;
		
		RemoveAlreadyEndedBroadcastsTask removeAlreadyEndedBroadcastsTask = new RemoveAlreadyEndedBroadcastsTask(broadcasts, 0) {
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);		
				listAdapter = new AiringOnDifferentChannelListAdapter(activity, broadcasts);
				listView.setAdapter(listAdapter);
				listView.setVisibility(View.VISIBLE);
			}
		};
		
		removeAlreadyEndedBroadcastsTask.execute();
	}
	
}
