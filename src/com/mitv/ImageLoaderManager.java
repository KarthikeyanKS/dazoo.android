
package com.mitv;



import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.utils.L;



public class ImageLoaderManager 
{
	@SuppressWarnings("unused")
	private static final String TAG = ImageLoaderManager.class.getName();

	
	private static ImageLoaderManager instance;
	
	
	private ImageLoader imageLoader;
	
	
	
	private ImageLoaderManager(final Context context)
	{
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
			//.defaultDisplayImageOptions()
			.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
			.memoryCacheSize(2 * 1024 * 1024)
			.discCacheSize(50 * 1024 * 1024)
			.discCacheFileCount(100)
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(configuration);
		
		L.disableLogging();
	}
	
	
	
	public void displayImageWithDefaultOptions(String url, ImageAware imageAware)
	{
		// Used in views where we don't want to reset the view itself
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions);
	}
	
	
	
	public void displayImageWithDefaultOptions(String url, ImageAware imageAware, ImageLoadingListener imageLoadingListener)
	{
		// Used in views where we don't want to reset the view itself
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions, imageLoadingListener);
	}
	
	
	
	public void displayImageWithDefaultOptions(String url, ImageView imageView)
	{
		// Used in views where we don't want to reset the view itself
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
		
		imageLoader.displayImage(url, imageView, displayImageOptions);
	}
	
	
	
	public void displayImageWithResetViewOptions(String url, ImageAware imageAware, ImageLoadingListener imageLoadingListener)
	{
		// Used when views are reset before loading
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.resetViewBeforeLoading(true)
		.build();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions, imageLoadingListener);
	}
	
	
	
	public void displayImageWithResetViewOptions(String url, ImageAware imageAware)
	{
		// Used when views are reset before loading
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.resetViewBeforeLoading(true)
		.build();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions);
	}
	
	
	
	public void displayImageWithResetViewOptions(String url, ImageView imageView)
	{
		// Used when views are reset before loading
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.resetViewBeforeLoading(true)
		.build();
		
		imageLoader.displayImage(url, imageView, displayImageOptions);
	}
	

		
	public static ImageLoaderManager sharedInstance(Context context) 
	{
		if (instance == null) 
		{
			instance = new ImageLoaderManager(context);
		}

		return instance;
	}
}
