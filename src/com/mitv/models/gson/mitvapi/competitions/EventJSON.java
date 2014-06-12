
package com.mitv.models.gson.mitvapi.competitions;



import java.util.ArrayList;
import java.util.List;
import android.util.Log;



public class EventJSON 
{
	private static final String TAG = EventJSON.class.getName();
	
	
	private long eventId;
	private long competitionId;
	private String eventDate;
	private boolean rescheduled;
	private long phaseId;
	private long homeTeamId;
	private String homeTeam;
	private long awayTeamId;
	private String awayTeam;
	private int stadiumId;
	private String stadium;
	private String stadiumImageCopyright;
	private String description;
	private int cityId;
	private String city;
	private String country;
	private int homeGoals;
	private int awayGoals;
	private int homeGoalsHalfTime;
	private int awayGoalsHalfTime;
	private long matchStatusId;
	private boolean postponed;
	private boolean finished;
	private boolean abandoned;
	private boolean awarded;
	private boolean live;
	private int refereeId;
	private String referee;
	private String refereeNation;
	private boolean dataEntryLiveScore;
	private boolean dataEntryLiveGoal;
	private boolean dataEntryLiveLineUp;
	private List<EventBroadcastJSON> broadcasts;
	private int currentMinute;
	
	
	
	public EventJSON()
	{}



	public long getEventId()
	{
		return eventId;
	}



	public long getCompetitionId() 
	{
		return competitionId;
	}



	public String getEventDate() 
	{
		if(eventDate == null)
		{
			eventDate = "";
			
			Log.w(TAG, "eventDate is null");
		}
		
		return eventDate;
	}



	public boolean isRescheduled() 
	{
		return rescheduled;
	}



	public long getPhaseId() 
	{
		return phaseId;
	}



	public long getHomeTeamId() 
	{
		return homeTeamId;
	}



	public String getHomeTeam() 
	{
		if(homeTeam == null)
		{
			homeTeam = "";
			
			Log.w(TAG, "homeTeam is null");
		}
		
		return homeTeam;
	}



	public long getAwayTeamId() 
	{
		return awayTeamId;
	}



	public String getAwayTeam() 
	{
		if(awayTeam == null)
		{
			awayTeam = "";
			
			Log.w(TAG, "awayTeam is null");
		}
		
		return awayTeam;
	}



	public int getStadiumId()
	{
		return stadiumId;
	}



	public String getStadium() 
	{
		if(stadium == null)
		{
			stadium = "";
		}
		
		return stadium;
	}



	public int getCityId() 
	{
		return cityId;
	}



	public String getCity() 
	{
		if(city == null)
		{
			city = "";
			
			Log.w(TAG, "city is null");
		}
		
		return city;
	}



	public String getCountry() 
	{
		if(country == null)
		{
			country = "";
			
			Log.w(TAG, "country is null");
		}
		
		return country;
	}



	public int getHomeGoals()
	{
		return homeGoals;
	}



	public int getAwayGoals()
	{
		return awayGoals;
	}



	public int getHomeGoalsHalfTime()
	{
		return homeGoalsHalfTime;
	}



	public int getAwayGoalsHalfTime() {
		return awayGoalsHalfTime;
	}



	public long getMatchStatusId() {
		return matchStatusId;
	}

	

	public boolean isPostponed() {
		return postponed;
	}



	public boolean isFinished() {
		return finished;
	}



	public boolean isAbandoned() {
		return abandoned;
	}



	public boolean isAwarded() {
		return awarded;
	}



	public boolean isLive() {
		return live;
	}



	public int getRefereeId() {
		return refereeId;
	}



	public String getReferee() 
	{
		if(referee == null)
		{
			referee = "";
			
			Log.w(TAG, "referee is null");
		}
		
		return referee;
	}



	public String getRefereeNation() 
	{
		if(refereeNation == null)
		{
			refereeNation = "";
			
			Log.w(TAG, "refereeNation is null");
		}
		
		return refereeNation;
	}



	public boolean isDataEntryLiveScore() 
	{
		return dataEntryLiveScore;
	}



	public boolean isDataEntryLiveGoal() 
	{
		return dataEntryLiveGoal;
	}



	public boolean isDataEntryLiveLineUp() 
	{
		return dataEntryLiveLineUp;
	}



	public String getStadiumImageCopyright() 
	{
		if(stadiumImageCopyright == null)
		{
			stadiumImageCopyright = "";
			
			Log.w(TAG, "stadiumImageCopyright is null");
		}
		
		return stadiumImageCopyright;
	}



	public String getDescription() 
	{
		if(description == null)
		{
			description = "";
			
			Log.w(TAG, "description is null");
		}
		
		return description;
	}


	
	public List<EventBroadcastJSON> getBroadcasts() 
	{
		if(broadcasts == null)
		{
			broadcasts = new ArrayList<EventBroadcastJSON>();
			
			Log.w(TAG, "broadcasts is null");
		}
		
		return broadcasts;
	}



	public int getCurrentMinute() 
	{
		return currentMinute;
	}
}
