
package com.mitv.models;



import java.util.Calendar;

import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.TVDateJSON;
import com.mitv.utilities.DateUtils;



public class TVDate 
	extends TVDateJSON 
	implements GSONDataFieldValidation
{
	@SuppressWarnings("unused")
	private static final String TAG = TVDate.class.getName();
	
	
	
	public TVDate(String dateRepresentation)
	{	
		this.id = dateRepresentation;
		this.date = dateRepresentation;
		
		this.dateCalendar = getDateCalendar();
		
		this.displayName = DateUtils.buildDayOfTheWeekAsString(dateCalendar);
	}
	
	
	
	public Calendar getDateCalendar() 
	{
		if(dateCalendar == null) 
		{	
			dateCalendar = DateUtils.convertFromYearAndDateStringToCalendar(date);
		}
		
		return dateCalendar;
	}


	
	public boolean isToday() 
	{
		return DateUtils.isToday(getDateCalendar());
	}



	@Override
	public boolean areDataFieldsValid() {
		final int yearOf2000 = 2000;

		boolean areDataFieldsValid = (!TextUtils.isEmpty(getId()) && !TextUtils.isEmpty(getDisplayName()) && 
				(getDateCalendar() != null) && getDateCalendar().get(Calendar.YEAR) > yearOf2000
				);
		return areDataFieldsValid;
	}
}