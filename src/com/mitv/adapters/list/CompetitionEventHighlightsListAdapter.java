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
import android.widget.TextView;
import com.mitv.R;
import com.mitv.models.objects.mitvapi.competitions.EventHighlight;



public class CompetitionEventHighlightsListAdapter
	extends BaseAdapter 
{
	private static final String TAG = CompetitionEventHighlightsListAdapter.class.getName();
	
	
	private LayoutInflater layoutInflater;
	private Activity activity;
	
	private List<EventHighlight> highlights;
		
	
	
	public CompetitionEventHighlightsListAdapter(
			final Activity activity,
			final List<EventHighlight> highlights)
	{
		this.highlights = highlights;
		
		this.activity = activity;
	
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	
	@Override
	public int getCount()
	{
		int count = highlights.size();
		
		return count;
	}
	
	
	
	@Override
	public EventHighlight getItem(int position) 
	{
		EventHighlight element = null;
		
		if (highlights != null)
		{
			element = highlights.get(position);
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
	
		final EventHighlight element = getItem(position);
	
		if (rowView == null) 
		{
			ViewHolder viewHolder = new ViewHolder();
	
			rowView = layoutInflater.inflate(R.layout.row_competition_event_highlights_list_item, null);
	
			viewHolder.highlight = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_name);
			viewHolder.time = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_time);
			viewHolder.timeLineIcon = (ImageView) rowView.findViewById(R.id.row_competition_event_highlight_timeline_icon);
			
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null) 
		{
			holder.highlight.setText(element.getAction());
			holder.time.setText(element.getActionMinute());
			
			// TODO - Set the timeline icon
			//holder.timeLineIcon
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
		private ImageView timeLineIcon;
	}
}