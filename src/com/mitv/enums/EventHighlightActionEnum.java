
package com.mitv.enums;



import com.mitv.R;



public enum EventHighlightActionEnum 
{
	UNKNOWN(0, "", R.drawable.competition_event_highlight_unknown),
	
	KICK_OFF_PERIOD(1, "0-1024-0", -1),
	
	END_OF_PERIOD(2, "0-2048-0", -1),
	
	INJURY_TIME(3, "0-32-0", -1),
	
	YELLOW_CARD(4, "2048-0-0", R.drawable.competition_event_highlight_yellow_card),
	
	GOAL(5, "4-0-0", R.drawable.competition_event_highlight_goal),
	
	GOAL_BY_FREE_KICK(6, "1028-0-0", R.drawable.competition_event_highlight_goal),
	
	GOAL_FROM_PENALTY(7, "12-0-0",  R.drawable.competition_event_highlight_penalty_scored),
	
	GOAL_BY_OWN_TEAM(8, "68-0-0",  R.drawable.competition_event_highlight_goal_by_own_team),
	
	RED_CARD_2_YELLOWS(9, "4096-0-0",  R.drawable.competition_event_highlight_red_card),
	
	RED_CARD_DIRECT(10, "8192-0-0",  R.drawable.competition_event_highlight_red_card),
	
	SUBSTITUTION(11, "16384-0-0",  R.drawable.competition_event_highlight_substitution),
	
	MATCH_SUSPENDED(12, "131072-262144-128", -1),
	
	MATCH_ABANDONED(13, "131072-262144-256", -1),
	
	PLAY_RESTARTED(14, "131072-262144-1024", -1),
	
	MATCH_RESCHEDULED_TO_BE_RESUMED(15, "131072-262144-2048",  -1),
	
	PENALYTY_SAVED_BY_THE_GOALKEEPER(16, "264-0-0",  R.drawable.competition_event_highlight_penalty_missed),
	
	PENALYTY_MISSED(17, "65544-0-0",  R.drawable.competition_event_highlight_penalty_missed),
	
	CORNER(18, "512-0-0", R.drawable.competition_event_highlight_corner),

    KICK_TO_GOAL(19, "1048576-0-0", R.drawable.competition_event_highlight_shoot_on_goal);
	
	
	
	private final int id;
	private final String code;
	private final int drawableResourceID;
	
	
	
	EventHighlightActionEnum(
			int id, 
			String code,
			int drawableResourceID) 
	{
		this.id = id;
		this.code = code;
		this.drawableResourceID = drawableResourceID;
	}
	
	

	public int getId() 
	{
		return id;
	}
	
	
	
	public String getCode() 
	{
		return code;
	}
	
		
	
	public int getDrawableResourceID()
	{
		return drawableResourceID;
	}
	
	
	
	public boolean isGoal()
	{
		boolean isGoal = (this == GOAL ||
						 this == GOAL_BY_FREE_KICK ||
						 this == GOAL_FROM_PENALTY ||
				         this == GOAL_BY_OWN_TEAM);
				         
		return isGoal;
	}
	
	
	
	public static EventHighlightActionEnum getTypeEnumFromCode(String code)
	{
		for(EventHighlightActionEnum result: EventHighlightActionEnum.values())
		{
			boolean matchesCode = result.getCode().equals(code);
			
			if(matchesCode)
			{
				return result;
			}
			// No need for else
		}

		return EventHighlightActionEnum.UNKNOWN;
	}
}
