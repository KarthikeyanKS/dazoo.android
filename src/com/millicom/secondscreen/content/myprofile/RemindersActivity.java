package com.millicom.secondscreen.content.myprofile;

import java.util.ArrayList;
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
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.RemindersListAdapter;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.Season;
import com.millicom.secondscreen.notification.NotificationDataSource;

public class RemindersActivity extends ActionBarActivity implements RemindersCountInterface, OnClickListener {

	private static final String		TAG			= "RemindersActivity";
	private ActionBar				mActionBar;
	private boolean					mIsChange	= false;
	private ListView				mListView;
	private RemindersListAdapter	mAdapter;
	private View					mTabSelectorContainerView;
	private TextView				mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private int mCount = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_reminders_activity);
		initLayout();
		populateViews();
	}

	private void initLayout() {
		mActionBar = getSupportActionBar();
		SpannableString s = new SpannableString(getResources().getString(R.string.reminders));
		// s.setSpan(new TypefaceSpan(this, "AvenirBlack"),0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		mActionBar.setTitle(getResources().getString(R.string.reminders));

		// styling bottom navigation tabs
		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);

		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.orange));

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
				program.setEpisode(item.getProgramEpisode());
				Season season = new Season();
				season.setNumber(item.getProgramSeason());
				program.setSeason(season);
			} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
				program.setYear(String.valueOf(item.getProgramYear()));
			}

			// program.setTags()

			broadcast.setProgram(program);

			Channel channel = new Channel();
			channel.setChannelId(item.getChannelId());
			channel.setName(item.getChannelName());

			broadcast.setChannel(channel);

			broadcasts.add(broadcast);
		}
		Toast.makeText(this, "Currently you have " + notificationList.size() + " being set. List with info is coming soon!", Toast.LENGTH_LONG).show();

		mAdapter = new RemindersListAdapter(this, broadcasts, this);
		mListView.setAdapter(mAdapter);
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
	}
}
