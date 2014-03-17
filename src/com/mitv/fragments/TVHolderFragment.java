
package com.mitv.fragments;



import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imbryk.viewPager.LoopViewPager;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.listadapters.TagTypeFragmentStatePagerAdapter;
import com.mitv.models.TVDate;
import com.mitv.models.TVTag;
import com.viewpagerindicator.TabPageIndicator;



public class TVHolderFragment 
	extends Fragment 
	implements OnPageChangeListener
{
	@SuppressWarnings("unused")
	private static final String TAG = TVHolderFragment.class.getName();
	
	private static final String BUNDLE_INFO_STARTING_INDEX = "BUNDLE_INFO_STARTING_INDEX";

	private static OnViewPagerIndexChangedListener viewPagerIndexChangedListener;
	
	private TabPageIndicator pageTabIndicator;
	private LoopViewPager viewPager;
	
	private TagTypeFragmentStatePagerAdapter pagerAdapter;
	private int selectedTabIndex = 0;
	
	private List<TVTag> tvTags;
	
	
	
	public interface OnViewPagerIndexChangedListener 
	{
		public void onIndexSelected(int position);
	}

	
	
	public static TVHolderFragment newInstance(int startingIndex, OnViewPagerIndexChangedListener listener)
	{
		viewPagerIndexChangedListener = listener;

		TVHolderFragment fragment = new TVHolderFragment();
		
		Bundle bundle = new Bundle();
		
		bundle.putInt(BUNDLE_INFO_STARTING_INDEX, startingIndex);

		fragment.setArguments(bundle);

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
		
		tvTags = ContentManager.sharedInstance().getFromCacheTVTags();

		viewPager = (LoopViewPager) v.findViewById(R.id.home_pager);
		
		pageTabIndicator = (TabPageIndicator) v.findViewById(R.id.home_indicator);
		
		setAdapter(selectedTabIndex);

		return v;
	}
	
	
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

	
	
	@Override
	public void onDetach() 
	{
		super.onDetach();
	}

	
	
	@Override
	public void onPageSelected(int pos) 
	{
		selectedTabIndex = pos;
		
		viewPagerIndexChangedListener.onIndexSelected(selectedTabIndex);
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
}