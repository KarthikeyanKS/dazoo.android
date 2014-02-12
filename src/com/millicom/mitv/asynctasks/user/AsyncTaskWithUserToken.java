package com.millicom.mitv.asynctasks.user;

import com.millicom.mitv.asynctasks.AsyncTaskBase;


public class AsyncTaskWithUserToken<T> extends AsyncTaskBase<T> {
	
	/* Here we use userToken... */
	
	public AsyncTaskWithUserToken(Class<T> clazz, String urlSuffix) {
		super(clazz, urlSuffix);
		// TODO Auto-generated constructor stub
	}
	
}
