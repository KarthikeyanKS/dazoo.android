
package com.mitv.models;



import java.util.Comparator;

import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.TVChannelJSON;
import com.mitv.models.sql.NotificationSQLElement;



public class TVChannel
	extends TVChannelJSON 
	implements GSONDataFieldValidation
{
	protected transient TVChannelId tvChannelIdObject;
	
	public TVChannel(){}
	
	public TVChannelId getChannelId() 
	{
		if(tvChannelIdObject == null) 
		{
			tvChannelIdObject = new TVChannelId(channelId);
		}
		
		return tvChannelIdObject;
	}


	public TVChannel(NotificationSQLElement item)
	{
		this.channelId = item.getChannelId();
		this.name = item.getChannelName();
		this.logo = new ImageSetSize(item);
	}


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

	public static class ChannelComparatorByName implements Comparator<TVChannel> {
		@Override
		public int compare(TVChannel lhs, TVChannel rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}
	}
}