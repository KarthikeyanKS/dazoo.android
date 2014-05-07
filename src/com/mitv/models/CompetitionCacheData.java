
package com.mitv.models;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;



public class CompetitionCacheData 
{
	private Competition competition;
	
	private List<Team> teams;
	private List<Phase> phases;
	private List<Event> events;
	private Map<Long, List<Standings>> standingsByPhase;
	
	
	
	public CompetitionCacheData()
	{
		this.competition = null;
		
		this.teams = new ArrayList<Team>();
		
		this.phases = new ArrayList<Phase>();
		
		this.events = new ArrayList<Event>();
		
		this.standingsByPhase = new HashMap<Long, List<Standings>>();
	}
	
	
	
	public CompetitionCacheData(Competition competition)
	{
		this.competition = competition;
		
		this.teams = new ArrayList<Team>();
		
		this.phases = new ArrayList<Phase>();
		
		this.events = new ArrayList<Event>();
		
		this.standingsByPhase = new HashMap<Long, List<Standings>>();
	}
	
	
	
	public boolean hasCompetitionInitialData()
	{
		boolean hasCompetitionInitialData = (events.isEmpty() == false && teams.isEmpty() == false && standingsByPhase.isEmpty() == false && phases.isEmpty() == false);
		
		return hasCompetitionInitialData;
	}



	public List<Phase> getPhases() {
		return phases;
	}



	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}



	public Competition getCompetition() {
		return competition;
	}



	public void setCompetition(Competition competition) {
		this.competition = competition;
	}



	public List<Event> getEvents() {
		return events;
	}



	public void setEvents(List<Event> events) {
		this.events = events;
	}



	public List<Team> getTeams() {
		return teams;
	}



	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}



	public Map<Long, List<Standings>> getStandingsByPhase() {
		return standingsByPhase;
	}



	public void setStandingsByPhase(Map<Long, List<Standings>> standingsByPhase) {
		this.standingsByPhase = standingsByPhase;
	}
}
