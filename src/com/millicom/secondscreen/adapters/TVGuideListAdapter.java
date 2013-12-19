package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.ChannelPageActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class TVGuideListAdapter extends BaseAdapter {

	private static final String	TAG	= "TVGuideListAdapter";

	private LayoutInflater		mLayoutInflater;
	private Activity			mActivity;
	private ArrayList<Guide>	mGuide;
	private TvDate				mDate;
	private int					mIndexOfNearestBroadcast;
	private int					mHour, mCurrentHour;
	private boolean				mIsToday;

	public TVGuideListAdapter(Activity activity, ArrayList<Guide> guide, TvDate date, int hour, boolean isToday) {
		this.mGuide = guide;
		this.mActivity = activity;
		this.mDate = date;
		this.mHour = hour;
		this.mIsToday = isToday;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.v("TVGuideListAdapter.getView:", "at position "+position);
		View rowView = convertView;
		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.row_tvguide_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.item_container);
			viewHolder.mChannelIconIv = (ImageView) rowView.findViewById(R.id.tvguide_channel_iv);
			// viewHolder.mBroadcastItemLl = (LinearLayout) rowView.findViewById(R.id.tvguide_program_list_container);


			// viewHolder.mLiveProgramContainer = (RelativeLayout) container;
			viewHolder.mLiveProgramTimeTv = (TextView) rowView.findViewById(R.id.tvguide_program_line_live);
			viewHolder.mLiveProgramTimeTv.setSingleLine(false);
//			viewHolder.mLiveProgramNameTv = (TextView) container.findViewById(R.id.tvguide_program_line_name_tv);
			
			/*
			container = rowView.findViewById(R.id.tvguide_program_line_next_container);
			// viewHolder.mNextProgramContainer = (RelativeLayout) container;
			viewHolder.mNextProgramTimeTv = (TextView) container.findViewById(R.id.tvguide_program_line_time_tv);
			viewHolder.mNextProgramNameTv = (TextView) container.findViewById(R.id.tvguide_program_line_name_tv);
			
			container = rowView.findViewById(R.id.tvguide_program_line_last_container);
			// viewHolder.mLastProgramContainer = (RelativeLayout) container;
			viewHolder.mLastProgramTimeTv = (TextView) container.findViewById(R.id.tvguide_program_line_time_tv);
			viewHolder.mLastProgramNameTv = (TextView) container.findViewById(R.id.tvguide_program_line_name_tv);
*/
			rowView.setTag(viewHolder);
		}
		ViewHolder holder = (ViewHolder) rowView.getTag();

		final Guide guide = getItem(position);

		if (guide.getLogoLHref() != null) {
			Log.v("TVGuideListAdapter:", "Calling displayImage");
//			ImageAware imageAware = new ImageViewAware(holder.mChannelIconIv, false);
//			ImageLoader.getInstance().displayImage(guide.getLogoSHref(), imageAware);
			ImageLoader.getInstance().displayImage(guide.getLogoSHref(), holder.mChannelIconIv);
		   
		} else {
			holder.mChannelIconIv.setImageResource(R.color.white);
		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mActivity, ChannelPageActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, guide.getId());
				intent.putExtra(Consts.INTENT_EXTRA_CHOSEN_DATE_TVGUIDE, mDate);
				intent.putExtra(Consts.INTENT_EXTRA_TV_GUIDE_HOUR, mHour);

				mActivity.startActivity(intent);
				mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});

		ArrayList<Broadcast> broadcasts = guide.getBroadcasts();

		String stringIconMovie = mActivity.getResources().getString(R.string.icon_movie) + " ";
		String stringIconLive = mActivity.getResources().getString(R.string.icon_live) + " ";
		String textForThreeBroadcasts = "";
		int textIndexToMarkAsOngoing =0;

		if (broadcasts != null && broadcasts.size() > 0) {
			/* get the nearest broadcasts */

			mIndexOfNearestBroadcast = guide.getClosestBroadcastIndexFromTime(broadcasts, mHour, mDate);

			if (mIndexOfNearestBroadcast != -1) {
				ArrayList<Broadcast> nextBroadcasts = Broadcast.getBroadcastsStartingFromPosition(mIndexOfNearestBroadcast, broadcasts, Consts.TV_GUIDE_NEXT_PROGRAMS_NUMBER);
			
				
				for (int j = 0; j < Math.min(nextBroadcasts.size(), 3); j++) {
					
					
					Broadcast broadcast = nextBroadcasts.get(j);
					Program program = broadcast.getProgram();
					String programType = program.getProgramType();
					String broadcastType = broadcast.getBroadcastType();
					
					String rowInfo = broadcast.getBeginTimeStringLocalHourAndMinute();
					rowInfo += "   ";
					
					
					
					
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
						rowInfo += stringIconMovie;
					} else if (Consts.DAZOO_BROADCAST_TYPE_LIVE.equals(broadcastType)) {
						rowInfo += stringIconLive;
					}
					
					
					String tmpString = null;
	                if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
	                	tmpString = program.getSeries().getName();
                    } else {
                    	tmpString = program.getTitle();
                    }
					
					//TODO: handle the title from series which should be read from broadcast not from program
					rowInfo +=  tmpString;
					
					
					//erik, make sure to mark the first line red if it is ongoing
					if (broadcast.isRunning()) {
						textIndexToMarkAsOngoing = rowInfo.length();
					}
					
					//TODO: make sure that we append "..." to the end and concatinate the text to match the width of the screen
					textForThreeBroadcasts +=rowInfo + "\n";
					
				}
				
				
				Spannable wordtoSpan = new SpannableString(textForThreeBroadcasts);        
				//TDOD: translate into the proper dazoo color codes
				wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, textIndexToMarkAsOngoing, 0);
				wordtoSpan.setSpan(new ForegroundColorSpan(Color.GRAY), textIndexToMarkAsOngoing+1, wordtoSpan.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			    
				holder.mLiveProgramTimeTv.setText(wordtoSpan, TextView.BufferType.SPANNABLE);
			

			}
		}
		
		return rowView;
	}

	static class ViewHolder {
		public RelativeLayout	mContainer;
		public ImageView		mChannelIconIv;
		public ProgressBar		mProgressBar;
		// public LinearLayout mBroadcastItemLl;

		// LIVE PROGRAM
		// public RelativeLayout mLiveProgramContainer;
		public TextView			mLiveProgramNameTv;
		public TextView			mLiveProgramTimeTv;

		// NEXT PROGRAM
		// public RelativeLayout mNextProgramContainer;
		public TextView			mNextProgramNameTv;
		public TextView			mNextProgramTimeTv;

		// LAST PROGRAM
		// public RelativeLayout mLastProgramContainer;
		public TextView			mLastProgramNameTv;
		public TextView			mLastProgramTimeTv;

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

	public void refreshList(int selectedHour) {
		mHour = selectedHour;

		SecondScreenApplication.getInstance().setSelectedHour(mHour);
		notifyDataSetChanged();
	}
}
