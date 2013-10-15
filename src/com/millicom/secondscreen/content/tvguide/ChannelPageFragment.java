package com.millicom.secondscreen.content.tvguide;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.ChannelPageListAdapter;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ChannelPageFragment extends Fragment{
	
	private static final String		TAG	= "ChannelPageFragment";

	private ActionBar				mActionBar;
	private ImageView				mChannelIconIv, mChannelBroadcastLiveIv;
	private ProgressBar				mChannelBroadcastLiveIvPrB, mDurationProgressBar;
	private TextView				mBroadcastLiveTimeTv, mBroadcastLiveTitleTv, mBroadcastLiveTextTv;
	private ListView				mFollowingBroadcastsLv;
	private ChannelPageListAdapter	mFollowingBroadcastsListAdapter;

	private Guide					mChannelGuide;
	private Channel					mChannel;
	private ArrayList<Broadcast>	mBroadcasts, mFollowingBroadcasts;

	String closestBroadcastStartTime, closestBroadcastEndTime;
	int duration = 0;
	private View mRootView;
	
	private ImageLoader				mImageLoader;
	private Activity mActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		mImageLoader = new ImageLoader(getActivity(), R.drawable.loadimage_2x);

		// get the info about the individual channel guide to be displayed from tv-guide listview
		Bundle bundle = getArguments();
		mChannelGuide = bundle.getParcelable(Consts.INTENT_EXTRA_CHANNEL_GUIDE);
		mChannel = bundle.getParcelable(Consts.INTENT_EXTRA_CHANNEL);
		mBroadcasts = mChannelGuide.getBroadcasts();
		
		getActivity().getActionBar().setTitle("ChannelPage");
		
	}
	
	@Override
	  public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    
	  }

	  @Override
	  public void onDetach() {
	    super.onDetach();
	  }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_tvguide_tag_fragment, null);
		
		mChannelIconIv = (ImageView) mRootView.findViewById(R.id.channelpage_channel_icon_iv);
		mChannelBroadcastLiveIv = (ImageView) mRootView.findViewById(R.id.channelpage_broadcast_iv);
		mChannelBroadcastLiveIvPrB = (ProgressBar) mRootView.findViewById(R.id.channelpage_broadcast_iv_progressbar);

		mBroadcastLiveTimeTv = (TextView) mRootView.findViewById(R.id.channelpage_broadcast_details_time_tv);
		mBroadcastLiveTitleTv = (TextView) mRootView.findViewById(R.id.channelpage_broadcast_details_title_tv);
		mDurationProgressBar = (ProgressBar) mRootView.findViewById(R.id.channelpage_broadcast_details_progressbar);
		mBroadcastLiveTextTv = (TextView) mRootView.findViewById(R.id.channelpage_broadcast_details_text_tv);

		mFollowingBroadcastsLv = (ListView) mRootView.findViewById(R.id.listview);
		
		populateViews();
		return mRootView;
	}
		
	private void populateViews() {
		mImageLoader.displayImage(mChannelGuide.getLogoLHref(), mChannelIconIv, ImageLoader.IMAGE_TYPE.POSTER);

		final int indexOfNearestBroadcast = Broadcast.getClosestBroadcastIndex(mBroadcasts);
		if (indexOfNearestBroadcast >= 0) {

			mFollowingBroadcasts = Broadcast.getBroadcastsStartingFromPosition(indexOfNearestBroadcast + 1, mBroadcasts, mBroadcasts.size());

			mImageLoader.displayImage(mBroadcasts.get(indexOfNearestBroadcast).getProgram().getPosterLUrl(), mChannelBroadcastLiveIv, mChannelBroadcastLiveIvPrB, ImageLoader.IMAGE_TYPE.LANDSCAPE);
			try {
				
				mBroadcastLiveTimeTv.setText(DateUtilities.isoStringToTimeString(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime()));
				closestBroadcastStartTime = mBroadcasts.get(indexOfNearestBroadcast).getBeginTime();
				closestBroadcastEndTime = mBroadcasts.get(indexOfNearestBroadcast).getEndTime();
				
				duration = Math.abs(DateUtilities.getDifferenceInMinutes(closestBroadcastEndTime, closestBroadcastStartTime));
				Log.d(TAG,"Duration: " + duration);
				
			} catch (ParseException e) {
				e.printStackTrace();
				mBroadcastLiveTimeTv.setText("");
			}
			mBroadcastLiveTitleTv.setText(mBroadcasts.get(indexOfNearestBroadcast).getProgram().getTitle());

			//int duration = mBroadcasts.get(indexOfNearestBroadcast).getDurationInMinutes();
			
			mDurationProgressBar.setMax(duration);

			int initialProgress = 0;
			long difference = 0;

			try {
				difference = DateUtilities.getAbsoluteTimeDifference(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			if (difference < 0) {
				mDurationProgressBar.setVisibility(View.GONE);
				initialProgress = 0;
				mDurationProgressBar.setProgress(0);
			} else {
				try {
					initialProgress = DateUtilities.getDifferenceInMinutes(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mDurationProgressBar.setProgress(initialProgress);
				mDurationProgressBar.setVisibility(View.VISIBLE);
			}

			// update progress bar value every minute
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						if (DateUtilities.getDifferenceInMinutes(mBroadcasts.get(indexOfNearestBroadcast).getBeginTime()) > 0) {
							mDurationProgressBar.setVisibility(View.VISIBLE);
							mDurationProgressBar.incrementProgressBy(1);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					handler.postDelayed(this, 60 * 1000);
				}
			}, 60 * 1000);

			mBroadcastLiveTextTv.setText(mBroadcasts.get(indexOfNearestBroadcast).getProgram().getSynopsisShort());

			mFollowingBroadcastsListAdapter = new ChannelPageListAdapter(getActivity(), mFollowingBroadcasts);
			mFollowingBroadcastsLv.setAdapter(mFollowingBroadcastsListAdapter);

			mFollowingBroadcastsLv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

					//// open the detail view for the individual broadcast
					//Intent intent = new Intent(ChannelPageActivity.this, BroadcastPageActivity.class);
					//intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_BROADCAST, mFollowingBroadcasts.get(position));
					//startActivity(intent);
					//overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				}
			});
		}
	}
	
	
}
