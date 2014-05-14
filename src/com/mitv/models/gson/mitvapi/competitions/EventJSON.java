
package com.mitv.models.gson.mitvapi.competitions;



import java.util.List;

import com.mitv.models.objects.mitvapi.competitions.EventBroadcastDetails;



public class EventJSON 
{
	protected long eventId;
	protected long competitionId;
	protected String eventDate;
	protected boolean rescheduled;
	protected long phaseId;
	protected long homeTeamId;
	protected String homeTeam;
	protected long awayTeamId;
	protected String awayTeam;
	protected int stadiumId;
	protected String stadium;
	protected int cityId;
	protected String city;
	protected String country;
	protected int homeGoals;
	protected int awayGoals;
	protected int homeGoalsHalfTime;
	protected int awayGoalsHalfTime;
	protected long matchStatusId;
	protected String matchStatus;
	protected boolean postponed;
	protected boolean finished;
	protected boolean abandoned;
	protected boolean awarded;
	protected boolean live;
	protected int refereeId;
	protected String referee;
	protected String refereeNation;
	protected boolean dataEntryLiveScore;
	protected boolean dataEntryLiveGoal;
	protected boolean dataEntryLiveLineUp;
	protected List<EventBroadcastDetails> broadcastDetails;
	
	protected boolean isOngoing;
	
	
	
	public EventJSON()
	{}



	public long getEventId() {
		return eventId;
	}



	public long getCompetitionId() {
		return competitionId;
	}



	public String getEventDate() {
		return eventDate;
	}



	public boolean isRescheduled() {
		return rescheduled;
	}



	public long getPhaseId() {
		return phaseId;
	}



	public long getHomeTeamId() {
		return homeTeamId;
	}



	public String getHomeTeam() {
		return homeTeam;
	}



	public long getAwayTeamId() {
		return awayTeamId;
	}



	public String getAwayTeam() {
		return awayTeam;
	}



	public int getStadiumId() {
		return stadiumId;
	}



	public String getStadium() {
		return stadium;
	}



	public int getCityId() {
		return cityId;
	}



	public String getCity() {
		return city;
	}



	public String getCountry() {
		return country;
	}



	public int getHomeGoals() {
		return homeGoals;
	}



	public int getAwayGoals() {
		return awayGoals;
	}



	public int getHomeGoalsHalfTime() {
		return homeGoalsHalfTime;
	}



	public int getAwayGoalsHalfTime() {
		return awayGoalsHalfTime;
	}



	public long getMatchStatusId() {
		return matchStatusId;
	}



	public String getMatchStatus() {
		return matchStatus;
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



	public String getReferee() {
		return referee;
	}



	public String getRefereeNation() {
		return refereeNation;
	}



	public boolean isDataEntryLiveScore() {
		return dataEntryLiveScore;
	}



	public boolean isDataEntryLiveGoal() {
		return dataEntryLiveGoal;
	}



	public boolean isDataEntryLiveLineUp() {
		return dataEntryLiveLineUp;
	}



	public List<EventBroadcastDetails> getBroadcastDetails() {
		return broadcastDetails;
	}



	public boolean isOngoing() {
		return isOngoing;
	}
}
