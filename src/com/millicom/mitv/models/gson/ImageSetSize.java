


package com.millicom.mitv.models.gson;



import java.io.Serializable;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import android.util.DisplayMetrics;



public class ImageSetSize {
	@SuppressWarnings("unused")
	private static final String TAG = ImageSetSize.class.getName();
	
	
	
	private String smallImageURI;
	private String mediumImageURI;
	private String largeImageURI;	
	
	
	public ImageSetSize(
			String smallImageURI,
			String mediumImageURI,
			String largeImageURI)
	{
		this.smallImageURI = smallImageURI;
		this.mediumImageURI = mediumImageURI;
		this.largeImageURI = largeImageURI;
	}
	
	public ImageSetSize()
	{
		this.smallImageURI = "";
		this.mediumImageURI = "";
		this.largeImageURI = "";
	}

	
	
	public String getImageURLForDensityDPI(int densityDpi)
	{
		String imageURL = null;
		
		switch(densityDpi)
		{
			case DisplayMetrics.DENSITY_LOW:
				imageURL = smallImageURI;
				break;
	
			case DisplayMetrics.DENSITY_MEDIUM:
				imageURL = mediumImageURI;
				break;
	
			case DisplayMetrics.DENSITY_HIGH:
				imageURL = largeImageURI;
				break;
	
			default:
				imageURL = largeImageURI;
				break;
		}
		
		return imageURL;
	}



	public String getSmallImageURI() 
	{
		return smallImageURI;
	}


	
	public String getMediumImageURI() 
	{
		return mediumImageURI;
	}

	

	public String getLargeImageURI() 
	{
		return largeImageURI;
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Small image:");
		sb.append(smallImageURI);
		sb.append("\n");
		sb.append("Medium image: ");
		sb.append(mediumImageURI);
		sb.append("\n");
		sb.append("Large image: ");
		sb.append(largeImageURI);
		
		return sb.toString();
	}


	public void setSmallImageURI(String smallImageURI) {
		this.smallImageURI = smallImageURI;
	}

	public void setMediumImageURI(String mediumImageURI) {
		this.mediumImageURI = mediumImageURI;
	}

	public void setLargeImageURI(String largeImageURI) {
		this.largeImageURI = largeImageURI;
	}
	
	
}
