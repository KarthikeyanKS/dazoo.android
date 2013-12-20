package com.millicom.secondscreen.content.myprofile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.adapters.RemindersListAdapter;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.Season;
import com.millicom.secondscreen.notification.NotificationDataSource;

public class RemindersActivity extends SSActivity implements RemindersCountInterface, OnClickListener {

	private static final String		TAG			= "RemindersActivity";
	private ActionBar				mActionBar;
	private boolean					mIsChange	= false;
	private ListView				mListView;
	private RemindersListAdapter	mAdapter;
	private RelativeLayout			mTabTvGuide, mTabActivity, mTabProfile, mTabDividerLeftContainer, mTabDividerRightContainer;
	private int						mCount		= 0;
	private TextView				mErrorTv;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_reminders_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
		
		mTabTvGuide = (RelativeLayout) findViewById(R.id.show_tvguide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.show_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.show_me);
		mTabProfile.setOnClickListener(this);

		mTabDividerLeftContainer = (RelativeLayout) findViewById(R.id.tab_left_divider_container);
		mTabDividerRightContainer = (RelativeLayout) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeftContainer.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		mTabDividerRightContainer.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));

		mErrorTv = (TextView) findViewById(R.id.reminders_error_tv);
		mListView = (ListView) findViewById(R.id.listview);
	}

	private void populateViews() {
		ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();

		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		List<NotificationDbItem> notificationList = notificationDataSource.getAllNotifications();
		for (int i = 0; i < notificationList.size(); i++) {
			NotificationDbItem item = notificationList.get(i);
			Broadcast broadcast = new Broadcast(item);
			broadcasts.add(broadcast);
		}
		// If empty - show notification.
		if (broadcasts.isEmpty()) {
			mErrorTv.setVisibility(View.VISIBLE);
		} else {
			// Sort the list of broadcasts by time.
			Collections.sort(broadcasts, new Broadcast.BroadcastComparatorByTime());

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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.show_tvguide:
			// tab to home page
			Intent intentHome = new Intent(RemindersActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.show_activity:
			// tab to home page
			Intent intentActivity = new Intent(RemindersActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.show_me:
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
