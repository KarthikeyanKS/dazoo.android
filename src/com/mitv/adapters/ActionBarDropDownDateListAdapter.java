package com.mitv.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.millicom.mitv.models.TVDate;
import com.mitv.R;

public class ActionBarDropDownDateListAdapter extends BaseAdapter implements SpinnerAdapter {

	private static final String TAG = "ActionBarDropDownDateListAdapter";
	private ArrayList<TVDate> mDays;

	private int mSelectedIndex = -1;

	public ActionBarDropDownDateListAdapter(ArrayList<TVDate> mDays) {
		this.mDays = mDays;
	}

	@Override
	public int getCount() {
		if (mDays != null) {
			return mDays.size();
		} else
			return 0;
	}

	@Override
	public TVDate getItem(int position) {
		if (mDays != null) {
			return mDays.get(position);
		} else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getViewForHeaderOrRow(position, convertView, parent, true);
	}

	private View getViewForHeaderOrRow(int position, View convertView, ViewGroup parent, boolean isHeader) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (isHeader) {
				row = inflater.inflate(R.layout.actionbar_dropdown_list_date_header, parent, false);
			} else {
				row = inflater.inflate(R.layout.actionbar_dropdown_list_date_item, parent, false);
			}
		}

		TextView dayName;
		TextView dayAndMonth;

		if (isHeader) {
			dayName = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_header_name);
			dayAndMonth = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_header_number);
		} else {
			dayName = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_item_name);
			dayAndMonth = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_item_number);

			// make the selected text position bold
			if (position == mSelectedIndex) {
				dayName.setTypeface(null, Typeface.BOLD);
			}
		}

		// do not display when no selection
		if (mSelectedIndex != -1) {
			TVDate tvDate = getItem(position);
			try {
				dayName.setText(tvDate.getDisplayName());
				// TODO NewArc use calendar from TVDate object as sketch below
				// /Calendar calendar = tvDate.getCalendar();
				// txtNumber.setText(DateUtils.buildDayAndMonthCompositionAsString(calendar);
			} catch (Exception e) {
				e.printStackTrace();
				dayName.setText("");
				dayAndMonth.setText("");
			}

			dayName.setVisibility(View.VISIBLE);
			dayAndMonth.setVisibility(View.VISIBLE);

		} else {
			dayName.setVisibility(View.GONE);
			dayAndMonth.setVisibility(View.GONE);
		}

		return row;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getViewForHeaderOrRow(position, convertView, parent, false);
	}

	public void setSelectedIndex(int index) {
		this.mSelectedIndex = index;
	}
}
