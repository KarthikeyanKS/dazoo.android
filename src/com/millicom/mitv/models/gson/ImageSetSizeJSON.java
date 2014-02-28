


package com.millicom.mitv.models.gson;



import android.util.DisplayMetrics;



public class ImageSetSizeJSON 
{
	@SuppressWarnings("unused")
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
	
	public String getImageURLForDensityDPI(int densityDpi)
	{
		String imageURL = null;
		
		switch(densityDpi)
		{
			case DisplayMetrics.DENSITY_LOW:
				imageURL = small;
				break;
	
			case DisplayMetrics.DENSITY_MEDIUM:
				imageURL = medium;
				break;
	
			case DisplayMetrics.DENSITY_HIGH:
				imageURL = large;
				break;
	
			default:
				imageURL = large;
				break;
		}
		
		return imageURL;
	}
	
	public String getSmall() {
		return small;
	}

	public String getMedium() {
		return medium;
	}

	public String getLarge() {
		return large;
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
}
