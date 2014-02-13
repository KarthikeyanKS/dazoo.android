package com.mitv.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.mitv.activities.BroadcastPageActivity;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.handlers.NotificationDialogHandler;
import com.mitv.interfaces.RemindersCountInterface;
import com.mitv.model.Broadcast;
import com.mitv.model.TVChannel;
import com.mitv.model.NotificationDbItem;
import com.mitv.model.Program;
import com.mitv.model.TVDate;
import com.mitv.notification.NotificationDataSource;
import com.mitv.storage.MiTVStore;

public class RemindersListAdapter extends BaseAdapter {

	private static final String		TAG				= "RemindersListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mBroadcasts;
	private RemindersCountInterface	mInterface;
	private int						notificationId;
	private int						currentPosition	= -1;

	private MiTVStore				mitvStore;
	private ArrayList<TVDate>		mTvDates;

	public RemindersListAdapter(Activity mActivity, ArrayList<Broadcast> mBroadcasts, RemindersCountInterface remindersInterface) {
		this.mBroadcasts = mBroadcasts;
		this.mActivity = mActivity;
		this.mInterface = remindersInterface;

		mitvStore = MiTVStore.getInstance();
		mTvDates = mitvStore.getTvDates();
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
			final TVChannel channel = broadcast.getChannel();
			Program program = broadcast.getProgram();

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
				holder.mHeaderTv.setText(broadcast.getDayOfWeekString() + " " + broadcast.getBeginTimeStringLocalDayMonth());
				holder.mHeaderContainer.setVisibility(View.VISIBLE);
			}

			if (position != (getCount() - 1) && !stringCurrent.equals(stringNext)) {
				holder.mDividerView.setVisibility(View.GONE);
			}

			if (program != null) {
				holder.mBroadcastTitleTv.setText(program.getTitle());
				holder.mBroadcastDetailsTv.setVisibility(View.VISIBLE);

				String programType = program.getProgramType();
				if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
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
				} else if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
					holder.mBroadcastDetailsTv.setText(program.getGenre() + " " + program.getYear());
				} else if (Consts.PROGRAM_TYPE_OTHER.equals(programType)) {
					String category = program.getCategory();
					holder.mBroadcastDetailsTv.setText(category);
				} else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) {
					if (program.getTournament() != null) {
						holder.mBroadcastDetailsTv.setText(program.getTournament());
					}
					else {
						holder.mBroadcastDetailsTv.setText(program.getSportType().getName());
					}
				}
			}

			if (channel != null) {
				holder.mChannelTv.setText(channel.getName());
			}

			holder.mBroadcastTimeTv.setText(broadcast.getDayOfWeekWithTimeString());
			holder.mInformationContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String broadcastUrl = Consts.URL_NOTIFY_BROADCAST_PREFIX + channel.getChannelId() + Consts.NOTIFY_BROADCAST_URL_MIDDLE + broadcast.getBeginTimeMillisGmt();
					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_URL, broadcastUrl);
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_LOGO_URL, channel.getImageUrl());
					intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);
					intent.putExtra(Consts.INTENT_EXTRA_FROM_PROFILE, true);
					mActivity.startActivity(intent);
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
				if(currentPosition >= 0 && currentPosition < mBroadcasts.size()) {
					mBroadcasts.remove(currentPosition);
					mInterface.setValues(mBroadcasts.size());
					notifyDataSetChanged();
				}
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
