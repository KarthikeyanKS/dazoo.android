package com.millicom.secondscreen.adapters;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.millicom.secondscreen.manager.AppConfigurationManager;

public class AdListAdapter<T> extends BaseAdapter {

	private List<T> items;
		
	public AdListAdapter(List<T> items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		int finalCount = 0;
		if (items != null) {
			int count = items.size();
			double multiple = 1 + (1/AppConfigurationManager.getInstance().getCellCountBetweenAdCells());
			finalCount = (int) Math.ceil((double)count * multiple);
		}

		return finalCount;
	}

	@Override
	public T getItem(int position) {
		T item = null;
		if (items != null) {
			if(!isAdPosition(position)) {
				int positionExcludingAds = positionExcludingAds(position);
				item = items.get(positionExcludingAds);
			}
		}
		return item;
	}

	@Override
	public int getItemViewType(int position) {
		if(isAdPosition(position)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return null;
	}
	
	public boolean isAdPosition(int position) {
		boolean isAdPosition = false;
		
		if(position > 0 && position % (1 + AppConfigurationManager.getInstance().getCellCountBetweenAdCells()) == 0) {
			isAdPosition = true;
		}
		
		return isAdPosition;
	}
	
	public int positionExcludingAds(int position) {
		int adsUntilThisPosition = (int) Math.floor((double)position / (double)(AppConfigurationManager.getInstance().getCellCountBetweenAdCells() + 1));
		int positionExcludingAds = position - adsUntilThisPosition;
		return positionExcludingAds;
	}

}
