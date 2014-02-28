
package com.millicom.mitv.models;



import com.millicom.mitv.models.gson.TVSportTypeJSON;
import com.millicom.mitv.models.sql.NotificationSQLElement;



public class TVSportType
	extends TVSportTypeJSON
{
	public TVSportType(NotificationSQLElement item)
	{
		this.sportTypeId = item.getSportTypeId();
		this.name = item.getSportTypeName();
	}
}