
package com.mitv.models.gson;



import com.mitv.models.ImageSetSize;



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


	
	public String getImageUrl()
	{
		return logo.getImageURLForDeviceDensityDPI();
	}
}