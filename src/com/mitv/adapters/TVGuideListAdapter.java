package com.mitv.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.model.Broadcast;
import com.mitv.model.ChannelGuide;
import com.mitv.model.Program;
import com.mitv.model.TvDate;
import com.mitv.tvguide.ChannelPageActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class TVGuideListAdapter extends AdListAdapter<ChannelGuide> {

	private static final String	TAG	= "TVGuideListAdapter";

	private LayoutInflater		mLayoutInflater;
	private Activity			mActivity;
	private ArrayList<ChannelGuide>	mGuide;
	private TvDate				mDate;
	private int					mIndexOfNearestBroadcast;
	private int					mHour, mCurrentHour;
	private boolean				mIsToday;
	private int 				screenWidth;
	private int 				rowWidth = -1;

	@SuppressLint("NewApi")
	public TVGuideListAdapter(Activity activity, ArrayList<ChannelGuide> guide, TvDate date, int hour, boolean isToday) {
		super(Consts.JSON_AND_FRAGMENT_KEY_GUIDE, activity, guide);
		this.mGuide = guide;
		this.mActivity = activity;
		this.mDate = date;
		this.mHour = hour;
		this.mIsToday = isToday;
	}
	
	@Override
	public int getViewTypeCount() {
		return super.getViewTypeCount() + 1;
	}
	
	public View getViewForGuideCell(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (rowView == null) {
			rowView = mLayoutInflater.inflate(R.layout.row_tvguide_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.item_container);
			viewHolder.mImageView = (ImageView) rowView.findViewById(R.id.tvguide_channel_iv);
			viewHolder.mTextView = (TextView) rowView.findViewById(R.id.tvguide_program_line_live);

			rowView.setTag(viewHolder);
		}
		
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (rowWidth < 0) {
			/*
			 * Start the view as invisible, when the height is known, update the
			 * height of each row and change visibility to visible
			 */
			holder.mTextView.setVisibility(View.INVISIBLE);
			holder.mTextView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	
				@Override
				public void onGlobalLayout() {
					// gets called after layout has been done but before it gets displayed, so we can get the height of the view
					int width = holder.mTextView.getWidth();
					TVGuideListAdapter guideListAdapter = ((TVGuideListAdapter) TVGuideListAdapter.this);
					guideListAdapter.setRowWidth(width);
					guideListAdapter.notifyDataSetChanged();

					TVGuideListAdapter.removeOnGlobalLayoutListener(holder.mTextView, this);
					holder.mTextView.setVisibility(View.VISIBLE);
				}
	
			});
		}

		final ChannelGuide guide = getItem(position);

		if (guide.getImageUrl() != null) {
			ImageAware imageAware = new ImageViewAware(holder.mImageView, false);
			ImageLoader.getInstance().displayImage(guide.getImageUrl(), imageAware);
		} else {
			holder.mImageView.setImageResource(R.color.white);
		}

		holder.mContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ChannelPageActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, guide.getId());
				intent.putExtra(Consts.INTENT_EXTRA_CHOSEN_DATE_TVGUIDE, mDate);
				intent.putExtra(Consts.INTENT_EXTRA_TV_GUIDE_HOUR, mHour);
				mActivity.startActivity(intent);
			}
		});

		ArrayList<Broadcast> broadcasts = guide.getBroadcasts();

		String stringIconMovie = mActivity.getResources().getString(R.string.icon_movie) + " ";
		String stringIconLive = mActivity.getResources().getString(R.string.icon_live) + " ";
		String textForThreeBroadcasts = "";
		int textIndexToMarkAsOngoing = 0;
		int textStartIndexToMarkAsOngoing = 0;

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

					if (Consts.PROGRAM_TYPE_MOVIE.equals(programType)) {
						rowInfo += stringIconMovie;
					} else if (Consts.BROADCAST_TYPE_LIVE.equals(broadcastType)) {
						rowInfo += stringIconLive;
					}
					
					String showName = null;
	                if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
	                	showName = program.getSeries().getName();
                    } else {
                    	showName = program.getTitle();
                    }
					
					rowInfo += showName;
					
					TextPaint testPaint = holder.mTextView.getPaint();
					float textWidth = testPaint.measureText(rowInfo);
					int limitIndex = rowInfo.length() - 1;
					
					boolean deletedChars = false;
					
					String toShow = "";
					
					if (rowWidth > 0) {
						int maxTextWidth = (int) (rowWidth * 0.9);
					
						/* Calculate max amount of characters that fits */
						while (textWidth > maxTextWidth && limitIndex > 0) {
							deletedChars = true;
							limitIndex--;
							rowInfo = rowInfo.substring(0, limitIndex);
							textWidth = testPaint.measureText(rowInfo);
						}
						
						String ellipsisString = "...";
						if (deletedChars) {
							rowInfo = rowInfo.replace(rowInfo.substring(limitIndex-3, rowInfo.length()), ellipsisString);
						}
						toShow = rowInfo;
					}

					if (broadcast.isRunning()) {
						textStartIndexToMarkAsOngoing = textForThreeBroadcasts.length();
						textIndexToMarkAsOngoing = textForThreeBroadcasts.length() + toShow.length();
					}
					
					textForThreeBroadcasts += toShow + "\n";
				}
				
				Spannable wordtoSpan = new SpannableString(textForThreeBroadcasts);     
				
				Resources resources = SecondScreenApplication.getInstance().getApplicationContext().getResources();
				if(textIndexToMarkAsOngoing > 0) {
					wordtoSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.red)), textStartIndexToMarkAsOngoing, textIndexToMarkAsOngoing, 0);
				}
				wordtoSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.grey3)), textIndexToMarkAsOngoing+1, wordtoSpan.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			    
				holder.mTextView.setText(wordtoSpan, TextView.BufferType.SPANNABLE);
				
			}
		}
		if (textForThreeBroadcasts.equals("")) {
			holder.mTextView.setVisibility(View.INVISIBLE);
		}
		else {
			holder.mTextView.setVisibility(View.VISIBLE);
		}
		Log.d(TAG, "Returning new view");
		return rowView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/* Superclass AdListAdapter will create view if this is a position of an ad. */
		View rowView = super.getView(position, convertView, parent);

		if(rowView == null) {
			rowView = getViewForGuideCell(position, convertView, parent);
		}
		
		return rowView;
	}

	static class ViewHolder {
		public RelativeLayout	mContainer;
		public ImageView		mImageView;
		public TextView			mTextView;
	}

	public void refreshList(int selectedHour) {
		mHour = selectedHour;

		SecondScreenApplication.getInstance().setSelectedHour(mHour);
		notifyDataSetChanged();
	}
	
	public void setRowWidth(int width) {
		this.rowWidth = width;
	}
	
	@SuppressLint("NewApi")
	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
		if (Build.VERSION.SDK_INT < 16) {
			v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		} else {
			v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		}
	}
}
