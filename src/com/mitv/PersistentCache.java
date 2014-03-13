
package com.mitv;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mitv.models.AppConfiguration;
import com.mitv.models.AppVersion;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannel;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVChannelId;
import com.mitv.models.TVDate;
import com.mitv.models.TVFeedItem;
import com.mitv.models.TVGuide;
import com.mitv.models.TVTag;
import com.mitv.models.UserLike;
import com.mitv.models.UserLoginData;
import com.mitv.models.orm.AppVersionORM;
import com.mitv.models.orm.TVDateORM;
import com.mitv.models.orm.TVTagORM;
import com.mitv.models.orm.UserLoginDataORM;
import com.mitv.models.orm.base.AbstractOrmLiteClass;



public class PersistentCache
{
	private static final String TAG = PersistentCache.class.getName();
	
	/* We only need to use this variable, and not "tvChannelIdsDefault" or "tvChannelIdsUser",
	 * "tvChannelIdsUsed" will hold either of those variables above mentioned. When you login/logout
	 * the data held by this variable will change between those two. */
	private List<TVChannelId> tvChannelIdsUsed;
	
	/* Should contain ALL channels provided by the backend, some hundreds or so */
	private List<TVChannel> tvChannels;
	
	/* Maps a day to a TVGuide, G, using the id of the TVDate as key. G may contain TVChannelGuides for TVChannels that the user have removed from her channel list */
	private HashMap<String, TVGuide> tvGuidesAll;
	
	/* This map has the same structure as 'tvGuidesAll' but it only contains the guides that should be presented to the user */
	private HashMap<String, TVGuide> tvGuidesMy;
	
	/* PERSISTENT USER DATA, WILL BE SAVED TO STORAGE ON EACH SET */
	private UserLoginData userData;
	private List<UserLike> userLikes;
	
	private AppVersion appVersionData;
	private AppConfiguration appConfigurationData;
	
	private List<TVTag> tvTags;
	private List<TVDate> tvDates;
	private List<TVChannelId> tvChannelIdsDefault;
	private List<TVChannelId> tvChannelIdsUser;
	
	private ArrayList<TVFeedItem> activityFeed;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;
	
	
	
	/* Should only be used by the ContentManager */
	public PersistentCache()
	{
		this.tvGuidesAll = new HashMap<String, TVGuide>();
			
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		AbstractOrmLiteClass.initDB(context, Constants.CACHE_DATABASE_NAME, Constants.CACHE_DATABASE_VERSION, null);
		
		this.appVersionData = AppVersionORM.getAppVersion();
		this.appConfigurationData = null; //AppConfigurationORM.getAppConfiguration();
		this.userData = UserLoginDataORM.getUserLoginData();
		
		this.tvTags = TVTagORM.getTVTags();
		this.tvDates = TVDateORM.getTVDates();
	}
	
	
	
	public synchronized boolean containsActivityFeedData() 
	{
		boolean containsActivityFeedData = (activityFeed != null && !activityFeed.isEmpty());
		
		return containsActivityFeedData;
	}
	
	
	public synchronized boolean containsPopularBroadcasts() 
	{
		boolean containsPopularBroadcasts = (popularBroadcasts != null && !popularBroadcasts.isEmpty());
		
		return containsPopularBroadcasts;
	}
	
	
	public synchronized boolean containsUserLikes() 
	{
		boolean containsUserLikes = (userLikes != null);
		
		return containsUserLikes;
	}
	
	
	public synchronized boolean containsAppConfigData() 
	{
		boolean containsAppConfig = (appConfigurationData != null);
		
		return containsAppConfig;
	}
	
	
	public synchronized boolean containsAppVersionData() 
	{
		boolean containsAppVersionData = (appVersionData != null);
		
		return containsAppVersionData;
	}
	
	
	public synchronized boolean containsApiVersionData()
	{
		boolean containsApiVersionData = (appVersionData != null);
		
		return containsApiVersionData;
	}
	
	
	public boolean containsTVDates() 
	{
		boolean containsTVDates = (tvDates != null && !tvDates.isEmpty());
		
		return containsTVDates;
	}
	
	
	public synchronized boolean containsTVTags() 
	{
		boolean containsTVTags = (tvTags != null && !tvTags.isEmpty());
		
		return containsTVTags;
	}
	
	
	public synchronized boolean containsTVChannels() 
	{
		boolean containsTVChannels = (tvChannels != null && !tvChannels.isEmpty());
		
		return containsTVChannels;
	}
	
	
	public synchronized boolean containsTVChannelIdsUser()
	{
		boolean containsTVChannelIdsUser = (tvChannelIdsUser != null && !tvChannelIdsUser.isEmpty());
		
		return containsTVChannelIdsUser;
	}
	
	
	
