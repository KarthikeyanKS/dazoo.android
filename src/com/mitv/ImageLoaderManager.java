
package com.mitv;



import android.content.Context;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;



public class ImageLoaderManager 
{
	@SuppressWarnings("unused")
	private static final String TAG = ImageLoaderManager.class.getName();

	
	private static ImageLoaderManager instance;
	
	
	private ImageLoader imageLoader;
	private ImageLoader imageLoaderWithViewReset;
	
	
	private ImageLoaderManager(final Context context)
	{
		// Used in views where we don't want to reset the view itself
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();

		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
			.defaultDisplayImageOptions(displayImageOptions)
			.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
			.memoryCacheSize(2 * 1024 * 1024)
			.discCacheSize(50 * 1024 * 1024)
			.discCacheFileCount(100)
			.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(configuration);
		

		// Used when views are reset before loading
		DisplayImageOptions resetViewDisplayImageOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.resetViewBeforeLoading(true)
			.build();

		ImageLoaderConfiguration resetViewConfiguration = new ImageLoaderConfiguration.Builder(context)
			.defaultDisplayImageOptions(resetViewDisplayImageOptions)
			.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
			.memoryCacheSize(2 * 1024 * 1024)
			.discCacheSize(50 * 1024 * 1024)
			.discCacheFileCount(100)
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.build();

		imageLoaderWithViewReset = ImageLoader.getInstance();
		imageLoaderWithViewReset.init(resetViewConfiguration);
		 
		L.disableLogging();
	}
	
	
	
	public ImageLoader getImageLoader()
	{
		return imageLoader;
	}
	
	
	
	public ImageLoader getImageLoaderWithViewReset()
	{
		return imageLoaderWithViewReset;
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
