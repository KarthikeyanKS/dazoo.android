
package com.millicom.mitv.models;



import android.util.Log;
import com.millicom.mitv.models.gson.TVSeriesSeasonJSON;
import com.mitv.model.NotificationDbItem;



public class TVSeriesSeason
	extends TVSeriesSeasonJSON
{
	private static final String TAG = TVSeriesSeason.class.getName();
	
	
	
	public TVSeriesSeason()
	{}
	
	
	
	public TVSeriesSeason(NotificationDbItem item)
	{
		String seasonNumberAsString = item.getProgramSeason();
		
		try
		{
			this.number = Integer.parseInt(seasonNumberAsString);
		}
		catch(NumberFormatException nfex)
		{
			Log.e(TAG, nfex.getMessage(), nfex);
			
			this.number = 0;
		}
	}
	
	
	
	public void setNumber(Integer number) 
	{
		this.number = number;
	}
}