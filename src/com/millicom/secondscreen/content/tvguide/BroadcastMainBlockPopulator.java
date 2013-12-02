package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;

import com.millicom.secondscreen.authentication.PromptSignInDialogHandler;
import com.millicom.secondscreen.authentication.SignInActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
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

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BroadcastMainBlockPopulator {

	private static final String		TAG				= "BroadcastMainBlockPopulator";

	private Activity				mActivity;
	private ImageLoader				mImageLoader;
	private ScrollView				mContainerView;
	private ImageView				mLikeIv, mRemindIv;
	private NotificationDataSource	mNotificationDataSource;
	private boolean					mIsSet			= false, mIsLiked = false, mIsLoggedIn = false, mIsFuture;
	private int						mNotificationId	= -1;
	private String					mToken, mProgramId, mLikeType, mTvDate, mContentTitle;

	public BroadcastMainBlockPopulator(Activity activity, ScrollView containerView, String token, String tvDate) {
		this.mActivity = activity;
		this.mToken = token;
		this.mTvDate = tvDate;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
		this.mContainerView = containerView;
		this.mNotificationDataSource = new NotificationDataSource(mActivity);
	}

	public void createBlock(final Broadcast broadcast) {
		LinearLayout containerView = (LinearLayout) mContainerView.findViewById(R.id.broacastpage_block_container_layout);

		View topContentView = LayoutInflater.from(mActivity).inflate(R.layout.block_broadcastpage_main_content, null);
		ImageView posterIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_poster_iv);
		ProgressBar posterPb = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_poster_progressbar);
		TextView titleTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_title_tv);
		TextView seasonTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_season_tv);
		TextView episodeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_tv);
		TextView episodeNameTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_name_tv);
		TextView timeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_time_tv);
		ImageView channelIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_channel_iv);
		TextView synopsisTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_synopsis_tv);
		TextView extraTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_extra_tv);
		TextView tagsTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_tags_tv);

		mLikeIv = (ImageView) topContentView.findViewById(R.id.block_social_panel_like_button_iv);
		ImageView mShareIv = (ImageView) topContentView.findViewById(R.id.block_social_panel_share_button_iv);
		mRemindIv = (ImageView) topContentView.findViewById(R.id.block_social_panel_remind_button_iv);

		RelativeLayout likeContainer = (RelativeLayout) topContentView.findViewById(R.id.block_social_panel_like_button_container);
		RelativeLayout shareContainer = (RelativeLayout) topContentView.findViewById(R.id.block_social_panel_share_button_container);
		RelativeLayout remindContainer = (RelativeLayout) topContentView.findViewById(R.id.block_social_panel_remind_button_container);

