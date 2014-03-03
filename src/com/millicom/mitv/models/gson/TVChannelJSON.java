
package com.millicom.mitv.models.gson;



import com.millicom.mitv.models.ImageSetSize;



public class TVChannelJSON
{
	protected String channelId;
	protected String name;
	protected ImageSetSize logo;
	
	
	public String getName() 
	{
		return name;
	}

	
	
	public ImageSetSize getLogo() 
	{
		return logo;
	}


	// TODO NewArc -  Replace with an implementation that takes the screen resolution into account
	/* Partially implemented method */
	public String getImageUrl()
	{
		// Should we use getImageURLForDensityDPI instead?
		//String imageUrl = logo.getImageURLForDensityDPI(densityDpi)
		
		return logo.getLarge();
	}
}
