
package com.mitv.activities;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.RemindersListAdapter;
import com.mitv.models.comparators.TVBroadcastComparatorByTime;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.sql.NotificationDataSource;
import com.mitv.models.sql.NotificationSQLElement;



public class RemindersActivity 
	extends BaseContentActivity 
	implements OnClickListener
{
	@SuppressWarnings("unused")
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
		actionBar.setTitle(getString(R.string.reminders));
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listview);
		
		setEmptyLayoutDetailsMessage(getString(R.string.no_reminders));
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
			Collections.sort(tvBroadcasts, new TVBroadcastComparatorByTime());

			listAdapter = new RemindersListAdapter(this, tvBroadcasts);

			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
		}
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return true;
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
			case SUCCESS_WITH_CONTENT:
			{
				listView.setAdapter(listAdapter);
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
