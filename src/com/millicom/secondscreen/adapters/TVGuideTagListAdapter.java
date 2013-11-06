package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.tvguide.ChannelPageActivity;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class TVGuideTagListAdapter extends BaseAdapter {

	private static final String	TAG	= "TVGuideListAdapter";

	private LayoutInflater		mLayoutInflater;
	private Activity			mActivity;
	private ArrayList<Broadcast> mTaggedBroadcasts;
	private ImageLoader			mImageLoader;
	private int mCurrentPosition;

	public TVGuideTagListAdapter(Activity activity, ArrayList<Broadcast> taggedBroadcasts, int currentPosition) {
		this.mTaggedBroadcasts = taggedBroadcasts;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
		this.mCurrentPosition = currentPosition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		// get the item with the displacement depending on the scheduled time on air
		final Broadcast broadcast = getItem(mCurrentPosition + position);

		// TODO: DIFFERENT ROW LAYOUTS DEPENDING ON THE TYPE OF THE BROADCAST
		
		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.layout_tvguide_tag_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView.findViewById(R.id.tvguide_tag_list_item_name_tv);
			viewHolder.time = (TextView) rowView.findViewById(R.id.tvguide_tag_list_item_time_tv);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.title.setText(broadcast.getProgram().getTitle() + "  " + broadcast.getProgram().getProgramType() + "   " +  broadcast.getBeginTime());
		
		try {
			holder.time.setText(DateUtilities.isoStringToTimeString(broadcast.getBeginTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
		return rowView;
	}

	static class ViewHolder {
		TextView title;
		TextView time;
	}

	@Override
	public int getCount() {
		if (mTaggedBroadcasts != null) {
			return mTaggedBroadcasts.size() - mCurrentPosition;
		} else {
			return 0;
		}
	}

	@Override
	public Broadcast getItem(int position) {
		if (mTaggedBroadcasts != null) {
			return mTaggedBroadcasts.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}
}

