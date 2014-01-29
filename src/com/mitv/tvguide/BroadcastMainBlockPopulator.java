package com.mitv.tvguide;

import java.util.Locale;

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

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.authentication.PromptSignInDialogHandler;
import com.mitv.authentication.SignInActivity;
import com.mitv.like.LikeService;
import com.mitv.model.Broadcast;
import com.mitv.model.NotificationDbItem;
import com.mitv.model.Program;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationDialogHandler;
import com.mitv.notification.NotificationService;
import com.mitv.share.ShareAction;
import com.mitv.storage.DazooStore;
import com.mitv.utilities.AnimationUtilities;
import com.mitv.utilities.ProgressBarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class BroadcastMainBlockPopulator {

	private static final String		TAG				= "BroadcastMainBlockPopulator";

	private Activity				mActivity;
	private ScrollView				mContainerView;
	private ImageView				mLikeIv, mRemindIv;
	private NotificationDataSource	mNotificationDataSource;
	private boolean					mIsSet			= false, mIsLiked = false, mIsLoggedIn = false;
	private int						mNotificationId	= -1;
	private String					mToken, mProgramId, mLikeType, mTvDate, mContentTitle;

	public BroadcastMainBlockPopulator(Activity activity, ScrollView containerView, String token, String tvDate) {
		this.mActivity = activity;
		this.mToken = token;
		this.mTvDate = tvDate;
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

		mLikeIv = (ImageView) topContentView.findViewById(R.id.element_social_buttons_like_button_iv);
		ImageView mShareIv = (ImageView) topContentView.findViewById(R.id.element_social_buttons_share_button_iv);
		mRemindIv = (ImageView) topContentView.findViewById(R.id.element_social_buttons_remind_button_iv);

		RelativeLayout likeContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_like_button_container);
		RelativeLayout shareContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_share_button_container);
		RelativeLayout remindContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_remind_button_container);

		// RelativeLayout progressBarContainer = (RelativeLayout) topContentView.findViewById(R.id.block_broadcastpage_broadcast_progress_container);
		ProgressBar progressBar = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_broadcast_progressbar);
		TextView progressTxt = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_timeleft_tv);

		// try {
		// mIsFuture = DateUtilities.isTimeInFuture(broadcast.getBeginTimeMillisLocal());
		// } catch (ParseException e1) {
		// e1.printStackTrace();
		// }

		if (mToken != null && TextUtils.isEmpty(mToken) != true) {
			Log.d(TAG, "LOGGED IN!");
			mIsLoggedIn = true;
		}

		Program program = broadcast.getProgram();
		String programType = program.getProgramType();

		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
			mProgramId = broadcast.getProgram().getSeries().getSeriesId();
			mContentTitle = broadcast.getProgram().getSeries().getName();
			if (!program.getSeason().getNumber().equals("0")) {
				seasonTv.setText(mActivity.getResources().getString(R.string.season) + " " + program.getSeason().getNumber() + " ");
				seasonTv.setVisibility(View.VISIBLE);
			}
			if (program.getEpisodeNumber() > 0) {
				episodeTv.setText(mActivity.getResources().getString(R.string.episode) + " " + String.valueOf(program.getEpisodeNumber()));
				episodeTv.setVisibility(View.VISIBLE);
			}
			if (program.getSeason().getNumber().equals("0") && program.getEpisodeNumber() == 0) {
				episodeNameTv.setTextSize(18);
			}

			titleTv.setText(program.getSeries().getName());
			
			String episodeName = program.getTitle();
			if (episodeName.length() > 0) {
				episodeNameTv.setText(episodeName);
				episodeNameTv.setVisibility(View.VISIBLE);
			}

		} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
			mProgramId = broadcast.getProgram().getSportType().getSportTypeId();
			mContentTitle = broadcast.getProgram().getSportType().getName();
			titleTv.setText(program.getTitle());
			if (program.getTournament() != null) {
				episodeNameTv.setText(program.getTournament());
				episodeNameTv.setVisibility(View.VISIBLE);
			} else {
				episodeNameTv.setText(program.getSportType().getName());
				episodeNameTv.setVisibility(View.VISIBLE);
			}
		} else {
			mContentTitle = broadcast.getProgram().getTitle();
			mProgramId = broadcast.getProgram().getProgramId();
			titleTv.setText(program.getTitle());
		}
		mLikeType = LikeService.getLikeType(programType);

		if (program.getPortSUrl() != null && TextUtils.isEmpty(program.getPortSUrl()) != true) {
			ImageAware imageAware = new ImageViewAware(posterIv, false);
			ImageLoader.getInstance().displayImage(program.getLandLUrl(), imageAware);
		}

		if (broadcast.getChannel() != null) {
			ImageAware imageAware = new ImageViewAware(channelIv, false);
			ImageLoader.getInstance().displayImage(broadcast.getChannel().getImageUrl(), imageAware);
		}

		// broadcast is currently on air: show progress
		if (broadcast.isRunning()) {
			ProgressBarUtils.setupProgressBar(mActivity, broadcast, progressBar, progressTxt);
			timeTv.setVisibility(View.GONE);
		}
		// broadcast is in the future: show time
		else {
			timeTv.setText(broadcast.getDayOfWeekWithTimeString() + " - " + broadcast.getEndTimeStringLocal());
		}

		String synopsis = program.getSynopsisShort();
		if (TextUtils.isEmpty(synopsis) == false) {
			synopsisTv.setText(program.getSynopsisShort());
			synopsisTv.setVisibility(View.VISIBLE);
		}

		int duration = broadcast.getDurationInMinutes();
		String extras = "";
		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
			extras = mActivity.getResources().getString(R.string.tv_series) + "  "
					+ ((program.getYear() == 0) ? "" : String.valueOf(program.getYear()) + "  ")
					+ ((duration == 0) ? "" : duration + " " + mActivity.getResources().getString(R.string.minutes) + "  ")
					+ ((program.getGenre() == null) ? "" : (program.getGenre()));
		} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
			extras = mActivity.getResources().getString(R.string.movie) + "  " 
					+ ((program.getYear() == 0) ? "" : String.valueOf(program.getYear()) + "  ")
					+ ((duration == 0) ? "" : duration + " " + mActivity.getResources().getString(R.string.minutes) + "  ")
					+ ((program.getGenre() == null) ? "" : (program.getGenre()));
		} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
			extras = program.getCategory() + "  "
					+ ((duration == 0) ? "" : duration + " " + mActivity.getResources().getString(R.string.minutes));
		} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
			extras = mActivity.getResources().getString(R.string.sport) + "  "
					+ ((duration == 0) ? "" : duration + " " + mActivity.getResources().getString(R.string.minutes) + "  ")
					+ program.getSportType().getName();
		}
		extraTv.setText(extras);
		extraTv.setVisibility(View.VISIBLE);

		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < program.getTags().size(); i++) {
		// sb.append(program.getTags().get(i));
		// sb.append(" ");
		// }
		//
		// tagsTv.setText(sb.toString());
		// tagsTv.setVisibility(View.VISIBLE);

		if (!broadcast.hasStarted()) {
			NotificationDbItem dbItem = new NotificationDbItem();
			// Sometime channel is null, avoiding crash
			if (broadcast.getChannel() == null) {
				// Toast.makeText(mActivity, "Channel null", Toast.LENGTH_LONG).show();
				Log.d(TAG, "Channe is null");
			} else {
				dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
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
			// mIsLiked = LikeService.isLiked(mToken, broadcast.getProgram().getProgramId());
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
							BroadcastPageActivity.toast = LikeService.showSetLikeToast(mActivity, mContentTitle);

							DazooStore.getInstance().addLikeIdToList(mProgramId);

							mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));

							AnimationUtilities.animationSet(mLikeIv);

							mIsLiked = true;
						} else {
							// Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
							Log.d(TAG, "!!! Adding a like faced an error !!!");
						}

					} else {
						// LikeDialogHandler likeDlg = new LikeDialogHandler();
						// likeDlg.showRemoveLikeDialog(mActivity, mToken, mLikeType, broadcast.getProgram().getProgramId(), yesLikeProc(), noLikeProc());
						LikeService.removeLike(mToken, mProgramId, mLikeType);

						DazooStore.getInstance().deleteLikeIdFromList(mProgramId);

						mIsLiked = false;
						mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));
					}
				} else {
					if (BroadcastPageActivity.toast != null) {
						BroadcastPageActivity.toast.cancel();
					}
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
				if (!broadcast.hasStarted()) {
					if (mIsSet == false) {
						if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), mTvDate)) {
							BroadcastPageActivity.toast = NotificationService.showSetNotificationToast(mActivity);
							mRemindIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

							NotificationDbItem dbItem = new NotificationDbItem();
							dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());

							mNotificationId = dbItem.getNotificationId();

							AnimationUtilities.animationSet(mRemindIv);

							mIsSet = true;
						} else {
							// Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
							Log.d(TAG, "!!! Setting notification faced an error !!!");
						}
					} else {
						if (mNotificationId != -1) {
							if (BroadcastPageActivity.toast != null) {
								BroadcastPageActivity.toast.cancel();
							}
							NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
							notificationDlg.showRemoveNotificationDialog(mActivity, broadcast, mNotificationId, yesNotificationProc(), noNotificationProc());
						} else {
							// Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
							Log.d(TAG, "!!! Could not find such reminder in DB !!!");
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
				mActivity.startActivity(intent);
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