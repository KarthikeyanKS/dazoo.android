
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
		return (subPersonId != 0) && (subPerson != null);
	}
	
	
	public EventHighlightActionEnum getType()
	{
		return EventHighlightActionEnum.getTypeEnumFromCode(highlightCode); // TODO cannot use highlightCode
	}
	
	
	
	public int getActionInfoInMinutes()
	{
		long minutesAsLong = (actionInfo / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE);
		
		return (int) minutesAsLong;
	}
}
