package com.millicom.mitv.models.gson;


public class ImageSetOrientation {
	
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

	

	public void setLandscape(ImageSetSize landscape) {
		this.landscape = landscape;
	}

	public void setPortrait(ImageSetSize portrait) {
		this.portrait = portrait;
	}
	
	
}
