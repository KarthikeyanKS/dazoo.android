
package com.mitv.models;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.util.Log;

import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventHighlight;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;



public class CompetitionsCacheData 
{	
	private static final String TAG = ContentManager.class.getName();
	
	
	
	private Map<Long, CompetitionCacheData> allCompetitions;
	private CompetitionCacheData selectedCompetition;
	
	
	
	public CompetitionsCacheData()
	{
		this.allCompetitions = new TreeMap<Long, CompetitionCacheData>();
		
		this.selectedCompetition = null;
	}
	
	
	
	public synchronized List<Competition> getAllCompetitions()
	{
		List<Competition> competitions = new ArrayList<Competition>();
		
		for(CompetitionCacheData competitionCacheData : allCompetitions.values())
		{
			competitions.add(competitionCacheData.getCompetition());
		}
		
		return competitions;
	}
	
	
	
	private synchronized CompetitionCacheData getCompetitionCacheDataByID(long competitionID) 
	{
		CompetitionCacheData competitionCacheData = allCompetitions.get(competitionID);
		
		if(competitionCacheData == null)
		{
			Log.w(TAG, "CompetitionCacheData is null");
		}
		
		return competitionCacheData;
	}
	
	
	
	/**
	 * Finds a competition containing the team. 
	 * 
	 * WARNING: May be inaccurate if team is in many competitions.
	 * 
	 * @param team
	 * @return competition containing the team
	 */
	public synchronized Competition getCompetitionByTeam(Team team) {
		for (CompetitionCacheData competitionCacheData : allCompetitions.values()) {
			if (competitionCacheData.getTeams().contains(team)) {
				return competitionCacheData.getCompetition();
			}
		}
		return null;
	}
	
	
	
	public synchronized Competition getCompetitionByID(Long competitionID) 
	{
		Competition competitionFound = null;
		
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);

		if(competitionCacheData != null)
		{
			competitionFound = competitionCacheData.getCompetition();
		}

