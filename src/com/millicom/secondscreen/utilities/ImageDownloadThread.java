package com.millicom.secondscreen.utilities;

import com.millicom.secondscreen.R;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageDownloadThread extends AsyncTask<String, Void, Bitmap> {
	final ImageView				imageView;
	final ProgressBar			progressBar;
	private final static String	TAG	= "ImageDownloadThread";

	public ImageDownloadThread(ImageView imageView, ProgressBar progressBar) {
		this.imageView = imageView;
		this.progressBar = progressBar;
	}

	@Override
	protected void onPreExecute() {
		imageView.setImageResource(R.drawable.loadimage_2x);
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bmp = null;
		String url = params[0];
		try {
			//InputStream in = new java.net.URL(url).openStream();
			
			HttpClient client = new DefaultHttpClient();
			           URI imageURI = new URI(url);
			            HttpGet req = new HttpGet();
			            req.setURI(imageURI);
			            HttpResponse response = client.execute(req);
			
			bmp = BitmapFactory.decodeStream(response.getEntity().getContent());
			//bmp = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		progressBar.setVisibility(View.GONE);
		if (result != null) {
			imageView.setImageBitmap(result);
		}
	}
}
