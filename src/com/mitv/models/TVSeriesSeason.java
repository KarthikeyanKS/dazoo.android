
package com.mitv.models;



import android.util.Log;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.TVSeriesSeasonJSON;
import com.mitv.models.sql.NotificationSQLElement;



public class TVSeriesSeason
	extends TVSeriesSeasonJSON implements GSONDataFieldValidation
{
	private static final String TAG = TVSeriesSeason.class.getName();
	
	
	
	public TVSeriesSeason()
	{}
	
	
	
	public TVSeriesSeason(NotificationSQLElement item)
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
	
	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (getNumber() != null);
		return areDataFieldsValid;
	}
}