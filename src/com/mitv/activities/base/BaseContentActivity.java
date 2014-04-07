
package com.mitv.activities.base;

import android.util.Log;

import com.mitv.SecondScreenApplication;



public abstract class BaseContentActivity 
	extends BaseActivity
{
	private static final String TAG = BaseContentActivity.class.getName();
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if(!SecondScreenApplication.isAppRestarting()) {
			loadDataWithConnectivityCheck();
		} else {
			Log.e(TAG, "Not loading data since we are restarting the app");
		}
	}
}
