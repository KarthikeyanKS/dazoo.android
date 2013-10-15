package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.utilities.DateUtilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ActionBarDropDownDateListAdapter extends BaseAdapter implements SpinnerAdapter {

	private static final String	TAG	= "Adapter";
	private ArrayList<TvDate>	mDays;
	private Context				context;

	public ActionBarDropDownDateListAdapter(Context context, ArrayList<TvDate> mDays) {
		this.context = context;
		this.mDays = mDays;
	}

	@Override
	public int getCount() {
		if (mDays != null) {
			return mDays.size();
		} else return 0;
	}

	@Override
	public TvDate getItem(int position) {
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
			row = inflater.inflate(R.layout.layout_actionbar_dropdown_list_date_header, parent, false);
		}

		Drawable res = context.getResources().getDrawable(R.drawable.ic_launcher);
		ImageView imageView = (ImageView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_header_iv);
		imageView.setImageDrawable(res);
		return row;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.layout_actionbar_dropdown_list_date_item, parent, false);
		}

		//ImageView imageView = (ImageView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_item_icon);
		TextView txtTitle = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_item_name);

		TvDate tvDate = mDays.get(position);

		Drawable res = context.getResources().getDrawable(R.drawable.ic_launcher);
	//	imageView.setImageDrawable(res);
		try {
			txtTitle.setText(tvDate.getName() + " " + DateUtilities.tvDateStringToDatePickerString(tvDate.getDate()));
		} catch (Exception e) {
			e.printStackTrace();
			txtTitle.setText("");
		}
		return row;
	}
}
