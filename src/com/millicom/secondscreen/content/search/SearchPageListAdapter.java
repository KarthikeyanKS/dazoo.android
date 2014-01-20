package com.millicom.secondscreen.content.search;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.SearchResultItem;
import com.millicom.secondscreen.customviews.FontTextView;

public class SearchPageListAdapter extends BaseAdapter {
	
	private ArrayList<SearchResultItem> searchResultItems;
	
	@Override
	public int getCount() {
		int count = 0;
		if(searchResultItems != null) {
			count = searchResultItems.size();
		}
		return count;
	}

	@Override
	public SearchResultItem getItem(int position) {
		return searchResultItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}
	
	public ArrayList<SearchResultItem> getSearchResultItems() {
		return searchResultItems;
	}

	public void setSearchResultItems(ArrayList<SearchResultItem> searchResultItems) {
		this.searchResultItems = searchResultItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		LayoutInflater mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (rowView == null) {
			rowView = mLayoutInflater.inflate(R.layout.row_search_result, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mTextView = (FontTextView) rowView.findViewById(R.id.row_search_result_textview);

			rowView.setTag(viewHolder);
		}
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		SearchResultItem resultItem = getItem(position);
		String displayText = resultItem.getDisplayText();
		holder.mTextView.setText(displayText);
		
		return rowView;
	}

	static class ViewHolder {
		public FontTextView mTextView;
	}
}
