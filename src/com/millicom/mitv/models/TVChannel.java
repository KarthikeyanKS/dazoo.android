
package com.millicom.mitv.models;



import com.millicom.mitv.models.gson.TVChannelJSON;
import com.mitv.notification.NotificationSQLElement;



public class TVChannel
	extends TVChannelJSON
{
	public TVChannel()
	{}
	
	
	public TVChannel(NotificationSQLElement item)
	{
		this.channelId = item.getChannelId();
		this.name = item.getChannelName();
		this.logo = new ImageSetSize(item);
	}
	
	
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