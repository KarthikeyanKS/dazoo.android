package com.mitv.tvguide;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.adapters.PopularListAdapter;
import com.mitv.adapters.UpcomingEpisodesListAdapter;
import com.mitv.content.SSActivity;
import com.mitv.content.activity.ActivityActivity;
import com.mitv.homepage.HomeActivity;
import com.mitv.http.NetworkUtils;
import com.mitv.model.Broadcast;
import com.mitv.myprofile.MyProfileActivity;
import com.mitv.storage.MiTVStore;

public class UpcomingeEpisodesPageActivity extends SSActivity implements OnClickListener {

	private static final String			TAG					= "UpcomingeEpisodesPageActivity";
	private String						token;
	private RelativeLayout				mTabTvGuide, mTabProfile, mTabActivity;private View mTabDividerLeft, mTabDividerRight;
	private ActionBar					mActionBar;
	private ListView					mListView;
	private UpcomingEpisodesListAdapter	mAdapter;
	private Broadcast 					mRunningBroadcast;
	private ArrayList<Broadcast>		mUpcomingBroadcasts	= new ArrayList<Broadcast>();
	private boolean	 					mIsFromActivity;
	private boolean 					mIsFromProfile;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_upcoming_episodes_list_activity);

		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		Intent intent = getIntent();
		mUpcomingBroadcasts = intent.getParcelableArrayListExtra(Consts.INTENT_EXTRA_UPCOMING_BROADCASTS);
		mRunningBroadcast = intent.getParcelableExtra(Consts.INTENT_EXTRA_RUNNING_BROADCAST);
		mIsFromActivity = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_FROM_ACTIVITY, false);
		mIsFromProfile = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_FROM_PROFILE, false);

		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();

		initViews();

		super.initCallbackLayouts();
		loadPage();
	}

	private void initViews() {
		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		mTabProfile.setOnClickListener(this);
		
		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);
		
		if (mIsFromActivity) {
			mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
			mTabActivity.setBackgroundColor(getResources().getColor(R.color.red));
			mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

			mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
			mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		} else if (mIsFromProfile) {
			mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
			mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
			mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));

			mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
			mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		} else {
			mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
			mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
			mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

			mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
			mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));

		}

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.upcoming_episodes));
		mListView = (ListView) findViewById(R.id.upcoming_episodes_list_listview);
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			mAdapter = new UpcomingEpisodesListAdapter(this, mUpcomingBroadcasts, mRunningBroadcast);
			mListView.setAdapter(mAdapter);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		if (mUpcomingBroadcasts != null && mUpcomingBroadcasts.isEmpty() != true) {
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
			Intent intentHome = new Intent(UpcomingeEpisodesPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			// tab to activity page
			Intent intentActivity = new Intent(UpcomingeEpisodesPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.tab_me:
			// tab to profile page
			Intent intentMe = new Intent(UpcomingeEpisodesPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			
			break;
		}
	}
}