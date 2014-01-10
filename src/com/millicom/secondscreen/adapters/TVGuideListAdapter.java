package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.model.AdzerkAd;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.content.tvguide.ChannelPageActivity;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.manager.DazooCore.AdCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class TVGuideListAdapter extends AdListAdapter<Guide> {

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
		super(guide);
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
		Log.d(TAG, "Maxrowlength: " + maxRowLenght);
		Log.d(TAG, "Screenwidth: " + screenWidth);
	}
	
	public View getViewForGuideCell(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (rowView == null) {
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
						Log.d(TAG, "Rowlength on row " + position + ": " + rowLenght);
						rowInfo = rowInfo.replace(rowInfo.substring(maxRowLenght-3, rowInfo.length()), ellipsisString);
					}
					
					if (broadcast.isRunning()) {
						textStartIndexToMarkAsOngoing = textForThreeBroadcasts.length();
						textIndexToMarkAsOngoing = textForThreeBroadcasts.length() + rowInfo.length();
					}
					
					textForThreeBroadcasts += rowInfo + "\n";
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
		
		return rowView;
	}
	
	private View getAdView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (rowView == null) {
			rowView = mLayoutInflater.inflate(R.layout.ad_space, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.ad_space_container);
			viewHolder.mChannelIconIv = (ImageView) rowView.findViewById(R.id.ad_space_imageview);

			rowView.setTag(viewHolder);
		}
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if (holder.mChannelIconIv != null) {
			String divId = new StringBuilder().append(TAG).append("AdDivId").append(position).toString();
			DazooCore.getAdzerkAd(divId, new AdCallBack() {
				@Override
				public void onAdResult(AdzerkAd ad) {
					if (ad != null) {
						final String imageUrl = ad.getImageUrl();
						final String impressionUrl = ad.getImpressionUrl();
						if (imageUrl != null) {
							
							/* displayImage in UIL must run on main thread! */
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ImageAware imageAware = new ImageViewAware(holder.mChannelIconIv, false);
									ImageLoader.getInstance().displayImage(imageUrl, imageAware, new ImageLoadingListener() {
										@Override
										public void onLoadingStarted(String imageUri, View view) {
										}

										@Override
										public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
										}

										@Override
										public void onLoadingCancelled(String imageUri, View view) {
										}

										@Override
										public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
											/*
											 * Register Ad as shown when image has loaded completely
											 */
											if (impressionUrl != null) {
												/* Let the image loader send the request, since it caches requests which is good */
												ImageLoader.getInstance().displayImage(impressionUrl, new ImageView(mActivity.getApplicationContext()), new ImageLoadingListener() {
													@Override
													public void onLoadingStarted(String imageUri, View view) {
													}

													@Override
													public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
														Log.w(TAG, "Tracking of impression url failed");
													}

													@Override
													public void onLoadingCancelled(String imageUri, View view) {
														Log.d(TAG, "Tracking of impression url canceled");
													}

													@Override
													public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
														Log.d(TAG, "Successfully tracked impression url");
													}
												});
											}
										}

									});
									holder.mChannelIconIv.setVisibility(View.VISIBLE);
								}
							});

						}

						final String clickUrl = ad.getClickUrl();
						if (clickUrl != null) {
							holder.mContainer.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
									mActivity.startActivity(intent);
								}
							});
						}
					}
				}
			});
		}

		return rowView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = null;
		if(isAdPosition(position)) {
			rowView = getAdView(position, convertView, parent);
		} else {
			rowView = getViewForGuideCell(position, convertView, parent);
		}
		
		return rowView;
	}

	static class ViewHolder {
		public RelativeLayout	mContainer;
		public ImageView		mChannelIconIv;
		public TextView			mTextView;
	}

	public void refreshList(int selectedHour) {
		mHour = selectedHour;

		SecondScreenApplication.getInstance().setSelectedHour(mHour);
		notifyDataSetChanged();
	}
}
