
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
import com.mitv.utilities.DateUtils;



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
		
		return competitionCacheData;
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

	
	
	public synchronized void setAllCompetitions(List<Competition> competitions)
	{
		for(Competition competition : competitions)
		{
			CompetitionCacheData competitionCacheData = new CompetitionCacheData(competition);
			
			Long competitionID = new Long(competition.getCompetitionId());
			
			allCompetitions.put(competitionID, competitionCacheData);
		}
	}
	
	
	
	public synchronized Competition getSelectedCompetition() 
	{
		return selectedCompetition.getCompetition();
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
	
	
	
	public Map<Long, List<Event>> getEventsGroupedByFirstPhase() 
	{
		return selectedCompetition.getEventsGroupedByFirstPhase();
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
		
		Long nowInMillis = DateUtils.getNow().getTimeInMillis();
		
		selectedCompetition.setHighlightsByEventFetchTime(nowInMillis);
	}
	
	
	
	public synchronized void setLineUpForEventInSelectedCompetition(Long eventID, List<EventLineUp> eventLineup)
	{
		selectedCompetition.getLineupByEvent().put(eventID, eventLineup);
		
		Long nowInMillis = DateUtils.getNow().getTimeInMillis();
		
		selectedCompetition.setLineupByEventFetchTime(nowInMillis);
	}
}
