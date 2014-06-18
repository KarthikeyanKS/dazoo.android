
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
import android.widget.ImageView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.enums.EventLineUpPositionEnum;
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
			viewHolder.playerInOrOutImage = (ImageView) rowView.findViewById(R.id.row_competition_event_lineup_player_in_or_out_image);
			viewHolder.playerInOrOutTime = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_player_in_or_out_time);
			
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null) 
		{
			if (eventLineUp != null) 
			{	
				String shirtNumberAsString;
				
				EventLineUpPositionEnum lineUpposition = eventLineUp.getPosition();
				
				String positionShort = lineUpposition.getPositionShort();
				
				switch (lineUpposition) 
				{
					case GOALKEEPER: 
					case DEFENSE: 
					case MIDFIELDER: 
					case FORWARD:
					{
						shirtNumberAsString = Integer.valueOf(eventLineUp.getShirtNumber()).toString();
						break;
					}
					
					case COACH: 
					{
						holder.dividerIfCoach.setVisibility(View.VISIBLE);
						shirtNumberAsString = "";
						break;
					}
					case REFEREE:
					case LINESMAN:
					case FORTHTHOFFICIAL:
					default:
					{
						shirtNumberAsString = "";
						break;
					}
				}
				
				if(eventLineUp.containsLineUpInMinute())
				{
					holder.playerInOrOutImage.setVisibility(View.VISIBLE);
					holder.playerInOrOutImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.competition_event_lineup_player_in));
					
					holder.playerInOrOutTime.setVisibility(View.VISIBLE);
					holder.playerInOrOutTime.setText(eventLineUp.getLineUpInMinute());
				}
				else if(eventLineUp.containsLineUpOutMinute())
				{
					holder.playerInOrOutImage.setVisibility(View.VISIBLE);
					holder.playerInOrOutImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.competition_event_lineup_player_out));
					
					holder.playerInOrOutTime.setVisibility(View.VISIBLE);
					holder.playerInOrOutTime.setText(eventLineUp.getLineUpOutMinute());
				}
				else
				{
					holder.playerInOrOutImage.setVisibility(View.GONE);
					holder.playerInOrOutTime.setVisibility(View.GONE);
				}

				holder.playerShirtNumber.setText(shirtNumberAsString);
				holder.playerPosition.setText(positionShort);
				
				holder.playerName.setText(eventLineUp.getPerson());
				holder.playerName.setEllipsize(TextUtils.TruncateAt.END);
				holder.playerName.setHorizontallyScrolling(false);
				holder.playerName.setSingleLine();
					
			} 
			else 
			{
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
		private ImageView playerInOrOutImage;
		private TextView playerInOrOutTime;
	}
}