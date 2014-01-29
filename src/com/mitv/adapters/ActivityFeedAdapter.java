package com.mitv.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.content.activity.ActivityActivity;
import com.mitv.content.activity.PopularPageActivity;
import com.mitv.like.LikeService;
import com.mitv.model.Broadcast;
import com.mitv.model.FeedItem;
import com.mitv.model.NotificationDbItem;
import com.mitv.model.Program;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationDialogHandler;
import com.mitv.notification.NotificationService;
import com.mitv.share.ShareAction;
import com.mitv.storage.DazooStore;
import com.mitv.tvguide.BroadcastPageActivity;
import com.mitv.utilities.AnimationUtilities;
import com.mitv.utilities.ProgressBarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class ActivityFeedAdapter extends AdListAdapter<FeedItem> {

	private static final String		TAG									= "ActivityFeedAdapter";
	private Activity				mActivity;
	private ArrayList<FeedItem>		mFeedItems;
	private LayoutInflater			mLayoutInflater;

	private int						DAZOO_ACTIVITY_BLOCKS_TYPE_NUMBER	= 5;

	private static final int		ITEM_TYPE_BROADCAST					= 1; //0 is reserved for ads
	private static final int		ITEM_TYPE_RECOMMENDED_BROADCAST		= 2;
	private static final int		ITEM_TYPE_POPULAR_BROADCASTS		= 3;
	private static final int		ITEM_TYPE_POPULAR_TWITTER			= 4;
	private static final int		ITEM_TYPE_POPULAR_BROADCAST			= 5;

	private String					mToken;
	private int						mNotificationId;
	private NotificationDataSource	mNotificationDataSource;
	private ArrayList<String>		mLikeIds;
	private int						currentPosition						= -1;

	// private ImageView likeLikeIv, remindLikeIv, likeRecIv, remindRecIv, likeTwitterIv, remindTwitterIv;
	private boolean					mIsLiked							= false, mIsSet = false;

	public ActivityFeedAdapter(Activity activity, ArrayList<FeedItem> feedItems, String token) {
		super(Consts.JSON_AND_FRAGMENT_KEY_ACTIVITY, activity, feedItems);
		this.mActivity = activity;
		this.mFeedItems = feedItems;
		this.mToken = token;
		this.mNotificationDataSource = new NotificationDataSource(mActivity);
		this.mLikeIds = LikeService.getLikeIdsList(token);
	}

	@Override
	public int getViewTypeCount() {
		return super.getViewTypeCount() + DAZOO_ACTIVITY_BLOCKS_TYPE_NUMBER;
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
		if (item != null) {
			String feedItemType = item.getItemType();
			if (Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_BROADCASTS.equals(feedItemType)) {
				return ITEM_TYPE_POPULAR_BROADCASTS;
			} 
			else if (Consts.DAZOO_FEED_ITEM_TYPE_BROADCAST.equals(feedItemType)) {
				return ITEM_TYPE_BROADCAST;
			} 
			else if (Consts.DAZOO_FEED_ITEM_TYPE_RECOMMENDED_BROADCAST.equals(feedItemType)) {
				return ITEM_TYPE_RECOMMENDED_BROADCAST;
			} 
			else if (Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_TWITTER.equals(feedItemType)) {
				return ITEM_TYPE_POPULAR_TWITTER;
			} 
			else if (Consts.DAZOO_FEED_ITEM_POPULAR_BROADCAST.equals(feedItemType)) {
				return ITEM_TYPE_POPULAR_BROADCAST;
			} 
			else {
				return ITEM_TYPE_BROADCAST;
			}
		} 
		else {
			return super.getItemViewType(position);
		}
	}

	public void populatePopularItemAtIndex(PopularBroadcastsViewHolder viewHolder, ArrayList<Broadcast> broadcasts, int popularRowIndex) {
		ImageView imageView = null;
		TextView title = null;
		TextView time = null;
		TextView channelName = null;
		TextView details = null;
		TextView progressTextView = null;
		ProgressBar progressBar = null;
		RelativeLayout containerLayout = null;

		switch (popularRowIndex) {
		case 0: {
			imageView = viewHolder.mPosterOne;
			title = viewHolder.mTitleOne;
			time = viewHolder.mTimeOne;
			channelName = viewHolder.mChannelNameOne;
			details = viewHolder.mDetailsOne;
			progressBar = viewHolder.mProgressBarOne;
			progressTextView = viewHolder.mProgressBarTitleOne;
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
			containerLayout = viewHolder.mContainerThree;
			break;
		}
		}

		if (popularRowIndex < broadcasts.size()) {
			final Broadcast broadcast = broadcasts.get(popularRowIndex);
			if (broadcast != null) {
				broadcast.updateTimeToBeginAndTimeToEnd();
				final Program program = broadcast.getProgram();

				String programType = broadcast.getProgram().getProgramType();

				ImageAware imageAware = new ImageViewAware(imageView, false);
				ImageLoader.getInstance().displayImage(broadcast.getProgram().getPortMUrl(), imageAware);

				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					title.setText(broadcast.getProgram().getSeries().getName());
				} 
				else {
					title.setText(broadcast.getProgram().getTitle());
				}

				time.setText(broadcast.getDayOfWeekWithTimeString());

				channelName.setText(broadcast.getChannel().getName());

				if (programType != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
						details.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
					} 
					else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						if (broadcast != null) {
							if (program != null) {
								String season = program.getSeason().getNumber();
								int episode = program.getEpisodeNumber();
								String seasonEpisode = "";
								if (!season.equals("0")) {
									seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
								}
								if (episode > 0) {
									seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
								}
								details.setText(seasonEpisode);
							}
						}
					} 
					else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
						details.setText(broadcast.getProgram().getSportType().getName() + " " + broadcast.getProgram().getTournament());
					} 
					else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
						details.setText(broadcast.getProgram().getCategory());
					}
				}


				progressBar.setVisibility(View.GONE);
				progressTextView.setVisibility(View.GONE);

				if (broadcast.isRunning()) {
					ProgressBarUtils.setupProgressBar(mActivity, broadcast, progressBar, progressTextView);
				} 

				containerLayout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						popularBroadcastClicked(broadcast);
					}
				});
			}
		}
		else {
			containerLayout.setVisibility(View.GONE);
		}
	}

	private void popularBroadcastClicked(Broadcast broadcast) {
		Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getTvDateString());
		intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
		intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);

		mActivity.startActivity(intent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/* Superclass AdListAdapter will create view if this is a position of an ad. */
		View rowView = super.getView(position, convertView, parent);

		if (rowView == null) {
			rowView = getViewForFeedItemCell(position, convertView, parent);
		}

		return rowView;
	}

	public View getViewForFeedItemCell(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		int type = getItemViewType(position);

		final FeedItem feedItem = getItem(position);

		if (feedItem.getBroadcast() != null || (feedItem.getBroadcasts() != null && feedItem.getBroadcasts().size() > 0)) {

			/* Many broadcasts */
			if (type == ITEM_TYPE_POPULAR_BROADCASTS) {
				
				final ArrayList<Broadcast> broadcasts = feedItem.getBroadcasts();
				// If there are atleast 2 broadcasts in popular list
				if (broadcasts.size() > 1) {
					if (rowView == null) {
						mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

						PopularBroadcastsViewHolder viewHolder = new PopularBroadcastsViewHolder();
						rowView = mLayoutInflater.inflate(R.layout.block_feed_popular, null);

						viewHolder.header = (TextView) rowView.findViewById(R.id.block_popular_header_tv);

						// one
						RelativeLayout elementOne = (RelativeLayout) rowView.findViewById(R.id.block_popular_element_one);
						viewHolder.mContainerOne = elementOne;
						viewHolder.mPosterOne = (ImageView) elementOne.findViewById(R.id.element_popular_list_item_image_iv);
						viewHolder.mTitleOne = (TextView) elementOne.findViewById(R.id.element_popular_list_item_title_tv);
						viewHolder.mTimeOne = (TextView) elementOne.findViewById(R.id.element_popular_list_item_time_tv);
						viewHolder.mChannelNameOne = (TextView) elementOne.findViewById(R.id.element_popular_list_item_channel_tv);
						viewHolder.mDetailsOne = (TextView) elementOne.findViewById(R.id.element_popular_list_item_type_tv);
						viewHolder.mProgressBarTitleOne = (TextView) elementOne.findViewById(R.id.element_popular_list_item_timeleft_tv);
						viewHolder.mProgressBarOne = (ProgressBar) elementOne.findViewById(R.id.element_popular_list_item_progressbar);

						// two
						RelativeLayout elementTwo = (RelativeLayout) rowView.findViewById(R.id.block_popular_element_two);
						viewHolder.mContainerTwo = elementTwo;// (LinearLayout) elementTwo.findViewById(R.id.block_popular_feed_container);
						viewHolder.mPosterTwo = (ImageView) elementTwo.findViewById(R.id.element_popular_list_item_image_iv);
						viewHolder.mTitleTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_title_tv);
						viewHolder.mTimeTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_time_tv);
						viewHolder.mChannelNameTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_channel_tv);
						viewHolder.mDetailsTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_type_tv);
						viewHolder.mProgressBarTitleTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_timeleft_tv);
						viewHolder.mProgressBarTwo = (ProgressBar) elementTwo.findViewById(R.id.element_popular_list_item_progressbar);

						// three
						RelativeLayout elementThree = (RelativeLayout) rowView.findViewById(R.id.block_popular_element_three);
						viewHolder.mContainerThree = elementThree;// (LinearLayout) elementThree.findViewById(R.id.block_popular_feed_container);
						viewHolder.mPosterThree = (ImageView) elementThree.findViewById(R.id.element_popular_list_item_image_iv);
						viewHolder.mTitleThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_title_tv);
						viewHolder.mTimeThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_time_tv);
						viewHolder.mChannelNameThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_channel_tv);
						viewHolder.mDetailsThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_type_tv);
						viewHolder.mProgressBarTitleThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_timeleft_tv);
						viewHolder.mProgressBarThree = (ProgressBar) elementThree.findViewById(R.id.element_popular_list_item_progressbar);

						rowView.setTag(viewHolder);
					}

					final PopularBroadcastsViewHolder holderPBC = (PopularBroadcastsViewHolder) rowView.getTag();

					// one
					populatePopularItemAtIndex(holderPBC, broadcasts, 0);

					// two
					populatePopularItemAtIndex(holderPBC, broadcasts, 1);

					// three
					populatePopularItemAtIndex(holderPBC, broadcasts, 2);

					RelativeLayout footer = (RelativeLayout) rowView.findViewById(R.id.block_popular_show_more_container);
					footer.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mActivity, PopularPageActivity.class);
							mActivity.startActivity(intent);
						}
					});

				}
				//If there is only one broadcast in popular list, show as popular card
				else if (broadcasts.size() > 0) {
					feedItem.setBroadcast(broadcasts.get(0));
					feedItem.setItemType(Consts.DAZOO_FEED_ITEM_POPULAR_BROADCAST);
					getViewForFeedItemCell(position, convertView, parent);
				}
			} 
			else {

				/* One broadcast */
				final Broadcast broadcast = feedItem.getBroadcast();
				broadcast.updateTimeToBeginAndTimeToEnd();

				final Program program = broadcast.getProgram();

				switch (type) {
				case ITEM_TYPE_RECOMMENDED_BROADCAST:
				case ITEM_TYPE_POPULAR_TWITTER:
				case ITEM_TYPE_POPULAR_BROADCAST:
				case ITEM_TYPE_BROADCAST:
					if (rowView == null) {
						mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

						BroadcastViewHolder viewHolder = new BroadcastViewHolder();

						rowView = mLayoutInflater.inflate(R.layout.block_feed_liked, null);

						viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.block_feed_liked_main_container);
						viewHolder.headerTv = (TextView) rowView.findViewById(R.id.block_feed_liked_header_tv);
						viewHolder.landscapeIv = (ImageView) rowView.findViewById(R.id.block_feed_liked_content_iv);
						viewHolder.landscapePb = (ProgressBar) rowView.findViewById(R.id.block_feed_liked_content_iv_progressbar);
						viewHolder.titleTv = (TextView) rowView.findViewById(R.id.block_feed_liked_title_tv);
						viewHolder.timeTv = (TextView) rowView.findViewById(R.id.block_feed_liked_time_tv);
						viewHolder.channelTv = (TextView) rowView.findViewById(R.id.block_feed_liked_channel_tv);
						viewHolder.detailsTv = (TextView) rowView.findViewById(R.id.block_feed_liked_details_tv);
						viewHolder.progressbarTv = (TextView) rowView.findViewById(R.id.block_feed_liked_timeleft_tv);
						viewHolder.progressBar = (ProgressBar) rowView.findViewById(R.id.block_feed_liked_progressbar);

						viewHolder.likeContainer = (RelativeLayout) rowView.findViewById(R.id.element_social_buttons_like_button_container);
						viewHolder.likeLikeIv = (ImageView) rowView.findViewById(R.id.element_social_buttons_like_button_iv);
						viewHolder.shareContainer = (RelativeLayout) rowView.findViewById(R.id.element_social_buttons_share_button_container);
						viewHolder.shareIv = (ImageView) rowView.findViewById(R.id.element_social_buttons_share_button_iv);
						viewHolder.remindContainer = (RelativeLayout) rowView.findViewById(R.id.element_social_buttons_remind_button_container);
						viewHolder.remindLikeIv = (ImageView) rowView.findViewById(R.id.element_social_buttons_remind_button_iv);

						viewHolder.container.setTag(Integer.valueOf(position));
						rowView.setTag(viewHolder);
					}

					final BroadcastViewHolder holderBC = (BroadcastViewHolder) rowView.getTag();
					// mIsLiked = LikeService.isLiked(mToken, program.getProgramId());

					final String programType = program.getProgramType();
					// determine like
					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getSeries().getSeriesId());
					} 
					else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getSportType().getSportTypeId());
					} 
					else {
						mIsLiked = DazooStore.getInstance().isInTheLikesList(program.getProgramId());
					}

					if (ITEM_TYPE_POPULAR_TWITTER == type) {
						holderBC.headerTv.setText(mActivity.getResources().getString(R.string.icon_twitter) + " " + feedItem.getTitle());
					} 
					else {
						holderBC.headerTv.setText(feedItem.getTitle());
					}

					ImageAware imageAware = new ImageViewAware(holderBC.landscapeIv, false);
					ImageLoader.getInstance().displayImage(program.getLandLUrl(), imageAware);

					if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						holderBC.titleTv.setText(program.getSeries().getName());
					} 
					else {
						holderBC.titleTv.setText(program.getTitle());
					}

					holderBC.timeTv.setText(broadcast.getDayOfWeekWithTimeString());
					holderBC.channelTv.setText(broadcast.getChannel().getName());

					if (programType != null) {
						if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
							holderBC.detailsTv.setText(program.getGenre() + " " + program.getYear());
						} 
						else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
							String season = program.getSeason().getNumber();
							int episode = program.getEpisodeNumber();
							String seasonEpisode = "";
							if (!season.equals("0")) {
								seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
							}
							if (episode > 0) {
								seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
							}
							if (season.equals("0") && episode == 0) {
								holderBC.detailsTv.setVisibility(View.GONE);
							}
							holderBC.detailsTv.setText(seasonEpisode);
						} 
						else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
							holderBC.detailsTv.setText(program.getSportType().getName() + " " + program.getTournament());
						} 
						else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
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
							intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);

							mActivity.startActivityForResult(intent, 0);

						}
					});

					if (broadcast.isRunning()) {
						ProgressBarUtils.setupProgressBar(mActivity, broadcast, holderBC.progressBar, holderBC.progressbarTv);
					} 
					else {
						holderBC.progressbarTv.setVisibility(View.GONE);
						holderBC.progressBar.setVisibility(View.GONE);
					}

					if (!broadcast.hasStarted()) {
						NotificationDbItem dbItem = new NotificationDbItem();
						// Sometime channel is null, avoiding crash
						if (broadcast.getChannel() == null) {
							// Toast.makeText(mActivity, "Channel null", Toast.LENGTH_LONG).show();
							Log.d(TAG, "Channe is null");
						} 
						else {
							dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
						}
						if (dbItem.getNotificationId() != 0) {
							mIsSet = true;
							mNotificationId = dbItem.getNotificationId();
						} else {
							mIsSet = false;
							mNotificationId = -1;
						}


						if (mIsSet) { 
							holderBC.remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
						}
						else { 
							holderBC.remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
						}
					} 
					else {
						holderBC.remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
					}

					if (mIsLiked) {
						holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
					}
					else { 
						holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));
					}

					holderBC.likeContainer.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							String likeType = LikeService.getLikeType(programType);

							String programId, contentTitle;
							if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
								programId = program.getSeries().getSeriesId();
								contentTitle = program.getSeries().getName();
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

									ActivityActivity.toast = LikeService.showSetLikeToast(mActivity, contentTitle);
									holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));

									AnimationUtilities.animationSet(holderBC.likeLikeIv);

									mIsLiked = true;
								} else {
									// Toast.makeText(mActivity, "Adding a like faced an error", Toast.LENGTH_SHORT).show();
									Log.d(TAG, "!!! Adding a like faced an error !!!");
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
							if (!broadcast.hasStarted()) {
								NotificationDbItem dbItem = new NotificationDbItem();

								dbItem = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());

								if (dbItem.getNotificationId() != 0) {
									Log.d(TAG, "dbItem: " + dbItem.getProgramTitle() + " " + dbItem.getNotificationId());
									mNotificationId = dbItem.getNotificationId();
									mIsSet = true;
								} 
								else {
									mIsSet = false;
								}

								if (mIsSet == false) {
									if (NotificationService.setAlarm(mActivity, broadcast, broadcast.getChannel(), broadcast.getTvDateString())) {
										ActivityActivity.toast = NotificationService.showSetNotificationToast(mActivity);
										holderBC.remindLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));

										NotificationDbItem dbItemRemind = new NotificationDbItem();
										dbItemRemind = mNotificationDataSource.getNotification(broadcast.getChannel().getChannelId(), broadcast.getBeginTimeMillisGmt());
										mNotificationId = dbItemRemind.getNotificationId();

										AnimationUtilities.animationSet(holderBC.remindLikeIv);

										mIsSet = true;
									} 
									else {
										// Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
										Log.d(TAG, "!!! Setting notification faced an error !!!");
									}
								} 
								else {
									if (mNotificationId != -1) {
										if (ActivityActivity.toast != null) {
											ActivityActivity.toast.cancel();
										}
										NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
										notificationDlg.showRemoveNotificationDialog(mActivity, broadcast, mNotificationId, yesNotificationProc(holderBC.remindLikeIv), noNotificationProc());
									} 
									else {
										// Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
										Log.d(TAG, "!!! Could not find such reminder in DB !!!");
									}
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
		}
		// else {
		// /* No content */
		// convertView = LayoutInflater.from(mActivity).inflate(R.layout.no_data, null);
		// }
		return rowView;
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
				mIsSet = false;
			}
		};
	}

	public Runnable yesNotificationRecProc(final ImageView remindRecIv) {
		return new Runnable() {
			public void run() {
				remindRecIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
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

	static class BroadcastViewHolder {
		RelativeLayout	container;
		TextView		headerTv;
		ImageView		landscapeIv;
		ProgressBar		landscapePb;
		TextView		titleTv;
		TextView		timeTv;
		TextView		channelTv;
		TextView		detailsTv;
		TextView		progressbarTv;
		ProgressBar		progressBar;
		RelativeLayout	likeContainer;
		ImageView		likeLikeIv;
		RelativeLayout	shareContainer;
		ImageView		shareIv;
		RelativeLayout	remindContainer;
		ImageView		remindLikeIv;
	}

	static class PopularBroadcastsViewHolder {
		TextView		header;
		RelativeLayout	mContainerOne;
		ImageView		mPosterOne;
		TextView		mTitleOne;
		TextView		mTimeOne;
		TextView		mChannelNameOne;
		TextView		mDetailsOne;
		TextView		mProgressBarTitleOne;
		ProgressBar		mProgressBarOne;

		RelativeLayout	mContainerTwo;
		ImageView		mPosterTwo;
		TextView		mTitleTwo;
		TextView		mTimeTwo;
		TextView		mChannelNameTwo;
		TextView		mDetailsTwo;
		TextView		mProgressBarTitleTwo;
		ProgressBar		mProgressBarTwo;

		RelativeLayout	mContainerThree;
		ImageView		mPosterThree;
		TextView		mTitleThree;
		TextView		mTimeThree;
		TextView		mChannelNameThree;
		TextView		mDetailsThree;
		TextView		mProgressBarTitleThree;
		ProgressBar		mProgressBarThree;
	}
}