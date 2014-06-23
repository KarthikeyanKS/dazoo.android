
package com.mitv.models.gson.mitvapi;



import android.util.Log;

import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import com.mitv.models.objects.mitvapi.ImageSetSize;



public class ImageSetOrientationJSON
	extends BaseObjectJSON
{	
	private static final String TAG = ImageSetOrientationJSON.class.getName();
	
	
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
	
	
	
	public ImageSetOrientationJSON(){}
	
	
	
	public ImageSetSize getLandscape()
	{
		if(landscape == null)
		{
			landscape = new ImageSetSize();
			
			Log.w(TAG, "landscape is null");
		}
		
		return landscape;
	}
	
	
	
	public ImageSetSize getPortrait() 
	{
		if(portrait == null)
		{
			portrait = new ImageSetSize();
			
			Log.w(TAG, "portrait is null");
		}
		
		return portrait;
	}
}
