
package com.mitv.managers;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.EventHighlightActionEnum;
import com.mitv.enums.EventLineUpPosition;
import com.mitv.enums.UserTutorialStatusEnum;
import com.mitv.models.Cache;
import com.mitv.models.objects.UserTutorialStatus;
import com.mitv.models.objects.mitvapi.AppConfiguration;
import com.mitv.models.objects.mitvapi.Notification;
import com.mitv.models.objects.mitvapi.RepeatingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.SearchResultsForQuery;
import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVFeedItem;
import com.mitv.models.objects.mitvapi.TVGuide;
import com.mitv.models.objects.mitvapi.TVTag;
import com.mitv.models.objects.mitvapi.UpcomingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventHighlight;
import com.mitv.models.objects.mitvapi.competitions.EventLineUp;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Standings;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.models.objects.mitvapi.competitions.TeamSquad;
import com.mitv.utilities.DateUtils;



public class CacheManager 
{
	private static final String TAG = CacheManager.class.getName();
	
	
	private Cache cache;
	
	
	
	public CacheManager()
	{	
		this.cache = new Cache();
	}
	
	
	
	protected Cache getCache()
	{
		if(cache == null)
		{
			Log.w(TAG, "Cache Object is null => Reinitializing instance");
			
			cache = new Cache();
		}
		
		return cache;
	}
	
	
	
	public void clearAllPersistentCacheData()
	{
		Cache.clearAllPersistentCacheData();
	}
	
	
	
	public void clearGuideCacheData()
	{
		cache.clearGuideCacheData();
	}
	
	
	
	public List<TVChannelId> getTVChannelIdsUsed() 
	{
		List<TVChannelId> tvChannelIdsUser = getCache().getTvChannelIdsUsed();
		
		return tvChannelIdsUser;
	}
	
	
	
	public boolean containsInitialData()
	{
		boolean containsAppConfigData = getCache().containsAppConfigData();
		boolean containsAppVersionData = getCache().containsAppVersionData();
		boolean containsTVDates = getCache().containsTVDates();
		boolean containsTVTags = getCache().containsTVTags();
		boolean containsTVChannels = getCache().containsTVChannels();
		boolean containsTVGuideForSelectedDay = getCache().containsTVGuideForSelectedDay();
		boolean containsCompetitions = getCache().getCompetitionsData().containsCompetitionsData();
		
		Log.d(TAG, "Contains AppConfigData " + containsAppConfigData);
		Log.d(TAG, "Contains AppVersionData " + containsAppVersionData);
		Log.d(TAG, "Contains TVDates " + containsTVDates);
		Log.d(TAG, "Contains TVTags " + containsTVTags);
		Log.d(TAG, "Contains TVChannels " + containsTVChannels);
		Log.d(TAG, "Contains TVGuideForSelectedDay " + containsTVGuideForSelectedDay);
		Log.d(TAG, "Contains Competitions " + containsCompetitions);
		
		boolean hasInitialData = containsAppConfigData && 
								 containsAppVersionData &&
								 containsTVDates && 
								 containsTVTags &&
								 containsTVChannels &&
								 containsTVGuideForSelectedDay &&
								 containsCompetitions;
		
		return hasInitialData;
	}
	
	
	
	public boolean containsTVDates()
	{
		return getCache().containsTVDates();
	}
	
	
	
	public boolean containsUserLikes()
	{
		return getCache().containsUserLikes();
	}
	
	
	
	public boolean containsActivityFeed()
	{
		return getCache().containsActivityFeedData();
	}
	
	
	
	public boolean containsTVTags()
	{
		return getCache().containsTVTags();
	}
	
	
	
	public boolean containsTVTagsAndGuideForSelectedTVDate()
	{
		TVDate tvDate = getCache().getTvDateSelected();
		
		if(tvDate != null)
		{
			boolean hasContent = getCache().containsTVTags() && getCache().containsTVGuideForTVDate(tvDate);
			
			return hasContent;
		}
		else
		{
			return false;
		}
	}
	
	
	
	public boolean containsUserTVChannelIds()
	{
		return getCache().containsTVChannelIdsUser();
	}
	
	
	
	public boolean containsTVChannelsAll()
	{
		return getCache().containsTVChannels();
	}
	
	
	
