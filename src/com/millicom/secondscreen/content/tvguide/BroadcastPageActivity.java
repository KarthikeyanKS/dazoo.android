package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.SSBroadcastPage;
import com.millicom.secondscreen.content.SSBroadcastsFromProgramPage;
import com.millicom.secondscreen.content.SSBroadcastsFromSeriesPage;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.http.NetworkUtils;
import com.millicom.secondscreen.storage.DazooStore;

public class BroadcastPageActivity extends /* ActionBarActivity */SSActivity implements OnClickListener {

	private static final String		TAG	= "BroadcastPageActivity";
	private Broadcast				mBroadcast;
	private String					mTvDate;

	private LinearLayout			mBlockContainer;
	private ActionBar				mActionBar;
	private Channel					mChannel;
	private String					token, mChannelId, mBroadcastPageUrl;
	private long					mBeginTimeInMillis;
	private boolean					mIsFromNotification	= false, mIsFromActivity = false, mIsLoggedIn = false, mIsBroadcast = false, mIsUpcoming = false, mIsSeries = false, mIsRepeat = false;
	private TextView				mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private DazooStore				dazooStore;
	private Activity				mActivity;
	private Intent					intent;
	private ArrayList<Broadcast>	mUpcomingBroadcasts;
	private ArrayList<Broadcast>	mRepeatBroadcasts;
	private ScrollView				mScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_broadcastpage_activity);

		dazooStore = DazooStore.getInstance();
		mActivity = this;

		// get the info about the program to be displayed from tv-guide listview
		intent = getIntent();
		mBeginTimeInMillis = intent.getLongExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, 0);
		mChannelId = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_ID);
		mTvDate = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE);
		mIsFromNotification = intent.getBooleanExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, false);
		mBroadcastPageUrl = intent.getStringExtra(Consts.INTENT_EXTRA_BROADCAST_URL);
		mIsFromActivity = intent.getBooleanExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, false);

		Log.d(TAG, "mBeginTimeInMillis: " + String.valueOf(mBeginTimeInMillis));
		Log.d(TAG, "mChannelId: " + mChannelId);
		Log.d(TAG, "mTvDate: " + mTvDate);
		Log.d(TAG, "mBroadcastPageUrl: " + mBroadcastPageUrl);

		initViews();

		super.initCallbackLayouts();

		loadStartPage();
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		Log.d(TAG, "mIsBroadcast: " + mIsBroadcast + " mIsUpcoming: " + mIsUpcoming);
		if (mIsBroadcast && mIsUpcoming && mIsRepeat) {
			if (super.requestIsSuccesfull(status)) {
				Log.d(TAG, "SUCCESSFUL");
				populateBlocks();
			}
		}
	}

	@Override
	protected void loadPage() {
		// try to load page again when network is up
		if (NetworkUtils.checkConnection(mActivity)) {
			getIndividualBroadcast(mBroadcastPageUrl);
		}
	}

	private void loadStartPage() {
		updateUI(REQUEST_STATUS.LOADING);
		Log.d(TAG, "LOADING");
		// check if the network connection exists
		if (!NetworkUtils.checkConnection(mActivity)) {
			updateUI(REQUEST_STATUS.FAILED);
		} else {
			token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
			if (!mIsFromActivity) {
				if (!mIsFromNotification) {
					if (token != null && TextUtils.isEmpty(token) != true) {
						Log.d(TAG, "LOGGED IN!");
						mIsLoggedIn = true;
						mBroadcast = dazooStore.getBroadcastFromMy(mTvDate, mChannelId, mBeginTimeInMillis);
						mChannel = dazooStore.getChannelFromAll(mChannelId);

						mBroadcast.setChannel(mChannel);

						if (mBroadcast != null) {
							mIsBroadcast = true;

							if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
								getUpcomingSeriesBroadcasts(mBroadcast.getProgram().getSeries().getSeriesId());
							} else {
								mIsUpcoming = true;
							}

							getRepetitionBroadcasts(mBroadcast.getProgram().getProgramId());

							updateUI(REQUEST_STATUS.SUCCESSFUL);
						}
					} else {
						mBroadcast = dazooStore.getBroadcastFromDefault(mTvDate, mChannelId, mBeginTimeInMillis);
						mChannel = dazooStore.getChannelFromDefault(mChannelId);

						mBroadcast.setChannel(mChannel);

						if (mBroadcast != null) {
							mIsBroadcast = true;

							if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
								getUpcomingSeriesBroadcasts(mBroadcast.getProgram().getSeries().getSeriesId());
							} else {
								mIsUpcoming = true;
							}
						
							getRepetitionBroadcasts(mBroadcast.getProgram().getProgramId());

							updateUI(REQUEST_STATUS.SUCCESSFUL);
						}
					}
				} else {
					getIndividualBroadcast(mBroadcastPageUrl);
				}
			} else {
				mChannel = dazooStore.getChannelFromAll(mChannelId);
				mBroadcastPageUrl = Consts.NOTIFY_BROADCAST_URL_PREFIX + mChannelId + Consts.NOTIFY_BROADCAST_URL_MIDDLE + mBeginTimeInMillis;
				getIndividualBroadcast(mBroadcastPageUrl);
			}
		}
	}

	private void getIndividualBroadcast(String broadcastPageUrl) {
		SSBroadcastPage.getInstance().getPage(broadcastPageUrl, new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult pageGetResult) {
				mBroadcast = SSBroadcastPage.getInstance().getBroadcast();

				if (mBroadcast != null) {
					mIsBroadcast = true;

					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
						getUpcomingSeriesBroadcasts(mBroadcast.getProgram().getSeries().getSeriesId());
					} else {
						mIsUpcoming = true;
					}

					getRepetitionBroadcasts(mBroadcast.getProgram().getProgramId());

					mBroadcast.setChannel(mChannel);

					updateUI(REQUEST_STATUS.SUCCESSFUL);
				}
			}
		});
	}

	private void initViews() {
		// styling the Action Bar
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		final int actionBarColor = getResources().getColor(R.color.blue1);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTxtTabPopular.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTxtTabFeed.setBackgroundColor(getResources().getColor(R.color.red));
	
		mBlockContainer = (LinearLayout) findViewById(R.id.broacastpage_block_container_layout);
		mScrollView = (ScrollView) findViewById(R.id.broadcast_scroll);
	}

	private void populateBlocks() {
		Log.d(TAG, "populateBlocks");
		mActionBar.setTitle(mBroadcast.getProgram().getTitle());

		// add main content block
		BroadcastMainBlockPopulator mainBlockPopulator = new BroadcastMainBlockPopulator(mActivity, mScrollView, token, mTvDate);
		mainBlockPopulator.createBlock(mBroadcast);

		// repetitions
		if (mRepeatBroadcasts != null && mRepeatBroadcasts.isEmpty() != true) {
			BroadcastRepeatitionsBlockPopulator repeatitionsBlock = new BroadcastRepeatitionsBlockPopulator(mActivity, mScrollView, mTvDate);
			repeatitionsBlock.createBlock(mRepeatBroadcasts);
		}

		// upcoming episodes
		if (mUpcomingBroadcasts != null && mUpcomingBroadcasts.isEmpty() != true) {
			BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(mActivity, mScrollView, mTvDate, mIsSeries);
			upcomingBlock.createBlock(mUpcomingBroadcasts);
		}
		
		// cast & crew

		// similar shows today

		// what else is on
	}

	@Override
	public void onBackPressed() {
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
			Intent intentHome = new Intent(BroadcastPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.show_activity:
			// tab to activity page
			Intent intentActivity = new Intent(BroadcastPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			break;
		case R.id.show_me:
			// tab to activity page
			Intent intentMe = new Intent(BroadcastPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			break;
		}
	}

	// task to get the upcoming broadcasts from series
	private void getUpcomingSeriesBroadcasts(String id) {
		SSBroadcastsFromSeriesPage.getInstance().getPage(id, new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {
				mUpcomingBroadcasts = SSBroadcastsFromSeriesPage.getInstance().getSeriesUpcomingBroadcasts();
				Log.d(TAG, "broadcasts from SERIES: " + mUpcomingBroadcasts.size());
				mIsSeries = true;
				mIsUpcoming = true;
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
		});
	}

	// task to get the broadcasts of the same program
	private void getRepetitionBroadcasts(String id) {
		SSBroadcastsFromProgramPage.getInstance().getPage(id, new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {
				mRepeatBroadcasts = SSBroadcastsFromProgramPage.getInstance().getProgramBroadcasts();
				Log.d(TAG, "broadcasts from program: " + mRepeatBroadcasts.size());
				mIsRepeat = true;
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
		});
	}

}
