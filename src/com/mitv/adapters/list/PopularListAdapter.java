
package com.mitv.adapters.list;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class PopularListAdapter 
	extends BaseAdapter 
{
	public static final String TAG = PopularListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;


	
	public PopularListAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts) 
	{
		this.activity = activity;
		
		this.popularBroadcasts = popularBroadcasts;
		
		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	
	@Override
	public int getCount() 
	{
		if (popularBroadcasts != null) 
		{
			return popularBroadcasts.size();
		} 
		else
		{
			return 0;
		}
	}

	
	
	@Override
	public TVBroadcastWithChannelInfo getItem(int position) 
	{
		if (popularBroadcasts != null) 
		{
			return popularBroadcasts.get(position);
		} 
		else
		{
			return null;
		}
	}

	
	
	@Override
	public long getItemId(int position) 
	{
		return -1;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);

		View rowView = convertView;
		
		if (rowView == null) 
		{
			rowView = layoutInflater.inflate(R.layout.element_poster_broadcast, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.headerTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_header_tv);
			viewHolder.dividerView = (View) rowView.findViewById(R.id.element_poster_broadcast_divider);
			viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.element_poster_broadcast_info_container);
			viewHolder.posterIv = (ImageView) rowView.findViewById(R.id.element_poster_broadcast_image_iv);
			viewHolder.titleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_title_tv);
			viewHolder.timeTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_time_tv);
			viewHolder.channelNameTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_channel_tv);
			viewHolder.detailsTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_type_tv);
			viewHolder.progressBarTitleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_timeleft_tv);
			viewHolder.progressBar = (ProgressBar) rowView.findViewById(R.id.element_poster_broadcast_progressbar);
			viewHolder.titleTv.setTag(Integer.valueOf(position));
			
			Log.d(TAG, "set tag: " + Integer.valueOf(position));

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (broadcastWithChannelInfo != null) 
		{
			holder.headerTv.setVisibility(View.GONE);
			holder.dividerView.setVisibility(View.VISIBLE);
			
			if (shouldShowHeader(position, broadcastWithChannelInfo)) 
			{
				StringBuilder headerSB = new StringBuilder();
				
				boolean isBeginTimeTodayOrTomorrow = broadcastWithChannelInfo.isBeginTimeTodayOrTomorrow();
				
				if(isBeginTimeTodayOrTomorrow)
				{
					headerSB.append(DateUtils.buildDayOfTheWeekAsString(broadcastWithChannelInfo.getBeginTimeCalendarLocal(), false));
				}
				else
				{
					headerSB.append(DateUtils.buildDayOfTheWeekAsString(broadcastWithChannelInfo.getBeginTimeCalendarLocal(), false));
					headerSB.append(" ");
					headerSB.append(broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString());
				}

				holder.headerTv.setText(headerSB.toString());
				
				holder.headerTv.setVisibility(View.VISIBLE);
			}

			if (shouldHideDivider(position, broadcastWithChannelInfo)) 
			{
				holder.dividerView.setVisibility(View.GONE);
			}

			holder.container.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					/* Go to the corresponding Broadcast page */
					ContentManager.sharedInstance().getCacheManager().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					
					Intent intent = new Intent(activity, BroadcastPageActivity.class);

					// coloring
					activity.startActivity(intent);
				}
			});

			ImageAware imageAware = new ImageViewAware(holder.posterIv, false);

			ImageSetOrientation imageSetOrientation = broadcastWithChannelInfo.getProgram().getImages();
			
			boolean containsPortraitOrientation = imageSetOrientation.containsPortraitImageSet();
			
			if(containsPortraitOrientation)
			{
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(imageSetOrientation.getPortrait().getImageURLForDeviceDensityDPI(), imageAware);
			}

			holder.timeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

			holder.channelNameTv.setText(broadcastWithChannelInfo.getChannel().getName());

			if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) 
			{
				LanguageUtils.setupProgressBar(activity, broadcastWithChannelInfo, holder.progressBar, holder.progressBarTitleTv);
			} 
			else 
			{
				holder.progressBar.setVisibility(View.GONE);
				holder.progressBarTitleTv.setVisibility(View.GONE);
			}

			ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();

			if (programType == ProgramTypeEnum.TV_EPISODE) 
			{
				holder.titleTv.setText(broadcastWithChannelInfo.getProgram().getSeries().getName());
			} 
			else 
			{
				holder.titleTv.setText(broadcastWithChannelInfo.getProgram().getTitle());
			}

			switch (programType) 
			{
				case TV_EPISODE:
				{
					String season = broadcastWithChannelInfo.getProgram().getSeason().getNumber().toString();
					int episode = broadcastWithChannelInfo.getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += activity.getString(R.string.season) + " " + season + " ";
					}
					if (episode > 0) {
						seasonEpisode += activity.getString(R.string.episode) + " " + episode;
					}
					holder.detailsTv.setText(seasonEpisode);
					break;
				}
				
				case MOVIE: 
				{
					holder.detailsTv.setText(broadcastWithChannelInfo.getProgram().getGenre() + " " + broadcastWithChannelInfo.getProgram().getYear());
					break;
				}
				
				case SPORT: 
				{
					holder.detailsTv.setText(broadcastWithChannelInfo.getProgram().getSportType().getName() + " "
							+ broadcastWithChannelInfo.getProgram().getTournament());
					break;
				}
				
				case OTHER: 
				{
					holder.detailsTv.setText(broadcastWithChannelInfo.getProgram().getCategory());
					break;
				}
				
				default: 
				{
					break;
				}
			}

			holder.container.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					ContentManager.sharedInstance().getCacheManager().pushToSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					
					activity.startActivity(intent);
				}
			});
		}
		
		return rowView;
	}

	
	
	private static class ViewHolder 
	{
		private TextView headerTv;
		private View dividerView;
		private RelativeLayout container;
		private ImageView posterIv;
		private TextView titleTv;
		private TextView timeTv;
		private TextView channelNameTv;
		private TextView detailsTv;
		private TextView progressBarTitleTv;
		private ProgressBar progressBar;
	}
	
	
	
	private boolean shouldShowHeader(int position, TVBroadcastWithChannelInfo broadcastWithChannelInfo) 
	{
		boolean isFirstposition = (position == 0);
		
		boolean isCurrentBroadcastDayEqualToPreviousBroadcastDay;
		
		if (isFirstposition == false)
		{
			TVBroadcastWithChannelInfo previousBroadcastInList = getItem(position - 1);
			
			isCurrentBroadcastDayEqualToPreviousBroadcastDay = broadcastWithChannelInfo.isTheSameDayAs(previousBroadcastInList);
		}
		else
		{
			isCurrentBroadcastDayEqualToPreviousBroadcastDay = false;
		}
		
		if (isFirstposition || isCurrentBroadcastDayEqualToPreviousBroadcastDay == false)
		{
			return true;
		}
		
		return false;
	}
	
	
	
	private boolean shouldHideDivider(int position, TVBroadcastWithChannelInfo broadcastWithChannelInfo) {
		boolean isLastPosition = (position == (getCount() - 1));

		int nextPos = Math.min(position + 1, (getCount() - 1));
		
		TVBroadcastWithChannelInfo broadcastNextPosition = getItem(nextPos);
		
		boolean isBeginTimeEqualToNextItem = broadcastWithChannelInfo.isTheSameDayAs(broadcastNextPosition);
		
		if (isLastPosition == false && isBeginTimeEqualToNextItem == false) 
		{
			return true;
		}
		
		return false;
	}
	
}