	public boolean containsTVBroadcastWithChannelInfo(
			final TVChannelId channelId, 
			final long beginTimeInMillis)
	{
		return getCache().containsTVBroadcastWithChannelInfo(channelId, beginTimeInMillis);
	}
	
	
	
	public boolean containsBroadcastPageData()
	{
		boolean hasBroadcastPageData = false;
		
		TVBroadcastWithChannelInfo broadcastWithChannelInfo = getCache().getNonPersistentLastSelectedBroadcastWithChannelInfo();

		if(broadcastWithChannelInfo != null)
		{
			boolean containsUpcomingBroadcasts = getCache().containsUpcomingBroadcastsForBroadcast(broadcastWithChannelInfo.getProgram().getProgramId());
		
			boolean containsRepeatingBroadcasts = getCache().containsRepeatingBroadcastsForBroadcast(broadcastWithChannelInfo.getProgram().getProgramId());
		
			hasBroadcastPageData = containsUpcomingBroadcasts && containsRepeatingBroadcasts;
		}
		
		return hasBroadcastPageData;
	}
	
	
	
	public boolean containsTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId)
	{
		return getCache().containsTVChannelGuideUsingTVChannelIdForSelectedDay(tvChannelId);
	}
	
	
	
	public boolean containsPopularBroadcasts()
	{
		return getCache().containsPopularBroadcasts();
	}
	
	
	
	public boolean containsCompetitionsData()
	{
		return getCache().getCompetitionsData().containsCompetitionsData();
	}
	
	
	
	public boolean containsCompetitionData(Long competitionID)
	{
		return getCache().getCompetitionsData().containsCompetitionData(competitionID);
	}
	
	
	
	public boolean containsEventData(Long competitionID, Long eventID)
	{
		return getCache().getCompetitionsData().containsEventData(competitionID, eventID);
	}
	
	
	
	public boolean containsTeamData(Long competitionID)
	{
		return getCache().getCompetitionsData().containsTeamData(competitionID);
	}
	
	
	
	public boolean containsTeamsGroupedByPhaseForSelectedCompetition()
	{
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long competitionID = selectedCompetition.getCompetitionId();
		
		return containsCompetitionData(competitionID);
	}

	
	
	public boolean containsEventsGroupedByPhaseForSelectedCompetition()
	{
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long competitionID = selectedCompetition.getCompetitionId();
		
		return containsCompetitionData(competitionID);
	}
	
	
	
	public boolean containsLineUpDataByEventIDForSelectedCompetition(Long eventID)
	{
		boolean hasData = false;
		
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long competitionID = selectedCompetition.getCompetitionId();
		
		hasData = getCache().getCompetitionsData().containsEventLineUpData(competitionID, eventID);

		return hasData;
	}
	
	
	
	public boolean containsHighlightsDataByEventIDForSelectedCompetition(Long eventID)
	{
		boolean hasData = false;
		
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long competitionID = selectedCompetition.getCompetitionId();
		
		hasData = getCache().getCompetitionsData().containsEventHighlightsData(competitionID, eventID);

		return hasData;
	}
	
	
	
	public boolean containsStandingsForPhaseInSelectedCompetition(Long phaseID)
	{
		boolean hasData = false;
		
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long competitionID = selectedCompetition.getCompetitionId();
		
		hasData = getCache().getCompetitionsData().containsStandingsData(competitionID, phaseID);
		
		return hasData;
	}
	
	
	
	public boolean containsSquadForTeamID(Long teamID)
	{
		boolean hasData = false;
		
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long competitionID = selectedCompetition.getCompetitionId();
		
		hasData = getCache().getCompetitionsData().containsSquadData(competitionID, teamID);
		
		return hasData;
	}
	
	

	public TVDate getTVDateSelected() 
	{
		TVDate tvDateSelected = getCache().getTvDateSelected();
		return tvDateSelected;
	}
	
	
	
	public int getTVDateSelectedIndex() 
	{
		int tvDateSelectedIndex = getCache().getTvDateSelectedIndex();
		return tvDateSelectedIndex;
	}
	
	
	
	public int getFirstHourOfTVDay() 
	{
		int firstHourOfDay = getCache().getFirstHourOfTVDay();
		
		return firstHourOfDay;
	}
	
	
	
	
	/* TVTags */
	public List<TVTag> getTVTags() 
	{
		List<TVTag> tvTags = getCache().getTvTags();
		
		return tvTags;
	}
	
	
	
	/* TVChannelGuide */
	public TVGuide getTVGuideForSelectedDay() 
	{
		TVDate tvDate = getTVDateSelected();
		
		TVGuide tvGuide = getCache().getTVGuideUsingTVDate(tvDate);
		
		return tvGuide;
	}
	
	
	
	public SearchResultsForQuery getSearchResults() 
	{
		SearchResultsForQuery searchResultForQuery = getCache().getNonPersistentSearchResultsForQuery();
		
		return searchResultForQuery;
	}
	
	
	public boolean containsAppConfiguration()
	{
		return (getAppConfiguration() != null);
	}
	
	
	
	public AppConfiguration getAppConfiguration()
	{
		AppConfiguration appConfiguration = getCache().getAppConfigData();
		
		return appConfiguration;
	}
	
	
	
	public TVChannelGuide getTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) 
	{
		TVChannelGuide tvChannelGuide = getCache().getTVChannelGuideUsingTVChannelIdForSelectedDay(tvChannelId);
		
		return tvChannelGuide;
	}

	
	
	public ArrayList<TVFeedItem> getActivityFeedData() 
	{
		ArrayList<TVFeedItem> activityFeedData = getCache().getActivityFeed();
		
		return activityFeedData;
	}
		
	
	
	/* UserToken related methods */
	
	/**
	 * This method should only be used by the AsyncTaskWithUserToken class. A
	 * part from that class no other class should ever access or modify the user
	 * token directly. All login/logout/sign up logic should be handled by this
	 * class.
	 * 
	 * @return
	 */
	public String getUserToken() 
	{
		String userToken = getCache().getUserToken();
		
		return userToken;
	}
	
	
	
	public boolean isLoggedIn() 
	{
		boolean isLoggedIn = getCache().isLoggedIn();
		
		return isLoggedIn;
	}
	
	
	
	/* NON-PERSISTENT USER DATA, TEMPORARY SAVED IN STORAGE, IN ORDER TO PASS DATA BETWEEN ACTIVITES */
	
	public void setUpcomingBroadcasts(
			final TVBroadcastWithChannelInfo broadcast, 
			final ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts) 
	{
		if(broadcast != null)
		{
			String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
		
			UpcomingBroadcastsForBroadcast upcomingBroadcastsObject = new UpcomingBroadcastsForBroadcast(tvSeriesId, upcomingBroadcasts);
		
			getCache().setNonPersistentUpcomingBroadcasts(upcomingBroadcastsObject);
		}
		else
		{
			Log.w(TAG, "Broadcast is null");
		}
	}
	
	
	
	public void setRepeatingBroadcasts(
			final TVBroadcastWithChannelInfo broadcast, 
			final ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts) 
	{
		if(broadcast != null)
		{
			String programId = broadcast.getProgram().getProgramId();
			
			RepeatingBroadcastsForBroadcast repeatingBroadcastsObject = new RepeatingBroadcastsForBroadcast(programId, repeatingBroadcasts);
			
			getCache().setNonPersistentRepeatingBroadcasts(repeatingBroadcastsObject);
		}
		else
		{
			Log.w(TAG, "Broadcast is null");
		}
	}
	
	
	/**
	 * This method only returns the repeating broadcasts if the ones in the storage have a tvSeriesId
	 * matching the one provided as argument
	 * @param programId
	 * @return
	 */
	public ArrayList<TVBroadcastWithChannelInfo> getUpcomingBroadcastsVerifyCorrect(TVBroadcast broadcast) 
	{
		if (broadcast.getProgram().getSeries() == null) 
		{
			return null;	
		} 
		else 
		{
			String tvSeriesId = broadcast.getProgram().getSeries().getSeriesId();
			
			ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
			
			if(getCache().containsUpcomingBroadcastsForBroadcast(tvSeriesId)) 
			{
				UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = getCache().getNonPersistentUpcomingBroadcasts();
				
				if(upcomingBroadcastsForBroadcast != null)
				{
					upcomingBroadcasts= upcomingBroadcastsForBroadcast.getRelatedBroadcasts();
				}
			}
			
			return upcomingBroadcasts;
		}
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getBroadcastsAiringOnDifferentChannels(
			final TVBroadcastWithChannelInfo broadcastWithChannelInfo,
			final boolean randomizeListElements)
	{
		ArrayList<TVBroadcastWithChannelInfo> broadcastsPlayingNow = new ArrayList<TVBroadcastWithChannelInfo>();
		
		TVChannelId inputTvChannelId = broadcastWithChannelInfo.getChannel().getChannelId();
		
		List<TVChannelId> tvChannelIds = getCache().getTvChannelIdsUsed();
		
		if(tvChannelIds != null && 
		   tvChannelIds.isEmpty() == false)
		{
			for(TVChannelId tvChannelId: tvChannelIds)
			{
				TVChannel tvChannel = getCache().getTVChannelById(tvChannelId);
				
				if(tvChannelId.equals(inputTvChannelId) == false && tvChannel != null)
				{
					TVChannelGuide tvChannelGuide = getCache().getTVChannelGuideUsingTVChannelIdForSelectedDay(tvChannelId);
			
					List<TVBroadcast> tvBroadcastsPlayingOnChannel = tvChannelGuide.getBroadcastPlayingAtSimilarTimeAs(broadcastWithChannelInfo);
					
					if(tvBroadcastsPlayingOnChannel.isEmpty() == false)
					{
						/* Only use the first of each channel */
						TVBroadcast tvBroadcastPlayingOnChannel = tvBroadcastsPlayingOnChannel.get(0);

						TVBroadcastWithChannelInfo tvBroadcastWithChannelInfoPlayingOnChannel = new TVBroadcastWithChannelInfo(tvBroadcastPlayingOnChannel);
						tvBroadcastWithChannelInfoPlayingOnChannel.setChannel(tvChannel);
						
						broadcastsPlayingNow.add(tvBroadcastWithChannelInfoPlayingOnChannel);
					}
				}
			}
		}
		
		if(randomizeListElements)
		{
			Collections.shuffle(broadcastsPlayingNow);
		}
		
		return broadcastsPlayingNow;
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getUpcomingBroadcasts() 
	{
		ArrayList<TVBroadcastWithChannelInfo> upcomingBroadcasts = null;
		
		UpcomingBroadcastsForBroadcast upcomingBroadcastsForBroadcast = getCache().getNonPersistentUpcomingBroadcasts();
		
		if(upcomingBroadcastsForBroadcast != null) 
		{
			upcomingBroadcasts= upcomingBroadcastsForBroadcast.getRelatedBroadcasts();
		}
		
		return upcomingBroadcasts;
	}
	
	
	
	/**
	 * This method only returns the repeating broadcasts if the ones in the storage have a programId
	 * matching the one provided as argument
	 * @param programId
	 * @return
	 */
	public ArrayList<TVBroadcastWithChannelInfo> getRepeatingBroadcastsVerifyCorrect(final TVBroadcast broadcast) 
	{
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
	
		if(broadcast != null)
		{
			String programId = broadcast.getProgram().getProgramId();
			
			
			
			if(getCache().containsRepeatingBroadcastsForBroadcast(programId)) 
			{
				RepeatingBroadcastsForBroadcast repeatingBroadcastObject = getCache().getNonPersistentRepeatingBroadcasts();
				
				if(repeatingBroadcastObject != null) 
				{
					repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcastsWithExclusions(broadcast);
				}
			}
		}
		else
		{
			Log.w(TAG, "Broadcast is null");
		}
		
		return repeatingBroadcasts;
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getRepeatingBroadcasts() 
	{
		ArrayList<TVBroadcastWithChannelInfo> repeatingBroadcasts = null;
		
		RepeatingBroadcastsForBroadcast repeatingBroadcastObject = getCache().getNonPersistentRepeatingBroadcasts();
		
		if(repeatingBroadcastObject != null) 
		{
			repeatingBroadcasts = repeatingBroadcastObject.getRelatedBroadcasts();
		}
		
		return repeatingBroadcasts;
	}
	
	
	
	public void pushToSelectedBroadcastWithChannelInfo(final TVBroadcastWithChannelInfo selectedBroadcast) 
	{
		getCache().pushToNonPersistentSelectedBroadcastWithChannelInfo(selectedBroadcast);
	}
	
	
	
	public void popFromSelectedBroadcastWithChannelInfo() 
	{
		getCache().popFromNonPersistentSelectedBroadcastWithChannelInfo();
	}
	
	
	
	public TVBroadcastWithChannelInfo getLastSelectedBroadcastWithChannelInfo() 
	{
		TVBroadcastWithChannelInfo runningBroadcast = getCache().getNonPersistentLastSelectedBroadcastWithChannelInfo();
		
		return runningBroadcast;
	}
	
	
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getTaggedBroadcastsForSelectedTVDate()
	{
		TVDate tvDate = getTVDateSelected();
		
		return getTaggedBroadcastsUsingTVDate(tvDate);
	}
	
		
	
	public Integer getSelectedHour()
	{
		Integer selectedHour = getCache().getNonPersistentSelectedHour();
		
		return selectedHour;
	}
	
	
	
	public void setSelectedHour(final Integer selectedHour) 
	{
		getCache().setNonPersistentSelectedHour(selectedHour);
	}
	
	
	
	public void setSelectedTVChannelId(final TVChannelId tvChannelId) 
	{
		getCache().setNonPersistentTVChannelId(tvChannelId);
	}
	
	
	
	public TVChannelId getSelectedTVChannelId() 
	{
		TVChannelId tvChannelId = getCache().getNonPersistentTVChannelId();
		
		return tvChannelId;
	}
	
	
	
	public String getUserLastname()
	{
		String userLastname = getCache().getUserLastname();
		
		return userLastname;
	}
	
	
	
	public String getUserFirstname() 
	{
		String userFirstname = getCache().getUserFirstname();
		
		return userFirstname;
	}
	
	
	
	public String getUserEmail() 
	{
		String userEmail = getCache().getUserEmail();
		
		return userEmail;
	}
	
	
	
	public String getUserId() 
	{
		String userId = getCache().getUserId();
		
		return userId;
	}
	
	
	
	public String getUserProfileImage() 
	{
		String userId = getCache().getUserProfileImageUrl();
		
		return userId;
	}
	
	
	
	public List<TVChannel> getTVChannelsAll()
	{
		List<TVChannel> tvChannelsAll = getCache().getTvChannels();
		
		return tvChannelsAll;
	}
	
	
	
	public List<UserLike> getUserLikes()
	{
		List<UserLike> userLikes = getCache().getUserLikes();
		
		return userLikes;
	}
	
	
	
	public boolean isContainedInUserLikes(final UserLike userLike)
	{
		boolean isContainedInUserLikes = getCache().isInUserLikes(userLike);
		
		return isContainedInUserLikes;
	}
	
	
	
	public boolean isContainedInUsedChannelIds(final TVChannelId channelId)
	{
		boolean isContainedInUsedChannelIds = getCache().isInUsedChannelIds(channelId);
		
		return isContainedInUsedChannelIds;
	}
	
	
	
	public TVChannel getTVChannelById(final TVChannelId tvChannelId)
	{
		return getCache().getTVChannelById(tvChannelId);
	}
	
	
	
	public String getWelcomeMessage()
	{
		String welcomeMessage = getCache().getWelcomeMessage();
		
		return welcomeMessage;
	}
	
	
	
	public HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getTaggedBroadcastsUsingTVDate(final TVDate tvDate)
	{
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcasts = getCache().getTaggedBroadcastsUsingTVDate(tvDate);
		
		return taggedBroadcasts;
	}
	
	
	
	public ArrayList<TVBroadcastWithChannelInfo> getPopularBroadcasts() 
	{
		ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts = getCache().getPopularBroadcasts();
		
		return popularBroadcasts;
	}
	
	
	
	public List<TVDate> getTVDates() 
	{
		 List<TVDate> tvDates = getCache().getTvDates();
		 
		 return tvDates;
	}
	
	
	
	public int getDisqusTotalPostsForLatestBroadcast()
	{
		int disqusTotalPostsForLatestBroadcast = getCache().getDisqusTotalPostsForLatestBroadcast();
		
		return disqusTotalPostsForLatestBroadcast;
	}
	
	
	
	/* METHODS TO FETCH COMPETITION DATA FROM CACHE */	
	
	public Team getTeamById(final long teamId)
	{
		Team matchingTeam = getCache().getCompetitionsData().getTeamByIDForSelectedCompetition(teamId);
		
		return matchingTeam;
	}
	
	
	
	public List<TeamSquad> getSquadByTeamId(
			final long teamId,
			final boolean includeCoach)
	{
		List<TeamSquad> squadToReturn = new ArrayList<TeamSquad>();
		
		List<TeamSquad> squadAll = getCache().getCompetitionsData().getSquadByTeamIDForSelectedCompetition(teamId);
		
		if(squadAll != null)
		{
			if(includeCoach == false)
			{
				for (TeamSquad squad : squadAll) 
				{
					if(squad.getPosition() != EventLineUpPosition.COACH) 
					{
						squadToReturn.add(squad);
					}
				}
			}
			else
			{
				squadToReturn = squadAll;
			}
		}
		
		return squadToReturn;
	}
	
	
	
	public Event getNextUpcomingEventForSelectedCompetition(
			final boolean filterFinishedEvents, 
			final boolean filterLiveEvents)
	{
		Event matchingEvent = null;
		
		List<Event> events = getCache().getCompetitionsData().getEventsForSelectedCompetition();
		
		if(events.isEmpty() == false)
		{
			if (filterFinishedEvents) {
				events = filterFinishedEvents(events);
			}
			
			if (filterLiveEvents) {
				events = filterLiveEvents(events);
			}
			
			matchingEvent = events.get(0);
		}
		
		for(Event event : events)
		{
			Calendar eventStartTimeCalendar = event.getEventDateCalendarLocal();
			
			if(matchingEvent.getEventDateCalendarLocal().after(eventStartTimeCalendar))
			{
				matchingEvent = event;
			}
		}
		
		return matchingEvent;
	}
	
	
	
	private List<Event> filterLiveEvents(List<Event> events) 
	{
		List<Event> filteredEvents = new ArrayList<Event>();
		
		for (Event ev: events) 
		{
			if(!ev.isLive()) 
			{
				filteredEvents.add(ev);
			}
		}
		
		return filteredEvents;
	}
	
	
	
	private List<Event> filterFinishedEvents(List<Event> events) 
	{
		List<Event> filteredEvents = new ArrayList<Event>();
		
		for (Event ev: events) 
		{
			if(!ev.isFinished()) 
			{
				filteredEvents.add(ev);
			}
		}
		
		return filteredEvents;
	}
	
	
	
	public Event getLiveEventForSelectedCompetition() 
	{
		List<Event> events = getCache().getCompetitionsData().getEventsForSelectedCompetition();
		
		if (events != null) 
		{
			events = filterFinishedEvents(events);
		}
		
		for (Event ev : events) {
			if (ev.isLive()) {
				return ev;
			}
		}
		
		return null;
	}
	
	
	
	public Map<Long, List<Event>> getAllEventsGroupedByGroupStageForSelectedCompetition()
	{
		return getCache().getCompetitionsData().getEventsGroupedByFirstPhase();
	}
	
	
	
	public Map<Long, List<Event>> getAllEventsGroupedBySecondStageForSelectedCompetition()
	{
		return getCache().getCompetitionsData().getEventsGroupedBySecondPhase();
	}
	
	
	
	public List<Event> getEventsForPhaseInSelectedCompetition(final long phaseId)
	{
		return getCache().getCompetitionsData().getEventsForPhase(phaseId);
	}
	
	
	
	public Event getEventByIDInSelectedCompetition(final long eventId)
	{
		Competition selectedCompetition = getCache().getCompetitionsData().getSelectedCompetition();
		
		long selectedCompetitionId = selectedCompetition.getCompetitionId();
		
		return getCache().getCompetitionsData().getEventByID(selectedCompetitionId, eventId);
	}
	
	
	
	public Map<Long, List<Standings>> getAllStandingsGroupedByPhaseForSelectedCompetition()
	{
		return getCache().getCompetitionsData().getStandingsByPhaseForSelectedCompetition();
	}
	
	
		
	public List<Competition> getVisibleCompetitions()
	{
		return getAllCompetitions(false);
	}
	
	
	
	public Competition getVisibleRandomCompetition()
	{
		// List<Competition> competitions = getVisibleCompetitions();
		
		// TODO - Revert the random
		//int randomCompetitionIndex = RandomNumberUtils.getRandomIntegerInRange(0, competitions.size());
		
		Competition competition = getCompetitionByID(Constants.FIFA_COMPETITION_ID);
		
		return competition;
	}
	
	
	
	public List<Competition> getAllCompetitions(final boolean inludeInvisible)
	{
		List<Competition> allCompetitions = getCache().getCompetitionsData().getAllCompetitions();
		
		if(inludeInvisible)
		{
			return allCompetitions;
		}
		else
		{
			List<Competition> onlyVisibleCompetitions = new ArrayList<Competition>();
			
			for(Competition competition : allCompetitions)
			{
				if(competition.isVisible())
				{
					onlyVisibleCompetitions.add(competition);
				}
			}
			
			return onlyVisibleCompetitions;
		}
	}
	
	
	
	public List<Phase> getAllPhasesForSelectedCompetition()
	{
		return getCache().getCompetitionsData().getPhasesForSelectedCompetition();
	}
	
	
	
	public void setSelectedCompetition(final Competition competition)
	{
		long competitionID = competition.getCompetitionId();
		
		getCache().getCompetitionsData().setSelectedCompetition(competitionID);
	}
	
	
	
	public Competition getCompetitionByID(final long competitionID)
	{
		return getCache().getCompetitionsData().getCompetitionByID(competitionID);
	}
	
	
	
	/**
	 * Finds a competition containing the team. 
	 * 
	 * WARNING: May be inaccurate if team is in many competitions.
	 * 
	 * @param team
	 * @return competition containing the team
	 */
	public Competition getCompetitionByTeam(final Team team) 
	{
		return getCache().getCompetitionsData().getCompetitionByTeam(team);
	}
	
	
	
	public Phase getPhaseByIDForSelectedCompetition(final long phaseID)
	{
		return getCache().getCompetitionsData().getPhaseByIDForSelectedCompetition(phaseID);
	}
	
	

//	public Phase getPhaseByTeamIDForSelectedCompetition(long teamID)
//	{
//		return null;
//	}
	
	
	
	public List<Event> getEventsByTeamIDForSelectedCompetition(
			final boolean filterFinishedEvents, 
			final boolean filterLiveEvents, 
			final long teamId)
	{
		List<Event> events = getCache().getCompetitionsData().getEventsForSelectedCompetition();
		List<Event> eventsByTeamID = new ArrayList<Event>();
		
		if (events != null) 
		{	
			if (filterFinishedEvents)
			{
				events = filterFinishedEvents(events);
			}
			
			if (filterLiveEvents)
			{
				events = filterLiveEvents(events);
			}
			
			for (Event ev : events) 
			{	
				if (ev.getHomeTeamId() == teamId || ev.getAwayTeamId() == teamId) 
				{
					eventsByTeamID.add(ev);
				}
			}
			
			events = eventsByTeamID;
		}
		
		return events;
	}
	
	
	
	public Event getEventById(
			final Long competitionId,
			final Long eventId)
	{
		Event event = getCache().getCompetitionsData().getEventByID(competitionId, eventId);
		
		return event;
	}
	
	
	
	public Event getEventByIDForSelectedCompetition(final Long eventId)
	{
		Event event = null;
		
		Competition competition = getCache().getCompetitionsData().getSelectedCompetition();

		if(competition != null)
		{
			Long competitionID = competition.getCompetitionId();
			
			event = getCache().getCompetitionsData().getEventByID(competitionID, eventId);
		}
		
		if (event == null)
		{
			Log.w(TAG, "Event for ID " + eventId + "  is null");
		}
		
		return event;
	}
	
	
	
	public void setSelectedCompetitionProcessedData()
	{
		getCache().getCompetitionsData().setSelectedCompetitionProcessedData();
	}
	
	
	
	public List<EventLineUp> getAllLineUpDataByEventIDForSelectedCompetition(Long eventID)
	{
		return getCache().getCompetitionsData().getEventLineUpForEventInSelectedCompetition(eventID);
	}
	
	
	
	public List<EventLineUp> getSubstitutesLineUpDataByEventIDForSelectedCompetition(
			final Long eventID,
			final Long teamID)
	{
		List<EventLineUp> lineups = new ArrayList<EventLineUp>();
		
		List<EventLineUp> lineupsAll = getAllLineUpDataByEventIDForSelectedCompetition(eventID);
		
		for (EventLineUp lineup : lineupsAll)
		{
			if (lineup.isInStartingLineUp() == false && 
				lineup.getTeamId() == teamID.longValue() &&
				lineup.getPosition() != EventLineUpPosition.REFEREE) 
			{
				lineups.add(lineup);
			}
		}
		
		return lineups;
	}
	
	
	
	public List<EventLineUp> getInStartingLineUpLineUpDataByEventIDForSelectedCompetition(
			final Long eventId,
			final Long teamId)
	{
		List<EventLineUp> lineups = new ArrayList<EventLineUp>();
		
		List<EventLineUp> lineupsAll = getAllLineUpDataByEventIDForSelectedCompetition(eventId);
		
		for (EventLineUp lineup : lineupsAll) 
		{
			if (lineup.isInStartingLineUp() &&
				lineup.getTeamId() == teamId.longValue() &&
				lineup.getPosition() != EventLineUpPosition.REFEREE) 
			{
				lineups.add(lineup);
			}
		}
		
		return lineups;
	}
	
	
	
	public List<EventHighlight> getHighlightsDataByEventIDForSelectedCompetition(
			final Long eventID,
			final boolean excludeUnknownHighlights)
	{
		List<EventHighlight> highlightsToReturn = new ArrayList<EventHighlight>();
		
		List<EventHighlight> highlightsFromCache = getCache().getCompetitionsData().getEventHighlightsForEventInSelectedCompetition(eventID);
				
		if(highlightsFromCache != null)
		{
			if(excludeUnknownHighlights)
			{
				for(EventHighlight eventHighlight : highlightsFromCache)
				{
					boolean isUnknow = eventHighlight.getType() == EventHighlightActionEnum.UNKNOWN;
					
					if(isUnknow == false)
					{
						highlightsToReturn.add(eventHighlight);
					}
				}
			}
			else
			{
				highlightsToReturn = highlightsFromCache;
			}
		}
		
		return highlightsToReturn;
	}
	
	
	
	public List<Standings> getStandingsForPhaseInSelectedCompetition(final Long phaseID)
	{
		List<Standings> standingsToReturn = getCache().getCompetitionsData().getEventStandingsForPhaseInSelectedCompetition(phaseID);
		
		if(standingsToReturn == null)
		{
			standingsToReturn = new ArrayList<Standings>();
		}
		
		return standingsToReturn;
	}
	
	
	
	public List<Notification> getNotifications()
	{
		return getCache().getNotifications();
	}
	
	
	
	public Notification getNotificationWithParameters(
			final String channelId, 
			final String programId,
			final Long beginTimeInMilliseconds,
			final Long competitionId,
			final Long eventId)
	{
		Notification notification = getCache().getNotificationWithParameters(channelId, programId, beginTimeInMilliseconds, competitionId, eventId);
		
		return notification;
	}
	
	
	
	public Notification getNotificationWithParameters(
			final String channelId,
			final String progarmId,
			final Long beginTimeInMilliseconds)
	{
		Notification notification = getCache().getNotificationWithParameters(channelId, progarmId, beginTimeInMilliseconds);
		
		return notification;
	}
	
	
	
	public Notification getNotificationWithId(final int notificationId)
	{
		return getCache().getNotificationWithId(notificationId);
	}
	
	
	
	public void addNotification(final Notification notification)
	{
		getCache().addNotification(notification);
	}
	
	
	
	public void removeNotificationWithId(final int notificationID)
	{
		getCache().removeNotificationWithID(notificationID);
	}
	
	
	
	protected void clearUserCache() 
	{
		getCache().clearUserData();
		getCache().clearTVChannelIdsUser();
		getCache().useDefaultChannelIds();
		getCache().clearUserLikes();
	}
	
	
	
	public UserTutorialStatus getUserTutorialStatus() 
	{	
		return getCache().getUserTutorialStatus();
	}
	
	
	
	/**
	 * User has seen the tutorial only once.
	 * If the user dont open the app for at least two
	 * weeks, then show the tutorial again.
	 * 
	 */
	public void setUserHasSeenTutorialOnce()
	{
		getCache().setUserTutorialStatus(UserTutorialStatusEnum.SEEN_ONCE);
	}
	
	
	
	/**
	 * Never show tutorial again.
	 * 
	 */
	public void setUserHasSeenTutorialTwice() 
	{
		getCache().setUserTutorialStatus(UserTutorialStatusEnum.NEVER_SHOW_AGAIN);
	}
	
	
	
	public void setDateUserLastOpenApp() 
	{
		UserTutorialStatusEnum status = getCache().getUserTutorialStatus().getUserTutorialStatus();
		
		if (status != UserTutorialStatusEnum.NEVER_SHOW_AGAIN) 
		{
			Calendar now = DateUtils.getNowWithGMTTimeZone();

			getCache().setUserTutorialDateOpenApp(now);
		}
	}
}
