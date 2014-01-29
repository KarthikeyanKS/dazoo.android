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

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.model.Broadcast;
import com.mitv.model.TvDate;
import com.mitv.tvguide.BroadcastPageActivity;
import com.mitv.utilities.ProgressBarUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class TVGuideTagListAdapter extends AdListAdapter<Broadcast> {

	private static final String		TAG	= "TVGuideListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mTaggedBroadcasts;
	private int						mCurrentPosition;
	private TvDate					mDate;

	public TVGuideTagListAdapter(Activity activity, String fragmentName, ArrayList<Broadcast> taggedBroadcasts, int currentPosition, TvDate date) {
		super(fragmentName, activity, taggedBroadcasts);
		this.mTaggedBroadcasts = taggedBroadcasts;
		this.mActivity = activity;
		this.mCurrentPosition = currentPosition;
		this.mDate = date;
	}
	
	@Override
	public int getViewTypeCount() {
		return super.getViewTypeCount() + 1;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/* Superclass AdListAdapter will create view if this is a position of an ad. */
		View rowView = super.getView(position, convertView, parent);
		
		if(rowView == null) {
			rowView = getViewForBroadCastCell(position, convertView, parent);
		}
		
		return rowView;
	}
	
	public View getViewForBroadCastCell(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		// get the item with the displacement depending on the scheduled time on air
		int indexForBroadcast = mCurrentPosition + position;
		if (indexForBroadcast >= 0) {
			final Broadcast broadcast = getItem(indexForBroadcast);

			if (rowView == null) {
				mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = mLayoutInflater.inflate(R.layout.element_poster_broadcast, null);
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

			if (broadcast != null) {
				// If on air
				if (broadcast.isRunning()) {
					ProgressBarUtils.setupProgressBar(mActivity, broadcast, holder.mDurationPb, holder.mTimeLeftTv);
				} else {
					holder.mDurationPb.setVisibility(View.GONE);
					holder.mTimeLeftTv.setVisibility(View.GONE);
				}
				
				ImageAware imageAware = new ImageViewAware(holder.mImageIv, false);
				ImageLoader.getInstance().displayImage(broadcast.getProgram().getPortMUrl(), imageAware);
				

				holder.mTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());
				holder.mChannelTv.setText(broadcast.getChannel().getName());

				String type = broadcast.getProgram().getProgramType();
				if (type != null) {
					if (Consts.PROGRAM_TYPE_MOVIE.equals(type)) {
						holder.mTitleTv.setText(mActivity.getResources().getString(R.string.icon_movie) + " "+ broadcast.getProgram().getTitle());
						holder.mDescTv.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
					} else if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(type)) {
						String season = broadcast.getProgram().getSeason().getNumber();
						int episode = broadcast.getProgram().getEpisodeNumber();
						String seasonEpisode = "";
						if (!season.equals("0")) {
							seasonEpisode += mActivity.getResources().getString(R.string.season) + " " + season + " ";
						}
						if (episode > 0) {
							seasonEpisode += mActivity.getResources().getString(R.string.episode) + " " + episode;
						}
						holder.mDescTv.setText(seasonEpisode);
						holder.mTitleTv.setText(broadcast.getProgram().getSeries().getName());
					} else if (Consts.PROGRAM_TYPE_SPORT.equals(type)) {
						
						if (Consts.BROADCAST_TYPE_LIVE.equals(broadcast.getBroadcastType())) {
							holder.mTitleTv.setText(mActivity.getResources().getString(R.string.icon_live) + " " + broadcast.getProgram().getTitle());
						} else {
							holder.mTitleTv.setText(broadcast.getProgram().getTitle());
						}
						
						holder.mDescTv.setText(broadcast.getProgram().getSportType().getName() + ": " + broadcast.getProgram().getTournament());
					} else if (Consts.PROGRAM_TYPE_OTHER.equals(type)) {
						holder.mTitleTv.setText(broadcast.getProgram().getTitle());
						holder.mDescTv.setText(broadcast.getProgram().getCategory());
					}
				} else {
					holder.mDescTv.setText("");
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

					Log.d(TAG, broadcast.getProgram().toString());

					Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillisGmt());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
					intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, mDate.getDate());

					mActivity.startActivity(intent);
				}
			});
		} else {
			Log.w("TVGuideTagListAdapter", "Warning: index was -1, returning convert view, or what else should we return here?!");
		}
		return rowView;
	}

	static class ViewHolder {
		RelativeLayout	mContainer;
		ImageView		mImageIv;
		TextView		mTitleTv;
		TextView		mTimeTv;
		TextView		mChannelTv;
		TextView		mDescTv;
		TextView		mTimeLeftTv;
		ProgressBar		mDurationPb;
	}

	@Override
	public int getCount() {
		if (mTaggedBroadcasts != null) {
			return mTaggedBroadcasts.size() - mCurrentPosition;
		} else {
			return 0;
		}
	}

	@Override
	public Broadcast getItem(int position) {
		if (mTaggedBroadcasts != null) {
			return mTaggedBroadcasts.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}
}
