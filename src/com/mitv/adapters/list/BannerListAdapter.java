
package com.mitv.adapters.list;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.competition.CompetitionPageActivity;
import com.mitv.enums.BannerViewType;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.competitions.Competition;



public abstract class BannerListAdapter<T> 
	extends BaseAdapter
{
	private static final String TAG = BannerListAdapter.class.getName();

	protected String fragmentName;
	private Activity activity;
	
	private List<T> items;
	private int cellCountBetweenAdCells;
	private boolean isAdsEnabled;
	private String adMobId;
	
	private boolean areCompetitionsEnabled;
	private boolean onlyDisplayCompetitionBannerOnce;
	
	
	
	
	public BannerListAdapter(
			final String fragmentName, 
			final Activity activity, 
			final List<T> items, 
			final String adMobId,
			final boolean showCompetitionsBanner) 
	{
		this(fragmentName, activity, items, adMobId, showCompetitionsBanner, true);
	}
	
	
	
	public BannerListAdapter(
			final String fragmentName, 
			final Activity activity, 
			final List<T> items, 
			final String adMobId,
			final boolean showCompetitionsBanner,
			final boolean onlyDisplayCompetitionBannerOnce) 
	{ 
		super();
		
		this.fragmentName = fragmentName;
		
		this.activity = activity;
		
		this.adMobId = adMobId;
		
		this.cellCountBetweenAdCells = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().getCellCountBetweenAdCellsUsingActivityName(fragmentName);
		
		this.items = items;
		
		boolean globalAdsEnabled = ContentManager.sharedInstance().getCacheManager().getAppConfiguration().isAdsEnabled();
		
		boolean localAdsEnabled = (cellCountBetweenAdCells > 0);
		
		this.isAdsEnabled = (globalAdsEnabled && localAdsEnabled);
		
		this.areCompetitionsEnabled = showCompetitionsBanner && (ContentManager.sharedInstance().getCacheManager().getVisibleCompetitions().isEmpty() == false);
		
		this.onlyDisplayCompetitionBannerOnce = onlyDisplayCompetitionBannerOnce;
	}
	

	
	public void setItems(List<T> items) 
	{
		this.items = items;
	}
	
	
	
	public boolean areAdsEnabled()
	{
		return isAdsEnabled;
	}
			

	
	@Override
	public int getCount() 
	{
		int count = 0;
		
		if (items != null) 
		{
			count = items.size();
			
			if (isAdsEnabled) 
			{
				count += getBannerAdCount();
			}
			
			if(areCompetitionsEnabled)
			{
				count += getBannerCompetitionCount();
			}
		}
		
		return count;
	}
	
	
	
	@Override
	public T getItem(int position)
	{
		T item = null;
		
		if (items != null) 
		{
			BannerViewType viewType = getBannerViewType(position);
			
			switch(viewType)
			{
				case BANNER_VIEW_TYPE_STANDARD:
				case BANNER_VIEW_TYPE_CUSTOM:
				{
					int positionExcludingBanners = positionExcludingBanners(position);
					
					item = items.get(positionExcludingBanners);
					
					break;
				}
				
				default:
				{
					// Do nothing
					break;
				}
			}
		}
		
		return item;
	}
	
	
	
	@Override
	public int getViewTypeCount()
	{
		return 4;
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
		
		BannerViewType viewType = getBannerViewType(position);
		
		if(viewType == BannerViewType.BANNER_VIEW_TYPE_AD)
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
				if(TextUtils.isEmpty(holder.adMobView.getAdUnitId())) 
				{
					holder.adMobView.setAdUnitId(this.adMobId);
					holder.adMobView.setAdSize(AdSize.BANNER);
					
					holder.adMobView.setAdListener(new AdListener() 
					{
						@Override
						public void onAdClosed() 
						{
							Log.d(TAG, "onAdClosed");
							super.onAdClosed();
						}

						@Override
						public void onAdFailedToLoad(int errorCode)
						{
							Log.d(TAG, "onAdFailedToLoad");
							super.onAdFailedToLoad(errorCode);
						}

						@Override
						public void onAdLeftApplication() 
						{
							Log.d(TAG, "onAdLeftApplication");
							super.onAdLeftApplication();
						}

						@Override
						public void onAdLoaded() 
						{
							Log.d(TAG, "onAdLoaded");
							super.onAdLoaded();
						}

						@Override
						public void onAdOpened() 
						{
							Log.d(TAG, "onAdOpened");
							super.onAdOpened();
						}
						
					});
					
					AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
					
					AdRequest adRequest = adRequestBuilder.build();
					
					if(adRequest.isTestDevice(activity))
					{
						Log.e(TAG, "AdRequest is for testing");
					}
					
					holder.adMobView.loadAd(adRequest);
				}
			}
		}
		else if(viewType == BannerViewType.BANNER_VIEW_TYPE_COMPETITION)
		{
			rowView = convertView;
			
			LayoutInflater mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if (rowView == null)
			{
				rowView = mLayoutInflater.inflate(R.layout.ad_space, null);
				
				final Competition competition = ContentManager.sharedInstance().getCacheManager().getVisibleRandomCompetition();
				
				boolean hasEnded = competition.hasEnded();
				
				if(hasEnded == false)
				{
					ViewHolder viewHolder = new ViewHolder();
					
					viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.ad_space_container);
					
					viewHolder.competitionBannerFrameLayout = (FrameLayout) rowView.findViewById(R.id.banner_image_competition_frame_layout);
					
					viewHolder.competitionBannerFrameLayout.setVisibility(View.VISIBLE);
				
//					if(Constants.FORCE_USAGE_OF_DEFAULT_COMPETITION_BANNER == false)
//					{
//						String imageUrl = competition.getBanner().getImageURLForDeviceDensityDPI();
//						
//						ImageAware imageAware = new ImageViewAware(viewHolder.competitionBannerView, false);
//		
//						SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithDefaultOptions(imageUrl, imageAware);
//					}
					
					viewHolder.competitionBannerFrameLayout.setOnClickListener(new View.OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							Intent intent = new Intent(activity, CompetitionPageActivity.class);
			                
			                intent.putExtra(Constants.INTENT_COMPETITION_ID, competition.getCompetitionId());
			                
			                if (BannerListAdapter.this instanceof TVGuideListAdapter) 
			                {
			                	TrackingGAManager.sharedInstance().sendUserCompetitionBannerPressedInAllTab(competition.getDisplayName());
			                }
			                else if (BannerListAdapter.this instanceof TVGuideTagListAdapter) 
			                {
			                	TrackingGAManager.sharedInstance().sendUserCompetitionBannerPressedInSportsTab(competition.getDisplayName());
			                }
			                else if (BannerListAdapter.this instanceof FeedListAdapter) {
			                	TrackingGAManager.sharedInstance().sendUserCompetitionBannerPressedInFeed(competition.getDisplayName());
			                }
			                
							activity.startActivity(intent);
						}
					});
					
					rowView.setTag(viewHolder);
				}
				
				if (this instanceof FeedListAdapter) 
				{
					rowView.findViewById(R.id.ad_space_separator).setBackgroundColor(activity.getResources().getColor(R.color.grey2));
				}
			}
		}

		return rowView;
	}	
		
	
	
	@Override
	public int getItemViewType(int position) 
	{
		return getBannerViewType(position).getId();
	}
	
	
	
	public BannerViewType getBannerViewType(int position)
	{
		if(isBannerAdPosition(position))
		{
			return BannerViewType.BANNER_VIEW_TYPE_AD;
		}
		else if(isCompetitionAdPosition(position, items))
		{
			return BannerViewType.BANNER_VIEW_TYPE_COMPETITION;
		}
		else
		{
			return BannerViewType.BANNER_VIEW_TYPE_STANDARD;
		}
	}
	
	
	
	private int getBannerAdCount() 
	{
		int count = 0;
		
		if(isAdsEnabled) 
		{
			count = (int) Math.floor(items.size() / cellCountBetweenAdCells);
		}
		
		return count;
	}
	
	
	
	private int getBannerCompetitionCount()
	{
		int count = 0;
		
		if(areCompetitionsEnabled) 
		{
			if(onlyDisplayCompetitionBannerOnce)
			{
				count = 1;
			}
			else
			{
				count = (int) Math.floor(items.size() / Constants.TOTAL_CELLS_IN_BETWEEN_COMPETITION_BANNERS);
			}
		}
		
		return count;
	}
	
	
	
	private boolean isBannerAdPosition(int position)
	{
		boolean isPosition = (position % (1 + cellCountBetweenAdCells) == cellCountBetweenAdCells);
		
		boolean isBannerAdPosition = (isAdsEnabled && isPosition);
		
		return isBannerAdPosition;
	} 
	
	
	
	private boolean isCompetitionAdPosition(int position, List<T> items)
	{
		boolean isPosition = (position % (1 + Constants.TOTAL_CELLS_IN_BETWEEN_COMPETITION_BANNERS) == Constants.TOTAL_CELLS_IN_BETWEEN_COMPETITION_BANNERS);
			
		boolean isAfterFirstPosition = (position > Constants.TOTAL_CELLS_IN_BETWEEN_COMPETITION_BANNERS);
		
		boolean isAfterLastListPosition = (position >= items.size());
		
		boolean isListSizeLowerThanCellBetweenCount = (items.size() < Constants.TOTAL_CELLS_IN_BETWEEN_COMPETITION_BANNERS);
		
		boolean isBannerPosition = false;
		
		if(isListSizeLowerThanCellBetweenCount && isAfterLastListPosition)
		{
			isBannerPosition = true;
		}
		else
		{
			if(onlyDisplayCompetitionBannerOnce == false)
			{
				isBannerPosition = (areCompetitionsEnabled && isPosition);
			}
			else
			{
				isBannerPosition = (areCompetitionsEnabled && isPosition && !isAfterFirstPosition);
			}
		}
		
		return isBannerPosition;
	}
	
	
	
	private int positionExcludingBanners(int position)
	{
		int positionExcludingBanners = position;
		
		if(isAdsEnabled) 
		{
			int bannerAdsUntilThisPosition = (int) Math.floor((double)position / (double)(cellCountBetweenAdCells + 1));
			
			positionExcludingBanners = positionExcludingBanners - bannerAdsUntilThisPosition;
		}
		
		if(areCompetitionsEnabled)
		{
			int bannerCompetitionsUntilThisPosition;
			
			if(onlyDisplayCompetitionBannerOnce == false)
			{
				bannerCompetitionsUntilThisPosition = (int) Math.floor((double)position / (double)(Constants.TOTAL_CELLS_IN_BETWEEN_COMPETITION_BANNERS + 1));
			}
			else
			{
				boolean isAfterFirstPosition = (position > Constants.TOTAL_CELLS_IN_BETWEEN_COMPETITION_BANNERS);
				
				if(isAfterFirstPosition)
				{
					bannerCompetitionsUntilThisPosition = 1;
				}
				else
				{
					bannerCompetitionsUntilThisPosition = 0;
				}
			}
			
			positionExcludingBanners = positionExcludingBanners - bannerCompetitionsUntilThisPosition;
		}
		
		return positionExcludingBanners;
	}
	
	
	
	private static class ViewHolder 
	{
		private RelativeLayout container;
		private FrameLayout competitionBannerFrameLayout;
		private AdView adMobView;
	}
}