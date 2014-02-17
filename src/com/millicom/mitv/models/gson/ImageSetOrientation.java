package com.millicom.mitv.models.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ImageSetOrientation implements JsonDeserializer<ImageSetOrientation> {
	
	private ImageSetSize landscape;
	private ImageSetSize portrait;
	
	public ImageSetOrientation(
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
	
	public ImageSetOrientation() 
	{
		this.landscape = new ImageSetSize();
		
		this.portrait = new ImageSetSize();
	}
	
	public ImageSetSize getLandscape() {
		return landscape;
	}
	public ImageSetSize getPortrait() {
		return portrait;
	}

	@Override
	public ImageSetOrientation deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLandscape(ImageSetSize landscape) {
		this.landscape = landscape;
	}

	public void setPortrait(ImageSetSize portrait) {
		this.portrait = portrait;
	}
	
	
}
