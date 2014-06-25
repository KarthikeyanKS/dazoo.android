
package com.mitv.models.gson.mitvapi.base;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.util.Log;
import com.mitv.utilities.DateUtils;



public class BaseObjectListJSON<T>
	extends ArrayList<T>
{	
	private static final long serialVersionUID = -4982657952866083477L;
	private static final String TAG = BaseObjectListJSON.class.getName();
	
	public static final int TIME_OFFSET_IN_MINUTES_FOR_NTP_COMPARISSON = 5;
		
	
	
	private Integer timeToLiveInSeconds;
	private Calendar serverDate;
	
	
	
	public BaseObjectListJSON(List<T> elementList)
	{
		super(elementList);
	}
	
	
	
	public void setTimeToLiveInMilliseconds(Integer timeToLiveInSeconds)
	{
		this.timeToLiveInSeconds = timeToLiveInSeconds;
	}
	
	
	
	public void setServerDate(Calendar serverDate)
	{
	    this.serverDate = serverDate;
	}
	
	
	
	public boolean isExpired()
	{
		boolean isExpired = true;
	
		if(timeToLiveInSeconds != null && serverDate != null)
		{
			Calendar serverDateWithIncrement = (Calendar) serverDate.clone();
			
			serverDateWithIncrement.add(Calendar.SECOND, timeToLiveInSeconds);
			
			Calendar now = DateUtils.getNowWithGMTTimeZone();
			
			isExpired = serverDateWithIncrement.after(now);
		}
		else
		{
			Log.w(TAG, "Either timeToLiveInSeconds or serverDate are null. isExpired defaulting to true");
		}
		
		return isExpired;
	}
	
	
	
	public boolean isDateOffSync()
	{
		boolean isDateOffSync = false;
		
		if(serverDate != null)
		{
			Calendar now = DateUtils.getNowWithGMTTimeZone();
			
			Integer difference = DateUtils.calculateDifferenceBetween(now, serverDate, Calendar.MINUTE, true, 0);
			
			if(difference > TIME_OFFSET_IN_MINUTES_FOR_NTP_COMPARISSON)
			{
				isDateOffSync = true;
			}
		}
		else
		{
			Log.w(TAG, "Server date is null. Assuming local device time as accurate.");
		}
	
		return isDateOffSync;
	}
}
