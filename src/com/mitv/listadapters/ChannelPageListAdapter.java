
package com.mitv.listadapters;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ChannelRowTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class ChannelPageListAdapter 
	extends BaseAdapter 
{
	private static final String TAG = ChannelPageListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ViewHolder holder;
	
	private List<TVBroadcast> currentAndUpcomingbroadcasts;
	
	
	
	public ChannelPageListAdapter(final Activity activity, final List<TVBroadcast> broadcasts) 
	{
		this.currentAndUpcomingbroadcasts = broadcasts;
		
		this.activity = activity;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	
	@Override
	public int getCount() 
	{
		int count = 0;
		
		if (currentAndUpcomingbroadcasts != null) 
		{
			count = currentAndUpcomingbroadcasts.size();
		}
		
		return count;
	}

	
	
	@Override
	public TVBroadcast getItem(int position) 
	{
		TVBroadcast broadcast = null;
		
		if (currentAndUpcomingbroadcasts != null)
		{
			broadcast = currentAndUpcomingbroadcasts.get(position);
		}
		
		return broadcast;
	}

	
	
	@Override
	public long getItemId(int arg0) 
	{
		return -1;
	}

	
	
	public void notifyBroadcastEnded() 
	{
		currentAndUpcomingbroadcasts.remove(0);
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		final TVBroadcast broadcast = getItem(position);

		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();
			
			if (getItemViewType(position) == 0) 
			{
				rowView = layoutInflater.inflate(R.layout.row_channelpage_current_list_item, null);
				
				viewHolder.startTime = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_time_tv);
				viewHolder.logo = (ImageView) rowView.findViewById(R.id.channelpage_broadcast_iv);
				viewHolder.title = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_title_tv);
				viewHolder.description = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_text_tv);
				
				/* - The "extra" data fields for the current broadcast.*/
				viewHolder.timeLeft = (TextView) rowView.findViewById(R.id.channelpage_broadcast_timeleft);
				viewHolder.durationProgressBar = (ProgressBar) rowView.findViewById(R.id.channelpage_broadcast_details_progressbar);
				
			} 
			else 
			{
				rowView = layoutInflater.inflate(R.layout.row_channelpage_list_item, null);
				viewHolder.startTime = (TextView) rowView.findViewById(R.id.channelpage_list_item_time_tv);
				viewHolder.title = (TextView) rowView.findViewById(R.id.channelpage_list_item_title_tv);
				viewHolder.description = (TextView) rowView.findViewById(R.id.channelpage_list_item_description_tv);
			}
			
			rowView.setTag(viewHolder);
		}

		holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) 
		{	
			boolean isAiring = false;
			
			if (getItemViewType(position) == 0) 
			{
				ImageAware imageAware = new ImageViewAware(holder.logo, false);
				
				ImageSetOrientation imageSetOrientation = broadcast.getProgram().getImages();
				
				boolean containsLandscapeOrientation = imageSetOrientation.containsLandscapeImageSet();
				
				if(containsLandscapeOrientation)
				{
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(imageSetOrientation.getLandscape().getImageURLForDeviceDensityDPI(), imageAware);
				}
				
				LanguageUtils.setupProgressBar(activity, broadcast, holder.durationProgressBar, holder.timeLeft);
				
				isAiring = true;
			}

			holder.startTime.setText(broadcast.getBeginTimeHourAndMinuteLocalAsString());
			
			StringBuilder titleSB = new StringBuilder();
			
			StringBuilder descriptionSB = new StringBuilder();
			
			if(Constants.ENABLE_POPULAR_BROADCAST_PROCESSING)
			{
				if(broadcast.isPopular())
				{
					String stringIconTrending = activity.getString(R.string.icon_trending);
					
					titleSB.append(stringIconTrending)
						.append(" ");
				}
			}
			
			ProgramTypeEnum programType = broadcast.getProgram().getProgramType();
			
			switch (programType) 
			{
				case MOVIE: 
				{
					titleSB.append(activity.getString(R.string.icon_movie))
						.append(" ");
					
					descriptionSB.append(broadcast.getProgram().getGenre())
						.append(" ")
						.append(broadcast.getProgram().getYear());
					
					break;
				}
				
				case TV_EPISODE: 
				{
					String seasonAndEpisodeString = broadcast.buildSeasonAndEpisodeString();
					
					descriptionSB.append(seasonAndEpisodeString);

					break;
				}
				
				case SPORT:
				{
					if (broadcast.getBroadcastType() == BroadcastTypeEnum.LIVE) 
					{
						titleSB.append(activity.getString(R.string.icon_live))
						.append(" ");
					}
					
					descriptionSB.append(broadcast.getProgram().getSportType().getName())
					.append(": ")
					.append(broadcast.getProgram().getTournament());

					break;
				}
				
				case OTHER: 
				{
					descriptionSB.append(broadcast.getProgram().getCategory());
					
					break;
				}
				
				default: 
				{
					// Do nothing
					break;
				}
			}
			
			titleSB.append(broadcast.getTitle());
			
			holder.title.setText(titleSB.toString());
			holder.description.setText(descriptionSB.toString());
			
			if(isAiring)
			{
				setOnGoingElementsRed();
			}
		}
		else
		{
			Log.w(TAG, "TVBroadcast is null");
		}
			
		return rowView;
	}
	

	
	@Override
	public int getViewTypeCount()
	{
		int viewTypeCount = ChannelRowTypeEnum.class.getEnumConstants().length;
		
		return viewTypeCount;
	}

	
	
	@Override
	public int getItemViewType(int position)
	{
		ChannelRowTypeEnum rowType = ChannelRowTypeEnum.UP_COMING;
		
		TVBroadcast broadcast = getItem(position);
		
		if (broadcast.isBroadcastCurrentlyAiring())
		{
			rowType = ChannelRowTypeEnum.ON_AIR;
		}
		
		return rowType.getId();
	}
	
	
	
	private void setOnGoingElementsRed()
	{
		holder.title.setTextColor(activity.getResources().getColor(R.color.red));
			
		holder.startTime.setTextColor(activity.getResources().getColor(R.color.red));
	}
	
	
	
	private static class ViewHolder 
	{
		private TextView title;
		private TextView description;
		private TextView startTime;
		private TextView timeLeft;
		private ImageView logo;
		private ProgressBar	durationProgressBar;
	}
}
