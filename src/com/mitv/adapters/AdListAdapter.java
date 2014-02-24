
package com.mitv.adapters;



import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.mitv.R;
import com.mitv.adapters.TVGuideListAdapter.ViewHolder;
import com.mitv.interfaces.AdCallBackInterface;
import com.mitv.manager.AppConfigurationManager;
import com.mitv.manager.ApiClient;
import com.mitv.model.OldAdzerkAd;
import com.mitv.storage.MiTVStore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class AdListAdapter<T> extends BaseAdapter {

	private String fragmentName;
	private Activity activity;
	private List<T> items;
	private SparseArray<OldAdzerkAd> adItems = new SparseArray<OldAdzerkAd>();
	private List<Integer> adFormats;
	private int cellCountBetweenAdCells;
	private boolean isAdsEnabled;
	
	public AdListAdapter(String fragmentName, Activity activity, List<T> items) {
		super();
		this.fragmentName = fragmentName;
		this.activity = activity;

		this.adFormats = AppConfigurationManager.getInstance().getAdFormatsForFragment(fragmentName);
		this.cellCountBetweenAdCells = AppConfigurationManager.getInstance().getCellsBetweenAdCellsCountForFragment(fragmentName);
		
		this.items = items;

		
		boolean globalAdsEnabled = AppConfigurationManager.getInstance().isAdsEnabled();
		boolean localAdsEnabled = (cellCountBetweenAdCells > 0);
		
		this.isAdsEnabled = globalAdsEnabled && localAdsEnabled;
		
		if (this.isAdsEnabled) 
		{
			adItems = MiTVStore.getInstance().getAdsForFragment(fragmentName);
			
			if(adItems == null) 
			{
				adItems = new SparseArray<OldAdzerkAd>();
				downloadAds();
			}
		}
	}
	
	private void downloadAds() 
	{
		//TODO USSE ContentManager here!!
//		final int adCount = getAdCount();
//		
//		for(int i = 0; i < adCount; ++i) 
//		{
//			final int index = i;
//			String divId = new StringBuilder().append(fragmentName).append("AdWithId").append(i).toString();
//			
//			ApiClient.getAdzerkAd(divId, adFormats, new AdCallBackInterface() 
//			{
//				@Override
//				public void onAdResult(final OldAdzerkAd ad) {
//					if (ad != null) {
//						adItems.put(index, ad);
//					}
//					
//					if(index == adCount-1) {
//						MiTVStore.getInstance().addAdsForFragment(fragmentName, adItems);
//						
//						activity.runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								AdListAdapter.this.notifyDataSetChanged();
//							}
//						});
//					}
//				}
//			});
//		}
	}

	@Override
	public int getCount() {
		int finalCount = 0;
		if (items != null) {
			finalCount = items.size();
			if (isAdsEnabled) {
				finalCount += getAdCount();
			}
		}
		return finalCount;
	}
	
	public int getAdCount() {
		int adCount = 0;
		if(isAdsEnabled) {
			adCount = (int) Math.floor(items.size()/cellCountBetweenAdCells);
		}
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
		return 1;
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
				final OldAdzerkAd ad = getAdAtGlobalIndex(position);
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
		int adIndex = (globalIndex/cellsPerAd); //Zero indexedbc
		return adIndex;
	}
	
	private OldAdzerkAd getAdAtGlobalIndex(int globalIndex) {
		int adIndex = globalIndexToAdIndex(globalIndex);
		OldAdzerkAd ad = adItems.get(adIndex);
		
		return ad;
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
		
		if(isAdsEnabled && position % (1 + cellCountBetweenAdCells) == cellCountBetweenAdCells) {
			isAdPosition = true;
		}
		
		return isAdPosition;
	}
	
	public int positionExcludingAds(int position) {
		int positionExcludingAds = position;
		if(isAdsEnabled) {
			int adsUntilThisPosition = (int) Math.floor((double)position / (double)(cellCountBetweenAdCells + 1));
			positionExcludingAds = position - adsUntilThisPosition;
		}
		return positionExcludingAds;
	}
}
