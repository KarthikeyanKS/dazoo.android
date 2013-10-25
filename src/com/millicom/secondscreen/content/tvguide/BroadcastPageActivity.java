package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.authentication.FacebookDazooLoginActivity;
import com.millicom.secondscreen.authentication.LoginActivity;
import com.millicom.secondscreen.authentication.PromptSignInDialogHandler;
import com.millicom.secondscreen.authentication.SignInActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.like.LikeDialogHandler;
import com.millicom.secondscreen.like.LikeService;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationDialogHandler;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.share.ShareAction;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;
import com.millicom.secondscreen.SecondScreenApplication;

public class BroadcastPageActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG	= "BroadcastPageActivity";
	private ImageLoader			mImageLoader;
	private Broadcast			mBroadcast;
	private Channel				mChannel;
	private LinearLayout		mBlockContainer;
	private ActionBar			mActionBar;
	private LayoutInflater		mLayoutInflater;
	private String				mBroadcastUrl, entityType, token;
	private boolean				mIsSet	= false, mIsLiked = false, mIsLoggedIn = false;
	private ImageView			mPosterIv, mLikeButtonIv, mShareButtonIv, mRemindButtonIv;
	private ProgressBar			mPosterPb;
	private TextView			mTitleTv, mSeasonTv, mEpisodeTv, mTimeTv, mDateTv, mChannelTv, mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private int					notificationId;
	private View				mTabSelectorContainerView;

	private Activity			mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_broadcastpage_activity);

		mActivity = this;
		mImageLoader = new ImageLoader(this, R.drawable.loadimage_2x);
		mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// get the info about the program to be displayed from tv-guide listview
		Intent intent = getIntent();
		mBroadcast = intent.getParcelableExtra(Consts.INTENT_EXTRA_BROADCAST);
		mChannel = intent.getParcelableExtra(Consts.INTENT_EXTRA_CHANNEL);

		if (mBroadcast == null || mChannel == null) {

			String broadcastPageUrl = intent.getStringExtra(Consts.INTENT_EXTRA_BROADCAST_URL);
			/*
			 * final SSBroadcastPage broadcastPage = new SSBroadcastPage(broadcastPageUrl); broadcastPage.getPage(new SSPageCallback() {
			 * 
			 * @Override public void onGetPageResult(SSPageGetResult aPageGetResult) {
			 * 
			 * mBroadcast = broadcastPage.getBroadcast(); Log.d(TAG,"Broadcast is null:" + mBroadcast.getBeginTime()); } });
			 */
			Toast.makeText(this, "Soon I will parse this broadcast page!", Toast.LENGTH_SHORT).show();
		}

		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		if (token != null && token.isEmpty() != true) {
			mIsLoggedIn = true;
		}

		initViews();
		if (mBroadcast != null && mChannel != null) {
			populateBlocks();
		}
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
	}

	private void populateBlocks() {
		// add the current broadcast top block
		// SSBroadcastBlockPopulator broadcastBlockPopulator = new SSBroadcastBlockPopulator(this, mBlockContainer);
		// broadcastBlockPopulator.createBlock(mBroadcast, mChannel);

		Program program = mBroadcast.getProgram();

		mActionBar.setTitle(mBroadcast.getProgram().getTitle());

		if (program.getPosterLUrl() != null && program.getPosterLUrl().isEmpty() != true) {
			mImageLoader.displayImage(program.getPosterLUrl(), mPosterIv, mPosterPb, ImageLoader.IMAGE_TYPE.POSTER);
		}
		mTitleTv.setText(program.getTitle());
		// seasonTv.setText(program.getSeason().getNumber());
		mEpisodeTv.setText(program.getEpisode());

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

		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		NotificationDbItem dbItem = new NotificationDbItem();
		dbItem = notificationDataSource.getNotification(mChannel.getChannelId(), mBroadcast.getBeginTimeMillis());
		if (dbItem.getNotificationId() != 0) {
			mIsSet = true;
			notificationId = dbItem.getNotificationId();
		} else {
			mIsSet = false;
			notificationId = -1;
		}

		if (mIsSet) mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
		else mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));

		// TODO TO GET TO KNOW IF IT IS ACTUALLY LIKED
		if (mIsLoggedIn) {
			Log.d(TAG, "" + mBroadcast.getProgram().getProgramId());
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
			break;
		case R.id.show_me:
			break;
		case R.id.show_activity:
			break;
		case R.id.block_social_panel_like_button_iv:
			if (mIsLoggedIn) {
				if (mIsLiked == false) {
					if (LikeService.addLike(token, mBroadcast.getProgram().getProgramId(), entityType)) {
						LikeService.showSetLikeToast(mActivity, mBroadcast.getProgram().getTitle());
						mLikeButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart_red));
						mIsLiked = true;
					} else {
						Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
					}
				} else {
					LikeDialogHandler likeDlg = new LikeDialogHandler();
					likeDlg.showRemoveLikeDialog(mActivity, token, mBroadcast.getProgram().getProgramId(), yesLikeProc(), noLikeProc());
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

			if (mIsSet == false) {
				if (NotificationService.setAlarm(mActivity, mBroadcast, mChannel)) {
					NotificationService.showSetNotificationToast(mActivity);
					mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
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
			break;
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
				Log.d(TAG, "No login");
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
				Log.d(TAG, "No changes to likes");
			}
		};
	}

	public Runnable yesNotificationProc() {
		return new Runnable() {
			public void run() {
				Log.d(TAG, "Notification is removed");
				mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
				mIsSet = false;
			}
		};
	}

	public Runnable noNotificationProc() {
		return new Runnable() {
			public void run() {
				Log.d(TAG, "No changes to the notification");
			}
		};
	}
}
