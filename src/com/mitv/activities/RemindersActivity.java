
package com.mitv.activities;



import java.util.Collections;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.adapters.list.RemindersListAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.comparators.NotificationComparatorByBeginTime;
import com.mitv.models.objects.mitvapi.Notification;



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

		if (isRestartNeeded()) {
			return;
		}
		
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
		
		List<Notification> notifications = ContentManager.sharedInstance().getCacheManager().getNotifications();

		Collections.sort(notifications, new NotificationComparatorByBeginTime());

		listAdapter = new RemindersListAdapter(this, notifications);

		if (notifications != null && notifications.isEmpty() == false) {
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
		}
		else {
			updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
		}
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
