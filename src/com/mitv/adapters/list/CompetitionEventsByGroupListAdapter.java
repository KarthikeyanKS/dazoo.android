
package com.mitv.adapters.list;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionEventsByGroupListAdapter 
	extends BaseAdapter 
{
	private static final String TAG = CompetitionEventsByGroupListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ViewHolder holder;
	
	private Map<String, List<Event>> eventsByGroup;
	private List<Event> events;
	
	
	
	public CompetitionEventsByGroupListAdapter(
			final Activity activity,
			final Map<String, List<Event>> eventsByGroup)
	{
		super();
		
		this.eventsByGroup = eventsByGroup;
		
		this.events = new ArrayList<Event>();
		
		Collection<List<Event>> values = eventsByGroup.values();
		
		for(List<Event> value : values)
		{
			events.addAll(value);
		}
		
		this.activity = activity;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	
	@Override
	public int getCount()
	{
		int count = 0;
		
		if (events != null) 
		{
			count = events.size();
		}
		
		return count;
	}

	
	
	@Override
	public Event getItem(int position) 
	{
		Event event = null;
		
		if (events != null)
		{
			event = events.get(position);
		}
		
		return event;
	}

	
	
	@Override
	public long getItemId(int arg0) 
	{
		return -1;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		final Event event = getItem(position);

		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();

			rowView = layoutInflater.inflate(R.layout.row_competition_event_list_item, null);

			// TODO - Fix this
			viewHolder.group = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
			viewHolder.startWeekDay = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
			
			viewHolder.team1name = (TextView) rowView.findViewById(R.id.row_competition_team_one_name);
			viewHolder.team1flag = (ImageView) rowView.findViewById(R.id.row_competition_team_one_flag);
			viewHolder.team2name = (TextView) rowView.findViewById(R.id.row_competition_team_two_name);
			viewHolder.team2flag = (ImageView) rowView.findViewById(R.id.row_competition_team_two_flag);
			
			viewHolder.startTime = (TextView) rowView.findViewById(R.id.row_competition_page_begin_time_broadcast);
			
			// TODO - Fix this
			viewHolder.score = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
			viewHolder.timeLeft = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
			
			viewHolder.broadcastChannels = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);
			
			rowView.setTag(viewHolder);
		}

		holder = (ViewHolder) rowView.getTag();

		if (event != null) 
		{	
			String team1ID = event.getTeam1Id();
			
			Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
			
			boolean isLocalFlagDrawableResourceAvailableForTeam1 = team1.isLocalFlagDrawableResourceAvailable();
			
			if(isLocalFlagDrawableResourceAvailableForTeam1)
			{
				holder.team1flag.setImageDrawable(team1.getLocalFlagDrawableResource());
			}
			else
			{
				ImageAware imageAware = new ImageViewAware(holder.team1flag, false);
				
				String team1FlagUrl = team1.getImages().getFlag().getImageURLForDeviceDensityDPI();
				
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(team1FlagUrl, imageAware);
			}
			
			String team2ID = event.getTeam2Id();
			
			Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);
			
			boolean isLocalFlagDrawableResourceAvailableForTeam2 = team2.isLocalFlagDrawableResourceAvailable();
			
			if(isLocalFlagDrawableResourceAvailableForTeam2)
			{
				holder.team2flag.setImageDrawable(team2.getLocalFlagDrawableResource());
			}
			else
			{
				ImageAware imageAware = new ImageViewAware(holder.team2flag, false);
				
				String team2FlagUrl = team2.getImages().getFlag().getImageURLForDeviceDensityDPI();
				
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(team2FlagUrl, imageAware);
			}
			
			holder.team1name.setText(team1.getDisplayName());
			
			holder.team2name.setText(team2.getDisplayName());
			
			// TODO - Set remaining variables
		}
		else
		{
			Log.w(TAG, "Event is null");
		}
			
		return rowView;
	}
	

	
	private static class ViewHolder 
	{
		private TextView group;
		private TextView startWeekDay;
		private TextView team1name;
		private ImageView team1flag;
		private TextView team2name;
		private ImageView team2flag;
		private TextView startTime;
		private TextView score;
		private TextView timeLeft;
		private TextView broadcastChannels;
	}
}