	/* USER DATA */
	
	public synchronized String getUserToken() 
	{
		String userToken = null;
		
		if(userData != null) 
		{
			userToken = userData.getToken();
		}
		
		return userToken;
	}
	
	
	public synchronized boolean isLoggedIn() 
	{
		boolean isLoggedIn = !TextUtils.isEmpty(getUserToken());
		
		return isLoggedIn;
	}
	
	
	public synchronized String getUserLastname()
	{
		return userData.getUser().getLastName();
	}
	
	
	public synchronized String getUserFirstname()
	{
		return userData.getUser().getFirstName();
	}
	
	
	public synchronized String getUserEmail()
	{
		return userData.getUser().getEmail();
	}
	
	
	public synchronized String getUserId()
	{
		return userData.getUser().getUserId();
	}
	
	
	public synchronized String getUserProfileImageUrl() 
	{
		return userData.getProfileImage().getUrl();
	}
	
	
	public synchronized void setUserData(UserLoginData userData) 
	{
		this.userData = userData;
		
		UserLoginDataORM userLoginDataORM = new UserLoginDataORM(userData);
		userLoginDataORM.saveInAsyncTask();
	}
	
	
	public void clearUserData() 
	{
		UserLoginDataORM userLoginDataORM = new UserLoginDataORM(userData);
		userLoginDataORM.delete();
		
		userData = null;
	}
	
	
	
	/* USER LIKES */
	
	public synchronized List<UserLike> getUserLikes() 
	{
		return userLikes;
	}

	
	public synchronized void setUserLikes(final List<UserLike> userLikes) 
	{
		this.userLikes = userLikes;
	}
	
	
	public synchronized void addUserLike(final UserLike userLike) 
	{
		if(userLikes != null)
		{
			this.userLikes.add(userLike);
		}
		else
		{
			Log.w(TAG, "Attempted to add user like without data in cache.");
		}
	}
	
	
	public synchronized void removeUserLike(final UserLike userLikeToRemove) 
	{
		if(userLikes != null)
		{
			int indexToRemove = -1;
			
			for(int i=0; i<userLikes.size(); i++)
			{
				UserLike userLike = userLikes.get(i);
				
				if(userLike.equals(userLikeToRemove))
				{
					indexToRemove = i;
					break;
				}
			}
			
			if(indexToRemove >= 0)
			{
				this.userLikes.remove(indexToRemove);
			}
		}
		else
		{
			Log.w(TAG, "Attempted to remove user like without data in cache.");
		}
	}
	
	
	
	public synchronized boolean isInUserLikes(final UserLike userLikeToCheck) 
	{
		boolean isContained = false;
		
		if(userLikes != null)
		{
			for(UserLike userLike : userLikes)
			{
				boolean isEqual = userLike.equals(userLikeToCheck);
				
				if(isEqual)
				{
					isContained = true;
					break;
				}
			}
		}
		
		return isContained;
	}
	
	
	
	/* APP VERSION */
	
	public synchronized void setAppVersionData(final AppVersion appVersionData) 
	{
		this.appVersionData = appVersionData;
		
		AppVersionORM.createAndSaveInAsyncTask(appVersionData);
	}

	
	public synchronized AppConfiguration getAppConfigData() 
	{
		return appConfigurationData;
	}
	
	
	public synchronized boolean isAPIVersionSupported() 
	{
		boolean isAPIVersionSupported = false;
		
		if(appVersionData != null)
		{
			isAPIVersionSupported = appVersionData.isAPIVersionSupported();
		}
		
		return isAPIVersionSupported;
	}
	
	
	public synchronized String getWelcomeMessage() 
	{
		String welcomeMessage = "";
		
		if(appConfigurationData != null) 
		{
			welcomeMessage = appConfigurationData.getWelcomeToast();
		}
		
		return welcomeMessage;
	}
	
	
	
	/* APP CONFIGURATION */
	
	public synchronized void setAppConfigData(final AppConfiguration appConfigurationData) 
	{
		this.appConfigurationData = appConfigurationData;
		
		//new AppConfigurationORM(appConfigurationData).saveInAsyncTask();
	}
	
	
	public synchronized int getFirstHourOfTVDay() 
	{
		/* Defaulting to value of 6 */
		int firstHourOfTVDay = 6;
		
		if(appConfigurationData != null) 
		{
			firstHourOfTVDay = appConfigurationData.getFirstHourOfDay();
		}
		
		return firstHourOfTVDay;
	}
	
	
	
