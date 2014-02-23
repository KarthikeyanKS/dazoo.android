
package com.mitv.adapters;



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

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.ReminderView;



public class RepetitionsListAdapter 
	extends BaseAdapter 
{
	@SuppressWarnings("unused")
	private static final String	TAG	= RepetitionsListAdapter.class.getName();

	
	private Activity activity;
	private LayoutInflater layoutInflater;
	private ArrayList<TVBroadcastWithChannelInfo> repeatingEpisodes;
	private TVProgram tvProgram;

	
	
	public RepetitionsListAdapter(
			Activity activity, ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts, 
			TVProgram program, 
			TVBroadcast runningBroadcast) 
	{
		/* Remove running broadcast */
		
		boolean foundRunningBroadcast = false;
		
		int indexOfRunningBroadcast = 0;
		
		for(int i = 0; i < repeatingBroadcasts.size(); ++i) 
		{
			TVBroadcastWithChannelInfo repeatingBroadcast = repeatingBroadcasts.get(i);
			
			if(repeatingBroadcast.equals(runningBroadcast)) 
			{
				foundRunningBroadcast = true;
				indexOfRunningBroadcast = i;
				break;
			}
		}

		if(foundRunningBroadcast) 
		{
			repeatingBroadcasts.remove(indexOfRunningBroadcast);
		}

		this.activity = activity;
		this.tvProgram = program;
		this.repeatingEpisodes =repeatingBroadcasts;
	}

	
	
	@Override
	public int getCount() 
	{
		if (repeatingEpisodes != null) 
		{
			return repeatingEpisodes.size();
		} 
		else 
		{
			return 0;
		}
	}

	
	
	@Override
	public TVBroadcastWithChannelInfo getItem(int position) 
	{
		if (repeatingEpisodes != null) 
		{
			return repeatingEpisodes.get(position);
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

	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		final TVBroadcastWithChannelInfo broadcast = getItem(position);
		
		//TODO why do this? Why should we need to set the program?
//		broadcast.setProgram(mProgram);

		if (rowView == null) 
		{
			layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// the same layout is used as for the Upcoming Episodes for the
			// series, just without the Season-Episode title which is hidden
			// here
			rowView = layoutInflater.inflate(R.layout.row_upcoming_episodes_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mHeaderContainer = (RelativeLayout) rowView.findViewById(R.id.row_upcoming_episodes_header_container);
			viewHolder.mHeader = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_header_tv);
			viewHolder.mSeasonEpisodeTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_season_episode);
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_title_time);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.row_upcoming_episodes_listitem_channel);

			viewHolder.mReminderView = (ReminderView) rowView.findViewById(R.id.row_upcoming_episodes_reminder_view);

			viewHolder.mContainer = (LinearLayout) rowView.findViewById(R.id.row_upcoming_episodes_listitem_info_container);

			viewHolder.mDivider = (View) rowView.findViewById(R.id.row_upcoming_episodes_listitem_bottom_divider);
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) 
		{
			holder.mReminderView.setBroadcast(broadcast);

			holder.mHeaderContainer.setVisibility(View.GONE);
			holder.mDivider.setVisibility(View.VISIBLE);
			
			if (position == 0 || broadcast.getBeginTimeDayAndMonthAsString().equals((getItem(position - 1)).getBeginTimeDayAndMonthAsString()) == false) 
			{
				holder.mHeader.setText(broadcast.getBeginTimeDayOfTheWeekAsString() + " " + broadcast.getBeginTimeDayAndMonthAsString());
				holder.mHeaderContainer.setVisibility(View.VISIBLE);
			}
			if (position != (getCount() - 1) && 
				broadcast.getBeginTimeDayAndMonthAsString().equals((getItem(position + 1)).getBeginTimeDayAndMonthAsString()) == false) 
			{
				holder.mDivider.setVisibility(View.GONE);
			}

			holder.mSeasonEpisodeTv.setVisibility(View.GONE);

			holder.mTimeTv.setText(broadcast.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());
			
			// Set channel
			String channel = broadcast.getChannel().getName();
			
			if(channel != null) 
			{
				holder.mChannelTv.setText(channel);
			}

			holder.mContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcast);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY, true);
					activity.startActivity(intent);
				}
			});

		} 
		else 
		{
			holder.mSeasonEpisodeTv.setText("");
			holder.mTimeTv.setText("");
			holder.mChannelTv.setText("");
		}

		return rowView;
	}

	
	
	static class ViewHolder 
	{
		RelativeLayout mHeaderContainer;
		TextView mHeader;
		LinearLayout mContainer;
		TextView mSeasonEpisodeTv;
		TextView mTimeTv;
		TextView mChannelTv;
		ReminderView mReminderView;
		View mDivider;
	}
}
