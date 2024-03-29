
package com.mitv.models.objects.mitvapi;



import com.mitv.models.gson.mitvapi.TVChannelIdJSON;



public class TVChannelId
	extends TVChannelIdJSON
{
	public TVChannelId(String channelId) 
	{
		this.channelId = channelId;
	}
	
	
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		return result;
	}

	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TVChannelId other = (TVChannelId) obj;
		if (channelId == null) {
			if (other.channelId != null) {
				return false;
			}
		} else if (!channelId.equals(other.channelId)) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public String toString()
	{
		return channelId;
	}
}