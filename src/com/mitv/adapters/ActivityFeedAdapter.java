
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

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.activities.PopularPageActivity;
import com.millicom.mitv.enums.FeedItemTypeEnum;
import com.millicom.mitv.enums.FeedItemViewTypeEnum;
import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVFeedItem;
import com.millicom.mitv.models.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.ReminderView;
import com.mitv.storage.MiTVStore;
import com.mitv.utilities.ProgressBarUtils;
import com.mitv.utilities.ShareUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class ActivityFeedAdapter 
	extends AdListAdapter<TVFeedItem> 
{
	private static final String	TAG	= ActivityFeedAdapter.class.getName();

	
	private static final int ACTIVITY_BLOCKS_TYPE_NUMBER = 5;
	
	private Activity activity;
	private ArrayList<TVFeedItem> feedItems;
	private LayoutInflater layoutInflater;

	

	
	
	public ActivityFeedAdapter(Activity activity, ArrayList<TVFeedItem> feedItems) 
	{
		super(Consts.JSON_AND_FRAGMENT_KEY_ACTIVITY, activity, feedItems);
		
		this.activity = activity;
		
		this.feedItems = feedItems;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	
	@Override
	public int getViewTypeCount() 
	{
		return super.getViewTypeCount() + ACTIVITY_BLOCKS_TYPE_NUMBER;
	}
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		/* Superclass AdListAdapter will create view if this is a position of an ad. */
		View rowView = super.getView(position, convertView, parent);

		if (rowView == null) 
		{
			rowView = getViewForFeedItemCell(position, convertView, parent);
		}

		return rowView;
	}

	
	
	public void addItem(final TVFeedItem item) 
	{
		feedItems.add(item);
		notifyDataSetChanged();
	}

	
	
	public void addItems(ArrayList<TVFeedItem> items) 
	{
		feedItems.addAll(items);
		notifyDataSetChanged();
	}

	
	
	@Override
	public int getItemViewType(int position) 
	{
		final TVFeedItem tvFeedItem = getItem(position);
		
		if (tvFeedItem != null) 
		{
			FeedItemTypeEnum tvFeedItemType = tvFeedItem.getItemType();
			
			switch(tvFeedItemType)
			{
				case BROADCAST:
				{
					return FeedItemViewTypeEnum.BROADCAST.getId();
				}
				
				case POPULAR_BROADCAST:
				{
					return FeedItemViewTypeEnum.POPULAR_BROADCAST.getId();
				}
				
				case POPULAR_BROADCASTS:
				{
					return FeedItemViewTypeEnum.POPULAR_BROADCASTS.getId();
				}
				
				case POPULAR_TWITTER:
				{
					return FeedItemViewTypeEnum.POPULAR_TWITTER.getId();
				}
				
				case RECOMMENDED_BROADCAST:
				{
					return FeedItemViewTypeEnum.RECOMMENDED_BROADCAST.getId();
				}
				
				default:
				case UNKNOWN:
				{
					return FeedItemViewTypeEnum.UNKNOWN.getId();
				}
			}
		} 
		else 
		{
			return super.getItemViewType(position);
		}
	}
	
	

	public void populatePopularItemAtIndex(
			PopularBroadcastsViewHolder viewHolder, 
			ArrayList<TVBroadcastWithChannelInfo> broadcasts, 
			int popularRowIndex) 
	{
		ImageView imageView = null;
		TextView title = null;
		TextView time = null;
		TextView channelName = null;
		TextView details = null;
		TextView progressTextView = null;
		ProgressBar progressBar = null;
		RelativeLayout containerLayout = null;

		switch (popularRowIndex) 
		{
			case 0: 
			{
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
			case 1: 
			{
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
			case 2: 
			{
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

		if (popularRowIndex < broadcasts.size()) 
		{
			final TVBroadcastWithChannelInfo broadcast = broadcasts.get(popularRowIndex);
			
			if (broadcast != null) 
			{
				final TVProgram program = broadcast.getProgram();

				ProgramTypeEnum programType = program.getProgramType();

				ImageAware imageAware = new ImageViewAware(imageView, false);
				ImageLoader.getInstance().displayImage(broadcast.getProgram().getImages().getPortrait().getMedium(), imageAware);

				if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) 
				{
					title.setText(broadcast.getProgram().getSeries().getName());
				} 
				else 
				{
					title.setText(broadcast.getProgram().getTitle());
				}

				time.setText(broadcast.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

				channelName.setText(broadcast.getChannel().getName());

				switch (programType) 
				{
					case TV_EPISODE:
					{
						Integer season = program.getSeason().getNumber();

						int episode = program.getEpisodeNumber();

						StringBuilder seasonEpisode = new StringBuilder();
						
						if(season.intValue() != 0)
						{
							seasonEpisode.append(activity.getResources().getString(R.string.season));
							seasonEpisode.append(" ");
							seasonEpisode.append(season);
							seasonEpisode.append(" ");
						}
						
						if(episode > 0)
						{
							seasonEpisode.append(activity.getResources().getString(R.string.episode));
							seasonEpisode.append(" ");
							seasonEpisode.append(episode);
						}
						
						details.setText(seasonEpisode.toString());
						break;
					}
					
					case SPORT:
					{
						StringBuilder detailsSB = new StringBuilder();
						detailsSB.append(program.getSportType().getName());
						detailsSB.append(" ");
						detailsSB.append(program.getTournament());
						
						details.setText(detailsSB.toString());
						break;
					}
					
					case MOVIE:
					{
						StringBuilder detailsSB = new StringBuilder();
						detailsSB.append(program.getGenre());
						detailsSB.append(" ");
						detailsSB.append(program.getYear());
						
						details.setText(detailsSB.toString());
						break;
					}

					case OTHER:
					{
						details.setText(broadcast.getProgram().getCategory());
						break;
					}
					
					default:
					case UNKNOWN:
					{
						Log.w(TAG, "Unknown program type.");
						break;
					}
				}

				progressBar.setVisibility(View.GONE);
				progressTextView.setVisibility(View.GONE);

				if (broadcast.isBroadcastCurrentlyAiring()) 
				{
					ProgressBarUtils.setupProgressBar(activity, broadcast, progressBar, progressTextView);
				} 

				containerLayout.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						popularBroadcastClicked(broadcast);
					}
				});
			}
		}
		else 
		{
			containerLayout.setVisibility(View.GONE);
		}
	}


	
	private View getViewForFeedItemCell(
			int position, 
			View convertView, 
			ViewGroup parent) 
	{
		View rowView = convertView;

		TVFeedItem feedItem = getItem(position);

		FeedItemTypeEnum feedType = feedItem.getItemType();
		
		switch (feedType) 
		{
			case BROADCAST:
			case POPULAR_BROADCAST:
			case POPULAR_TWITTER:
			case RECOMMENDED_BROADCAST:
			{
				TVBroadcastWithChannelInfo broadcast = feedItem.getBroadcast();
				
				if (broadcast != null) 
				{
					rowView = populateSingleBroadcastCell(rowView, broadcast, position, feedItem);
				}
				break;
			}
			
			case POPULAR_BROADCASTS:
			{
				ArrayList<TVBroadcastWithChannelInfo> broadcasts = new ArrayList<TVBroadcastWithChannelInfo>(feedItem.getBroadcasts());
				
				if (broadcasts != null && broadcasts.size() > 0) 
				{
					rowView = populateMultipleBroadcastsCell(rowView, broadcasts);
				}
				break;
			}
			
			default:
			case UNKNOWN:
			{
				Log.w(TAG, "Unknown feed type.");
				break;
			}
		}

		if (rowView == null) 
		{
			rowView = LayoutInflater.from(activity).inflate(R.layout.no_data, null);
		}
		
		return rowView;
	}
	
	
	
	private View populateSingleBroadcastCell(
			View rowView, 
			final TVBroadcastWithChannelInfo broadcast, 
			int position, 
			final TVFeedItem feedItem) 
	{
		final TVProgram program = broadcast.getProgram();
		
		if (rowView == null) 
		{
			BroadcastViewHolder viewHolder = new BroadcastViewHolder();

			rowView = layoutInflater.inflate(R.layout.block_feed_liked, null);

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
	
		if(holderBC != null) 
		{
			holderBC.reminderView.setBroadcast(broadcast);

			final ProgramTypeEnum programType = program.getProgramType();
			
			final boolean isLikedByUser;
			
			switch (programType) 
			{
				case TV_EPISODE:
				{
					isLikedByUser = MiTVStore.getInstance().isInTheLikesList(program.getSeries().getSeriesId());
					
					Integer season = program.getSeason().getNumber();;
					int episode = program.getEpisodeNumber();

					StringBuilder seasonEpisode = new StringBuilder();

					if(season.intValue() != 0)
					{
						seasonEpisode.append(activity.getResources().getString(R.string.season));
						seasonEpisode.append(" ");
						seasonEpisode.append(season);
						seasonEpisode.append(" ");
					}
					
					if(episode > 0)
					{
						seasonEpisode.append(activity.getResources().getString(R.string.episode));
						seasonEpisode.append(" ");
						seasonEpisode.append(episode);
					}
					
					if(season.intValue() == 0 && episode == 0)
					{
						holderBC.detailsTv.setVisibility(View.GONE);
					}
					
					holderBC.titleTv.setText(program.getSeries().getName());
					holderBC.detailsTv.setText(seasonEpisode.toString());
					break;
				}
				
				case SPORT:
				{
					isLikedByUser = MiTVStore.getInstance().isInTheLikesList(program.getSportType().getSportTypeId());
					
					StringBuilder detailsSB = new StringBuilder();
					detailsSB.append(program.getSportType().getName());
					detailsSB.append(" ");
					detailsSB.append(program.getTournament());
					
					holderBC.detailsTv.setText(detailsSB.toString());
					break;
				}
				
				case OTHER:
				{
					isLikedByUser = MiTVStore.getInstance().isInTheLikesList(program.getProgramId());
					
					holderBC.titleTv.setText(program.getTitle());
					holderBC.detailsTv.setText(program.getCategory());
					break;
				}
				
				case MOVIE:
				{
					isLikedByUser = MiTVStore.getInstance().isInTheLikesList(program.getProgramId());
					
					StringBuilder detailsSB = new StringBuilder();
					detailsSB.append(program.getGenre());
					detailsSB.append(" ");
					detailsSB.append(program.getYear());
					
					holderBC.titleTv.setText(program.getTitle());
					holderBC.detailsTv.setText(detailsSB.toString());
					break;
				}
				
				case UNKNOWN:
				default:
				{
					isLikedByUser = false;
					
					Log.w(TAG, "Unknown program type.");
					break;
				}
			}
			
			FeedItemTypeEnum feedItemType = feedItem.getItemType();
			
			switch (feedItemType) 
			{
				case POPULAR_TWITTER:
				{
					holderBC.headerTv.setText(activity.getResources().getString(R.string.icon_twitter) + " " + feedItem.getTitle());
					break;
				}
				
				default:
				{
					holderBC.headerTv.setText(feedItem.getTitle());
				}
			}
			
			ImageAware imageAware = new ImageViewAware(holderBC.landscapeIv, false);
			
			ImageLoader.getInstance().displayImage(program.getImages().getLandscape().getLarge(), imageAware);

			holderBC.timeTv.setText(broadcast.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			holderBC.channelTv.setText(broadcast.getChannel().getName());

			if(broadcast.isBroadcastCurrentlyAiring()) 
			{
				ProgressBarUtils.setupProgressBar(activity, broadcast, holderBC.progressBar, holderBC.progressbarTv);
			}
			else
			{
				holderBC.progressbarTv.setVisibility(View.GONE);
				holderBC.progressBar.setVisibility(View.GONE);
			}

			if (isLikedByUser)
			{
				holderBC.likeLikeIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_like_selected));
			}
			else
			{
				holderBC.likeLikeIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_like_default));
			}
			
			holderBC.container.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					popularBroadcastClicked(broadcast);
				}
			});

			holderBC.likeContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					//TOOD convert from program type to LikeType
					LikeTypeResponseEnum likeType;// = LikeService.getLikeType(programType);

					String programId, contentTitle;
					
					final ProgramTypeEnum programType = program.getProgramType();
					
					switch (programType) 
					{
						case TV_EPISODE:
						{
							programId = program.getSeries().getSeriesId();
							contentTitle = program.getSeries().getName();
							break;
						}
						
						case SPORT:
						{
							programId = program.getSportType().getSportTypeId();
							contentTitle = program.getSportType().getName();
							break;
						}
						
						default:
						{
							programId = program.getProgramId();
							contentTitle = program.getTitle();
						}
					}

					if (isLikedByUser == false) 
					{
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
					} 
					else 
					{
						//TODO use ContentManager architecture for likes
//						LikeService.removeLike(likeType, programId);
//						MiTVStore.getInstance().deleteLikeIdFromList(programId);
//
//						mIsLiked = false;
//						holderBC.likeLikeIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_like_default));

					}
				}
			});

			holderBC.shareContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					ShareUtils.shareAction(
							activity, 
							activity.getResources().getString(R.string.app_name), 
							broadcast.getShareUrl(),
							activity.getResources().getString(R.string.share_action_title));
				}
			});
		}
		
		return rowView;
	}
	
	
	
	private View populateMultipleBroadcastsCell(View rowView, ArrayList<TVBroadcastWithChannelInfo> broadcasts)
	{
		if (rowView == null) 
		{
			PopularBroadcastsViewHolder viewHolder = new PopularBroadcastsViewHolder();
			
			rowView = layoutInflater.inflate(R.layout.block_feed_popular, null);

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
			viewHolder.mContainerTwo = elementTwo;
			viewHolder.mPosterTwo = (ImageView) elementTwo.findViewById(R.id.element_popular_list_item_image_iv);
			viewHolder.mTitleTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_title_tv);
			viewHolder.mTimeTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_time_tv);
			viewHolder.mChannelNameTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_channel_tv);
			viewHolder.mDetailsTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_type_tv);
			viewHolder.mProgressBarTitleTwo = (TextView) elementTwo.findViewById(R.id.element_popular_list_item_timeleft_tv);
			viewHolder.mProgressBarTwo = (ProgressBar) elementTwo.findViewById(R.id.element_popular_list_item_progressbar);

			// three
			RelativeLayout elementThree = (RelativeLayout) rowView.findViewById(R.id.block_popular_element_three);
			viewHolder.mContainerThree = elementThree;
			viewHolder.mPosterThree = (ImageView) elementThree.findViewById(R.id.element_popular_list_item_image_iv);
			viewHolder.mTitleThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_title_tv);
			viewHolder.mTimeThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_time_tv);
			viewHolder.mChannelNameThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_channel_tv);
			viewHolder.mDetailsThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_type_tv);
			viewHolder.mProgressBarTitleThree = (TextView) elementThree.findViewById(R.id.element_popular_list_item_timeleft_tv);
			viewHolder.mProgressBarThree = (ProgressBar) elementThree.findViewById(R.id.element_popular_list_item_progressbar);

			rowView.setTag(viewHolder);
		}

		final PopularBroadcastsViewHolder popularBroadcastsViewHolder = (PopularBroadcastsViewHolder) rowView.getTag();

		populatePopularItemAtIndex(popularBroadcastsViewHolder, broadcasts, 0);
		populatePopularItemAtIndex(popularBroadcastsViewHolder, broadcasts, 1);
		populatePopularItemAtIndex(popularBroadcastsViewHolder, broadcasts, 2);

		RelativeLayout footer = (RelativeLayout) rowView.findViewById(R.id.block_popular_show_more_container);
		
		footer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(activity, PopularPageActivity.class);
				
				activity.startActivity(intent);
			}
		});

		return rowView;
	}
	
	
	
	private void popularBroadcastClicked(TVBroadcastWithChannelInfo broadcastWithChannelInfo) 
	{
		Intent intent = new Intent(activity, BroadcastPageActivity.class);
		
		ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);

		intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
		intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, false);

		activity.startActivity(intent);
	}
	

	
	private static class BroadcastViewHolder 
	{
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

	
	
	private static class PopularBroadcastsViewHolder 
	{
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