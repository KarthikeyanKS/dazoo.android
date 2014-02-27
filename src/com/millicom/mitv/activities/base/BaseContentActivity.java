
package com.millicom.mitv.activities.base;



public abstract class BaseContentActivity 
	extends BaseActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = BaseContentActivity.class.getName();
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
				
		//TODO NewArc we probably don't want to check for internet connectivity at all, not even in onCreate?
//		loadDataWithConnectivityCheck();
		loadData();
	}
}
