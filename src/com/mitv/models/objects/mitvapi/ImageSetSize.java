
package com.mitv.models.objects.mitvapi;



import com.mitv.models.gson.mitvapi.ImageSetSizeJSON;
import com.mitv.models.sql.NotificationSQLElement;



public class ImageSetSize 
	extends ImageSetSizeJSON 
{
	public ImageSetSize(){}
	
	
	
	public ImageSetSize(NotificationSQLElement item)
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
	
	
	
	public boolean containsImages()
	{
		return (small != null && small.isEmpty() == false &&
				medium != null && medium.isEmpty() == false &&
				large != null && large.isEmpty() == false);
	}
}