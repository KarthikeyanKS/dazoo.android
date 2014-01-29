package com.mitv.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.model.Broadcast;
import com.mitv.utilities.ProgressBarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class ChannelPageListAdapter extends BaseAdapter {

	private static final String		TAG				= "ChannelPageListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mFollowingBroadcasts;

	private int						mLastPosition	= -1;
	private ViewHolder				holder;

	public ChannelPageListAdapter(Activity activity, ArrayList<Broadcast> followingBroadcasts) {
		this.mFollowingBroadcasts = followingBroadcasts;
		this.mActivity = activity;
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
		broadcast.updateTimeToBeginAndTimeToEnd();
		String broadcastType = broadcast.getBroadcastType();
		String programType = broadcast.getProgram().getProgramType();
		long timeToBegin = broadcast.getTimeToBegin();

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (getItemViewType(position) == 0) {
				rowView = mLayoutInflater.inflate(R.layout.row_channelpage_current_list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_time_tv);
				viewHolder.mIconIv = (ImageView) rowView.findViewById(R.id.channelpage_broadcast_iv);
				viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_title_tv);
				viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.channelpage_broadcast_details_text_tv);
				// MC - The "extra" data fields for the current broadcast.
				viewHolder.mTimeleftTv = (TextView) rowView.findViewById(R.id.channelpage_broadcast_timeleft);
				viewHolder.mDurationPb = (ProgressBar) rowView.findViewById(R.id.channelpage_broadcast_details_progressbar);
				viewHolder.mIconPb = (ProgressBar) rowView.findViewById(R.id.channelpage_broadcast_iv_progressbar);
				rowView.setTag(viewHolder);
			} else {
				rowView = mLayoutInflater.inflate(R.layout.row_channelpage_list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.channelpage_list_item_time_tv);
				viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.channelpage_list_item_title_tv);
				viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.channelpage_list_item_description_tv);
				rowView.setTag(viewHolder);
			}
		}

		holder = (ViewHolder) rowView.getTag();

		if (broadcast != null) {
			if (getItemViewType(position) == 0) {
				// MC - Set the image for current broadcast.
				ImageAware imageAware = new ImageViewAware(holder.mIconIv, false);
				ImageLoader.getInstance().displayImage(broadcast.getProgram().getLandLUrl(), imageAware);
				
				ProgressBarUtils.setupProgressBar(mActivity, broadcast, holder.mDurationPb, holder.mTimeleftTv);
			}

			// MC - Set the begin time of the broadcast.

			holder.mTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());
			String title = broadcast.getProgram().getTitle();
			
			if (programType != null) {
				if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
					holder.mTitleTv.setText(mActivity.getResources().getString(R.string.icon_movie) + " " + title);
					holder.mDescTv.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
				} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
					int season = Integer.parseInt(broadcast.getProgram().getSeason().getNumber());
					int episode = broadcast.getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (season > 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode > 0) {
						seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
					}
					holder.mDescTv.setText(seasonEpisode);
					holder.mTitleTv.setText(broadcast.getProgram().getSeries().getName());
				} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(programType)) {
					if (Consts.DAZOO_BROADCAST_TYPE_LIVE.equals(broadcastType)) {
						holder.mTitleTv.setText(mActivity.getResources().getString(R.string.icon_live) + " " + title);
						holder.mDescTv.setText(broadcast.getProgram().getSportType().getName() + ": " + broadcast.getProgram().getTournament());
					} else {
						holder.mDescTv.setText(broadcast.getProgram().getSportType().getName() + ": " + broadcast.getProgram().getTournament());
						holder.mTitleTv.setText(title);
					}
				} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)) {
					holder.mTitleTv.setText(title);
					holder.mDescTv.setText(broadcast.getProgram().getCategory());
				}
			} else {
				holder.mTitleTv.setText("");
				holder.mDescTv.setText("");
			}
		} else {
			holder.mTimeTv.setText("");
			holder.mTitleTv.setText("");
			holder.mDescTv.setText("");
		}
		
//		if (TextUtils.isEmpty(holder.mDescTv.getText().toString()) == false) {
//			holder.mDescTv.setVisibility(View.VISIBLE);
//		}
//		else {
//			holder.mDescTv.setVisibility(View.GONE);
//		}

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
		if (broadcast.isRunning()) {
			return 0;
		}
		return 1;
	}

	static class ViewHolder {
		TextView	mTimeTv;
		ImageView	mIconIv;
		TextView	mTitleTv;
		TextView	mDescTv;

		ProgressBar	mIconPb;
		ProgressBar	mDurationPb;
		TextView	mTimeleftTv;
	}
}
