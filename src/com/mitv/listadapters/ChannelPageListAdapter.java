
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

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ChannelRowTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
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
	
	private List<TVBroadcast> currentAndUpcomingbroadcasts;
	private ViewHolder holder;
	private boolean isAiring = false;

	
	
	public ChannelPageListAdapter(Activity activity, List<TVBroadcast> currentAndUpcomingbroadcasts) 
	{
		this.currentAndUpcomingbroadcasts = currentAndUpcomingbroadcasts;
		this.activity = activity;

		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() 
	{
		int count = 0;
		if (currentAndUpcomingbroadcasts != null) {
			count = currentAndUpcomingbroadcasts.size();
		}
		return count;
	}

	@Override
	public TVBroadcast getItem(int position) {
		TVBroadcast broadcast = null;
		if (currentAndUpcomingbroadcasts != null) {
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
			if (getItemViewType(position) == 0) 
			{
				ImageAware imageAware = new ImageViewAware(holder.logo, false);
				
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(broadcast.getProgram().getImages().getLandscape().getLarge(), imageAware);
				
				LanguageUtils.setupProgressBar(activity, broadcast, holder.durationProgressBar, holder.timeLeft);
				
				isAiring = true;
			}

			holder.startTime.setText(broadcast.getBeginTimeHourAndMinuteLocalAsString());
			
			ProgramTypeEnum programType = broadcast.getProgram().getProgramType();
			
			switch (programType) 
			{
				case MOVIE: 
				{
					StringBuilder titleSB = new StringBuilder();
					
					titleSB.append(activity.getString(R.string.icon_movie))
					.append(" ")
					.append(broadcast.getTitle());
					
					holder.title.setText(titleSB.toString());
					
					holder.description.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
					break;
				}
				
				case TV_EPISODE: 
				{
					holder.title.setText(broadcast.getTitle());
					
					String seasonAndEpisodeString = broadcast.buildSeasonAndEpisodeString();
					
					holder.description.setText(seasonAndEpisodeString);
					break;
				}
				
				case SPORT:
				{
					if (broadcast.getBroadcastType() == BroadcastTypeEnum.LIVE) 
					{
						StringBuilder titleSB = new StringBuilder();
						
						titleSB.append(activity.getString(R.string.icon_live))
						.append(" ")
						.append(broadcast.getTitle());
						
						holder.title.setText(titleSB.toString());
					}
					else 
					{
						holder.title.setText(broadcast.getTitle());
					}
					
					StringBuilder descriptionSB = new StringBuilder();
					
					descriptionSB.append(broadcast.getProgram().getSportType().getName())
					.append(": ")
					.append(broadcast.getProgram().getTournament());
	
					holder.description.setText(descriptionSB.toString());
					break;
				}
				
				case OTHER: 
				{
					if (broadcast.getBroadcastType() == BroadcastTypeEnum.LIVE) 
					{
						StringBuilder titleSB = new StringBuilder();
						
						titleSB.append(activity.getString(R.string.icon_live))
						.append(" ")
						.append(broadcast.getTitle());
						
						holder.title.setText(titleSB.toString());
					}
					else 
					{
						holder.title.setText(broadcast.getTitle());
					}
					
					holder.description.setText(broadcast.getProgram().getCategory());
					break;
				}
				
				default: 
				{
					holder.startTime.setText("");
					holder.title.setText("");
					holder.description.setText("");
					break;
				}
			}
			
			setOnGoingElementRed();
		}
		else
		{
			Log.w(TAG, "TVBroadcast is null");
		}
			
		return rowView;
	}
	
	/* If a show is airing, we set it to be red */
	private void setOnGoingElementRed() {
		if (isAiring) {
			holder.title.setTextColor(activity.getResources().getColor(R.color.red));
			holder.startTime.setTextColor(activity.getResources().getColor(R.color.red));
			isAiring = false;
		}
	}

	@Override
	public int getViewTypeCount() {
		int viewTypeCount = ChannelRowTypeEnum.class.getEnumConstants().length;
		return viewTypeCount;
	}

	@Override
	public int getItemViewType(int position) {
		ChannelRowTypeEnum rowType = ChannelRowTypeEnum.UP_COMING;
		
		TVBroadcast broadcast = getItem(position);
		if (broadcast.isBroadcastCurrentlyAiring()) {
			rowType = ChannelRowTypeEnum.ON_AIR;
		}
		return rowType.getId();
	}
	
	static class ViewHolder {
		TextView	title;
		TextView	description;
		TextView	startTime;
		TextView	timeLeft;
		
		ImageView	logo;

		ProgressBar	durationProgressBar;
	}
}
