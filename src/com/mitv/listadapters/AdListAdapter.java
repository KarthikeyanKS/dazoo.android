
package com.mitv.listadapters;



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

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.base.BaseActivity;
import com.mitv.listadapters.TVGuideListAdapter.ViewHolder;
import com.mitv.models.AdAdzerk;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class AdListAdapter<T> extends BaseAdapter {

	protected static final int VIEW_TYPE_AD = 0;
	protected static final int VIEW_TYPE_STANDARD = 1;
	protected static final int VIEW_TYPE_CUSTOM = 2;
	
	private Activity activity;
	private String fragmentName;
	private List<T> items;
	private SparseArray<AdAdzerk> adItems = new SparseArray<AdAdzerk>();
	private List<Integer> adFormats;
	private int cellCountBetweenAdCells;
	private boolean isAdsEnabled;

	public AdListAdapter(String fragmentName, Activity activity, List<T> items) 
	{
		super();
		
		this.fragmentName = fragmentName;
		
		this.activity = activity;

		this.adFormats = ContentManager.sharedInstance().getFromCacheAppConfiguration().getAdzerkFormatsForAndroidGuide();
		this.cellCountBetweenAdCells = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCellCountBetweenAdCellsUsingActivityName(fragmentName);
		
		this.items = items;
		
		boolean globalAdsEnabled = ContentManager.sharedInstance().getFromCacheAppConfiguration().isAdsEnabled();
		
		boolean localAdsEnabled = (cellCountBetweenAdCells > 0);
		
		this.isAdsEnabled = (globalAdsEnabled && localAdsEnabled);
		
		if (this.isAdsEnabled) 
		{
			// adItems = MiTVStore.getInstance().getAdsForFragment(fragmentName);
			
			if(adItems == null) 
			{
				adItems = new SparseArray<AdAdzerk>();
				downloadAds();
			}
		}
	}
	

	public void setItems(List<T> items) {
		this.items = items;
	}
	
	private void downloadAds() 
	{
		// TODO NewArc - Use ContentManager for ads
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
	public int getCount() 
	{
		int finalCount = 0;
		
		if (items != null) 
		{
			finalCount = items.size();
			
			if (isAdsEnabled) 
			{
				finalCount += getAdCount();
			}
		}
		
		return finalCount;
	}
	
	
	
	public int getAdCount() 
	{
		int adCount = 0;
		
		if(isAdsEnabled) 
		{
			adCount = (int) Math.floor(items.size()/cellCountBetweenAdCells);
		}
		
		return adCount;
	}

	
	
	@Override
	public T getItem(int position) 
	{
		T item = null;
		
		if (items != null) 
		{
			if(!isAdPosition(position))
			{
				int positionExcludingAds = positionExcludingAds(position);
				
				item = items.get(positionExcludingAds);
			}
		}
		
		return item;
	}
	
	
	
	@Override
	public int getViewTypeCount() 
	{
		return 1;
	}

	
	
	@Override
	public long getItemId(int position)
	{
		return -1;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = null;
		
		if(isAdPosition(position))
		{	
			rowView = convertView;
			
			LayoutInflater mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if (rowView == null)
			{
				rowView = mLayoutInflater.inflate(R.layout.ad_space, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.ad_space_container);
				viewHolder.channelLogo = (ImageView) rowView.findViewById(R.id.ad_space_imageview);

				rowView.setTag(viewHolder);
			}
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			if (holder.channelLogo != null) 
			{
				final AdAdzerk ad = getAdAtGlobalIndex(position);
				
				if (ad != null) 
				{
					final String imageUrl = ad.getImageUrl();
					
					final String impressionUrl = ad.getImpressionUrl();
					
					final View separator = holder.container.findViewById(R.id.ad_space_separator);
					
					if (imageUrl != null) 
					{
						/* displayImage in UIL must run on main thread! */
						activity.runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								ImageAware imageAware = new ImageViewAware(holder.channelLogo, false);
								
								SecondScreenApplication.sharedInstance().getImageLoaderManager().getImageLoader().displayImage(imageUrl, imageAware, new ImageLoadingListener() 
								{
									@Override
									public void onLoadingStarted(String imageUri, View view) {}

									@Override
									public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

									@Override
									public void onLoadingCancelled(String imageUri, View view) {}

									@Override
									public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) 
									{
										/*
										 * Register Ad as shown when image has loaded completely
										 */
										if (impressionUrl != null)
										{
											/* Let the image loader send the request, since it caches requests which is good */
											SecondScreenApplication.sharedInstance().getImageLoaderManager().getImageLoader().displayImage(impressionUrl, new ImageView(activity.getApplicationContext()));
										}
									}
								});
							}
						});
					} 
					else 
					{
						holder.channelLogo.setVisibility(View.GONE);
						
						separator.setVisibility(View.GONE);
					}

					final String clickUrl = ad.getClickUrl();
					
					if (clickUrl != null) 
					{
						holder.container.setOnClickListener(new View.OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
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
	
	
	
	private int globalIndexToAdIndex(int globalIndex) 
	{
		int cellsPerAd = cellCountBetweenAdCells + 1;
		
		int adIndex = (globalIndex/cellsPerAd); //Zero indexedbc
		
		return adIndex;
	}
	
	
	
	private AdAdzerk getAdAtGlobalIndex(int globalIndex) 
	{
		int adIndex = globalIndexToAdIndex(globalIndex);
		
		AdAdzerk ad = adItems.get(adIndex);
		
		return ad;
	}
		
	
	
	@Override
	public int getItemViewType(int position) {
		if(isAdPosition(position)) {
			return VIEW_TYPE_AD;
		} else {
			return VIEW_TYPE_STANDARD;
		}
	}
	
	
	
	public boolean isAdPosition(int position)
	{
		boolean isAdPosition = false;
		
		if(isAdsEnabled && position % (1 + cellCountBetweenAdCells) == cellCountBetweenAdCells)
		{
			isAdPosition = true;
		}
		
		return isAdPosition;
	}
	
	
	
	public int positionExcludingAds(int position)
	{
		int positionExcludingAds = position;
		
		if(isAdsEnabled) 
		{
			int adsUntilThisPosition = (int) Math.floor((double)position / (double)(cellCountBetweenAdCells + 1));
			
			positionExcludingAds = position - adsUntilThisPosition;
		}
		return positionExcludingAds;
	}
}