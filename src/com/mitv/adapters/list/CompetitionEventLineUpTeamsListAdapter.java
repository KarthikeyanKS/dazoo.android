
package com.mitv.adapters.list;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.enums.EventLineUpPosition;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;



public class CompetitionEventLineUpTeamsListAdapter 
	extends BaseAdapter 
{
	private static final String TAG = CompetitionEventLineUpTeamsListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	
	private List<EventLineUp> lineups;
		
	
	
	public CompetitionEventLineUpTeamsListAdapter(
			final Activity activity,
			final List<EventLineUp> lineups)
	{
		this.lineups = lineups;
		
		this.activity = activity;
	
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	
	@Override
	public int getCount()
	{
		int count = 0;
		
		if (lineups != null) 
		{
			count = lineups.size();
		}
		
		return count;
	}
	
	
	
	@Override
	public EventLineUp getItem(int position) 
	{
		EventLineUp element = null;
		
		if (lineups != null)
		{
			element = lineups.get(position);
		}
		
		return element;
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
	
		final EventLineUp eventLineUp = getItem(position);
	
		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();
	
			rowView = layoutInflater.inflate(R.layout.row_competition_event_lineup_teams_list_item, null);
	
			viewHolder.playerShirtNumber = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_player_shirt_number);
			viewHolder.playerPosition = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_player_position);
			viewHolder.playerName = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_player_name);
			viewHolder.dividerIfCoach = (View) rowView.findViewById(R.id.competition_event_header_divider_lineup_teeest);
			
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null) 
		{
			if (eventLineUp != null) 
			{	
				String shirtNumberAsString;
				
				EventLineUpPosition lineUpposition = eventLineUp.getPosition();
				
				String positionShort = "";
				
				switch (lineUpposition) 
				{
					case GOALKEEPER: {
						positionShort = "A";
						shirtNumberAsString = Integer.valueOf(eventLineUp.getShirtNumber()).toString();
						break;
					}
					case DEFENSE: {
						positionShort = "D";
						shirtNumberAsString = Integer.valueOf(eventLineUp.getShirtNumber()).toString();
						break;
					}
					case MIDFIELDER: {
						positionShort = "MC";
						shirtNumberAsString = Integer.valueOf(eventLineUp.getShirtNumber()).toString();
						break;
					}
					case FORWARD:
					{
						positionShort = "DEL";
						shirtNumberAsString = Integer.valueOf(eventLineUp.getShirtNumber()).toString();
						break;
					}
					case COACH: {
						holder.dividerIfCoach.setVisibility(View.VISIBLE);
						positionShort = "EN";
						shirtNumberAsString = "";
						break;
					}
					case REFEREE:
					case LINESMAN:
					case FORTHTHOFFICIAL:
					default:
					{
						positionShort = "";
						shirtNumberAsString = "";
						break;
					}
				}
				
				String playerNameFull = eventLineUp.getPerson();
				
				holder.playerShirtNumber.setText(shirtNumberAsString);
				holder.playerPosition.setText(positionShort);
				
				holder.playerName.setText(playerNameFull);
				holder.playerName.setEllipsize(TextUtils.TruncateAt.END);
				holder.playerName.setHorizontallyScrolling(false);
				holder.playerName.setSingleLine();
					
			} else {
				Log.w(TAG, "EventLineUp is null");
			}
			
		}
		else
		{
			Log.w(TAG, "EventLineUp is null");
		}
			
		return rowView;
	}
	
	
	
	private static class ViewHolder 
	{
		private TextView playerName;
		private TextView playerPosition;
		private TextView playerShirtNumber;
		private View dividerIfCoach;
	}
}