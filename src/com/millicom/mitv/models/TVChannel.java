
package com.millicom.mitv.models;



import junit.framework.Assert;
import android.text.TextUtils;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.TVChannelJSON;



public class TVChannel
	extends TVChannelJSON implements GSONDataFieldValidation
{
	public void setChannelId(String channelId) 
	{
		this.channelId = channelId;
	}
	
	
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	
	
	public void setAllImageUrls(String imageUrl) 
	{
		ImageSetSize images = new ImageSetSize(imageUrl, imageUrl, imageUrl);
		
		this.logo = images;
	}



	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getName()) && getChannelId() != null && !TextUtils.isEmpty(getChannelId().getChannelId()) && 
				getLogo() != null && !TextUtils.isEmpty(getLogo().getSmall()) && !TextUtils.isEmpty(getLogo().getMedium()) && 
				!TextUtils.isEmpty(getLogo().getLarge()));
		
		return areDataFieldsValid;
	}
}