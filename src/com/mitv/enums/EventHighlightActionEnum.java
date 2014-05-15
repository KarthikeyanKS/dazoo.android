
package com.mitv.enums;



import com.mitv.R;



public enum EventHighlightActionEnum 
{
	RED_CARD(0, R.drawable.competition_event_highlight_red_card),
	
	YELLOW_CARD(2048, R.drawable.competition_event_highlight_yellow_card),
	
	GOAL(4, R.drawable.competition_event_highlight_goal),
	
	PENALYTY_SCORED(3, R.drawable.competition_event_highlight_penalty_scored),
	
	PENALYTY_MISSED(5, R.drawable.competition_event_highlight_penalty_missed),
	
	SUBSTITUTION(16384, R.drawable.competition_event_highlight_substitution);

	
	
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
	
	
	
	public static EventHighlightActionEnum getTypeEnumFromCode(int code)
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
