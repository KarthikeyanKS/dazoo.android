
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
import com.millicom.mitv.enums.ActivityFeedAdapterTypeEnum;
import com.millicom.mitv.enums.FeedItemTypeEnum;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVFeedItem;
import com.millicom.mitv.models.TVProgram;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.utilities.ToastHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.LikeView;
import com.mitv.customviews.ReminderView;
import com.mitv.utilities.ProgressBarUtils;
import com.mitv.utilities.ShareUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class FeedListAdapter 
	extends AdListAdapter<TVFeedItem>
	implements ActivityCallbackListener
{
	private static final String	TAG	= FeedListAdapter.class.getName();

	
	private ArrayList<TVFeedItem> feedItems;
	private LayoutInflater layoutInflater;
	private Activity activity;
	
	
	public FeedListAdapter(Activity activity, ArrayList<TVFeedItem> feedItems) 
	{
		super(Consts.JSON_AND_FRAGMENT_KEY_ACTIVITY, activity, feedItems);
		this.activity = activity;
		this.feedItems = feedItems;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	
	@Override
	public int getViewTypeCount() 
	{
		int viewTypeCount = ActivityFeedAdapterTypeEnum.class.getEnumConstants().length;
		
		return viewTypeCount;
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
					return ActivityFeedAdapterTypeEnum.BROADCAST.getId();
				}
				
				case POPULAR_BROADCAST:
				{
					return ActivityFeedAdapterTypeEnum.POPULAR_BROADCAST.getId();
				}
				
				case POPULAR_BROADCASTS:
				{
					return ActivityFeedAdapterTypeEnum.POPULAR_BROADCASTS.getId();
				}
				
				case POPULAR_TWITTER:
				{
					return ActivityFeedAdapterTypeEnum.POPULAR_TWITTER.getId();
				}
				
				case RECOMMENDED_BROADCAST:
				{
					return ActivityFeedAdapterTypeEnum.RECOMMENDED_BROADCAST.getId();
				}
				
				default:
				case UNKNOWN:
				{
					return ActivityFeedAdapterTypeEnum.ADS.getId();
				}
			}
		} 
		else 
		{
			return ActivityFeedAdapterTypeEnum.ADS.getId();
		}
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
	
	
	
	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
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
				final TVProgram tvProgram = broadcast.getProgram();

				ProgramTypeEnum programType = tvProgram.getProgramType();

				ImageAware imageAware = new ImageViewAware(imageView, false);
				
				ImageLoader.getInstance().displayImage(tvProgram.getImages().getPortrait().getMedium(), imageAware);

				time.setText(broadcast.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

				channelName.setText(broadcast.getChannel().getName());

				switch (programType) 
				{
					case TV_EPISODE:
					{
						Integer season = tvProgram.getSeason().getNumber();

						int episode = tvProgram.getEpisodeNumber();

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
						title.setText(broadcast.getProgram().getSeries().getName());
						break;
					}
					
					case SPORT:
					{
						StringBuilder detailsSB = new StringBuilder();
						detailsSB.append(tvProgram.getSportType().getName());
						detailsSB.append(" ");
						detailsSB.append(tvProgram.getTournament());
						
						details.setText(detailsSB.toString());
						title.setText(broadcast.getProgram().getTitle());
						break;
					}
					
					case MOVIE:
					{
						StringBuilder detailsSB = new StringBuilder();
						detailsSB.append(tvProgram.getGenre());
						detailsSB.append(" ");
						detailsSB.append(tvProgram.getYear());
						
						details.setText(detailsSB.toString());
						title.setText(broadcast.getProgram().getTitle());
						break;
					}

					case OTHER:
					{
						details.setText(broadcast.getProgram().getCategory());
						title.setText(broadcast.getProgram().getTitle());
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

			viewHolder.likeView = (LikeView) rowView.findViewById(R.id.element_social_buttons_like_view);
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
			
			
			String title;
			LikeTypeResponseEnum likeType;
			String contentId;
			
			switch (programType) 
			{
				case TV_EPISODE:
				{
					title = program.getSeries().getName();
					likeType = LikeTypeResponseEnum.SERIES;
					contentId = program.getSeries().getSeriesId();
					
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
					title = program.getSportType().getName();
					likeType = LikeTypeResponseEnum.SPORT_TYPE;
					contentId = program.getSportType().getSportTypeId();
					
					StringBuilder detailsSB = new StringBuilder();
					detailsSB.append(program.getSportType().getName());
					detailsSB.append(" ");
					detailsSB.append(program.getTournament());
					
					holderBC.detailsTv.setText(detailsSB.toString());
					break;
				}
				
				case MOVIE:
				{
					title = program.getTitle();
					likeType = LikeTypeResponseEnum.PROGRAM;
					contentId = program.getProgramId();
					
					StringBuilder detailsSB = new StringBuilder();
					detailsSB.append(program.getGenre());
					detailsSB.append(" ");
					detailsSB.append(program.getYear());
					
					holderBC.titleTv.setText(program.getTitle());
					holderBC.detailsTv.setText(detailsSB.toString());
					break;
				}
				
				case OTHER:
				{
					title = program.getTitle();
					likeType = LikeTypeResponseEnum.PROGRAM;
					contentId = program.getProgramId();
					
					holderBC.titleTv.setText(program.getTitle());
					holderBC.detailsTv.setText(program.getCategory());
					break;
				}
				
				case UNKNOWN:
				default:
				{
					title = program.getTitle();
					likeType = LikeTypeResponseEnum.PROGRAM;
					contentId = program.getProgramId();
					
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

			holderBC.likeView.setBroadcast(broadcast);
			
			holderBC.container.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					popularBroadcastClicked(broadcast);
				}
			});

			holderBC.shareContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					ShareUtils.startShareActivity(
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
		ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
		
		Intent intent = new Intent(activity, BroadcastPageActivity.class);
		
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
		ImageView		likeLikeIv;
		RelativeLayout	shareContainer;
		ImageView		shareIv;
		ReminderView 	reminderView;
		LikeView 		likeView;
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