//		RelativeLayout progressBarContainer = (RelativeLayout) topContentView.findViewById(R.id.block_broadcastpage_broadcast_progress_container);
		ProgressBar progressBar = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_broadcast_progressbar);
		TextView progressTxt = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_timeleft_tv);


		try {
			mIsFuture = DateUtilities.isTimeInFuture(broadcast.getBeginTime());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		if (mToken != null && TextUtils.isEmpty(mToken) != true) {
			Log.d(TAG, "LOGGED IN!");
			mIsLoggedIn = true;
		}

		Program program = broadcast.getProgram();
		String programType = program.getProgramType();

		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
			mProgramId = broadcast.getProgram().getSeries().getSeriesId();
			mContentTitle = broadcast.getProgram().getTitle();
			seasonTv.setText(mActivity.getResources().getString(R.string.season) + " " + program.getSeason().getNumber() + " ");
			seasonTv.setVisibility(View.VISIBLE);

			episodeTv.setText(mActivity.getResources().getString(R.string.episode) + " " + String.valueOf(program.getEpisodeNumber()));
			episodeTv.setVisibility(View.VISIBLE);

			titleTv.setText(program.getSeries().getName());

			episodeNameTv.setText(program.getTitle());
			episodeNameTv.setVisibility(View.VISIBLE);

		} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)){
			mProgramId = broadcast.getProgram().getSportType().getSportTypeId();
			mContentTitle = broadcast.getProgram().getSportType().getName();
			titleTv.setText(program.getTitle());
			
			episodeNameTv.setText(program.getSportType().getName());
			episodeNameTv.setVisibility(View.VISIBLE);
		}
		else{
			mContentTitle = broadcast.getProgram().getTitle();
			mProgramId = broadcast.getProgram().getProgramId();
			titleTv.setText(program.getTitle());
		}
		mLikeType = LikeService.getLikeType(programType);

		if (program.getPortSUrl() != null && TextUtils.isEmpty(program.getPortSUrl()) != true) {
			mImageLoader.displayImage(program.getLandLUrl(), posterIv, posterPb, ImageLoader.IMAGE_TYPE.POSTER);
		}

		if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
			extraTv.setText(program.getGenre() + " " + program.getYear());
			extraTv.setVisibility(View.VISIBLE);
		} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
			extraTv.setText(program.getCategory());
			extraTv.setVisibility(View.VISIBLE);
		} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)){
			Log.d(TAG,"" + program.getSportType().getName());
			Log.d(TAG,"" + program.getSportType().getSportTypeId());
			extraTv.setText(program.getSportType().getName());
			extraTv.setVisibility(View.VISIBLE);
		}

		String beginTimeStr, endTimeStr;
		long timeSinceBegin = 0;
		long timeToEnd = 0;
		try {
			beginTimeStr = DateUtilities.isoStringToTimeString(broadcast.getBeginTime());
			endTimeStr = DateUtilities.isoStringToTimeString(broadcast.getEndTime());
			timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
			timeToEnd = DateUtilities.getAbsoluteTimeDifference(broadcast.getEndTime());
		} catch (ParseException e) {
			beginTimeStr = "";
			endTimeStr = "";
			e.printStackTrace();
		}

		if(broadcast.getChannel()!=null){
			mImageLoader.displayImage(broadcast.getChannel().getLogoLUrl(), channelIv, ImageLoader.IMAGE_TYPE.THUMBNAIL);
		}


		// broadcast is currently on air: show progress
		if (timeSinceBegin > 0 && timeToEnd < 0) {

			int duration = 0;
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getEndTime());
				duration = (int) (startTime - endTime) / (1000 * 60);
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}

			progressBar.setMax(duration);

			// Calculate the current progress of the ProgressBar and update.
			int initialProgress = 0;
			long difference = 0;
			try {
				difference = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (difference < 0) {
				initialProgress = 0;
				progressBar.setProgress(0);
			} else {
				initialProgress = (int) difference / (1000 * 60);
				progressTxt.setText(duration - initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
				progressBar.setProgress(initialProgress);
				progressTxt.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
				timeTv.setVisibility(View.GONE);
			}
		}
		// broadcast is in the future: show time
		else {
			try {
				timeTv.setText(DateUtilities.isoStringToDayOfWeek(broadcast.getBeginTime()) + " " + DateUtilities.tvDateStringToDatePickerString(broadcast.getBeginTime()) + " " + beginTimeStr + "-" + endTimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		synopsisTv.setText(program.getSynopsisShort());

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < program.getTags().size(); i++) {
			sb.append(program.getTags().get(i));
			sb.append(" ");
		}

		tagsTv.setText(sb.toString());

		if (!mIsFuture) {
			NotificationDbItem dbItem = new NotificationDbItem();
			// Sometime channel is null, avoiding crash
			if (broadcast.getChannel() == null) {
				Toast.makeText(mActivity, "Channel null", Toast.LENGTH_LONG).show();
			}
			else {
				dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillis());
			}
			if (dbItem.getNotificationId() != 0) {
				mIsSet = true;
				mNotificationId = dbItem.getNotificationId();
			} else {
				mIsSet = false;
				mNotificationId = -1;
			}

			if (mIsSet) mRemindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			else mRemindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
		} else {
			mRemindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
		}

		if (mIsLoggedIn) {
			//mIsLiked = LikeService.isLiked(mToken, broadcast.getProgram().getProgramId());
			mIsLiked = DazooStore.getInstance().isInTheLikesList(mProgramId);
		}

		if (mIsLiked) mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
		else mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

		likeContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsLoggedIn) {
					if (mIsLiked == false) {
						if (LikeService.addLike(mToken, mProgramId, mLikeType)) {
							LikeService.showSetLikeToast(mActivity, mContentTitle);
							mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));

							AnimationUtilities.animationSet(mLikeIv);

							mIsLiked = true;
						} else {
							Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
						}

					} else {
						// LikeDialogHandler likeDlg = new LikeDialogHandler();
						// likeDlg.showRemoveLikeDialog(mActivity, mToken, mLikeType, broadcast.getProgram().getProgramId(), yesLikeProc(), noLikeProc());
						LikeService.removeLike(mToken, mProgramId, mLikeType);
						mIsLiked = false;
						mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));
					}
				} else {
					PromptSignInDialogHandler loginDlg = new PromptSignInDialogHandler();
					loginDlg.showPromptSignInDialog(mActivity, yesLoginProc(), noLoginProc());
				}

			}
		});

		shareContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), broadcast.getShareUrl(), mActivity.getResources().getString(R.string.share_action_title));
			}
		});

		remindContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mIsFuture) {
					if (mIsSet == false) {
						if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), mTvDate)) {
							NotificationService.showSetNotificationToast(mActivity);
							mRemindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

							NotificationDbItem dbItem = new NotificationDbItem();
							dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillis());

							mNotificationId = dbItem.getNotificationId();

							AnimationUtilities.animationSet(mRemindIv);

							mIsSet = true;
						} else {
							Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						if (mNotificationId != -1) {
							NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
							notificationDlg.showRemoveNotificationDialog(mActivity, broadcast, mNotificationId, yesNotificationProc(), noNotificationProc());
						} else {
							Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
						}
					}
				} 
			}
		});
		topContentView.setVisibility(View.VISIBLE);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10, 10, 10, 10);
		containerView.addView(topContentView, layoutParams);
	}

	public Runnable yesLoginProc() {
		return new Runnable() {
			public void run() {
				Intent intent = new Intent(mActivity, SignInActivity.class);
				// Intent intent = new Intent(BroadcastPageActivity.this, LoginActivity.class);
				mActivity.startActivity(intent);
				mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
				mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));
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
				mRemindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
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
