
package com.mitv.models.objects.mitvapi;



import java.util.Calendar;

import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVDateJSON;
import com.mitv.models.orm.TVDateORM;
import com.mitv.utilities.DateUtils;



public class TVDate 
	extends TVDateJSON 
	implements GSONDataFieldValidation
{
	@SuppressWarnings("unused")
	private static final String TAG = TVDate.class.getName();
		
	
	
	
	public TVDate(long timeinMilliseconds)
	{	
		Calendar calendar = DateUtils.getCalendarForStartOfTVDay(timeinMilliseconds);
		
		String calendarDateRepresentation = DateUtils.buildDateCompositionAsString(calendar);
		
		this.id = calendarDateRepresentation;
		this.date = calendarDateRepresentation;
		this.displayName = DateUtils.buildDayOfTheWeekAsString(getStartOfTVDayCalendarLocal());
	}
	
	
	/*
	 * The input string format should be in the format: "yyyy-MM-dd"
	 */
	public TVDate(String dateRepresentation)
	{	
		this.id = dateRepresentation;
		this.date = dateRepresentation;
		this.displayName = DateUtils.buildDayOfTheWeekAsString(getStartOfTVDayCalendarLocal());
	}
	
	
	
	public TVDate(TVDateORM tvDateORM)
	{
		this.id =  tvDateORM.getId();
		this.date = tvDateORM.getDisplayName();
		
		/* This field will be set to null, as it needs to be recalculated */
		this.displayName = null;
	}
	
	
	
	@Override
	public String getDisplayName() 
	{
		String displayName = super.getDisplayName();
		
		this.displayName = DateUtils.buildDayOfTheWeekAsString(getStartOfTVDayCalendarLocal());
		
		return displayName;
	}
	
	
	
	public Calendar getStartOfTVDayCalendarGMT() 
	{
		Calendar startOfTVDayCalendar = DateUtils.getCalendarForStartOfTVDay(date);
		
		return startOfTVDayCalendar;
	}
	
	
	
	public Calendar getStartOfTVDayCalendarLocal() 
	{
		Calendar startOfTVDayCalendar = DateUtils.getCalendarForStartOfTVDay(date);
		
		startOfTVDayCalendar = DateUtils.setTimeZoneAndOffsetToLocal(startOfTVDayCalendar);
		
		return startOfTVDayCalendar;
	}
	
	
	
	public Calendar getEndOfTVDayCalendarGMT() 
	{
		Calendar endOfTVDayCalendar = DateUtils.getCalendarForEndOfTVDayUsingStartCalendar(getStartOfTVDayCalendarGMT());
		
		return endOfTVDayCalendar;
	}
	
	
	
	public Calendar getEndOfTVDayCalendarLocal() 
	{
		Calendar endOfTVDayCalendar = DateUtils.getCalendarForEndOfTVDayUsingStartCalendar(getEndOfTVDayCalendarGMT());
		
		endOfTVDayCalendar = DateUtils.setTimeZoneAndOffsetToLocal(endOfTVDayCalendar);
		
		return endOfTVDayCalendar;
	}
	

	
	public boolean isToday() 
	{
		boolean isToday = DateUtils.isTodayUsingTVDate(this);
		return isToday;
	}



	@Override
	public boolean areDataFieldsValid()
	{
		final int yearOf2000 = 2000;

		boolean areDataFieldsValid = (!TextUtils.isEmpty(getId()) && !TextUtils.isEmpty(getDisplayName()) && 
				(getStartOfTVDayCalendarGMT() != null) && getStartOfTVDayCalendarGMT().get(Calendar.YEAR) > yearOf2000 &&
				 getEndOfTVDayCalendarGMT() != null && getEndOfTVDayCalendarGMT().get(Calendar.YEAR) > yearOf2000
				);
		
		return areDataFieldsValid;
	}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TVDateJSON other = (TVDateJSON) obj;
		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}
}