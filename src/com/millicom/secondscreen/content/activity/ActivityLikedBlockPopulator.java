package com.millicom.secondscreen.content.activity;

import java.text.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.BlockPopularListViewAdapter;
import com.millicom.secondscreen.authentication.PromptSignInDialogHandler;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.like.LikeDialogHandler;
import com.millicom.secondscreen.like.LikeService;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationDialogHandler;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.share.ShareAction;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class ActivityLikedBlockPopulator {

	private static final String	TAG	= "ActivityLikedBlockPopulator";

	private Activity			mActivity;
	private LinearLayout		mContainerView;
	private ImageLoader			mImageLoader;
	private ImageView			remindIv, likeIv;
	private boolean				mIsSet	= false, mIsLiked = false;
	private String				mToken;
	private int					mNotificationId;
	NotificationDataSource		mNotificationDataSource;

	public ActivityLikedBlockPopulator(Activity activity, LinearLayout containerView, String token) {
		this.mActivity = activity;
		this.mContainerView = containerView;
		this.mToken = token;
		//this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
		this.mImageLoader = new ImageLoader(mActivity, 0);
	}

	public void createBlock(final FeedItem popularItem) {
		mIsLiked = LikeService.isLiked(mToken, popularItem.getBroadcast().getProgram().getProgramId());

		mNotificationDataSource = new NotificationDataSource(mActivity);
		NotificationDbItem dbItem = new NotificationDbItem();
		dbItem = mNotificationDataSource.getNotification(popularItem.getBroadcast().getChannel().getChannelId(), popularItem.getBroadcast().getBeginTimeMillisGmt());
		if (dbItem.getNotificationId() != 0) {
			mIsSet = true;
			mNotificationId = dbItem.getNotificationId();
		} else {
			mIsSet = false;
			mNotificationId = -1;
		}

		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_liked, null);
		RelativeLayout container = (RelativeLayout) contentView.findViewById(R.id.block_feed_liked_main_container);
		TextView headerTv = (TextView) contentView.findViewById(R.id.block_feed_liked_header_tv);
		ImageView landscapeIv = (ImageView) contentView.findViewById(R.id.block_feed_liked_content_iv);
		ProgressBar landscapePb = (ProgressBar) contentView.findViewById(R.id.block_feed_liked_content_iv_progressbar);
		ImageView iconIv = (ImageView) contentView.findViewById(R.id.block_feed_liked_icon);
		TextView titleTv = (TextView) contentView.findViewById(R.id.block_feed_liked_title_tv);
		TextView timeTv = (TextView) contentView.findViewById(R.id.block_feed_liked_time_tv);
		TextView channelTv = (TextView) contentView.findViewById(R.id.block_feed_liked_channel_tv);
		TextView detailsTv = (TextView) contentView.findViewById(R.id.block_feed_liked_details_tv);
		TextView progressbarTv = (TextView) contentView.findViewById(R.id.block_feed_liked_timeleft_tv);
		ProgressBar progressBar = (ProgressBar) contentView.findViewById(R.id.block_feed_liked_progressbar);
		LinearLayout likeContainer = (LinearLayout) contentView.findViewById(R.id.block_feed_liked_like_button_container);
		likeIv = (ImageView) contentView.findViewById(R.id.block_feed_liked_like_button_iv);
		LinearLayout shareContainer = (LinearLayout) contentView.findViewById(R.id.block_feed_liked_share_button_container);
		ImageView shareIv = (ImageView) contentView.findViewById(R.id.block_feed_liked_share_button_iv);
		LinearLayout remindContainer = (LinearLayout) contentView.findViewById(R.id.block_feed_liked_remind_button_container);
		remindIv = (ImageView) contentView.findViewById(R.id.block_feed_liked_remind_button_iv);

		headerTv.setText(popularItem.getTitle());

		mImageLoader.displayImage(popularItem.getBroadcast().getProgram().getLandMUrl(), landscapeIv, ImageLoader.IMAGE_TYPE.GALLERY);

		final String programType = popularItem.getBroadcast().getProgram().getProgramType();
		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
			titleTv.setText(popularItem.getBroadcast().getProgram().getSeries().getName());
		} else {
			titleTv.setText(popularItem.getBroadcast().getProgram().getTitle());
		}

		
		timeTv.setText(popularItem.getBroadcast().getDayOfWeekWithTimeString());

		channelTv.setText(popularItem.getBroadcast().getChannel().getName());

		if (programType != null) {
			if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
				detailsTv.setText(popularItem.getBroadcast().getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + popularItem.getBroadcast().getProgram().getYear());
			} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				detailsTv.setText(mActivity.getResources().getString(R.string.season) + " " + popularItem.getBroadcast().getProgram().getSeason().getNumber() + " "
						+ mActivity.getResources().getString(R.string.episode) + " " + popularItem.getBroadcast().getProgram().getEpisodeNumber());
			} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
				detailsTv.setText(popularItem.getBroadcast().getProgram().getSportType() + " " + popularItem.getBroadcast().getProgram().getTournament());
			} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
				detailsTv.setText(popularItem.getBroadcast().getProgram().getCategory());
			}
		}

		container.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, popularItem.getBroadcast().getBeginTimeMillisGmt());
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, popularItem.getBroadcast().getChannel().getChannelId());
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, popularItem.getBroadcast().getTvDateString());

				mActivity.startActivity(intent);
				mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

			}
		});

		if (mIsSet) remindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
		else remindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));

		if (mIsLiked) likeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
		else likeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

		likeContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String likeType = LikeService.getLikeType(programType);

				String programId, contentTitle;
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					programId = popularItem.getBroadcast().getProgram().getSeries().getSeriesId();
					contentTitle = popularItem.getBroadcast().getProgram().getTitle();
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)){
					programId = popularItem.getBroadcast().getProgram().getSportType().getSportTypeId();
					contentTitle = popularItem.getBroadcast().getProgram().getSportType().getName();
				}
				else {
					programId = popularItem.getBroadcast().getProgram().getProgramId();
					contentTitle = popularItem.getBroadcast().getProgram().getTitle();
				}

				if (mIsLiked == false) {
					if (LikeService.addLike(mToken, programId, likeType)) {
						LikeService.showSetLikeToast(mActivity, contentTitle);
						likeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
						mIsLiked = true;
					} else {
						//Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "!!! Adding a like faced an error !!!");
					}
				} else {
					LikeDialogHandler likeDlg = new LikeDialogHandler();
					likeDlg.showRemoveLikeDialog(mActivity, mToken, likeType, popularItem.getBroadcast().getProgram().getProgramId(), yesLikeProc(), noLikeProc());
				}
			}
		});

		shareContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), popularItem.getBroadcast().getShareUrl(),
						mActivity.getResources().getString(R.string.share_action_title));
			}
		});

		remindContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsSet == false) {
					if (NotificationService.setAlarm(mActivity, popularItem.getBroadcast(), popularItem.getBroadcast().getChannel(), popularItem.getBroadcast().getTvDateString())) {
						NotificationService.showSetNotificationToast(mActivity);
						remindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

						NotificationDbItem dbItem = new NotificationDbItem();
						dbItem = mNotificationDataSource.getNotification(popularItem.getBroadcast().getChannel().getChannelId(), popularItem.getBroadcast().getBeginTimeMillisGmt());

						mNotificationId = dbItem.getNotificationId();

						mIsSet = true;
					} else {
						//Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "!!! Setting notification faced an error !!!");
					}
				} else {
					if (mNotificationId != -1) {
						NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
						notificationDlg.showRemoveNotificationDialog(mActivity, popularItem.getBroadcast(), mNotificationId, yesNotificationProc(), noNotificationProc());
					} else {
						//Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "!!! Could not find such reminder in DB !!!");
					}
				}

			}
		});

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.setMargins(0, 0, 0, 20);
		mContainerView.addView(contentView, layoutParams);
		
	}

	public Runnable yesLikeProc() {
		return new Runnable() {
			public void run() {
				likeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));
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
				remindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
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
