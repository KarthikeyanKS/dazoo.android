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
import com.mitv.enums.EventHighlightActionEnum;
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
			viewHolder.middleName = (TextView) rowView.findViewById(R.id.row_competition_event_highlight_middle_name);
			viewHolder.middleSeparatorBottom = (ImageView) rowView.findViewById(R.id.row_competition_event_highlight_middle_separator_icon_bottom);
					
			rowView.setTag(viewHolder);
		}
	
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder != null)
		{
			boolean isFirstPosition = (position == 0);
			boolean isLastPosition = (position == (getCount() - 1));
			
			long eventID = element.getEventId(); 
			
			Event event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
		
			boolean isFinished = event.isFinished();
			
			if(event != null)
			{
				if (isFirstPosition)
				{
					if(isFinished)
					{
						holder.middleSeparatorTop.setVisibility(View.GONE);
					}
					else
					{
						holder.middleSeparatorTop.setVisibility(View.VISIBLE);
					}
				}
				else if (isLastPosition) 
				{
					holder.middleSeparatorBottom.setVisibility(View.GONE);
				}

				holder.middleName.setVisibility(View.GONE);

				EventHighlightActionEnum eventActionType = element.getType();
				
				if(eventActionType.getDrawableResourceID() == -1)
				{
					holder.leftLayout.setVisibility(View.GONE);
					holder.rightLayout.setVisibility(View.GONE);
					holder.middleIcon.setVisibility(View.GONE);
					holder.middleName.setVisibility(View.VISIBLE);
					
					StringBuilder sb = new StringBuilder();
					
					switch (eventActionType)
					{
						case KICK_OFF_PERIOD:
						{
							sb.append(activity.getString(R.string.event_page_highlight_kick_off));
							break;
						}
						
						case END_OF_PERIOD:
						{
							sb.append(activity.getString(R.string.event_page_highlight_end_of_game));
							break;
						}
						
						case MATCH_SUSPENDED:
						{
							sb.append(activity.getString(R.string.event_page_highlight_match_suspended));
							break;
						}
						
						case MATCH_ABANDONED:
						{
							sb.append(activity.getString(R.string.event_page_highlight_match_abandoned));
							break;
						}
						
						case PLAY_RESTARTED:
						{
							sb.append(activity.getString(R.string.event_page_highlight_play_restarted));
							break;
						}
						
						case MATCH_RESCHEDULED_TO_BE_RESUMED:
						{
							sb.append(activity.getString(R.string.event_page_highlight_match_rescheduled));
							break;
						}
						
						default:
						{
							sb.append(element.getAction());
							break;
						}					
					}
					
					holder.middleName.setText(sb);
				}
				else if(isHomeTeam(element))
				{
					int drawableResourceID = element.getType().getDrawableResourceID();
					
					holder.middleIcon.setBackgroundResource(drawableResourceID);
					
					holder.leftLayout.setVisibility(View.VISIBLE);
					holder.rightLayout.setVisibility(View.GONE);
					holder.middleIcon.setVisibility(View.VISIBLE);
					holder.middleName.setVisibility(View.GONE);

					holder.leftName.setText(element.getPersonShort());

					boolean isSubstitution = (eventActionType == EventHighlightActionEnum.SUBSTITUTION);
					
					if(isSubstitution && element.hasSubPerson())
					{
						holder.leftNameExtra.setVisibility(View.VISIBLE);
						holder.leftNameExtra.setText(element.getSubPersonShort());
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
					int drawableResourceID = element.getType().getDrawableResourceID();
					
					holder.middleIcon.setBackgroundResource(drawableResourceID);
					
					holder.leftLayout.setVisibility(View.GONE);
					holder.rightLayout.setVisibility(View.VISIBLE);

					holder.rightName.setText(element.getPersonShort());

					boolean isSubstitution = (eventActionType == EventHighlightActionEnum.SUBSTITUTION);
					
					if(isSubstitution && element.hasSubPerson())
					{
						holder.rightNameExtra.setVisibility(View.VISIBLE);
						holder.rightNameExtra.setText(element.getSubPersonShort());
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
					Log.w(TAG, "Highlight " + element.getEventId() + " is not for home or away team");
				}
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
		private TextView middleName;
		private ImageView middleSeparatorBottom;
	}
}