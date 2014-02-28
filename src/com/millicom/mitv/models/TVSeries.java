
package com.millicom.mitv.models;



import com.millicom.mitv.models.gson.TVSeriesJSON;
import com.millicom.mitv.models.sql.NotificationSQLElement;


public class TVSeries
	extends TVSeriesJSON
{
	public TVSeries(NotificationSQLElement item)
	{
		this.seriesId = item.getSeriesId(); 
		this.name = item.getSeriesName();
	}
}