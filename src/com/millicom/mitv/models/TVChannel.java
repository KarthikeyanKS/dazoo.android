
package com.millicom.mitv.models;



import com.millicom.mitv.models.gson.TVChannelJSON;



public class TVChannel
	extends TVChannelJSON
{
	public void setChannelId(String channelId) 
	{
		this.channelId = channelId;
	}
	
	
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	
	
	public void setAllImageUrls(String imageUrl) 
	{
		ImageSetSize images = new ImageSetSize(imageUrl, imageUrl, imageUrl);
		
		this.logo = images;
	}
}