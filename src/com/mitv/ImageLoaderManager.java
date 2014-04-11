
package com.mitv;



import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.utils.L;



public class ImageLoaderManager 
{
	private static final String TAG = ImageLoaderManager.class.getName();

	
	private static ImageLoaderManager instance;
	
	
	private ImageLoader imageLoader;
	
	
	
	public static ImageLoaderManager sharedInstance(final Context context) 
	{
		if (instance == null) 
		{
			instance = new ImageLoaderManager(context);
		}

		return instance;
	}
	

	private ImageLoaderManager(final Context context)
	{
		int maximumMemoryCacheSize;
		int maximumDiskCacheSize = 50 * 1024 * 1024;
		int diskCacheFileCount = 200;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
		{
			maximumMemoryCacheSize = 2 * 1024 * 1024;
		}
		else
		{
			maximumMemoryCacheSize = 1 * 1024 * 1024;
		}
		
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
			.threadPriority(Thread.MIN_PRIORITY)
			.threadPoolSize(2)
			.denyCacheImageMultipleSizesInMemory()
			.memoryCache(new LRULimitedMemoryCache(maximumMemoryCacheSize))
			.discCacheSize(maximumDiskCacheSize)
			.discCacheFileCount(diskCacheFileCount)
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(configuration);
		
		L.disableLogging();
	}
	
	
	
	/* Used in views where we don't want to reset the view itself and we always want to use memory cache */
	private DisplayImageOptions getDisplayImageDefaultOptions()
	{		
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.loading_placeholder_vertical)
        .showImageForEmptyUri(R.drawable.loading_placeholder_vertical)
        .showImageOnFail(R.drawable.loading_placeholder_vertical)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.EXACTLY)
		.displayer(new FadeInBitmapDisplayer(1000))
		.build();

		return displayImageOptions;
	}
	
	
	
	/* Used when views are reset before loading */
	private DisplayImageOptions getDisplayImageWithResetViewOptions()
	{	
		boolean useMemoryCache;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
		{
			useMemoryCache = true;
		}
		else
		{
			useMemoryCache = false;
		}
		
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.loading_placeholder_vertical)
        .showImageForEmptyUri(R.drawable.loading_placeholder_vertical)
        .showImageOnFail(R.drawable.loading_placeholder_vertical)
		.cacheInMemory(useMemoryCache)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.EXACTLY)
		.displayer(new FadeInBitmapDisplayer(1000))
		.resetViewBeforeLoading(true)
		.build();

		return displayImageOptions;
	}
	
	
	
	public void displayImageWithDefaultOptions(String url, ImageAware imageAware)
	{
		DisplayImageOptions displayImageOptions = getDisplayImageDefaultOptions();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions);
	}
	
	
	
	public void displayImageWithResetViewOptions(String url, ImageAware imageAware, ImageLoadingListener imageLoadingListener)
	{
		DisplayImageOptions displayImageOptions = getDisplayImageWithResetViewOptions();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions, imageLoadingListener);
	}
	
	
	
	public void displayImageWithResetViewOptions(String url, ImageAware imageAware)
	{
		DisplayImageOptions displayImageOptions = getDisplayImageWithResetViewOptions();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions);
	}
	
	
	
	public void displayImageWithResetViewOptions(String url, ImageView imageView)
	{
		DisplayImageOptions displayImageOptions = getDisplayImageWithResetViewOptions();
		
		imageLoader.displayImage(url, imageView, displayImageOptions);
	}
		
	
	public void pause()
	{
		if(imageLoader != null)
		{
			imageLoader.pause();
		}
		else
		{
			Log.w(TAG, "ImageLoader is null");
		}
	}
	
	
	
	public void resume()
	{
		if(imageLoader != null)
		{
			imageLoader.resume();
		}
		else
		{
			Log.w(TAG, "ImageLoader is null");
		}
	}
}