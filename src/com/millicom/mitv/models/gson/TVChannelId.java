package com.millicom.mitv.models.gson;


public class TVChannelId {

	private String channelId;

	public TVChannelId(){}

	public TVChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public String getChannelId() {
		return channelId;
	}

	@Override
	public int hashCode() {
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
	
}
