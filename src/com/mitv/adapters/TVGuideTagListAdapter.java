package com.mitv.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVDate;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.utilities.ProgressBarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class TVGuideTagListAdapter extends AdListAdapter<TVBroadcastWithChannelInfo> {

	private static final String TAG = TVGuideTagListAdapter.class.getName();

	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	private int currentPosition;

	public TVGuideTagListAdapter(Activity activity, String fragmentName, ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts, int currentPosition) {
		super(fragmentName, activity, taggedBroadcasts);
		this.taggedBroadcasts = taggedBroadcasts;
		this.activity = activity;
		this.currentPosition = currentPosition;
	}

	@Override
	public int getViewTypeCount() {
		return super.getViewTypeCount() + 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/* Superclass AdListAdapter will create view if this is a position of an ad. */
		View rowView = super.getView(position, convertView, parent);

		if (rowView == null) {
			rowView = getViewForBroadCastCell(position, convertView, parent);
		}

		return rowView;
	}

	public View getViewForBroadCastCell(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		// get the item with the displacement depending on the scheduled time on air
		int indexForBroadcast = currentPosition + position;
		if (indexForBroadcast >= 0) {
			final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(indexForBroadcast);

			if (rowView == null) {
				layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = layoutInflater.inflate(R.layout.element_poster_broadcast, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.element_poster_broadcast_container);
				viewHolder.mImageIv = (ImageView) rowView.findViewById(R.id.element_poster_broadcast_image_iv);
				viewHolder.mTitleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_title_tv);
				viewHolder.mTimeTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_time_tv);
				viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_channel_tv);
				viewHolder.mDescTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_type_tv);
				viewHolder.mTimeLeftTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_timeleft_tv);
				viewHolder.mDurationPb = (ProgressBar) rowView.findViewById(R.id.element_poster_broadcast_progressbar);

				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();

			if (broadcastWithChannelInfo != null) {
				// If on air
				if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) {
					ProgressBarUtils.setupProgressBar(activity, broadcastWithChannelInfo, holder.mDurationPb, holder.mTimeLeftTv);
				} else {
					holder.mDurationPb.setVisibility(View.GONE);
					holder.mTimeLeftTv.setVisibility(View.GONE);
				}

				ImageAware imageAware = new ImageViewAware(holder.mImageIv, false);
				ImageLoader.getInstance().displayImage(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);

				holder.mTimeTv.setText(broadcastWithChannelInfo.getBeginTimeStringLocalHourAndMinute());
				holder.mChannelTv.setText(broadcastWithChannelInfo.getChannel().getName());

				ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();

				switch (programType) {
				case MOVIE: {
					holder.mTitleTv.setText(activity.getResources().getString(R.string.icon_movie) + " " + broadcastWithChannelInfo.getProgram().getTitle());
					holder.mDescTv.setText(broadcastWithChannelInfo.getProgram().getGenre() + " " + broadcastWithChannelInfo.getProgram().getYear());
					break;
				}
				case TV_EPISODE: {
					String season = broadcastWithChannelInfo.getProgram().getSeason().getNumber().toString();
					int episode = broadcastWithChannelInfo.getProgram().getEpisodeNumber();
					String seasonEpisode = "";
					if (!season.equals("0")) {
						seasonEpisode += activity.getResources().getString(R.string.season) + " " + season + " ";
					}
					if (episode > 0) {
						seasonEpisode += activity.getResources().getString(R.string.episode) + " " + episode;
					}
					holder.mDescTv.setText(seasonEpisode);
					holder.mTitleTv.setText(broadcastWithChannelInfo.getProgram().getSeries().getName());
					break;
				}
				case SPORT: {
					if (Consts.BROADCAST_TYPE_LIVE.equals(broadcastWithChannelInfo.getBroadcastType())) {
						holder.mTitleTv.setText(activity.getResources().getString(R.string.icon_live) + " " + broadcastWithChannelInfo.getProgram().getTitle());
					} else {
						holder.mTitleTv.setText(broadcastWithChannelInfo.getProgram().getTitle());
					}

					holder.mDescTv.setText(broadcastWithChannelInfo.getProgram().getSportType().getName() + ": " + broadcastWithChannelInfo.getProgram().getTournament());
					break;
				}
				case OTHER: {
					holder.mTitleTv.setText(broadcastWithChannelInfo.getProgram().getTitle());
					holder.mDescTv.setText(broadcastWithChannelInfo.getProgram().getCategory());
					break;
				}
				default: {
					holder.mDescTv.setText("");
					break;
				}
				}
			} else {
				holder.mTitleTv.setText("");
				holder.mTimeTv.setText("");
				holder.mChannelTv.setText("");
				holder.mDescTv.setText("");
			}

			holder.mContainer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					activity.startActivity(intent);
				}
			});
		} else {
			/* No content */
			rowView = LayoutInflater.from(activity).inflate(R.layout.no_data, null);
		}
		return rowView;
	}

	static class ViewHolder {
		RelativeLayout mContainer;
		ImageView mImageIv;
		TextView mTitleTv;
		TextView mTimeTv;
		TextView mChannelTv;
		TextView mDescTv;
		TextView mTimeLeftTv;
		ProgressBar mDurationPb;
	}

	@Override
	public int getCount() {
		if (taggedBroadcasts != null) {
			return taggedBroadcasts.size() - currentPosition;
		} else {
			return 0;
		}
	}

	@Override
	public TVBroadcastWithChannelInfo getItem(int position) {
		if (taggedBroadcasts != null) {
			return taggedBroadcasts.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}
}
