package com.millicom.secondscreen.http;

import org.apache.http.cookie.Cookie;

import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.millicom.secondscreen.SecondScreenApplication;

public abstract class MiTVCallback<T> extends AjaxCallback<T> {
	
	public MiTVCallback() {		
		header("Content-Type", "application/json");
		String userAgent = SecondScreenApplication.getUserAgent();
		setAgent(userAgent);
		String session = SecondScreenApplication.getSession();
		cookie("session",  session);
	}
	
	@Override
	public final void callback(String url, T json, AjaxStatus status) {		
		for (Cookie cookie : status.getCookies()) {
			if (cookie.getName().equals("session")) {
				Log.i("SESSION", cookie.getValue());
				SecondScreenApplication.setSession(cookie.getValue());
				break;
			}
		}
		
		Log.d("DURATION", url + ": " + status.getDuration() + " ms");
		header("Request-Duration", status.getDuration() + " ms");
		
		mitvCallback(url, json, status);
	}
	public abstract void mitvCallback(String url, T json, AjaxStatus status);
}
