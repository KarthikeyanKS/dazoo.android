
package com.millicom.mitv.models;



import com.millicom.mitv.models.gson.TVSportTypeJSON;
import com.mitv.model.NotificationDbItem;



public class TVSportType
	extends TVSportTypeJSON
{
	public TVSportType(NotificationDbItem item)
	{
		this.sportTypeId = item.getSportTypeId();
		this.name = item.getSportTypeName();
	}
}