package com.millicom.secondscreen.content.tvguide;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.http.NetworkUtils;
import com.millicom.secondscreen.storage.DazooStore;

public class BroadcastPageActivity extends /* ActionBarActivity */SSActivity implements OnClickListener {

	private static final String		TAG	= "BroadcastPageActivity";
	private Broadcast				mBroadcast;
	private String					mTvDate, mChannelLogoUrl;
	private LinearLayout			mBlockContainer;
	private ActionBar				mActionBar;
	private Channel					mChannel;
	private String					token, mChannelId, mBroadcastPageUrl;
	private long					mBeginTimeInMillis;
	private boolean					mIsFromNotification	= false, mIsFromActivity = false, mIsLoggedIn = false, mIsBroadcast = false, mIsUpcoming = false, mIsSeries = false, mIsRepeat = false,
			mIsFromProfile = false;
	private RelativeLayout			mTabTvGuide, mTabActivity, mTabProfile;
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
		mIsFromProfile = intent.getBooleanExtra(Consts.INTENT_EXTRA_FROM_PROFILE, false);
		mChannelLogoUrl = intent.getStringExtra(Consts.INTENT_EXTRA_CHANNEL_LOGO_URL);

		Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Log.d(TAG, "mBeginTimeInMillis: " + String.valueOf(mBeginTimeInMillis));
		Log.d(TAG, "mChannelId: " + mChannelId);
		Log.d(TAG, "mTvDate: " + mTvDate);
		Log.d(TAG, "mBroadcastPageUrl: " + mBroadcastPageUrl);
		Log.d(TAG, "mChannelLogoUrl" + mChannelLogoUrl);
		Log.d(TAG, "from notification: " + mIsFromNotification);
		Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!");

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
			if (mBroadcastPageUrl == null) mBroadcastPageUrl = Consts.NOTIFY_BROADCAST_URL_PREFIX + mChannelId + Consts.NOTIFY_BROADCAST_URL_MIDDLE + mBeginTimeInMillis;

