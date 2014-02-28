
package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.R;
import com.mitv.adapters.RemindersListAdapter;
import com.mitv.interfaces.RemindersCountInterface;
import com.mitv.model.NotificationDbItem;
import com.mitv.notification.NotificationDataSource;



public class RemindersActivity 
	extends BaseActivity 
	implements RemindersCountInterface, OnClickListener
{
	@SuppressWarnings("unused")
	private static final String TAG = RemindersActivity.class.getName();
	
	private ListView mListView;
	private RemindersListAdapter mAdapter;
	private RelativeLayout mTabTvGuide;
	private RelativeLayout mTabActivity;
	private RelativeLayout mTabProfile;
	private View mTabDividerLeft;
	private View mTabDividerRight;
	private TextView mErrorTv;
		

	
	
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

		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		mTabProfile.setOnClickListener(this);

		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));

		mErrorTv = (TextView) findViewById(R.id.reminders_error_tv);
		mListView = (ListView) findViewById(R.id.listview);
	}

	
	
	private void populateViews() 
	{
		ArrayList<TVBroadcastWithChannelInfo> broadcasts = new ArrayList<TVBroadcastWithChannelInfo>();

		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		
		List<NotificationDbItem> notificationList = notificationDataSource.getAllNotifications();
		
		for (int i = 0; i < notificationList.size(); i++) 
		{
			NotificationDbItem item = notificationList.get(i);
			
			TVBroadcastWithChannelInfo broadcast = new TVBroadcastWithChannelInfo(item);
			
			broadcasts.add(broadcast);
		}
		
		if (broadcasts.isEmpty())
		{
			mErrorTv.setVisibility(View.VISIBLE);
		} 
		else
		{
			Collections.sort(broadcasts, new TVBroadcast.BroadcastComparatorByTime());

			mAdapter = new RemindersListAdapter(this, broadcasts, this);
			mListView.setAdapter(mAdapter);
		}
	}


	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		
	}
	
	
	
	@Override
	public void setValues(int count) 
	{
		if(count == 0) 
		{
			mErrorTv.setVisibility(View.VISIBLE);
		} 
		else 
		{
			mErrorTv.setVisibility(View.GONE);
		}
	}
	
	
	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		// TODO NewArc - Implement this
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
				populateViews();
				break;
			}
	
			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}
	}
}
