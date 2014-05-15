package com.mitv.models.gson.mitvapi;

import com.mitv.models.objects.mitvapi.ImageSetSize;


public class ImageSetOrientationJSON 
{	
	protected ImageSetSize landscape;
	protected ImageSetSize portrait;

	
	
	public ImageSetOrientationJSON(
			String smallLandscape,
			String mediumLandscape,
			String largeLandscape,
			String smallPortrait,
			String mediumPortrait,
			String largePortrait) 
	{
		this.landscape = new ImageSetSize(smallLandscape, mediumLandscape, largeLandscape);
		
		this.portrait = new ImageSetSize(smallPortrait, mediumPortrait, largePortrait);
	}
	
	
	
	public ImageSetOrientationJSON() 
	{
		this.landscape = new ImageSetSize();
		
		this.portrait = new ImageSetSize();
	}
	
	
	
	public ImageSetSize getLandscape()
	{
		return landscape;
	}
	
	
	
	public ImageSetSize getPortrait() 
	{
		return portrait;
	}
}
