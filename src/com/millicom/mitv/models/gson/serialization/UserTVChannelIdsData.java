
package com.millicom.mitv.models.gson.serialization;



import java.util.List;
import com.millicom.mitv.models.TVChannelId;



public class UserTVChannelIdsData 
{
	private List<TVChannelId> channelIds;
	
	
	
	public UserTVChannelIdsData()
	{}



	public List<TVChannelId> getChannelIds() {
		return channelIds;
	}



	public void setChannelIds(List<TVChannelId> channelIds) {
		this.channelIds = channelIds;
	}
}