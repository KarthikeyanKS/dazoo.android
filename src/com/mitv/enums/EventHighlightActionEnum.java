
package com.mitv.enums;



import com.mitv.R;



public enum EventHighlightActionEnum 
{
	KICK_OFF(1, -1),
	
	END_OF_PERIOD_1H(2, -1),
	
	KICK_OFF_2H(3, -1),
	
	END_OF_PERIOD_2H(4, -1),
	
	INJURY_TIME_2H(5, -1),
	
	KICK_OFF_EXTRA_TIME_1(6, -1),
	
	END_OF_PERIOD_EXTRA_TIME_1(7, -1),
	
	INJURY_TIME_EXTRA_TIME_1(8, -1),
	
	KICK_OFF_EXTRA_TIME_2(9, -1),
	
	END_OF_PERIOD_EXTRA_TIME_2(10, -1),
	
	INJURY_TIME_EXTRA_TIME_2(11, -1),
	
	KICK_OFF_PENALTIES(12, -1),
	
	END_OF_PERIOD_PENALTIES(13, -1),
	
	END_OF_GAME(14, -1),
	
	YELLOW_CARD(15, R.drawable.competition_event_highlight_yellow_card),
	
	GOAL(16, R.drawable.competition_event_highlight_goal),
	
	GOAL_BY_PENALTY(18, R.drawable.competition_event_highlight_penalty_scored),
	
	GOAL_BY_OWN_TEAM(19, R.drawable.competition_event_highlight_goal),
	
	RED_CARD_2_YELLOWS(20, R.drawable.competition_event_highlight_red_card),
	
	RED_CARD_DIRECT(21, R.drawable.competition_event_highlight_red_card),
	
	SUBSTITUTION(22, R.drawable.competition_event_highlight_substitution),
	
	MATCH_SUSPENDED(24, -1),
	
	MATCH_ABANDONED(25, -1),
	
	PLAY_RESTARTED(26, -1),
	
	MATCH_RESCHEDULED_TO_BE_RESUMED(27, -1),
	
	PENALYTY_MISSED(28, R.drawable.competition_event_highlight_penalty_missed);
	
	
	
	private final int id;
	private final int drawableResourceID;
	
	
	
	EventHighlightActionEnum(int id, int drawableResourceID) 
	{
		this.id = id;
		this.drawableResourceID = drawableResourceID;
	}
	
	

	public int getId() 
	{
		return id;
	}
	
	
	
	public int getDrawableResourceID()
	{
		return drawableResourceID;
	}
	
	
	
	public static EventHighlightActionEnum getTypeEnumFromCode(long code)
	{
		for(EventHighlightActionEnum result: EventHighlightActionEnum.values())
		{
			if(result.getId() == code) 
			{
				return result;
			}
			// No need for else
		}

		return EventHighlightActionEnum.SUBSTITUTION;
	}
}
