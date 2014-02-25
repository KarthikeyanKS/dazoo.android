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
import com.mitv.adapters.RepetitionsListAdapter;
import com.mitv.content.SSActivity;
import com.mitv.content.activity.ActivityActivity;
import com.mitv.homepage.HomeActivity;
import com.mitv.model.Broadcast;
import com.mitv.model.Program;
import com.mitv.myprofile.MyProfileActivity;

public class RepetitionsPageActivity extends SSActivity implements OnClickListener {

	private static final String		TAG						= "RepeatitionsPageActivity";
	private String					token;
	private RelativeLayout			mTabTvGuide, mTabProfile, mTabActivity;private View mTabDividerLeft, mTabDividerRight;
	private ActionBar				mActionBar;
	private ListView				mListView;
	private RepetitionsListAdapter	mAdapter;
	private ArrayList<Broadcast>	mRepeatingBroadcasts	= new ArrayList<Broadcast>();
	private Program					mRepeatingProgram;
	private Broadcast				mRunningBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_repeating_list_activity);
		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		Intent intent = getIntent();
		mRepeatingBroadcasts = intent.getParcelableArrayListExtra(Consts.INTENT_EXTRA_REPEATING_BROADCASTS);
		mRepeatingProgram = intent.getParcelableExtra(Consts.INTENT_EXTRA_REPEATING_PROGRAM);
		mRunningBroadcast = intent.getParcelableExtra(Consts.INTENT_EXTRA_RUNNING_BROADCAST);
		
		Broadcast tmpBrc = mRunningBroadcast;
		Program tmpPrgFromBrc = tmpBrc.getProgram();
		Program tmpPrg = mRepeatingProgram;

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

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.red));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));

		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
				
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
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(RepetitionsPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			// tab to activity page
			Intent intentActivity = new Intent(RepetitionsPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.tab_me:
			// tab to profile page
			Intent intentMe = new Intent(RepetitionsPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			
			break;
		}
	}
}
