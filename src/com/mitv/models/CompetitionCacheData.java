
package com.mitv.models;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.mitv.Constants;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventHighlight;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;



public class CompetitionCacheData 
{
	private Competition competition;
	
	private List<Team> teams;
	private List<Phase> phases;
	private List<Event> events;
	
	private Map<Long, List<Standings>> standingsByPhase;
	
	private TreeMap<Long, List<Event>> eventsGroupedByFirstPhase;
	private TreeMap<Long, List<Event>> eventsGroupedBySecondPhase;
	
	private TreeMap<Long, List<EventHighlight>> highlightsByEvent;
	
	private TreeMap<Long, List<EventLineUp>> lineupByEvent;
	
	private TreeMap<Long, List<TeamSquad>> squadByTeam;
	
	private TreeMap<Long, Phase> currentPhaseByTeam;
	
	
	
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
		
		this.standingsByPhase = new TreeMap<Long, List<Standings>>();
		
		this.eventsGroupedByFirstPhase = new TreeMap<Long, List<Event>>();
		
		this.eventsGroupedBySecondPhase = new TreeMap<Long, List<Event>>();
		
		this.highlightsByEvent = new TreeMap<Long, List<EventHighlight>>();
		
		this.lineupByEvent = new TreeMap<Long, List<EventLineUp>>();
		
		this.squadByTeam = new TreeMap<Long, List<TeamSquad>>();
		
		this.currentPhaseByTeam = new TreeMap<Long, Phase>();
	}
	
	
	
	public CompetitionCacheData(Competition competition)
	{
		this();
		
		this.competition = competition;
	}
	
	
	
	public void clear()
	{
		teams.clear();
		phases.clear();
		events.clear();
		standingsByPhase.clear();
		eventsGroupedByFirstPhase.clear();
		eventsGroupedBySecondPhase.clear();
		highlightsByEvent.clear();
		lineupByEvent.clear();
		squadByTeam.clear();
		currentPhaseByTeam.clear();
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
				
				eventsGroupedBySecondPhase. put(phase.getPhaseId(), eventsForPhase);
			}
		}
	}
	
	
	
	public void setEvent(Event event) 
	{
		boolean containsEvent = events.contains(event);

		if(containsEvent)
		{
			int elementIndex = events.indexOf(event);
			
			events.set(elementIndex, event);
		}
	}
	
	
	
	public boolean hasCompetitionInitialData()
	{
		boolean hasCompetitionInitialData = (events.isEmpty() == false && teams.isEmpty() == false && standingsByPhase.isEmpty() == false && phases.isEmpty() == false);
		
		return hasCompetitionInitialData;
	}
	
	
	
	public boolean hasEventData()
	{
		boolean hasEventData = (events.isEmpty() == false);
		
		return hasEventData;
	}
	
	
	
	public boolean hasTeamData()
	{
		boolean hasTeamData = (teams.isEmpty() == false);
		
		return hasTeamData;
	}
	
	
	
	public boolean hasLineUpData(Long eventID)
	{
		boolean hasLineUpData = (lineupByEvent.isEmpty() == false && lineupByEvent.containsKey(eventID));
		
		return hasLineUpData;
	}

	
	
	public boolean hasHighlightsData(Long eventID)
	{
		boolean hasHighlightsData = (highlightsByEvent.isEmpty() == false && highlightsByEvent.containsKey(eventID));
		
		return hasHighlightsData;
	}
	
	
	
	public boolean hasStandingsData(Long phaseID)
	{
		boolean hasStandingsData = (standingsByPhase.isEmpty() == false && standingsByPhase.containsKey(phaseID));
		
		return hasStandingsData;
	}
	
	
	
	public boolean hasSquadData(Long teamID)
	{
		boolean hasStandingsData = (squadByTeam.isEmpty() == false && squadByTeam.containsKey(teamID));
		
		return hasStandingsData;
	}
	
	
	
	public boolean hasCurrentPhaseByTeam(Long teamID)
	{
		boolean hasStandingsData = (currentPhaseByTeam.isEmpty() == false && currentPhaseByTeam.containsKey(teamID));
		
		return hasStandingsData;
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



	public TreeMap<Long, List<EventHighlight>> getHighlightsByEvent() {
		return highlightsByEvent;
	}



	public void setHighlightsByEvent(
			TreeMap<Long, List<EventHighlight>> highlightsByEvent) {
		this.highlightsByEvent = highlightsByEvent;
	}



	public TreeMap<Long, List<EventLineUp>> getLineupByEvent() {
		return lineupByEvent;
	}



	public void setLineupByEvent(TreeMap<Long, List<EventLineUp>> lineupByEvent) {
		this.lineupByEvent = lineupByEvent;
	}

	
	
	public TreeMap<Long, List<TeamSquad>> getSquadByTeam() {
		return squadByTeam;
	}
	
	
	public void setSquadByTeam(TreeMap<Long, List<TeamSquad>> squadByTeam) {
		this.squadByTeam = squadByTeam;
	}



	public TreeMap<Long, Phase> getCurrentPhaseByTeam() {
		return currentPhaseByTeam;
	}



	public void setCurrentPhaseByTeam(TreeMap<Long, Phase> currentPhaseByTeam) {
		this.currentPhaseByTeam = currentPhaseByTeam;
	}	
}
