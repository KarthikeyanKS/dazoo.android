
package com.mitv.listadapters;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mitv.R;
import com.mitv.managers.ContentManager;



public class AdListAdapter<T> 
	extends BaseAdapter
{
	private static final String TAG = AdListAdapter.class.getName();
	
	protected static final int VIEW_TYPE_AD = 0;
	protected static final int VIEW_TYPE_STANDARD = 1;
	protected static final int VIEW_TYPE_CUSTOM = 2;
	
	private Activity activity;
	
	@SuppressWarnings("unused")
	private String fragmentName;
	
	@SuppressWarnings("unused")
	private List<Integer> adFormats;
	
	private List<T> items;
	private int cellCountBetweenAdCells;
	protected boolean isAdsEnabled;
	private String adMobId;

	
	public AdListAdapter(String fragmentName, Activity activity, List<T> items, String adMobId) 
	{ 
		super();
		
		this.fragmentName = fragmentName;
		
		this.activity = activity;
		
		this.adMobId = adMobId;
		this.adFormats = ContentManager.sharedInstance().getFromCacheAppConfiguration().getAdzerkFormatsForAndroidGuide();
		this.cellCountBetweenAdCells = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCellCountBetweenAdCellsUsingActivityName(fragmentName);
		
		this.items = items;
		
		boolean globalAdsEnabled = ContentManager.sharedInstance().getFromCacheAppConfiguration().isAdsEnabled();
		
		boolean localAdsEnabled = (cellCountBetweenAdCells > 0);
		
		this.isAdsEnabled = (globalAdsEnabled && localAdsEnabled);
	}
	

	public void setItems(List<T> items) {
		this.items = items;
	}
	
	public boolean isAdsEnabled() {
		return isAdsEnabled;
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

	static class ViewHolder 
	{
		public RelativeLayout container;
		public AdView adMobView;
		public TextView textView;
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
				viewHolder.adMobView = new AdView(activity);
				viewHolder.container.addView(viewHolder.adMobView);
				
				rowView.setTag(viewHolder);
			}
			
			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			if (holder.adMobView != null) 
			{
				if(TextUtils.isEmpty(holder.adMobView.getAdUnitId())) {
					holder.adMobView.setAdUnitId(this.adMobId);
					holder.adMobView.setAdSize(AdSize.BANNER);
					
					holder.adMobView.setAdListener(new AdListener() {

						@Override
						public void onAdClosed() {
							Log.d(TAG, "onAdClosed");
							super.onAdClosed();
						}

						@Override
						public void onAdFailedToLoad(int errorCode) {
							Log.d(TAG, "onAdFailedToLoad");
							super.onAdFailedToLoad(errorCode);
						}

						@Override
						public void onAdLeftApplication() {
							Log.d(TAG, "onAdLeftApplication");
							super.onAdLeftApplication();
						}

						@Override
						public void onAdLoaded() {
							Log.d(TAG, "onAdLoaded");
							super.onAdLoaded();
						}

						@Override
						public void onAdOpened() {
							Log.d(TAG, "onAdOpened");
							super.onAdOpened();
						}
						
					});
					AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
					
//					adRequestBuilder.addTestDevice("0EA84CAA2B5996FC7C2A5CBDD571F830"); // Huawei
					
					AdRequest adRequest = adRequestBuilder.build();
					if(adRequest.isTestDevice(activity)) {
						Log.e(TAG, "AdRequest is for testing");
					}
					holder.adMobView.loadAd(adRequest);
				}
			}
		}

		return rowView;
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