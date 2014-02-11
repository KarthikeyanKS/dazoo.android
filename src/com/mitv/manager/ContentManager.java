
package com.mitv.manager;



public abstract class ContentManager 
{
	/*
	 * This fetches all 97 channels from webservice
	 */
	public static void updateContent()
	{
		ApiClient.setmShouldRefreshGuide(true);
	}
}
