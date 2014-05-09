
package com.mitv.models;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitv.Constants;
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
	
	private Map<Long, List<Event>> eventsGroupedByFirstPhase;
	private Map<Long, List<Event>> eventsGroupedBySecondPhase;
	
	
	
	public Map<Long, List<Event>> getEventsForFirstStages()
	{
		return eventsGroupedByFirstPhase;
	}
	
	
	
	public Map<Long, List<Event>> getEventsForSecondStages()
	{
		return eventsGroupedBySecondPhase;
	}
	
	
	
	public CompetitionCacheData()
	{
		this.competition = null;
		
		this.teams = new ArrayList<Team>();
		
		this.phases = new ArrayList<Phase>();
		
		this.events = new ArrayList<Event>();
		
		this.standingsByPhase = new HashMap<Long, List<Standings>>();
		
		this.eventsGroupedByFirstPhase = new HashMap<Long, List<Event>>();
		
		this.eventsGroupedBySecondPhase = new HashMap<Long, List<Event>>();
	}
	
	
	
	public CompetitionCacheData(Competition competition)
	{
		this.competition = competition;
		
		this.teams = new ArrayList<Team>();
		
		this.phases = new ArrayList<Phase>();
		
		this.events = new ArrayList<Event>();
		
		this.standingsByPhase = new HashMap<Long, List<Standings>>();
		
		this.eventsGroupedByFirstPhase = new HashMap<Long, List<Event>>();
		
		this.eventsGroupedBySecondPhase = new HashMap<Long, List<Event>>();
	}
	
	
	
	public void setEventsGroupedByFirstStage()
	{
		for(Phase phase : phases)
		{
			boolean isGroupStage = phase.getStage().equals(Constants.GROUP_STAGE);
	
			if(isGroupStage)
			{
				List<Event> eventsForPhase = new ArrayList<Event>();
				
				for(Event event : events)
				{
					boolean matchesPhaseId = (event.getPhaseId() == phase.getPhaseId());
					
					if(matchesPhaseId)
					{
						eventsForPhase.add(event);
					}
				}
				
				eventsGroupedByFirstPhase.put(phase.getPhaseId(), eventsForPhase);
			}
		}
	}
	
	
	public void setEventsGroupedBySecondStage()
	{
		for(Phase phase : phases)
		{
			boolean isGroupStage = (phase.getStage().equals(Constants.GROUP_STAGE) == false);
	
			if(isGroupStage)
			{
				List<Event> eventsForPhase = new ArrayList<Event>();
				
				for(Event event : events)
				{
					boolean matchesPhaseId = (event.getPhaseId() == phase.getPhaseId());
					
					if(matchesPhaseId)
					{
						eventsForPhase.add(event);
					}
				}
				
				eventsGroupedBySecondPhase.put(phase.getPhaseId(), eventsForPhase);
			}
		}
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



	public Map<Long, List<Event>> getEventsGroupedByFirstPhase() {
		return eventsGroupedByFirstPhase;
	}



	public Map<Long, List<Event>> getEventsGroupedBySecondPhase() {
		return eventsGroupedBySecondPhase;
	}
}
