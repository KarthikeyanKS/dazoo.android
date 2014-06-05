
package com.mitv.models.objects.mitvapi;



import com.mitv.models.gson.mitvapi.ImageSetSizeJSON;



public class ImageSetSize 
	extends ImageSetSizeJSON 
{
	public ImageSetSize(){}
	
	
	
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