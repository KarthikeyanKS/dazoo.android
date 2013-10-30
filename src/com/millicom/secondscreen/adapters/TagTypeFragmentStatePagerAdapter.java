package com.millicom.secondscreen.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.imbryk.viewPager.LoopViewPager;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.tvguide.TVGuideTagFragment;
import com.millicom.secondscreen.content.tvguide.TVGuideTagTypeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public abstract class TagTypeFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

	private static final String						TAG					= "CategoryFragmentPagerAdapter";

	private SparseArray<WeakReference<Fragment>>	mPageReferenceMap	= new SparseArray<WeakReference<Fragment>>();
	private ArrayList<String>						mTabTitles;

	public abstract Fragment initFragment(int position);

	public TagTypeFragmentStatePagerAdapter(FragmentManager fm, ArrayList<String> tabTitles) {
		super(fm);
		mTabTitles = tabTitles;
	}

	@Override
	public Object instantiateItem(ViewGroup viewGroup, int position) {
		mPageReferenceMap.put(Integer.valueOf(position), new WeakReference<Fragment>(initFragment(position)));
		return super.instantiateItem(viewGroup, position);
	}

	@Override
	public int getCount() {
		return mTabTitles.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		//return mTabTitles.get(position);
		return mTabTitles.get(position % mTabTitles.size());
	}

	@Override
	public Fragment getItem(int position) {
		position = LoopViewPager.toRealPosition(position, getCount());
		return getFragment(position);
		//return getFragment(pos);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		mPageReferenceMap.remove(Integer.valueOf(position));
	}

	public Fragment getFragment(int key) {

		WeakReference<Fragment> weakReference = mPageReferenceMap.get(key);

		if (null != weakReference) {

			return (Fragment) weakReference.get();

		} else {
			return null;
		}
	}

	@Override
	public int getItemPosition(Object object) {

		// Have to return this, otherwise notifyDatasetChanged(); won't have any effect
		return POSITION_NONE;
	}
}
