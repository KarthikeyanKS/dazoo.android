
package com.mitv.models.gson.mitvapi;



import android.util.DisplayMetrics;
import android.util.Log;

import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import com.mitv.utilities.GenericUtils;



public class ImageSetSizeJSON 
	extends BaseObjectJSON
{
	private static final String TAG = ImageSetSizeJSON.class.getName();
	
	
	protected String small;
	protected String medium;
	protected String large;	
	
	
	
	public ImageSetSizeJSON(
			String small,
			String medium,
			String large)
	{
		this.small = small;
		this.medium = medium;
		this.large = large;
	}
	
	
	
	public ImageSetSizeJSON()
	{
		this.small = "";
		this.medium = "";
		this.large = "";
	}
	
	
	
	public String getImageURLForDeviceDensityDPI()
	{
		String imageURL = null;
		
		int densityDpi = GenericUtils.geDeviceDensityDPI();
		
		switch(densityDpi)
		{
			case DisplayMetrics.DENSITY_LOW:
				imageURL = small;
				break;
	
			case DisplayMetrics.DENSITY_MEDIUM:
				imageURL = medium;
				break;
	
			case DisplayMetrics.DENSITY_HIGH:
			case DisplayMetrics.DENSITY_XHIGH:
			case DisplayMetrics.DENSITY_XXHIGH:
			case DisplayMetrics.DENSITY_XXXHIGH:
				imageURL = large;
				break;
				
			default:
				imageURL = large;
				break;
		}
		
		return imageURL;
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Small image:");
		sb.append(small);
		sb.append("\n");
		sb.append("Medium image: ");
		sb.append(medium);
		sb.append("\n");
		sb.append("Large image: ");
		sb.append(large);
		
		return sb.toString();
	}



	public String getSmall() 
	{
		if(small == null)
		{
			small = "";
			
			Log.w(TAG, "small is null");
		}
		
		return small;
	}



	public String getMedium() 
	{
		if(medium == null)
		{
			medium = "";
			
			Log.w(TAG, "medium is null");
		}
		
		return medium;
	}



	public String getLarge() 
	{
		if(large == null)
		{
			large = "";
			
			Log.w(TAG, "large is null");
		}
		
		return large;
	}	
}
