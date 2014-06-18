
package com.mitv.models.gson.mitvapi.competitions;

import android.util.Log;




public class EventHighlightJSON
{
	private static final String TAG = EventHighlightJSON.class.getName();
	
	
	private long eventId;
    private String highlightCode;
    private long actionTime;
    private String periodShort;
    private int periodSort;
    private long teamId;
    private String actionMinute;
    private String personShort;
    private String subPersonShort;
    private String actionInfo;
    private Integer homeGoals;
    private Integer awayGoals;

    
    
    public EventHighlightJSON() {}

     
    
    public long getActionTime() 
    {
    	return actionTime;
    }

    
    
    public long getTeamId() 
    {
    	return teamId;
    }

    
    
    protected String getHighlightCode() 
    {
    	if (highlightCode == null) 
    	{
    		highlightCode = "";
    	
    		Log.w(TAG, "highlightCode is null");
    	}
    	
    	return highlightCode;
    }
    
    
    
    public String getActionMinute()
    {
    	if (actionMinute == null)
    	{
    		actionMinute = "";
    	
    		Log.w(TAG, "actionMinute is null");
    	}
    	
    	return actionMinute;
    }

    
    
    public String getPersonShort() 
    {
    	if (personShort == null) 
    	{
    		personShort = "";
    	
    		Log.w(TAG, "personShort is null");
    	}
    	
    	return personShort;
    }

    
    
    public long getEventId() 
    {
    	return eventId;
    }

    
    
    public String getSubPersonShort() 
    {
    	if (subPersonShort == null) 
    	{
    		subPersonShort = "";
    	
    		Log.w(TAG, "subPersonShort is null");
    	}
    	
    	return subPersonShort;
    }



	protected String getPeriodShort() 
	{
		if (periodShort == null)
		{
			periodShort = "";
			
			Log.w(TAG, "periodShort is null");
    	}
		
		return periodShort;
	}



	protected int getPeriodSort()
	{
		return periodSort;
	}



	protected String getActionInfo() 
	{
		if (actionInfo == null)
		{
			actionInfo = "";
			
			Log.w(TAG, "actionInfo is null");
    	}
		
		return actionInfo;
	}



	protected Integer getHomeGoals() 
	{
		if (homeGoals == null)
		{
			homeGoals = Integer.valueOf(0);
			
			Log.w(TAG, "homeGoals is null");
    	}
		
		return homeGoals;
	}



	protected Integer getAwayGoals() 
	{
		if (awayGoals == null)
		{
			awayGoals = Integer.valueOf(0);
			
			Log.w(TAG, "awayGoals is null");
    	}
		
		return awayGoals;
	}
}
