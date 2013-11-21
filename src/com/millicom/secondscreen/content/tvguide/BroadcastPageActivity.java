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

	private static final String		TAG					= "BroadcastPageActivity";
	private Broadcast				mBroadcast;
	private String					mTvDate;

	private LinearLayout			mBlockContainer;
	private ActionBar				mActionBar;
	private Channel					mChannel;
	private String					token, mChannelId, mBroadcastPageUrl;
	private long					mBeginTimeInMillis;
	/*private boolean					mIsSet	= false, mIsLiked = false, mIsLoggedIn = false, mIsFuture, mIsFromNotification = false, mIsFromActivity = false;
	private ImageView				mPosterIv, mMovieIv, mChannelLogoIv, mLikeButtonIv, mShareButtonIv, mRemindButtonIv;
	private ProgressBar				mPosterPb, mChannelLogoPb, mDurationPb;
	private TextView				mTitleTv, mSeasonTv, mEpisodeTv, mEpisodeNameTv, mTimeLeftTv, mTimeTv, mDateTv, mSynopsisTv, mCategoryTv, mYearTv, mCountryTv, mDurationTv, mGenreTv, mParentalRatingTv, mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private int						notificationId;
	private View					mTabSelectorContainerView;
	private NotificationDataSource	mNotificationDataSource;*/
	private boolean					mIsFromNotification	= false, mIsFromActivity = false, mIsLoggedIn = false, mIsBroadcast = false, mIsUpcoming = false;
	private TextView				mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private DazooStore				dazooStore;
	private Activity				mActivity;
	private Intent					intent;
	private ArrayList<Broadcast>	mUpcomingBroadcasts	= new ArrayList<Broadcast>();
	private String					mContentId;
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
		if (mIsBroadcast && mIsUpcoming) {
			if (super.requestIsSuccesfull(status)) {
				Log.d(TAG,"SUCCESSFUL");
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
		Log.d(TAG,"LOADING");
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
						if (mBroadcast != null) {
							mIsBroadcast = true;

							if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
								getUpcomingSeriesBroadcasts(mBroadcast.getProgram().getSeries().getSeriesId());
							} else {
								getUpcomingProgramBroadcasts(mBroadcast.getProgram().getProgramId());
							}

							updateUI(REQUEST_STATUS.SUCCESSFUL);
						}
					} else {
						mBroadcast = dazooStore.getBroadcastFromDefault(mTvDate, mChannelId, mBeginTimeInMillis);
						mChannel = dazooStore.getChannelFromDefault(mChannelId);

						if (mBroadcast != null) {
							mIsBroadcast = true;

							if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
								getUpcomingSeriesBroadcasts(mBroadcast.getProgram().getSeries().getSeriesId());
							} else {
								getUpcomingProgramBroadcasts(mBroadcast.getProgram().getProgramId());
							}

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
						getUpcomingProgramBroadcasts(mBroadcast.getProgram().getProgramId());
					}

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

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.orange));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));


