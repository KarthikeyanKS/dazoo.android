package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.utilities.DateUtilities;


//import com.millicom.secondscreen.Consts;
//import com.millicom.secondscreen.R;
//import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
//import com.millicom.secondscreen.content.model.Broadcast;
//import com.millicom.secondscreen.content.model.Guide;
//import com.millicom.secondscreen.utilities.DateUtilities;
//import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UpcomingEpisodesListAdapter extends BaseAdapter {

	private static final String		TAG				= "UpcomingEpisodesListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<Broadcast>	mFollowingEpisodes;


	public UpcomingEpisodesListAdapter(Activity activity, ArrayList<Broadcast> followingBroadcasts) {
		this.mFollowingEpisodes = followingBroadcasts;
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		if (mFollowingEpisodes != null) {
			return mFollowingEpisodes.size();
		} else return 0;
	}

	@Override
	public Broadcast getItem(int position) {
		if (mFollowingEpisodes != null) {
			return mFollowingEpisodes.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Broadcast broadcast = getItem(position);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				rowView = mLayoutInflater.inflate(R.layout.layout_upcoming_episodes_listitem, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mSeasonEpisodeTv = (TextView) rowView.findViewById(R.id.upcoming_episodes_listitem_season_episode);
				viewHolder.mTitleTimeTv = (TextView) rowView.findViewById(R.id.upcoming_episodes_listitem_title_time);
				viewHolder.mChannelTv = (TextView) rowView.findViewById(R.id.upcoming_episodes_listitem_channel);
//				viewHolder.mReminderIv = (ImageView) rowView.findViewById(R.id.upcoming_episodes_listitem_addreminder);
				
				viewHolder.mDivider = (View) rowView.findViewById(R.id.upcoming_episodes_listitem_bottom_divider);
				
				rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		
		if (broadcast != null) {
			// Set season and episode
			String season = broadcast.getProgram().getSeason().getNumber();
			String episode = broadcast.getProgram().getEpisode();
			if (season != null && episode != null) {
				holder.mSeasonEpisodeTv.setText(mActivity.getResources().getString(R.string.season) + " " + season + " " + 
												mActivity.getResources().getString(R.string.episode) + " " + episode);
			}
			
			//Set the title and begin time of the broadcast.
			String title = broadcast.getProgram().getTitle();
			try {
				holder.mTitleTimeTv.setText(title + " - " + DateUtilities.isoStringToTimeString(broadcast.getBeginTime()));
			} 
			catch (ParseException e) {
				e.printStackTrace();
				holder.mTitleTimeTv.setText("");
			}
			
			//Set channel
			String channel = broadcast.getChannel().getName();
			if (channel != null) {
				holder.mChannelTv.setText(channel);
			}
	
			if (position == (this.getCount() - 1)) {
				holder.mDivider.setVisibility(View.GONE);
			}
			//Placeholder-data
//			holder.mSeasonEpisodeTv.setText("Season 1 Episode 1");
//			holder.mTitleTimeTv.setText("Title - Time");
//			holder.mChannelTv.setText("Channel");
		} 
		else {
			holder.mSeasonEpisodeTv.setText("");
			holder.mTitleTimeTv.setText("");
			holder.mChannelTv.setText("");
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

	static class ViewHolder {
		TextView	mSeasonEpisodeTv;
		TextView	mTitleTimeTv;
		TextView	mChannelTv;
//		ImageView	mReminderIv;
		
		View		mDivider;
	}
}
