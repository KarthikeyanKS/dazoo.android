
package com.mitv.adapters.list;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionEventStandingsListAdapter 
	extends BaseAdapter 
{
	private static final String TAG = CompetitionEventStandingsListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private List<Standings> standings;
		
	
	
	public CompetitionEventStandingsListAdapter(
			final Activity activity,
			final List<Standings> standings)
	{
		this.standings = standings;
		
		this.activity = activity;
	
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	
	@Override
	public int getCount()
	{
		int count = standings.size();
		
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
		View rowView = convertView;
	
		final Standings element = getItem(position);
	
		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();
	
			rowView = layoutInflater.inflate(R.layout.row_competition_event_team_standings_list_item, null);
	
			viewHolder.headerContainer = (RelativeLayout) rowView.findViewById(R.id.row_competition_event_group_header_container);
					
			viewHolder.teamFlag = (ImageView) rowView.findViewById(R.id.row_competition_event_lineup_team_flag);
			viewHolder.teamName = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_team_name);
			viewHolder.teamGP = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_team_gp);
			viewHolder.teamPlusMinus = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_team_plus_minus);
			viewHolder.teamPoints = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_team_pts);
			
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
			
			long teamID = element.getTeamId();
			
			Team team = ContentManager.sharedInstance().getFromCacheTeamByID(teamID);
			
			if(team != null)
			{
				ImageAware imageAware = new ImageViewAware(holder.teamFlag, false);
					
				String teamFlagUrl = team.getImages().getFlag().getImageURLForDeviceDensityDPI();
						
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(teamFlagUrl, imageAware);
			}

			String teamGPAsString = new Integer(element.getMatches()).toString();
			String teamPlusMinusAsString = new Integer(element.getGoalsForMinusGoalsAgainst()).toString();
			String teamPointsAsString = new Integer(element.getPoints()).toString();

			holder.teamGP.setText(teamGPAsString);
			holder.teamPlusMinus.setText(teamPlusMinusAsString);
			holder.teamPoints.setText(teamPointsAsString);
			
			holder.teamName.setText(element.getTeam());
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
		
		private ImageView teamFlag;
		private TextView teamName;		
		private TextView teamGP;
		private TextView teamPlusMinus;
		private TextView teamPoints;
	}
}