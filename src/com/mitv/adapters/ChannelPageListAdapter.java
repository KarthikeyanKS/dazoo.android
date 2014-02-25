package com.mitv.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.millicom.mitv.enums.BroadcastTypeEnum;
import com.millicom.mitv.enums.ChannelRowTypeEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.utilities.ProgressBarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class ChannelPageListAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = ChannelPageListAdapter.class.getName();

	private LayoutInflater			layoutInflater;
	private Activity				activity;
	private ArrayList<TVBroadcast>	currentAndUpcomingbroadcasts;
	private ViewHolder				holder;

	
	
	public ChannelPageListAdapter(Activity activity, ArrayList<TVBroadcast> currentAndUpcomingbroadcasts) {
		this.currentAndUpcomingbroadcasts = currentAndUpcomingbroadcasts;
		this.activity = activity;

		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
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
	public long getItemId(int arg0) {
		return -1;
	}

	public void notifyBroadcastEnded() {
		currentAndUpcomingbroadcasts.remove(0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final TVBroadcast broadcast = getItem(position);
		BroadcastTypeEnum broadcastType = broadcast.getBroadcastType();
		ProgramTypeEnum programType = broadcast.getProgram().getProgramType();

		if (rowView == null) {
			ViewHolder viewHolder = new ViewHolder();
			if (getItemViewType(position) == 0) {
				rowView = layoutInflater.inflate(R.layout.row_channelpage_current_list_item, null);
				viewHolder.startTime = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_time_tv);
				viewHolder.logo = (ImageView) rowView.findViewById(R.id.channelpage_broadcast_iv);
				viewHolder.title = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_title_tv);
				viewHolder.description = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_text_tv);
				
				/* - The "extra" data fields for the current broadcast.*/
				viewHolder.timeLeft = (TextView) rowView.findViewById(R.id.channelpage_broadcast_timeleft);
				viewHolder.durationProgressBar = (ProgressBar) rowView.findViewById(R.id.channelpage_broadcast_details_progressbar);
				
			} else {
				rowView = layoutInflater.inflate(R.layout.row_channelpage_list_item, null);
				viewHolder.startTime = (TextView) rowView.findViewById(R.id.channelpage_list_item_time_tv);
				viewHolder.title = (TextView) rowView.findViewById(R.id.channelpage_list_item_title_tv);
				viewHolder.description = (TextView) rowView.findViewById(R.id.channelpage_list_item_description_tv);
			}
			rowView.setTag(viewHolder);
		}

		holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			if (getItemViewType(position) == 0) {
				// MC - Set the image for current broadcast.
				ImageAware imageAware = new ImageViewAware(holder.logo, false);
				ImageLoader.getInstance().displayImage(broadcast.getProgram().getImages().getLandscape().getLarge(), imageAware);
				
				ProgressBarUtils.setupProgressBar(activity, broadcast, holder.durationProgressBar, holder.timeLeft);
			}

			// MC - Set the begin time of the broadcast.

			holder.startTime.setText(broadcast.getBeginTimeHourAndMinuteLocalAsString());
			String title = broadcast.getProgram().getTitle();
			
			switch (programType) {
			case MOVIE: {
				holder.title.setText(activity.getResources().getString(R.string.icon_movie) + " " + title);
				holder.description.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
				break;
			}
			case TV_EPISODE: {
				int season = broadcast.getProgram().getSeason().getNumber();
				int episode = broadcast.getProgram().getEpisodeNumber();
				String seasonEpisode = "";
				if (season > 0) {
					seasonEpisode += activity.getResources().getString(R.string.season) + " " + season + " ";
				}
				if (episode > 0) {
					seasonEpisode += activity.getResources().getString(R.string.episode) + " " + episode;
				}
				holder.description.setText(seasonEpisode);
				holder.title.setText(broadcast.getProgram().getSeries().getName());
				break;
			}
			case SPORT: {
				if (Consts.BROADCAST_TYPE_LIVE.equals(broadcastType)) {
					holder.title.setText(activity.getResources().getString(R.string.icon_live) + " " + title);
					holder.description.setText(broadcast.getProgram().getSportType().getName() + ": " + broadcast.getProgram().getTournament());
				} else {
					holder.description.setText(broadcast.getProgram().getSportType().getName() + ": " + broadcast.getProgram().getTournament());
					holder.title.setText(title);
				}
				break;
			}
			case OTHER: {
				holder.title.setText(title);
				holder.description.setText(broadcast.getProgram().getCategory());
				break;
			}
			default: {
				//TODO NewArc do not set the string to "no value" set it to emppty!
				holder.startTime.setText("no value");
				holder.title.setText("no value");
				holder.description.setText("no value");
				break;
			}
			}
		}
			
		return rowView;
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
