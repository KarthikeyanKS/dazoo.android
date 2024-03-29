package com.mitv.models.objects.mitvapi;

import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.UserLikeNextBroadcastJSON;

public class UserLikeNextBroadcast 
	extends UserLikeNextBroadcastJSON 
	implements GSONDataFieldValidation
{
	protected transient TVChannelId tvChannelIdObject;

	
	
	public TVChannelId getChannelId() 
	{
		if (tvChannelIdObject == null) 
		{
			tvChannelIdObject = new TVChannelId(getChannelIdString());
		}

		return tvChannelIdObject;
	}

	
	
	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areDataFieldsValid = (getChannelId() != null && !TextUtils.isEmpty(getChannelId().getChannelId()) && getBeginTimeMillis() != null);
		
		return areDataFieldsValid;
	}
}
