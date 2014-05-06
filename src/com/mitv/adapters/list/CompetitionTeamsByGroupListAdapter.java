
package com.mitv.adapters.list;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
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
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionTeamsByGroupListAdapter 
	extends BaseAdapter 
{
	private static final String TAG = CompetitionTeamsByGroupListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	
	private Map<String, List<Team>> teamsByGroup;
	private List<Team> teams;
	
	private static final String PHASE01 = "PHASE01";
	private static final String PHASE02 = "PHASE02";
	private static final String PHASE03 = "PHASE03";
	private static final String PHASE04 = "PHASE04";
	private static final String PHASE05 = "PHASE05";
	private static final String PHASE06 = "PHASE06";
	
	
	
	public CompetitionTeamsByGroupListAdapter(
			final Activity activity,
			final Map<String, List<Team>> teamsByGroup)
	{
		this.teamsByGroup = teamsByGroup;
		
		this.teams = new ArrayList<Team>();
		
		Collection<List<Team>> values = teamsByGroup.values();
		
		for(List<Team> value : values)
		{
			teams.addAll(value);
		}
		
		this.activity = activity;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	
	@Override
	public int getCount() 
	{
		int count = 0;
		
		if (teams != null) 
		{
			count = teams.size();
		}
		
		return count;
	}

	
	
	@Override
	public Team getItem(int position) 
	{
		Team team = null;
		
		if (teams != null)
		{
			team = teams.get(position);
		}
		
		return team;
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

		final Team team = getItem(position);

		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();

			rowView = layoutInflater.inflate(R.layout.row_competition_team_list_item, null);

			viewHolder.headerContainer = (RelativeLayout) rowView.findViewById(R.id.row_competition_group_header_container);
			viewHolder.group = (TextView) rowView.findViewById(R.id.row_competition_header_group);
			viewHolder.dividerView = rowView.findViewById(R.id.row_competition_header_divider);
			
			viewHolder.headerGP = (TextView) rowView.findViewById(R.id.row_competition_header_gp);
			viewHolder.headerPlusMinus = (TextView) rowView.findViewById(R.id.row_competition_header_plus_minus);
			viewHolder.headerPts = (TextView) rowView.findViewById(R.id.row_competition_header_pts);
			
			/* Flag and name */
			viewHolder.teamName = (TextView) rowView.findViewById(R.id.row_competition_team_name);
			viewHolder.teamFlag = (ImageView) rowView.findViewById(R.id.row_competition_team_flag);
			
			/* Table */
			viewHolder.teamInfoGP = (TextView) rowView.findViewById(R.id.row_competition_team_table_gp);
			viewHolder.teamInfoPlusMinus = (TextView) rowView.findViewById(R.id.row_competition_team_table_plus_minus);
			viewHolder.teamInfoPts = (TextView) rowView.findViewById(R.id.row_competition_team_table_pts);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		// TODO Sort the teams in each group accordingly to highest Pts.

		if (team != null) 
		{
			holder.headerContainer.setVisibility(View.GONE);
			holder.group.setVisibility(View.GONE);
			holder.dividerView.setVisibility(View.GONE);
			holder.headerGP.setVisibility(View.GONE);
			holder.headerPlusMinus.setVisibility(View.GONE);
			holder.headerPts.setVisibility(View.GONE);
			
			for (int i = 0; i < teamsByGroup.size(); i++) {
				
				boolean isFirstposition = (position == 0);

				boolean isLastPosition = (position == (getCount() - 1));

				boolean isCurrentTeamGroupEqualToPreviousTeamGroup;

				if(isFirstposition == false)
				{
					Team prevTeam = getItem(position - 1);

					isCurrentTeamGroupEqualToPreviousTeamGroup = team.isSameGroup(prevTeam);
				}
				else
				{
					isCurrentTeamGroupEqualToPreviousTeamGroup = true;
					
					// TODO After sorting, set the team with highest score to bold.
					// TODO Also make the margin 1dp smaller programmatically.
					
//					holder.teamInfoGP.setTypeface(null, Typeface.BOLD);
//					holder.teamInfoPlusMinus.setTypeface(null, Typeface.BOLD);
//					holder.teamInfoPts.setTypeface(null, Typeface.BOLD);
				}

				boolean isBeginTimeEqualToNextItem;

				if(isLastPosition == false)
				{
					Team nextTeam = getItem(position + 1);

					isBeginTimeEqualToNextItem = team.isSameGroup(nextTeam);
				}
				else
				{
					isBeginTimeEqualToNextItem = false;
				}

				if (isFirstposition || isCurrentTeamGroupEqualToPreviousTeamGroup == false) 
				{
					/* Capitalized letters in header */
					String headerText = team.getPhaseId();
					holder.group.setText(headerText.toUpperCase());

					holder.headerContainer.setVisibility(View.VISIBLE);
					holder.group.setVisibility(View.VISIBLE);
					holder.dividerView.setVisibility(View.VISIBLE);
					holder.headerGP.setVisibility(View.VISIBLE);
					holder.headerPlusMinus.setVisibility(View.VISIBLE);
					holder.headerPts.setVisibility(View.VISIBLE);
				}
				
				boolean isLocalFlagDrawableResourceAvailableForTeam1 = team.isLocalFlagDrawableResourceAvailable();
				
				if(isLocalFlagDrawableResourceAvailableForTeam1)
				{
					holder.teamFlag.setImageDrawable(team.getLocalFlagDrawableResource());
				}
				else
				{
					ImageAware imageAware = new ImageViewAware(holder.teamFlag, false);
					
					// TODO
					//SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(team1.g, imageAware);
				}
				
				holder.teamName.setText(team.getDisplayName());
				
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
		private TextView group;
		private TextView teamName;
		private ImageView teamFlag;
		private TextView teamInfoGP;
		private TextView teamInfoPlusMinus;
		private TextView teamInfoPts;
		private View dividerView;
		private TextView headerGP;
		private TextView headerPlusMinus;
		private TextView headerPts;
		private RelativeLayout headerContainer;
	}
}
