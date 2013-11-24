package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.activity.ActivityLikedBlockPopulator;
import com.millicom.secondscreen.content.activity.ActivityPopularBlockPopulator;
import com.millicom.secondscreen.content.activity.ActivityRecommendedBlockPopulator;
import com.millicom.secondscreen.content.activity.PopularPageActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityFeedAdapter extends BaseAdapter {

	private static final String		TAG									= "ActivityFeedAdapter";
	private Activity				mActivity;
	private ArrayList<FeedItem>		mFeedItems;
	private ImageLoader				mImageLoader;

	private int						DAZOO_ACTIVITY_BLOCKS_TYPE_NUMBER	= 3;
	private static final int		ITEM_TYPE_BROADCAST					= 0;
	private static final int		ITEM_TYPE_RECOMMENDED_BROADCAST		= 1;
	private static final int		ITEM_TYPE_POPULAR_BROADCASTS		= 2;

	private String					mToken;
	private int						mNotificationId;
	private NotificationDataSource	mNotificationDataSource;
	private ArrayList<String>		mLikeIds;

	private ImageView				likeLikeIv, remindLikeIv, likeRecIv, remindRecIv;
	private boolean					mIsLiked							= false, mIsSet = false;

	public ActivityFeedAdapter(Activity activity, ArrayList<FeedItem> feedItems, String token) {
		this.mActivity = activity;
		this.mFeedItems = feedItems;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
		this.mToken = token;
		this.mNotificationDataSource = new NotificationDataSource(mActivity);
		this.mLikeIds = LikeService.getLikeIdsList(token);
	}

	@Override
	public int getCount() {
		if (mFeedItems != null) {
			return mFeedItems.size();
		} else return 0;
	}

	@Override
	public FeedItem getItem(int position) {
		if (mFeedItems != null) {
			return mFeedItems.get(position);
		} else return null;
	}

	@Override
	public int getViewTypeCount() {
		return DAZOO_ACTIVITY_BLOCKS_TYPE_NUMBER;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addItem(final FeedItem item) {
		mFeedItems.add(item);
		notifyDataSetChanged();
	}

	public void addItems(ArrayList<FeedItem> items) {
		mFeedItems.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		FeedItem item = getItem(position);
		String feedItemType = item.getItemType();
		if (Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_BROADCASTS.equals(feedItemType)) {
			return ITEM_TYPE_POPULAR_BROADCASTS;
		} else if (Consts.DAZOO_FEED_ITEM_TYPE_BROADCAST.equals(feedItemType)) {
			return ITEM_TYPE_BROADCAST;
		} else if (Consts.DAZOO_FEED_ITEM_TYPE_RECOMMENDED_BROADCAST.equals(feedItemType)) {
			return ITEM_TYPE_RECOMMENDED_BROADCAST;
		}
		return ITEM_TYPE_BROADCAST;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);

		final FeedItem feedItem = getItem(position);

		switch (type) {
		case ITEM_TYPE_BROADCAST:
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_liked, null);

			RelativeLayout container = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_main_container);
			TextView headerTv = (TextView) convertView.findViewById(R.id.block_feed_liked_header_tv);
			ImageView landscapeIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_content_iv);
			ProgressBar landscapePb = (ProgressBar) convertView.findViewById(R.id.block_feed_liked_content_iv_progressbar);
			TextView titleTv = (TextView) convertView.findViewById(R.id.block_feed_liked_title_tv);
			TextView timeTv = (TextView) convertView.findViewById(R.id.block_feed_liked_time_tv);
			TextView channelTv = (TextView) convertView.findViewById(R.id.block_feed_liked_channel_tv);
			TextView detailsTv = (TextView) convertView.findViewById(R.id.block_feed_liked_details_tv);
			TextView progressbarTv = (TextView) convertView.findViewById(R.id.block_feed_liked_timeleft_tv);
			ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.block_feed_liked_progressbar);
			LinearLayout likeContainer = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_like_button_container);
			likeLikeIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_like_button_iv);
			LinearLayout shareContainer = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_share_button_container);
			ImageView shareIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_share_button_iv);
			LinearLayout remindContainer = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_remind_button_container);
			remindLikeIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_remind_button_iv);

			// mIsLiked = LikeService.isLiked(mToken, feedItem.getBroadcast().getProgram().getProgramId());

			final String programType = feedItem.getBroadcast().getProgram().getProgramType();
			// determine like
			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getSeries().getSeriesId());
			} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getSportType().getSportTypeId());
			} else {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getProgramId());
			}

			headerTv.setText(feedItem.getTitle() + " " + feedItem.getItemType());

			mImageLoader.displayImage(feedItem.getBroadcast().getProgram().getPosterMUrl(), landscapeIv, ImageLoader.IMAGE_TYPE.GALLERY);

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				titleTv.setText(feedItem.getBroadcast().getProgram().getSeries().getName());
			} else {
				titleTv.setText(feedItem.getBroadcast().getProgram().getTitle());
			}

			try {
				timeTv.setText(DateUtilities.isoStringToDayOfWeek(feedItem.getBroadcast().getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(feedItem.getBroadcast().getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			channelTv.setText(feedItem.getBroadcast().getChannel().getName());

			if (programType != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					detailsTv.setText(feedItem.getBroadcast().getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + feedItem.getBroadcast().getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					detailsTv.setText(mActivity.getResources().getString(R.string.season) + " " + feedItem.getBroadcast().getProgram().getSeason().getNumber() + " "
							+ mActivity.getResources().getString(R.string.episode) + " " + feedItem.getBroadcast().getProgram().getEpisodeNumber());
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
					detailsTv.setText(feedItem.getBroadcast().getProgram().getSportType().getName() + " " + feedItem.getBroadcast().getProgram().getTournament());
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
					detailsTv.setText(feedItem.getBroadcast().getProgram().getCategory());
				}
			}

			container.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(feedItem.getBroadcast().getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, feedItem.getBroadcast().getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, feedItem.getBroadcast().getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

				}
			});

			int duration = 0;
			// MC - Calculate the duration of the program and set up ProgressBar.
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getEndTime());
				duration = (int) (startTime - endTime) / (1000 * 60);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			progressBar.setMax(duration);

			// MC - Calculate the current progress of the ProgressBar and update.
			int initialProgress = 0;
			long difference = 0;
			try {
				difference = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (difference < 0) {
				progressBar.setVisibility(View.GONE);
				initialProgress = 0;
				progressBar.setProgress(0);
			} else {
				try {
					initialProgress = (int) DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime()) / (1000 * 60);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				progressbarTv.setText(duration - initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
				progressBar.setProgress(initialProgress);
				progressbarTv.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
			}

			NotificationDbItem dbItem = new NotificationDbItem();
			dbItem = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
			if (dbItem.getNotificationId() != 0) {
				mIsSet = true;
			} else {
				mIsSet = false;
			}

			if (mIsSet) remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
			else remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));

			if (mIsLiked) likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart_red));
			else likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart));

			likeContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String likeType = LikeService.getLikeType(programType);

					String programId, contentTitle;
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						programId = feedItem.getBroadcast().getProgram().getSeries().getSeriesId();
						contentTitle = feedItem.getBroadcast().getProgram().getTitle();
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
						programId = feedItem.getBroadcast().getProgram().getSportType().getSportTypeId();
						contentTitle = feedItem.getBroadcast().getProgram().getSportType().getName();
					} else {
						programId = feedItem.getBroadcast().getProgram().getProgramId();
						contentTitle = feedItem.getBroadcast().getProgram().getTitle();
					}

					if (mIsLiked == false) {
						// TODO: OPTIMIZATION ON WHEN THE BACKEND TO ADD/DELETE LIKE IS LAUNCHED
						if (LikeService.addLike(mToken, programId, likeType)) {
							DazooStore.getInstance().addLikeIdToList(programId);
							
							LikeService.showSetLikeToast(mActivity, contentTitle);
							likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart_red));

							AnimationUtilities.animationSet(likeLikeIv);

							mIsLiked = true;
						} else {
							Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						LikeService.removeLike(mToken, likeType, programId);
						DazooStore.getInstance().deleteLikeIdFromList(programId);

						mIsLiked = false;
						likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart));

					}
				}
			});

			shareContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), feedItem.getBroadcast().getShareUrl(),
							mActivity.getResources().getString(R.string.share_action_title));
				}
			});

			remindContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(feedItem.getBroadcast().getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					if (mIsSet == false) {
						if (NotificationService.setAlarm(mActivity, feedItem.getBroadcast(), feedItem.getBroadcast().getChannel(), tvDate)) {
							NotificationService.showSetNotificationToast(mActivity);
							remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));

							NotificationDbItem dbItem = new NotificationDbItem();
							Log.d(TAG, "feedItem.getBroadcast().getChannel().getChannelId()" + feedItem.getBroadcast().getChannel().getChannelId());
							Log.d(TAG, "feedItem.getBroadcast().getBeginTimeMillis()" + feedItem.getBroadcast().getBeginTimeMillis());

							dbItem = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());

							mNotificationId = dbItem.getNotificationId();

							AnimationUtilities.animationSet(remindLikeIv);

							mIsSet = true;
						} else {
							Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						if (mNotificationId != -1) {
							NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
							notificationDlg.showRemoveNotificationDialog(mActivity, feedItem.getBroadcast(), mNotificationId, yesNotificationProc(), noNotificationProc());
						} else {
							Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
						}
					}

				}
			});
			break;
		case ITEM_TYPE_RECOMMENDED_BROADCAST:
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_recommended, null);

			RelativeLayout containerRec = (RelativeLayout) convertView.findViewById(R.id.block_feed_recommended_main_container);
			TextView headerTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_header_tv);
			ImageView landscapeIvRec = (ImageView) convertView.findViewById(R.id.block_feed_recommended_content_iv);
			ProgressBar landscapePbRec = (ProgressBar) convertView.findViewById(R.id.block_feed_recommended_content_iv_progressbar);
			TextView titleTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_title_tv);
			TextView timeTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_time_tv);
			TextView channelTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_channel_tv);
			TextView detailsTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_details_tv);
			TextView progressbarTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_timeleft_tv);
			ProgressBar progressBarRec = (ProgressBar) convertView.findViewById(R.id.block_feed_recommended_progressbar);
			LinearLayout likeContainerRec = (LinearLayout) convertView.findViewById(R.id.block_feed_recommended_like_button_container);
			likeRecIv = (ImageView) convertView.findViewById(R.id.block_feed_recommended_like_button_iv);
			LinearLayout shareContainerRec = (LinearLayout) convertView.findViewById(R.id.block_feed_recommended_share_button_container);
			ImageView shareIvRec = (ImageView) convertView.findViewById(R.id.block_feed_recommended_share_button_iv);
			LinearLayout remindContainerRec = (LinearLayout) convertView.findViewById(R.id.block_feed_recommended_remind_button_container);
			remindRecIv = (ImageView) convertView.findViewById(R.id.block_feed_recommended_remind_button_iv);

			// mIsLiked = LikeService.isLiked(mToken, feedItem.getBroadcast().getProgram().getProgramId());

			final String programTypeRec = feedItem.getBroadcast().getProgram().getProgramType();

			// determine like
			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getSeries().getSeriesId());
			} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeRec)) {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getSportType().getSportTypeId());
			} else {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getProgramId());
			}

			headerTvRec.setText(feedItem.getTitle() + " " + feedItem.getItemType());

			mImageLoader.displayImage(feedItem.getBroadcast().getProgram().getPosterMUrl(), landscapeIvRec, ImageLoader.IMAGE_TYPE.GALLERY);

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
				titleTvRec.setText(feedItem.getBroadcast().getProgram().getSeries().getName());
			} else {
				titleTvRec.setText(feedItem.getBroadcast().getProgram().getTitle());
			}

			try {
				timeTvRec.setText(DateUtilities.isoStringToDayOfWeek(feedItem.getBroadcast().getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(feedItem.getBroadcast().getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			channelTvRec.setText(feedItem.getBroadcast().getChannel().getName());

			if (programTypeRec != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeRec)) {
					detailsTvRec.setText(feedItem.getBroadcast().getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + feedItem.getBroadcast().getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
					detailsTvRec.setText(mActivity.getResources().getString(R.string.season) + " " + feedItem.getBroadcast().getProgram().getSeason().getNumber() + " "
							+ mActivity.getResources().getString(R.string.episode) + " " + feedItem.getBroadcast().getProgram().getEpisodeNumber());
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeRec)) {
					detailsTvRec.setText(feedItem.getBroadcast().getProgram().getSportType().getName() + " " + feedItem.getBroadcast().getProgram().getTournament());
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeRec)) {
					detailsTvRec.setText(feedItem.getBroadcast().getProgram().getCategory());
				}
			}

			int durationRec = 0;
			// MC - Calculate the duration of the program and set up ProgressBar.
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getEndTime());
				durationRec = (int) (startTime - endTime) / (1000 * 60);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			progressBarRec.setMax(durationRec);

			// MC - Calculate the current progress of the ProgressBar and update.
			int initialProgressRec = 0;
			long differenceRec = 0;
			try {
				differenceRec = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (differenceRec < 0) {
				progressBarRec.setVisibility(View.GONE);
				initialProgress = 0;
				progressBarRec.setProgress(0);
			} else {
				try {
					initialProgress = (int) DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime()) / (1000 * 60);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				progressbarTvRec.setText(durationRec - initialProgressRec + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
				progressBarRec.setProgress(initialProgressRec);
				progressBarRec.setVisibility(View.VISIBLE);
				progressBarRec.setVisibility(View.VISIBLE);
			}

			containerRec.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(feedItem.getBroadcast().getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, feedItem.getBroadcast().getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, feedItem.getBroadcast().getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

				}
			});

			NotificationDbItem dbItemBroadcast = new NotificationDbItem();
			dbItemBroadcast = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
			if (dbItemBroadcast.getNotificationId() != 0) {
				mIsSet = true;
			} else {
				mIsSet = false;
			}

			if (mIsSet) remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
			else remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));

			if (mIsLiked) likeRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart_red));
			else likeRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart));

			likeContainerRec.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String likeType = LikeService.getLikeType(programTypeRec);

					String programId, contentTitle;
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
						programId = feedItem.getBroadcast().getProgram().getSeries().getSeriesId();
						contentTitle = feedItem.getBroadcast().getProgram().getTitle();
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeRec)) {
						programId = feedItem.getBroadcast().getProgram().getSportType().getSportTypeId();
						contentTitle = feedItem.getBroadcast().getProgram().getSportType().getName();
					} else {
						programId = feedItem.getBroadcast().getProgram().getProgramId();
						contentTitle = feedItem.getBroadcast().getProgram().getTitle();
					}

					if (mIsLiked == false) {
						if (LikeService.addLike(mToken, programId, likeType)) {
							DazooStore.getInstance().addLikeIdToList(programId);
							LikeService.showSetLikeToast(mActivity, contentTitle);
							likeRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart_red));

							AnimationUtilities.animationSet(likeRecIv);

							mIsLiked = true;
						} else {
							Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						LikeService.removeLike(mToken, likeType, programId);
						DazooStore.getInstance().deleteLikeIdFromList(programId);
						mIsLiked = false;
						likeRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_heart));

					}
				}
			});

			shareContainerRec.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), feedItem.getBroadcast().getShareUrl(),
							mActivity.getResources().getString(R.string.share_action_title));
				}
			});

			remindContainerRec.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(feedItem.getBroadcast().getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					if (mIsSet == false) {
						if (NotificationService.setAlarm(mActivity, feedItem.getBroadcast(), feedItem.getBroadcast().getChannel(), tvDate)) {
							NotificationService.showSetNotificationToast(mActivity);
							remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));

							NotificationDbItem dbItem = new NotificationDbItem();
							dbItem = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
							mNotificationId = dbItem.getNotificationId();
							AnimationUtilities.animationSet(remindRecIv);
							mIsSet = true;
						} else {
							Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						if (mNotificationId != -1) {
							NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
							notificationDlg.showRemoveNotificationDialog(mActivity, feedItem.getBroadcast(), mNotificationId, yesNotificationRecProc(), noNotificationRecProc());
						} else {
							Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			break;
		case ITEM_TYPE_POPULAR_BROADCASTS:

			ArrayList<Broadcast> broadcasts = feedItem.getBroadcasts();

			convertView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_popular, null);

			// one
			TextView header = (TextView) convertView.findViewById(R.id.block_popular_header_tv);
			LinearLayout mContainerOne = (LinearLayout) convertView.findViewById(R.id.block_popular_feed_container_one);
			ImageView mPosterOne = (ImageView) convertView.findViewById(R.id.block_feed_popular_listitem_one_iv);
			ProgressBar mImageProgressBarOne = (ProgressBar) convertView.findViewById(R.id.block_feed_popular_listitem_iv_one_progressbar);
			TextView mTitleOne = (TextView) convertView.findViewById(R.id.block_popular_feed_details_title_one_tv);
			TextView mTimeOne = (TextView) convertView.findViewById(R.id.block_popular_feed_details_time_one_tv);
			TextView mChannelNameOne = (TextView) convertView.findViewById(R.id.block_popular_feed_details_channel_one_tv);
			TextView mDetailsOne = (TextView) convertView.findViewById(R.id.block_popular_feed_details_extra_one_tv);
			TextView mProgressBarTitleOne = (TextView) convertView.findViewById(R.id.block_popular_feed_timeleft_one_tv);
			ProgressBar mProgressBarOne = (ProgressBar) convertView.findViewById(R.id.block_popular_feed_one_progressbar);

			if (broadcasts.get(0) != null) {

				final Broadcast broadcastOne = broadcasts.get(0);

				String programTypeOne = broadcastOne.getProgram().getProgramType();

				mImageLoader.displayImage(broadcastOne.getProgram().getPosterLUrl(), mPosterOne, ImageLoader.IMAGE_TYPE.THUMBNAIL);
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeOne)) {
					mTitleOne.setText(broadcastOne.getProgram().getSeries().getName());
				} else {
					mTitleOne.setText(broadcastOne.getProgram().getTitle());
				}
				try {
					mTimeOne.setText(DateUtilities.isoStringToDayOfWeek(broadcastOne.getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastOne.getBeginTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mChannelNameOne.setText(broadcastOne.getChannel().getName());

				if (programTypeOne != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeOne)) {
						mDetailsOne.setText(broadcastOne.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcastOne.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeOne)) {
						mDetailsOne.setText(mActivity.getResources().getString(R.string.season) + " " + broadcastOne.getProgram().getSeason().getNumber() + " "
								+ mActivity.getResources().getString(R.string.episode) + " " + broadcastOne.getProgram().getEpisodeNumber());
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeOne)) {
						mDetailsOne.setText(broadcastOne.getProgram().getSportType().getName() + " " + broadcastOne.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeOne)) {
						mDetailsOne.setText(broadcastOne.getProgram().getCategory());
					}
				}

				int durationOne = 0;
				// MC - Calculate the duration of the program and set up ProgressBar.
				try {
					long startTime = DateUtilities.getAbsoluteTimeDifference(broadcastOne.getBeginTime());
					long endTime = DateUtilities.getAbsoluteTimeDifference(broadcastOne.getEndTime());
					durationOne = (int) (startTime - endTime) / (1000 * 60);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mProgressBarOne.setMax(durationOne);

				// MC - Calculate the current progress of the ProgressBar and update.
				int initialProgressOne = 0;
				long differenceOne = 0;
				try {
					differenceOne = DateUtilities.getAbsoluteTimeDifference(broadcastOne.getBeginTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (differenceOne < 0) {
					mProgressBarOne.setVisibility(View.GONE);
					initialProgressOne = 0;
					mProgressBarOne.setProgress(0);
				} else {
					try {
						initialProgressOne = (int) DateUtilities.getAbsoluteTimeDifference(broadcastOne.getBeginTime()) / (1000 * 60);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					mProgressBarTitleOne.setText(durationOne - initialProgressOne + " " + mActivity.getResources().getString(R.string.minutes) + " "
							+ mActivity.getResources().getString(R.string.left));
					mProgressBarOne.setProgress(initialProgressOne);
					mProgressBarOne.setVisibility(View.VISIBLE);
					mProgressBarTitleOne.setVisibility(View.VISIBLE);
				}

				mContainerOne.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String tvDate = "";
						try {
							tvDate = DateUtilities.isoDateStringToTvDateString(broadcastOne.getBeginTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}

						Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
						intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastOne.getBeginTimeMillis());
						intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastOne.getChannel().getChannelId());
						intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);
						intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

						mActivity.startActivity(intent);
						mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
					}
				});
			}

			// two
			LinearLayout mContainerTwo = (LinearLayout) convertView.findViewById(R.id.block_popular_feed_container_two);
			ImageView mPosterTwo = (ImageView) convertView.findViewById(R.id.block_feed_popular_listitem_two_iv);
			ProgressBar mImageProgressBarTwo = (ProgressBar) convertView.findViewById(R.id.block_feed_popular_listitem_iv_two_progressbar);
			TextView mTitleTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_details_title_two_tv);
			TextView mTimeTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_details_time_two_tv);
			TextView mChannelNameTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_details_channel_two_tv);
			TextView mDetailsTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_details_extra_two_tv);
			TextView mProgressBarTitleTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_timeleft_two_tv);
			ProgressBar mProgressBarTwo = (ProgressBar) convertView.findViewById(R.id.block_popular_feed_two_progressbar);

			if (broadcasts.get(1) != null) {
				mContainerTwo.setVisibility(View.VISIBLE);

				final Broadcast broadcastTwo = broadcasts.get(1);

				String programTypeTwo = broadcastTwo.getProgram().getProgramType();

				mImageLoader.displayImage(broadcastTwo.getProgram().getPosterLUrl(), mPosterTwo, ImageLoader.IMAGE_TYPE.THUMBNAIL);
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTwo)) {
					mTitleTwo.setText(broadcastTwo.getProgram().getSeries().getName());
				} else {
					mTitleTwo.setText(broadcastTwo.getProgram().getTitle());
				}
				try {
					mTimeTwo.setText(DateUtilities.isoStringToDayOfWeek(broadcastTwo.getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastTwo.getBeginTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mChannelNameTwo.setText(broadcastTwo.getChannel().getName());

				if (programTypeTwo != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeTwo)) {
						mDetailsTwo.setText(broadcastTwo.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcastTwo.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTwo)) {
						mDetailsTwo.setText(mActivity.getResources().getString(R.string.season) + " " + broadcastTwo.getProgram().getSeason().getNumber() + " "
								+ mActivity.getResources().getString(R.string.episode) + " " + broadcastTwo.getProgram().getEpisodeNumber());
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeTwo)) {
						mDetailsTwo.setText(broadcastTwo.getProgram().getSportType().getName() + " " + broadcastTwo.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeTwo)) {
						mDetailsTwo.setText(broadcastTwo.getProgram().getCategory());
					}
				}

				int durationTwo = 0;
				// MC - Calculate the duration of the program and set up ProgressBar.
				try {
					long startTime = DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getBeginTime());
					long endTime = DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getEndTime());
					durationTwo = (int) (startTime - endTime) / (1000 * 60);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mProgressBarTwo.setMax(durationTwo);

				// MC - Calculate the current progress of the ProgressBar and update.
				int initialProgressTwo = 0;
				long differenceTwo = 0;
				try {
					differenceTwo = DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getBeginTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (differenceTwo < 0) {
					mProgressBarTwo.setVisibility(View.GONE);
					initialProgressTwo = 0;
					mProgressBarTwo.setProgress(0);
				} else {
					try {
						initialProgressTwo = (int) DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getBeginTime()) / (1000 * 60);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					mProgressBarTitleTwo.setText(durationTwo - initialProgressTwo + " " + mActivity.getResources().getString(R.string.minutes) + " "
							+ mActivity.getResources().getString(R.string.left));
					mProgressBarTwo.setProgress(initialProgressTwo);
					mProgressBarTwo.setVisibility(View.VISIBLE);
					mProgressBarTitleTwo.setVisibility(View.VISIBLE);
				}

				mContainerTwo.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String tvDate = "";
						try {
							tvDate = DateUtilities.isoDateStringToTvDateString(broadcastTwo.getBeginTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}

						Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
						intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastTwo.getBeginTimeMillis());
						intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastTwo.getChannel().getChannelId());
						intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);
						intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

						mActivity.startActivity(intent);
						mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
					}
				});
			}

			// three
			LinearLayout mContainerThree = (LinearLayout) convertView.findViewById(R.id.block_popular_feed_container_three);
			ImageView mPosterThree = (ImageView) convertView.findViewById(R.id.block_feed_popular_listitem_three_iv);
			ProgressBar mImageProgressBarThree = (ProgressBar) convertView.findViewById(R.id.block_feed_popular_listitem_iv_three_progressbar);
			TextView mTitleThree = (TextView) convertView.findViewById(R.id.block_popular_feed_details_title_three_tv);
			TextView mTimeThree = (TextView) convertView.findViewById(R.id.block_popular_feed_details_time_three_tv);
			TextView mChannelNameThree = (TextView) convertView.findViewById(R.id.block_popular_feed_details_channel_three_tv);
			TextView mDetailsThree = (TextView) convertView.findViewById(R.id.block_popular_feed_details_extra_three_tv);
			TextView mProgressBarTitleThree = (TextView) convertView.findViewById(R.id.block_popular_feed_timeleft_three_tv);
			ProgressBar mProgressBarThree = (ProgressBar) convertView.findViewById(R.id.block_popular_feed_three_progressbar);

			if (broadcasts.get(2) != null) {
				mContainerThree.setVisibility(View.VISIBLE);

				final Broadcast broadcastThree = broadcasts.get(2);

				String programTypeThree = broadcastThree.getProgram().getProgramType();

				mImageLoader.displayImage(broadcastThree.getProgram().getPosterLUrl(), mPosterThree, ImageLoader.IMAGE_TYPE.THUMBNAIL);
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeThree)) {
					mTitleThree.setText(broadcastThree.getProgram().getSeries().getName());
				} else {
					mTitleThree.setText(broadcastThree.getProgram().getTitle());
				}
				try {
					mTimeThree.setText(DateUtilities.isoStringToDayOfWeek(broadcastThree.getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastThree.getBeginTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mChannelNameThree.setText(broadcastThree.getChannel().getName());

				int durationThree = 0;
				// MC - Calculate the duration of the program and set up ProgressBar.
				try {
					long startTime = DateUtilities.getAbsoluteTimeDifference(broadcastThree.getBeginTime());
					long endTime = DateUtilities.getAbsoluteTimeDifference(broadcastThree.getEndTime());
					durationThree = (int) (startTime - endTime) / (1000 * 60);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mProgressBarThree.setMax(durationThree);

				// MC - Calculate the current progress of the ProgressBar and update.
				int initialProgressThree = 0;
				long differenceThree = 0;
				try {
					differenceThree = DateUtilities.getAbsoluteTimeDifference(broadcastThree.getBeginTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (differenceThree < 0) {
					mProgressBarThree.setVisibility(View.GONE);
					initialProgressThree = 0;
					mProgressBarThree.setProgress(0);
				} else {
					try {
						initialProgressThree = (int) DateUtilities.getAbsoluteTimeDifference(broadcastThree.getBeginTime()) / (1000 * 60);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					mProgressBarTitleThree.setText(durationThree - initialProgressThree + " " + mActivity.getResources().getString(R.string.minutes) + " "
							+ mActivity.getResources().getString(R.string.left));
					mProgressBarThree.setProgress(initialProgressThree);
					mProgressBarThree.setVisibility(View.VISIBLE);
					mProgressBarTitleThree.setVisibility(View.VISIBLE);
				}

				if (programTypeThree != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeThree)) {
						mDetailsThree.setText(broadcastThree.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcastThree.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeThree)) {
						mDetailsThree.setText(mActivity.getResources().getString(R.string.season) + " " + broadcastThree.getProgram().getSeason().getNumber() + " "
								+ mActivity.getResources().getString(R.string.episode) + " " + broadcastThree.getProgram().getEpisodeNumber());
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeThree)) {
						mDetailsThree.setText(broadcastThree.getProgram().getSportType().getName() + " " + broadcastThree.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeThree)) {
						mDetailsThree.setText(broadcastThree.getProgram().getCategory());
					}
				}
				mContainerThree.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String tvDate = "";
						try {
							tvDate = DateUtilities.isoDateStringToTvDateString(broadcastThree.getBeginTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}

						Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
						intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastThree.getBeginTimeMillis());
						intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcastThree.getChannel().getChannelId());
						intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, tvDate);
						intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

						mActivity.startActivity(intent);
						mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
					}
				});
			}

			RelativeLayout footer = (RelativeLayout) convertView.findViewById(R.id.block_popular_show_more_container);
			footer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, PopularPageActivity.class);
					// ADD THE URL TO THE POPULAR LIST AS AN ARGUMENT?
					mActivity.startActivity(intent);
				}
			});

			break;
		}

		return convertView;
	}

	public Runnable yesNotificationProc() {
		return new Runnable() {
			public void run() {
				remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
				mIsSet = false;
			}
		};
	}

	public Runnable yesNotificationRecProc() {
		return new Runnable() {
			public void run() {
				remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
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

	public Runnable noNotificationRecProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

}
