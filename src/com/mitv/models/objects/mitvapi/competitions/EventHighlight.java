

package com.mitv.models.objects.mitvapi.competitions;



import android.content.Context;
import android.util.Log;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.EventHighlightActionEnum;
import com.mitv.enums.EventHighlightPeriodSortEnum;
import com.mitv.models.gson.mitvapi.competitions.EventHighlightJSON;
import com.mitv.utilities.DateUtils;



public class EventHighlight 
	extends EventHighlightJSON
{
	private static final String TAG = EventHighlight.class.getName();
	
	
	public EventHighlight(){}



	public String getAction()
	{
		StringBuilder sb = new StringBuilder();
		
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		EventHighlightActionEnum eventHighlightAction = getType();
		
		EventHighlightPeriodSortEnum eventHighlightPeriodSort = getPeriodSortEnum();
		
		switch (eventHighlightAction)
		{
			case KICK_OFF_PERIOD:
			{
				switch(eventHighlightPeriodSort) 
				{
					case FIRST_HALF:
					{
						sb.append(context.getString(R.string.event_page_highlight_start_of_game));
						break;
					}
					
					case SECOND_HALF:
					{
						sb.append(context.getString(R.string.event_page_highlight_start_of_second_half));
						break;
					}
					
					case FIRST_EXTRA_TIME: {
						sb.append(context.getString(R.string.event_page_highlight_start_of_extra_time));
						break;
					}
					
					case SECOND_EXTRA_TIME:
					{
						sb.append(context.getString(R.string.event_page_highlight_kick_off_extra_time));
						break;
					}
					
					case PENALTY_SHOOTOUT: {
						sb.append(context.getString(R.string.event_page_highlight_start_of_penalty));
						break;
					}
					
					default:
					{
						sb.append(this.getAction());
						break;
					}
				}
				
				break;
			}
	
			case END_OF_PERIOD:
			{
				switch(eventHighlightPeriodSort) 
				{
					case FIRST_HALF:
					{
						sb.append(context.getString(R.string.event_page_highlight_end_of_first_half));
						sb.append(" (");
						sb.append(getScoreAsString());
						sb.append(")");
						break;
					}
					
					case SECOND_HALF:
					{
						sb.append(context.getString(R.string.event_page_highlight_end_of_second_half));
						sb.append(" (");
						sb.append(getScoreAsString());
						sb.append(")");
						break;
					}
					
					case FIRST_EXTRA_TIME:
					{
						sb.append(context.getString(R.string.event_page_highlight_end_extra_time));
						break;
					}
					
					case SECOND_EXTRA_TIME:
					{
						sb.append(context.getString(R.string.event_page_highlight_end_of_extra_time));
						break;
					}
					
					case END_OF_GAME:
					{
						sb.append(context.getString(R.string.event_page_highlight_end_of_game));
						break;
					}
					
					case PENALTY_SHOOTOUT: {
						sb.append(context.getString(R.string.event_page_highlight_end_of_penalty));
						sb.append(" (");
						sb.append(getScoreAsString());
						sb.append(")");
						break;
					}
					
					default:
					{
						sb.append(this.getAction());
						break;
					}
				}

				break;
			}
			
			case INJURY_TIME:
			{
				sb.append(context.getString(R.string.event_page_highlight_injury_time));
				sb.append(" (+");
				sb.append(getExtraTimeInMinutes());
				sb.append("')");
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
	
	
	
	public boolean isFirstHalfKickOff()
	{
		EventHighlightActionEnum eventHighlightAction = getType();
		
		EventHighlightPeriodSortEnum eventHighlightPeriodSort = getPeriodSortEnum();
		
		boolean isFirstHalfKickOff = (eventHighlightAction == EventHighlightActionEnum.KICK_OFF_PERIOD) && 
				 					 (eventHighlightPeriodSort == EventHighlightPeriodSortEnum.FIRST_HALF);
		
		return isFirstHalfKickOff;
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
	
	
	public EventHighlightPeriodSortEnum getPeriodSortEnum()
	{
		return EventHighlightPeriodSortEnum.getTypeEnumFromCode(getPeriodSort());
	}
	
	
	
	public int getActionTimeInMinutes()
	{
		long minutesAsLong = (getActionTime() / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
		
		return (int) minutesAsLong;
	}
	
	
	
	private Integer getExtraTimeInMinutes()
	{
		Integer extraMinutes;
		
		EventHighlightActionEnum eventHighlightAction = getType();
		
		if(eventHighlightAction != EventHighlightActionEnum.INJURY_TIME)
		{
			Log.w(TAG, "Attempting to get extra minutes for an invalid type of EventHighlightActionEnum.");
			
			extraMinutes = Integer.valueOf(0);
		}
		else
		{
			String extraMillisecondsAsString = getActionInfo();
			
			Integer extraMilliseconds;
			
			try
			{
				extraMilliseconds = Integer.parseInt(extraMillisecondsAsString);
			}
			catch(NumberFormatException nfex)
			{
				extraMilliseconds = Integer.valueOf(0);
				
				Log.w(TAG, nfex.getMessage());
			}
			
			extraMinutes = (extraMilliseconds / (int) DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
		}
		
		return extraMinutes;
	}
	
	
	
	private String getScoreAsString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getHomeGoals());
		sb.append(" - ");
		sb.append(getAwayGoals());
		
		return sb.toString();
	}
}
