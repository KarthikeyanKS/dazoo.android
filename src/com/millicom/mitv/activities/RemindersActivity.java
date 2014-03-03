
package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.sql.NotificationDataSource;
import com.millicom.mitv.models.sql.NotificationSQLElement;
import com.mitv.R;
import com.mitv.adapters.RemindersListAdapter;



public class RemindersActivity 
	extends BaseContentActivity 
	implements OnClickListener
{
	private static final String TAG = RemindersActivity.class.getName();
	
	
	private ListView listView;
	private RemindersListAdapter listAdapter;

	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_reminders_activity);

		initLayout();
	}
	
	
	
	private void initLayout()
	{
		actionBar.setTitle(getResources().getString(R.string.reminders));
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listview);
		
		setEmptyLayoutDetailsMessage(getResources().getString(R.string.no_reminders));
	}
	
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		
	}
	
	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ArrayList<TVBroadcastWithChannelInfo> tvBroadcasts = new ArrayList<TVBroadcastWithChannelInfo>();

		NotificationDataSource notificationDataSource = new NotificationDataSource(this);

		List<NotificationSQLElement> notificationList = notificationDataSource.getAllNotifications();

		for (int i = 0; i < notificationList.size(); i++) 
		{
			NotificationSQLElement item = notificationList.get(i);

			TVBroadcastWithChannelInfo broadcast = new TVBroadcastWithChannelInfo(item);

			tvBroadcasts.add(broadcast);
		}

		if (tvBroadcasts.isEmpty())
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
		} 
		else
		{
			Collections.sort(tvBroadcasts, new TVBroadcast.BroadcastComparatorByTime());

			listAdapter = new RemindersListAdapter(this, tvBroadcasts);

			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		}
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		// Do nothing (the reminders list is  populated in a synchronous way)
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{
			case SUCCESS_WITH_NO_CONTENT:
			{
				// Do nothing (handled by parent activity)
				break;
			}
		
			case SUCCEEDED_WITH_DATA:
			{
				listView.setAdapter(listAdapter);
				break;
			}
	
			default:
			{
				Log.w(TAG, "Unhandled UIStatus");
				break;
			}
		}
	}
}
