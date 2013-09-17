package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

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

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Category;
import com.millicom.secondscreen.content.model.ProgramType;

public class ActionBarDropDownCategoryListAdapter extends BaseAdapter implements SpinnerAdapter {

	private static final String TAG = "ActionBarDropDownCategoryListAdapter";
	private ArrayList<ProgramType>	mCategories;
	private Context context;

	public ActionBarDropDownCategoryListAdapter(Context context, ArrayList<ProgramType> mTypes) {
		this.context = context;
		this.mCategories = mTypes;
	}

	@Override
	public int getCount() {
		if (mCategories != null){
			return mCategories.size();
		} else return 0;
	}

	@Override
	public ProgramType getItem(int position) {
		if (mCategories != null) {
			return mCategories.get(position);
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
			row = inflater.inflate(R.layout.layout_actionbar_dropdown_list_category_item, parent, false);
		}

		ProgramType category = mCategories.get(position);
		
		TextView textView = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_category_item_name);
		textView.setText(category.getName());
		return row;
	}
}