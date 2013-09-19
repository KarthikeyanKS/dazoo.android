package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
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
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class TVGuideListAdapter extends BaseAdapter {

	private static final String			TAG	= "TVGuideListAdapter";

	private LayoutInflater				mLayoutInflater;
	private Activity					mActivity;
	private ArrayList<Guide>			mGuide;

	private ImageLoader mImageLoader;
	
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

			//viewHolder.mLiveProgramContainer = (RelativeLayout) rowView.findViewById(R.id.tvguide_program_line_live_container);
			viewHolder.mLiveProgramIv = (ImageView) rowView.findViewById(R.id.tvguide_program_line_live_icon_iv);
			viewHolder.mLiveProgramNameTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_live_name_tv);
			viewHolder.mLiveProgramTimeTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_live_time_tv);

			//viewHolder.mNextProgramContainer = (RelativeLayout) rowView.findViewById(R.id.tvguide_program_line_next_container);
			viewHolder.mNextProgramIv = (ImageView) rowView.findViewById(R.id.tvguide_program_line_next_icon_iv);
			viewHolder.mNextProgramNameTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_next_name_tv);
			viewHolder.mNextProgramTimeTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_next_time_tv);

			//viewHolder.mLastProgramContainer = (RelativeLayout) rowView.findViewById(R.id.tvguide_program_line_last_container);
			viewHolder.mLastProgramIv = (ImageView) rowView.findViewById(R.id.tvguide_program_line_last_icon_iv);
			viewHolder.mLastProgramNameTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_last_name_tv);
			viewHolder.mLastProgramTimeTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_last_time_tv);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (guide.getLogoLHref() != null) {
			// ImageDownloadThread getChannelIconTask = new ImageDownloadThread(holder.mChannelIconIv, holder.mProgressBar);
			// getChannelIconTask.execute(guide.getLogoHref());
			//imageLoader.displayImage(guide.getLogoLHref(), mActivity, holder.mChannelIconIv);
			mImageLoader.displayImage(guide.getLogoLHref(),holder.mChannelIconIv, ImageLoader.IMAGE_TYPE.POSTER);
			
		} else {
			holder.mChannelIconIv.setImageResource(R.drawable.loadimage_2x);
		}

		ArrayList<Broadcast> broadcasts = guide.getBroadcasts();

		if (broadcasts != null) {
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


			/*
			 * for (int k = 0; k < counter; k++) { if (k == 0) { View child = mLayoutInflater.inflate(R.layout.row_tvguide_broadcast_live_item, null);
			 * 
			 * ImageView childIcon = (ImageView) child.findViewById(R.id.tvguide_program_line_live_icon_iv);
			 * 
			 * TextView childName = (TextView) child.findViewById(R.id.tvguide_program_line_live_name_tv); childName.setText(broadcasts.get(k).getProgram().getTitle()); TextView childTime = (TextView) child.findViewById(R.id.tvguide_program_line_live_time_tv); try {
			 * childTime.setText((DateUtilities.isoStringToTimeString(broadcasts.get(k).getBeginTime()))); } catch (Exception e) { e.printStackTrace(); childTime.setText(""); } holder.mBroadcastItemLl.addView(child); } else { View child =
			 * mLayoutInflater.inflate(R.layout.row_tvguide_broadcast_next_item, null);
			 * 
			 * ImageView childIcon = (ImageView) child.findViewById(R.id.tvguide_program_line_next_icon_iv); TextView childName = (TextView) child.findViewById(R.id.tvguide_program_line_next_name_tv); TextView childTime = (TextView) child.findViewById(R.id.tvguide_program_line_next_time_tv);
			 * 
			 * if (k < broadcasts.size()) { childName.setText(broadcasts.get(k).getProgram().getTitle()); try { childTime.setText((DateUtilities.isoStringToTimeString(broadcasts.get(k).getBeginTime()))); } catch (Exception e) { e.printStackTrace(); childTime.setText(""); } } else {
			 * childName.setText(""); childTime.setText(""); } holder.mBroadcastItemLl.addView(child); } }
			 */
		}
		return rowView;
	}

	static class ViewHolder {
		public ImageView		mChannelIconIv;
		public ProgressBar		mProgressBar;
		// public LinearLayout mBroadcastItemLl;

		// LIVE PROGRAM
		//public RelativeLayout	mLiveProgramContainer;
		public ImageView		mLiveProgramIv;
		public TextView			mLiveProgramNameTv;
		public TextView			mLiveProgramTimeTv;

		// NEXT PROGRAM
		//public RelativeLayout	mNextProgramContainer;
		public ImageView		mNextProgramIv;
		public TextView			mNextProgramNameTv;
		public TextView			mNextProgramTimeTv;

		// LAST PROGRAM
		//public RelativeLayout	mLastProgramContainer;
		public ImageView		mLastProgramIv;
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

}