	/* TV TAGS */
	
	public synchronized List<TVTag> getTvTags() 
	{
		return tvTags;
	}
	
	
	public synchronized void setTvTags(final List<TVTag> tvTags) 
	{
		this.tvTags = tvTags;
		
		TVTagORM.createAndSaveInAsyncTask(tvTags);
	}
	
	
	
	/* TV DATES */
	
	public synchronized List<TVDate> getTvDates() 
	{
		return tvDates;
	}
	

	public synchronized void setTvDates(final List<TVDate> tvDates) 
	{
		this.tvDates = tvDates;
		
		TVDateORM.createAndSaveInAsyncTask(tvDates);
	}
	
	
	public synchronized TVDate getTvDateAtIndex(int index) 
	{
		if(tvDates != null)
		{
			return tvDates.get(index);
		}
		else
		{
			return null;
		}
	}
	
	
	
	/* TV CHANNELS */
	
	public synchronized List<TVChannel> getTvChannels() 
	{
		return tvChannels;
	}

	
	public synchronized void setTvChannels(final List<TVChannel> tvChannels) 
	{
		this.tvChannels = tvChannels;
	}
	
	
	//TODO dont iterate through a list, change tvChannels to a Map instead?
	public synchronized TVChannel getTVChannelById(TVChannelId tvChannelId) 
	{
		for(TVChannel tvChannel : tvChannels) 
		{
			if(tvChannel.getChannelId().equals(tvChannelId))
			{
				return tvChannel;
			}
		}
		
		return null;
	}

	
	
	/* TV FEED ITEMS */
	
	public synchronized ArrayList<TVFeedItem> getActivityFeed() 
	{
		return activityFeed;
	}

	
	public synchronized void setActivityFeed(ArrayList<TVFeedItem> activityFeed) 
	{
		this.activityFeed = activityFeed;
	}
	
	
	
	public synchronized void addMoreActivityFeedItems(ArrayList<TVFeedItem> additionalActivityFeedItems) 
	{
		if(this.activityFeed == null)
		{
			activityFeed = new ArrayList<TVFeedItem>();
		}
		
		activityFeed.addAll(additionalActivityFeedItems);
	}

	
	
	/* TV BROADCASTS */
	
	public synchronized ArrayList<TVBroadcastWithChannelInfo> getPopularBroadcasts() 
	{
		return popularBroadcasts;
	}

	
	public synchronized void setPopularBroadcasts(ArrayList<TVBroadcastWithChannelInfo> popularFeed) 
	{
		this.popularBroadcasts = popularFeed;
	}

	
	
	/* TV CHANNEL IDS USED */
	
	public synchronized List<TVChannelId> getTvChannelIdsUsed() 
	{
		if(tvChannelIdsUsed == null) 
		{
			if(tvChannelIdsDefault != null) 
			{
				tvChannelIdsUsed = tvChannelIdsDefault;
			} 
			else 
			{
				if(tvChannelIdsUser != null) 
				{
					tvChannelIdsUsed = tvChannelIdsUser;
				}
			}
		}
		
		return tvChannelIdsUsed;
	}
	
	
	public synchronized void setTvChannelIdsUsed(List<TVChannelId> tvChannelIdsUsed) 
	{
		this.tvChannelIdsUsed = tvChannelIdsUsed;
		
		/* When changing to use another list as TVChannel ids, then we must clear the list of the TVGuides that should be presented to the user
		 * since the guide is dependent on the tv channel ids. */
		clearTVGuidesMy();
	}
	
	
	public synchronized boolean isInUsedChannelIds(TVChannelId channelId)
	{
		boolean isContainedInUserChannels = false;
		
		for(TVChannelId channelIdTmp : tvChannelIdsUsed)
		{
			if(channelIdTmp.getChannelId().equals(channelId.getChannelId()))
			{
				isContainedInUserChannels = true;
				break;
			}
		}
		
		return isContainedInUserChannels;
	}
	
	
	public synchronized void clearTVChannelIdsUser() 
	{
		if(tvChannelIdsUser != null) 
		{
			tvChannelIdsUser.clear();
		}
	}
	
	
	
	/* TV CHANNEL IDS DEFAULT */
	
