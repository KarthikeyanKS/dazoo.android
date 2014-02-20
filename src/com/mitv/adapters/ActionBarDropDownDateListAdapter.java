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

import com.mitv.R;
import com.mitv.model.OldTVDate;
import com.mitv.utilities.OldDateUtilities;

public class ActionBarDropDownDateListAdapter extends BaseAdapter implements SpinnerAdapter {

	private static final String	TAG				= "ActionBarDropDownDateListAdapter";
	private ArrayList<OldTVDate>	mDays;

	private int					mSelectedIndex	= -1;

	public ActionBarDropDownDateListAdapter(ArrayList<OldTVDate> mDays) {
		this.mDays = mDays;
	}

	@Override
	public int getCount() {
		if (mDays != null) {
			return mDays.size();
		} else return 0;
	}

	@Override
	public OldTVDate getItem(int position) {
		if (mDays != null) {
			return mDays.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.actionbar_dropdown_list_date_header, parent, false);
		}

		TextView txtName = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_header_name);
		TextView txtNumber = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_header_number);

		// do not display when no selection
		if (mSelectedIndex != -1) {

			OldTVDate tvDate = getItem(position);
			try {
				txtName.setText(tvDate.getName());
				txtNumber.setText(OldDateUtilities.tvDateStringToDatePickerString(tvDate.getDate()));
			} catch (Exception e) {
				e.printStackTrace();
				txtName.setText("");
				txtNumber.setText("");
			}

			txtName.setVisibility(View.VISIBLE);
			txtNumber.setVisibility(View.VISIBLE);

		} else {
			txtName.setVisibility(View.GONE);
			txtNumber.setVisibility(View.GONE);
		}

		return row;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.actionbar_dropdown_list_date_item, parent, false);
		}

		TextView txtName = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_item_name);
		TextView txtNumber = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_item_number);

		// do not display when no selection
		if (mSelectedIndex != -1) {

			OldTVDate tvDate = getItem(position);
			try {
				txtName.setText(tvDate.getName());
				txtNumber.setText(OldDateUtilities.tvDateStringToDatePickerString(tvDate.getDate()));
			} catch (Exception e) {
				e.printStackTrace();
				txtName.setText("");
				txtNumber.setText("");
			}

			// make the selected text position bold
			if(position==mSelectedIndex){
				txtName.setTypeface(null, Typeface.BOLD);
			}
			
			txtName.setVisibility(View.VISIBLE);
			txtNumber.setVisibility(View.VISIBLE);

		} else {
			txtName.setVisibility(View.GONE);
			txtNumber.setVisibility(View.GONE);
		}

		return row;
	}

	public void setSelectedIndex(int index) {
		this.mSelectedIndex = index;
	}
}
