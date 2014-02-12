package com.millicom.mitv.asynctasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.millicom.mitv.http.URLParameters;

import android.os.AsyncTask;

public class AsyncTaskBase<T> extends AsyncTask<String, Void, Void> {
	
	private String urlSuffix;
	protected URLParameters urlParameters;
	private Gson gson;
	private Class<T> clazz;
	
	public AsyncTaskBase(Class<T> clazz, String urlSuffix) {
		this(clazz, urlSuffix, new URLParameters());
	}
	
	public AsyncTaskBase(Class<T> clazz, String urlSuffix, URLParameters urlParameters) {
		this.urlSuffix = urlSuffix;
		this.urlParameters = urlParameters;
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		try {
			gsonBuilder.registerTypeAdapter(clazz, clazz.newInstance());
			this.gson = gsonBuilder.create();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Void doInBackground(String... params) {
		/* Use fancy HTTPCore stuff */
		return null;
	}
}
