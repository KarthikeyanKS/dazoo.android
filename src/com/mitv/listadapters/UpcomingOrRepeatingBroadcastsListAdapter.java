package com.mitv.listadapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.ui.elements.ReminderView;

public class UpcomingOrRepeatingBroadcastsListAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = UpcomingOrRepeatingBroadcastsListAdapter.class.getName();

	private LayoutInflater layoutInflater;
	private Activity activity;
	private final ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	private boolean usedForUpcomingEpisodes; /* else used for repeating... */

	public UpcomingOrRepeatingBroadcastsListAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> broadcasts, boolean usedForUpcomingEpisodes) {
		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.usedForUpcomingEpisodes = usedForUpcomingEpisodes;
		
		boolean foundRunningBroadcast = false;
		int indexOfRunningBroadcast = 0;
		for (int i = 0; i < broadcasts.size(); ++i) {
			TVBroadcastWithChannelInfo upcomingBroadcast = broadcasts.get(i);
			if (upcomingBroadcast.isAiring()) {
				foundRunningBroadcast = true;
				indexOfRunningBroadcast = i;
				break;
			}
		}

		if (foundRunningBroadcast) {
			broadcasts.remove(indexOfRunningBroadcast);
		}

		this.broadcasts = new ArrayList<TVBroadcastWithChannelInfo>(broadcasts);
		this.activity = activity;
	}

	@Override
	public int getCount() {
		if (broadcasts != null) {
			return broadcasts.size();
		} else
			return 0;
	}

	@Override
	public TVBroadcastWithChannelInfo getItem(int position) {
		if (broadcasts != null) {
			return broadcasts.get(position);
		} else
			return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);

		if (rowView == null) {
			rowView = layoutInflater.inflate(R.layout.row_upcoming_or_repeating_broadcast, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.headerContainer = (RelativeLayout) rowView.findViewById(R.id.row_upcoming_or_repeating_header_container);
			viewHolder.header = (TextView) rowView.findViewById(R.id.row_upcoming_or_repeating_header_tv);
			viewHolder.seasonEpisodeTv = (TextView) rowView.findViewById(R.id.row_upcoming_or_repeating_listitem_season_episode);
			viewHolder.timeTv = (TextView) rowView.findViewById(R.id.row_upcoming_or_repeating_listitem_title_time);
			viewHolder.channelTv = (TextView) rowView.findViewById(R.id.row_upcoming_or_repeating_listitem_channel);

			viewHolder.reminderView = (ReminderView) rowView.findViewById(R.id.row_upcoming_or_repeating_reminder_view);

			viewHolder.container = (LinearLayout) rowView.findViewById(R.id.row_upcoming_or_repeating_listitem_info_container);

			viewHolder.divider = (View) rowView.findViewById(R.id.row_upcoming_or_repeating_listitem_bottom_divider);
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcastWithChannelInfo != null) {
			holder.reminderView.setBroadcast(broadcastWithChannelInfo);
			
			/* Used to set a smaller size on the reminder icon */
			boolean iconSizeSmall = true;
			holder.reminderView.setSizeOfIcon(iconSizeSmall);

			holder.headerContainer.setVisibility(View.GONE);
			
			holder.divider.setVisibility(View.VISIBLE);
			
			if (position == 0 || broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString().equals((getItem(position - 1)).getBeginTimeDayAndMonthAsString()) == false) 
			{
				holder.header.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekAsString() + " " + broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString());
				holder.headerContainer.setVisibility(View.VISIBLE);
			}
			if (position != (getCount() - 1) && broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString().equals((getItem(position + 1)).getBeginTimeDayAndMonthAsString()) == false)
			{
				holder.divider.setVisibility(View.GONE);
			}

			if (usedForUpcomingEpisodes) 
			{
				int season = broadcastWithChannelInfo.getProgram().getSeason().getNumber().intValue();
				
				int episode = broadcastWithChannelInfo.getProgram().getEpisodeNumber();
				
				String seasonEpisode = "";
				
				if (season != 0) 
				{
					seasonEpisode += activity.getResources().getString(R.string.season) + " " + season + " ";
				}
				
				if (episode > 0) 
				{
					seasonEpisode += activity.getResources().getString(R.string.episode) + " " + episode;
				}
				
				holder.seasonEpisodeTv.setText(seasonEpisode);
			} 
			else 
			{
				holder.seasonEpisodeTv.setVisibility(View.GONE);
			}
			
			holder.timeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

			// Set channel
			String channel = broadcastWithChannelInfo.getChannel().getName();
			
			if (channel != null) 
			{
				holder.channelTv.setText(channel);
			}

			holder.container.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					
					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);

					activity.startActivity(intent);

				}
			});

		} else {
			holder.seasonEpisodeTv.setText("");
			holder.timeTv.setText("");
			holder.channelTv.setText("");
		}

		return rowView;
	}

	static class ViewHolder {
		RelativeLayout headerContainer;
		TextView header;
		LinearLayout container;
		TextView seasonEpisodeTv;
		TextView timeTv;
		TextView channelTv;
		ReminderView reminderView;

		View divider;
	}
}
