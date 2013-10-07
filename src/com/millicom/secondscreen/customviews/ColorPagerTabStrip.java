package com.millicom.secondscreen.customviews;

import com.millicom.secondscreen.R;
import android.content.Context;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;

public class ColorPagerTabStrip extends PagerTabStrip {
	
	private static final String TAG = "ColorPagerTabStrip";
	
	public ColorPagerTabStrip(Context context, AttributeSet attrs){
		super(context, attrs);
		
		//color for the indicator
		setTabIndicatorColor(context.getResources().getColor(R.color.lightblue));
		
		//line
		setDrawFullUnderline(true);
	}

}
