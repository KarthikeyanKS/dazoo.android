package com.millicom.mitv.models;

import com.millicom.mitv.models.gson.ImageSetSizeJSON;

public class ImageSetSize extends ImageSetSizeJSON {

	public ImageSetSize(
			String small,
			String medium,
			String large)
	{
		this.small = small;
		this.medium = medium;
		this.large = large;
	}
	
	public ImageSetSize(){}
}
