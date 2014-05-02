
package com.mitv.fragments;



import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.imbryk.viewPager.LoopViewPager;
import com.mitv.R;
import com.mitv.adapters.pager.EventTabFragmentStatePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.TVGuideTabTypeEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.utilities.GenericUtils;
import com.viewpagerindicator.TabPageIndicator;



public class TVGuideTabFragmentCompetition
	extends TVGuideTabFragment
	implements OnPageChangeListener
{
	private static final String TAG = TVGuideTabFragmentCompetition.class.getName();
	
	
	private static final int STARTING_TAB_INDEX = 0;
	
	
	private Competition competition;
	private Event event;
	
	private TabPageIndicator pageTabIndicator;
	private LoopViewPager viewPager;
	private EventTabFragmentStatePagerAdapter pagerAdapter;
	private OnViewPagerIndexChangedListener viewPagerIndexChangedListener;
	private int selectedTabIndex;
	
	
	
	
	
	public TVGuideTabFragmentCompetition(Competition competition)
	{
		super(competition.getCompetitionId(), competition.getDisplayName(), TVGuideTabTypeEnum.COMPETITION);

		this.competition = competition;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_competition_events_page, null);

		pageTabIndicator = (TabPageIndicator) rootView.findViewById(R.id.tab_event_indicator);
		
		viewPager = (LoopViewPager) rootView.findViewById(R.id.tab_event_pager);
		
		selectedTabIndex = STARTING_TAB_INDEX;
		
		super.initRequestCallbackLayouts(rootView);
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		return rootView;
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
		// TODO - Implement this
		return true;
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
				Log.d(TAG, "PROFILING: updateUI:SUCCEEDED_WITH_DATA");
					
				// TODO - Fill in the missing fields
				
				setTabs(selectedTabIndex);
				
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
	
	
	
	private void setTabs(int selectedIndex) 
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
