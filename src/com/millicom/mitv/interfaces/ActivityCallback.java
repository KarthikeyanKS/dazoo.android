package com.millicom.mitv.interfaces;

public interface ActivityCallback {
	
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
	 * @param successful - boolean telling if the request was performed successfully or not.
	 * @param message - a message that corresponds to the result of the HTTP request.
	 */
	public void onResult(final boolean successful, final String message);
}
