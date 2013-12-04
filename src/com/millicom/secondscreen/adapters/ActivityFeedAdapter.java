package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.UpcomingEpisodesListAdapter.ViewHolder;
import com.millicom.secondscreen.content.activity.ActivityLikedBlockPopulator;
import com.millicom.secondscreen.content.activity.ActivityPopularBlockPopulator;
import com.millicom.secondscreen.content.activity.ActivityRecommendedBlockPopulator;
import com.millicom.secondscreen.content.activity.PopularPageActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
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
import android.widget.AbsListView;
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

	private int						DAZOO_ACTIVITY_BLOCKS_TYPE_NUMBER	= 4;
	private static final int		ITEM_TYPE_BROADCAST					= 0;
	private static final int		ITEM_TYPE_RECOMMENDED_BROADCAST		= 1;
	private static final int		ITEM_TYPE_POPULAR_BROADCASTS		= 2;
	private static final int		ITEM_TYPE_POPULAR_TWITTER			= 3;

	private String					mToken;
	private int						mNotificationId;
	private NotificationDataSource	mNotificationDataSource;
	private ArrayList<String>		mLikeIds;

//	private ImageView				likeLikeIv, remindLikeIv, likeRecIv, remindRecIv, likeTwitterIv, remindTwitterIv;
	private boolean					mIsLiked							= false, mIsSet = false;

	public ActivityFeedAdapter(Activity activity, ArrayList<FeedItem> feedItems, String token) {
		this.mActivity = activity;
		this.mFeedItems = feedItems;
		this.mImageLoader = new ImageLoader(mActivity, R.color.white);
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
		} else if (Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_TWITTER.equals(feedItemType)) {
			return ITEM_TYPE_POPULAR_TWITTER;
		}
		return ITEM_TYPE_BROADCAST;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);

		final FeedItem feedItem = getItem(position);

		switch (type) {
		case ITEM_TYPE_POPULAR_TWITTER:
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_liked, null);
				
				PopularTwitterViewHolder viewHolder = new PopularTwitterViewHolder();
				
				viewHolder.containerTw = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_main_container);
				viewHolder.headerTvTw = (TextView) convertView.findViewById(R.id.block_feed_liked_header_tv);
				viewHolder.landscapeIvTw = (ImageView) convertView.findViewById(R.id.block_feed_liked_content_iv);
				viewHolder.landscapePbTw = (ProgressBar) convertView.findViewById(R.id.block_feed_liked_content_iv_progressbar);
				viewHolder.titleTvTw = (TextView) convertView.findViewById(R.id.block_feed_liked_title_tv);
				viewHolder.timeTvTw = (TextView) convertView.findViewById(R.id.block_feed_liked_time_tv);
				viewHolder.channelTvTw = (TextView) convertView.findViewById(R.id.block_feed_liked_channel_tv);
				viewHolder.detailsTvTw = (TextView) convertView.findViewById(R.id.block_feed_liked_details_tv);
				viewHolder.progressbarTvTw = (TextView) convertView.findViewById(R.id.block_feed_liked_timeleft_tv);
				viewHolder.progressBarTw = (ProgressBar) convertView.findViewById(R.id.block_feed_liked_progressbar);
				viewHolder.likeContainerTw = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_like_button_container);
				viewHolder.likeTwitterIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_like_button_iv);
				viewHolder.shareContainerTw = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_share_button_container);
				viewHolder.shareIvTw = (ImageView) convertView.findViewById(R.id.block_feed_liked_share_button_iv);
				viewHolder.remindContainerTw = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_remind_button_container);
				viewHolder.remindTwitterIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_remind_button_iv);
				
				convertView.setTag(viewHolder);
			}
			final PopularTwitterViewHolder holder = (PopularTwitterViewHolder) convertView.getTag();

			// mIsLiked = LikeService.isLiked(mToken, feedItem.getBroadcast().getProgram().getProgramId());

			holder.headerTvTw.setText(mActivity.getResources().getString(R.string.icon_twitter) + " " + feedItem.getTitle());

			final String programTypeTw = feedItem.getBroadcast().getProgram().getProgramType();

			// determine like
			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTw)) {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getSeries().getSeriesId());
			} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeTw)) {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getSportType().getSportTypeId());
			} else {
				mIsLiked = DazooStore.getInstance().isInTheLikesList(feedItem.getBroadcast().getProgram().getProgramId());
			}

			mImageLoader.displayImage(feedItem.getBroadcast().getProgram().getLandLUrl(), holder.landscapeIvTw, ImageLoader.IMAGE_TYPE.GALLERY);

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTw)) {
				holder.titleTvTw.setText(feedItem.getBroadcast().getProgram().getSeries().getName());
			} else {
				holder.titleTvTw.setText(feedItem.getBroadcast().getProgram().getTitle());
			}

			try {
				holder.timeTvTw.setText(DateUtilities.isoStringToDayOfWeek(feedItem.getBroadcast().getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(feedItem.getBroadcast().getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			holder.channelTvTw.setText(feedItem.getBroadcast().getChannel().getName());

			if (programTypeTw != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeTw)) {
					holder.detailsTvTw.setText(feedItem.getBroadcast().getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + feedItem.getBroadcast().getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTw)) {
					String season = feedItem.getBroadcast().getProgram().getSeason().getNumber();
					int episode = feedItem.getBroadcast().getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + feedItem.getBroadcast().getProgram().getSeason().getNumber() + " ";
					}
					if (episode != 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + String.valueOf(feedItem.getBroadcast().getProgram().getEpisodeNumber());
					}
					holder.detailsTvTw.setText(seasonEpisode);
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeTw)) {
					holder.detailsTvTw.setText(feedItem.getBroadcast().getProgram().getSportType().getName() + " " + feedItem.getBroadcast().getProgram().getTournament());
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeTw)) {
					holder.detailsTvTw.setText(feedItem.getBroadcast().getProgram().getCategory());
				}
			}

			holder.containerTw.setOnClickListener(new View.OnClickListener() {

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

			int durationTw = 0;		
			long timeSinceBegin = 0;
			long timeToEnd = 0;
			// MC - Calculate the duration of the program and set up ProgressBar.
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getEndTime());
				timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				timeToEnd = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getEndTime());
				durationTw = (int) (startTime - endTime) / (1000 * 60);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (timeSinceBegin > 0 && timeToEnd < 0) {
				holder.progressBarTw.setMax(durationTw);
	
				// MC - Calculate the current progress of the ProgressBar and update.
				int initialProgressTw = 0;
				long differenceTw = 0;
				try {
					differenceTw = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
	
				if (differenceTw < 0) {
					holder.progressBarTw.setVisibility(View.GONE);
					initialProgressTw = 0;
					holder.progressBarTw.setProgress(0);
				} else {
					try {
						initialProgressTw = (int) DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime()) / (1000 * 60);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					holder.progressbarTvTw.setText(durationTw - initialProgressTw + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
					holder.progressBarTw.setProgress(initialProgressTw);
					holder.progressbarTvTw.setVisibility(View.VISIBLE);
					holder.progressBarTw.setVisibility(View.VISIBLE);
				}
			}
			else {
				holder.progressbarTvTw.setVisibility(View.GONE);
				holder.progressBarTw.setVisibility(View.GONE);
			}

			NotificationDbItem dbItemTw = new NotificationDbItem();
			dbItemTw = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
			if (dbItemTw.getNotificationId() != 0) {
				mIsSet = true;
				mNotificationId =  dbItemTw.getNotificationId();
			} else {
				mIsSet = false;
			}

			if (mIsSet) holder.remindTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			else holder.remindTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));

			if (mIsLiked) holder.likeTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
			else holder.likeTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

			holder.likeContainerTw.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String likeType = LikeService.getLikeType(programTypeTw);

					String programId, contentTitle;
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTw)) {
						programId = feedItem.getBroadcast().getProgram().getSeries().getSeriesId();
						contentTitle = feedItem.getBroadcast().getProgram().getTitle();
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeTw)) {
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
							holder.likeTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));

							AnimationUtilities.animationSet(holder.likeTwitterIv);

							mIsLiked = true;
						} else {
							Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						LikeService.removeLike(mToken, likeType, programId);
						DazooStore.getInstance().deleteLikeIdFromList(programId);

						mIsLiked = false;
						holder.likeTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

					}
				}
			});

			holder.shareContainerTw.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), feedItem.getBroadcast().getShareUrl(),
							mActivity.getResources().getString(R.string.share_action_title));
				}
			});

			holder.remindContainerTw.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(feedItem.getBroadcast().getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					NotificationDbItem item = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
					if (item.getNotificationId() != 0) {
						mIsSet = true;
						mNotificationId = item.getNotificationId();
					} else {
						mIsSet = false;
					}
					
					Log.d(TAG,"Twitter: " + mIsSet);
					
					if (mIsSet == false) {
						if (NotificationService.setAlarm(mActivity, feedItem.getBroadcast(), feedItem.getBroadcast().getChannel(), tvDate)) {
							NotificationService.showSetNotificationToast(mActivity);
							holder.remindTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

							NotificationDbItem dbItemTw = new NotificationDbItem();
							Log.d(TAG, "feedItem.getBroadcast().getChannel().getChannelId()" + feedItem.getBroadcast().getChannel().getChannelId());
							Log.d(TAG, "feedItem.getBroadcast().getBeginTimeMillis()" + feedItem.getBroadcast().getBeginTimeMillis());

							dbItemTw = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());

							mNotificationId = dbItemTw.getNotificationId();

							AnimationUtilities.animationSet(holder.remindTwitterIv);

							mIsSet = true;
						} else {
							Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						
						
						if (mNotificationId != -1) {
							Log.d(TAG,"mNotificationId: " + mNotificationId);
							NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
							notificationDlg.showRemoveNotificationDialog(mActivity, feedItem.getBroadcast(), mNotificationId, yesNotificationTwitterProc(holder.remindTwitterIv), noNotificationProc());
						} else {
							Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
						}
					}

				}
			});

			break;

		case ITEM_TYPE_BROADCAST:
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_liked, null);
		
				BroadcastViewHolder viewHolder = new BroadcastViewHolder();
				viewHolder.container = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_main_container);
				viewHolder.headerTv = (TextView) convertView.findViewById(R.id.block_feed_liked_header_tv);
				viewHolder.landscapeIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_content_iv);
				viewHolder.landscapePb = (ProgressBar) convertView.findViewById(R.id.block_feed_liked_content_iv_progressbar);
				viewHolder.titleTv = (TextView) convertView.findViewById(R.id.block_feed_liked_title_tv);
				viewHolder.timeTv = (TextView) convertView.findViewById(R.id.block_feed_liked_time_tv);
				viewHolder.channelTv = (TextView) convertView.findViewById(R.id.block_feed_liked_channel_tv);
				viewHolder.detailsTv = (TextView) convertView.findViewById(R.id.block_feed_liked_details_tv);
				viewHolder.progressbarTv = (TextView) convertView.findViewById(R.id.block_feed_liked_timeleft_tv);
				viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.block_feed_liked_progressbar);
				viewHolder.likeContainer = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_like_button_container);
				viewHolder.likeLikeIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_like_button_iv);
				viewHolder.shareContainer = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_share_button_container);
				viewHolder.shareIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_share_button_iv);
				viewHolder.remindContainer = (LinearLayout) convertView.findViewById(R.id.block_feed_liked_remind_button_container);
				viewHolder.remindLikeIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_remind_button_iv);
				
				convertView.setTag(viewHolder);
			}
			final BroadcastViewHolder holderBC = (BroadcastViewHolder) convertView.getTag();
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

			holderBC.headerTv.setText(feedItem.getTitle());

			mImageLoader.displayImage(feedItem.getBroadcast().getProgram().getLandLUrl(), holderBC.landscapeIv, ImageLoader.IMAGE_TYPE.GALLERY);

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				holderBC.titleTv.setText(feedItem.getBroadcast().getProgram().getSeries().getName());
			} else {
				holderBC.titleTv.setText(feedItem.getBroadcast().getProgram().getTitle());
			}

			try {
				holderBC.timeTv.setText(DateUtilities.isoStringToDayOfWeek(feedItem.getBroadcast().getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(feedItem.getBroadcast().getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			holderBC.channelTv.setText(feedItem.getBroadcast().getChannel().getName());

			if (programType != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					holderBC.detailsTv.setText(feedItem.getBroadcast().getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + feedItem.getBroadcast().getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					String season = feedItem.getBroadcast().getProgram().getSeason().getNumber();
					int episode = feedItem.getBroadcast().getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + feedItem.getBroadcast().getProgram().getSeason().getNumber() + " ";
					}
					if (episode != 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + String.valueOf(feedItem.getBroadcast().getProgram().getEpisodeNumber());
					}
					holderBC.detailsTv.setText(seasonEpisode);
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
					holderBC.detailsTv.setText(feedItem.getBroadcast().getProgram().getSportType().getName() + " " + feedItem.getBroadcast().getProgram().getTournament());
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
					holderBC.detailsTv.setText(feedItem.getBroadcast().getProgram().getCategory());
				}
			}

			holderBC.container.setOnClickListener(new View.OnClickListener() {

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
			timeSinceBegin = 0;
			timeToEnd = 0;
			// MC - Calculate the duration of the program and set up ProgressBar.
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getEndTime());
				timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				timeToEnd = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getEndTime());
				duration = (int) (startTime - endTime) / (1000 * 60);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (timeSinceBegin > 0 && timeToEnd < 0) {
				holderBC.progressBar.setMax(duration);
	
				// MC - Calculate the current progress of the ProgressBar and update.
				int initialProgress = 0;
				long difference = 0;
				try {
					difference = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
	
				if (difference < 0) {
					holderBC.progressBar.setVisibility(View.GONE);
					initialProgress = 0;
					holderBC.progressBar.setProgress(0);
				} else {
					try {
						initialProgress = (int) DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime()) / (1000 * 60);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					holderBC.progressbarTv.setText(duration - initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
					holderBC.progressBar.setProgress(initialProgress);
					holderBC.progressbarTv.setVisibility(View.VISIBLE);
					holderBC.progressBar.setVisibility(View.VISIBLE);
				}
			}
			else {
				holderBC.progressbarTv.setVisibility(View.GONE);
				holderBC.progressBar.setVisibility(View.GONE);
			}

			NotificationDbItem dbItem = new NotificationDbItem();
			dbItem = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
			Log.d(TAG,"uP: " + feedItem.getBroadcast().getChannel().getChannelId() + " " + feedItem.getBroadcast().getBeginTimeMillis());
			if (dbItem.getNotificationId() != 0) {
				Log.d(TAG,"dbItem: " + dbItem.getProgramTitle() + " " + dbItem.getNotificationId() );
				mNotificationId = dbItem.getNotificationId();
				mIsSet = true;
			} else {
				mIsSet = false;
			}

			Log.d(TAG,"lIKE UP IS SET: " + mIsSet);
			
			if (mIsSet) holderBC.remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			else holderBC.remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));

			if (mIsLiked) holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
			else holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

			holderBC.likeContainer.setOnClickListener(new View.OnClickListener() {

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
						if (LikeService.addLike(mToken, programId, likeType)) {
							DazooStore.getInstance().addLikeIdToList(programId);

							LikeService.showSetLikeToast(mActivity, contentTitle);
							holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));

							AnimationUtilities.animationSet(holderBC.likeLikeIv);

							mIsLiked = true;
						} else {
							Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						LikeService.removeLike(mToken, likeType, programId);
						DazooStore.getInstance().deleteLikeIdFromList(programId);

						mIsLiked = false;
						holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

					}
				}
			});

			holderBC.shareContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), feedItem.getBroadcast().getShareUrl(),
							mActivity.getResources().getString(R.string.share_action_title));
				}
			});

			holderBC.remindContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					NotificationDbItem dbItem = new NotificationDbItem();
					
					
					dbItem = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
					Log.d(TAG,"DOWN: " + feedItem.getBroadcast().getChannel().getChannelId() + " " + feedItem.getBroadcast().getBeginTimeMillis());
					
					if (dbItem.getNotificationId() != 0) {
						Log.d(TAG,"dbItem: " + dbItem.getProgramTitle() + " " + dbItem.getNotificationId() );
						mNotificationId = dbItem.getNotificationId();
						mIsSet = true;
					} else {
						mIsSet = false;
					}
					
					Log.d(TAG,"lIKED REMIND: " + mIsSet);
					
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(feedItem.getBroadcast().getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}

					if (mIsSet == false) {
						if (NotificationService.setAlarm(mActivity, feedItem.getBroadcast(), feedItem.getBroadcast().getChannel(), tvDate)) {
							NotificationService.showSetNotificationToast(mActivity);
							holderBC.remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

							NotificationDbItem dbItemRemind= new NotificationDbItem();
							Log.d(TAG, "feedItem.getBroadcast().getChannel().getChannelId()" + feedItem.getBroadcast().getChannel().getChannelId());
							Log.d(TAG, "feedItem.getBroadcast().getBeginTimeMillis()" + feedItem.getBroadcast().getBeginTimeMillis());

							dbItemRemind = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
							Log.d(TAG,"db Item: " + dbItemRemind.getNotificationId() + " " + dbItemRemind.getBroadcastTimeInMillis() + " " + dbItemRemind.getChannelId()
									+ " " + dbItemRemind.getProgramTitle() );
							
							
							mNotificationId = dbItemRemind.getNotificationId();

							AnimationUtilities.animationSet(holderBC.remindLikeIv);

							mIsSet = true;
						} else {
							Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						if (mNotificationId != -1) {
							NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
							notificationDlg.showRemoveNotificationDialog(mActivity, feedItem.getBroadcast(), mNotificationId, yesNotificationProc(holderBC.remindLikeIv), noNotificationProc());
						} else {
							Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
						}
					}

				}
			});
			break;
		case ITEM_TYPE_RECOMMENDED_BROADCAST:
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_recommended, null);
	
				RecommendedBroadcastViewHolder viewHolder = new RecommendedBroadcastViewHolder();
				
				viewHolder.containerRec = (RelativeLayout) convertView.findViewById(R.id.block_feed_recommended_main_container);
				viewHolder.headerTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_header_tv);
				viewHolder.landscapeIvRec = (ImageView) convertView.findViewById(R.id.block_feed_recommended_content_iv);
				viewHolder.landscapePbRec = (ProgressBar) convertView.findViewById(R.id.block_feed_recommended_content_iv_progressbar);
				viewHolder.titleTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_title_tv);
				viewHolder.timeTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_time_tv);
				viewHolder.channelTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_channel_tv);
				viewHolder.detailsTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_details_tv);
				viewHolder.progressbarTvRec = (TextView) convertView.findViewById(R.id.block_feed_recommended_timeleft_tv);
				viewHolder.progressBarRec = (ProgressBar) convertView.findViewById(R.id.block_feed_recommended_progressbar);
				viewHolder.likeContainerRec = (LinearLayout) convertView.findViewById(R.id.block_feed_recommended_like_button_container);
				viewHolder.likeRecIv = (ImageView) convertView.findViewById(R.id.block_feed_recommended_like_button_iv);
				viewHolder.shareContainerRec = (LinearLayout) convertView.findViewById(R.id.block_feed_recommended_share_button_container);
				viewHolder.shareIvRec = (ImageView) convertView.findViewById(R.id.block_feed_recommended_share_button_iv);
				viewHolder.remindContainerRec = (LinearLayout) convertView.findViewById(R.id.block_feed_recommended_remind_button_container);
				viewHolder.remindRecIv = (ImageView) convertView.findViewById(R.id.block_feed_recommended_remind_button_iv);
				
				convertView.setTag(viewHolder);
			}
			final RecommendedBroadcastViewHolder holderRBC = (RecommendedBroadcastViewHolder) convertView.getTag();
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

			holderRBC.headerTvRec.setText(feedItem.getTitle());

			mImageLoader.displayImage(feedItem.getBroadcast().getProgram().getLandLUrl(), holderRBC.landscapeIvRec, ImageLoader.IMAGE_TYPE.GALLERY);

			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
				holderRBC.titleTvRec.setText(feedItem.getBroadcast().getProgram().getSeries().getName());
			} else {
				holderRBC.titleTvRec.setText(feedItem.getBroadcast().getProgram().getTitle());
			}

			try {
				holderRBC.timeTvRec.setText(DateUtilities.isoStringToDayOfWeek(feedItem.getBroadcast().getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(feedItem.getBroadcast().getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			holderRBC.channelTvRec.setText(feedItem.getBroadcast().getChannel().getName());

			if (programTypeRec != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeRec)) {
					holderRBC.detailsTvRec.setText(feedItem.getBroadcast().getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + feedItem.getBroadcast().getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
					String season = feedItem.getBroadcast().getProgram().getSeason().getNumber();
					int episode = feedItem.getBroadcast().getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + feedItem.getBroadcast().getProgram().getSeason().getNumber() + " ";
					}
					if (episode != 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + String.valueOf(feedItem.getBroadcast().getProgram().getEpisodeNumber());
					}
					holderRBC.detailsTvRec.setText(seasonEpisode);
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeRec)) {
					holderRBC.detailsTvRec.setText(feedItem.getBroadcast().getProgram().getSportType().getName() + " " + feedItem.getBroadcast().getProgram().getTournament());
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeRec)) {
					holderRBC.detailsTvRec.setText(feedItem.getBroadcast().getProgram().getCategory());
				}
			}

			int durationRec = 0;
			timeSinceBegin = 0;
			timeToEnd = 0;
			// MC - Calculate the duration of the program and set up ProgressBar.
			try {
				long startTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				long endTime = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getEndTime());
				timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				timeToEnd = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getEndTime());
				durationRec = (int) (startTime - endTime) / (1000 * 60);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (timeSinceBegin > 0 && timeToEnd < 0) {
				holderRBC.progressBarRec.setMax(durationRec);
	
				// MC - Calculate the current progress of the ProgressBar and update.
				int initialProgressRec = 0;
				long differenceRec = 0;
				try {
					differenceRec = DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
	
				if (differenceRec < 0) {
					holderRBC.progressBarRec.setVisibility(View.GONE);
					initialProgressRec = 0;
					holderRBC.progressBarRec.setProgress(0);
				} else {
					try {
						initialProgressRec = (int) DateUtilities.getAbsoluteTimeDifference(feedItem.getBroadcast().getBeginTime()) / (1000 * 60);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					holderRBC.progressbarTvRec.setText(durationRec - initialProgressRec + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
					holderRBC.progressBarRec.setProgress(initialProgressRec);
					holderRBC.progressBarRec.setVisibility(View.VISIBLE);
					holderRBC.progressBarRec.setVisibility(View.VISIBLE);
				}
			}
			else {
				holderRBC.progressBarRec.setVisibility(View.GONE);
				holderRBC.progressBarRec.setVisibility(View.GONE);
			}

			holderRBC.containerRec.setOnClickListener(new View.OnClickListener() {

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
				mNotificationId = dbItemBroadcast.getNotificationId(); 
				Log.d(TAG,"Recommended: " + mIsSet + " " + mNotificationId);
			} else {
				mIsSet = false;
				Log.d(TAG,"Recommended: " + mIsSet);
			}

			if (mIsSet) holderRBC.remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			else holderRBC.remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));

			if (mIsLiked) holderRBC.likeRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
			else holderRBC.likeRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

			holderRBC.likeContainerRec.setOnClickListener(new View.OnClickListener() {

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
							holderRBC.likeRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));

							AnimationUtilities.animationSet(holderRBC.likeRecIv);

							mIsLiked = true;
						} else {
							Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						LikeService.removeLike(mToken, likeType, programId);
						DazooStore.getInstance().deleteLikeIdFromList(programId);
						mIsLiked = false;
						holderRBC.likeRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

					}
				}
			});

			holderRBC.shareContainerRec.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), feedItem.getBroadcast().getShareUrl(),
							mActivity.getResources().getString(R.string.share_action_title));
				}
			});

			holderRBC.remindContainerRec.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String tvDate = "";
					try {
						tvDate = DateUtilities.isoDateStringToTvDateString(feedItem.getBroadcast().getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					NotificationDbItem dbItemBroadcast = new NotificationDbItem();
					dbItemBroadcast = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
					if (dbItemBroadcast.getNotificationId() != 0) {
						mIsSet = true;
						mNotificationId = dbItemBroadcast.getNotificationId(); 
						Log.d(TAG,"Recommended down: " + mIsSet + " " + mNotificationId);
					} else {
						mIsSet = false;
						Log.d(TAG,"Recommended down: " + mIsSet);
					}

					if (mIsSet == false) {
						if (NotificationService.setAlarm(mActivity, feedItem.getBroadcast(), feedItem.getBroadcast().getChannel(), tvDate)) {
							NotificationService.showSetNotificationToast(mActivity);
							holderRBC.remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

							NotificationDbItem dbItem = new NotificationDbItem();
							dbItem = mNotificationDataSource.getNotification(feedItem.getBroadcast().getChannel().getChannelId(), feedItem.getBroadcast().getBeginTimeMillis());
							mNotificationId = dbItem.getNotificationId();
							AnimationUtilities.animationSet(holderRBC.remindRecIv);
							mIsSet = true;
						} else {
							Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						if (mNotificationId != -1) {
							NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
							notificationDlg.showRemoveNotificationDialog(mActivity, feedItem.getBroadcast(), mNotificationId, yesNotificationRecProc(holderRBC.remindRecIv), noNotificationProc());
						} else {
							Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			break;
		case ITEM_TYPE_POPULAR_BROADCASTS:

			ArrayList<Broadcast> broadcasts = feedItem.getBroadcasts();
			
			PopularBroadcastsViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_popular, null);
	
				viewHolder = new PopularBroadcastsViewHolder();
				
				// one
				viewHolder.header = (TextView) convertView.findViewById(R.id.block_popular_header_tv);
				viewHolder.mContainerOne = (LinearLayout) convertView.findViewById(R.id.block_popular_feed_container_one);
				viewHolder.mPosterOne = (ImageView) convertView.findViewById(R.id.block_feed_popular_listitem_one_iv);
				viewHolder.mImageProgressBarOne = (ProgressBar) convertView.findViewById(R.id.block_feed_popular_listitem_iv_one_progressbar);
				viewHolder.mTitleOne = (TextView) convertView.findViewById(R.id.block_popular_feed_details_title_one_tv);
				viewHolder.mTimeOne = (TextView) convertView.findViewById(R.id.block_popular_feed_details_time_one_tv);
				viewHolder.mChannelNameOne = (TextView) convertView.findViewById(R.id.block_popular_feed_details_channel_one_tv);
				viewHolder.mDetailsOne = (TextView) convertView.findViewById(R.id.block_popular_feed_details_extra_one_tv);
				viewHolder.mProgressBarTitleOne = (TextView) convertView.findViewById(R.id.block_popular_feed_timeleft_one_tv);
				viewHolder.mProgressBarOne = (ProgressBar) convertView.findViewById(R.id.block_popular_feed_one_progressbar);
				
				viewHolder.mContainerTwo = (LinearLayout) convertView.findViewById(R.id.block_popular_feed_container_two);
				viewHolder.mPosterTwo = (ImageView) convertView.findViewById(R.id.block_feed_popular_listitem_two_iv);
				viewHolder.mImageProgressBarTwo = (ProgressBar) convertView.findViewById(R.id.block_feed_popular_listitem_iv_two_progressbar);
				viewHolder.mTitleTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_details_title_two_tv);
				viewHolder.mTimeTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_details_time_two_tv);
				viewHolder.mChannelNameTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_details_channel_two_tv);
				viewHolder.mDetailsTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_details_extra_two_tv);
				viewHolder.mProgressBarTitleTwo = (TextView) convertView.findViewById(R.id.block_popular_feed_timeleft_two_tv);
				viewHolder.mProgressBarTwo = (ProgressBar) convertView.findViewById(R.id.block_popular_feed_two_progressbar);
				
				viewHolder.mContainerThree = (LinearLayout) convertView.findViewById(R.id.block_popular_feed_container_three);
				viewHolder.mPosterThree = (ImageView) convertView.findViewById(R.id.block_feed_popular_listitem_three_iv);
				viewHolder.mImageProgressBarThree = (ProgressBar) convertView.findViewById(R.id.block_feed_popular_listitem_iv_three_progressbar);
				viewHolder.mTitleThree = (TextView) convertView.findViewById(R.id.block_popular_feed_details_title_three_tv);
				viewHolder.mTimeThree = (TextView) convertView.findViewById(R.id.block_popular_feed_details_time_three_tv);
				viewHolder.mChannelNameThree = (TextView) convertView.findViewById(R.id.block_popular_feed_details_channel_three_tv);
				viewHolder.mDetailsThree = (TextView) convertView.findViewById(R.id.block_popular_feed_details_extra_three_tv);
				viewHolder.mProgressBarTitleThree = (TextView) convertView.findViewById(R.id.block_popular_feed_timeleft_three_tv);
				viewHolder.mProgressBarThree = (ProgressBar) convertView.findViewById(R.id.block_popular_feed_three_progressbar);
				
				convertView.setTag(viewHolder);

			}
			final PopularBroadcastsViewHolder holderPBC = (PopularBroadcastsViewHolder) convertView.getTag();


			if (broadcasts.get(0) != null) {

				final Broadcast broadcastOne = broadcasts.get(0);

				String programTypeOne = broadcastOne.getProgram().getProgramType();

				mImageLoader.displayImage(broadcastOne.getProgram().getPortMUrl(), holderPBC.mPosterOne, ImageLoader.IMAGE_TYPE.THUMBNAIL);
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeOne)) {
					holderPBC.mTitleOne.setText(broadcastOne.getProgram().getSeries().getName());
				} else {
					holderPBC.mTitleOne.setText(broadcastOne.getProgram().getTitle());
				}
				try {
					holderPBC.mTimeOne.setText(DateUtilities.isoStringToDayOfWeek(broadcastOne.getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastOne.getBeginTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				holderPBC.mChannelNameOne.setText(broadcastOne.getChannel().getName());

				if (programTypeOne != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeOne)) {
						holderPBC.mDetailsOne.setText(broadcastOne.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcastOne.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeOne)) {
						String season = broadcastOne.getProgram().getSeason().getNumber();
						int episode = broadcastOne.getProgram().getEpisodeNumber();
						String seasonEpisode = "";
						if (!season.equals("0")) {
							seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + broadcastOne.getProgram().getSeason().getNumber() + " ";
						}
						if (episode != 0) {
							seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + String.valueOf(broadcastOne.getProgram().getEpisodeNumber());
						}
						holderPBC.mDetailsOne.setText(seasonEpisode);
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeOne)) {
						holderPBC.mDetailsOne.setText(broadcastOne.getProgram().getSportType().getName() + " " + broadcastOne.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeOne)) {
						holderPBC.mDetailsOne.setText(broadcastOne.getProgram().getCategory());
					}
				}

				int durationOne = 0;
				timeSinceBegin = 0;
				timeToEnd = 0;
				// MC - Calculate the duration of the program and set up ProgressBar.
				try {
					long startTime = DateUtilities.getAbsoluteTimeDifference(broadcastOne.getBeginTime());
					long endTime = DateUtilities.getAbsoluteTimeDifference(broadcastOne.getEndTime());
					timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(broadcastOne.getBeginTime());
					timeToEnd = DateUtilities.getAbsoluteTimeDifference(broadcastOne.getEndTime());
					durationOne = (int) (startTime - endTime) / (1000 * 60);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (timeSinceBegin > 0 && timeToEnd < 0) {
					holderPBC.mProgressBarOne.setMax(durationOne);
	
					// MC - Calculate the current progress of the ProgressBar and update.
					int initialProgressOne = 0;
					long differenceOne = 0;
					try {
						differenceOne = DateUtilities.getAbsoluteTimeDifference(broadcastOne.getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
	
					if (differenceOne < 0) {
						holderPBC.mProgressBarOne.setVisibility(View.GONE);
						initialProgressOne = 0;
						holderPBC.mProgressBarOne.setProgress(0);
					} else {
						try {
							initialProgressOne = (int) DateUtilities.getAbsoluteTimeDifference(broadcastOne.getBeginTime()) / (1000 * 60);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						holderPBC.mProgressBarTitleOne.setText(durationOne - initialProgressOne + " " + mActivity.getResources().getString(R.string.minutes) + " "
								+ mActivity.getResources().getString(R.string.left));
						holderPBC.mProgressBarOne.setProgress(initialProgressOne);
						holderPBC.mProgressBarOne.setVisibility(View.VISIBLE);
						holderPBC.mProgressBarTitleOne.setVisibility(View.VISIBLE);
					}
				}
				else {
					holderPBC.mProgressBarOne.setVisibility(View.GONE);
					holderPBC.mProgressBarTitleOne.setVisibility(View.GONE);
				}

				holderPBC.mContainerOne.setOnClickListener(new View.OnClickListener() {

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

			if (broadcasts.get(1) != null) {
				holderPBC.mContainerTwo.setVisibility(View.VISIBLE);

				final Broadcast broadcastTwo = broadcasts.get(1);

				String programTypeTwo = broadcastTwo.getProgram().getProgramType();

				mImageLoader.displayImage(broadcastTwo.getProgram().getPortMUrl(), holderPBC.mPosterTwo, ImageLoader.IMAGE_TYPE.THUMBNAIL);
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTwo)) {
					holderPBC.mTitleTwo.setText(broadcastTwo.getProgram().getSeries().getName());
				} else {
					holderPBC.mTitleTwo.setText(broadcastTwo.getProgram().getTitle());
				}
				try {
					holderPBC.mTimeTwo.setText(DateUtilities.isoStringToDayOfWeek(broadcastTwo.getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastTwo.getBeginTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				holderPBC.mChannelNameTwo.setText(broadcastTwo.getChannel().getName());

				if (programTypeTwo != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeTwo)) {
						holderPBC.mDetailsTwo.setText(broadcastTwo.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcastTwo.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTwo)) {
						Broadcast broadcast = feedItem.getBroadcast();
						if (broadcast != null) {
							Program program = broadcast.getProgram();
							if (program != null ) {
								String season = program.getSeason().getNumber();
								int episode = program.getEpisodeNumber();
								String seasonEpisode = "";
								if (!season.equals("0")) {
									seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + feedItem.getBroadcast().getProgram().getSeason().getNumber() + " ";
								}
								if (episode != 0) {
									seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + String.valueOf(feedItem.getBroadcast().getProgram().getEpisodeNumber());
								}
								holderPBC.mDetailsTwo.setText(seasonEpisode);
							}
						}
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeTwo)) {
						holderPBC.mDetailsTwo.setText(broadcastTwo.getProgram().getSportType().getName() + " " + broadcastTwo.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeTwo)) {
						holderPBC.mDetailsTwo.setText(broadcastTwo.getProgram().getCategory());
					}
				}

				int durationTwo = 0;
				timeSinceBegin = 0;
				timeToEnd = 0;
				// MC - Calculate the duration of the program and set up ProgressBar.
				try {
					long startTime = DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getBeginTime());
					long endTime = DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getEndTime());
					timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getBeginTime());
					timeToEnd = DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getEndTime());
					durationTwo = (int) (startTime - endTime) / (1000 * 60);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (timeSinceBegin > 0 && timeToEnd < 0) {
					holderPBC.mProgressBarTwo.setMax(durationTwo);
	
					// MC - Calculate the current progress of the ProgressBar and update.
					int initialProgressTwo = 0;
					long differenceTwo = 0;
					try {
						differenceTwo = DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
	
					if (differenceTwo < 0) {
						holderPBC.mProgressBarTwo.setVisibility(View.GONE);
						initialProgressTwo = 0;
						holderPBC.mProgressBarTwo.setProgress(0);
					} else {
						try {
							initialProgressTwo = (int) DateUtilities.getAbsoluteTimeDifference(broadcastTwo.getBeginTime()) / (1000 * 60);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						holderPBC.mProgressBarTitleTwo.setText(durationTwo - initialProgressTwo + " " + mActivity.getResources().getString(R.string.minutes) + " "
								+ mActivity.getResources().getString(R.string.left));
						holderPBC.mProgressBarTwo.setProgress(initialProgressTwo);
						holderPBC.mProgressBarTwo.setVisibility(View.VISIBLE);
						holderPBC.mProgressBarTitleTwo.setVisibility(View.VISIBLE);
					}
				}
				else {
					holderPBC.mProgressBarTwo.setVisibility(View.GONE);
					holderPBC.mProgressBarTitleTwo.setVisibility(View.GONE);
				}

				holderPBC.mContainerTwo.setOnClickListener(new View.OnClickListener() {

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

			if (broadcasts.get(2) != null) {
				holderPBC.mContainerThree.setVisibility(View.VISIBLE);

				final Broadcast broadcastThree = broadcasts.get(2);

				String programTypeThree = broadcastThree.getProgram().getProgramType();

				mImageLoader.displayImage(broadcastThree.getProgram().getPortMUrl(), holderPBC.mPosterThree, ImageLoader.IMAGE_TYPE.THUMBNAIL);
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeThree)) {
					holderPBC.mTitleThree.setText(broadcastThree.getProgram().getSeries().getName());
				} else {
					holderPBC.mTitleThree.setText(broadcastThree.getProgram().getTitle());
				}
				try {
					holderPBC.mTimeThree.setText(DateUtilities.isoStringToDayOfWeek(broadcastThree.getBeginTime()) + " - " + DateUtilities.isoStringToTimeString(broadcastThree.getBeginTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				holderPBC.mChannelNameThree.setText(broadcastThree.getChannel().getName());

				int durationThree = 0;
				timeSinceBegin = 0;
				timeToEnd = 0;
				// MC - Calculate the duration of the program and set up ProgressBar.
				try {
					long startTime = DateUtilities.getAbsoluteTimeDifference(broadcastThree.getBeginTime());
					long endTime = DateUtilities.getAbsoluteTimeDifference(broadcastThree.getEndTime());
					timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(broadcastThree.getBeginTime());
					timeToEnd = DateUtilities.getAbsoluteTimeDifference(broadcastThree.getEndTime());
					durationThree = (int) (startTime - endTime) / (1000 * 60);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (timeSinceBegin > 0 && timeToEnd < 0) {
					holderPBC.mProgressBarThree.setMax(durationThree);
	
					// MC - Calculate the current progress of the ProgressBar and update.
					int initialProgressThree = 0;
					long differenceThree = 0;
					try {
						differenceThree = DateUtilities.getAbsoluteTimeDifference(broadcastThree.getBeginTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
	
					if (differenceThree < 0) {
						holderPBC.mProgressBarThree.setVisibility(View.GONE);
						initialProgressThree = 0;
						holderPBC.mProgressBarThree.setProgress(0);
					} else {
						try {
							initialProgressThree = (int) DateUtilities.getAbsoluteTimeDifference(broadcastThree.getBeginTime()) / (1000 * 60);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						holderPBC.mProgressBarTitleThree.setText(durationThree - initialProgressThree + " " + mActivity.getResources().getString(R.string.minutes) + " "
								+ mActivity.getResources().getString(R.string.left));
						holderPBC.mProgressBarThree.setProgress(initialProgressThree);
						holderPBC.mProgressBarThree.setVisibility(View.VISIBLE);
						holderPBC.mProgressBarTitleThree.setVisibility(View.VISIBLE);
					}
				}
				else {
					holderPBC.mProgressBarThree.setVisibility(View.GONE);
					holderPBC.mProgressBarTitleThree.setVisibility(View.GONE);
				}

				if (programTypeThree != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeThree)) {
						holderPBC.mDetailsThree.setText(broadcastThree.getProgram().getGenre() + mActivity.getResources().getString(R.string.from) + broadcastThree.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeThree)) {
						String season = broadcastThree.getProgram().getSeason().getNumber();
						int episode = broadcastThree.getProgram().getEpisodeNumber();
						String seasonEpisode = "";
						if (!season.equals("0")) {
							seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + broadcastThree.getProgram().getSeason().getNumber() + " ";
						}
						if (episode != 0) {
							seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + String.valueOf(broadcastThree.getProgram().getEpisodeNumber());
						}
						holderPBC.mDetailsThree.setText(seasonEpisode);
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeThree)) {
						holderPBC.mDetailsThree.setText(broadcastThree.getProgram().getSportType().getName() + " " + broadcastThree.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeThree)) {
						holderPBC.mDetailsThree.setText(broadcastThree.getProgram().getCategory());
					}
				}
				holderPBC.mContainerThree.setOnClickListener(new View.OnClickListener() {

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

	public Runnable yesNotificationTwitterProc(final ImageView remindTwitterIv) {
		return new Runnable() {
			public void run() {
				remindTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				mIsSet = false;
			}
		};
	}

	public Runnable yesNotificationProc(final ImageView remindLikeIv) {
		return new Runnable() {
			public void run() {
				remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				Log.d(TAG,"SET IMAGE DEFAULT");
				mIsSet = false;
			}
		};
	}

	public Runnable yesNotificationRecProc(final ImageView remindRecIv) {
		return new Runnable() {
			public void run() {
				remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
				Log.d(TAG,"SET IMAGE DEFAULT");
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
	static class PopularTwitterViewHolder {
		RelativeLayout containerTw;
		TextView headerTvTw;
		ImageView landscapeIvTw;
		ProgressBar landscapePbTw;
		TextView titleTvTw;
		TextView timeTvTw;
		TextView channelTvTw;
		TextView detailsTvTw;
		TextView progressbarTvTw;
		ProgressBar progressBarTw;
		LinearLayout likeContainerTw;
		ImageView likeTwitterIv;
		LinearLayout shareContainerTw;
		ImageView shareIvTw;
		LinearLayout remindContainerTw;
		ImageView remindTwitterIv;
	}
	
	static class BroadcastViewHolder {
		RelativeLayout container;
		TextView headerTv;
		ImageView landscapeIv;
		ProgressBar landscapePb;
		TextView titleTv;
		TextView timeTv;
		TextView channelTv;
		TextView detailsTv;
		TextView progressbarTv;
		ProgressBar progressBar;
		LinearLayout likeContainer;
		ImageView likeLikeIv;
		LinearLayout shareContainer;
		ImageView shareIv;
		LinearLayout remindContainer;
		ImageView remindLikeIv;
	}
	
	static class RecommendedBroadcastViewHolder {
		RelativeLayout containerRec;
		TextView headerTvRec;
		ImageView landscapeIvRec;
		ProgressBar landscapePbRec;
		TextView titleTvRec;
		TextView timeTvRec;
		TextView channelTvRec;
		TextView detailsTvRec;
		TextView progressbarTvRec;
		ProgressBar progressBarRec;
		LinearLayout likeContainerRec;
		ImageView likeRecIv;
		LinearLayout shareContainerRec;
		ImageView shareIvRec;
		LinearLayout remindContainerRec;
		ImageView remindRecIv;
	}
	
	static class PopularBroadcastsViewHolder {
		TextView header;
		LinearLayout mContainerOne;
		ImageView mPosterOne;
		ProgressBar mImageProgressBarOne;
		TextView mTitleOne;
		TextView mTimeOne;
		TextView mChannelNameOne;
		TextView mDetailsOne;
		TextView mProgressBarTitleOne;
		ProgressBar mProgressBarOne;
		
		LinearLayout mContainerTwo;
		ImageView mPosterTwo;
		ProgressBar mImageProgressBarTwo;
		TextView mTitleTwo;
		TextView mTimeTwo;
		TextView mChannelNameTwo;
		TextView mDetailsTwo;
		TextView mProgressBarTitleTwo;
		ProgressBar mProgressBarTwo;
		
		LinearLayout mContainerThree;
		ImageView mPosterThree;
		ProgressBar mImageProgressBarThree;
		TextView mTitleThree;
		TextView mTimeThree;
		TextView mChannelNameThree;
		TextView mDetailsThree;
		TextView mProgressBarTitleThree;
		ProgressBar mProgressBarThree;
	}
}
