package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.ChannelPageActivity;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class TVGuideListAdapter extends BaseAdapter {

	private static final String	TAG	= "TVGuideListAdapter";

	private LayoutInflater		mLayoutInflater;
	private Activity			mActivity;
	private ArrayList<Guide>	mGuide;
	private TvDate				mDate;
	private ImageLoader			mImageLoader;
	private int					mIndexOfNearestBroadcast;
	private int					mHour, mCurrentHour;
	private boolean				mIsToday;

	public TVGuideListAdapter(Activity activity, ArrayList<Guide> guide, TvDate date, int hour, boolean isToday) {
		this.mGuide = guide;
		this.mActivity = activity;
		this.mDate = date;
		this.mImageLoader = new ImageLoader(mActivity, R.color.grey1);
		this.mHour = hour;
		this.mIsToday = isToday;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.row_tvguide_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.item_container);
			viewHolder.mChannelIconIv = (ImageView) rowView.findViewById(R.id.tvguide_channel_iv);
			viewHolder.mProgressBar = (ProgressBar) rowView.findViewById(R.id.tvguide_channel_progressbar);
			// viewHolder.mBroadcastItemLl = (LinearLayout) rowView.findViewById(R.id.tvguide_program_list_container);

			View container;
			
			container = rowView.findViewById(R.id.tvguide_program_line_live_container);
			// viewHolder.mLiveProgramContainer = (RelativeLayout) container;
			viewHolder.mLiveProgramTimeTv = (TextView) container.findViewById(R.id.tvguide_program_line_time_tv);
			viewHolder.mLiveProgramNameTv = (TextView) container.findViewById(R.id.tvguide_program_line_name_tv);
			
			container = rowView.findViewById(R.id.tvguide_program_line_next_container);
			// viewHolder.mNextProgramContainer = (RelativeLayout) container;
			viewHolder.mNextProgramTimeTv = (TextView) container.findViewById(R.id.tvguide_program_line_time_tv);
			viewHolder.mNextProgramNameTv = (TextView) container.findViewById(R.id.tvguide_program_line_name_tv);
			
			container = rowView.findViewById(R.id.tvguide_program_line_last_container);
			// viewHolder.mLastProgramContainer = (RelativeLayout) container;
			viewHolder.mLastProgramTimeTv = (TextView) container.findViewById(R.id.tvguide_program_line_time_tv);
			viewHolder.mLastProgramNameTv = (TextView) container.findViewById(R.id.tvguide_program_line_name_tv);

			rowView.setTag(viewHolder);
		}
		ViewHolder holder = (ViewHolder) rowView.getTag();

		final Guide guide = getItem(position);

		if (guide.getLogoLHref() != null) {
			// ImageDownloadThread getChannelIconTask = new ImageDownloadThread(holder.mChannelIconIv, holder.mProgressBar);
			// getChannelIconTask.execute(guide.getLogoHref());
			// imageLoader.displayImage(guide.getLogoLHref(), mActivity, holder.mChannelIconIv);
			Log.v("TVGuideListAdapter:", "Calling displayImage");
			mImageLoader.displayImage(guide.getLogoSHref(), holder.mChannelIconIv, ImageLoader.IMAGE_TYPE.THUMBNAIL);
		} else {
			holder.mChannelIconIv.setImageResource(R.color.white);
		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mActivity, ChannelPageActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, guide.getId());
				intent.putExtra(Consts.INTENT_EXTRA_CHOSEN_DATE_TVGUIDE, mDate);
				intent.putExtra(Consts.INTENT_EXTRA_TV_GUIDE_HOUR, mHour);

				mActivity.startActivity(intent);
				mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});

		ArrayList<Broadcast> broadcasts = guide.getBroadcasts();

		String stringIconMovie = mActivity.getResources().getString(R.string.icon_movie) + " ";
		String stringIconLive = mActivity.getResources().getString(R.string.icon_live) + " ";
		String name;

		if (broadcasts != null && broadcasts.size() > 0) {
			/* get the nearest broadcasts */

			mIndexOfNearestBroadcast = guide.getClosestBroadcastIndexFromTime(broadcasts, mHour, mDate);

			if (mIndexOfNearestBroadcast != -1) {
				ArrayList<Broadcast> nextBroadcasts = Broadcast.getBroadcastsStartingFromPosition(mIndexOfNearestBroadcast, broadcasts, Consts.TV_GUIDE_NEXT_PROGRAMS_NUMBER);
			
				// reset the viewholder to prevent the view to store the old info
				holder.mLiveProgramNameTv.setText("");
				holder.mLiveProgramTimeTv.setText("");
				holder.mNextProgramNameTv.setText("");
				holder.mNextProgramTimeTv.setText("");
				holder.mLastProgramNameTv.setText("");
				holder.mLastProgramTimeTv.setText("");
				
				for (int j = 0; j < Math.min(nextBroadcasts.size(), 3); j++) {
					Broadcast broadcast = nextBroadcasts.get(j);
					Program program = broadcast.getProgram();
					String programType = program.getProgramType();
					String broadcastType = broadcast.getBroadcastType();
					if (j == 0) {
						if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
							holder.mLiveProgramNameTv.setText(program.getSeries().getName());
						} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
							name = program.getTitle();
							name = stringIconMovie + name;
							holder.mLiveProgramNameTv.setText(name);
						} else {

							name = program.getTitle();
							if (Consts.DAZOO_BROADCAST_TYPE_LIVE.equals(broadcastType)) {
								 name = stringIconLive + name;
								holder.mLiveProgramNameTv.setText(name);
							} else {
								holder.mLiveProgramNameTv.setText(name);
							}
						}
						try {
							 holder.mLiveProgramTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());
						} catch (Exception e) {
							e.printStackTrace();
							 holder.mLiveProgramTimeTv.setText("");
						}

						if (broadcast.isRunning()) {
							holder.mLiveProgramNameTv.setTextColor(mActivity.getResources().getColor(R.color.red));
							holder.mLiveProgramTimeTv.setTextColor(mActivity.getResources().getColor(R.color.red));
						} else {
							holder.mLiveProgramNameTv.setTextColor(mActivity.getResources().getColor(R.color.grey3));
							holder.mLiveProgramTimeTv.setTextColor(mActivity.getResources().getColor(R.color.grey3));
						}
					} else if (j == 1 && j < nextBroadcasts.size()) {

						if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
							name = program.getSeries().getName();
							holder.mNextProgramNameTv.setText(name);
						} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
							name = program.getTitle();
							name = stringIconMovie + name;
							holder.mNextProgramNameTv.setText(name);
						} else {
							name = program.getTitle();
							if (Consts.DAZOO_BROADCAST_TYPE_LIVE.equals(broadcastType)) {
								name = stringIconLive + name;
							} 
							holder.mNextProgramNameTv.setText(name);
						}
						try {
							 holder.mNextProgramTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());
						} catch (Exception e) {
							e.printStackTrace();
							 holder.mNextProgramTimeTv.setText("");
						}
					} else if (j == 2 && j < nextBroadcasts.size()) {
						if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(program.getProgramType())) {
							name = program.getSeries().getName();
							holder.mLastProgramNameTv.setText(name);
						} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
							name = program.getTitle();
							name = stringIconMovie + name;
							holder.mLastProgramNameTv.setText(name);
						} else {
							name = program.getTitle();

							if (Consts.DAZOO_BROADCAST_TYPE_LIVE.equals(broadcastType)) {
								name = stringIconLive + name;
							}
							holder.mLastProgramNameTv.setText(name);
						}
						try {
							 holder.mLastProgramTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());
						} catch (Exception e) {
							e.printStackTrace();
							 holder.mLastProgramTimeTv.setText("");
						}
					}
				}
				/* get the nearest broadcasts */
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
		public RelativeLayout	mContainer;
		public ImageView		mChannelIconIv;
		public ProgressBar		mProgressBar;
		// public LinearLayout mBroadcastItemLl;

		// LIVE PROGRAM
		// public RelativeLayout mLiveProgramContainer;
		public TextView			mLiveProgramNameTv;
		public TextView			mLiveProgramTimeTv;

		// NEXT PROGRAM
		// public RelativeLayout mNextProgramContainer;
		public TextView			mNextProgramNameTv;
		public TextView			mNextProgramTimeTv;

		// LAST PROGRAM
		// public RelativeLayout mLastProgramContainer;
		public TextView			mLastProgramNameTv;
		public TextView			mLastProgramTimeTv;

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

	public void refreshList(int selectedHour) {
		mHour = selectedHour;

		SecondScreenApplication.getInstance().setSelectedHour(mHour);
		notifyDataSetChanged();
	}
}
