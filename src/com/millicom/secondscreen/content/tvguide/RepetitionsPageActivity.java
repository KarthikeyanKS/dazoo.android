package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.adapters.RepetitionsListAdapter;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;

public class RepetitionsPageActivity extends SSActivity implements OnClickListener {

	private static final String		TAG						= "RepeatitionsPageActivity";
	private String					token;
	private RelativeLayout			mTabTvGuide, mTabProfile, mTabActivity;
	private ActionBar				mActionBar;
	private ListView				mListView;
	private RepetitionsListAdapter	mAdapter;
	private ArrayList<Broadcast>	mRepeatingBroadcasts	= new ArrayList<Broadcast>();
	private Program					mRepeatingProgram;
	private Broadcast 				mRunningBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_repeating_list_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		Intent intent = getIntent();
		mRepeatingBroadcasts = intent.getParcelableArrayListExtra(Consts.INTENT_EXTRA_REPEATING_BROADCASTS);
		mRepeatingProgram = intent.getParcelableExtra(Consts.INTENT_EXTRA_REPEATING_PROGRAM);
		mRunningBroadcast = intent.getParcelableExtra(Consts.INTENT_EXTRA_REPEATING_RUNNING_BROADCAST);
	
		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();

		initViews();

		super.initCallbackLayouts();
		loadPage();
	}

	private void initViews() {
		mTabTvGuide = (RelativeLayout) findViewById(R.id.show_tvguide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.show_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.show_me);
		mTabProfile.setOnClickListener(this);

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

		mActionBar = getSupportActionBar();

		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.repetitions));
		mListView = (ListView) findViewById(R.id.repeating_list_listview);
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			mAdapter = new RepetitionsListAdapter(this, mRepeatingBroadcasts, mRepeatingProgram, mRunningBroadcast);
			mListView.setAdapter(mAdapter);
			mListView.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void loadPage() {
		updateUI(REQUEST_STATUS.LOADING);
		if (mRepeatingBroadcasts != null && mRepeatingBroadcasts.isEmpty() != true) {
			updateUI(REQUEST_STATUS.SUCCESSFUL);
		} else {
			updateUI(REQUEST_STATUS.EMPTY_RESPONSE);
		}
	}

	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.show_tvguide:
			// tab to home page
			Intent intentHome = new Intent(RepetitionsPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.show_activity:
			// tab to activity page
			Intent intentActivity = new Intent(RepetitionsPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.show_me:
			// tab to profile page
			Intent intentMe = new Intent(RepetitionsPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		}
	}
}
