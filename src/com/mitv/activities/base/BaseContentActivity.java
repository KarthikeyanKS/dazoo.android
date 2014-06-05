
package com.mitv.activities.base;




public abstract class BaseContentActivity 
	extends BaseActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = BaseContentActivity.class.getName();
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		loadDataWithConnectivityCheck();
	}
}
