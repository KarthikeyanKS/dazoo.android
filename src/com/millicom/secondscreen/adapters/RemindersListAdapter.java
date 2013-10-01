package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RemindersListAdapter extends BaseAdapter {

	private static final String		TAG	= "RemindersListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mBroadcasts;

	private ImageLoader				mImageLoader;

	public RemindersListAdapter(Activity mActivity, ArrayList<Broadcast> mBroadcasts) {
		this.mBroadcasts = mBroadcasts;
		this.mActivity = mActivity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
	}

	@Override
	public int getCount() {
		if (mBroadcasts != null) {
			return mBroadcasts.size();
		} else return 0;
	}

	@Override
	public Broadcast getItem(int position) {
		if (mBroadcasts != null) {
			return mBroadcasts.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// inflate different layouts depending on the program type
			rowView = mLayoutInflater.inflate(R.layout.row_reminders, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mHeaderContainer = (LinearLayout) rowView.findViewById(R.id.row_reminders_header_container);
			viewHolder.mHeaderTv = (TextView) rowView.findViewById(R.id.row_reminders_header_textview);
			viewHolder.mBroadcastIv = (ImageView) rowView.findViewById(R.id.row_reminders_iv);
			viewHolder.mBroadcastTitleTv = (TextView) rowView.findViewById(R.id.row_reminders_text_title_tv);
			viewHolder.mBroadcastDetailsTv = (TextView) rowView.findViewById(R.id.row_reminders_text_details_tv);
			viewHolder.mBroadcastChannelIv = (ImageView) rowView.findViewById(R.id.row_reminders_channel_icon_iv);
			viewHolder.mBroadcastTimeTv = (TextView) rowView.findViewById(R.id.row_reminders_text_time_tv);
			viewHolder.mReminderIconIv = (ImageView) rowView.findViewById(R.id.row_reminders_notification_iv);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		final Broadcast broadcast = getItem(position);
		if (broadcast != null) {
			Channel channel = broadcast.getChannel();
			Program program = broadcast.getProgram();

			// TODO
			// include the sorting logic to show or hide the header title
			holder.mHeaderContainer.setVisibility(View.VISIBLE);
			holder.mHeaderTv.setText("Reminders");

			if (program != null) {
				holder.mBroadcastTitleTv.setText(program.getTitle());
				holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);
				holder.mBroadcastDetailsTv.setText(program.getSeason() + ", " + program.getEpisode());
				mImageLoader.displayImage(program.getPosterMUrl(), holder.mBroadcastIv, ImageLoader.IMAGE_TYPE.POSTER);
			}

			if (channel != null) {
				if (channel.getLogoSUrl() != null) {
					holder.mBroadcastChannelIv.setVisibility(View.VISIBLE);
					mImageLoader.displayImage(channel.getLogoSUrl(), holder.mBroadcastChannelIv, ImageLoader.IMAGE_TYPE.POSTER);
				}
			}
			try {
				holder.mBroadcastTimeTv.setText(DateUtilities.isoStringToDateShortAndTimeString(broadcast.getBeginTime()));
			} catch (Exception e) {
				e.printStackTrace();
				holder.mBroadcastTimeTv.setText("");
			}
		}

		return rowView;
	}

	private static class ViewHolder {
		public LinearLayout	mHeaderContainer;
		public TextView		mHeaderTv;
		public ImageView	mBroadcastIv;
		public TextView		mBroadcastTitleTv;
		public TextView		mBroadcastDetailsTv;
		public ImageView	mBroadcastChannelIv;
		public TextView		mBroadcastTimeTv;
		public ImageView	mReminderIconIv;
	}

}
