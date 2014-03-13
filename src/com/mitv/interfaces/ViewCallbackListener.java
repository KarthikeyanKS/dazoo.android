
package com.mitv.interfaces;



import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;



public interface ViewCallbackListener 
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
	 * @param fetchRequestResult - This variable holds detailed information reguarding the result of the request. 
	 * Either the request was sucessful, or not, then the reason is specified.
	 * 
	 * @param requestIdentifier - Thi variable holds the original request identifier for the request that was performed.
	 */
	public void onResult(final FetchRequestResultEnum fetchRequestResult, final RequestIdentifierEnum requestIdentifier);
}