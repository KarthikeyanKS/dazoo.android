
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
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
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
	
	
	
	/* Used in views where we don't want to reset the view itself */
	private DisplayImageOptions getDisplayImageDefaultOptions()
	{
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.loading_placeholder_vertical)
        .showImageForEmptyUri(R.drawable.loading_placeholder_vertical)
        .showImageOnFail(R.drawable.loading_placeholder_vertical)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();

		return displayImageOptions;
	}
	
	
	
	/* Used when views are reset before loading */
	private DisplayImageOptions getDisplayImageWithResetViewOptions()
	{	
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.loading_placeholder_vertical)
        .showImageForEmptyUri(R.drawable.loading_placeholder_vertical)
        .showImageOnFail(R.drawable.loading_placeholder_vertical)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.resetViewBeforeLoading(true)
		.build();

		return displayImageOptions;
	}
	
	
	
	public void displayImageWithDefaultOptions(String url, ImageAware imageAware)
	{
		DisplayImageOptions displayImageOptions = getDisplayImageDefaultOptions();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions);
	}
	
	
	
	public void displayImageWithDefaultOptions(String url, ImageAware imageAware, ImageLoadingListener imageLoadingListener)
	{
		DisplayImageOptions displayImageOptions = getDisplayImageDefaultOptions();
		
		imageLoader.displayImage(url, imageAware, displayImageOptions, imageLoadingListener);
	}
	
	
	
	public void displayImageWithDefaultOptions(String url, ImageView imageView)
	{
		DisplayImageOptions displayImageOptions = getDisplayImageDefaultOptions();
		
		imageLoader.displayImage(url, imageView, displayImageOptions);
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
}