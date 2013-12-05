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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
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
	private View					mTabSelectorContainerView;
	private RelativeLayout			mTabTvGuide, mTabActivity, mTabProfile;
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

		// styling bottom navigation tabs
		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);
		mTabTvGuide = (RelativeLayout) findViewById(R.id.show_tvguide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.show_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.show_me);
		mTabProfile.setOnClickListener(this);

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
			Broadcast broadcast = new Broadcast();
			broadcast.setBeginTime(item.getBroadcastBeginTime());
			broadcast.setBeginTimeMillis(Long.parseLong(item.getBroadcastTimeInMillis()));

			Program program = new Program();
			program.setTitle(item.getProgramTitle());
			String programType = item.getProgramType();
			program.setProgramType(programType);

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				program.setEpisodeNumber(item.getProgramEpisodeNumber());
				Season season = new Season();
				season.setNumber(item.getProgramSeason());
				program.setSeason(season);
			} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
				program.setYear(item.getProgramYear());
			}

			// program.setTags()

			broadcast.setProgram(program);

			Channel channel = new Channel();
			channel.setChannelId(item.getChannelId());
			channel.setName(item.getChannelName());

			broadcast.setChannel(channel);

			broadcasts.add(broadcast);
		}
		// If empty - show notification.
		if (broadcasts.isEmpty()) {
			mErrorTv.setVisibility(View.VISIBLE);
		}
		else {
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
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
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
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.show_activity:
			// tab to home page
			Intent intentActivity = new Intent(RemindersActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.show_me:
			// tab to my profile page
			Intent returnIntent = new Intent();
			if (mIsChange == true) {
				setResult(Consts.INFO_UPDATE_REMINDERS, returnIntent);
				returnIntent.putExtra(Consts.INFO_UPDATE_REMINDERS_NUMBER, mCount);
			}
			finish();
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		}
	}

	@Override
	public void setValues(int count) {
		mIsChange = true;
		mCount = count;
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void loadPage() {
		// TODO Auto-generated method stub

	}
}
