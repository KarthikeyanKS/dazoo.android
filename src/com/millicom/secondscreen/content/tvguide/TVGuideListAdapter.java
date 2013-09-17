package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageDownloadThread;
import com.millicom.secondscreen.utilities.ImageDownloadThreadCache;

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
import android.widget.TextView;

public class TVGuideListAdapter extends BaseAdapter {

	private static final String			TAG	= "TVGuideListAdapter";

	private LayoutInflater				mLayoutInflater;
	private Activity					mActivity;
	private ArrayList<Guide>			mGuide;

	private ImageDownloadThreadCache	imageLoader;

	public TVGuideListAdapter(Activity mActivity, ArrayList<Guide> mGuide) {
		this.mGuide = mGuide;
		this.mActivity = mActivity;
		imageLoader = new ImageDownloadThreadCache(mActivity);
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
			viewHolder.mBroadcastItemLl = (LinearLayout) rowView.findViewById(R.id.tvguide_program_list_container);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (guide.getLogoLHref() != null) {
			// ImageDownloadThread getChannelIconTask = new ImageDownloadThread(holder.mChannelIconIv, holder.mProgressBar);
			// getChannelIconTask.execute(guide.getLogoHref());
			imageLoader.displayImage(guide.getLogoLHref(), mActivity, holder.mChannelIconIv);

		} else {
			holder.mChannelIconIv.setImageResource(R.drawable.loadimage_2x);
		}

		ArrayList<Broadcast> broadcasts = guide.getBroadcasts();

		if (broadcasts != null) {
			int counter = 0;
			if (broadcasts.size() < Consts.TV_GUIDE_NEXT_PROGRAMS_NUMBER){
				counter = broadcasts.size();
			}
			else{
				counter =  Consts.TV_GUIDE_NEXT_PROGRAMS_NUMBER;
			}
			
			for (int k = 0; k < counter; k++) {
				if (k == 0) {
					View child = mLayoutInflater.inflate(R.layout.row_tvguide_broadcast_live_item, null);

					ImageView childIcon = (ImageView) child.findViewById(R.id.tvguide_program_line_live_icon_iv);
					
					TextView childName = (TextView) child.findViewById(R.id.tvguide_program_line_live_name_tv);
					childName.setText(broadcasts.get(k).getProgram().getTitle());
					Log.d(TAG,"title:" + broadcasts.get(k).getProgram().getTitle());
					TextView childTime = (TextView) child.findViewById(R.id.tvguide_program_line_live_time_tv);
					try {
						childTime.setText((DateUtilities.isoStringToTimeString(broadcasts.get(k).getBeginTime())));
					} catch (Exception e) {
						e.printStackTrace();
						childTime.setText("");
					}
					holder.mBroadcastItemLl.addView(child);
				} else {
					View child = mLayoutInflater.inflate(R.layout.row_tvguide_broadcast_next_item, null);
					
					ImageView childIcon = (ImageView) child.findViewById(R.id.tvguide_program_line_next_icon_iv);

					TextView childName = (TextView) child.findViewById(R.id.tvguide_program_line_next_name_tv);
					childName.setText(broadcasts.get(k).getProgram().getTitle());
					
					Log.d(TAG,"title child:" + broadcasts.get(k).getProgram().getTitle());
					TextView childTime = (TextView) child.findViewById(R.id.tvguide_program_line_next_time_tv);
					try {
						childTime.setText((DateUtilities.isoStringToTimeString(broadcasts.get(k).getBeginTime())));
					} catch (Exception e) {
						e.printStackTrace();
						childTime.setText("");
					}
					holder.mBroadcastItemLl.addView(child);
				}
			}
		}

		return rowView;
	}

	static class ViewHolder {
		public ImageView	mChannelIconIv;
		public ProgressBar	mProgressBar;
		public LinearLayout	mBroadcastItemLl;
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
