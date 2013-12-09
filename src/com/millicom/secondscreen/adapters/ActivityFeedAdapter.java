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
import android.text.BoringLayout;
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
	
	public void populatePopularItemAtIndex(PopularBroadcastsViewHolder viewHolder, ArrayList<Broadcast> broadcasts, int popularRowIndex) {
		if (popularRowIndex < broadcasts.size()) {
			final Broadcast broadcast = broadcasts.get(popularRowIndex);
			if (broadcast != null) {
				broadcast.updateTimeToBeginAndTimeToEnd();
				final Program program = broadcast.getProgram();
				final long timeSinceBegin = broadcast.getTimeSinceBegin();
				final long timeToEnd = broadcast.getTimeToEnd();
				final int duration = broadcast.getDurationInMinutes();

				String programType = broadcast.getProgram().getProgramType();

				ImageView imageView = null;
				TextView title = null;
				TextView time = null;
				TextView channelName = null;
				TextView details = null;
				TextView progressTextView = null;
				ProgressBar progressBar = null;
				ProgressBar imageProgressBar = null;
				LinearLayout containerLayout = null;

				switch (popularRowIndex) {
				case 0: {
					imageView = viewHolder.mPosterOne;
					title = viewHolder.mTitleOne;
					time = viewHolder.mTimeOne;
					channelName = viewHolder.mChannelNameOne;
					details = viewHolder.mDetailsOne;
					progressBar = viewHolder.mProgressBarOne;
					progressTextView = viewHolder.mProgressBarTitleOne;
					imageProgressBar = viewHolder.mImageProgressBarOne;
					containerLayout = viewHolder.mContainerOne;
					break;
				}
				case 1: {
					imageView = viewHolder.mPosterTwo;
					title = viewHolder.mTitleTwo;
					time = viewHolder.mTimeTwo;
					channelName = viewHolder.mChannelNameTwo;
					details = viewHolder.mDetailsTwo;
					progressBar = viewHolder.mProgressBarTwo;
					progressTextView = viewHolder.mProgressBarTitleTwo;
					imageProgressBar = viewHolder.mImageProgressBarTwo;
					containerLayout = viewHolder.mContainerTwo;
					break;
				}
				case 2: {
					imageView = viewHolder.mPosterThree;
					title = viewHolder.mTitleThree;
					time = viewHolder.mTimeThree;
					channelName = viewHolder.mChannelNameThree;
					details = viewHolder.mDetailsThree;
					progressBar = viewHolder.mProgressBarThree;
					progressTextView = viewHolder.mProgressBarTitleThree;
					imageProgressBar = viewHolder.mImageProgressBarThree;
					containerLayout = viewHolder.mContainerThree;
					break;
				}
				}

				mImageLoader.displayImage(broadcast.getProgram().getPortMUrl(), imageView, ImageLoader.IMAGE_TYPE.THUMBNAIL);

				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					title.setText(broadcast.getProgram().getSeries().getName());
				} else {
					title.setText(broadcast.getProgram().getTitle());
				}

				time.setText(broadcast.getDayOfWeekWithTimeString());
				
				channelName.setText(broadcast.getChannel().getName());

				if (programType != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
						details.setText(broadcast.getProgram().getGenre() + " " + mActivity.getResources().getString(R.string.from)
								+ " " + broadcast.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						if (broadcast != null) {
							if (program != null) {
								String season = program.getSeason().getNumber();
								int episode = program.getEpisodeNumber();
								String seasonEpisode = "";
								if (!season.equals("0")) {
									seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
								}
								if (episode != 0) {
									seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
								}
								details.setText(seasonEpisode);
							}
						}
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
						details.setText(broadcast.getProgram().getSportType().getName() + " " + broadcast.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
						details.setText(broadcast.getProgram().getCategory());
					}
				}

				if (timeSinceBegin > 0 && timeToEnd < 0) {
					progressBar.setMax(duration);

					// MC - Calculate the current progress of the ProgressBar and update.
					int initialProgressOne = 0;

					if (broadcast.getTimeSinceBegin() < 0) {
						progressBar.setVisibility(View.GONE);
						initialProgressOne = 0;
						progressBar.setProgress(0);
					} else {
						initialProgressOne = (int) broadcast.getTimeSinceBegin() / (1000 * 60);

						progressTextView.setText(duration - initialProgressOne + " " + mActivity.getResources().getString(R.string.minutes) + " "
								+ mActivity.getResources().getString(R.string.left));
						progressBar.setProgress(initialProgressOne);
						progressBar.setVisibility(View.VISIBLE);
						progressTextView.setVisibility(View.VISIBLE);
					}
				} else {
					progressBar.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
				}

				containerLayout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						popularBroadcastClicked(broadcast);
					}
				});
			}
		}
	}
	
	private void popularBroadcastClicked(Broadcast broadcast) {
		Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
		intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);

		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);

		final FeedItem feedItem = getItem(position);

		/* Many broadcasts */
		if(type == ITEM_TYPE_POPULAR_BROADCASTS) {
			PopularBroadcastsViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.block_feed_popular, null);
	
				viewHolder = new PopularBroadcastsViewHolder();
				

				viewHolder.header = (TextView) convertView.findViewById(R.id.block_popular_header_tv);
				
				// one
				LinearLayout elementOne = (LinearLayout) convertView.findViewById(R.id.block_popular_element_one);
				viewHolder.mContainerOne = elementOne;
				viewHolder.mPosterOne = (ImageView) elementOne.findViewById(R.id.block_feed_popular_listitem_iv);
				viewHolder.mImageProgressBarOne = (ProgressBar) elementOne.findViewById(R.id.block_feed_popular_listitem_iv_progressbar);
				viewHolder.mTitleOne = (TextView) elementOne.findViewById(R.id.block_popular_feed_details_title_tv);
				viewHolder.mTimeOne = (TextView) elementOne.findViewById(R.id.block_popular_feed_details_time_tv);
				viewHolder.mChannelNameOne = (TextView) elementOne.findViewById(R.id.block_popular_feed_details_channel_tv);
				viewHolder.mDetailsOne = (TextView) elementOne.findViewById(R.id.block_popular_feed_details_extra_tv);
				viewHolder.mProgressBarTitleOne = (TextView) elementOne.findViewById(R.id.block_popular_feed_timeleft_tv);
				viewHolder.mProgressBarOne = (ProgressBar) elementOne.findViewById(R.id.block_popular_feed_progressbar);
				
				// two
				LinearLayout elementTwo = (LinearLayout) convertView.findViewById(R.id.block_popular_element_two);
				viewHolder.mContainerTwo = elementTwo;//(LinearLayout) elementTwo.findViewById(R.id.block_popular_feed_container);
				viewHolder.mPosterTwo = (ImageView) elementTwo.findViewById(R.id.block_feed_popular_listitem_iv);
				viewHolder.mImageProgressBarTwo = (ProgressBar) elementTwo.findViewById(R.id.block_feed_popular_listitem_iv_progressbar);
				viewHolder.mTitleTwo = (TextView) elementTwo.findViewById(R.id.block_popular_feed_details_title_tv);
				viewHolder.mTimeTwo = (TextView) elementTwo.findViewById(R.id.block_popular_feed_details_time_tv);
				viewHolder.mChannelNameTwo = (TextView) elementTwo.findViewById(R.id.block_popular_feed_details_channel_tv);
				viewHolder.mDetailsTwo = (TextView) elementTwo.findViewById(R.id.block_popular_feed_details_extra_tv);
				viewHolder.mProgressBarTitleTwo = (TextView) elementTwo.findViewById(R.id.block_popular_feed_timeleft_tv);
				viewHolder.mProgressBarTwo = (ProgressBar) elementTwo.findViewById(R.id.block_popular_feed_progressbar);
				
				// three
				LinearLayout elementThree = (LinearLayout) convertView.findViewById(R.id.block_popular_element_three);
				viewHolder.mContainerThree = elementThree;//(LinearLayout) elementThree.findViewById(R.id.block_popular_feed_container);
				viewHolder.mPosterThree = (ImageView) elementThree.findViewById(R.id.block_feed_popular_listitem_iv);
				viewHolder.mImageProgressBarThree = (ProgressBar) elementThree.findViewById(R.id.block_feed_popular_listitem_iv_progressbar);
				viewHolder.mTitleThree = (TextView) elementThree.findViewById(R.id.block_popular_feed_details_title_tv);
				viewHolder.mTimeThree = (TextView) elementThree.findViewById(R.id.block_popular_feed_details_time_tv);
				viewHolder.mChannelNameThree = (TextView) elementThree.findViewById(R.id.block_popular_feed_details_channel_tv);
				viewHolder.mDetailsThree = (TextView) elementThree.findViewById(R.id.block_popular_feed_details_extra_tv);
				viewHolder.mProgressBarTitleThree = (TextView) elementThree.findViewById(R.id.block_popular_feed_timeleft_tv);
				viewHolder.mProgressBarThree = (ProgressBar) elementThree.findViewById(R.id.block_popular_feed_progressbar);
				
				convertView.setTag(viewHolder);

			}
			
			final PopularBroadcastsViewHolder holderPBC = (PopularBroadcastsViewHolder) convertView.getTag();
			final ArrayList<Broadcast> broadcasts = feedItem.getBroadcasts();

			// one
			populatePopularItemAtIndex(holderPBC, broadcasts, 0);
	
			// two
			populatePopularItemAtIndex(holderPBC, broadcasts, 1);
			
			// three
			populatePopularItemAtIndex(holderPBC, broadcasts, 2);

			mImageLoader.displayImage(broadcasts.get(1).getProgram().getPortMUrl(), holderPBC.mPosterTwo, ImageLoader.IMAGE_TYPE.THUMBNAIL);
			mImageLoader.displayImage(broadcasts.get(2).getProgram().getPortMUrl(), holderPBC.mPosterThree, ImageLoader.IMAGE_TYPE.THUMBNAIL);

			RelativeLayout footer = (RelativeLayout) convertView.findViewById(R.id.block_popular_show_more_container);
			footer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, PopularPageActivity.class);
					// ADD THE URL TO THE POPULAR LIST AS AN ARGUMENT?
					mActivity.startActivity(intent);
				}
			});

		} else {		

			/* One broadcast */
			final Broadcast broadcast = feedItem.getBroadcast();
			broadcast.updateTimeToBeginAndTimeToEnd();
			
			final Program program = broadcast.getProgram();
			final int duration = broadcast.getDurationInMinutes();
			final long timeSinceBegin = broadcast.getTimeSinceBegin();
			final long timeToEnd = broadcast.getTimeToEnd();
			
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
						viewHolder.likeContainerTw = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_like_button_container);
						viewHolder.likeTwitterIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_like_button_iv);
						viewHolder.shareContainerTw = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_share_button_container);
						viewHolder.shareIvTw = (ImageView) convertView.findViewById(R.id.block_feed_liked_share_button_iv);
						viewHolder.remindContainerTw = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_remind_button_container);
						viewHolder.remindTwitterIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_remind_button_iv);
						
						convertView.setTag(viewHolder);
					}
					final PopularTwitterViewHolder holder = (PopularTwitterViewHolder) convertView.getTag();
		
					// mIsLiked = LikeService.isLiked(mToken, program.getProgramId());
		
					holder.headerTvTw.setText(mActivity.getResources().getString(R.string.icon_twitter) + " " + feedItem.getTitle());
		
					final String programTypeTw = program.getProgramType();
		
					// determine like
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTw)) {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getSeries().getSeriesId());
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeTw)) {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getSportType().getSportTypeId());
					} else {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getProgramId());
					}
		
					mImageLoader.displayImage(program.getLandLUrl(), holder.landscapeIvTw, ImageLoader.IMAGE_TYPE.GALLERY);
		
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTw)) {
						holder.titleTvTw.setText(program.getSeries().getName());
					} else {
						holder.titleTvTw.setText(program.getTitle());
					}
		
				
					holder.timeTvTw.setText(broadcast.getDayOfWeekWithTimeString());
			
					holder.channelTvTw.setText(broadcast.getChannel().getName());
		
					if (programTypeTw != null) {
						if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeTw)) {
							holder.detailsTvTw.setText(program.getGenre() + " " + mActivity.getResources().getString(R.string.from) + " " + program.getYear());
						} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeTw)) {
							String season = program.getSeason().getNumber();
							int episode = program.getEpisodeNumber();
							String seasonEpisode = "";
							if (!season.equals("0")) {
								seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
							}
							if (episode != 0) {
								seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
							}
							if (season.equals("0") && episode == 0) {
								holder.detailsTvTw.setVisibility(View.GONE);
							}
							holder.detailsTvTw.setText(seasonEpisode);
						} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeTw)) {
							holder.detailsTvTw.setText(program.getSportType().getName() + " " + program.getTournament());
						} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeTw)) {
							holder.detailsTvTw.setText(program.getCategory());
						}
					}
		
					holder.containerTw.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							popularBroadcastClicked(broadcast);
		
						}
					});
		
		
					Log.d(TAG, "TIME SINCE BEGIN: " + timeSinceBegin);
					Log.d(TAG, "TIME TO END: " + timeToEnd);
					
					if (timeSinceBegin > 0 && timeToEnd < 0) {
						holder.progressBarTw.setMax(duration);
			
						// MC - Calculate the current progress of the ProgressBar and update.
						int initialProgressTw = 0;
						Log.d(TAG, "GET TIME SINCE BEGIN: " + broadcast.getTimeSinceBegin());
						
						if (broadcast.getTimeSinceBegin() < 0) {
							holder.progressBarTw.setVisibility(View.GONE);
							initialProgressTw = 0;
							holder.progressBarTw.setProgress(0);
						} else {
							initialProgressTw = (int)broadcast.getTimeSinceBegin() / (1000*60);
							
							Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							Log.d(TAG,"initialProgressTw: " + initialProgressTw);
							
							holder.progressbarTvTw.setText(duration - initialProgressTw + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
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
					dbItemTw = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
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
								programId = program.getSeries().getSeriesId();
								contentTitle = program.getTitle();
							} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeTw)) {
								programId = program.getSportType().getSportTypeId();
								contentTitle = program.getSportType().getName();
							} else {
								programId = program.getProgramId();
								contentTitle = program.getTitle();
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
							ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), broadcast.getShareUrl(),
									mActivity.getResources().getString(R.string.share_action_title));
						}
					});
		
					holder.remindContainerTw.setOnClickListener(new View.OnClickListener() {
		
						@Override
						public void onClick(View v) {
							NotificationDbItem item = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
							if (item.getNotificationId() != 0) {
								mIsSet = true;
								mNotificationId = item.getNotificationId();
							} else {
								mIsSet = false;
							}
							
							Log.d(TAG,"Twitter: " + mIsSet);
							
							if (mIsSet == false) {
								if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), broadcast.getTvDateString())) {
									NotificationService.showSetNotificationToast(mActivity);
									holder.remindTwitterIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
		
									NotificationDbItem dbItemTw = new NotificationDbItem();
									Log.d(TAG, "broadcast.getChannel().getChannelId()" + broadcast.getChannel().getChannelId());
									Log.d(TAG, "broadcast.getBeginTimeMillis()" + broadcast.getBeginTimeStringGmt());
		
									dbItemTw = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
		
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
									notificationDlg.showRemoveNotificationDialog(mActivity, broadcast, mNotificationId, yesNotificationTwitterProc(holder.remindTwitterIv), noNotificationProc());
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
						viewHolder.likeContainer = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_like_button_container);
						viewHolder.likeLikeIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_like_button_iv);
						viewHolder.shareContainer = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_share_button_container);
						viewHolder.shareIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_share_button_iv);
						viewHolder.remindContainer = (RelativeLayout) convertView.findViewById(R.id.block_feed_liked_remind_button_container);
						viewHolder.remindLikeIv = (ImageView) convertView.findViewById(R.id.block_feed_liked_remind_button_iv);
						
						convertView.setTag(viewHolder);
					}
					final BroadcastViewHolder holderBC = (BroadcastViewHolder) convertView.getTag();
					// mIsLiked = LikeService.isLiked(mToken, program.getProgramId());
		
					final String programType = program.getProgramType();
					// determine like
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getSeries().getSeriesId());
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getSportType().getSportTypeId());
					} else {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getProgramId());
					}
		
					holderBC.headerTv.setText(feedItem.getTitle());
		
					mImageLoader.displayImage(program.getLandLUrl(), holderBC.landscapeIv, ImageLoader.IMAGE_TYPE.GALLERY);
		
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						holderBC.titleTv.setText(program.getSeries().getName());
					} else {
						holderBC.titleTv.setText(program.getTitle());
					}
		
					holderBC.timeTv.setText(broadcast.getDayOfWeekWithTimeString());
		
					holderBC.channelTv.setText(broadcast.getChannel().getName());
		
					if (programType != null) {
						if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
							holderBC.detailsTv.setText(program.getGenre() + " " + mActivity.getResources().getString(R.string.from) + " " + program.getYear());
						} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
							String season = program.getSeason().getNumber();
							int episode = program.getEpisodeNumber();
							String seasonEpisode = "";
							if (!season.equals("0")) {
								seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
							}
							if (episode != 0) {
								seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
							}
							if (season.equals("0") && episode == 0) {
								holderBC.detailsTv.setVisibility(View.GONE);
							}
							holderBC.detailsTv.setText(seasonEpisode);
						} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
							holderBC.detailsTv.setText(program.getSportType().getName() + " " + program.getTournament());
						} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
							holderBC.detailsTv.setText(program.getCategory());
						}
					}
		
					holderBC.container.setOnClickListener(new View.OnClickListener() {
		
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
							intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
							intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
							intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
							intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
		
							mActivity.startActivity(intent);
							mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		
						}
					});
		
					if (timeSinceBegin > 0 && timeToEnd < 0) {
						holderBC.progressBar.setMax(duration);
			
						// MC - Calculate the current progress of the ProgressBar and update.
						int initialProgress = 0;
						long difference = broadcast.getTimeSinceBegin();
			
						if (difference < 0) {
							holderBC.progressBar.setVisibility(View.GONE);
							initialProgress = 0;
							holderBC.progressBar.setProgress(0);
						} else {
							initialProgress = (int) broadcast.getTimeSinceBegin() / (1000 * 60);
						
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
					dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
					Log.d(TAG,"uP: " + broadcast.getChannel().getChannelId() + " " + broadcast.getBeginTimeStringGmt());
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
								programId = program.getSeries().getSeriesId();
								contentTitle = program.getTitle();
							} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
								programId = program.getSportType().getSportTypeId();
								contentTitle = program.getSportType().getName();
							} else {
								programId = program.getProgramId();
								contentTitle = program.getTitle();
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
							ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), broadcast.getShareUrl(),
									mActivity.getResources().getString(R.string.share_action_title));
						}
					});
		
					holderBC.remindContainer.setOnClickListener(new View.OnClickListener() {
		
						@Override
						public void onClick(View v) {
							
							NotificationDbItem dbItem = new NotificationDbItem();
							
							
							dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
							Log.d(TAG,"DOWN: " + broadcast.getChannel().getChannelId() + " " + broadcast.getBeginTimeStringGmt());
							
							if (dbItem.getNotificationId() != 0) {
								Log.d(TAG,"dbItem: " + dbItem.getProgramTitle() + " " + dbItem.getNotificationId() );
								mNotificationId = dbItem.getNotificationId();
								mIsSet = true;
							} else {
								mIsSet = false;
							}
							
							Log.d(TAG,"lIKED REMIND: " + mIsSet);
							
							if (mIsSet == false) {
								if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), broadcast.getTvDateString())) {
									NotificationService.showSetNotificationToast(mActivity);
									holderBC.remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
		
									NotificationDbItem dbItemRemind= new NotificationDbItem();
									Log.d(TAG, "broadcast.getChannel().getChannelId()" + broadcast.getChannel().getChannelId());
									Log.d(TAG, "broadcast.getBeginTimeMillis()" + broadcast.getBeginTimeStringGmt());
		
									dbItemRemind = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
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
									notificationDlg.showRemoveNotificationDialog(mActivity, broadcast, mNotificationId, yesNotificationProc(holderBC.remindLikeIv), noNotificationProc());
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
					// mIsLiked = LikeService.isLiked(mToken, program.getProgramId());
		
					final String programTypeRec = program.getProgramType();
		
					// determine like
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getSeries().getSeriesId());
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeRec)) {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getSportType().getSportTypeId());
					} else {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getProgramId());
					}
		
					holderRBC.headerTvRec.setText(feedItem.getTitle());
		
					mImageLoader.displayImage(program.getLandLUrl(), holderRBC.landscapeIvRec, ImageLoader.IMAGE_TYPE.GALLERY);
		
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
						holderRBC.titleTvRec.setText(program.getSeries().getName());
					} else {
						holderRBC.titleTvRec.setText(program.getTitle());
					}
	
					holderRBC.timeTvRec.setText(broadcast.getDayOfWeekWithTimeString());
		
					holderRBC.channelTvRec.setText(broadcast.getChannel().getName());
		
					if (programTypeRec != null) {
						if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programTypeRec)) {
							holderRBC.detailsTvRec.setText(program.getGenre() + " " + mActivity.getResources().getString(R.string.from) + " " + program.getYear());
						} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programTypeRec)) {
							String season = program.getSeason().getNumber();
							int episode = program.getEpisodeNumber();
							String seasonEpisode = "";
							if (!season.equals("0")) {
								seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
							}
							if (episode != 0) {
								seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
							}
							holderRBC.detailsTvRec.setText(seasonEpisode);
						} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeRec)) {
							holderRBC.detailsTvRec.setText(program.getSportType().getName() + " " + program.getTournament());
						} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programTypeRec)) {
							holderRBC.detailsTvRec.setText(program.getCategory());
						}
					}
		
					if (timeSinceBegin > 0 && timeToEnd < 0) {
						holderRBC.progressBarRec.setMax(duration);
			
						// MC - Calculate the current progress of the ProgressBar and update.
						int initialProgressRec = 0;
				
						if (broadcast.getTimeSinceBegin() < 0) {
							holderRBC.progressBarRec.setVisibility(View.GONE);
							initialProgressRec = 0;
							holderRBC.progressBarRec.setProgress(0);
						} else {
							initialProgressRec = (int) (broadcast.getTimeSinceBegin() / (1000 * 60));
						
							holderRBC.progressbarTvRec.setText(duration - initialProgressRec + " " + mActivity.getResources().getString(R.string.minutes) + " " + mActivity.getResources().getString(R.string.left));
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
							popularBroadcastClicked(broadcast);
						}
					});
					
					NotificationDbItem dbItemBroadcast = new NotificationDbItem();
					dbItemBroadcast = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
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
								programId = program.getSeries().getSeriesId();
								contentTitle = program.getTitle();
							} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programTypeRec)) {
								programId = program.getSportType().getSportTypeId();
								contentTitle = program.getSportType().getName();
							} else {
								programId = program.getProgramId();
								contentTitle = program.getTitle();
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
							ShareAction.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), broadcast.getShareUrl(),
									mActivity.getResources().getString(R.string.share_action_title));
						}
					});
		
					holderRBC.remindContainerRec.setOnClickListener(new View.OnClickListener() {
		
						@Override
						public void onClick(View v) {					
							NotificationDbItem dbItemBroadcast = new NotificationDbItem();
							dbItemBroadcast = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
							if (dbItemBroadcast.getNotificationId() != 0) {
								mIsSet = true;
								mNotificationId = dbItemBroadcast.getNotificationId(); 
								Log.d(TAG,"Recommended down: " + mIsSet + " " + mNotificationId);
							} else {
								mIsSet = false;
								Log.d(TAG,"Recommended down: " + mIsSet);
							}
		
							if (mIsSet == false) {
								if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), broadcast.getTvDateString())) {
									NotificationService.showSetNotificationToast(mActivity);
									holderRBC.remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
		
									NotificationDbItem dbItem = new NotificationDbItem();
									dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
									mNotificationId = dbItem.getNotificationId();
									AnimationUtilities.animationSet(holderRBC.remindRecIv);
									mIsSet = true;
								} else {
									Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
								}
							} else {
								if (mNotificationId != -1) {
									NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
									notificationDlg.showRemoveNotificationDialog(mActivity, broadcast, mNotificationId, yesNotificationRecProc(holderRBC.remindRecIv), noNotificationProc());
								} else {
									Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
		
					break;
				case ITEM_TYPE_POPULAR_BROADCASTS:			
					/* Handled above */
					break;
			}
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
		RelativeLayout likeContainerTw;
		ImageView likeTwitterIv;
		RelativeLayout shareContainerTw;
		ImageView shareIvTw;
		RelativeLayout remindContainerTw;
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
		RelativeLayout likeContainer;
		ImageView likeLikeIv;
		RelativeLayout shareContainer;
		ImageView shareIv;
		RelativeLayout remindContainer;
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
