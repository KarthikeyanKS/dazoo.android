package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.ChannelHour;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.myprofile.RemindersActivity;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class TVGuideListAdapter extends BaseAdapter {

	private static final String	TAG	= "TVGuideListAdapter";

	private LayoutInflater		mLayoutInflater;
	private Activity			mActivity;
	private ArrayList<Guide>	mGuide;

	private ImageLoader			mImageLoader;

	public TVGuideListAdapter(Activity mActivity, ArrayList<Guide> mGuide) {
		this.mGuide = mGuide;
		this.mActivity = mActivity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Guide guide = getItem(position);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.layout_tvguide_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mChannelIconIv = (ImageView) rowView.findViewById(R.id.tvguide_channel_iv);
			viewHolder.mProgressBar = (ProgressBar) rowView.findViewById(R.id.tvguide_channel_progressbar);
			// viewHolder.mBroadcastItemLl = (LinearLayout) rowView.findViewById(R.id.tvguide_program_list_container);

			// viewHolder.mLiveProgramContainer = (RelativeLayout) rowView.findViewById(R.id.tvguide_program_line_live_container);
			viewHolder.mLiveProgramIv = (ImageView) rowView.findViewById(R.id.tvguide_program_line_live_icon_iv);
			viewHolder.mLiveProgramNameTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_live_name_tv);
			viewHolder.mLiveProgramTimeTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_live_time_tv);

			// viewHolder.mNextProgramContainer = (RelativeLayout) rowView.findViewById(R.id.tvguide_program_line_next_container);
			viewHolder.mNextProgramIv = (ImageView) rowView.findViewById(R.id.tvguide_program_line_next_icon_iv);
			viewHolder.mNextProgramNameTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_next_name_tv);
			viewHolder.mNextProgramTimeTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_next_time_tv);

			// viewHolder.mLastProgramContainer = (RelativeLayout) rowView.findViewById(R.id.tvguide_program_line_last_container);
			viewHolder.mLastProgramIv = (ImageView) rowView.findViewById(R.id.tvguide_program_line_last_icon_iv);
			viewHolder.mLastProgramNameTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_last_name_tv);
			viewHolder.mLastProgramTimeTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_last_time_tv);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (guide.getLogoLHref() != null) {
			// ImageDownloadThread getChannelIconTask = new ImageDownloadThread(holder.mChannelIconIv, holder.mProgressBar);
			// getChannelIconTask.execute(guide.getLogoHref());
			// imageLoader.displayImage(guide.getLogoLHref(), mActivity, holder.mChannelIconIv);
			mImageLoader.displayImage(guide.getLogoLHref(), holder.mChannelIconIv, ImageLoader.IMAGE_TYPE.POSTER);

		} else {
			holder.mChannelIconIv.setImageResource(R.drawable.loadimage_2x);
		}
		
		holder.mChannelIconIv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, RemindersActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_PAGE_LINK, guide.getHref());
				mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);		
			}
		});

		ArrayList<Broadcast> broadcasts = guide.getBroadcasts();

		if (broadcasts != null && broadcasts.size() > 0) {
			
			/* get the nearest broadcasts*/
			/*
			ArrayList<Broadcast> nextBroadcasts = getClosestBroadcasts(broadcasts);
			for (int j = 0; j < nextBroadcasts.size(); j++) {
				if (j == 0) {
					holder.mLiveProgramNameTv.setText(nextBroadcasts.get(j).getProgram().getTitle());
					try {
						holder.mLiveProgramTimeTv.setText((DateUtilities.isoStringToTimeString(nextBroadcasts.get(j).getBeginTime())));
					} catch (Exception e) {
						e.printStackTrace();
						holder.mLiveProgramTimeTv.setText("");
					}
				} else if (j == 1 && j < nextBroadcasts.size()) {
					holder.mNextProgramNameTv.setText(broadcasts.get(j).getProgram().getTitle());
					try {
						holder.mNextProgramTimeTv.setText((DateUtilities.isoStringToTimeString(nextBroadcasts.get(j).getBeginTime())));
					} catch (Exception e) {
						e.printStackTrace();
						holder.mNextProgramTimeTv.setText("");
					}
				} else if ( j== 2 && j < nextBroadcasts.size()) {
					holder.mLastProgramNameTv.setText(nextBroadcasts.get(j).getProgram().getTitle());
					try {
						holder.mLastProgramTimeTv.setText((DateUtilities.isoStringToTimeString(nextBroadcasts.get(j).getBeginTime())));
					} catch (Exception e) {
						e.printStackTrace();
						holder.mLastProgramTimeTv.setText("");
					}
				}
				*/
			/*get the nearest broadcasts*/
			
			int counter = Consts.TV_GUIDE_NEXT_PROGRAMS_NUMBER;

			for (int k = 0; k < counter; k++) {
				if (k == 0) {
					holder.mLiveProgramNameTv.setText(broadcasts.get(k).getProgram().getTitle());
					try {
						holder.mLiveProgramTimeTv.setText((DateUtilities.isoStringToTimeString(broadcasts.get(k).getBeginTime())));
					} catch (Exception e) {
						e.printStackTrace();
						holder.mLiveProgramTimeTv.setText("");
					}
				} else if (k == 1 && k < broadcasts.size()) {
					holder.mNextProgramNameTv.setText(broadcasts.get(k).getProgram().getTitle());
					try {
						holder.mNextProgramTimeTv.setText((DateUtilities.isoStringToTimeString(broadcasts.get(k).getBeginTime())));
					} catch (Exception e) {
						e.printStackTrace();
						holder.mNextProgramTimeTv.setText("");
					}
				} else if (k == 2 && k < broadcasts.size()) {
					holder.mLastProgramNameTv.setText(broadcasts.get(k).getProgram().getTitle());
					try {
						holder.mLastProgramTimeTv.setText((DateUtilities.isoStringToTimeString(broadcasts.get(k).getBeginTime())));
					} catch (Exception e) {
						e.printStackTrace();
						holder.mLastProgramTimeTv.setText("");
					}
				}
			}
		} else {
			holder.mLiveProgramNameTv.setText("");
			holder.mLiveProgramTimeTv.setText("");
			holder.mNextProgramNameTv.setText("");
			holder.mNextProgramTimeTv.setText("");
			holder.mLastProgramNameTv.setText("");
			holder.mLastProgramTimeTv.setText("");
		}
		return rowView;
	}

	static class ViewHolder {
		public ImageView	mChannelIconIv;
		public ProgressBar	mProgressBar;
		// public LinearLayout mBroadcastItemLl;

		// LIVE PROGRAM
		// public RelativeLayout mLiveProgramContainer;
		public ImageView	mLiveProgramIv;
		public TextView		mLiveProgramNameTv;
		public TextView		mLiveProgramTimeTv;

		// NEXT PROGRAM
		// public RelativeLayout mNextProgramContainer;
		public ImageView	mNextProgramIv;
		public TextView		mNextProgramNameTv;
		public TextView		mNextProgramTimeTv;

		// LAST PROGRAM
		// public RelativeLayout mLastProgramContainer;
		public ImageView	mLastProgramIv;
		public TextView		mLastProgramNameTv;
		public TextView		mLastProgramTimeTv;

	}

	@Override
	public int getCount() {
		if (mGuide != null) {
			return mGuide.size();
		} else {
			return 0;
		}
	}

	@Override
	public Guide getItem(int position) {
		if (mGuide != null) {
			return mGuide.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	private ArrayList<Broadcast> getClosestBroadcasts(ArrayList<Broadcast> broadcastList) {
		ArrayList<Broadcast> nextBroadcasts = new ArrayList<Broadcast>();

		// get the time now
		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		TimeZone tz = TimeZone.getTimeZone("UTC");
		df.setTimeZone(tz);
		String timeNowStr = df.format(new Date());
		long timeNow = 0;
		try {
			timeNow = DateUtilities.isoStringToLong(timeNowStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int nearestIndex = -1;
		long bestDistanceFoundYet = Long.MAX_VALUE;
		for (int i = 0; i < broadcastList.size(); i++) {
			long timeBroadcast = 0;
			try {
				timeBroadcast = DateUtilities.isoStringToLong(broadcastList.get(i).getBeginTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			long d = Math.abs(timeNow - timeBroadcast);
			if (d < bestDistanceFoundYet) {
				nearestIndex = i;
				bestDistanceFoundYet = d;
			}
		}

		// get the selection of upcoming broadcasts
		for (int j = nearestIndex; j < Consts.TV_GUIDE_NEXT_PROGRAMS_NUMBER; j++) {
			if (j < broadcastList.size()) {
				nextBroadcasts.add(broadcastList.get(j));
			}
		}

		return nextBroadcasts;
	}

	private String getHourClosestToCurrent(String dateString) {
		SimpleDateFormat df = new SimpleDateFormat(Consts.ISO_DATE_FORMAT, Locale.getDefault());
		TimeZone tz = TimeZone.getTimeZone("UTC");
		df.setTimeZone(tz);
		String dateNow = df.format(new Date());
		String hourNow = DateUtilities.getCurrentHourString();
		try {
			String broadcastHour = DateUtilities.isoStringToHourString(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hourNow;
		// TODO

	}
}
