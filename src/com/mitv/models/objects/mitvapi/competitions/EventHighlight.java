
package com.mitv.models.objects.mitvapi.competitions;



import com.mitv.enums.EventHighlightActionEnum;
import com.mitv.models.gson.mitvapi.competitions.EventHighlightJSON;



public class EventHighlight 
	extends EventHighlightJSON
{
	public EventHighlight(){}
	
	
	public boolean hasSubPerson()
	{
		return (subPersonId != 0) && (subPerson != null);
	}
	
	
	public EventHighlightActionEnum getActionType()
	{
		return EventHighlightActionEnum.getTypeEnumFromCode(actionCode);
	}
}