		return competitionFound;
	}
	
	
	
	public synchronized Event getEventByID(long competitionID, long eventID) 
	{
		Event eventFound = null;
		
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{	
			for(Event event : competitionCacheData.getEvents())
			{
				long eventCacheID = event.getEventId();
				
				if(eventCacheID == eventID)
				{
					eventFound = event;
					break;
				}
			}
		}
		
		return eventFound;
	}
	
	
	
	public synchronized Phase getPhaseByIDForSelectedCompetition(long phaseID) 
	{
		Phase phaseFound = null;
		
		List<Phase> phases = selectedCompetition.getPhases();
		
		for(Phase phase : phases)
		{
			long phaseCacheID = phase.getPhaseId();
			
			if(phaseCacheID == phaseID)
			{
				phaseFound = phase;
				break;
			}
		}
		
		return phaseFound;
	}
	
	
	
	public synchronized Team getTeamByIDForSelectedCompetition(Long teamID) 
	{
		Team teamFound = null;
		
		List<Team> teams = getTeamsForSelectedCompetition();
		
		for(Team team : teams)
		{
			if(team.getTeamId() == teamID)
			{
				teamFound = team;
				break;
			}
		}
		
		return teamFound;
	}
	
	
	
	public synchronized List<TeamSquad> getSquadByTeamIDForSelectedCompetition(Long teamID) 
	{
		List<TeamSquad> squad = null;
		
		Team team = getTeamByIDForSelectedCompetition(teamID);
		
		if(team != null)
		{
			squad = selectedCompetition.getSquadByTeam().get(teamID);
		}
		
		if(squad == null)
		{
			Log.w(TAG, "Squad is null");
		}
		
		return squad;
	}

	
	
	public synchronized void setAllCompetitions(List<Competition> competitions)
	{
		for(Competition competition : competitions)
		{
			CompetitionCacheData competitionCacheData = new CompetitionCacheData(competition);
			
			Long competitionID = new Long(competition.getCompetitionId());
			
			allCompetitions.put(competitionID, competitionCacheData);
		}
	}
	
	
	
	public void setEvent(Event event) 
	{
		selectedCompetition.setEvent(event);
	}
	
	
	
	public synchronized Competition getSelectedCompetition() 
	{
		Competition competition = selectedCompetition.getCompetition();
		
		if(competition == null)
		{
			Log.w(TAG, "Selected competition is null");
		}
		
		return competition;
	}
	
	

	public synchronized void setSelectedCompetition(long competitionID)
	{
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			selectedCompetition = competitionCacheData;
		}
	}
	

	
	public synchronized List<Event> getEventsForSelectedCompetition() 
	{
		if(selectedCompetition != null)
		{
			return selectedCompetition.getEvents();
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
			
			return Collections.emptyList();
		}
	}



	public synchronized void setEventsForSelectedCompetition(List<Event> events) 
	{
		if(selectedCompetition != null)
		{
			selectedCompetition.setEvents(events);
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
		}
	}



	public synchronized List<Team> getTeamsForSelectedCompetition() 
	{
		if(selectedCompetition != null)
		{
			return selectedCompetition.getTeams();
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
			
			return Collections.emptyList();
		}
	}



	public synchronized void setTeamsForSelectedCompetition(List<Team> teams) 
	{
		if(selectedCompetition != null)
		{
			selectedCompetition.setTeams(teams);
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
		}
	}
	
	
	
	
	public synchronized List<Phase> getPhasesForSelectedCompetition() 
	{
		if(selectedCompetition != null)
		{
			return selectedCompetition.getPhases();
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
			
			return Collections.emptyList();
		}
	}



	public synchronized void setPhasesForSelectedCompetition(List<Phase> phases) 
	{
		if(selectedCompetition != null)
		{
			selectedCompetition.setPhases(phases);
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
		}
	}
	
	
	
	public synchronized Map<Long, List<Standings>> getStandingsByPhaseForSelectedCompetition() 
	{
		if(selectedCompetition != null)
		{
			return selectedCompetition.getStandingsByPhase();
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
			
			return Collections.emptyMap();
		}
	}



	public synchronized void addStandingsForPhaseIDForSelectedCompetition(List<Standings> standings, Long phaseID) 
	{
		if(selectedCompetition != null)
		{
			selectedCompetition.getStandingsByPhase().put(phaseID, standings);
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
		}
	}
	
	
	
	public synchronized void addOrModifyTeamForSelectedCompetition(Team team) 
	{
		if(selectedCompetition != null)
		{
			boolean containsTeam = selectedCompetition.getTeams().contains(team);
			
			if(containsTeam == false)
			{
				selectedCompetition.getTeams().add(team);
			}
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
		}
	}
	
	
	
	public synchronized boolean containsCompetitionsData()
	{
		boolean containsCompetitionsData = (allCompetitions.isEmpty() == false);
		
		return containsCompetitionsData;
	}

	
	
	public synchronized boolean containsCompetitionData(Long competitionID) 
	{
		boolean containsCompetitionData = false;
	
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			containsCompetitionData = competitionCacheData.hasCompetitionInitialData();
		}
		
		return containsCompetitionData;
	}
	
	
	
	public synchronized boolean containsEventLineUpData(Long competitionID, Long eventID) 
	{
		boolean containsEventLineUpData = false;
	
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			containsEventLineUpData = competitionCacheData.hasLineUpData(eventID);
		}
		
		return containsEventLineUpData;
	}
	
	
	public synchronized boolean containsEventHighlightsData(Long competitionID, Long eventID) 
	{
		boolean containsEventHighlightsData = false;
	
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			containsEventHighlightsData = competitionCacheData.hasHighlightsData(eventID);
		}
		
		return containsEventHighlightsData;
	}
	
	
	
	public synchronized boolean containsStandingsData(Long competitionID, Long phaseID) 
	{
		boolean containsStandingsData = false;
	
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			containsStandingsData = competitionCacheData.hasStandingsData(phaseID);
		}
		
		return containsStandingsData;
	}
	
	
	
	public synchronized boolean containsEventData(Long competitionID, Long eventID) 
	{
		boolean containsEventData = false;
		
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			containsEventData = competitionCacheData.hasEventData();
		}
		
		return containsEventData;
	}
	
	
	
	public synchronized boolean containsCurentPhaseByTeam(Long competitionID, Long teamID)
	{
		boolean containsEventData = false;
		
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			containsEventData = competitionCacheData.hasCurrentPhaseByTeam(teamID);
		}
		
		return containsEventData;
	}
	
	
	
	public synchronized boolean containsTeamData(Long competitionID) 
	{
		boolean containsTeamData = false;
		
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			containsTeamData = competitionCacheData.hasTeamData();
		}
		
		return containsTeamData;
	}
	
	
	
	public synchronized boolean containsSquadData(
			Long competitionID,
			Long teamID)
	{
		boolean containsTeamData = false;
		
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			containsTeamData = competitionCacheData.hasSquadData(teamID);
		}
		
		return containsTeamData;
	}
	
	
	
	public Map<Long, List<Event>> getEventsGroupedByFirstPhase() 
	{
		return selectedCompetition.getEventsGroupedByFirstPhase();
	}
	
	
	
	public List<Event> getEventsForPhase(long phaseID) 
	{
		List<Event> events = selectedCompetition.getEventsGroupedByFirstPhase().get(phaseID);
		
		if(events == null)
		{
			events = selectedCompetition.getEventsGroupedBySecondPhase().get(phaseID);
		}
		
		return events;
	}



	public Map<Long, List<Event>> getEventsGroupedBySecondPhase() 
	{
		return selectedCompetition.getEventsGroupedBySecondPhase();
	}
	
	
	
	public synchronized void setSelectedCompetitionProcessedData()
	{
		selectedCompetition.setEventsGroupedByFirstStage();
		selectedCompetition.setEventsGroupedBySecondStage();
	}
	
	
	
	public synchronized List<EventHighlight> getEventHighlightsForEventInSelectedCompetition(Long eventID)
	{
		return selectedCompetition.getHighlightsByEvent().get(eventID);
	}
	
	
	
	public synchronized List<EventLineUp> getEventLineUpForEventInSelectedCompetition(Long eventID)
	{
		return selectedCompetition.getLineupByEvent().get(eventID);
	}
	
	
	
	public synchronized List<Standings> getEventStandingsForPhaseInSelectedCompetition(Long phaseID)
	{
		return selectedCompetition.getStandingsByPhase().get(phaseID);
	}
	
	
	
	public synchronized void setHighlightsForEventInSelectedCompetition(Long eventID, List<EventHighlight> eventHighligths)
	{
		selectedCompetition.getHighlightsByEvent().put(eventID, eventHighligths);
	}
	
	
	
	public synchronized void setLineUpForEventInSelectedCompetition(Long eventID, List<EventLineUp> eventLineup)
	{
		selectedCompetition.getLineupByEvent().put(eventID, eventLineup);
	}
	
	
	
	public synchronized void setSquadForTeamInSelectedCompetition(Long teamID, List<TeamSquad> squad)
	{
		selectedCompetition.getSquadByTeam().put(teamID, squad);
	}


	
	public synchronized Phase getCurrentPhaseForTeam(Long teamID)
	{
		return selectedCompetition.getCurrentPhaseByTeam().get(teamID);
	}
	
	
	
	public synchronized void addCurrentPhase(Phase phase, Long teamID)
	{
		selectedCompetition.getCurrentPhaseByTeam().put(teamID, phase);
	}
	
	
	
	public synchronized void clearCompetition(Long competitionID)
	{
		CompetitionCacheData competitionCacheData = this.getCompetitionCacheDataByID(competitionID);
		
		if(competitionCacheData != null)
		{
			competitionCacheData.clear();
		}
	}
}
