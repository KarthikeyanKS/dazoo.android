
package com.mitv.adapters.list;



import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mitv.R;
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
		int count = lineups.size();
		
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
	
		final EventLineUp element = getItem(position);
	
		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();
	
			rowView = layoutInflater.inflate(R.layout.row_competition_event_lineup_teams_list_item, null);
	
			viewHolder.highlight = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_teams_name);
			viewHolder.time = (TextView) rowView.findViewById(R.id.row_competition_event_lineup_teams_time);
			
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null) 
		{
			// TODO - Replace with proper model values
			holder.highlight.setText("Bananas");
			holder.time.setText("99'");
		}
		else
		{
			Log.w(TAG, "Event is null");
		}
			
		return rowView;
	}
	
	
	
	private static class ViewHolder 
	{
		private TextView highlight;
		private TextView time;
	}
}