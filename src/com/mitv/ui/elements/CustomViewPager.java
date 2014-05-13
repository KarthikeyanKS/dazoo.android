package com.mitv.ui.elements;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.imbryk.viewPager.LoopViewPager;
import com.mitv.R;

public class CustomViewPager extends LoopViewPager {

	public HashMap<Integer, Integer> heightsMap;
	int screenHeight;

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		heightsMap = new HashMap<Integer, Integer>();
	}

	/*Used to pass length. Can use GenericUtils instead */
	public void setScreenHeight(int height) {
		screenHeight = height;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) this.getLayoutParams();
		int newPos = arg0;
		//Find out which page is scrolled to
		if (arg1 > 0) {
			newPos++;
		}

		//Get status bar height
		int statusBarHeight = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = getResources().getDimensionPixelSize(resourceId);
		}

		//Get actionbar height
		TypedValue tv = new TypedValue();
		getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
		int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

		//Get indicator height, here defined in dimensions
		int indicatorHeight = (int) getResources().getDimension(R.dimen.indicator_height);

		//Max size viewpager can use
		int heightToWorkWith = (int) ((screenHeight - statusBarHeight - actionBarHeight - indicatorHeight));

		//If shorter than size to work with, use full length. Its to reduce flickering when changing tab.
		if (heightsMap.get(newPos) != null) {
			if (heightToWorkWith > heightsMap.get(newPos)) {
				params.height = heightToWorkWith;
			}
			//Otherwise use the length of the list.
			else {
				params.height = heightsMap.get(newPos);
			}
		}
		else {
			params.height = heightToWorkWith;
		}

		setLayoutParams(params);

		super.onPageScrolled(arg0, arg1, arg2);
	}
}