	public synchronized void useDefaultChannelIds()
	{
		this.tvChannelIdsUsed = tvChannelIdsDefault;
	}

	
	public synchronized void setTvChannelIdsDefault(List<TVChannelId> tvChannelIdsDefault) 
	{
		this.tvChannelIdsDefault = tvChannelIdsDefault;
		
		/* Only use the default ids if we are not logged in */
		if(!isLoggedIn()) 
		{
			setTvChannelIdsUsed(tvChannelIdsDefault);
		}
	}
	
	
	
	/* TV CHANNEL IDS USER */
	
	public void setTvChannelIdsUser(ArrayList<TVChannelId> tvChannelIdsUser) 
	{
		this.tvChannelIdsUser = tvChannelIdsUser;
		
		setTvChannelIdsUsed(tvChannelIdsUser);
	}
	
	
	
	/* TV GUIDES MY */
	
	protected void clearTVGuidesMy() 
	{
		tvGuidesMy = null;
	}
	
	
	/**
	 * This method return the TVGuide for specified TVDate, but before returning the TVGuide
	 * the TVChannelGuides for channels that the user has not selected are filtered out
	 * @param tvDate
	 * @return
	 */
	public synchronized TVGuide getTVGuideUsingTVDate(TVDate tvDate) 
	{
		if (tvGuidesMy == null) 
		{
			tvGuidesMy = new HashMap<String, TVGuide>();
		}

		TVGuide guideForWithMyChannels = tvGuidesMy.get(tvDate.getId());

		if (guideForWithMyChannels == null) {

			TVGuide guideForAllChannels = getTVGuideUsingTVDateNonFiltered(tvDate);

			if(guideForAllChannels != null) 
			{
				ArrayList<TVChannelGuide> allChannelGuides = guideForAllChannels.getTvChannelGuides();
				
				ArrayList<TVChannelGuide> myChannelGuides = new ArrayList<TVChannelGuide>();
	
				for (TVChannelGuide channelGuide : allChannelGuides) 
				{
					if (tvChannelIdsUsed.contains(channelGuide.getChannelId())) 
					{
						myChannelGuides.add(channelGuide);
					}
				}
	
				guideForWithMyChannels = new TVGuide(tvDate, myChannelGuides);
				
				tvGuidesMy.put(tvDate.getId(), guideForWithMyChannels);
			}
		}

		return guideForWithMyChannels;
	}
	
	
	
	/* TV GUIDES ALL */
	
	protected TVGuide getTVGuideUsingTVDateNonFiltered(TVDate tvDate) 
	{
		String tvDateId = tvDate.getId();
		
		TVGuide guideForAllChannels = tvGuidesAll.get(tvDateId);
		
		return guideForAllChannels;
	}
	
	
	public synchronized void addNewTVChannelGuidesUsingDayAndTvGuide(TVDate tvDate, TVGuide tvGuide) 
	{
		clearTVGuidesMy();
		
		TVGuide guideForAllChannels = getTVGuideUsingTVDateNonFiltered(tvDate);
		
		if(guideForAllChannels != null) 
		{
			/* Maybe unnecessary safety check to verify that the guides are for the same day */
			if(tvGuide.getTvDate().equals(guideForAllChannels.getTvDate())) 
			{
				ArrayList<TVChannelGuide> allChannelGuides = guideForAllChannels.getTvChannelGuides();
				ArrayList<TVChannelGuide> newChannelGuides = tvGuide.getTvChannelGuides();
				
				/* Add all the new TVChannel Guides to the list, results in duplicates sometimes */
				//TODO NewArc fix duplicates problem, happens when logged in then log out and fetching default channels
				allChannelGuides.addAll(newChannelGuides);
				
				ArrayList<TVChannelGuide> allChannelGuidesWithoutDuplicates = new ArrayList<TVChannelGuide>();
				
				for(TVChannelGuide channelGuide : allChannelGuides) 
				{
					if(!allChannelGuidesWithoutDuplicates.contains(channelGuide)) 
					{
						allChannelGuidesWithoutDuplicates.add(channelGuide);
					} 
					else 
					{
						Log.d(TAG, "Duplicate");
					}
				}
				guideForAllChannels.setTvChannelGuides(allChannelGuidesWithoutDuplicates);
			} 
			else 
			{
				Log.e(TAG, "TVDate for new guide and existing don't match, but they should");
			}
		} 
		else 
		{
			guideForAllChannels = tvGuide;
		}
		
		this.tvGuidesAll.put(tvDate.getId(), guideForAllChannels);
	}
}