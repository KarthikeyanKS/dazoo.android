package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChannelPageListAdapter extends BaseAdapter {

	private static final String		TAG				= "ChannelPageListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mFollowingBroadcasts;

	private ImageLoader				mImageLoader;
	private int						mLastPosition	= -1;
	private ViewHolder 				holder;
	private int 					duration 		= 0;

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

	public void notifyBroadcastEnded() {
		mFollowingBroadcasts.remove(0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Broadcast broadcast = getItem(position);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (getItemViewType(position) == 0) {
				rowView = mLayoutInflater.inflate(R.layout.layout_channelpage_header, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_time_tv);
				viewHolder.mIconIv = (ImageView) rowView.findViewById(R.id.channelpage_broadcast_iv);
				viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_title_tv);
				viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_text_tv);
				//MC - The "extra" data fields for the current broadcast.
				viewHolder.mTimeleftTv = (TextView) rowView.findViewById(R.id.channelpage_broadcast_timeleft);
				viewHolder.mDurationPb = (ProgressBar) rowView.findViewById(R.id.channelpage_broadcast_details_progressbar);
				viewHolder.mIconPb = (ProgressBar) rowView.findViewById(R.id.channelpage_broadcast_iv_progressbar);
				rowView.setTag(viewHolder);
			}
			else {
				rowView = mLayoutInflater.inflate(R.layout.layout_channelpage_list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.channelpage_list_item_time_tv);
				viewHolder.mIconIv = (ImageView) rowView.findViewById(R.id.channelpage_list_item_iv);
				viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.channelpage_list_item_title_tv);
				viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.channelpage_list_item_description_tv);
				rowView.setTag(viewHolder);
			}
		}

		holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			if (getItemViewType(position) == 0) {
				//MC - Set the image for current broadcast.
				mImageLoader.displayImage(broadcast.getProgram().getPosterLUrl(), holder.mIconIv, holder.mIconPb, ImageLoader.IMAGE_TYPE.GALLERY);
				//MC - Calculate the duration of the program and set up ProgressBar.
				try {
					long startTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
					long endTime = DateUtilities.getAbsoluteTimeDifference(broadcast.getEndTime());
					duration = (int) (startTime - endTime) / (1000 * 60);
				} 
				catch (ParseException e) {
					e.printStackTrace();
				}
				holder.mDurationPb.setMax(duration);

				//MC - Calculate the current progress of the ProgressBar and update.
				int initialProgress = 0;
				long difference = 0;
				try {
					difference = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
				} 
				catch (ParseException e) {
					e.printStackTrace();
				}

				if (difference < 0) {
					holder.mDurationPb.setVisibility(View.GONE);
					initialProgress = 0;
					holder.mDurationPb.setProgress(0);
				} 
				else {
					try {
						initialProgress = (int) DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime()) / (1000 * 60);
					} 
					catch (ParseException e) {
						e.printStackTrace();
					}
					holder.mTimeleftTv.setText(duration-initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + 
							" " + mActivity.getResources().getString(R.string.left));
					holder.mDurationPb.setProgress(initialProgress);
					holder.mDurationPb.setVisibility(View.VISIBLE);
				}
			}

			//MC - Set the begin time of the broadcast.
			try {
				holder.mTimeTv.setText(DateUtilities.isoStringToTimeString(broadcast.getBeginTime()));
			} 
			catch (ParseException e) {
				e.printStackTrace();
				holder.mTimeTv.setText("");
			}

			// TODO: manipulate icon

			//MC - Set the title of the broadcast.
			String title = broadcast.getProgram().getTitle();
			if (title != null) {
				holder.mTitleTv.setText(title);
			} 
			else {
				holder.mTitleTv.setText("");
			}
			
			//MC - Show the correct program type specific tags.
			
			//TODO: Figure out how tags for the different broadcast types are constructed.
			String type = broadcast.getProgram().getProgramType();
			if (type != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(type)) {
					ArrayList<String> tags = broadcast.getProgram().getTags();
					holder.mDescTv.setText(tags.get(2) + " " + mActivity.getResources().getString(R.string.from) + " " +
							broadcast.getProgram().getYear());
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(type)) {
					holder.mDescTv.setText(mActivity.getResources().getString(R.string.season) + " " + 
							broadcast.getProgram().getSeason().getNumber() + " " + 
							mActivity.getResources().getString(R.string.episode) + " " +
							String.valueOf(broadcast.getProgram().getEpisodeNumber()));
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(type)) {
					ArrayList<String> tags = broadcast.getProgram().getTags();
					holder.mDescTv.setText(tags.get(tags.size()-1));
				}
				else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(type)) {
					ArrayList<String> tags = broadcast.getProgram().getTags();
					holder.mDescTv.setText(tags.get(tags.size()-1));
				}
			}
			else {
				holder.mDescTv.setText("");
			}
		} 
		else {
			holder.mTimeTv.setText("");
			holder.mTitleTv.setText("");
			holder.mDescTv.setText("");
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

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		Broadcast broadcast = getItem(position);
		long timeSinceBegin = 0;
		long timeToEnd = 0;
		try {
			timeSinceBegin = DateUtilities.getAbsoluteTimeDifference(broadcast.getBeginTime());
			timeToEnd = DateUtilities.getAbsoluteTimeDifference(broadcast.getEndTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (timeSinceBegin > 0 && timeToEnd < 0) {
			return 0;
		}
		return 1;
	}

	static class ViewHolder {
		TextView	mTimeTv;
		ImageView	mIconIv;
		TextView	mTitleTv;
		TextView	mDescTv;

		ProgressBar mIconPb;
		ProgressBar mDurationPb;
		TextView	mTimeleftTv;
	}
}
