package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.FeedItem;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BlockPopularListViewAdapter extends BaseAdapter{
	
	private static final String TAG = "BlockPopularListViewAdapter";
	
	private Activity mActivity;
	private ArrayList<Broadcast> mPopularBroadcasts;
	
	public BlockPopularListViewAdapter(Activity activity, ArrayList<Broadcast> popularBroadcasts){
		this.mActivity = activity;
		this.mPopularBroadcasts = popularBroadcasts;
	}

	@Override
	public int getCount() {
		if (mPopularBroadcasts!=null){
			return mPopularBroadcasts.size();
		} else return 0;
	}

	@Override
	public Broadcast getItem(int position) {
		if (mPopularBroadcasts!=null){
			return mPopularBroadcasts.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}


}
