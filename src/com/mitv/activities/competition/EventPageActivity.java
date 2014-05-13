
package com.mitv.activities.competition;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.imbryk.viewPager.LoopViewPager;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.adapters.list.CompetitionEventsByGroupListAdapter;
import com.mitv.adapters.pager.EventAndLineUpTabFragmentStatePagerAdapter;
import com.mitv.adapters.pager.EventTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.gson.mitvapi.competitions.EventBroadcastDetailsJSON;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Phase;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.utilities.DateUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.viewpagerindicator.TabPageIndicator;



public class EventPageActivity 
	extends BaseContentActivity
	implements OnPageChangeListener, ViewCallbackListener, FetchDataProgressCallbackListener 
{
	private static final String TAG = EventPageActivity.class.getName();
	
	
	private static final int STARTING_TAB_INDEX = 0;
	

	private Event event;
	private Competition competition;
	
	private TabPageIndicator pageTabIndicator;
	private LoopViewPager viewPager;
	private EventAndLineUpTabFragmentStatePagerAdapter pagerAdapter;
	private OnViewPagerIndexChangedListener viewPagerIndexChangedListener;
	private int selectedTabIndex;

	private LinearLayout listView;
	private CompetitionEventsByGroupListAdapter listAdapter;
	
	private TextView team1Name;
	private ImageView team1Flag;
	private TextView team2Name;
	private ImageView team2Flag;
	private RelativeLayout nextGameLayout;
	private TextView groupHeader;
	private TextView liveStandings;
	private TextView liveTimeInGame;
	private LinearLayout broadcastListView;
	private TextView likeIcon;
	private TextView shareIcon;
	private TextView beginTime;
	private TextView beginTimeDate;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if (super.isRestartNeeded())
		{
			return;
		}
		
		setContentView(R.layout.layout_competition_event_page);
		
		Intent intent = getIntent();
		
		long eventID = intent.getLongExtra(Constants.INTENT_COMPETITION_EVENT_ID, 0);
		long competitionID = intent.getLongExtra(Constants.INTENT_COMPETITION_ID, 0);
		
		event = ContentManager.sharedInstance().getFromCacheEventByIDForSelectedCompetition(eventID);
		
		competition = ContentManager.sharedInstance().getFromCacheCompetitionByID(competitionID);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		
		initLayout();
		
//		setAdapter(selectedTabIndex);
	}
		
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
		
		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				setListView();
				
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
	
	
	
	
	/* Tab elements */
	
	public interface OnViewPagerIndexChangedListener 
	{
		public void onIndexSelected(int position);
	}
	
	
	
	@Override
	public void onPageSelected(int pos) 
	{
		selectedTabIndex = pos;
		
		if(viewPagerIndexChangedListener != null)
		{
			viewPagerIndexChangedListener.onIndexSelected(selectedTabIndex);
		}
		else
		{
			Log.w(TAG, "The ViewPagerIndexChangedListener is null");
		}
	}
	
	
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2){}
	
	
	
	@Override
	public void onPageScrollStateChanged(int arg0){}
	
	
	
	public OnViewPagerIndexChangedListener getViewPagerIndexChangedListener() 
	{
		return viewPagerIndexChangedListener;
	}
	
	
	
	public void setViewPagerIndexChangedListener(OnViewPagerIndexChangedListener viewPagerIndexChangedListener) 
	{
		this.viewPagerIndexChangedListener = viewPagerIndexChangedListener;
	}
	
	
	
	public int getSelectedTabIndex() 
	{
		return selectedTabIndex;
	}
	
	
	
	public void setSelectedTabIndex(int selectedTabIndex) 
	{
		this.selectedTabIndex = selectedTabIndex;
	}
	
	
	
	private void setData()
	{
		String homeTeamName = event.getHomeTeam();
			
		String awayTeamName = event.getAwayTeam();
			
		boolean containsTeamInfo = event.containsTeamInfo();
			
		if(containsTeamInfo)
		{	
			long team1ID = event.getHomeTeamId();
			
			Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
			
			if(team1 != null)
			{
				Log.w(TAG, "Local flag for team: " + team1.getNationCode() + " not found in cache");
					
				ImageAware imageAware = new ImageViewAware(team1Flag, false);
					
				String team1FlagUrl = team1.getImages().getFlag().getImageURLForDeviceDensityDPI();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team1FlagUrl, imageAware);
			}
			else
			{
				Log.w(TAG, "Team with id: " + team1ID + " not found in cache");
			}
			
			long team2ID = event.getAwayTeamId();
			
			Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);

			if(team2 != null)
			{
				ImageAware imageAware = new ImageViewAware(team2Flag, false);
					
				String team2FlagUrl = team2.getImages().getFlag().getImageURLForDeviceDensityDPI();
					
				SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithCompetitionOptions(team2FlagUrl, imageAware);
			}
			else
			{
				Log.w(TAG, "Team with id: " + team2ID + " not found in cache");
			}
		}
		
		team1Name.setText(homeTeamName);
		
		team2Name.setText(awayTeamName);
		
		/* Group name */
		
		long phaseID = event.getPhaseId();
		
		Phase phase = ContentManager.sharedInstance().getFromCachePhaseByIDForSelectedCompetition(phaseID);
		
		String groupHeaderName = phase.getPhase();
		
		groupHeader.setText(groupHeaderName);
		
		StringBuilder sb = new StringBuilder();
		
		/* The event is ongoing */
		if (event.isOngoing() && !event.isPostponed()) {
			
			sb.append(event.getAwayGoals())
				.append(" - ")
				.append(event.getHomeGoals());
			
			liveStandings.setText(sb.toString());
			
			Calendar beginTimeCal = event.getEventDateCalendarLocal();
			
			// TODO Check if this method works....
			int minutesInGame = event.countMinutesInGame(beginTimeCal);
			
			liveTimeInGame.setText(minutesInGame + "'");
			
			liveStandings.setVisibility(View.VISIBLE);
			liveTimeInGame.setVisibility(View.VISIBLE);
			beginTime.setVisibility(View.GONE);
			beginTimeDate.setVisibility(View.GONE);
		}
		
		/* The event has not started yet */
		else {
			String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getEventDateCalendarLocal());
			
			beginTime.setText(eventStartTimeHourAndMinuteAsString);
			
			sb.append(event.getEventTimeDayOfTheWeekAsString())
			.append(" ")
			.append(event.getEventTimeDayAndMonthAsString());
			
			beginTimeDate.setText(sb.toString());
			
			liveStandings.setVisibility(View.GONE);
			liveTimeInGame.setVisibility(View.GONE);
			beginTime.setVisibility(View.VISIBLE);
			beginTimeDate.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	private void initLayout()
	{
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		StringBuilder eventName = new StringBuilder();
		eventName.append(event.getHomeTeam())
		.append(" : ")
		.append(event.getAwayTeam());
		
		actionBar.setTitle(eventName);
		
		team1Name = (TextView) findViewById(R.id.competition_team_one_name);
		team1Flag = (ImageView) findViewById(R.id.competition_team_one_flag);
		team2Name = (TextView) findViewById(R.id.competition_team_two_name);
		team2Flag = (ImageView) findViewById(R.id.competition_team_two_flag);
		nextGameLayout = (RelativeLayout) findViewById(R.id.competition_next_game_layout);
		groupHeader = (TextView) findViewById(R.id.competition_event_group_header);
		liveStandings = (TextView) findViewById(R.id.competition_event_live_standing);
		liveTimeInGame = (TextView) findViewById(R.id.competition_event_live_time);
		broadcastListView = (LinearLayout) findViewById(R.id.competition_event_broadcasts_listview);
		likeIcon = (TextView) findViewById(R.id.competition_element_social_buttons_like_view);
		shareIcon = (TextView) findViewById(R.id.competition_element_social_buttons_share_button_iv);
		beginTime = (TextView) findViewById(R.id.competition_event_starttime_time);
		beginTimeDate = (TextView) findViewById(R.id.competition_event_starttime_date);
		
		pageTabIndicator = (TabPageIndicator) findViewById(R.id.tab_event_indicator);
		
		viewPager = (LoopViewPager) findViewById(R.id.tab_event_pager);
		
		selectedTabIndex = STARTING_TAB_INDEX;
	}
	
	
	
	private void setAdapter(int selectedIndex) 
	{
		pagerAdapter = new EventAndLineUpTabFragmentStatePagerAdapter(getSupportFragmentManager());
	
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(1);
		viewPager.setBoundaryCaching(true);
		viewPager.setCurrentItem(selectedIndex);
		viewPager.setVisibility(View.VISIBLE);
		viewPager.setEnabled(false);

		pageTabIndicator.setVisibility(View.VISIBLE);
		pageTabIndicator.setViewPager(viewPager);
		
		pageTabIndicator.setCurrentItem(selectedIndex);
		pageTabIndicator.setOnPageChangeListener(this);
		
		pagerAdapter.notifyDataSetChanged();
		
		pageTabIndicator.setInitialStyleOnAllTabs();
		pageTabIndicator.setStyleOnTabViewAtIndex(selectedIndex);
	}



	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		//ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, false, competition.getCompetitionId());
	}



	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasData = ContentManager.sharedInstance().getFromCacheHasEventData(event.getCompetitionId(), event.getEventId());
		
		return hasData;
	}



	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			event = ContentManager.sharedInstance().getFromCacheNextUpcomingEventForSelectedCompetition();
	
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
	
	
	
	private void setListView() 
	{
		broadcastListView.removeAllViews();
		
		Map<Long, List<Event>> eventsByGroups = ContentManager.sharedInstance().getFromCacheAllEventsGroupedByGroupStageForSelectedCompetition();

//		listAdapter = new CompetitionEventsByGroupListAdapter(this, eventsByGroups);
		
		for (int i = 0; i < listAdapter.getCount(); i++) 
		{
            View listItem = listAdapter.getView(i, null, broadcastListView);
           
            if (listItem != null) 
            {
            	broadcastListView.addView(listItem);
            }
        }
		
		broadcastListView.measure(0, 0);
		
		CompetitionPageActivity.viewPager.heightsMap.put(1, broadcastListView.getMeasuredHeight());
		
		CompetitionPageActivity.viewPager.onPageScrolled(1, 0, 0); //TODO: Ugly solution to viewpager not updating height on first load.
	}



	@Override
	public void onFetchDataProgress(int totalSteps, String message) {}
}
