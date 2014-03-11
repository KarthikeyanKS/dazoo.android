
package com.mitv.interfaces;



import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;



/**
 * This interface is to be implemented by the ContentManager, in order to pass data back from
 * the async tasks, to the ContentManager.
 * @author consultant_hdme
 *
 */
public interface ContentCallbackListener
{	
	/** 
	 * This method is called by the AsyncTask in the onPostExecute method. After JSON data has been parsed.
	 * It is used to notify the content manager when the request has finished (successful or not).
	 * 
	 * @param requestIdentifier - an enum for identifying the type of the request, e.g. "get channels" or "get guide"
	 * @param result - This contains information about the result of the request, either success or some detail about how it failed.
	 * @param content - the data object/model, parsed from JSON. Can be null if 'successful' = false, but never otherwise.
	 */
	public void onResult(ActivityCallbackListener activityCallbackListener, RequestIdentifierEnum requestIdentifier, FetchRequestResultEnum result, Object content);
}