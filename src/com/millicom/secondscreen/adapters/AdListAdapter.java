package com.millicom.secondscreen.adapters;

import java.util.HashMap;
import java.util.List;


import com.millicom.secondscreen.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
	private HashMap<Integer, AdzerkAd> adItems = new HashMap<Integer, AdzerkAd>();
	private int cellCountBetweenAdCells;
	
	public AdListAdapter(String tag, Activity activity, List<T> items) {
		super();
		TAG = tag;
		this.activity = activity;
		this.items = items;
		
		this.cellCountBetweenAdCells = AppConfigurationManager.getInstance().getCellCountBetweenAdCells();
		
		downloadAds();
	}
	
	@Override
	public int getCount() {
		int finalCount = 0;
		if (items != null) {
			finalCount = getAdCount() + items.size();
		}

		return finalCount;
	}
	
	public int getAdCount() {
		int adCount = (int) Math.floor(items.size()/cellCountBetweenAdCells);
		return adCount;
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
		
		return item;
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
				final AdzerkAd ad = getAdAtGlobalIndex(position);
				if (ad != null) {
					final String imageUrl = ad.getImageUrl();
					final String impressionUrl = ad.getImpressionUrl();
					final View separator = holder.mContainer.findViewById(R.id.ad_space_separator);
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
										/* Set the size of the imageView according to the size of the ad image */
										LayoutParams params = holder.mImageView.getLayoutParams();
										params.width = ad.getWidth();
										params.height = ad.getHeight();
										holder.mImageView.setLayoutParams(params);
										
										/* Image loaded, show imageView */
										holder.mImageView.setVisibility(View.VISIBLE);
										separator.setVisibility(View.VISIBLE);
										
										/*
										 * Register Ad as shown when image has loaded completely
										 */
										if (impressionUrl != null) {
											/* Let the image loader send the request, since it caches requests which is good */
											ImageLoader.getInstance().displayImage(impressionUrl, new ImageView(activity.getApplicationContext()));
										}
									}
								});
							}
						});
					} else {
						holder.mImageView.setVisibility(View.GONE);
						separator.setVisibility(View.GONE);
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
		}

		return rowView;
	}
	
	private int globalIndexToAdIndex(int globalIndex) {
		int cellsPerAd = cellCountBetweenAdCells + 1;
		int adIndex = (globalIndex/cellsPerAd); //Zero indexed
		Log.e("REMOVE ME", "globalIndex: " + globalIndex + " adIndex: " + adIndex);
		return adIndex;
	}
	
	private AdzerkAd getAdAtGlobalIndex(int globalIndex) {
		int adIndex = globalIndexToAdIndex(globalIndex);
		AdzerkAd ad = adItems.get(adIndex);
		
		return ad;
	}
	
	private void downloadAds() {
		final int adCount = getAdCount();
		for(int i = 0; i < adCount; ++i) {
			final int index = i;
			String divId = new StringBuilder().append(TAG).append("AdWithId").append(i).toString();
			DazooCore.getAdzerkAd(divId, new AdCallBack() {
				@Override
				public void onAdResult(final AdzerkAd ad) {
					if (ad != null) {
						adItems.put(index, ad);
					}
					
					if(index == adCount-1) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								AdListAdapter.this.notifyDataSetChanged();
							}
						});
					}
				}
			});
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		if(isAdPosition(position)) {
			return 0;
		} else {
			return 1;
		}
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
