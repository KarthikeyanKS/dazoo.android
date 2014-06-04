
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Phase;
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
	
			rowView = layoutInflater.inflate(R.layout.row_competition_team_squad_list_item, null);
			viewHolder.rowContainer = (RelativeLayout) rowView.findViewById(R.id.row_squad_container);
			viewHolder.headersContainer = (RelativeLayout) rowView.findViewById(R.id.row_competition_group_header_container);

			/* Headers */
			viewHolder.playerShirtNumberHeader = (TextView) rowView.findViewById(R.id.row_squad_header_shirt_number);
			viewHolder.playerNameHeader = (TextView) rowView.findViewById(R.id.row_squad_header_player);
			viewHolder.pjHeader = (TextView) rowView.findViewById(R.id.row_squad_header_pj);
			viewHolder.redCardHeader = (ImageView) rowView.findViewById(R.id.row_squad_red_flag_icon);
			viewHolder.yellowCardHeader = (ImageView) rowView.findViewById(R.id.row_squad_yellow_flag_icon);
			viewHolder.fotballHeader = (ImageView) rowView.findViewById(R.id.row_squad_football_icon);
	
			/* Row */
			viewHolder.playerShirtNumber = (TextView) rowView.findViewById(R.id.row_squad_row_shirt_number);
			viewHolder.playerName = (TextView) rowView.findViewById(R.id.row_squad_row_player);
			viewHolder.pj = (TextView) rowView.findViewById(R.id.row_squad_row_pj);
			viewHolder.goals = (TextView) rowView.findViewById(R.id.row_squad_row_goals);
			viewHolder.redCards = (TextView) rowView.findViewById(R.id.row_squad_row_red_cards);
			viewHolder.yellowCrads = (TextView) rowView.findViewById(R.id.row_squad_row_yellow_cards);
			
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null) 
		{
			if (teamSquad != null) {
				/* check if position is odd or not. change background color on that row.. */
				
				boolean isFirstposition = (position == 0);
				
				if(isFirstposition) {
					/* Headers */
					holder.redCardHeader.setImageDrawable(activity.getResources().getDrawable(R.drawable.competition_event_highlight_red_card));
					holder.yellowCardHeader.setImageDrawable(activity.getResources().getDrawable(R.drawable.competition_event_highlight_yellow_card));
					holder.fotballHeader.setImageDrawable(activity.getResources().getDrawable(R.drawable.competition_event_highlight_goal));
					holder.playerShirtNumberHeader.setText(activity.getResources().getString(R.string.team_page_squad_header_shirt_number));
					holder.playerNameHeader.setText(activity.getResources().getString(R.string.team_page_squad_header_player));
					holder.pjHeader.setText(activity.getResources().getString(R.string.team_page_squad_header_pj));
					
					holder.headersContainer.setVisibility(View.VISIBLE);
					
				} else {
					holder.headersContainer.setVisibility(View.GONE);
				}
				
				/* Row */
				int shirtNr = teamSquad.getShirtNumber();
				if (shirtNr > 0) {
					holder.playerShirtNumber.setText(Integer.valueOf(shirtNr).toString());
				} else {
					holder.playerShirtNumber.setText("-");
				}
				
				String playerNameShort = teamSquad.getPersonShort();
				
				/* Needed? */
				holder.playerName.setText(playerNameShort);
				holder.playerName.setEllipsize(TextUtils.TruncateAt.END);
				holder.playerName.setHorizontallyScrolling(false);
				holder.playerName.setSingleLine();
				
				
				/* GAMES PLAYED */
				int gamesPlayed = teamSquad.getMatches();
				holder.pj.setText(Integer.valueOf(gamesPlayed).toString());
				
				
				/* GOALS */
				int goals = teamSquad.getGoals();
				if (goals > 0) {
					holder.goals.setText(Integer.valueOf(goals).toString());
					
				} else {
					holder.goals.setText(" ");
				}
				
				
				/* YELLOW CARDS */
				int yellowC = teamSquad.getYellowCards();
				if (yellowC > 0) {
					holder.yellowCrads.setText(Integer.valueOf(yellowC).toString());
					
				} else {
					holder.yellowCrads.setText(" ");
				}
				
				
				/* RED CARDS */
				int redC = teamSquad.getRedCards();
				if (redC > 0) {
					holder.redCards.setText(Integer.valueOf(redC).toString());
					
				} else {
					holder.redCards.setText(" ");
				}
				
				/* Change to different background if position is odd or not */
				if ((position > 0) && (position % 2) == 0)
				{
					holder.rowContainer.setBackgroundColor(activity.getResources().getColor(R.color.white));
				}
				else if (position == 0) {
//					holder.rowContainer.setBackgroundColor(activity.getResources().getColor(R.color.white));
				}
				else {
					holder.rowContainer.setBackgroundColor(activity.getResources().getColor(R.color.grey0));
				}
				
				holder.rowContainer.setVisibility(View.VISIBLE);
				
					
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
		private RelativeLayout headersContainer;
		private RelativeLayout rowContainer;
		private TextView playerNameHeader;
		private TextView playerShirtNumberHeader;
		private TextView pjHeader;
		private ImageView redCardHeader;
		private ImageView yellowCardHeader;
		private ImageView fotballHeader;
		private TextView playerShirtNumber;
		private TextView playerName;
		private TextView pj;
		private TextView goals;
		private TextView redCards;
		private TextView yellowCrads;
	}
}