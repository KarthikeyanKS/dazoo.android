package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;

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
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationDialogHandler;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.share.ShareAction;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class BroadcastPageActivity extends ActionBarActivity implements OnClickListener {

	private static final String							TAG		= "BroadcastPageActivity";
	private ImageLoader									mImageLoader;
	private Broadcast									mBroadcast;
	private Channel										mChannel;
	private LinearLayout								mBlockContainer;
	private ActionBar									mActionBar;
	private LayoutInflater								mLayoutInflater;
	private String										mBroadcastUrl;
	private boolean										mIsSet	= false;
	private ImageView									mPosterIv, mLikeButtonIv, mShareButtonIv, mRemindButtonIv;
	private ProgressBar									mPosterPb;
	private TextView									mTitleTv, mSeasonTv, mEpisodeTv, mTimeTv, mDateTv, mChannelTv, mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;
	private int											notificationId;
	private View										mTabSelectorContainerView;

	private Activity									mActivity;

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

		initViews();
		if (mBroadcast != null && mChannel != null) {
			populateBlocks();
		}
	}

	private void initViews() {
		// styling the Action Bar
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setCustomView(R.layout.layout_actionbar_programpage);

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

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.black));
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

		mShareButtonIv.setOnClickListener(this);

		mBlockContainer = (LinearLayout) findViewById(R.id.broacastpage_block_container_layout);
	}

	private void populateBlocks() {
		// add the current broadcast top block
		// SSBroadcastBlockPopulator broadcastBlockPopulator = new SSBroadcastBlockPopulator(this, mBlockContainer);
		// broadcastBlockPopulator.createBlock(mBroadcast, mChannel);

		Program program = mBroadcast.getProgram();

		mImageLoader.displayImage(program.getPosterLUrl(), mPosterIv, mPosterPb, ImageLoader.IMAGE_TYPE.POSTER);
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

		if (mIsSet == true) {
			mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
		} else {
			mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
		}

		mRemindButtonIv.setOnClickListener(this);

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

	public Runnable yesProc() {
		return new Runnable() {
			public void run() {
				Log.d(TAG, "Notification is removed");
				mRemindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
				mIsSet = false;
			}
		};
	}

	public Runnable noProc() {
		return new Runnable() {
			public void run() {
				Log.d(TAG, "No changes to the notification");
			}
		};
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
		case R.id.show_tvguide:
			break;
		case R.id.show_me:
			break;
		case R.id.show_activity:
			break;
		case R.id.block_social_panel_share_button_iv:
			ShareAction.shareAction(mActivity, "subject", "link to share", getResources().getString(R.string.share_action_title));
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
				NotificationDataSource notificationDataSource = new NotificationDataSource(mActivity);

				NotificationDbItem notificationDbItem = new NotificationDbItem();
				notificationDbItem = notificationDataSource.getNotification(mChannel.getChannelId(), Long.valueOf(mBroadcast.getBeginTimeMillis()));
				if (notificationDbItem != null) {
					NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
					notificationDlg.showRemoveNotificationDialog(mActivity, mBroadcast, notificationId, yesProc(), noProc());
				} else {
					Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
				}
			}	
			break;
		}	
	}
}
