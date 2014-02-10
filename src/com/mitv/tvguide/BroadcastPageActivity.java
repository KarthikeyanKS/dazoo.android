package com.mitv.tvguide;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.content.SSActivity;
import com.mitv.content.SSBroadcastPage;
import com.mitv.content.SSBroadcastsFromProgramPage;
import com.mitv.content.SSBroadcastsFromSeriesPage;
import com.mitv.content.SSPageCallback;
import com.mitv.content.SSPageGetResult;
import com.mitv.content.activity.ActivityActivity;
import com.mitv.homepage.HomeActivity;
import com.mitv.manager.InternalTrackingManager;
import com.mitv.model.Broadcast;
import com.mitv.model.Channel;
import com.mitv.model.Program;
import com.mitv.model.TvDate;
import com.mitv.myprofile.MyProfileActivity;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.DateUtilities;
import com.mitv.utilities.NetworkUtils;

public class BroadcastPageActivity extends SSActivity implements OnClickListener {

	private static final String		TAG	= "BroadcastPageActivity";
	private Broadcast				mBroadcast;
	private String					mTvDate, mChannelLogoUrl;
	private LinearLayout			mBlockContainer;
	private ActionBar				mActionBar;
	private Channel					mChannel;
	private String					mChannelId, mBroadcastPageUrl;
	private long					mBeginTimeInMillis;
	private boolean					mIsFromNotification	= false, mIsFromActivity = false, mIsBroadcast = false, mIsUpcoming = false, mIsSeries = false, mIsRepeat = false,
			mIsFromProfile = false;
	private RelativeLayout			mTabTvGuide, mTabActivity, mTabProfile;
	private View mTabDividerLeft, mTabDividerRight;
	private MiTVStore				mitvStore;
	private Activity				mActivity;
	private Intent					intent;
	private ArrayList<Broadcast>	mUpcomingBroadcasts;
	private ArrayList<Broadcast>	mRepeatBroadcasts;
	private ScrollView				mScrollView;
	private int						mActivityCardNumber;
	public static Toast 			toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_broadcastpage_activity);

		mitvStore = MiTVStore.getInstance();
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
		mActivityCardNumber = intent.getIntExtra(Consts.INTENT_EXTRA_ACTIVITY_CARD_NUMBER, -1);

		Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Log.d(TAG, "BeginTimeInMillis: " + String.valueOf(mBeginTimeInMillis));
		Log.d(TAG, "ChannelId: " + mChannelId);
		Log.d(TAG, "TvDate: " + mTvDate);
		Log.d(TAG, "BroadcastPageUrl: " + mBroadcastPageUrl);
		Log.d(TAG, "ChannelLogoUrl" + mChannelLogoUrl);
		Log.d(TAG, "from notification: " + mIsFromNotification);
		Log.d(TAG, "from Activity: " + mIsFromActivity);
		Log.d(TAG, "Activity card#" + String.valueOf(mActivityCardNumber));
		Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		initViews();

		super.initCallbackLayouts();

		loadStartPage();
		
		InternalTrackingManager.trackBroadcastStatic(mBroadcast);
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
		if (NetworkUtils.isConnectedAndHostIsReachable(mActivity)) {
			getIndividualBroadcast(mBroadcastPageUrl);
		}
	}

	private void loadStartPage() {
		updateUI(REQUEST_STATUS.LOADING);
		Log.d(TAG, "LOADING");
		// check if the network connection exists

		boolean loadIndividualBroadcast = true;
		boolean useStandardChannel = true;

		if (!NetworkUtils.isConnectedAndHostIsReachable(mActivity)) {
			updateUI(REQUEST_STATUS.FAILED);
		} else {
			if (mBroadcastPageUrl == null)
				mBroadcastPageUrl = Consts.URL_NOTIFY_BROADCAST_PREFIX + mChannelId + Consts.NOTIFY_BROADCAST_URL_MIDDLE + mBeginTimeInMillis;

			if (!mIsFromActivity) {
				if (!mIsFromNotification) {
					if (SecondScreenApplication.isLoggedIn()) {
						Log.d(TAG, "LOGGED IN!");
						mBroadcast = mitvStore.getBroadcast(mTvDate, mChannelId, mBeginTimeInMillis);
						mChannel = mitvStore.getChannelById(mChannelId);

						if (mBroadcast != null) {
							loadIndividualBroadcast = false;
							mIsBroadcast = true;

							if (mChannel != null) {
								mBroadcast.setChannel(mChannel);
							} else {
								Channel channel = new Channel();
								channel.setChannelId(mChannelId);
								mBroadcast.setChannel(channel);
							}

							if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
								getUpcomingSeriesBroadcasts(mBroadcast.getProgram().getSeries().getSeriesId());
							} else {
								mIsUpcoming = true;
							}

							getRepetitionBroadcasts(mBroadcast.getProgram().getProgramId());

							updateUI(REQUEST_STATUS.SUCCESSFUL);
						}
					}

				} else {
					useStandardChannel = false;
				}
			}

			if (loadIndividualBroadcast) {
				Log.d(TAG, "NOT LOGGED IN");
				if (useStandardChannel) {
					mChannel = mitvStore.getChannelById(mChannelId);
				}

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

					if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
						getUpcomingSeriesBroadcasts(mBroadcast.getProgram().getSeries().getSeriesId());
					} else {
						mIsUpcoming = true;
					}

					getRepetitionBroadcasts(mBroadcast.getProgram().getProgramId());

					// if we have the data in the singleton about the channel - set it completely
					if(mBroadcast.getChannel() == null) {
						mChannel = mitvStore.getChannelById(mChannelId);
						if (mChannel != null) {
							mBroadcast.setChannel(mChannel);
	
						} else {
							// otherwise - just use the id that we got with the notification intent
							Channel channel = new Channel();
							channel.setChannelId(mChannelId);
							if (mChannelLogoUrl != null) {
								channel.setAllImageUrls(mChannelLogoUrl);
							}
	
							mBroadcast.setChannel(channel);
						}
					}

					updateUI(REQUEST_STATUS.SUCCESSFUL);
				}
			}
		});
	}

	private void initViews() {
		// styling the Action Bar
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(mActivity.getResources().getString(R.string.broadcast_info));
		mActionBar.setDisplayHomeAsUpEnabled(true);

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

		mBlockContainer = (LinearLayout) findViewById(R.id.broacastpage_block_container_layout);
		mScrollView = (ScrollView) findViewById(R.id.broadcast_scroll);
	}

	private void populateBlocks() {
		Log.d(TAG, "populateBlocks");

		// add main content block
		BroadcastMainBlockPopulator mainBlockPopulator = new BroadcastMainBlockPopulator(mActivity, mScrollView);
		mainBlockPopulator.createBlock(mBroadcast);

		// Remove upcoming broadcasts with season 0 and episode 0
		LinkedList<Broadcast> upcomingToRemove = new LinkedList<Broadcast>();
		if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
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
			BroadcastRepetitionsBlockPopulator repeatitionsBlock = new BroadcastRepetitionsBlockPopulator(mActivity, mScrollView, mBroadcast);
			repeatitionsBlock.createBlock(mRepeatBroadcasts, mBroadcast.getProgram());
		}

		// upcoming episodes
		if (mUpcomingBroadcasts != null && mUpcomingBroadcasts.isEmpty() != true) {
			BroadcastUpcomingBlockPopulator upcomingBlock = new BroadcastUpcomingBlockPopulator(mActivity, mScrollView, mIsSeries, mBroadcast);
			upcomingBlock.createBlock(mUpcomingBroadcasts, null);
		}

		// cast & crew

		// similar shows today

		// what else is on
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
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
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(BroadcastPageActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			// tab to activity page
			Intent intentActivity = new Intent(BroadcastPageActivity.this, ActivityActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.tab_me:
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
				int hour;
				TvDate tvDate;
				if (mIsFromNotification) {
					hour = Integer.valueOf(DateUtilities.getCurrentHourString());
					tvDate = new TvDate();
					tvDate.setDate(mTvDate);
					// Log.d(TAG, "hour: " + hour + " TvDate: " + tvDate.getDate());
				} else {
					hour = ((SecondScreenApplication) getApplicationContext()).getSelectedHour();
					tvDate = MiTVStore.getInstance().getDate(mTvDate);
					// Log.d(TAG, "hour: " + hour + " TvDate: " + tvDate.getDate());
				}

				int indexOfNearestBroadcast = 0;
				if (tvDate != null) {
					indexOfNearestBroadcast = Broadcast.getClosestBroadcastIndexFromTime(mRepeatBroadcasts, hour, tvDate);
				} else {
					Log.e(TAG, "TvDate was null");
					indexOfNearestBroadcast = Broadcast.getClosestBroadcastIndex(mRepeatBroadcasts);
				}

				if (indexOfNearestBroadcast >= 0) {
					mRepeatBroadcasts = Broadcast.getBroadcastsStartingFromPosition(indexOfNearestBroadcast, mRepeatBroadcasts, mRepeatBroadcasts.size());
					// Log.d(TAG, "broadcasts from program: " + mRepeatBroadcasts.size());
				}
				mIsRepeat = true;
				updateUI(REQUEST_STATUS.SUCCESSFUL);

			}
		});
	}

}
