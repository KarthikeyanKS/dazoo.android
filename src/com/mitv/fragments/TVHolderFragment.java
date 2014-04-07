
package com.mitv.fragments;



import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imbryk.viewPager.LoopViewPager;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.listadapters.TagTypeFragmentStatePagerAdapter;
import com.mitv.models.TVDate;
import com.mitv.models.TVTag;
import com.viewpagerindicator.TabPageIndicator;



public class TVHolderFragment 
	extends Fragment 
	implements OnPageChangeListener
{
	private static final String TAG = TVHolderFragment.class.getName();
	
	private static final String BUNDLE_INFO_STARTING_INDEX = "BUNDLE_INFO_STARTING_INDEX";

	private TabPageIndicator pageTabIndicator;
	private LoopViewPager viewPager;
	
	private TagTypeFragmentStatePagerAdapter pagerAdapter;
	
	private OnViewPagerIndexChangedListener viewPagerIndexChangedListener;
	private int selectedTabIndex;

	
	
	public interface OnViewPagerIndexChangedListener 
	{
		public void onIndexSelected(int position);
	}

	
	
	public static TVHolderFragment newInstance(
			final int startingIndex, 
			final OnViewPagerIndexChangedListener listener)
	{
		TVHolderFragment fragment = new TVHolderFragment();
		
		Bundle bundle = new Bundle();
		
		bundle.putInt(BUNDLE_INFO_STARTING_INDEX, startingIndex);

		fragment.setArguments(bundle);

		fragment.setViewPagerIndexChangedListener(listener);
		fragment.setSelectedTabIndex(0);
		
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

		if(!SecondScreenApplication.isAppRestarting()) 
		{
			setAdapter(selectedTabIndex);
		}
		
		return v;
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
	public void onPageScrolled(int arg0, float arg1, int arg2) 
	{}

	
	
	@Override
	public void onPageScrollStateChanged(int arg0)
	{}

	
	
	private void setAdapter(int selectedIndex) 
	{
		TVDate tvDate = ContentManager.sharedInstance().getFromCacheTVDateSelected();
		List<TVTag> tvTags = ContentManager.sharedInstance().getFromCacheTVTags();

		pagerAdapter = new TagTypeFragmentStatePagerAdapter(getChildFragmentManager(), tvTags, tvDate);

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
	}



	public OnViewPagerIndexChangedListener getViewPagerIndexChangedListener() {
		return viewPagerIndexChangedListener;
	}



	public void setViewPagerIndexChangedListener(
			OnViewPagerIndexChangedListener viewPagerIndexChangedListener) {
		this.viewPagerIndexChangedListener = viewPagerIndexChangedListener;
	}



	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}



	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}
}