
package com.millicom.mitv.models;



import java.util.Calendar;
import android.content.Context;
import com.millicom.mitv.models.gson.TVDateJSON;
import com.millicom.mitv.utilities.DateUtils;
import com.mitv.SecondScreenApplication;



public class TVDate 
	extends TVDateJSON
{
	public Calendar getDateCalendar() 
	{
		if(dateCalendar == null) 
		{
			Context context = SecondScreenApplication.getInstance().getApplicationContext();
			
			dateCalendar = DateUtils.convertFromStringToCalendar(date, context);
		}
		
		return dateCalendar;
	}

	
	
	public String getDisplayName() 
	{
		return displayName;
	}

	
	
	//TODO Determine which of those dummy methods we need, and implement them
	/* HERE COMES DUMMY METHODS, ALL OF THEM MAY NOT BE NEEDED, INVESTIGATE! */
	public boolean isToday() 
	{
		//TODO implement or delete me
		return false;
	}
}
