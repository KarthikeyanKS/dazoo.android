
package com.mitv.models.objects.mitvapi;



import com.mitv.models.gson.mitvapi.ImageSetSizeJSON;



public class ImageSetSize 
	extends ImageSetSizeJSON 
{
	public ImageSetSize(){}
	
	
	
	public ImageSetSize(
			final String small,
			final String medium,
			final String large)
	{
		this.small = small;
		this.medium = medium;
		this.large = large;
	}
	
	
	
	public boolean containsImages()
	{
		return (getSmall().isEmpty() == false &&
				getMedium().isEmpty() == false &&
				getLarge().isEmpty() == false);
	}
}