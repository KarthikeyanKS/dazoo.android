
package com.millicom.mitv.models;



import com.millicom.mitv.models.gson.TVSportTypeJSON;
import com.mitv.notification.NotificationSQLElement;



public class TVSportType
	extends TVSportTypeJSON
{
	public TVSportType(NotificationSQLElement item)
	{
		this.sportTypeId = item.getSportTypeId();
		this.name = item.getSportTypeName();
	}
}