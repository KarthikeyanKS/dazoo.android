package com.millicom.secondscreen.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.AdzerkAd;
import com.millicom.secondscreen.manager.AppConfigurationManager;
import com.millicom.secondscreen.manager.DazooCore;
import com.millicom.secondscreen.manager.DazooCore.AdCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class AdListAdapter<T> extends BaseAdapter {

	private String TAG;
	private Activity activity;
	private List<T> items;
	private int cellCountBetweenAdCells;
	
	public AdListAdapter(String tag, Activity activity, List<T> items) {
		super();
		TAG = tag;
		this.activity = activity;
		this.items = items;
		
		this.cellCountBetweenAdCells = AppConfigurationManager.getInstance().getCellCountBetweenAdCells();
	}

	@Override
	public int getCount() {
		int finalCount = 0;
		if (items != null) {
			int count = items.size();
			double multiple = 1 + (1/AppConfigurationManager.getInstance().getCellCountBetweenAdCells());
			finalCount = (int) Math.ceil((double)count * multiple);
		}

		return finalCount;
	}

	@Override
	public T getItem(int position) {
		T item = null;
		if (items != null) {
			if(!isAdPosition(position)) {
				int positionExcludingAds = positionExcludingAds(position);
				item = items.get(positionExcludingAds);
			}
		}
		if(item != null) {
			Log.e(TAG, "getItem, position:" + position);
		} else {
			Log.e(TAG, "getItem, item NULL:" + position);
		}
		
		return item;
	}

	@Override
	public int getItemViewType(int position) {
		if(isAdPosition(position)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = null;
		
		if(isAdPosition(position)) {
			rowView = convertView;
			LayoutInflater mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if (rowView == null) {
				rowView = mLayoutInflater.inflate(R.layout.ad_space, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.ad_space_container);
				viewHolder.mImageView = (ImageView) rowView.findViewById(R.id.ad_space_imageview);

				rowView.setTag(viewHolder);
			}
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			if (holder.mImageView != null) {
				String divId = new StringBuilder().append(TAG).append("AdDivId").append(position).toString();
				DazooCore.getAdzerkAd(divId, new AdCallBack() {
					@Override
					public void onAdResult(AdzerkAd ad) {
						if (ad != null) {
							final String imageUrl = ad.getImageUrl();
							final String impressionUrl = ad.getImpressionUrl();
							if (imageUrl != null) {
								
								/* displayImage in UIL must run on main thread! */
								activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ImageAware imageAware = new ImageViewAware(holder.mImageView, false);
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
													ImageLoader.getInstance().displayImage(impressionUrl, new ImageView(activity.getApplicationContext()), new ImageLoadingListener() {
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
										holder.mImageView.setVisibility(View.VISIBLE);
									}
								});

							}

							final String clickUrl = ad.getClickUrl();
							if (clickUrl != null) {
								holder.mContainer.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
										activity.startActivity(intent);
									}
								});
							}
						}
					}
				});
			}
		}
		
		return rowView;
	}
	
	public boolean isAdPosition(int position) {
		boolean isAdPosition = false;
		
		if(position % (1 + cellCountBetweenAdCells) == cellCountBetweenAdCells) {
			isAdPosition = true;
		}
		
		return isAdPosition;
	}
	
	public int positionExcludingAds(int position) {
		int adsUntilThisPosition = (int) Math.floor((double)position / (double)(cellCountBetweenAdCells + 1));
		int positionExcludingAds = position - adsUntilThisPosition;
		return positionExcludingAds;
	}
}
