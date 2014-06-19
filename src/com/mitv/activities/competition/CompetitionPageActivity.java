
package com.mitv.activities.competition;



import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.SignUpSelectionActivity;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.adapters.list.CompetitionPageTodayListAdapter;
import com.mitv.adapters.pager.CompetitionTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.elements.CustomViewPager;
import com.mitv.ui.elements.EventCountDownTimer;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.viewpagerindicator.TabPageIndicator;



public class CompetitionPageActivity 
	extends BaseContentActivity
	implements ViewCallbackListener, FetchDataProgressCallbackListener, OnPageChangeListener
{
	private static final String TAG = CompetitionPageActivity.class.getName();
	
	
	private Competition competition;
	private Event event;
	
	private TabPageIndicator pageTabIndicator;
	private CustomViewPager viewPager;
	private CompetitionTabFragmentStatePagerAdapter pagerAdapter;
	private int selectedTabIndex;
	
	private LinearLayout beforeLayout;
	private RelativeLayout countDownLayout;
	private TextView remainingTimeInDays;
	private TextView remainingTimeInDaysTitle;
	private TextView remainingTimeInHours;
	private TextView remainingTimeInHoursTitle;
	private TextView remainingTimeInMinutes;
	private TextView remainingTimeInMinutesTitle;
	private TextView countdownTitle;
	private TextView nextGameText;
	private TextView eventStartTime;
	private TextView tvBroadcastChannels;
	private TextView team1Name;
	private ImageView team1Flag;
	private TextView team2Name;
	private ImageView team2Flag;
	private RelativeLayout ongoingLayout;
	
	private LinearLayout todaysLiveAndUpcomingList;
	private CompetitionPageTodayListAdapter listAdapter;
	
	private RelativeLayout favoriteTeamContainerLayout;
	private RelativeLayout favoriteTeamTitleLayoutBeforeLike;
	private ImageView favoriteTeamFlagBefore;
	private TextView favoriteTeamBeforeText;
	private ImageView favoriteTeamFlagAfter;
	private TextView favoriteTeamAfterText;
	private TextView favoriteTeamNameText;
	private RelativeLayout favoriteTeamTitleLayoutAfterLike;
	private LinearLayout favoriteTeamButtonContainerLayout;
	private Button favoriteTeamButtonDismiss;
	private Button favoriteTeamButtonLike;
	
	private EventCountDownTimer eventCountDownTimer;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) 
		{
			return;
		}
		
		setContentView(R.layout.layout_competition_page);
		
		Intent intent = getIntent();
		
		long competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);

		this.selectedTabIndex = intent.getIntExtra(Constants.INTENT_COMPETITION_SELECTED_TAB_INDEX, 0);
		
		this.competition = ContentManager.sharedInstance().getCacheManager().getCompetitionByID(competitionID);
		
		ContentManager.sharedInstance().getCacheManager().setSelectedCompetition(competition);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		
		initLayout();
		
		setAdapter(selectedTabIndex);
		
		int reloadIntervalInSecond = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionPageReloadInterval();
		
		setBackgroundLoadTimerValueInSeconds(reloadIntervalInSecond);
	}
	
		
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		if(eventCountDownTimer != null)
		{
			eventCountDownTimer.cancel();
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
		
		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				setData();
				
				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	
	
	private void setLikeBannerVisible()
	{
		String teamDisplayName = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_NAME;
		long teamId = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_ID;
		
		UserLike userLike = new UserLike(teamDisplayName, teamId);
		
		boolean isDismissed = SecondScreenApplication.sharedInstance().isFavoriteTeamBannerSeen();
		
		boolean isLiked = ContentManager.sharedInstance().getCacheManager().isContainedInUserLikes(userLike);
		
		if(isLiked)
		{
			favoriteTeamContainerLayout.setVisibility(View.VISIBLE);
			favoriteTeamTitleLayoutBeforeLike.setVisibility(View.GONE);
			favoriteTeamTitleLayoutAfterLike.setVisibility(View.VISIBLE);
			favoriteTeamButtonContainerLayout.setVisibility(View.GONE);

			ImageAware imageAware = new ImageViewAware(favoriteTeamFlagAfter, false);
			
			String teamFlagUrl = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_FLAG_URL;

			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(teamFlagUrl, imageAware);

			favoriteTeamNameText.setText(teamDisplayName);
			favoriteTeamAfterText.setText(getString(R.string.competition_page_favorite_team_title_after_like));

			favoriteTeamTitleLayoutAfterLike.setOnClickListener(this);
		}
		else
		{
			if(isDismissed)
			{
				favoriteTeamContainerLayout.setVisibility(View.GONE);
			}
			else
			{
				favoriteTeamContainerLayout.setVisibility(View.VISIBLE);
				favoriteTeamTitleLayoutBeforeLike.setVisibility(View.VISIBLE);
				favoriteTeamTitleLayoutAfterLike.setVisibility(View.GONE);
				favoriteTeamButtonContainerLayout.setVisibility(View.VISIBLE);
	
				ImageAware imageAware = new ImageViewAware(favoriteTeamFlagBefore, false);
	
				String teamFlagUrl = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_FLAG_URL;
	
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(teamFlagUrl, imageAware);
	
				favoriteTeamBeforeText.setText(getString(R.string.competition_page_favorite_team_title_before_like));
	
				favoriteTeamButtonDismiss.setOnClickListener(this);
				favoriteTeamButtonLike.setOnClickListener(this);
			}
		}
	}
	
	
	
	private void setLikeBannerNotVisible()
	{
		favoriteTeamContainerLayout.setVisibility(View.GONE);
	}
	
	
	
	private void setData()
	{
		String competitionName = competition.getDisplayName();
		
		boolean hasBegun = competition.hasBegun();
		boolean hasEnded = competition.hasEnded();
		boolean isOngoing = hasBegun && !hasEnded;
		
		/* Ongoing */
		if (isOngoing)
		{
			beforeLayout.setVisibility(View.GONE);
			countDownLayout.setVisibility(View.GONE);
			
			setOngoingLayoutForEventsToday();
			
			setLikeBannerVisible();
		}
		
		/* Has ended */
		else if (hasEnded)
		{
			countDownLayout.setVisibility(View.GONE);
			beforeLayout.setVisibility(View.GONE);
			todaysLiveAndUpcomingList.setVisibility(View.GONE);
			
			setLikeBannerNotVisible();
		}
		
		/* Not yet started */
		else 
		{
			beforeLayout.setVisibility(View.VISIBLE);
			countDownLayout.setVisibility(View.VISIBLE);
			todaysLiveAndUpcomingList.setVisibility(View.GONE);
			
			long competitionStartTimeInMiliseconds = competition.getBeginTimeCalendarGMT().getTimeInMillis();
			
			long millisecondsUntilEventStart = (competitionStartTimeInMiliseconds - DateUtils.getNowWithGMTTimeZone().getTimeInMillis());
			
			eventCountDownTimer = new EventCountDownTimer(
					competitionName, 
					millisecondsUntilEventStart, 
					remainingTimeInDays,
					remainingTimeInHours,
					remainingTimeInMinutes,
					remainingTimeInDaysTitle,
					remainingTimeInHoursTitle,
					remainingTimeInMinutesTitle,
					countdownTitle,
					countDownLayout);
			
			eventCountDownTimer.start();
			
			countDownLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
	            	TrackingGAManager.sharedInstance().sendUserCompetitionCountdownPressedEvent(competition.getDisplayName());
				}
			});
			
			setBeforeLayout();
			
			setLikeBannerVisible();
		}		
	}
	
	
	
	@SuppressWarnings("deprecation")
	private void setOngoingLayoutForEventsToday() 
	{
		List<Event> events = ContentManager.sharedInstance().getCacheManager().getAllLiveEventsForSelectedCompetition();
		
		/* ONLY FOR TESTING: If no live events we want to show the next upcoming instead */
//		if (events.isEmpty()) {
//			boolean filterFinishedEvents = true;
//			boolean filterLiveEvents = true;
//			
//			Event event = ContentManager.sharedInstance().getFromCacheNextUpcomingEventForSelectedCompetition(filterFinishedEvents, filterLiveEvents);
//			
//			events.add(event);
//			events.add(event);
//		}
		
		todaysLiveAndUpcomingList.removeAllViews();

		listAdapter = new CompetitionPageTodayListAdapter(this, events, competition.getDisplayName());
		
		for (int i = 0; i < listAdapter.getCount(); i++) 
		{
            View listItem = listAdapter.getView(i, null, todaysLiveAndUpcomingList);
           
            if (listItem != null) 
            {
            	todaysLiveAndUpcomingList.addView(listItem);
            }
        }
		
		todaysLiveAndUpcomingList.measure(0, 0);

		todaysLiveAndUpcomingList.setVisibility(View.VISIBLE);
		
		int sdk = android.os.Build.VERSION.SDK_INT;
		
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
		    ongoingLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.competition_backdrop_mundial));
		    
		} else {
			ongoingLayout.setBackground(getResources().getDrawable(R.drawable.competition_backdrop_mundial));
		}
	}
	
	
	
	private void setBeforeLayout()
	{
		String homeTeamName = event.getHomeTeam();
		
		String awayTeamName = event.getAwayTeam();
			
		boolean containsTeamInfo = event.containsTeamInfo();
			
		if(containsTeamInfo)
		{
			long team1ID = event.getHomeTeamId();
			
			Team team1 = ContentManager.sharedInstance().getCacheManager().getTeamById(team1ID);
			
			if(team1 != null)
			{
				ImageAware imageAware = new ImageViewAware(team1Flag, false);
					
				String team1FlagUrl = team1.getFlagImageURL();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team1FlagUrl, imageAware);
			}
			else
			{
				Log.w(TAG, "Team with id: " + team1ID + " not found in cache");
			}
			
			long team2ID = event.getAwayTeamId();
			
			Team team2 = ContentManager.sharedInstance().getCacheManager().getTeamById(team2ID);

			if(team2 != null)
			{
				ImageAware imageAware = new ImageViewAware(team2Flag, false);
					
				String team2FlagUrl = team2.getFlagImageURL();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithOptionsForTeamFlags(team2FlagUrl, imageAware);
			}
			else
			{
				Log.w(TAG, "Team with id: " + team2ID + " not found in cache");
			}
		}
		
		team1Name.setText(homeTeamName);
		
		team2Name.setText(awayTeamName);
		
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.competition_page_first_game))
			.append(" - ")
			.append(event.getEventTimeDayOfTheWeekAsString())
			.append(" ")
			.append(event.getEventTimeDayAndMonthAsString());
		
		nextGameText.setText(sb.toString());
		
		String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
		
		eventStartTime.setText(eventStartTimeHourAndMinuteAsString);
		
		StringBuilder channelsSB = new StringBuilder();
		
		boolean containsBroadcastDetails = event.containsBroadcastDetails();
		
		if(containsBroadcastDetails)
		{
			List<EventBroadcast> eventBroadcastDetailsList = event.getEventBroadcasts();
			
			int totalChannelCount = eventBroadcastDetailsList.size();
			
			List<String> channelNames = new ArrayList<String>(totalChannelCount);
			
			for(EventBroadcast eventBroadcastDetails : eventBroadcastDetailsList)
			{
				String channelID = eventBroadcastDetails.getChannelId();
				
				TVChannelId tvChannelId = new TVChannelId(channelID);
				
				TVChannel tvChannel = ContentManager.sharedInstance().getCacheManager().getTVChannelById(tvChannelId);
				
				if(tvChannel != null)
				{
					channelNames.add(tvChannel.getName());
				}
				else
				{
					Log.w(TAG, "No matching TVChannel ID was found for ID: " + channelID);
				}
			}
			
			for(int i=0; i<channelNames.size(); i++)
			{
				if(i >= Constants.MAXIMUM_CHANNELS_TO_SHOW_IN_COMPETITON)
				{
					int remainingChannels = totalChannelCount - Constants.MAXIMUM_CHANNELS_TO_SHOW_IN_COMPETITON;
							 
					channelsSB.append("+ ");
					channelsSB.append(remainingChannels);
					channelsSB.append(" ");
					channelsSB.append(getResources().getString(R.string.competition_page_more_channels_broadcasting));
					break;
				}
				
				channelsSB.append(channelNames.get(i));
				
				if(i != channelNames.size()-1)
				{
					channelsSB.append(", ");
				}
			}
		}
		
		String channels = channelsSB.toString();
		
		if (channels != null && !channels.isEmpty() && channels != "")
		{
			tvBroadcastChannels.setText(channels);
			tvBroadcastChannels.setVisibility(View.VISIBLE);			
		}
		else
		{
			tvBroadcastChannels.setVisibility(View.GONE);
		}
	}
	
	
	
	private void initLayout()
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		String competitionName = competition.getDisplayName();
		
		actionBar.setTitle(competitionName);
		
		/* Before */
		countDownLayout = (RelativeLayout) findViewById(R.id.competition_count_down_area);
		remainingTimeInDays = (TextView) findViewById(R.id.competition_days_number);
		remainingTimeInDaysTitle = (TextView) findViewById(R.id.competition_days_title);
		remainingTimeInHours = (TextView) findViewById(R.id.competition_hours_number);
		remainingTimeInHoursTitle = (TextView) findViewById(R.id.competition_hours_title);
		remainingTimeInMinutes = (TextView) findViewById(R.id.competition_minutes_number);
		remainingTimeInMinutesTitle = (TextView) findViewById(R.id.competition_minutes_title);
		countdownTitle = (TextView) findViewById(R.id.competition_title);
		
		/* Before */
		beforeLayout = (LinearLayout) findViewById(R.id.competition_page_before_block_container_layout);
		eventStartTime = (TextView) findViewById(R.id.competition_page_begin_time_broadcast);
		tvBroadcastChannels = (TextView) findViewById(R.id.competition_airing_channels_for_broadcast);
		team1Name = (TextView) findViewById(R.id.competition_team_one_name);
		team1Flag = (ImageView) findViewById(R.id.competition_team_one_flag);
		team2Name = (TextView) findViewById(R.id.competition_team_two_name);
		team2Flag = (ImageView) findViewById(R.id.competition_team_two_flag);
		
		/* Ongoing */
		ongoingLayout = (RelativeLayout) findViewById(R.id.competition_scrollable_layout);
		
		/* Favorite team banner */
		favoriteTeamContainerLayout = (RelativeLayout) findViewById(R.id.favorite_team_container_layout);
		favoriteTeamTitleLayoutBeforeLike = (RelativeLayout) findViewById(R.id.favorite_team_title_layout_before_like);
		favoriteTeamFlagBefore = (ImageView) findViewById(R.id.favorite_team_flag_before);
		favoriteTeamBeforeText = (TextView) findViewById(R.id.favorite_team_before_text);
		favoriteTeamTitleLayoutAfterLike = (RelativeLayout) findViewById(R.id.favorite_team_title_layout_after_like);
		favoriteTeamAfterText = (TextView) findViewById(R.id.favorite_team_after_text);
		favoriteTeamNameText = (TextView) findViewById(R.id.favorite_team_name_text);
		favoriteTeamFlagAfter = (ImageView) findViewById(R.id.favorite_team_flag_after);
		favoriteTeamButtonContainerLayout = (LinearLayout) findViewById(R.id.favorite_team_button_container_layout);
		favoriteTeamButtonDismiss = (Button) findViewById(R.id.favorite_team_button_dismiss);
		favoriteTeamButtonLike = (Button) findViewById(R.id.favorite_team_button_like);
		
		/* List of ongoing and upcoming events for today */
		todaysLiveAndUpcomingList = (LinearLayout) findViewById(R.id.todays_ongoing_events_list);
		
		pageTabIndicator = (TabPageIndicator) findViewById(R.id.tab_event_indicator);
		
		viewPager = (CustomViewPager) findViewById(R.id.tab_event_pager);
		
