
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
		this.id = dateRepresentation;
		this.date = dateRepresentation;
		
		this.dateCalendar = DateUtils.convertFromStringToCalendar(dateRepresentation);
		
		this.displayName = DateUtils.buildDayOfTheWeekAsString(dateCalendar);
	}
	
	
	
	public Calendar getDateCalendar() 
	{
		if(dateCalendar == null) 
		{	
			dateCalendar = DateUtils.convertFromStringToCalendar(date);
		}
		
		return dateCalendar;
	}

	
	
	public String getDisplayName() 
	{
		return displayName;
	}

	
	
	public boolean isToday() 
	{
		return DateUtils.isToday(getDateCalendar());
	}
}