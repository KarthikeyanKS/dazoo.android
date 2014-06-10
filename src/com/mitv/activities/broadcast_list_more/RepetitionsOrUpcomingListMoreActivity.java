
package com.mitv.activities.broadcast_list_more;



import java.util.ArrayList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.adapters.list.UpcomingOrRepeatingBroadcastsListAdapter;
import com.mitv.enums.BroadcastListAdapterTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;



public abstract class RepetitionsOrUpcomingListMoreActivity 
	extends BaseActivity
{
	private static final String TAG = RepetitionsOrUpcomingListMoreActivity.class.getName();

	
	private ListView listView;
	private UpcomingOrRepeatingBroadcastsListAdapter listAdapter;
	private ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	private BroadcastListAdapterTypeEnum broadcastListAdapterType;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_repeating_list_activity);
		
		TVBroadcastWithChannelInfo runningBroadcast = ContentManager.sharedInstance().getFromCacheLastSelectedBroadcastWithChannelInfo();
		
		if(this instanceof RepetitionsListMoreActivity) 
		{
			broadcasts = ContentManager.sharedInstance().getFromCacheRepeatingBroadcastsVerifyCorrect(runningBroadcast);
			
			broadcastListAdapterType = BroadcastListAdapterTypeEnum.PROGRAM_REPETITIONS;
		} 
		else
		{
			broadcasts = ContentManager.sharedInstance().getFromCacheUpcomingBroadcastsVerifyCorrect(runningBroadcast);
			
			broadcastListAdapterType = BroadcastListAdapterTypeEnum.UPCOMING_BROADCASTS;
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
		
		switch(broadcastListAdapterType)
		{
			case PROGRAM_REPETITIONS:
			{
				title = getString(R.string.repetitions);
				break;
			}
			
			case UPCOMING_BROADCASTS:
			{
				title = getString(R.string.upcoming_episodes);
				break;
			}
						
			default:
			{
				Log.w(TAG, "Unhnadled broadcast type enum");
				title = "";
				break;
			}
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
		ContentManager.sharedInstance().popFromSelectedBroadcastWithChannelInfo();
		
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
				listAdapter = new UpcomingOrRepeatingBroadcastsListAdapter(this, broadcasts, broadcastListAdapterType);
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