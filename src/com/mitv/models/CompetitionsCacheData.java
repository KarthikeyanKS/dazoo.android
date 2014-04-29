
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
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.models.objects.mitvapi.competitions.TeamDetails;



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
	
	
	
	public List<Competition> getAllCompetitions()
	{
		List<Competition> competitions = new ArrayList<Competition>();
		
		for(CompetitionCacheData competitionCacheData : allCompetitions)
		{
			competitions.add(competitionCacheData.getCompetition());
		}
		
		return competitions;
	}
	
	
	
	public Competition getSelectedCompetition() 
	{
		return selectedCompetition.getCompetition();
	}



	public void setSelectedCompetition(String competitionID)
	{
		for(CompetitionCacheData competitionCacheData : allCompetitions)
		{
			String competitionCacheID = competitionCacheData.getCompetition().get_id();
			
			if(competitionCacheID.equals(competitionID))
			{
				selectedCompetition = competitionCacheData;
				break;
			}
		}
	}
	
	
	
	public List<Event> getEventsForSelectedCompetition() 
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



	public void setEventsForSelectedCompetition(List<Event> events) 
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



	public List<Team> getTeamsForSelectedCompetition() 
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



	public void setTeamsForSelectedCompetition(List<Team> teams) 
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
	
	
	
	
	public List<Phase> getPhasesForSelectedCompetition() 
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



	public void setPhasesForSelectedCompetition(List<Phase> phases) 
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



	public TeamDetails getTeamDetailsForTeamInSelectedCompetition(String teamID) 
	{
		if(selectedCompetition != null)
		{
			Map<String, TeamDetails> teamDetailsForAllTeams = selectedCompetition.getTeamDetailsForTeam();
			
			TeamDetails teamDetails = teamDetailsForAllTeams.get(teamID);
			
			return teamDetails;
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
			
			return null;
		}
	}



	public void setTeamDetailsForTeam(String teamID, TeamDetails teamDetails) 
	{
		if(selectedCompetition != null)
		{
			Map<String, TeamDetails> teamDetailsForAllTeams = selectedCompetition.getTeamDetailsForTeam();
			
			teamDetailsForAllTeams.put(teamID, teamDetails);
		}
		else
		{
			Log.w(TAG, "Selected competition is null");
		}
	}
}
