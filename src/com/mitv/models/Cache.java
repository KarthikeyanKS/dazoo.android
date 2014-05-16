
package com.mitv.models;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Stack;

import android.content.Context;
import android.util.Log;

import com.mitv.Constants;
import com.mitv.SecondScreenApplication;
import com.mitv.models.objects.mitvapi.RepeatingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.SearchResultsForQuery;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelGuide;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVGuide;
import com.mitv.models.objects.mitvapi.UpcomingBroadcastsForBroadcast;
import com.mitv.models.objects.mitvapi.UserLike;



public class Cache 
	extends PersistentCache
{
	private static final String TAG = Cache.class.getName();
	
	/* NON-PERSISTENT USER DATA, USED FOR PASSING DATA BETWEEN ACTIVITIES */
	
	/* Key for the wrapping Map, wMap, is the id from the TVDate, which gets you an inner Map, iMap, which key is a TVTag
	 * The value stored in iMap is a list of "tagged" broadcasts for the TVTag provided as key to iMap. 
	 * E.g.
	 * 
	 * wMap:
	 * {"2014-02-20" = iMap1,
	 * "2014-02-21" = iMap2}
	 * 
	 * WHERE:
	 * iMap1:
	 * {"MOVIE" = [BroadcastA, BroadcastB],
	 * "SPORT" = [BroadcastX, BroadcastY]} 
	 * 
	 * */
	private HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>> nonPersistentTaggedBroadcastsForAllDays;
	
	private int nonPersistentTVDateSelectedIndex;
	private boolean nonPersistentFlagUpdatingGuide;
	private Stack<TVBroadcastWithChannelInfo> nonPersistentSelectedBroadcastsWithChannelInfo;
	private UpcomingBroadcastsForBroadcast nonPersistentUpcomingBroadcasts;
	private RepeatingBroadcastsForBroadcast nonPersistentRepeatingBroadcasts;
	private SearchResultsForQuery nonPersistentSearchResultsForQuery;
	private Integer nonPersistentSelectedHour;
	private TVChannelId nonPersistentSelectedTVChannelId;
	private Class<?> nonPersistentReturnActivity;
	private UserLike nonPersistentLikeToAddAfterLogin;
	private Calendar initialCallSNTPCalendar;
	private CompetitionsCacheData competitionsData;
	
	private int disqusTotalPostsForLatestBroadcast;
	
	
	
	/* Should only be used by the ContentManager */
	public Cache()
	{
		super();
		
		this.nonPersistentSelectedBroadcastsWithChannelInfo = new Stack<TVBroadcastWithChannelInfo>();
		
		this.nonPersistentFlagUpdatingGuide = false;
		
		this.initialCallSNTPCalendar = null;
		
		/* Default selected day to 0 */
		setTvDateSelectedIndex(0);
		
		this.competitionsData = new CompetitionsCacheData();
	}
	
	
	
	public static synchronized void clearAllPersistentCacheData()
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		context.deleteDatabase(Constants.CACHE_DATABASE_NAME);
	}
	
	
	
	public synchronized boolean isUpdatingGuide() {
		return nonPersistentFlagUpdatingGuide;
	}


	public synchronized void setUpdatingGuide(boolean nonPersistentFlagUpdatingGuide) {
		this.nonPersistentFlagUpdatingGuide = nonPersistentFlagUpdatingGuide;
	}


	public synchronized UserLike getLikeToAddAfterLogin() {
		return nonPersistentLikeToAddAfterLogin;
	}

	public synchronized void setLikeToAddAfterLogin(UserLike likeToAddAfterLogin) {
		this.nonPersistentLikeToAddAfterLogin = likeToAddAfterLogin;
	}

	public synchronized Class<?> getReturnActivity() {
		return nonPersistentReturnActivity;
	}

	public synchronized void setReturnActivity(Class<?> returnActivity) {
		this.nonPersistentReturnActivity = returnActivity;
	}
	
	public synchronized void clearReturnActivity() {
		setReturnActivity(null);
	}

	public synchronized SearchResultsForQuery getNonPersistentSearchResultsForQuery() {
		return nonPersistentSearchResultsForQuery;
	}

	public synchronized void setNonPersistentSearchResultsForQuery(SearchResultsForQuery nonPersistentSearchResultsForQuery) {
		this.nonPersistentSearchResultsForQuery = nonPersistentSearchResultsForQuery;
	}

	public synchronized boolean containsSearchResultForQuery(String searchQuery) {
		boolean containsSearchResultForQuery = false;
		if(nonPersistentSearchResultsForQuery != null) {
			String queryUsedForResultsInStorage = nonPersistentSearchResultsForQuery.getQueryString();
			if(queryUsedForResultsInStorage.equals(searchQuery)) {
				containsSearchResultForQuery = true;
			}
		}
		return containsSearchResultForQuery;
	}
	
	
	public synchronized boolean containsTVBroadcastWithChannelInfo(TVChannelId channelId, long beginTimeMillis) 
	{
		boolean containsTVBroadcastWithChannelInfo = false;
		
		if(nonPersistentSelectedBroadcastsWithChannelInfo.isEmpty() == false && getNonPersistentLastSelectedBroadcastWithChannelInfo().getChannel().getChannelId().equals(channelId.getChannelId()) && beginTimeMillis == getNonPersistentLastSelectedBroadcastWithChannelInfo().getBeginTimeMillis().longValue())
		{
			containsTVBroadcastWithChannelInfo = true;
		}
		
		return containsTVBroadcastWithChannelInfo;
	}
	
	
	public synchronized void addNewTVChannelGuidesForSelectedDayUsingTvGuide(TVGuide tvGuide) 
	{
		TVDate tvDate = getTvDateSelected();
		addNewTVChannelGuidesUsingDayAndTvGuide(tvDate, tvGuide);
	}
	
	
	
	
	
	public synchronized TVChannelGuide getTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) {
		TVDate selectedTVDate = getTvDateSelected();
		return getTVChannelGuideUsingTVChannelIdAndTVDate(tvChannelId, selectedTVDate);
	}
	
	
	public synchronized boolean containsTVChannelGuideUsingTVChannelIdForSelectedDay(TVChannelId tvChannelId) 
	{
		boolean containsTVChannelGuideUsingTVChannelIdForSelectedDay = false;
		
		TVDate selectedTVDate = getTvDateSelected();
		
		if(tvChannelId != null && selectedTVDate != null)
		{
			TVChannelGuide guide = getTVChannelGuideUsingTVChannelIdAndTVDate(tvChannelId, selectedTVDate);
			
			containsTVChannelGuideUsingTVChannelIdForSelectedDay = (guide != null);
		}
		
		return containsTVChannelGuideUsingTVChannelIdForSelectedDay;
	}
	
	
	public synchronized TVChannelGuide getTVChannelGuideUsingTVChannelIdAndTVDate(TVChannelId tvChannelId, TVDate tvDate) 
	{
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		
		if(tvGuide == null || tvChannelId == null)
		{
			return null;
		}
		
		ArrayList<TVChannelGuide> tvChannelGuides = tvGuide.getTvChannelGuides();
		
		TVChannelGuide tvChannelGuideFound = null;
		
		for(TVChannelGuide tvChannelGuide : tvChannelGuides) 
		{
			if(tvChannelGuide.getChannelId().getChannelId().equals(tvChannelId.getChannelId())) 
			{
				tvChannelGuideFound = tvChannelGuide;
				break;
			}
		}
		
		return tvChannelGuideFound;
	}
		
	public synchronized HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>> getTaggedBroadcastsForAllDays() {
		return nonPersistentTaggedBroadcastsForAllDays;
	}
	
	public synchronized void addTaggedBroadcastsForSelectedDay(HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay) 
	{
		TVDate tvDate = getTvDateSelected();
		addTaggedBroadcastsForDay(taggedBroadcastForDay, tvDate);
	}
	
	public synchronized void addTaggedBroadcastsForDay(HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay, TVDate tvDate) 
	{
		if(nonPersistentTaggedBroadcastsForAllDays == null) {
			nonPersistentTaggedBroadcastsForAllDays = new HashMap<String, HashMap<String, ArrayList<TVBroadcastWithChannelInfo>>>();
		}
		nonPersistentTaggedBroadcastsForAllDays.put(tvDate.getId(), taggedBroadcastForDay);
	}
	
	public synchronized HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> getTaggedBroadcastsUsingTVDate(TVDate tvDateAsKey) 
	{
		HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = nonPersistentTaggedBroadcastsForAllDays.get(tvDateAsKey.getId());
		return taggedBroadcastForDay;
	}
	
	public synchronized void purgeTaggedBroadcastForDay(TVDate tvDate) 
	{
		if(nonPersistentTaggedBroadcastsForAllDays != null && nonPersistentTaggedBroadcastsForAllDays.containsKey(tvDate.getId())) {
			nonPersistentTaggedBroadcastsForAllDays.remove(tvDate.getId());
		}
	}
	
		
	public synchronized boolean containsTVGuideForSelectedDay() 
	{
		TVDate tvDate = getTvDateSelected();
		
		boolean containsTVGuideForSelectedDay = false;
		
		if(tvDate != null) 
		{
			containsTVGuideForSelectedDay = containsTVGuideForTVDate(tvDate);
		}
		
		return containsTVGuideForSelectedDay;
	}
	
	
	public synchronized TVGuide getTVGuideForToday() 
	{
		TVDate tvDate = getTvDateAtIndex(0);
		
		return getTVGuideUsingTVDate(tvDate);
	}
	
	
	/* Verifies that the TVGuide we have (if any) contains a TVChannelGuide for all
	 * tv channels we have in tvChannelIdsUsed */
	public synchronized boolean containsTVGuideForTVDate(TVDate tvDate) 
	{
		boolean containsTVGuideForTVDate = true;
		
		TVGuide tvGuide = getTVGuideUsingTVDate(tvDate);
		
		if(tvGuide != null) 
		{
			ArrayList<TVChannelGuide> channelGuides = tvGuide.getTvChannelGuides();
			
			ArrayList<TVChannelId> channelIdsFromChannelGuides = new ArrayList<TVChannelId>();
			
			for(TVChannelGuide channelGuide : channelGuides) 
			{
				channelIdsFromChannelGuides.add(channelGuide.getChannelId());
			}
	
			for(TVChannelId tvChannelId : getTvChannelIdsUsed()) 
			{
				if(!channelIdsFromChannelGuides.contains(tvChannelId)) 
				{
					/* TODO
					 * 
					 * WARNING WARNING WARNING
					 * 
					 * If tvChannelId is not in channelIdsFromChannelGuides WE WILL RESTART FOREVER!!!
					 * We need to make sure from BE that user channel ids exists
					 * */
					
					containsTVGuideForTVDate = false;
					break;
				}
			}
		} else {
			containsTVGuideForTVDate = false;
		}
		
		return containsTVGuideForTVDate;
	}
	
	
	public synchronized boolean containsTaggedBroadcastsForTVDate(TVDate tvDate) {
		boolean containsTaggedBroadcastsForTVDate = false;
		if(nonPersistentTaggedBroadcastsForAllDays != null) {
			containsTaggedBroadcastsForTVDate = nonPersistentTaggedBroadcastsForAllDays.containsKey(tvDate.getId());
		}
		return containsTaggedBroadcastsForTVDate;
	}
	
	

	
	/**
	 * This method is probably not used by the contentManager, since the HomeActivity does not pass a TVDate object
	 * but rather an index (0-6). So this method is probably used by the
	 * @param tvDateSelectedIndex
	 */
	public synchronized void setTvDateSelectedIndex(int tvDateSelectedIndex) 
	{
		this.nonPersistentTVDateSelectedIndex = tvDateSelectedIndex;
	}
	
	
	public synchronized TVDate getTvDateSelected() 
	{
		TVDate tvDateSelected = null;
		
		tvDateSelected = getTvDateAtIndex(nonPersistentTVDateSelectedIndex);
		
		return tvDateSelected;
	}
	
	
	public synchronized int getTvDateSelectedIndex() 
	{
		return nonPersistentTVDateSelectedIndex;
	}
	
	
	public synchronized void setTvDateSelectedUsingIndex(int tvDateSelectedIndex) 
	{
		setTvDateSelectedIndex(tvDateSelectedIndex);
	}
	
	/* NON PERSISTENT USER DATA */
	/**
	 * Non-persistent
	 */
	public synchronized Integer getNonPersistentSelectedHour() {
		return nonPersistentSelectedHour;
	}
	
	public synchronized void setNonPersistentSelectedHour(Integer seletectedHour) {
		this.nonPersistentSelectedHour = seletectedHour;
	}
			
	public synchronized void pushToNonPersistentSelectedBroadcastWithChannelInfo(TVBroadcastWithChannelInfo nonPersistentSelectedBroadcastWithChannelInfo) 
	{
		this.nonPersistentSelectedBroadcastsWithChannelInfo.add(nonPersistentSelectedBroadcastWithChannelInfo);
	}
	
	public synchronized void popFromNonPersistentSelectedBroadcastWithChannelInfo() 
	{
		if(nonPersistentSelectedBroadcastsWithChannelInfo.isEmpty() == false)
		{
			this.nonPersistentSelectedBroadcastsWithChannelInfo.pop();
		}
		else
		{
			Log.w(TAG, "Stack is empty. Nothing to pop");
		}
	}
	
	public synchronized UpcomingBroadcastsForBroadcast getNonPersistentUpcomingBroadcasts() {
		return nonPersistentUpcomingBroadcasts;
	}

	public synchronized void setNonPersistentUpcomingBroadcasts(UpcomingBroadcastsForBroadcast nonPersistentUpcomingBroadcasts) {
		this.nonPersistentUpcomingBroadcasts = nonPersistentUpcomingBroadcasts;
	}
	
	public synchronized boolean containsUpcomingBroadcastsForBroadcast(String tvSeriesId) {
		boolean containsUpcomingBroadcastsForBroadcast = false;
		
		if(nonPersistentUpcomingBroadcasts != null) {
			String tvSeriesIdInStorage = nonPersistentUpcomingBroadcasts.getTvSeriesId();
			containsUpcomingBroadcastsForBroadcast = tvSeriesId.equals(tvSeriesIdInStorage);
		}
		
		return containsUpcomingBroadcastsForBroadcast;
	}

	public synchronized RepeatingBroadcastsForBroadcast getNonPersistentRepeatingBroadcasts() {
		return nonPersistentRepeatingBroadcasts;
	}
		
	public synchronized void setNonPersistentRepeatingBroadcasts(RepeatingBroadcastsForBroadcast nonPersistentRepeatingBroadcasts) {
		this.nonPersistentRepeatingBroadcasts = nonPersistentRepeatingBroadcasts;
	}
	
	public synchronized boolean containsRepeatingBroadcastsForBroadcast(String programId) {
		boolean containsRepeatingBroadcastsForBroadcast = false;
		
		if(nonPersistentRepeatingBroadcasts != null) {
			String programIdInStorage = nonPersistentRepeatingBroadcasts.getProgramId();
			containsRepeatingBroadcastsForBroadcast = programId.equals(programIdInStorage);
		}
		
		return containsRepeatingBroadcastsForBroadcast;
	}

	public synchronized TVBroadcastWithChannelInfo getNonPersistentLastSelectedBroadcastWithChannelInfo() 
	{
		if(nonPersistentSelectedBroadcastsWithChannelInfo.isEmpty() == false)
		{
			return nonPersistentSelectedBroadcastsWithChannelInfo.peek();
		}
		else
		{
			return null;
		}
	}
	
	public synchronized void setNonPersistentTVChannelId(TVChannelId tvChannelId) {
		this.nonPersistentSelectedTVChannelId = tvChannelId;
	}
	
	public synchronized TVChannelId getNonPersistentTVChannelId() {
		return nonPersistentSelectedTVChannelId;
	}



	public synchronized Calendar getInitialCallSNTPCalendar() 
	{
		return initialCallSNTPCalendar;
	}


	public synchronized void setInitialCallSNTPCalendar(Calendar initialCallSNTPCalendar) 
	{
		this.initialCallSNTPCalendar = initialCallSNTPCalendar;
	}



	public int getDisqusTotalPostsForLatestBroadcast() {
		return disqusTotalPostsForLatestBroadcast;
	}



	public void setDisqusTotalPostsForLatestBroadcast(
			int disqusTotalPostsForLatestBroadcast) {
		this.disqusTotalPostsForLatestBroadcast = disqusTotalPostsForLatestBroadcast;
	}



	public CompetitionsCacheData getCompetitionsData() {
		return competitionsData;
	}
}
