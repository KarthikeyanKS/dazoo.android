
package com.mitv.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.imbryk.viewPager.LoopViewPager;
import com.mitv.R;
import com.mitv.adapters.pager.TVGuideTagFragmentStatePagerAdapter;
import com.mitv.managers.TrackingGAManager;
import com.viewpagerindicator.TabPageIndicator;



public class TVGuideHolderFragment 
	extends Fragment 
	implements OnPageChangeListener
{
	private static final String TAG = TVGuideHolderFragment.class.getName();
	
	private static final String BUNDLE_INFO_STARTING_INDEX = "BUNDLE_INFO_STARTING_INDEX";
	private static final int STARTING_TAB_INDEX = 0;

	private TabPageIndicator pageTabIndicator;
	private LoopViewPager viewPager;
	
	private TVGuideTagFragmentStatePagerAdapter pagerAdapter;
	
	private OnViewPagerIndexChangedListener viewPagerIndexChangedListener;
	private int selectedTabIndex;

	
	
	public interface OnViewPagerIndexChangedListener 
	{
		public void onIndexSelected(int position);
	}

	
	
	public static TVGuideHolderFragment newInstance(
			final int startingIndex, 
			final OnViewPagerIndexChangedListener listener)
	{
		TVGuideHolderFragment fragment = new TVGuideHolderFragment();
		
		Bundle bundle = new Bundle();
		
		bundle.putInt(BUNDLE_INFO_STARTING_INDEX, startingIndex);

		fragment.setArguments(bundle);

		fragment.setViewPagerIndexChangedListener(listener);
		
		fragment.setSelectedTabIndex(STARTING_TAB_INDEX);
		
		return fragment;
	}

	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		
		selectedTabIndex = bundle.getInt(BUNDLE_INFO_STARTING_INDEX);
	}
	


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{		
		View v = inflater.inflate(R.layout.fragment_tvguide_holder_layout, null);
		
		viewPager = (LoopViewPager) v.findViewById(R.id.home_pager);
		
		pageTabIndicator = (TabPageIndicator) v.findViewById(R.id.home_indicator);

		setAdapter(selectedTabIndex);
		
		return v;
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

	
	
	private void setAdapter(int selectedIndex) 
	{
		pagerAdapter = new TVGuideTagFragmentStatePagerAdapter(getChildFragmentManager());

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
}