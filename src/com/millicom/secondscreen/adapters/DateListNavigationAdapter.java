package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.utilities.DateUtilities;

public class DateListNavigationAdapter extends BaseAdapter {

	private ArrayList<TvDate>	mTabTitles;
	private Context				mContext;

	private int					mFullWidth;
	private final int			PADDING			= 20;

	private int					mSelectedIndex	= -1;

	private final String		TAG				= "DateListNavigationAdapter";

	public DateListNavigationAdapter(Context context, ArrayList<TvDate> tabTitles) {
		this.mContext = context;
		this.mTabTitles = tabTitles;

		//WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		//this.mFullWidth = wm.getDefaultDisplay().getWidth();
	}
	@Override
	public int getCount() {
		if (mTabTitles != null) {
			return mTabTitles.size();
		} else return 0;
	}

	@Override
	public TvDate getItem(int position) {
		if (mTabTitles != null) {
			return mTabTitles.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.actionbar_dropdown_list_date_item, null);

		TextView txtName = (TextView) view.findViewById(R.id.layout_actionbar_dropdown_list_date_item_name);
		TextView txtNumber = (TextView) view.findViewById(R.id.layout_actionbar_dropdown_list_date_item_number);

		// do not display when no selection
		if (mSelectedIndex != -1) {

			TvDate tvDate = getItem(position);
				try {
				txtName.setText(tvDate.getName());
				txtNumber.setText(DateUtilities.tvDateStringToDatePickerString(tvDate.getDate()));
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
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.actionbar_dropdown_list_date_item, null);

		TextView txtName = (TextView) view.findViewById(R.id.layout_actionbar_dropdown_list_date_item_name);
		TextView txtNumber = (TextView) view.findViewById(R.id.layout_actionbar_dropdown_list_date_item_number);

		// do not display when no selection
		if (mSelectedIndex != -1) {

			TvDate tvDate = getItem(position);
				try {
				txtName.setText(tvDate.getName());
				txtNumber.setText(DateUtilities.tvDateStringToDatePickerString(tvDate.getDate()));
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
		return view;
	}

	public void setSelectedIndex(int index) {
		this.mSelectedIndex = index;
	}
}