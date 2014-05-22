
package com.mitv.adapters.list;



import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;



public class CompetitionTeamSquadsTeamsListAdapter 
	extends BaseAdapter 
{
	private static final String TAG = CompetitionTeamSquadsTeamsListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	
	private List<TeamSquad> teamSquads;
	private int rowWidth = -1;
		
	
	
	public CompetitionTeamSquadsTeamsListAdapter(
			final Activity activity,
			final List<TeamSquad> teamSquads)
	{
		this.teamSquads = teamSquads;
		
		this.activity = activity;
	
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	
	@Override
	public int getCount()
	{
		int count = 0;
		
		if (teamSquads != null) 
		{
			count = teamSquads.size();
		}
		
		return count;
	}
	
	
	
	@Override
	public TeamSquad getItem(int position) 
	{
		TeamSquad element = null;
		
		if (teamSquads != null)
		{
			element = teamSquads.get(position);
		}
		
		return element;
	}
	
	
	
	@Override
	public long getItemId(int arg0) 
	{
		return -1;
	}
	
	
	
	public void setRowWidth(int width) 
	{
		this.rowWidth = width;
	}
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;
	
		final TeamSquad teamSquad = getItem(position);
	
		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();
	
			rowView = layoutInflater.inflate(R.layout.row_competition_event_lineup_teams_list_item, null);
	
			viewHolder.playerShirtNumber = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_player_shirt_number);
			viewHolder.playerPosition = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_player_position);
			viewHolder.playerName = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_player_name);
			
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null) 
		{
			if (teamSquad != null) {
				
//				String shirtNr = teamSquad.getShirtNr();
//				String positionShort = teamSquad.getFunctionShort();
				String playerNameFull = teamSquad.getPerson();
				
//				holder.playerShirtNumber.setText(shirtNr);
//				holder.playerPosition.setText(positionShort);
				
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
	}
}