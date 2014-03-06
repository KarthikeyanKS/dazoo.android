package com.mitv.listadapters;

import java.util.ArrayList;

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

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.BroadcastPageActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.utilities.LanguageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class PopularListAdapter extends BaseAdapter {

	public static final String TAG = PopularListAdapter.class.getName();

	private LayoutInflater layoutInflater;
	private Activity activity;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;


	public PopularListAdapter(Activity activity, ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts) {
		this.activity = activity;
		this.popularBroadcasts = popularBroadcasts;
		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (popularBroadcasts != null) {
			return popularBroadcasts.size();
		} else
			return 0;
	}

	@Override
	public TVBroadcastWithChannelInfo getItem(int position) {
		if (popularBroadcasts != null) {
			return popularBroadcasts.get(position);
		} else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final TVBroadcastWithChannelInfo broadcastWithChannelInfo = getItem(position);

		View rowView = convertView;
		if (rowView == null) {
			rowView = layoutInflater.inflate(R.layout.element_poster_broadcast, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.headerContainer = (RelativeLayout) rowView.findViewById(R.id.element_poster_broadcast_header_container);
			viewHolder.headerTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_header_tv);
			viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.element_poster_broadcast_container);
			viewHolder.posterIv = (ImageView) rowView.findViewById(R.id.element_poster_broadcast_image_iv);
			viewHolder.titleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_title_tv);
			viewHolder.timeTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_time_tv);
			viewHolder.channelNameTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_channel_tv);
			viewHolder.detailsTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_type_tv);
			viewHolder.progressBarTitleTv = (TextView) rowView.findViewById(R.id.element_poster_broadcast_timeleft_tv);
			viewHolder.progressBar = (ProgressBar) rowView.findViewById(R.id.element_poster_broadcast_progressbar);

			viewHolder.titleTv.setTag(Integer.valueOf(position));
			Log.d(TAG, "set tag: " + Integer.valueOf(position));

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		if (broadcastWithChannelInfo != null) {

			holder.headerContainer.setVisibility(View.GONE);
			if (position == 0
					|| broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString().equals((getItem(position - 1)).getBeginTimeDayAndMonthAsString()) == false) {
				holder.headerTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString() + " "
						+ broadcastWithChannelInfo.getBeginTimeDayAndMonthAsString());
				holder.headerContainer.setVisibility(View.VISIBLE);

			}

			holder.container.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					/* Go to the corresponding Broadcast page */
					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					// TODO NewArc set return activity?? For detecting tab
					// coloring
					activity.startActivity(intent);

				}
			});

			ImageAware imageAware = new ImageViewAware(holder.posterIv, false);
			ImageLoader.getInstance().displayImage(broadcastWithChannelInfo.getProgram().getImages().getPortrait().getMedium(), imageAware);

			holder.timeTv.setText(broadcastWithChannelInfo.getBeginTimeDayOfTheWeekWithHourAndMinuteAsString());

			holder.channelNameTv.setText(broadcastWithChannelInfo.getChannel().getName());

			if (broadcastWithChannelInfo.isBroadcastCurrentlyAiring()) {
				LanguageUtils.setupProgressBar(activity, broadcastWithChannelInfo, holder.progressBar, holder.progressBarTitleTv);
			} else {
				holder.progressBar.setVisibility(View.GONE);
				holder.progressBarTitleTv.setVisibility(View.GONE);
			}

			// different details about the broadcast program depending on the
			// type

			ProgramTypeEnum programType = broadcastWithChannelInfo.getProgram().getProgramType();

			if (programType == ProgramTypeEnum.TV_EPISODE) {
				holder.titleTv.setText(broadcastWithChannelInfo.getProgram().getSeries().getName());
			} else {
				holder.titleTv.setText(broadcastWithChannelInfo.getProgram().getTitle());
			}

			switch (programType) {
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
				holder.detailsTv.setText(seasonEpisode);
				break;
			}
			case MOVIE: {
				holder.detailsTv.setText(broadcastWithChannelInfo.getProgram().getGenre() + " " + broadcastWithChannelInfo.getProgram().getYear());
				break;
			}
			case SPORT: {
				holder.detailsTv.setText(broadcastWithChannelInfo.getProgram().getSportType().getName() + " "
						+ broadcastWithChannelInfo.getProgram().getTournament());
				break;
			}
			case OTHER: {
				holder.detailsTv.setText(broadcastWithChannelInfo.getProgram().getCategory());
				break;
			}
			default: {
				break;
			}
			}

		
			holder.container.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(broadcastWithChannelInfo);
					Intent intent = new Intent(activity, BroadcastPageActivity.class);
					activity.startActivity(intent);
				}
			});
		}
		return rowView;
	}

	public static class ViewHolder {
		RelativeLayout headerContainer;
		TextView headerTv;
		RelativeLayout container;
		ImageView posterIv;
		ProgressBar imageProgressBar;
		TextView titleTv;
		TextView timeTv;
		TextView channelNameTv;
		TextView detailsTv;
		TextView progressBarTitleTv;
		ProgressBar progressBar;
	}
}
