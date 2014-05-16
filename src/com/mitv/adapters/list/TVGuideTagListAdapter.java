
package com.mitv.adapters.list;



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

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.BannerViewType;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class TVGuideTagListAdapter 
	extends BannerListAdapter<TVBroadcastWithChannelInfo> 
{
	private static final String TAG = TVGuideTagListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	private int currentPosition;

	
	
	public TVGuideTagListAdapter(
			final Activity activity, 
			final String fragmentName, 
			final ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts, 
			final int currentPosition,
			final boolean showCompetitionsBanner) 
	{
		super(fragmentName, activity, taggedBroadcasts, Constants.AD_UNIT_ID_GUIDE_ACTIVITY, showCompetitionsBanner);
		
		this.taggedBroadcasts = taggedBroadcasts;
		
		this.activity = activity;
		
		this.currentPosition = currentPosition;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Log.d(TAG, "Fetching index " + position);
		
		/* Superclass AdListAdapter will create view if this is a position of an ad. */
		View rowView = super.getView(position, convertView, parent);

		if (rowView == null)
		{
			BannerViewType viewType = getBannerViewType(position);
			
			switch (viewType) 
			{
				case BANNER_VIEW_TYPE_STANDARD: 
				{
					rowView = getViewForBroadCastCell(position, convertView, parent);
					break;
				}
				
				default:
				{
					// Do nothing
					break;
				}
			}
		}

		return rowView;
	}

	
	
	private View getViewForBroadCastCell(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		// Get the item with the displacement depending on the scheduled time on air
		int indexForBroadcast = currentPosition + position;
		
		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(indexForBroadcast);

		if (rowView == null) 
		{
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			rowView = layoutInflater.inflate(R.layout.element_poster_broadcast, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.element_poster_broadcast_container);
			viewHolder.mImageIv = (ImageView) rowView.findViewById(R.id.element_poster_broadcast_image_iv);
			viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_title_tv);
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_time_tv);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_channel_tv);
			viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_type_tv);
			viewHolder.mTimeLeftTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_timeleft_tv);
			viewHolder.mDurationPb = (ProgressBar) rowView.findViewById(R.id.element_poster_broadcast_progressbar);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcastWithChannelInfo != null) 
		{
			if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) 
			{
				LanguageUtils.setupProgressBar(activity, broadcastWithChannelInfo, holder.mDurationPb, holder.mTimeLeftTv);
			} 
			else 
			{
				holder.mDurationPb.setVisibility(View.GONE);
				holder.mTimeLeftTv.setVisibility(View.GONE);
			}

			ImageAware imageAware = new ImageViewAware(holder.mImageIv, false);
			
			ImageSetOrientation imageSetOrientation = broadcastWithChannelInfo.getProgram().getImages();
			
			boolean containsPortraitOrientation = imageSetOrientation.containsPortraitImageSet();
			
			if(containsPortraitOrientation)
			{
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getImageURLForDeviceDensityDPI(), imageAware);
			}
			
			holder.mTimeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			holder.mChannelTv.setText(broadcastWithChannelInfo.getChannel().getName());

			StringBuilder titleSB = new StringBuilder();
			
			StringBuilder descriptionSB = new StringBuilder();
			
			if(Constants.ENABLE_POPULAR_BROADCAST_PROCESSING)
			{
				if(broadcastWithChannelInfo.isPopular())
				{
					String stringIconTrending = activity.getString(R.string.icon_trending);
					
					titleSB.append(stringIconTrending)
					.append(" ");
				}
			}
			
			ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();

			switch (programType) 
			{
				case MOVIE: 
				{
					titleSB.append(activity.getString(R.string.icon_movie))
					.append(" ");
					
					holder.mTitleTv.setText(titleSB.toString());
					
					descriptionSB.append(broadcastWithChannelInfo.getProgram().getGenre())
					.append(" ")
					.append(broadcastWithChannelInfo.getProgram().getYear());
					
					break;
				}
				
				case TV_EPISODE: 
				{
					String seasonAndEpisodeString = broadcastWithChannelInfo.buildSeasonAndEpisodeString();
					
					descriptionSB.append(seasonAndEpisodeString);
					
					break;
				}
				
				case SPORT: 
				{
					if (broadcastWithChannelInfo.getBroadcastType() == BroadcastTypeEnum.LIVE) 
					{
						titleSB.append(activity.getString(R.string.icon_live))
						.append(" ");
					}
	
					descriptionSB.append(broadcastWithChannelInfo.getProgram().getSportType().getName())
					.append(": ")
					.append(broadcastWithChannelInfo.getProgram().getTournament());
					
					break;
				}
				
				case OTHER: 
				{
					if (broadcastWithChannelInfo.getBroadcastType() == BroadcastTypeEnum.LIVE) 
					{
						titleSB.append(activity.getString(R.string.icon_live))
						.append(" ");
					}
					
					descriptionSB.append(broadcastWithChannelInfo.getProgram().getCategory());
					
					break;
				}
				
				default: 
				{
					// Do nothing
					break;
				}
			}
			
			titleSB.append(broadcastWithChannelInfo.getTitle());
			
			holder.mTitleTv.setText(titleSB.toString());
			holder.mDescTv.setText(descriptionSB.toString());
		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(activity, BroadcastPageActivity.class);
				
				ContentManager.sharedInstance().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
				
				activity.startActivity(intent);
			}
		});
		
		return rowView;
	}

	
	
	private static class ViewHolder 
	{
		private RelativeLayout mContainer;
		private ImageView mImageIv;
		private TextView mTitleTv;
		private TextView mTimeTv;
		private TextView mChannelTv;
		private TextView mDescTv;
		private TextView mTimeLeftTv;
		private ProgressBar mDurationPb;
	}

	
	
	@Override
	public int getCount() 
	{
		if (taggedBroadcasts != null) 
		{
			return taggedBroadcasts.size() - currentPosition;
		} 
		else 
		{
			return 0;
		}
	}

	
	
	@Override
	public TVBroadcastWithChannelInfo getItem(int position) 
	{
		if (taggedBroadcasts != null) 
		{
			return taggedBroadcasts.get(position);
		} 
		else 
		{
			return null;
		}
	}

	
	
	@Override
	public long getItemId(int arg0) 
	{
		return -1;
	}
}