//		StickyScrollView scrollView = (StickyScrollView) findViewById(R.id.competition_scrollview);
	}
	
	
	
	private void setAdapter(int selectedIndex)
	{
		pagerAdapter = new CompetitionTabFragmentStatePagerAdapter(getSupportFragmentManager(), viewPager, competition.getCompetitionId());
	
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(selectedIndex);
		viewPager.setVisibility(View.VISIBLE);
		viewPager.setEnabled(false);

		pagerAdapter.notifyDataSetChanged();
		
		pageTabIndicator.setVisibility(View.VISIBLE);
		pageTabIndicator.setViewPager(viewPager);
		viewPager.setScreenHeight(GenericUtils.getScreenHeight(this));
		
		pagerAdapter.notifyDataSetChanged();
		pageTabIndicator.setCurrentItem(selectedIndex);
		
		pageTabIndicator.setInitialStyleOnAllTabs();
		pageTabIndicator.setStyleOnTabViewAtIndex(selectedIndex);
		pageTabIndicator.setOnPageChangeListener(this);
	}



	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);

		int reloadIntervalInMinutes = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCompetitionPageReloadInterval();
		
		boolean forceRefresh = wasActivityDataUpdatedMoreThan(reloadIntervalInMinutes);

		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, forceRefresh, competition.getCompetitionId());
	}
	

	
	@Override
	protected void loadDataInBackground()
	{
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, true, competition.getCompetitionId());
	}



	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasData = false;
		
		if (competition != null) 
		{
			hasData = ContentManager.sharedInstance().getCacheManager().containsCompetitionData(competition.getCompetitionId());
		}
		return hasData;
	}



	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			boolean filterFinishedEvents = false;
			boolean filterLiveEvents = false;
			event = ContentManager.sharedInstance().getCacheManager().getNextUpcomingEventForSelectedCompetition(filterFinishedEvents, filterLiveEvents);
	
			if(event == null)
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
			} 
			else
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
			}
		}
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}	
	}



	@Override
	public void onFetchDataProgress(int totalSteps, String message) {}



	@Override
	public void onPageScrollStateChanged(int arg0) {}



	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}



	@Override
	public void onPageSelected(int pos) 
	{
		selectedTabIndex = pos;
		
		String competitionName = null;
		if (competition != null) 
		{
			competitionName = competition.getDisplayName();
		}
		
		TrackingGAManager.sharedInstance().sendUserCompetitionTabPressedEvent(competitionName, pagerAdapter.getPageTitle(pos).toString());
	}
	
	
	
	@Override
	public void onClick(View v) 
	{
		super.onClick(v);
		
		int viewId = v.getId();

		switch (viewId) 
		{
			case R.id.favorite_team_button_like: 
			{
				setTeamLike();
				
				break;
			}
	
			case R.id.favorite_team_button_dismiss:
			{
				SecondScreenApplication.sharedInstance().setFavoriteTeamBannerAsSeen();
				
				String teamDisplayName = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_NAME;
				long teamId = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_ID;
				
				TrackingGAManager.sharedInstance().sendUserDismissedTeamBanner(teamDisplayName, teamId);
				
				setLikeBannerVisible();
				
				String message = getString(R.string.competition_page_favorite_team_dismiss_message);
				
				ToastHelper.createAndShowShortToast(message);
				
				break;
			}
	
			case R.id.favorite_team_title_layout_after_like:
			{
				long competitionId = Constants.FIFA_COMPETITION_ID;
				long teamId = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_ID;
				long phaseId = Constants.FAVORITE_TEAM_COLOMBIA_PHASE_ID;
				
				Intent intent = new Intent(CompetitionPageActivity.this, TeamPageActivity.class);
				
				intent.putExtra(Constants.INTENT_COMPETITION_ID, competitionId);
				intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, teamId);
				intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, phaseId);
				
				startActivity(intent);
				
				break;
			}
			
			default: 
			{
				Log.w(TAG, "Unknown onClick action for viewId: " + viewId);
			}
		}
	}
	
	
	
	private void setTeamLike()
	{
		String teamDisplayName = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_NAME;
		long teamId = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_ID;
		
		UserLike userLike = new UserLike(teamDisplayName, teamId);
		
		boolean isLoggedIn = ContentManager.sharedInstance().getCacheManager().isLoggedIn();
		
		if (isLoggedIn)
		{
			boolean isConnected = NetworkUtils.isConnected();
			
			if(isConnected == false)
			{
				ToastHelper.createAndShowNoInternetConnectionToast();
			}
			else
			{
				ContentManager.sharedInstance().addUserLike(this, userLike);

				TrackingGAManager.sharedInstance().sendUserLikesTeamInBanner(teamDisplayName, teamId);
				
				setLikeBannerVisible();
			}
		}
		else 
		{
			String message = getString(R.string.competition_page_favorite_team_register_popup_message);
			
			DialogHelper.showPromptSignInDialog(this, message, loginBeforeLikeProcedure(userLike), null);
		}
	}
	
	
	
	/* Sign in dialog */
	private Runnable loginBeforeLikeProcedure(final UserLike userLike) 
	{
		return new Runnable() 
		{
			public void run() 
			{
				/* We are not logged in, but we want the Like to be added after we log in, so set it
				 * After login is complete the ContentManager will perform the adding of the like to backend */
				ContentManager.sharedInstance().setLikeToAddAfterLogin(userLike);
				
				long competitionId = Constants.FIFA_COMPETITION_ID;
				long teamId = Constants.FAVORITE_TEAM_COLOMBIA_TEAM_ID;
				long phaseId = Constants.FAVORITE_TEAM_COLOMBIA_PHASE_ID;
				
				Intent intent = new Intent(CompetitionPageActivity.this, SignUpSelectionActivity.class);
				intent.putExtra(Constants.INTENT_EXTRA_ACTIVITY_TO_RETURN_AFTER_LOGIN, TeamPageActivity.class.getName());
				intent.putExtra(Constants.INTENT_COMPETITION_ID, competitionId);
				intent.putExtra(Constants.INTENT_COMPETITION_TEAM_ID, teamId);
				intent.putExtra(Constants.INTENT_COMPETITION_PHASE_ID, phaseId);
				
				startActivity(intent);
			}
		};
	}
}
