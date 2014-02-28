
package com.millicom.mitv.models;



import com.millicom.mitv.models.gson.TVSeriesJSON;
import com.mitv.model.NotificationDbItem;


public class TVSeries
	extends TVSeriesJSON
{
	public TVSeries(NotificationDbItem item)
	{
		this.seriesId = item.getSeriesId(); 
		this.name = item.getSeriesName();
	}
}