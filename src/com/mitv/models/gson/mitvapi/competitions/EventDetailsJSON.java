
package com.mitv.models.gson.mitvapi.competitions;



import java.util.List;



public class EventDetailsJSON 
{
	private String eventId;
	private List<EventHighlightJSON> highlights;
	private String pollId;
	private List<String> refereeIds;
	
	
	public EventDetailsJSON(){}


	
	public String getEventId() {
		return eventId;
	}


	public List<EventHighlightJSON> getHighlights() {
		return highlights;
	}


	public String getPollId() {
		return pollId;
	}


	public List<String> getRefereeIds() {
		return refereeIds;
	}
}
