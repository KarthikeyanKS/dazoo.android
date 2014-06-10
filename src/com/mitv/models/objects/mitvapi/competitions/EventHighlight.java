
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.enums.EventHighlightActionEnum;
import com.mitv.models.gson.mitvapi.competitions.EventHighlightJSON;
import com.mitv.utilities.DateUtils;



public class EventHighlight 
	extends EventHighlightJSON
{
	public EventHighlight(){}

	
	
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
