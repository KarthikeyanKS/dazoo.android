package com.millicom.secondscreen.customviews;

import com.millicom.secondscreen.adapters.SlideExpandableListAdapter;

import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.content.Context;

/**
 * Simple subclass of ListView which does nothing more than wrap
 * any ListAdapter in a SlideExpandalbeListAdapter
 */
class SlideExpandableListView extends ListView {

	public SlideExpandableListView(Context context) {
		super(context);
	}

	public SlideExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setAdapter(ListAdapter adapter, ListView listView) {
		super.setAdapter(new SlideExpandableListAdapter(adapter, listView));
	}

}