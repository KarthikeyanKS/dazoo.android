package com.millicom.secondscreen.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.damnhandy.uri.template.UriTemplate;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.SecondScreenApplication;

public class ImageLoader {

	private static final String		TAG								= "ImageLoader";

	static MemoryCache				memoryCache						= new MemoryCache();
	private Context					context;
	FileCache						fileCache;
	private Map<ImageView, String>	imageViews						= Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService					executorService;
	private int						placeholder						= 0;
	private boolean					usePlaceholder					= true;
	private boolean					useLayoutAnimation				= false;
	private final int				IMAGELOADING_CONNECTION_TIMEOUT	= 10000;
	private Pattern					pattern;

	private static char[]			hextable						= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static enum IMAGE_TYPE {
		LANDSCAPE, THUMBNAIL, POSTER, GALLERY
	};

	public ImageLoader(Context context, int placeholder) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		this.context = context;
		this.placeholder = placeholder;
		pattern = Pattern.compile("https?:\\/\\/(.*?)\\/(.*?)(\\/.*)"); // Pattern to use for ImageMachine
	}

	/**
	 * Creates a tokenized URL an displays image.
	 * 
	 * @param url
	 *            The URL of the image.
	 * @param imageView
	 *            The ImageView to display the image in.
	 * @param type
	 *            The type of image to display, this can be either gallery (large) or thumbnail (small) image.
	 * 
	 *            The hashed string is created by first splitting the image URL to a Project and ImageURL String.
	 * 
	 * */
	public void displayImage(String url, ImageView imageView, IMAGE_TYPE type) {
		this.displayImage(url, imageView, null, type);
	}

	/**
	 * Creates a tokenized URL an displays image.
	 * 
	 * @param url
	 *            The URL of the image.
	 * @param imageView
	 *            The ImageView to display the image in.
	 * @param progressBar
	 * 			  The ProgressBar to display while the image is loading
	 * @param type
	 *            The type of image to display, this can be either gallery (large) or thumbnail (small) image.
	 * 
	 *            The hashed string is created by first splitting the image URL to a Project and ImageURL String.
	 * 
	 * */
	public void displayImage(String url, ImageView imageView, ProgressBar progressBar, IMAGE_TYPE type) {
		try {
			Matcher m = pattern.matcher(url);
			String project = "";
			String imageURL = "";

			while (m.find()) {
				project = m.group(2);
				imageURL = m.group(3);
			}

			String width = null;
			String height = null;

			if (type == IMAGE_TYPE.POSTER) {

				width = "" + SecondScreenApplication.sImageSizePosterWidth;
				height = "" + SecondScreenApplication.sImageSizePosterHeight;
			} else if (type == IMAGE_TYPE.LANDSCAPE) {

				width = "" + SecondScreenApplication.sImageSizeLandscapeWidth;
				height = "" + SecondScreenApplication.sImageSizeLandscapeHeight;
			} else if (type == IMAGE_TYPE.THUMBNAIL) {

				width = "" + SecondScreenApplication.sImageSizeThumbnailWidth;
				height = "" + SecondScreenApplication.sImageSizeThumbnailHeight;
			}

			else if (type == IMAGE_TYPE.GALLERY) {

				width = "" + SecondScreenApplication.sImageSizeGalleryWidth;
				height = "" + SecondScreenApplication.sImageSizeGalleryHeight;
			}

			String hashedString = md5((imageURL + project + width + height + Consts.IMAGE_MACHINE_SECURITY_KEY)).substring(0, 8);

			UriTemplate template = UriTemplate.fromTemplate(url);

			template.set("width", width);
			template.set("height", height);
			template.set("token", hashedString);

			url = template.expand();
		} catch (Exception e) {
			e.printStackTrace();
		}

		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		
		if(progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);
		}

		if (bitmap != null) {
			if(progressBar != null) {
				progressBar.setVisibility(View.GONE);
			}
			
			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(url, imageView);

			if (usePlaceholder) {
				imageView.setImageResource(placeholder);
				
				if(progressBar != null) {
					progressBar.setVisibility(View.GONE);
				}
			}

		}
		

	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);
		Bitmap b = decodeFile(f);

		if (b != null) return b;
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();

			conn.setConnectTimeout(IMAGELOADING_CONNECTION_TIMEOUT);
			conn.setReadTimeout(IMAGELOADING_CONNECTION_TIMEOUT);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();

			// bitmap = decodeFile(f);

			HttpClient client = new DefaultHttpClient();
			URI imageURI = new URI(url);
			HttpGet req = new HttpGet();
			req.setURI(imageURI);
			HttpResponse response = client.execute(req);

			bitmap = BitmapFactory.decodeStream(response.getEntity().getContent());

			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// Decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			return BitmapFactory.decodeStream(new FileInputStream(f));
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	private class PhotoToLoad { // Task for the queue
		public String		url;
		public ImageView	imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad	photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad)) return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad)) return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url)) return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap		bitmap;
		PhotoToLoad	photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad)) return;
			if (bitmap != null) {
				if (useLayoutAnimation) photoToLoad.imageView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
				photoToLoad.imageView.setImageBitmap(bitmap);
			} else {
				photoToLoad.imageView.setImageResource(placeholder);
			}
		}
	}

	// Generates a MD5 hash, used to create the token used in the image url
	private static String md5(String s) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes(), 0, s.length());
			byte[] bytes = digest.digest();
			String hash = "";
			for (int i = 0; i < bytes.length; ++i) {
				int di = (bytes[i] + 256) & 0xFF;
				hash = hash + hextable[(di >> 4) & 0xF] + hextable[di & 0xF];
			}
			return hash;
		} catch (NoSuchAlgorithmException e) {
		}

		return "";
	}

	public boolean isUsePlaceholder() {
		return usePlaceholder;
	}

	public void setUsePlaceholder(boolean usePlaceholder) {
		this.usePlaceholder = usePlaceholder;
	}

	public boolean isUseLayoutAnimation() {
		return useLayoutAnimation;
	}

	public void setUseLayoutAnimation(boolean useLayoutAnimation) {
		this.useLayoutAnimation = useLayoutAnimation;
	}

	// Clear caches
	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public void clearMemoryCache() {
		memoryCache.clear();
	}

}
