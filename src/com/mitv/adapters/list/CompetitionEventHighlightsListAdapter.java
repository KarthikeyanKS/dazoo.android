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
import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Event;
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
	
			viewHolder.leftLayout = (RelativeLayout) rowView.findViewById(R.id.row_competition_event_highlight_left_layout);
			viewHolder.leftName = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_name_left);
			viewHolder.leftNameExtra = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_name_extra_left);
			viewHolder.leftTime = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_time_left);
			viewHolder.leftDetails = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_details_left);
						
			viewHolder.rightLayout = (RelativeLayout) rowView.findViewById(R.id.row_competition_event_highlight_right_layout);
			viewHolder.rightName = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_name_right);
			viewHolder.rightNameExtra = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_name_extra_right);
			viewHolder.rightTime = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_time_right);
			viewHolder.rightDetails = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_details_right);
			
			viewHolder.middleSeparatorTop = (ImageView) rowView.findViewById(R.id.row_competition_event_highlight_middle_separator_icon_top);
			viewHolder.middleIcon = (ImageView) rowView.findViewById(R.id.row_competition_event_highlight_middle_icon);
			viewHolder.middleSeparatorBottom = (ImageView) rowView.findViewById(R.id.row_competition_event_highlight_middle_separator_icon_bottom);
					
			viewHolder.middleTopLayout = (RelativeLayout) rowView.findViewById(R.id.row_competition_event_highlight_middle_top_layout);
			viewHolder.middleTopName = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_middle_name_top);
			
			viewHolder.middleBottomLayout = (RelativeLayout) rowView.findViewById(R.id.row_competition_event_highlight_middle_bottom_layout);
			viewHolder.middleBottomName = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_middle_name_bottom);
			
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null)
		{
			boolean isFirstPosition = (position == 0);
			boolean isLastPosition = (position == (getCount() - 1));
			
			long eventID = element.getEventId(); 
			
			Event event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
		
			if(event != null)
			{
				boolean hasEventEnded = event.hasEnded();
				boolean hasEventStarted = event.hasStarted();

				if (isFirstPosition) 
				{
					if(hasEventEnded)
					{
						StringBuilder sb = new StringBuilder();
						sb.append(activity.getString(R.string.event_page_highlight_end_result))
						.append(" ")
						.append(event.getScoreAsString());
	
						holder.middleTopName.setText(sb);
						
						holder.middleTopName.setVisibility(View.VISIBLE);
					}
					else
					{
						holder.middleTopName.setVisibility(View.GONE);
					}
				}
				else if (isLastPosition) 
				{
					holder.middleSeparatorTop.setVisibility(View.VISIBLE);
					
					if(hasEventStarted)
					{
						holder.middleBottomName.setText(activity.getString(R.string.event_page_highlight_kick_off));
						
						holder.middleBottomName.setVisibility(View.VISIBLE);
					}
					else
					{
						holder.middleBottomName.setVisibility(View.GONE);
					}
				}
				else
				{
					holder.middleTopName.setVisibility(View.GONE);
					holder.middleBottomName.setVisibility(View.GONE);
				}

				int drawableResourceID = element.getActionType().getDrawableResourceID();

				holder.middleIcon.setBackgroundResource(drawableResourceID);

				if(isHomeTeam(element))
				{
					holder.leftLayout.setVisibility(View.VISIBLE);
					holder.rightLayout.setVisibility(View.GONE);

					holder.leftName.setText(element.getPerson());

					if(element.hasSubPerson())
					{
						holder.leftNameExtra.setVisibility(View.VISIBLE);
						holder.leftNameExtra.setText(element.getSubPerson());
						holder.leftNameExtra.setTextColor(activity.getResources().getColor(R.color.competition_event_highlight_substitution_player_out));

						holder.leftDetails.setVisibility(View.GONE);							
							
						holder.leftName.setTextColor(activity.getResources().getColor(R.color.competition_event_highlight_substitution_player_in));
						
						RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.leftTime.getLayoutParams();
						layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
						holder.leftTime.setLayoutParams(layoutParams);
					}
					else
					{
						holder.leftNameExtra.setVisibility(View.GONE);
						
						holder.leftDetails.setVisibility(View.VISIBLE);
						holder.leftDetails.setText(element.getAction());
					}

					holder.leftTime.setText(element.getActionMinute());

				}
				else if(isAwayTeam(element))
				{
					holder.leftLayout.setVisibility(View.GONE);
					holder.rightLayout.setVisibility(View.VISIBLE);

					holder.rightName.setText(element.getPerson());

					if(element.hasSubPerson())
					{
						holder.rightNameExtra.setVisibility(View.VISIBLE);
						holder.rightNameExtra.setText(element.getSubPerson());
						holder.rightNameExtra.setTextColor(activity.getResources().getColor(R.color.competition_event_highlight_substitution_player_out));

						holder.rightDetails.setVisibility(View.GONE);
						
						holder.rightName.setTextColor(activity.getResources().getColor(R.color.competition_event_highlight_substitution_player_in));
						
						RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.rightTime.getLayoutParams();
						layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
						holder.rightTime.setLayoutParams(layoutParams);
					}
					else
					{
						holder.rightNameExtra.setVisibility(View.GONE);
						
						holder.rightDetails.setVisibility(View.VISIBLE);
						holder.rightDetails.setText(element.getAction());
					}

					holder.rightTime.setText(element.getActionMinute());
				}
				else
				{
					Log.w(TAG, "Hightlight is not for home or away team");
				}
			}
			else
			{
				Log.w(TAG, "Event is null");
			}
		}
			
		return rowView;
	}
	
	
	
	private boolean isHomeTeam(EventHighlight highlight)
	{
		boolean isHomeTeam = false;
		
		long eventID = highlight.getEventId(); 
		
		Event event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
		
		if(event != null)
		{
			long highlightTeamID = highlight.getTeamId(); 
			
			isHomeTeam = (event.getHomeTeamId() == highlightTeamID);
		}
		
		return isHomeTeam;
	}
	
	
	
	private boolean isAwayTeam(EventHighlight highlight)
	{
		boolean isAwayTeam = false;
		
		long eventID = highlight.getEventId(); 
		
		Event event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
		
		if(event != null)
		{
			long highlightTeamID = highlight.getTeamId(); 
			
			isAwayTeam = (event.getAwayTeamId() == highlightTeamID);
		}
		
		return isAwayTeam;
	}
	
	
	
	private static class ViewHolder 
	{
		private RelativeLayout leftLayout;
		private TextView leftName;
		private TextView leftNameExtra;
		private TextView leftTime;
		private TextView leftDetails;
		
		private RelativeLayout rightLayout;
		private TextView rightName;
		private TextView rightNameExtra;
		private TextView rightTime;
		private TextView rightDetails;
		
		private ImageView middleSeparatorTop;
		private ImageView middleIcon;
		private ImageView middleSeparatorBottom;
		
		private RelativeLayout middleTopLayout;
		private TextView middleTopName;
		
		private RelativeLayout middleBottomLayout;
		private TextView middleBottomName;
	}
}