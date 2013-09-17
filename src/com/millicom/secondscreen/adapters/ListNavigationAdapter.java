package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

import com.millicom.secondscreen.R;

public class ListNavigationAdapter extends BaseAdapter {

	private ArrayList<String>	mTabTitles;
	private Context				mContext;

	private int					mFullWidth;
	private final int			PADDING			= 20;

	private int					mSelectedIndex	= -1;

	private final String		TAG				= "ListNavigationAdapter";

	public ListNavigationAdapter(Context context, ArrayList<String> tabTitles) {
		this.mContext = context;
		this.mTabTitles = tabTitles;
		//WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		//this.mFullWidth = wm.getDefaultDisplay().getWidth();
	}

	@Override
	public int getCount() {
		return mTabTitles.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		View v = LayoutInflater.from(mContext).inflate(R.layout.menu_navigation_row_overflow, null);
		v.setLayoutParams(new LayoutParams(mFullWidth, LayoutParams.WRAP_CONTENT));

		TextView tv = (TextView) v.findViewById(R.id.overflow_menu_tv);
		tv.setText(mTabTitles.get(position));

		if (position == mSelectedIndex) {
			tv.setTextColor(mContext.getResources().getColor(R.color.gray));
		}
		return v;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = LayoutInflater.from(mContext).inflate(R.layout.menu_row_overflow, null);
		TextView tv = (TextView) v.findViewById(R.id.overflow_menu_tv);
		tv.setText(mTabTitles.get(position));
		
		return v;
		

		// TextView tv = new TextView(mContext);
		//
		// tv.setTextSize(18);
		// tv.setPadding(0, 0, 0, 0);
		// tv.setTextColor(Color.WHITE);
		// tv.setText(mTabTitles.get(position));
		// tv.setEllipsize(TruncateAt.END);
		//
		// return tv;
	}

	public void setSelectedIndex(int index) {
		this.mSelectedIndex = index;
	}

}
