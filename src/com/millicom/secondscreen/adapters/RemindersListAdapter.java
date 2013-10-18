package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationService;
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
import android.widget.Toast;

public class RemindersListAdapter extends BaseAdapter {

	private static final String		TAG	= "RemindersListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mBroadcasts;

	private ImageLoader				mImageLoader;
	private boolean isSet = false;

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
			viewHolder.mBroadcastTitleTv = (TextView) rowView.findViewById(R.id.row_reminders_text_title_tv);
			viewHolder.mBroadcastDetailsTv = (TextView) rowView.findViewById(R.id.row_reminders_text_details_tv);
			viewHolder.mBroadcastTimeTv = (TextView) rowView.findViewById(R.id.row_reminders_text_time_tv);
			viewHolder.mReminderIconIv = (ImageView) rowView.findViewById(R.id.row_reminders_notification_iv);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		final Broadcast broadcast = getItem(position);
		if (broadcast != null) {
			final Channel channel = broadcast.getChannel();
			Program program = broadcast.getProgram();

			// TODO
			// include the sorting logic to show or hide the header title
			holder.mHeaderContainer.setVisibility(View.VISIBLE);
			holder.mHeaderTv.setText("Reminders");

			if (program != null) {
				holder.mBroadcastTitleTv.setText(program.getTitle());
				holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);

				String programType = program.getProgramType();
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					holder.mBroadcastDetailsTv.setText(program.getSeason() + ", " + program.getEpisode());
				} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					holder.mBroadcastDetailsTv.setText(program.getYear());
				} 
			}

			if (channel != null) {
				// TODO ADD NAME
			}
			try {
				holder.mBroadcastTimeTv.setText(DateUtilities.isoStringToDateShortAndTimeString(broadcast.getBeginTime()));
			} catch (Exception e) {
				e.printStackTrace();
				holder.mBroadcastTimeTv.setText("");
			}
			
			
			NotificationDataSource notificationDataSource = new NotificationDataSource(mActivity);
			NotificationDbItem dbItem = new NotificationDbItem();
			dbItem = notificationDataSource.getNotification(channel.getChannelId(), broadcast.getBeginTimeMillis());
			if (dbItem!= null){
				isSet = true;
				holder.mReminderIconIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
			} else {
				isSet = false;
				holder.mReminderIconIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
			}
			
			
			holder.mReminderIconIv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isSet == false) {
						// add reminder
						isSet = true;
						holder.mReminderIconIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
						if (NotificationService.setAlarm(mActivity, broadcast, channel)) {
							NotificationService.showSetNotificationToast(mActivity);
						} else {
							Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
						}
					} else {
						// remove reminder
						
						
						NotificationDataSource notificationDataSource = new NotificationDataSource(mActivity);
						
						NotificationDbItem notificationDbItem = new NotificationDbItem();
						notificationDbItem = notificationDataSource.getNotification(channel.getChannelId(), Long.valueOf(broadcast.getBeginTimeMillis()));
						if (notificationDbItem != null){
						
						boolean answer[] = NotificationService.showRemoveNotificationDialog(mActivity, broadcast, notificationDbItem.getNotificationId());
						
						if(answer[0]== false){
							isSet = true;
							
						} else {
							isSet = false;
							holder.mReminderIconIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
						}
						
						
						
						
						}
						else {
							Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
			
		}

		return rowView;
	}

	private static class ViewHolder {
		public LinearLayout	mHeaderContainer;
		public TextView		mHeaderTv;
		public TextView		mBroadcastTitleTv;
		public TextView		mBroadcastDetailsTv;
		public TextView		mBroadcastTimeTv;
		public ImageView	mReminderIconIv;
	}

}
