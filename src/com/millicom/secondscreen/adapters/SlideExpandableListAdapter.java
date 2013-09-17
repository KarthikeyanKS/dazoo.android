package com.millicom.secondscreen.adapters;

import com.millicom.secondscreen.R;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class SlideExpandableListAdapter extends AbstractSlideExpandableListAdapter {
	private int toggle_button_id;
	private int expandable_view_id;

	public SlideExpandableListAdapter(ListAdapter wrapped, int toggle_button_id, int expandable_view_id, ListView listView) {
		super(wrapped, listView);
		this.toggle_button_id = toggle_button_id;
		this.expandable_view_id = expandable_view_id;
	}

	public SlideExpandableListAdapter(ListAdapter wrapped, ListView listView) {
		this(wrapped, R.id.expandable_toggle_button, R.id.expandable, listView);
	}

	@Override
	public RelativeLayout getExpandToggleButton(View parent) {
		View view = parent.findViewById(toggle_button_id);
		
		if (!(view instanceof RelativeLayout)) {
			throw new IllegalArgumentException("Could not cast view with id "+toggle_button_id+" to a RelativeLayout");
		}
		
		return (RelativeLayout) view;
	}

	@Override
	public View getExpandableView(View parent) {
		return parent.findViewById(expandable_view_id);
	}
}
