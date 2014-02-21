package com.mitv.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.activities.PopularPageActivity;
import com.millicom.mitv.enums.FeedItemTypeEnum;
import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVFeedItem;
import com.millicom.mitv.models.gson.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.ReminderView;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.ProgressBarUtils;
import com.mitv.utilities.ShareUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class ActivityFeedAdapter extends AdListAdapter<TVFeedItem> {

	private static final String		TAG									= "ActivityFeedAdapter";
	private Activity				mActivity;
	private ArrayList<TVFeedItem>		mFeedItems;
	private LayoutInflater			mLayoutInflater;

	private int						ACTIVITY_BLOCKS_TYPE_NUMBER	= 5;

	private static final int		ITEM_TYPE_BROADCAST					= 1; //0 is reserved for ads
	private static final int		ITEM_TYPE_RECOMMENDED_BROADCAST		= 2;
	private static final int		ITEM_TYPE_POPULAR_BROADCASTS		= 3;
	private static final int		ITEM_TYPE_POPULAR_TWITTER			= 4;
	private static final int		ITEM_TYPE_POPULAR_BROADCAST			= 5;

	private ArrayList<String>		mLikeIds;
	private int						currentPosition						= -1;

	// private ImageView likeLikeIv, remindLikeIv, likeRecIv, remindRecIv, likeTwitterIv, remindTwitterIv;
	private boolean					mIsLiked							= false, mIsSet = false;

	public ActivityFeedAdapter(Activity activity, ArrayList<TVFeedItem> feedItems) {
		super(Consts.JSON_AND_FRAGMENT_KEY_ACTIVITY, activity, feedItems);
		this.mActivity = activity;
		this.mFeedItems = feedItems;
		//TODO fetch likes using contentmanager
//		this.mLikeIds = LikeService.getLikeIdsList();
		this.mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getViewTypeCount() {
		return super.getViewTypeCount() + ACTIVITY_BLOCKS_TYPE_NUMBER;
	}

	public void addItem(final TVFeedItem item) {
		mFeedItems.add(item);
		notifyDataSetChanged();
	}

	public void addItems(ArrayList<TVFeedItem> items) {
		mFeedItems.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		TVFeedItem item = getItem(position);
		if (item != null) {
//			String feedItemType = item.getItemType();
			FeedItemTypeEnum feedItemType = item.getItemType();
			//TODO use switch on feedItemType instead
			
			if (Consts.FEED_ITEM_TYPE_POPULAR_BROADCASTS.equals(feedItemType)) {
				return ITEM_TYPE_POPULAR_BROADCASTS;
			} 
			else if (Consts.FEED_ITEM_TYPE_BROADCAST.equals(feedItemType)) {
				return ITEM_TYPE_BROADCAST;
			} 
			else if (Consts.FEED_ITEM_TYPE_RECOMMENDED_BROADCAST.equals(feedItemType)) {
				return ITEM_TYPE_RECOMMENDED_BROADCAST;
			} 
			else if (Consts.FEED_ITEM_TYPE_POPULAR_TWITTER.equals(feedItemType)) {
				return ITEM_TYPE_POPULAR_TWITTER;
			} 
			else if (Consts.FEED_ITEM_TYPE_POPULAR_BROADCAST.equals(feedItemType)) {
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

	public void populatePopularItemAtIndex(PopularBroadcastsViewHolder viewHolder, ArrayList<TVBroadcastWithChannelInfo> broadcasts, int popularRowIndex) {
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
			final TVBroadcastWithChannelInfo broadcast = broadcasts.get(popularRowIndex);
			if (broadcast != null) 
			{
				final TVProgram program = broadcast.getProgram();

//				String programType = broadcast.getProgram().getProgramType();
				ProgramTypeEnum programType = broadcast.getProgram().getProgramType();

				ImageAware imageAware = new ImageViewAware(imageView, false);
				ImageLoader.getInstance().displayImage(broadcast.getProgram().getImages().getPortrait().getMedium(), imageAware);

				if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					title.setText(broadcast.getProgram().getSeries().getName());
				} 
				else {
					title.setText(broadcast.getProgram().getTitle());
				}

				time.setText(broadcast.getDayOfWeekWithTimeString());

				channelName.setText(broadcast.getChannel().getName());

				if (programType != null) {
					if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
						details.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
					} 
					else if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						if (broadcast != null) {
							if (program != null) {
								String season = program.getSeason().getNumber().toString();
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
					else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
						details.setText(broadcast.getProgram().getSportType().getName() + " " + broadcast.getProgram().getTournament());
					} 
					else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) {
						details.setText(broadcast.getProgram().getCategory());
					}
				}


				progressBar.setVisibility(View.GONE);
				progressTextView.setVisibility(View.GONE);

				if (broadcast.isBroadcastCurrentlyAiring()) {
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

	private void popularBroadcastClicked(TVBroadcastWithChannelInfo broadcast) 
	{
		Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillis());
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId().getChannelId());
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getBeginTimeDateRepresentation());
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
	
	private View populateSingleBroadcastCell(View rowView, final TVBroadcastWithChannelInfo broadcast, int position, TVFeedItem feedItem) 
	{
		final TVProgram program = broadcast.getProgram();
		
		if (rowView == null) 
		{
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
			viewHolder.reminderView = (ReminderView) rowView.findViewById(R.id.element_social_buttons_reminder);

			viewHolder.container.setTag(Integer.valueOf(position));
			rowView.setTag(viewHolder);
		}

		final BroadcastViewHolder holderBC = (BroadcastViewHolder) rowView.getTag();
	
		if(holderBC != null) {
			holderBC.reminderView.setBroadcast(broadcast);

			final ProgramTypeEnum programType = program.getProgramType();
			// determine like
			if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				mIsLiked = MiTVStore.getInstance().isInTheLikesList(program.getSeries().getSeriesId());
			} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
				mIsLiked = MiTVStore.getInstance().isInTheLikesList(program.getSportType().getSportTypeId());
			} else {
				mIsLiked = MiTVStore.getInstance().isInTheLikesList(program.getProgramId());
			}

			if (ITEM_TYPE_POPULAR_TWITTER == getItemViewType(position)) {
				holderBC.headerTv.setText(mActivity.getResources().getString(R.string.icon_twitter) + " " + feedItem.getTitle());
			} else {
				holderBC.headerTv.setText(feedItem.getTitle());
			}

			ImageAware imageAware = new ImageViewAware(holderBC.landscapeIv, false);
			ImageLoader.getInstance().displayImage(program.getImages().getLandscape().getLarge(), imageAware);

			if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				holderBC.titleTv.setText(program.getSeries().getName());
			} else {
				holderBC.titleTv.setText(program.getTitle());
			}

			holderBC.timeTv.setText(broadcast.getDayOfWeekWithTimeString());
			holderBC.channelTv.setText(broadcast.getChannel().getName());

			if (programType != null) {
				if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
					holderBC.detailsTv.setText(program.getGenre() + " " + program.getYear());
				} else if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					String season = program.getSeason().getNumber().toString();
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
				} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
					holderBC.detailsTv.setText(program.getSportType().getName() + " " + program.getTournament());
				} else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) {
					holderBC.detailsTv.setText(program.getCategory());
				}
			}

			holderBC.container.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillis());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, broadcast.getBeginTimeDateRepresentation());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);

					mActivity.startActivityForResult(intent, 0);
				}
			});

			if (broadcast.isBroadcastCurrentlyAiring()) {
				ProgressBarUtils.setupProgressBar(mActivity, broadcast, holderBC.progressBar, holderBC.progressbarTv);
			} else {
				holderBC.progressbarTv.setVisibility(View.GONE);
				holderBC.progressBar.setVisibility(View.GONE);
			}

			if (mIsLiked) {
				holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
			} else {
				holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));
			}

			holderBC.likeContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					//TOOD convert from program type to LikeType
					LikeTypeResponseEnum likeType;// = LikeService.getLikeType(programType);

					String programId, contentTitle;
					if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
						programId = program.getSeries().getSeriesId();
						contentTitle = program.getSeries().getName();
					} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
						programId = program.getSportType().getSportTypeId();
						contentTitle = program.getSportType().getName();
					} else {
						programId = program.getProgramId();
						contentTitle = program.getTitle();
					}

					if (mIsLiked == false) {
						//TODO use ContentManager architecture for likes
//						if (LikeService.addLike(programId, likeType)) {
//							MiTVStore.getInstance().addLikeIdToList(programId);
//
//							ActivityActivity.toast = LikeService.showSetLikeToast(mActivity, contentTitle);
//							holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_selected));
//
//							AnimationUtilities.animationSet(holderBC.likeLikeIv);
//
//							mIsLiked = true;
//						} else {
//							// Toast.makeText(mActivity,
//							// "Adding a like faced an error",
//							// Toast.LENGTH_SHORT).show();
//							Log.d(TAG, "!!! Adding a like faced an error !!!");
//						}
					} else {

						//TODO use ContentManager architecture for likes
//						LikeService.removeLike(likeType, programId);
//						MiTVStore.getInstance().deleteLikeIdFromList(programId);
//
//						mIsLiked = false;
//						holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

					}
				}
			});

			holderBC.shareContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ShareUtils.shareAction(mActivity, mActivity.getResources().getString(R.string.app_name), broadcast.getShareUrl(), mActivity.getResources()
							.getString(R.string.share_action_title));
				}
			});
		}
		return rowView;
	}
	
	private View populateMultipleBroadcastsCell(View rowView, ArrayList<TVBroadcastWithChannelInfo> broadcasts) {
		if (rowView == null) {
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
			viewHolder.mContainerTwo = elementTwo;// (LinearLayout)
													// elementTwo.findViewById(R.id.block_popular_feed_container);
			viewHolder.mPosterTwo = (ImageView) elementTwo.findViewById(R.id.element_popular_list_item_image_iv);
			viewHolder.mTitleTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_title_tv);
			viewHolder.mTimeTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_time_tv);
			viewHolder.mChannelNameTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_channel_tv);
			viewHolder.mDetailsTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_type_tv);
			viewHolder.mProgressBarTitleTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_timeleft_tv);
			viewHolder.mProgressBarTwo = (ProgressBar) elementTwo.findViewById(R.id.element_popular_list_item_progressbar);

			// three
			RelativeLayout elementThree = (RelativeLayout) rowView.findViewById(R.id.block_popular_element_three);
			viewHolder.mContainerThree = elementThree;// (LinearLayout)
														// elementThree.findViewById(R.id.block_popular_feed_container);
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

		return rowView;
	}

	public View getViewForFeedItemCell(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		int type = getItemViewType(position);

		final TVFeedItem feedItem = getItem(position);

		switch (type) {
		case ITEM_TYPE_RECOMMENDED_BROADCAST:
		case ITEM_TYPE_POPULAR_TWITTER:
		case ITEM_TYPE_POPULAR_BROADCAST:
		case ITEM_TYPE_BROADCAST:
			/* One broadcast */
			final TVBroadcastWithChannelInfo broadcast = feedItem.getBroadcast();

			if (broadcast != null) {
				rowView = populateSingleBroadcastCell(rowView, broadcast, position, feedItem);
			}
			break;

		case ITEM_TYPE_POPULAR_BROADCASTS:

			final ArrayList<TVBroadcastWithChannelInfo> broadcasts = new ArrayList<TVBroadcastWithChannelInfo>(feedItem.getBroadcasts());

			if (broadcasts != null && broadcasts.size() > 0) {
				rowView = populateMultipleBroadcastsCell(rowView, broadcasts);
			}
			break;

		}

		if (rowView == null) {
			/* No content */
			rowView = LayoutInflater.from(mActivity).inflate(R.layout.no_data, null);
		}
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
		ReminderView reminderView;
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
