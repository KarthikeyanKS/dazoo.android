package com.millicom.secondscreen.content;

import java.text.ParseException;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import com.millicom.secondscreen.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SSBroadcastBlockPopulator {

	private Activity		mActivity;
	private ImageLoader		mImageLoader;
	private LinearLayout	mContainerView;

	public SSBroadcastBlockPopulator(Activity activity, LinearLayout containerView) {
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
		this.mContainerView = containerView;
	}

	public void createBlock(Broadcast broadcast, Channel mChannel) {
		View topContentView = LayoutInflater.from(mActivity).inflate(R.layout.block_broadcastpage_main_content, null);
		ImageView posterIv = (ImageView) topContentView.findViewById(R.id.block_broadcastpage_poster_iv);
		ProgressBar posterPb = (ProgressBar) topContentView.findViewById(R.id.block_broadcastpage_poster_progressbar);
		TextView titleTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_title_tv);
		TextView seasonTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_season_tv);
		TextView episodeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_episode_tv);
		TextView timeTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_time_tv);
		TextView dateTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_date_tv);
		TextView channelTv = (TextView) topContentView.findViewById(R.id.block_broadcastpage_broadcast_details_channelname_tv);

		Program program = broadcast.getProgram();

		mImageLoader.displayImage(program.getPosterLUrl(), posterIv, posterPb, ImageLoader.IMAGE_TYPE.POSTER);
		titleTv.setText(program.getTitle());
		//seasonTv.setText(program.getSeason().getNumber());
		episodeTv.setText(program.getEpisode());

		String beginTime, endTime;
		try {
			beginTime = DateUtilities.isoStringToTimeString(broadcast.getBeginTime());
			endTime = DateUtilities.isoStringToTimeString(broadcast.getEndTime());
		} catch (ParseException e) {
			beginTime = "";
			endTime = "";
			e.printStackTrace();
		}

		timeTv.setText(beginTime + "-" + endTime);

		String date;
		try {
			date = DateUtilities.isoStringToDayOfWeek(broadcast.getBeginTime());
		} catch (ParseException e) {
			date = "";
			e.printStackTrace();
		}
		dateTv.setText(date);
		// channelTv.setText(mChannel.ge);

		mContainerView.addView(topContentView);

	}

}
