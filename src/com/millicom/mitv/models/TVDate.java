
package com.millicom.mitv.models;



import java.util.Calendar;
import android.content.Context;
import com.millicom.mitv.models.gson.TVDateJSON;
import com.millicom.mitv.utilities.DateUtils;
import com.mitv.SecondScreenApplication;



public class TVDate 
	extends TVDateJSON
{
	@SuppressWarnings("unused")
	private static final String TAG = TVDate.class.getName();
	
	
	
	public TVDate(String dateRepresentation)
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		this.id = dateRepresentation;
		this.date = dateRepresentation;
		
		this.dateCalendar = DateUtils.convertFromStringToCalendar(dateRepresentation, context);
		
		this.displayName = DateUtils.buildDayOfTheWeekAsString(dateCalendar, context);
	}
	
	
	
	public Calendar getDateCalendar() 
	{
		if(dateCalendar == null) 
		{
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
			
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
