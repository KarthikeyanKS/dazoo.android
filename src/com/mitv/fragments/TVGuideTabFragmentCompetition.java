
package com.mitv.fragments;



import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.imbryk.viewPager.LoopViewPager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.pager.EventTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.TVGuideTabTypeEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.gson.mitvapi.competitions.EventBroadcastDetailsJSON;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.elements.EventCountDownTimer;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.viewpagerindicator.TabPageIndicator;



public class TVGuideTabFragmentCompetition
	extends TVGuideTabFragment
	implements OnPageChangeListener
{
	private static final String TAG = TVGuideTabFragmentCompetition.class.getName();
	
	
	private static final int MAXIMUM_CHANNELS_TO_SHOW = 2;
	private static final int STARTING_TAB_INDEX = 0;
	
	
	private Competition competition;
	private Event event;
	
	private TabPageIndicator pageTabIndicator;
	private LoopViewPager viewPager;
	private EventTabFragmentStatePagerAdapter pagerAdapter;
	private OnViewPagerIndexChangedListener viewPagerIndexChangedListener;
	private int selectedTabIndex;
	
	private TextView remainingTimeInDays;
	private TextView remainingTimeInHours;
	private TextView remainingTimeInMinutes;
	private TextView eventStartTime;
	private TextView tvBroadcastChannels;
	private TextView team1Name;
	private ImageView team1Flag;
	private TextView team2Name;
	private ImageView team2Flag;
	
	private EventCountDownTimer eventCountDownTimer;
	
	
	
	
	public TVGuideTabFragmentCompetition(Competition competition)
	{
		super(competition.getCompetitionId(), competition.getDisplayName(), TVGuideTabTypeEnum.COMPETITION);

		this.competition = competition;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_events_page, null);

		initLayout();
		
		pageTabIndicator = (TabPageIndicator) rootView.findViewById(R.id.tab_event_indicator);
		
		viewPager = (LoopViewPager) rootView.findViewById(R.id.tab_event_pager);
		
		selectedTabIndex = STARTING_TAB_INDEX;
		
		if(!SecondScreenApplication.isAppRestarting()) 
		{
			setAdapter(selectedTabIndex);
		}
		
		super.initRequestCallbackLayouts(rootView);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
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
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);

		String loadingMessage = String.format(GenericUtils.getCurrentLocale(activity), "%s %s", getString(R.string.loading_message_tag), getTabTitle());
		
		setLoadingLayoutDetailsMessage(loadingMessage);
		
		String competitionID = competition.getCompetitionId();
		
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, false, competitionID);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		String competitionID = competition.getCompetitionId();
		
		return ContentManager.sharedInstance().getFromCacheHasCompetitionData(competitionID);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
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
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				long eventStartTimeInMiliseconds = event.getStartTimeCalendarLocal().getTimeInMillis();
				
				long millisecondsUntilEventStart = (eventStartTimeInMiliseconds - DateUtils.getNow().getTimeInMillis());
				
				eventCountDownTimer = new EventCountDownTimer(millisecondsUntilEventStart, remainingTimeInDays, remainingTimeInHours, remainingTimeInMinutes);
				
				eventCountDownTimer.start();
				
				String team1ID = event.getTeam1Id();
				
				Team team1 = ContentManager.sharedInstance().getFromCacheTeamByID(team1ID);
				
				team1Name.setText(team1.getDisplayName());
				
				String team2ID = event.getTeam2Id();
				
				Team team2 = ContentManager.sharedInstance().getFromCacheTeamByID(team2ID);
				
				team2Name.setText(team2.getDisplayName());
				
				boolean isLocalFlagDrawableResourceAvailableForTeam1 = team1.isLocalFlagDrawableResourceAvailable();
				
				if(isLocalFlagDrawableResourceAvailableForTeam1)
				{
					team1Flag.setImageDrawable(team1.getLocalFlagDrawableResource());
				}
				else
				{
					ImageAware imageAware = new ImageViewAware(team1Flag, false);
					
					String team1FlagUrl = team1.getImages().getFlag().getImageURLForDeviceDensityDPI();
					
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(team1FlagUrl, imageAware);
				}
				
				boolean isLocalFlagDrawableResourceAvailableForTeam2 = team2.isLocalFlagDrawableResourceAvailable();
				
				if(isLocalFlagDrawableResourceAvailableForTeam2)
				{
					team2Flag.setImageDrawable(team2.getLocalFlagDrawableResource());
				}
				else
				{
					ImageAware imageAware = new ImageViewAware(team2Flag, false);
					
					String team2FlagUrl = team2.getImages().getFlag().getImageURLForDeviceDensityDPI();
					
					SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(team2FlagUrl, imageAware);
				}
				
				String eventStartTimeHourAndMinuteAsString = DateUtils.getHourAndMinuteCompositionAsString(event.getStartTimeCalendarLocal());
				
				eventStartTime.setText(eventStartTimeHourAndMinuteAsString);
				
				StringBuilder channelsSB = new StringBuilder();
						
				List<EventBroadcastDetailsJSON> eventBroadcastDetailsList = event.getBroadcastDetails();
				
				int totalChannelCount = eventBroadcastDetailsList.size();
				
				List<String> channelNames = new ArrayList<String>(totalChannelCount);
				
				for(EventBroadcastDetailsJSON eventBroadcastDetails : eventBroadcastDetailsList)
				{
					String channelID = eventBroadcastDetails.getChannelId();
					
					TVChannelId tvChannelId = new TVChannelId(channelID);
					
					TVChannel tvChannel = ContentManager.sharedInstance().getFromCacheTVChannelById(tvChannelId);
					
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
					if(i >= MAXIMUM_CHANNELS_TO_SHOW)
					{
						int remainingChannels = totalChannelCount-MAXIMUM_CHANNELS_TO_SHOW;
								
						channelsSB.append("+ ");
						channelsSB.append(remainingChannels);
						channelsSB.append(" ");
						channelsSB.append(activity.getString(R.string.competition_page_more_channels_broadcasting));
						break;
					}
					
					channelsSB.append(channelNames.get(i));
					
					if(i != channelNames.size()-1)
					{
						channelsSB.append(", ");
					}
				}

				tvBroadcastChannels.setText(channelsSB);

				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	
	
	@Override
	public void onTimeChange(int hour)
	{}
	
	
	
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
			
			TrackingGAManager.sharedInstance().sendUserTagSelectionEvent(pos);
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
	
	
	
	private void initLayout()
	{
		remainingTimeInDays = (TextView) rootView.findViewById(R.id.competition_page_time_left_to_fifa_days);
		remainingTimeInHours = (TextView) rootView.findViewById(R.id.competition_page_time_left_to_fifa_hours);
		remainingTimeInMinutes = (TextView) rootView.findViewById(R.id.competition_page_time_left_to_fifa_minutes);
		eventStartTime = (TextView) rootView.findViewById(R.id.competition_page_begin_time_broadcast);
		tvBroadcastChannels = (TextView) rootView.findViewById(R.id.competition_airing_channels_for_broadcast);
		team1Name = (TextView) rootView.findViewById(R.id.competition_team_one_name);
		team1Flag = (ImageView) rootView.findViewById(R.id.competition_team_one_flag);
		team2Name = (TextView) rootView.findViewById(R.id.competition_team_two_name);
		team2Flag = (ImageView) rootView.findViewById(R.id.competition_team_two_flag);
	}
	
	
	
	private void setAdapter(int selectedIndex) 
	{
		pagerAdapter = new EventTabFragmentStatePagerAdapter(getChildFragmentManager());

		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(1);
		viewPager.setBoundaryCaching(true);
		viewPager.setCurrentItem(selectedIndex);
		viewPager.setVisibility(View.VISIBLE);
		viewPager.setEnabled(false);

		pagerAdapter.notifyDataSetChanged();

		pageTabIndicator.setVisibility(View.VISIBLE);
		pageTabIndicator.setViewPager(viewPager);
		pageTabIndicator.notifyDataSetChanged();
		pageTabIndicator.setCurrentItem(selectedIndex);
		pageTabIndicator.setOnPageChangeListener(this);
		
		pageTabIndicator.setInitialStyleOnAllTabs();
		pageTabIndicator.setStyleOnTabViewAtIndex(selectedIndex);
	}
}
