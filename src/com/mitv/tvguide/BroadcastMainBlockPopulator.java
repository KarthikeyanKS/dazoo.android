package com.mitv.tvguide;

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
import com.mitv.SecondScreenApplication;
import com.mitv.authentication.MiTVLoginActivity;
import com.mitv.authentication.PromptSignInDialogHandler;
import com.mitv.authentication.SignInOrSignupWithFacebookActivity;
import com.mitv.customviews.ReminderView;
import com.mitv.like.LikeService;
import com.mitv.model.Broadcast;
import com.mitv.model.Program;
import com.mitv.notification.NotificationDataSource;
import com.mitv.share.ShareAction;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.AnimationUtilities;
import com.mitv.utilities.ProgressBarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class BroadcastMainBlockPopulator {

	private static final String		TAG				= "BroadcastMainBlockPopulator";

	private Activity				mActivity;
	private ScrollView				mContainerView;
	private ImageView				mLikeIv;
	private boolean					mIsLiked = false;
	private String					mProgramId, mLikeType, mContentTitle;

	public BroadcastMainBlockPopulator(Activity activity, ScrollView containerView) {
		this.mActivity = activity;
		this.mContainerView = containerView;
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
		
		ReminderView reminderView = (ReminderView) topContentView.findViewById(R.id.element_social_buttons_reminder);
		reminderView.setBroadcast(broadcast);

		mLikeIv = (ImageView) topContentView.findViewById(R.id.element_social_buttons_like_button_iv);
		ImageView mShareIv = (ImageView) topContentView.findViewById(R.id.element_social_buttons_share_button_iv);

		RelativeLayout likeContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_like_button_container);
		RelativeLayout shareContainer = (RelativeLayout) topContentView.findViewById(R.id.element_social_buttons_share_button_container);

		ProgressBar progressBar = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_broadcast_progressbar);
		TextView progressTxt = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_timeleft_tv);

		Program program = broadcast.getProgram();
		String programType = program.getProgramType();

		if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
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

		} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
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
		if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
			extras = mActivity.getResources().getString(R.string.tv_series) + "  "
					+ ((program.getYear() == 0) ? "" : String.valueOf(program.getYear()) + "  ")
					+ ((duration == 0) ? "" : duration + " " + mActivity.getResources().getString(R.string.minutes) + "  ")
					+ ((program.getGenre() == null) ? "" : (program.getGenre()));
		} else if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
			extras = mActivity.getResources().getString(R.string.movie) + "  " 
					+ ((program.getYear() == 0) ? "" : String.valueOf(program.getYear()) + "  ")
					+ ((duration == 0) ? "" : duration + " " + mActivity.getResources().getString(R.string.minutes) + "  ")
					+ ((program.getGenre() == null) ? "" : (program.getGenre()));
		} else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) {
			extras = program.getCategory() + "  "
					+ ((duration == 0) ? "" : duration + " " + mActivity.getResources().getString(R.string.minutes));
		} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
			extras = mActivity.getResources().getString(R.string.sport) + "  "
					+ ((duration == 0) ? "" : duration + " " + mActivity.getResources().getString(R.string.minutes) + "  ")
					+ program.getSportType().getName();
		}
		extraTv.setText(extras);
		extraTv.setVisibility(View.VISIBLE);

		if (SecondScreenApplication.isLoggedIn()) {
			// mIsLiked = LikeService.isLiked(mToken, broadcast.getProgram().getProgramId());
			mIsLiked = MiTVStore.getInstance().isInTheLikesList(mProgramId);
		}

		if (mIsLiked) mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
		else mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

		likeContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SecondScreenApplication.isLoggedIn()) {
					if (mIsLiked == false) {
						if (LikeService.addLike(mProgramId, mLikeType)) {
							BroadcastPageActivity.toast = LikeService.showSetLikeToast(mActivity, mContentTitle);

							MiTVStore.getInstance().addLikeIdToList(mProgramId);

							mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));

							AnimationUtilities.animationSet(mLikeIv);

							mIsLiked = true;
						} else {
							Log.d(TAG, "!!! Adding a like faced an error !!!");
						}

					} 
					else 
					{
						boolean removeSucceded = LikeService.removeLike(mLikeType, mProgramId);

						if(removeSucceded)
						{
							MiTVStore.getInstance().deleteLikeIdFromList(mProgramId);
	
							mIsLiked = false;
							mLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));
						}
						else
						{
							Log.w(TAG, "An error occured while removing a like from the webservice");
						}
					}
				} 
				else 
				{
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

		topContentView.setVisibility(View.VISIBLE);

		containerView.addView(topContentView);
	}

	public Runnable yesLoginProc() {
		return new Runnable() {
			public void run() {
				Intent intent = new Intent(mActivity, SignInOrSignupWithFacebookActivity.class);
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
}
