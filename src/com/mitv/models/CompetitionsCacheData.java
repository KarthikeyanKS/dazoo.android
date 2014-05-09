
package com.mitv.models;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.mitv.managers.ContentManager;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;



public class CompetitionsCacheData 
{	
	private static final String TAG = ContentManager.class.getName();
	
	
	
	private List<CompetitionCacheData> allCompetitions;
	private CompetitionCacheData selectedCompetition;
	
	
	
	public CompetitionsCacheData()
	{
		this.allCompetitions = new ArrayList<CompetitionCacheData>();
		
		this.selectedCompetition = null;
	}
	
	
	
	public synchronized List<Competition> getAllCompetitions()
	{
		List<Competition> competitions = new ArrayList<Competition>();
		
		for(CompetitionCacheData competitionCacheData : allCompetitions)
		{
			competitions.add(competitionCacheData.getCompetition());
		}
		
		return competitions;
	}
	
	
	
	public synchronized Competition getCompetitionByID(long competitionID) 
	{
		Competition competitionFound = null;
		
		for(CompetitionCacheData competitionCacheData : allCompetitions)
		{
			long competitionCacheID = competitionCacheData.getCompetition().getCompetitionId();
			
			if(competitionCacheID == competitionID)
			{
				competitionFound = competitionCacheData.getCompetition();
				break;
			}
		}
		
		return competitionFound;
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
			
			allCompetitions.add(competitionCacheData);
		}
	}
	
	
	
	public synchronized Competition getSelectedCompetition() 
	{
		return selectedCompetition.getCompetition();
	}
	
	

	public synchronized void setSelectedCompetition(long competitionID)
	{
		for(CompetitionCacheData competitionCacheData : allCompetitions)
		{
			long competitionCacheID = competitionCacheData.getCompetition().getCompetitionId();
			
			if(competitionCacheID == competitionID)
			{
				selectedCompetition = competitionCacheData;
				break;
			}
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



	public synchronized void addStandingsForPhaseIDForSelectedCompetition(List<Standings> standings, long phaseID) 
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

	
	
	public synchronized boolean containsCompetitionData(long competitionID) 
	{
		boolean containsCompetitionData = false;
		
		for(CompetitionCacheData competitionCacheData : allCompetitions)
		{
			boolean matchesCompetitionID = (competitionCacheData.getCompetition().getCompetitionId() == competitionID);
			
			if(matchesCompetitionID)
			{
				containsCompetitionData = competitionCacheData.hasCompetitionInitialData();
				break;
			}
		}
		
		return containsCompetitionData;
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
}