			if (!mIsFromActivity) {
				if (!mIsFromNotification) {
					if (token != null && TextUtils.isEmpty(token) != true) {
						Log.d(TAG, "LOGGED IN!");
						mIsLoggedIn = true;
						mBroadcast = dazooStore.getBroadcastFromMy(mTvDate, mChannelId, mBeginTimeInMillis);
						mChannel = dazooStore.getChannelFromAll(mChannelId);

						if (mBroadcast != null) {
							mIsBroadcast = true;

							if (mChannel != null) {
								mBroadcast.setChannel(mChannel);
							} else {
								Channel channel = new Channel();
								channel.setChannelId(mChannelId);
								mBroadcast.setChannel(channel);
							}

							if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
								getUpcomingSeriesBroadcasts(mBroadcast.getProgram().getSeries().getSeriesId());
							} else {
								mIsUpcoming = true;
							}

							getRepetitionBroadcasts(mBroadcast.getProgram().getProgramId());

							updateUI(REQUEST_STATUS.SUCCESSFUL);
						}
					} else {
						Log.d(TAG, "NOT LOGGED IN");
						mChannel = dazooStore.getChannelFromDefault(mChannelId);
						getIndividualBroadcast(mBroadcastPageUrl);
					}
				} else {
					Log.d(TAG, "FROM NOTIFICATION");
					getIndividualBroadcast(mBroadcastPageUrl);
				}
			} else {
				mChannel = dazooStore.getChannelFromAll(mChannelId);
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

					// if we have the data in the singleton about the channel - set it completely
					if (mChannel != null) {
						mBroadcast.setChannel(mChannel);

					} else {
						// otherwise - just use the id that we got with the notification intent
						Channel channel = new Channel();
						channel.setChannelId(mChannelId);
						if (mChannelLogoUrl != null) {
							channel.setLogoSUrl(mChannelLogoUrl);
						}

						mBroadcast.setChannel(channel);
					}

					updateUI(REQUEST_STATUS.SUCCESSFUL);
				}
			}
		});
	}

	private void initViews() {
		// styling the Action Bar
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(mActivity.getResources().getString(R.string.tv_guide));

		mTabTvGuide = (RelativeLayout) findViewById(R.id.show_tvguide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.show_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.show_me);
		mTabProfile.setOnClickListener(this);

		if (mIsFromActivity) {
			mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
			mTabActivity.setBackgroundColor(getResources().getColor(R.color.red));
			mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		} else if (mIsFromProfile) {
			mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
			mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
			mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));
		} else {
			mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.red));
			mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
			mTabProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
		}

		mBlockContainer = (LinearLayout) findViewById(R.id.broacastpage_block_container_layout);
		mScrollView = (ScrollView) findViewById(R.id.broadcast_scroll);
	}

	private void populateBlocks() {
		Log.d(TAG, "populateBlocks");

		// add main content block
		BroadcastMainBlockPopulator mainBlockPopulator = new BroadcastMainBlockPopulator(mActivity, mScrollView, token, mTvDate);
		mainBlockPopulator.createBlock(mBroadcast);

		//Remove upcoming broadcasts with season 0 and episode 0
		LinkedList<Broadcast> upcomingToRemove = new LinkedList<Broadcast>();
		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
			Program program = mBroadcast.getProgram();
			if (program.getSeason().getNumber().equals("0") && program.getEpisodeNumber() == 0) {
				for (int i = 0; i < mUpcomingBroadcasts.size(); i++) {
					Broadcast b = mUpcomingBroadcasts.get(i);
					Program p = b.getProgram();
					if (p.getSeason().getNumber().equals("0") && p.getEpisodeNumber() == 0) {
						upcomingToRemove.add(b);
					}
				}
			}
		}
		for (Broadcast b : upcomingToRemove) {
			mUpcomingBroadcasts.remove(b);
		}

		// repetitions
		if (mRepeatBroadcasts != null && mRepeatBroadcasts.isEmpty() != true) {
			BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(mActivity, mScrollView, mTvDate);
			repeatitionsBlock.createBlock(mRepeatBroadcasts, mBroadcast.getProgram());
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			// Log.d(TAG,"UP INTENT: " + upIntent);
			// if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
			// // This activity is NOT part of this app's task, so create a new task
			// // when navigating up, with a synthesized back stack.
			// TaskStackBuilder.create(this)
			// // Add all of this activity's parents to the back stack
			// .addNextIntentWithParentStack(upIntent)
			// // Navigate up to the closest parent
			// .startActivities();
			// } else {
			// Log.d(TAG,"GO UP TO TVGUIDE");
			// // This activity is part of this app's task, so simply
			// // navigate up to the logical parent activity.
			// NavUtils.navigateUpTo(this, upIntent);
			// }

			if (mIsFromActivity) {
				NavUtils.navigateUpTo(this, new Intent(BroadcastPageActivity.this, ActivityActivity.class));
			} else if (mIsFromProfile) {
				NavUtils.navigateUpTo(this, new Intent(BroadcastPageActivity.this, MyProfileActivity.class));
			}

			else {
				// This activity is part of this app's task, so simply
				// navigate up to the logical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}

			return true;
		}
		return super.onOptionsItemSelected(item);
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
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.show_activity:
			// tab to activity page
			Intent intentActivity = new Intent(BroadcastPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.show_me:
			// tab to activity page
			Intent intentMe = new Intent(BroadcastPageActivity.this, MyProfileActivity.class);
			startActivity(intentMe);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
				int hour = ((SecondScreenApplication) getApplicationContext()).getSelectedHour();
				TvDate tvDate = DazooStore.getInstance().getDate(mTvDate);
				int indexOfNearestBroadcast = Broadcast.getClosestBroadcastIndexFromTime(mRepeatBroadcasts, hour, tvDate);

				if (indexOfNearestBroadcast >= 0) {
					mRepeatBroadcasts = Broadcast.getBroadcastsStartingFromPosition(indexOfNearestBroadcast, mRepeatBroadcasts, mRepeatBroadcasts.size());
					Log.d(TAG, "broadcasts from program: " + mRepeatBroadcasts.size());
				}
				mIsRepeat = true;
				updateUI(REQUEST_STATUS.SUCCESSFUL);

			}
		});
	}

}
