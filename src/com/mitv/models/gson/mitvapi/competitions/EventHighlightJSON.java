
package com.mitv.models.gson.mitvapi.competitions;




public class EventHighlightJSON
{
	protected long eventId;
    
    private String highlightCode;
    
    private long actionTime;
    private long teamId;
    private String actionMinute;
    private String personShort;
    private String subPersonShort;

    
    
    public EventHighlightJSON() {}

     
    
    public long getActionTime() {
    	return actionTime;
    }

    
    
    public long getTeamId() {
    	return teamId;
    }

    
    public String getHighlightCode() {
    	if (highlightCode == null) {
    		highlightCode = "";
    	}
    	return highlightCode;
    }
    
    
    public String getActionMinute() {
    	if (actionMinute == null) {
    		actionMinute = "";
    	}
    	return actionMinute;
    }

    public String getPersonShort() {
    	if (personShort == null) {
    		personShort = "";
    	}
    	return personShort;
    }

    public long getEventId() {
    	return eventId;
    }

    public String getSubPersonShort() {
    	if (subPersonShort == null) {
    		subPersonShort = "";
    	}
    	return subPersonShort;
    }
}
