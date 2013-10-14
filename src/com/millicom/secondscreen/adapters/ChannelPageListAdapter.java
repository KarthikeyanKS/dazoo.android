package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelPageListAdapter extends BaseAdapter {

	private static final String		TAG				= "ChannelPageListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mFollowingBroadcasts;

	private ImageLoader				mImageLoader;
	private int						mLastPosition	= -1;

	public ChannelPageListAdapter(Activity activity, ArrayList<Broadcast> followingBroadcasts) {
		this.mFollowingBroadcasts = followingBroadcasts;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(activity, R.drawable.loadimage);
	}

	@Override
	public int getCount() {
		if (mFollowingBroadcasts != null) {
			return mFollowingBroadcasts.size();
		} else return 0;
	}

	@Override
	public Broadcast getItem(int position) {
		if (mFollowingBroadcasts != null) {
			return mFollowingBroadcasts.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Broadcast broadcast = getItem(position);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.layout_channelpage_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.channelpage_list_item_time_tv);
			viewHolder.mIconIv = (ImageView) rowView.findViewById(R.id.channelpage_list_item_iv);
			viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.channelpage_list_item_title_tv);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			try {
				holder.mTimeTv.setText(DateUtilities.isoStringToTimeString(broadcast.getBeginTime()));
			} catch (ParseException e) {
				e.printStackTrace();
				holder.mTimeTv.setText("");
			}

			// TODO: manipulate icon

			String title = broadcast.getProgram().getTitle();
			if (title != null) {
				holder.mTitleTv.setText(title);
			} else {
				holder.mTitleTv.setText("");
			}

		} else {
			holder.mTimeTv.setText("");
			holder.mTitleTv.setText("");
		}

		// animate the item - available for higher api levels only
		// TranslateAnimation animation = null;
		// if (position > mLastPosition) {
		// animation = new TranslateAnimation(
		// Animation.RELATIVE_TO_SELF,
		// 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		// Animation.RELATIVE_TO_SELF, 1.0f,
		// Animation.RELATIVE_TO_SELF, 0.0f);
		//
		// animation.setDuration(1000);
		// rowView.startAnimation(animation);
		// mLastPosition = position;
		// }

		return rowView;
	}

	static class ViewHolder {
		TextView	mTimeTv;
		ImageView	mIconIv;
		TextView	mTitleTv;
	}
}
