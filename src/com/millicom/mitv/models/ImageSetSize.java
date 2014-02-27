
package com.millicom.mitv.models;



import com.millicom.mitv.models.gson.ImageSetSizeJSON;
import com.mitv.model.NotificationDbItem;



public class ImageSetSize 
	extends ImageSetSizeJSON 
{
	public ImageSetSize(){}
	
	
	
	public ImageSetSize(NotificationDbItem item)
	{
		this.small = item.getChannelLogoSmall();
		this.medium = item.getChannelLogoMedium();
		this.large = item.getChannelLogoLarge();
	}
	

	
	public ImageSetSize(
			String small,
			String medium,
			String large)
	{
		this.small = small;
		this.medium = medium;
		this.large = large;
	}
}