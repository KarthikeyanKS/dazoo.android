
package com.mitv.models.objects.mitvapi;



import android.text.TextUtils;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVChannelJSON;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TVChannel other = (TVChannel) obj;
		if (channelId == null) {
			if (other.channelId != null)
				return false;
		} else if (!channelId.equals(other.channelId))
			return false;
		return true;
	}

	
	
	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getName()) && getChannelId() != null && !TextUtils.isEmpty(getChannelId().getChannelId()) && 
				getLogo() != null && getLogo().containsImages());

		return areDataFieldsValid;
	}
}