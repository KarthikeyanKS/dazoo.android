
package com.mitv.adapters.list;



import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.competition.TeamPageActivity;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionEventStandingsListAdapter 
	extends BaseAdapterWithShowMoreAdapter 
{
	private static final String TAG = CompetitionEventStandingsListAdapter.class.getName();
	
	
	private List<Standings> standings;
		
	
	
	public CompetitionEventStandingsListAdapter(
			final Activity activity,
			final List<Standings> standings,
			final boolean enableMoreViewAtBottom,
			final String viewBottomMessage, 
			final Runnable viewBottomConfirmProcedure)
	{
		super(activity, enableMoreViewAtBottom, viewBottomMessage, viewBottomConfirmProcedure);
		
		this.standings = standings;
	}
	
	
	
	@Override
	public int getCount()
	{
		int count = super.getCount();
		
		count += standings.size();
		
		return count;
	}
	
	
	
	@Override
	public Standings getItem(int position) 
	{
		Standings element = null;
		
		if (standings != null)
		{
			element = standings.get(position);
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
		if(isShowMoreView(position))
		{
			return super.getView(position, convertView, parent);
		}
		
		View rowView = convertView;
		
		final Standings element = getItem(position);
	
		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();
	
			rowView = layoutInflater.inflate(R.layout.row_competition_team_standings_list_item, null);
	
			viewHolder.headerContainer = (RelativeLayout) rowView.findViewById(R.id.row_competition_group_header_container);
			viewHolder.group = (TextView) rowView.findViewById(R.id.row_competition_header_group_name);
			viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.row_competition_row_container);
			viewHolder.teamName = (TextView) rowView.findViewById(R.id.row_competition_team_name);
			viewHolder.teamFlag = (ImageView) rowView.findViewById(R.id.row_competition_team_flag);
			viewHolder.teamGP = (TextView) rowView.findViewById(R.id.row_competition_team_table_gp);
			viewHolder.teamPlusMinus = (TextView) rowView.findViewById(R.id.row_competition_team_table_plus_minus);
			viewHolder.teamPoints = (TextView) rowView.findViewById(R.id.row_competition_team_table_pts);
			viewHolder.rowDividerView = (View) rowView.findViewById(R.id.row_competition_standings_row_divider);
			
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null) 
		{
			boolean isFirstposition = (position == 0);
			
			if (isFirstposition)
			{
				holder.headerContainer.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.headerContainer.setVisibility(View.GONE);
			}
			
			holder.group.setText(activity.getResources().getString(R.string.event_page_standings_team));
			holder.group.setTextSize(15);
			
			final long teamID = element.getTeamId();
			
			Team team = ContentManager.sharedInstance().getCacheManager().getTeamById(teamID);
			
			if(team != null)
			{
				ImageAware imageAware = new ImageViewAware(holder.teamFlag, false);
					
				String teamFlagUrl = team.getFlagImageURL();
						
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(teamFlagUrl, imageAware);
			}

			String teamGPAsString = Integer.valueOf(element.getMatches()).toString();
			String teamPlusMinusAsString = Integer.valueOf(element.getGoalsForMinusGoalsAgainst()).toString();
			String teamPointsAsString = Integer.valueOf(element.getPoints()).toString();

			holder.teamGP.setText(teamGPAsString);
			holder.teamPlusMinus.setText(teamPlusMinusAsString);
			holder.teamPoints.setText(teamPointsAsString);
			
			holder.teamName.setText(element.getTeam());
			
			holder.container.setOnClickListener(new View.OnClickListener() 
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
						
						TrackingGAManager.sharedInstance().sendUserCompetitionTeamPressedEvent(competitionName, element.getTeam(), "Standings tab");
					}
	            	
	            	Intent intent = new Intent(activity, TeamPageActivity.class);
	                
	                intent.putExtra(Constants.INTENT_COMPETITION_ID, element.getCompetitionId());
	                intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, element.getTeamId());
	                intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, element.getPhaseId());
	                
	                activity.startActivity(intent);
	            }
	        });
			
			if((position > 0) && (position % 2) == 0)
			{
				holder.rowDividerView.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.rowDividerView.setVisibility(View.GONE);
			}
		}
		else
		{
			Log.w(TAG, "Event is null");
		}
			
		return rowView;
	}
	
	
	
	private static class ViewHolder 
	{
		private RelativeLayout headerContainer;
		private TextView group;
		private ImageView teamFlag;
		private TextView teamName;		
		private TextView teamGP;
		private TextView teamPlusMinus;
		private TextView teamPoints;
		private RelativeLayout container;
		private View rowDividerView;
	}
	
}