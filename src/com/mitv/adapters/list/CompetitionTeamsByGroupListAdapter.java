
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
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class CompetitionTeamsByGroupListAdapter 
	extends BaseAdapter 
{
	private static final String TAG = CompetitionTeamsByGroupListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	private ViewHolder holder;
	
	private Map<String, List<Team>> teamsByGroup;
	private List<Team> teams;
	
	
	
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
			teams.size();
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

			// TODO - Fix this
			viewHolder.group = (TextView) rowView.findViewById(R.id.row_competition_airing_channels_for_broadcast);

			viewHolder.teamName = (TextView) rowView.findViewById(R.id.row_competition_team_one_name);
			viewHolder.teamFlag = (ImageView) rowView.findViewById(R.id.row_competition_team_one_flag);

			rowView.setTag(viewHolder);
		}

		holder = (ViewHolder) rowView.getTag();

		if (team != null) 
		{	
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
		private TextView teamName;
		private ImageView teamFlag;
	}
}
