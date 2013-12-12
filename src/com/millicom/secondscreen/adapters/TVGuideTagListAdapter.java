package com.millicom.secondscreen.adapters;

import java.text.ParseException;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.content.tvguide.ChannelPageActivity;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

public class TVGuideTagListAdapter extends BaseAdapter {

	private static final String		TAG	= "TVGuideListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mTaggedBroadcasts;
	private ImageLoader				mImageLoader;
	private int						mCurrentPosition;
	private TvDate					mDate;

	public TVGuideTagListAdapter(Activity activity, ArrayList<Broadcast> taggedBroadcasts, int currentPosition, TvDate date) {
		this.mTaggedBroadcasts = taggedBroadcasts;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.color.grey1);
		this.mCurrentPosition = currentPosition;
		this.mDate = date;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
					holder.mDurationPb.setMax(broadcast.getDurationInMinutes());

					// Calculate the current progress of the ProgressBar and update.
					int initialProgress = 0;

					if (broadcast.getTimeToBegin() > 0) {
						holder.mDurationPb.setVisibility(View.GONE);
						initialProgress = 0;
						holder.mDurationPb.setProgress(0);
					} else {
						
						initialProgress =  broadcast.minutesSinceStart();
			
						// different representation of "X min left" for Spanish and all other languages
						if (Locale.getDefault().getLanguage().endsWith("es")) {
							holder.mTimeLeftTv.setText(mActivity.getResources().getString(R.string.left) + " " + String.valueOf(broadcast.getDurationInMinutes() - initialProgress) + " "
									+ mActivity.getResources().getString(R.string.minutes));
						} else {
						holder.mTimeLeftTv.setText(broadcast.getDurationInMinutes() - initialProgress + " " + mActivity.getResources().getString(R.string.minutes) + " "
								+ mActivity.getResources().getString(R.string.left));
						}
						
						holder.mDurationPb.setProgress(initialProgress);
						holder.mDurationPb.setVisibility(View.VISIBLE);
						holder.mTimeLeftTv.setVisibility(View.VISIBLE);
					}
				} else {
					holder.mDurationPb.setVisibility(View.GONE);
					holder.mTimeLeftTv.setVisibility(View.GONE);
				}

				mImageLoader.displayImage(broadcast.getProgram().getPortMUrl(), holder.mImageIv, ImageLoader.IMAGE_TYPE.GALLERY);
				

				holder.mTimeTv.setText(broadcast.getBeginTimeStringLocalHourAndMinute());
				holder.mChannelTv.setText(broadcast.getChannel().getName());

				String type = broadcast.getProgram().getProgramType();
				if (type != null) {
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(type)) {
						//holder.mTitleTv.setText(mActivity.getResources().getString(R.string.icon_movie) + " "+ broadcast.getProgram().getTitle());
						holder.mTitleTv.setText(broadcast.getProgram().getTitle());
						holder.mDescTv.setText(broadcast.getProgram().getGenre() + " " + broadcast.getProgram().getYear());
					} else if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(type)) {
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
					} else if (Consts.DAZOO_PROGRAM_TYPE_SPORT.equals(type)) {
						
						//if (Consts.DAZOO_BROADCAST_TYPE_LIVE.equals(broadcast.getBroadcastType())) {
						//	holder.mTitleTv.setText(mActivity.getResources().getString(R.string.icon_live) + " " + broadcast.getProgram().getTitle());
						//} else {
							holder.mTitleTv.setText(broadcast.getProgram().getTitle());
						//}
						
						holder.mDescTv.setText(broadcast.getProgram().getSportType().getName() + ": " + broadcast.getProgram().getTournament());
					} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(type)) {
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
					mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
