package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
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

	private static final int SCREEN_WIDTH_SMALL 	= 240;
	private static final int SCREEN_WIDTH_MEDIUM 	= 320;
	private static final int SCREEN_WIDTH_LARGE 	= 480;
	private static final int SCREEN_WIDTH_X_LARGE 	= 720;
	private static final int SCREEN_WIDTH_X_X_LARGE = 1080;
	private static final String	TAG	= "TVGuideListAdapter";

	private LayoutInflater		mLayoutInflater;
	private Activity			mActivity;
	private ArrayList<Guide>	mGuide;
	private TvDate				mDate;
	private int					mIndexOfNearestBroadcast;
	private int					mHour, mCurrentHour;
	private boolean				mIsToday;
	private int 				maxRowLenght;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public TVGuideListAdapter(Activity activity, ArrayList<Guide> guide, TvDate date, int hour, boolean isToday) {
		this.mGuide = guide;
		this.mActivity = activity;
		this.mDate = date;
		this.mHour = hour;
		this.mIsToday = isToday;
		
		Display display = activity.getWindowManager().getDefaultDisplay();
		int screenWidth;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
		} else {
			screenWidth = display.getWidth();
		}
		
		
		int baseRowLenght = 22;
		
		float scalingFactorOfScreenFactor = 0.8f;
		float screenFactor = ((float)screenWidth/(float)SCREEN_WIDTH_X_X_LARGE);
		
		/* Decrease the magnitude which the scaling is done */
		screenFactor /= scalingFactorOfScreenFactor;
		
		int extraRowLenght = (int)(screenFactor*15.0f);
		maxRowLenght = baseRowLenght + extraRowLenght;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.row_tvguide_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.item_container);
			viewHolder.mChannelIconIv = (ImageView) rowView.findViewById(R.id.tvguide_channel_iv);
			viewHolder.mTextView = (TextView) rowView.findViewById(R.id.tvguide_program_line_live);

			rowView.setTag(viewHolder);
		}
		ViewHolder holder = (ViewHolder) rowView.getTag();

		final Guide guide = getItem(position);

		if (guide.getLogoLHref() != null) {
			ImageAware imageAware = new ImageViewAware(holder.mChannelIconIv, false);
			ImageLoader.getInstance().displayImage(guide.getLogoSHref(), imageAware);
		   
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
		int textIndexToMarkAsOngoing = 0;

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
					
					boolean isMovie = false;
					boolean isLive = false;
					if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)) {
						rowInfo += stringIconMovie;
						isMovie = true;
					} else if (Consts.DAZOO_BROADCAST_TYPE_LIVE.equals(broadcastType)) {
						rowInfo += stringIconLive;
						isLive = true;
					}
					
					
					String showName = null;
	                if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
	                	showName = program.getSeries().getName();
                    } else {
                    	showName = program.getTitle();
                    }
					
					rowInfo += showName;
					
					String ellipsisString = "...";
					
					int rowLenght = rowInfo.length();
					
					if(isMovie) {
						/* The 'movie'-char is three times as wide as other chars */
						rowLenght += 2; 
					} else if(isLive) {
						/* The 'live'-char is five times as wide as other chars */
						rowLenght += 4;
					}
					
					if(rowLenght > maxRowLenght) {
						rowInfo = rowInfo.replace(rowInfo.substring(maxRowLenght-3, rowInfo.length()), ellipsisString);
					}
					
					if (broadcast.isRunning()) {
						textIndexToMarkAsOngoing = rowInfo.length();
					}
					
					textForThreeBroadcasts += rowInfo + "\n";
				}
				
				
				Spannable wordtoSpan = new SpannableString(textForThreeBroadcasts);     
				
				Resources resources = SecondScreenApplication.getInstance().getApplicationContext().getResources();
				if(textIndexToMarkAsOngoing > 0) {
					wordtoSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.red)), 0, textIndexToMarkAsOngoing, 0);
				}
				wordtoSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.grey3)), textIndexToMarkAsOngoing+1, wordtoSpan.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			    
				holder.mTextView.setText(wordtoSpan, TextView.BufferType.SPANNABLE);
			

			}
		}
		
		return rowView;
	}

	static class ViewHolder {
		public RelativeLayout	mContainer;
		public ImageView		mChannelIconIv;
		public TextView			mTextView;
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
