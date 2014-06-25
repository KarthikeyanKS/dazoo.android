
package com.mitv.adapters.list;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.competition.TeamPageActivity;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.comparators.EventStandingsComparatorByPointsAndGoalDifference;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionStandingsByGroupListAdapter 
	extends BaseAdapter 
{
	@SuppressWarnings("unused")
	private static final String TAG = CompetitionStandingsByGroupListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private List<Standings> standings;
	

	
	public CompetitionStandingsByGroupListAdapter(
			final Activity activity,
			final Map<Long, List<Standings>> standingsByPhase)
	{
		this.standings = new ArrayList<Standings>();
		
		Collection<List<Standings>> values = standingsByPhase.values();
		
		for(List<Standings> value : values)
		{
			if (value != null && value.size() > 0) 
			{
				Collections.sort(value, new EventStandingsComparatorByPointsAndGoalDifference());
				
				Collections.reverse(value);
				
				standings.addAll(value);
			}
		}
		
		this.activity = activity;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	
	@Override
	public int getCount() 
	{
		int count = 0;
		
		if (standings != null) 
		{
			count = standings.size();
		}
		
		return count;
	}

	
	
	@Override
	public Standings getItem(int position) 
	{
		Standings standingsElement = null;
		
		if (standings != null)
		{
			standingsElement = standings.get(position);
		}
		
		return standingsElement;
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

		final Standings element = getItem(position);

		if (rowView == null) 
		{
			rowView = layoutInflater.inflate(R.layout.row_competition_team_standings_list_item, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.headerContainer = (RelativeLayout) rowView.findViewById(R.id.row_competition_group_header_container);
			viewHolder.group = (TextView) rowView.findViewById(R.id.row_competition_header_group_name);
			viewHolder.rowContainer = (RelativeLayout) rowView.findViewById(R.id.row_competition_row_container);
			viewHolder.teamName = (TextView) rowView.findViewById(R.id.row_competition_team_name);
			viewHolder.teamFlag = (ImageView) rowView.findViewById(R.id.row_competition_team_flag);
			viewHolder.teamGP = (TextView) rowView.findViewById(R.id.row_competition_team_table_gp);
			viewHolder.teamPlusMinus = (TextView) rowView.findViewById(R.id.row_competition_team_table_plus_minus);
			viewHolder.teamPoints = (TextView) rowView.findViewById(R.id.row_competition_team_table_pts);
			viewHolder.rowDivider = (View) rowView.findViewById(R.id.row_competition_standings_row_divider);
			viewHolder.transparentDivider = (View) rowView.findViewById(R.id.row_competition_standings_divider_transparent);
			viewHolder.dividerContainer = (RelativeLayout) rowView.findViewById(R.id.divider_container_team_standings);
			
			viewHolder.rowContainer.setBackgroundColor(activity.getResources().getColor(R.color.white));
			viewHolder.dividerContainer.setBackgroundColor(activity.getResources().getColor(R.color.white));

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		// TODO Sort the teams in each group accordingly to highest Pts.
		if (holder != null) 
		{
			holder.transparentDivider.setVisibility(View.GONE);
			
			boolean isFirstposition = (position == 0);

			boolean isCurrentPhaseEqualToPreviousPhase;

			if(isFirstposition == false)
			{
				Standings previousElement = getItem(position - 1);

				isCurrentPhaseEqualToPreviousPhase = element.isTheSamePhaseAs(previousElement);
			}
			else
			{
				isCurrentPhaseEqualToPreviousPhase = true;
			}
			
			if (isFirstposition || isCurrentPhaseEqualToPreviousPhase == false) 
			{
				holder.headerContainer.setVisibility(View.VISIBLE);
				
				String headerText = element.getPhase();
				holder.group.setText(headerText.toUpperCase());
				
				holder.transparentDivider.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.headerContainer.setVisibility(View.GONE);
			}
				
			String teamGPAsString = Integer.valueOf(element.getMatches()).toString();
			String teamPlusMinusAsString = Integer.valueOf(element.getGoalsForMinusGoalsAgainst()).toString();
			String teamPointsAsString = Integer.valueOf(element.getPoints()).toString();

			holder.teamGP.setText(teamGPAsString);
			holder.teamPlusMinus.setText(teamPlusMinusAsString);
			holder.teamPoints.setText(teamPointsAsString);

			holder.teamName.setText(element.getTeam());

			long teamID = element.getTeamId();

			Team team = ContentManager.sharedInstance().getCacheManager().getTeamById(teamID);

			if(team != null)
			{
				ImageAware imageAware = new ImageViewAware(holder.teamFlag, false);

				String teamFlagUrl = team.getFlagImageURL();

				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(teamFlagUrl, imageAware);
			}
			
			holder.rowContainer.setOnClickListener(new View.OnClickListener() 
	        {
	            public void onClick(View v)
	            {
					if (element != null) 
					{
						Competition competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(element.getCompetitionId());
						
						String competitionName = null;
						if (competition != null)
						{
							competitionName = competition.getDisplayName();
						}
						else
						{
							competitionName = String.valueOf(element.getCompetitionId());
						}
						TrackingGAManager.sharedInstance().sendUserCompetitionTeamPressedEvent(competitionName, element.getTeam(), "Group tab");
					}
					
	            	Intent intent = new Intent(activity, TeamPageActivity.class);
	                
	                intent.putExtra(Constants.INTENT_COMPETITION_ID, element.getCompetitionId());
	                intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, element.getTeamId());
	                intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, element.getPhaseId());
	                
	                activity.startActivity(intent);
	            }
	        });
			
			if((position % 2) == 0 && !isFirstposition && isCurrentPhaseEqualToPreviousPhase)
			{
				holder.rowDivider.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.rowDivider.setVisibility(View.GONE);
			}
		}
			
		return rowView;
	}
	

	
	private static class ViewHolder 
	{
		private RelativeLayout headerContainer;
		private RelativeLayout rowContainer;
		private TextView group;
		private TextView teamName;
		private ImageView teamFlag;
		private TextView teamGP;
		private TextView teamPlusMinus;
		private TextView teamPoints;
		private View rowDivider;
		private View transparentDivider;
		private RelativeLayout dividerContainer;
	}
}
