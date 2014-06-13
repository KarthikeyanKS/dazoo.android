

package com.mitv.models.objects.mitvapi.competitions;



import android.content.Context;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.EventHighlightActionEnum;
import com.mitv.models.gson.mitvapi.competitions.EventHighlightJSON;
import com.mitv.utilities.DateUtils;



public class EventHighlight 
	extends EventHighlightJSON
{
	public EventHighlight(){}



	public String getAction() 
	{
		StringBuilder sb = new StringBuilder();
		
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		switch (getType())
		{
			case KICK_OFF_PERIOD:
			{
				sb.append(context.getString(R.string.event_page_highlight_kick_off));
				break;
			}
	
			case END_OF_PERIOD:
			{
				sb.append(context.getString(R.string.event_page_highlight_end_of_game));
				break;
			}
			
			case INJURY_TIME:
			{
				sb.append(context.getString(R.string.event_page_highlight_injury_time));
				break;
			}
			
			case YELLOW_CARD:
			{
				sb.append(context.getString(R.string.event_page_highlight_yellow_card));
				break;
			}
			
			case GOAL:
			{
				sb.append(context.getString(R.string.event_page_highlight_goal));
				break;
			}
			
			case GOAL_BY_FREE_KICK:
			{
				sb.append(context.getString(R.string.event_page_highlight_goal_by_free_kick));
				break;
			}
			
			case GOAL_FROM_PENALTY:
			{
				sb.append(context.getString(R.string.event_page_highlight_goal_from_penalty));
				break;
			}
			
			case GOAL_BY_OWN_TEAM:
			{
				sb.append(context.getString(R.string.event_page_highlight_goal_by_own_team));
				break;
			}
			
			case RED_CARD_2_YELLOWS:
			{
				sb.append(context.getString(R.string.event_page_highlight_red_card_2_yellows));
				break;
			}
			
			case RED_CARD_DIRECT:
			{
				sb.append(context.getString(R.string.event_page_highlight_red_card_direct));
				break;
			}
	
			case MATCH_SUSPENDED:
			{
				sb.append(context.getString(R.string.event_page_highlight_match_suspended));
				break;
			}
	
			case MATCH_ABANDONED:
			{
				sb.append(context.getString(R.string.event_page_highlight_match_abandoned));
				break;
			}
	
			case PLAY_RESTARTED:
			{
				sb.append(context.getString(R.string.event_page_highlight_play_restarted));
				break;
			}
	
			case MATCH_RESCHEDULED_TO_BE_RESUMED:
			{
				sb.append(context.getString(R.string.event_page_highlight_match_rescheduled));
				break;
			}
			
			case PENALYTY_SAVED_BY_THE_GOALKEEPER:
			{
				sb.append(context.getString(R.string.event_page_highlight_penalty_saved_by_goalkeeper));
				break;
			}
			
			case PENALYTY_MISSED:
			{
				sb.append(context.getString(R.string.event_page_highlight_penalty_missed));
				break;
			}
			
			case CORNER:
			{
				sb.append(context.getString(R.string.event_page_highlight_corner));
				break;
			}
			
			case KICK_TO_GOAL:
			{
				sb.append(context.getString(R.string.event_page_highlight_kick_to_goal));
				break;
			}
	
			case SUBSTITUTION:
			{
				sb.append("");
				break;
			}	
			
			case UNKNOWN:
			default:
			{
				sb.append("");
				break;
			}	
		}
		
		return sb.toString();
	}
	
	
	public boolean hasSubPerson()
	{
		boolean hasSubPerson = (getSubPersonShort().isEmpty() == false);
		
		return hasSubPerson;
	}
	
	
	
	public EventHighlightActionEnum getType()
	{
		return EventHighlightActionEnum.getTypeEnumFromCode(getHighlightCode());
	}
	
	
	
	public int getActionTimeInMinutes()
	{
		long minutesAsLong = (getActionTime() / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
		
		return (int) minutesAsLong;
	}
}
