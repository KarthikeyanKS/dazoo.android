
package com.mitv.managers;



import com.mitv.SecondScreenApplication;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.TVDate;



public class ContentManager 
	extends ContentManagerCallback
{
	@SuppressWarnings("unused")
	private static final String TAG = ContentManager.class.getName();
	
	
	
	public static ContentManager sharedInstance() 
	{
		return SecondScreenApplication.sharedInstance().getContentManager();
	}
	
	
	public ContentManager()
	{
		super();
	}
	
	
	
	public void registerListenerForRequest(RequestIdentifierEnum requestIdentifier, ViewCallbackListener listener) 
    {
		registerListenerForRequest(requestIdentifier, listener);
    }
    
    
    
	public void unregisterListenerFromAllRequests(ViewCallbackListener listener) 
    {
		unregisterListenerFromAllRequests(listener);
    }
	
	
	
	public void setTVDateSelectedUsingIndexAndFetchGuideForDay(ViewCallbackListener activityCallbackListener, int tvDateIndex) 
	{
		/* Update the index in the storage */
		getCache().setTvDateSelectedUsingIndex(tvDateIndex);

		/* Fetch TVDate object from storage, using new TVDate index */
		TVDate tvDate = getCache().getTvDateSelected();

		/*
		 * Since selected TVDate has been changed, set/fetch the TVGuide for
		 * that day
		 */
		getElseFetchFromServiceTVGuideUsingTVDate(activityCallbackListener, false, tvDate);
	}
}
