package com.millicom.mitv.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.adapters.RemindersListAdapter;
import com.mitv.interfaces.RemindersCountInterface;
import com.mitv.model.OldBroadcast;
import com.mitv.model.OldNotificationDbItem;
import com.mitv.notification.NotificationDataSource;

public class RemindersActivity extends BaseActivity implements RemindersCountInterface, OnClickListener {

	private static final String		TAG			= "RemindersActivity";
	private ActionBar				mActionBar;
	private boolean					mIsChange	= false;
	private ListView				mListView;
	private RemindersListAdapter	mAdapter;
	private RelativeLayout			mTabTvGuide, mTabActivity, mTabProfile;private View mTabDividerLeft, mTabDividerRight;
	private int						mCount		= 0;
	private TextView				mErrorTv;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_reminders_activity);

		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		initLayout();
		super.initCallbackLayouts();
		populateViews();
	}

	private void initLayout() {
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(getResources().getString(R.string.reminders));
		mActionBar.setDisplayHomeAsUpEnabled(true);

		// styling bottom navigation tabs
		
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

	private void populateViews() {
		ArrayList<OldBroadcast> broadcasts = new ArrayList<OldBroadcast>();

		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		List<OldNotificationDbItem> notificationList = notificationDataSource.getAllNotifications();
		for (int i = 0; i < notificationList.size(); i++) {
			OldNotificationDbItem item = notificationList.get(i);
			OldBroadcast broadcast = new OldBroadcast(item);
			broadcasts.add(broadcast);
		}
		// If empty - show notification.
		if (broadcasts.isEmpty()) {
			mErrorTv.setVisibility(View.VISIBLE);
		} else {
			// Sort the list of broadcasts by time.
			Collections.sort(broadcasts, new OldBroadcast.BroadcastComparatorByTime());

			mAdapter = new RemindersListAdapter(this, broadcasts, this);
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		if (mIsChange == true) {
			setResult(Consts.INFO_UPDATE_REMINDERS, returnIntent);
			returnIntent.putExtra(Consts.INFO_UPDATE_REMINDERS_NUMBER, mCount);
		}
		super.onBackPressed();
		
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(RemindersActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			// tab to home page
			Intent intentActivity = new Intent(RemindersActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.tab_me:
			// tab to my profile page
			Intent returnIntent = new Intent();
			if (mIsChange == true) {
				setResult(Consts.INFO_UPDATE_REMINDERS, returnIntent);
				returnIntent.putExtra(Consts.INFO_UPDATE_REMINDERS_NUMBER, mCount);
			}
			finish();
			
			break;
		}
	}

	@Override
	public void setValues(int count) {
		mIsChange = true;
		mCount = count;
		
		if(count == 0) {
			mErrorTv.setVisibility(View.VISIBLE);
		} else {
			mErrorTv.setVisibility(View.GONE);
		}
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {

	}

	@Override
	protected void loadPage() {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		// update the reminders list on Up/Home button press too
		case android.R.id.home:

			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (mIsChange == true) {
				setResult(Consts.INFO_UPDATE_REMINDERS, upIntent);
				upIntent.putExtra(Consts.INFO_UPDATE_REMINDERS_NUMBER, mCount);
			}
			NavUtils.navigateUpTo(this, upIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}