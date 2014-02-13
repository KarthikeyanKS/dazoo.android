package com.millicom.mitv.interfaces;

import com.millicom.mitv.enums.RequestIdentifierEnum;

/**
 * This interface is to be implemented by the ContentManager, in order to pass data back from
 * the async tasks, to the ContentManager.
 * @author consultant_hdme
 *
 */
public interface ContentCallback {
	
	/** 
	 * This method is called by the AsyncTask in the onPostExecute method. After JSON data has been parsed.
	 * It is used to notify the content manager when the request has finished (successful or not).
	 * 
	 * @param successful - boolean telling if the request was performed successfully or not.
	 * @param requestIdentifier - an enum for identifying the type of the request, e.g. "get channels" or "get guide"
	 * @param httpResponseCode - the response code returned by the HTTPCore for this request
	 * @param data - the data object/model, parsed from JSON. Can be null if 'successful' = false, but never otherwise.
	 */
	public void onResult(ActivityCallback activityCallback, boolean successful, RequestIdentifierEnum requestIdentifier, int httpResponseCode, Object data);

}
