package com.millicom.secondscreen.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.myprofile.RemindersCountInterface;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationDialogHandler;
import com.millicom.secondscreen.storage.DazooStore;

public class RemindersListAdapter extends BaseAdapter {

	private static final String		TAG				= "RemindersListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mBroadcasts;
	private RemindersCountInterface	mInterface;
	private int						notificationId;
	private int						currentPosition	= -1;

	private DazooStore				dazooStore;
	private ArrayList<TvDate>		mTvDates;

	public RemindersListAdapter(Activity mActivity, ArrayList<Broadcast> mBroadcasts, RemindersCountInterface remindersInterface) {
		this.mBroadcasts = mBroadcasts;
		this.mActivity = mActivity;
		this.mInterface = remindersInterface;

		dazooStore = DazooStore.getInstance();
		mTvDates = dazooStore.getTvDates();
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
			viewHolder.mHeaderContainer = (RelativeLayout) rowView.findViewById(R.id.row_reminders_header_container);
			viewHolder.mInformationContainer = (RelativeLayout) rowView.findViewById(R.id.row_reminders_text_container);
			viewHolder.mHeaderTv = (TextView) rowView.findViewById(R.id.row_reminders_header_textview);
			viewHolder.mBroadcastTitleTv = (TextView) rowView.findViewById(R.id.row_reminders_text_title_tv);
			viewHolder.mBroadcastDetailsTv = (TextView) rowView.findViewById(R.id.row_reminders_text_details_tv);
			viewHolder.mBroadcastTimeTv = (TextView) rowView.findViewById(R.id.row_reminders_text_time_tv);
			viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.row_reminders_text_channel_tv);
			viewHolder.mReminderIconIv = (ImageView) rowView.findViewById(R.id.row_reminders_notification_iv);
			viewHolder.mReminderIconIv.setTag(Integer.valueOf(position));

			viewHolder.mDividerView = (View) rowView.findViewById(R.id.row_reminders_header_divider);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		final Broadcast broadcast = getItem(position);
		if (broadcast != null) {
			final Channel channel = broadcast.getChannel();
			Program program = broadcast.getProgram();

			// Get the correct date name index
			int dateIndex = 0;

			for (int i = 0; i < mTvDates.size(); i++) {
				if (broadcast.getBeginTimeStringGmt().contains(mTvDates.get(i).getDate())) {
					dateIndex = i;
					break;
				}
			}

			// If first or the previous broadcast is not the same date, show header.
			holder.mHeaderContainer.setVisibility(View.GONE);
			holder.mDividerView.setVisibility(View.VISIBLE);

			int prevPos = Math.max(position - 1, 0);

			int nextPos = Math.min(position + 1, (mBroadcasts.size() - 1));
			Broadcast broadcastPreviousPosition = getItem(prevPos);
			Broadcast broadcastNextPosition = getItem(nextPos);

			String stringCurrent = broadcast.getBeginTimeStringLocalDayMonth();
			String stringPrevious = broadcastPreviousPosition.getBeginTimeStringLocalDayMonth();
			String stringNext = broadcastNextPosition.getBeginTimeStringLocalDayMonth();

			if ((position == 0) || !stringCurrent.equals(stringPrevious)) {
				holder.mHeaderContainer.setVisibility(View.VISIBLE);
				if (mTvDates != null && mTvDates.isEmpty() != true) {
					holder.mHeaderTv.setText(mTvDates.get(dateIndex).getName().toUpperCase(Locale.getDefault()));
				} else {
					holder.mHeaderTv.setVisibility(View.GONE);
				}
			}
			if (position != (getCount() - 1) && !stringCurrent.equals(stringNext)) {
				holder.mDividerView.setVisibility(View.GONE);
			}

			if (program != null) {
				holder.mBroadcastTitleTv.setText(program.getTitle());
				holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);

				String programType = program.getProgramType();
				if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					String season = broadcast.getProgram().getSeason().getNumber();
					int seasonNumber = Integer.parseInt(season);
					int episode = broadcast.getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (seasonNumber > 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode > 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
					}
					holder.mBroadcastDetailsTv.setText(seasonEpisode);
				} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					holder.mBroadcastDetailsTv.setText(program.getGenre() + " " + program.getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
					holder.mBroadcastDetailsTv.setText(program.getCategory());
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
					holder.mBroadcastDetailsTv.setText(program.getSportType().getName());
				}
			}

			if (channel != null) {
				holder.mChannelTv.setText(channel.getName());
			}

			holder.mBroadcastTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());
			holder.mInformationContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO use local or GMT?
					String broadcastUrl = Consts.NOTIFY_BROADCAST_URL_PREFIX + channel.getChannelId() + Consts.NOTIFY_BROADCAST_URL_MIDDLE + broadcast.getBeginTimeMillisGmt();
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_URL, broadcastUrl);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_PROFILE, true);
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			});

			holder.mReminderIconIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			holder.mReminderIconIv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					currentPosition = (Integer) v.getTag();

					NotificationDataSource notificationDataSource = new NotificationDataSource(mActivity);

					NotificationDbItem notificationDbItem = new NotificationDbItem();
					notificationDbItem = notificationDataSource.getNotification(channel.getChannelId(), Long.valueOf(broadcast.getBeginTimeMillisGmt()));
					if (notificationDbItem != null) {
						notificationId = notificationDbItem.getNotificationId();
						NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
						notificationDlg.showRemoveNotificationDialog(mActivity, broadcast, notificationId, yesProc(), noProc());
					}
				}
			});
		}
		return rowView;
	}

	private static class ViewHolder {
		public RelativeLayout	mHeaderContainer;
		public RelativeLayout	mInformationContainer;
		public TextView			mHeaderTv;
		public TextView			mBroadcastTitleTv;
		public TextView			mBroadcastDetailsTv;
		public TextView			mBroadcastTimeTv;
		public TextView			mChannelTv;
		public ImageView		mReminderIconIv;

		public View				mDividerView;
	}

	public Runnable yesProc() {
		return new Runnable() {
			public void run() {
				mBroadcasts.remove(currentPosition);
				mInterface.setValues(mBroadcasts.size());
				notifyDataSetChanged();
			}
		};
	}

	public Runnable noProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}
}