/*
		// view for the general information about the broadcast
		mPosterIv = (ImageView) findViewById(R.id.block_broadcastpage_poster_iv);
		mPosterPb = (ProgressBar) findViewById(R.id.block_broadcastpage_poster_progressbar);
		mMovieIv = (ImageView) findViewById(R.id.block_broadcastpage_broadcast_details_movie_iv);
		mTitleTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_title_tv);
		mSeasonTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_season_tv);
		mEpisodeTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_episode_tv);
		mTimeTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_time_tv);
		mDateTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_date_tv);

		mEpisodeNameTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_episode_name_tv);
		mTimeLeftTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_progress_timeleft_tv);
		mSynopsisTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_synopsis_tv);
		mCategoryTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_category_tv);
		mYearTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_year_tv);
		mCountryTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_country_tv);
		mDurationTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_duration_tv);
		mGenreTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_genre_tv);
		mParentalRatingTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_parental_rating_tv);
		mChannelLogoIv = (ImageView) findViewById(R.id.block_broadcastpage_channellogo_iv);
		mChannelLogoPb = (ProgressBar) findViewById(R.id.block_broadcastpage_channellogo_progressbar);
		mDurationPb = (ProgressBar) findViewById(R.id.channelpage_broadcast_details_progressbar);


		// view for the social interaction buttons
		mLikeButtonIv = (ImageView) findViewById(R.id.block_social_panel_like_button_iv);
		mShareButtonIv = (ImageView) findViewById(R.id.block_social_panel_share_button_iv);
		mRemindButtonIv = (ImageView) findViewById(R.id.block_social_panel_remind_button_iv);

		mLikeButtonIv.setOnClickListener(this);
		mShareButtonIv.setOnClickListener(this);
		mRemindButtonIv.setOnClickListener(this);
*/

		mBlockContainer = (LinearLayout) findViewById(R.id.broacastpage_block_container_layout);
		mScrollView = (ScrollView) findViewById(R.id.broadcast_scroll);
	}

	private void populateBlocks() {
		Log.d(TAG, "populateBlocks");
		mActionBar.setTitle(mBroadcast.getProgram().getTitle());


/*
		//Set channel logo
		Channel channel = mBroadcast.getChannel();
		if (channel != null && channel.getLogoLUrl() != null && TextUtils.isEmpty(channel.getLogoLUrl()) != true) {
			mImageLoader.displayImage(channel.getLogoLUrl(), mChannelLogoIv, mChannelLogoPb, ImageLoader.IMAGE_TYPE.POSTER);
		}

		//Set synopsis
		mSynopsisTv.setText(program.getSynopsisLong());

		// Calculate the duration of the program and set up ProgressBar and TimeLeft.
		int duration = 0;
		long timeSinceBegin = 0;
		long timeToEnd = 0;
		try {
			timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(mBroadcast.getBeginTime());
			timeToEnd = DateUtilities.getAbsoluteTimeDifference(mBroadcast.getEndTime());
			long startTime = DateUtilities.getAbsoluteTimeDifference(mBroadcast.getBeginTime());
			long endTime = DateUtilities.getAbsoluteTimeDifference(mBroadcast.getEndTime());
			duration = (int) (startTime - endTime) / (1000 * 60);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//If on air
		if (timeSinceBegin > 0 && timeToEnd < 0) {
			mDurationPb.setMax(duration);

			//Calculate the current progress of the ProgressBar and update.
			int initialProgress = 0;
			long difference = 0;
			try {
				difference = DateUtilities.getAbsoluteTimeDifference(mBroadcast.getBeginTime());
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}

			if (difference < 0) {
				mDurationPb.setVisibility(View.GONE);
				initialProgress = 0;
				mDurationPb.setProgress(0);
			} 
			else {
				try {
					initialProgress = (int) DateUtilities.getAbsoluteTimeDifference(mBroadcast.getBeginTime()) / (1000 * 60);
				} 
				catch (ParseException e) {
					e.printStackTrace();
				}
				mTimeLeftTv.setText(duration-initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + 
						" " + mActivity.getResources().getString(R.string.left));
				mDurationPb.setProgress(initialProgress);
				mDurationPb.setVisibility(View.VISIBLE);
			}

			mTimeTv.setVisibility(View.GONE);
			mDateTv.setVisibility(View.GONE);
		}
		//Not on air - show date and time
		else {
			String beginTime, endTime;
			try {
				beginTime = DateUtilities.isoStringToTimeString(mBroadcast.getBeginTime());
				endTime = DateUtilities.isoStringToTimeString(mBroadcast.getEndTime());
			} catch (ParseException e) {
				beginTime = "";
				endTime = "";
				e.printStackTrace();
			}

			mTimeTv.setText(beginTime + "-" + endTime);

			String date;
			try {
				date = DateUtilities.isoStringToDayOfWeek(mBroadcast.getBeginTime());
			} catch (ParseException e) {
				date = "";
				e.printStackTrace();
			}
			mDateTv.setText(date + " ");

			mDurationPb.setVisibility(View.GONE);
			mTimeLeftTv.setVisibility(View.GONE);
		}

		mDurationTv.setText(duration + " " + getResources().getString(R.string.minutes));

		if (program.getPosterLUrl() != null && TextUtils.isEmpty(program.getPosterLUrl()) != true) {
			mImageLoader.displayImage(program.getPosterLUrl(), mPosterIv, mPosterPb, ImageLoader.IMAGE_TYPE.POSTER);
		}
				
		if (program != null) {

			String programType = program.getProgramType();
			
			TextView temp = new TextView(mActivity);
			
			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				mActionBar.setTitle(mBroadcast.getProgram().getSeries().getName());
				
				mTitleTv.setText(program.getSeries().getName());
				mEpisodeNameTv.setText(program.getTitle());
				mEpisodeNameTv.setVisibility(View.VISIBLE);
				mSeasonTv.setText(getResources().getString(R.string.season) + " " + program.getSeason().getNumber() + " ");
				mEpisodeTv.setText(getResources().getString(R.string.episode) + " " + String.valueOf(program.getEpisodeNumber()));
				mSeasonTv.setVisibility(View.VISIBLE);
				mEpisodeTv.setVisibility(View.VISIBLE);
				mCategoryTv.setText(getResources().getString(R.string.tv_series));
				mGenreTv.setText(program.getGenre());
				if (program.getGenre() == null) {
					mGenreTv.setVisibility(View.GONE); //Sometimes the program does not have a genre, remove to not make margins look bad
				}
				mCountryTv.setText("COUNTRY"); //TODO: What to put here?
				mCountryTv.setVisibility(View.VISIBLE);
				if (program.getYear() != 0) {
					mYearTv.setText(String.valueOf(program.getYear()));
					mYearTv.setVisibility(View.VISIBLE);
				}
				mParentalRatingTv.setText("PARENTAL RATING"); //TODO: What to put here?
				mParentalRatingTv.setVisibility(View.VISIBLE);
				
				temp.setText("ADDITIONAL VIEWS FOR TV EPISODE");
				mBlockContainer.addView(temp); //TODO: Add additional fields 
			} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
				//TODO: Set the movie icon
				mTitleTv.setText(program.getTitle());
				mCategoryTv.setText(getResources().getString(R.string.movie));
				mGenreTv.setText(program.getGenre());
				mCountryTv.setText("COUNTRY"); //TODO: What to put here?
				mCountryTv.setVisibility(View.VISIBLE);
				if (program.getYear() != 0) {
					mYearTv.setText(String.valueOf(program.getYear()));
					mYearTv.setVisibility(View.VISIBLE);
				}
				mParentalRatingTv.setText("PARENTAL RATING"); //TODO: What to put here?
				mParentalRatingTv.setVisibility(View.VISIBLE);
				
				temp.setText("ADDITIONAL VIEWS FOR MOVIE");
				mBlockContainer.addView(temp); //TODO: Add additional fields 
			} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
				mTitleTv.setText(program.getTitle());
				if (program.getSeason() != null) {
					mSeasonTv.setText(getResources().getString(R.string.season) + " " + program.getSeason().getNumber() + " ");
					mEpisodeTv.setText(getResources().getString(R.string.episode) + " " + String.valueOf(program.getEpisodeNumber()));
					mSeasonTv.setVisibility(View.VISIBLE);
					mEpisodeTv.setVisibility(View.VISIBLE);
				}
				mCategoryTv.setText(program.getCategory());
				mGenreTv.setText(program.getGenre());
				if (program.getGenre() == null) {
					mGenreTv.setVisibility(View.GONE); //Sometimes the program does not have a genre, remove to not make margins look bad
				}
				if (program.getYear() != 0) {
					mYearTv.setText(String.valueOf(program.getYear()));
					mYearTv.setVisibility(View.VISIBLE);
				}
				mParentalRatingTv.setText("PARENTAL RATING");
				mParentalRatingTv.setVisibility(View.VISIBLE);
				
				temp.setText("ADDITIONAL VIEWS FOR OTHER");
				mBlockContainer.addView(temp); //TODO: Add additional fields 
			} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
				//TODO: Set live icon if live
				if (program.getTournament() != null) {
					mEpisodeNameTv.setText(program.getTournament());
					mEpisodeNameTv.setVisibility(View.VISIBLE);
				}
				mTitleTv.setText(program.getTitle());
				mCategoryTv.setText(getResources().getString(R.string.sport));
				mGenreTv.setText(program.getSportType());
				mCountryTv.setVisibility(View.GONE); 
				mYearTv.setVisibility(View.GONE);
				mParentalRatingTv.setVisibility(View.GONE);
				
				temp.setText("ADDITIONAL VIEWS FOR SPORTS");
				mBlockContainer.addView(temp); //TODO: Add additional fields 
			}
		}



		mNotificationDataSource = new NotificationDataSource(this);
*/

		// add main content block
		BroadcastMainBlockPopulator mainBlockPopulator = new BroadcastMainBlockPopulator(mActivity, mScrollView, token, mTvDate);
		mainBlockPopulator.createBlock(mBroadcast);

		// upcoming episodes
		if (mUpcomingBroadcasts != null && mUpcomingBroadcasts.isEmpty() != true) {
			BroadcastUpcomingBlockPopulator repetitionsBlock = new BroadcastUpcomingBlockPopulator(mActivity, mScrollView, mTvDate);
			repetitionsBlock.createBlock(mUpcomingBroadcasts);
		}
		// cast & crew

		// repetitions

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
/*
		case R.id.block_social_panel_like_button_iv:
			if (mIsLoggedIn) {
				if (mIsLiked == false) {
					if (LikeService.addLike(token, mProgramId, mLikeType)) {
						LikeService.showSetLikeToast(mActivity, mBroadcast.getProgram().getTitle());
						mLikeButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart_red));

						AnimationUtilities.animationSet(mLikeButtonIv);

						mIsLiked = true;
					} else {
						Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
					}

				} else {
					LikeDialogHandler likeDlg = new LikeDialogHandler();
					likeDlg.showRemoveLikeDialog(mActivity, token, mLikeType, mBroadcast.getProgram().getProgramId(), yesLikeProc(), noLikeProc());
				}
			} else {
				PromptSignInDialogHandler loginDlg = new PromptSignInDialogHandler();
				loginDlg.showPromptSignInDialog(mActivity, yesLoginProc(), noLoginProc());
			}
			break;
		case R.id.block_social_panel_share_button_iv:
			ShareAction.shareAction(mActivity, getResources().getString(R.string.app_name), mBroadcast.getShareUrl(), getResources().getString(R.string.share_action_title));
			break;
		case R.id.block_social_panel_remind_button_iv:
			if (!mIsFuture) {
				if (mIsSet == false) {
					if (NotificationService.setAlarm(mActivity, mBroadcast, mChannel, mTvDate)) {
						NotificationService.showSetNotificationToast(mActivity);
						mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));

						NotificationDbItem dbItem = new NotificationDbItem();
						dbItem = mNotificationDataSource.getNotification(mChannel.getChannelId(), mBroadcast.getBeginTimeMillis());

						notificationId = dbItem.getNotificationId();

						AnimationUtilities.animationSet(mRemindButtonIv);

						mIsSet = true;
					} else {
						Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
					}
				} else {
					if (notificationId != -1) {
						NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
						notificationDlg.showRemoveNotificationDialog(mActivity, mBroadcast, notificationId, yesNotificationProc(), noNotificationProc());
					} else {
						Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Toast.makeText(mActivity, "The broadcast was already shown! You cannot set a reminder on that", Toast.LENGTH_SHORT).show();
			}
			break;
*/

		}
	}

	// task to get the upcoming broadcasts from series
	private void getUpcomingSeriesBroadcasts(String id) {
		SSBroadcastsFromSeriesPage.getInstance().getPage(id, new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {
				mUpcomingBroadcasts = SSBroadcastsFromSeriesPage.getInstance().getSeriesUpcomingBroadcasts();
				mIsUpcoming = true;
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
		});
	}

	// task to get the broadcasts from the program
	private void getUpcomingProgramBroadcasts(String id) {
		SSBroadcastsFromProgramPage.getInstance().getPage(id, new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult aPageGetResult) {
				mUpcomingBroadcasts = SSBroadcastsFromProgramPage.getInstance().getProgramBroadcasts();
				mIsUpcoming = true;
				updateUI(REQUEST_STATUS.SUCCESSFUL);
			}
		});
	}

}
