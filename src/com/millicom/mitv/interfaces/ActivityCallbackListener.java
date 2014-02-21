
package com.millicom.mitv.interfaces;



import com.millicom.mitv.enums.FetchRequestResultEnum;



public interface ActivityCallbackListener 
{	
	/** 
	 * This method is called from the ContentManager when all the 
	 * request operations has finished and the result of said operations
	 * is available.
	 * 
	 * The content fetched by the app is never returned to the calling Activity,
	 * but rather stored to and obtained from the storage.
	 * 
	 * Use this method to achieve navigation in the app, based on the result. 
	 * Such as start another activity when data is downloaded or similar.
	 * 
	 * @param fetchRequestResult - this variable holds detailed information reguarding the result of the request. 
	 * Either the request was sucessful, or not, then the reason is specified.
	 */
	public void onResult(final FetchRequestResultEnum fetchRequestResult);
}