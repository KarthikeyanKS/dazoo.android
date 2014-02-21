package com.millicom.mitv.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.UpcomingEpisodesListAdapter;

public class UpcomingEpisodesPageActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = UpcomingEpisodesPageActivity.class.getName();
	
	private RelativeLayout tabTvGuide;
	private RelativeLayout tabProfile;
	private RelativeLayout tabActivity;
	
	private View tabDividerLeft;
	private View tabDividerRight;
	
	private ActionBar actionBar;
	private ListView listView;
	private UpcomingEpisodesListAdapter adapter;
	private TVBroadcastWithChannelInfo runningBroadcast;
	private ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_upcoming_episodes_list_activity);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		runningBroadcast = ContentManager.sharedInstance().getFromStorageSelectedBroadcastWithChannelInfo();
		upcomingBroadcasts = ContentManager.sharedInstance().getFromStorageUpcomingBroadcasts(runningBroadcast);

		initViews();

		super.initCallbackLayouts();
		loadPage();
	}

	private void initViews() {
		tabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		tabTvGuide.setOnClickListener(this);
		tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		tabActivity.setOnClickListener(this);
		tabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		tabProfile.setOnClickListener(this);

		tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		tabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

		tabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		tabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		tabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		tabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.upcoming_episodes));
		listView = (ListView) findViewById(R.id.upcoming_episodes_list_listview);
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			adapter = new UpcomingEpisodesListAdapter(this, upcomingBroadcasts, runningBroadcast);
			listView.setAdapter(adapter);
			listView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		if (upcomingBroadcasts != null && upcomingBroadcasts.isEmpty() != true) {
			updateUI(REQUEST_STATUS.SUCCESSFUL);
		} else {
			updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(UpcomingEpisodesPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);

			break;
		case R.id.tab_activity:
			// tab to activity page
			Intent intentActivity = new Intent(UpcomingEpisodesPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);

			break;
		case R.id.tab_me:
			// tab to profile page
			Intent intentMe = new Intent(UpcomingEpisodesPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);

			break;
		}
	}
}