package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.authentication.FacebookDazooLoginActivity;
import com.millicom.secondscreen.authentication.LoginActivity;
import com.millicom.secondscreen.authentication.PromptSignInDialogHandler;
import com.millicom.secondscreen.authentication.SignInActivity;
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
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.http.NetworkUtils;
import com.millicom.secondscreen.like.LikeDialogHandler;
import com.millicom.secondscreen.like.LikeService;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationDialogHandler;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.share.ShareAction;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.AnimationUtilities;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;
import com.millicom.secondscreen.SecondScreenApplication;

public class BroadcastPageActivity extends /* ActionBarActivity */SSActivity implements OnClickListener {

	private static final String			TAG	= "BroadcastPageActivity";
	private ImageLoader					mImageLoader;
	private Broadcast					mBroadcast;
	private Channel						mChannel;
	// private TvDate mTvDate;
	private String						mTvDate;

	private LinearLayout				mBlockContainer;
	private ActionBar					mActionBar;
	private LayoutInflater				mLayoutInflater;
	private String						mBroadcastUrl, entityType, token, mChannelId, mChannelDate, mLikeType, mProgramType, mProgramId, mBroadcastPageUrl;
	private long						mBeginTimeInMillis;
	private boolean						mIsSet	= false, mIsLiked = false, mIsLoggedIn = false, mIsFuture, mIsFromNotification = false, mIsFromActivity = false;
	private ImageView					mPosterIv, mLikeButtonIv, mShareButtonIv, mRemindButtonIv;
	private ProgressBar					mPosterPb;
	private TextView					mTitleTv, mSeasonTv, mEpisodeTv, mTimeTv, mDateTv, mChannelTv, mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private int							notificationId;
	private View						mTabSelectorContainerView;
	private NotificationDataSource		mNotificationDataSource;
	private DazooStore					dazooStore;
	private Activity					mActivity;
	private Intent						intent;
	private static ArrayList<Broadcast>	mUpcomingSeriesBroadcasts;
	private static ArrayList<Broadcast>	mProgramBroadcasts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_broadcastpage_activity);

		dazooStore = DazooStore.getInstance();
		mActivity = this;
		mImageLoader = new ImageLoader(this, R.drawable.loadimage_2x);
		mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

		super.initCallbackLayouts();

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
							mProgramType = mBroadcast.getProgram().getProgramType();
							if (mProgramType != null) {
								mLikeType = LikeService.getLikeType(mProgramType);

								if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mProgramType)) {
									mProgramId = mBroadcast.getProgram().getSeries().getSeriesId();
								} else {
									mProgramId = mBroadcast.getProgram().getProgramId();
								}
							}

							updateUI(REQUEST_STATUS.SUCCESSFUL);
						}
					} else {
						mBroadcast = dazooStore.getBroadcastFromDefault(mTvDate, mChannelId, mBeginTimeInMillis);
						mChannel = dazooStore.getChannelFromDefault(mChannelId);

						if (mBroadcast != null) {
							updateUI(REQUEST_STATUS.SUCCESSFUL);
						}
					}

					if (mBroadcast != null) {
						try {
							mIsFuture = DateUtilities.isTimeInFuture(mBroadcast.getBeginTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				} else {
					getIndividualBroadcast(mBroadcastPageUrl);
					mIsFuture = true;
				}

			} else {
				mChannel = dazooStore.getChannelFromAll(mChannelId);
				mBroadcastPageUrl = Consts.NOTIFY_BROADCAST_URL_PREFIX + mChannelId + Consts.NOTIFY_BROADCAST_URL_MIDDLE + mBeginTimeInMillis;
				getIndividualBroadcast(mBroadcastPageUrl);
				mIsFuture = false;
			}
		}
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		if (super.requestIsSuccesfull(status)) {
			initViews();
			populateBlocks();
		}
	}

	@Override
	protected void loadPage() {
		// try to load page again when network is up
		if (NetworkUtils.checkConnection(mActivity)) {
			getIndividualBroadcast(mBroadcastPageUrl);
		}
	}

	private void getIndividualBroadcast(String broadcastPageUrl) {
		SSBroadcastPage.getInstance().getPage(broadcastPageUrl, new SSPageCallback() {
			@Override
			public void onGetPageResult(SSPageGetResult pageGetResult) {
				mBroadcast = SSBroadcastPage.getInstance().getBroadcast();

				if (mBroadcast != null) {
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

		// styling bottom navigation tabs
		mTabSelectorContainerView = findViewById(R.id.tab_selector_container);

		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.orange));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.gray));

		// view for the general information about the broadcast
		mPosterIv = (ImageView) findViewById(R.id.block_broadcastpage_poster_iv);
		mPosterPb = (ProgressBar) findViewById(R.id.block_broadcastpage_poster_progressbar);
		mTitleTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_title_tv);
		mSeasonTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_season_tv);
		mEpisodeTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_episode_tv);
		mTimeTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_time_tv);
		mDateTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_date_tv);
		mChannelTv = (TextView) findViewById(R.id.block_broadcastpage_broadcast_details_channelname_tv);

		// view for the social interaction buttons
		mLikeButtonIv = (ImageView) findViewById(R.id.block_social_panel_like_button_iv);
		mShareButtonIv = (ImageView) findViewById(R.id.block_social_panel_share_button_iv);
		mRemindButtonIv = (ImageView) findViewById(R.id.block_social_panel_remind_button_iv);

		mLikeButtonIv.setOnClickListener(this);
		mShareButtonIv.setOnClickListener(this);
		mRemindButtonIv.setOnClickListener(this);

		mBlockContainer = (LinearLayout) findViewById(R.id.broacastpage_block_container_layout);

		populateBlocks();
	}

	private void populateBlocks() {
		// add the current broadcast top block
		// SSBroadcastBlockPopulator broadcastBlockPopulator = new SSBroadcastBlockPopulator(this, mBlockContainer);
		// broadcastBlockPopulator.createBlock(mBroadcast, mChannel);

		Program program = mBroadcast.getProgram();

		mActionBar.setTitle(mBroadcast.getProgram().getTitle());

		if (program.getPosterLUrl() != null && TextUtils.isEmpty(program.getPosterLUrl()) != true) {
			mImageLoader.displayImage(program.getPosterLUrl(), mPosterIv, mPosterPb, ImageLoader.IMAGE_TYPE.POSTER);
		}
		mTitleTv.setText(program.getTitle());
		// seasonTv.setText(program.getSeason().getNumber());
		mEpisodeTv.setText(String.valueOf(program.getEpisodeNumber()));

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
		mDateTv.setText(date);

		mNotificationDataSource = new NotificationDataSource(this);

		if (!mIsFuture) {
			NotificationDbItem dbItem = new NotificationDbItem();
			dbItem = mNotificationDataSource.getNotification(mChannel.getChannelId(), mBroadcast.getBeginTimeMillis());
			if (dbItem.getNotificationId() != 0) {
				mIsSet = true;
				notificationId = dbItem.getNotificationId();
			} else {
				mIsSet = false;
				notificationId = -1;
			}

			if (mIsSet) mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
			else mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
		} else {
			mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_dissabled_clock));
		}

		if (mIsLoggedIn) {
			Log.d(TAG, "id: " + mBroadcast.getProgram().getProgramId());
			Log.d(TAG, "type: " + mBroadcast.getProgram().getProgramType());
			mIsLiked = LikeService.isLiked(token, mBroadcast.getProgram().getProgramId());
		}

		if (mIsLiked) mLikeButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart_red));
		else mLikeButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart));

		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(program.getProgramType())) {
			entityType = Consts.DAZOO_LIKE_ENTITY_TYPE_SERIES;
		} else entityType = Consts.DAZOO_LIKE_ENTITY_TYPE_PROGRAM;

		// add the cast and crew block
		// SSCastCrewBlockPopulator castCrewBlockPopulator = new SSCastCrewBlockPopulator(this, mBlockContainer);
		// castCrewBlockPopulator.createBlock(mCast);
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
		}
	}

	// task to get the upcoming broadcasts from series
	private static class GetSeriesUpcomingBroadcastsTask extends AsyncTask<String, Boolean, Boolean> {

		protected void onPreExecute() {
			super.onPreExecute();
			// UI response
		}

		protected void onPostExecute(Boolean result) {
			// update UI with fetched content
		}

		@Override
		protected Boolean doInBackground(String... params) {
			SSBroadcastsFromSeriesPage.getInstance().getPage(params[0], new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mUpcomingSeriesBroadcasts = SSBroadcastsFromSeriesPage.getInstance().getSeriesUpcomingBroadcasts();
				}
			});
			return true;
		}
	}

	// task to get the broadcasts from the program
	private static class GetProgramBroadcastsTask extends AsyncTask<String, Boolean, Boolean> {

		protected void onPreExecute() {
			super.onPreExecute();
			// UI response
		}

		protected void onPostExecute(Boolean result) {
			// update UI with fetched content
		}

		@Override
		protected Boolean doInBackground(String... params) {
			SSBroadcastsFromProgramPage.getInstance().getPage(params[0], new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mProgramBroadcasts = SSBroadcastsFromProgramPage.getInstance().getProgramBroadcasts();
				}
			});
			return true;
		}
	}

	public Runnable yesLoginProc() {
		return new Runnable() {
			public void run() {
				Intent intent = new Intent(BroadcastPageActivity.this, SignInActivity.class);
				// Intent intent = new Intent(BroadcastPageActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		};
	}

	public Runnable noLoginProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

	public Runnable yesLikeProc() {
		return new Runnable() {
			public void run() {
				mLikeButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart));
				mIsLiked = false;
			}
		};
	}

	public Runnable noLikeProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

	public Runnable yesNotificationProc() {
		return new Runnable() {
			public void run() {
				mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
				mIsSet = false;
			}
		};
	}

	public Runnable noNotificationProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}
}
