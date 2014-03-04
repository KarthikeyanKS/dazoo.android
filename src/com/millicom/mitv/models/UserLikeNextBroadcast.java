package com.millicom.mitv.models;

import android.text.TextUtils;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.UserLikeNextBroadcastJSON;

public class UserLikeNextBroadcast extends UserLikeNextBroadcastJSON implements GSONDataFieldValidation {

	protected transient TVChannelId tvChannelIdObject;

	public TVChannelId getChannelId() {
		if (tvChannelIdObject == null) {
			tvChannelIdObject = new TVChannelId(channelId);
		}

		return tvChannelIdObject;
	}

	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (getChannelId() != null && !TextUtils.isEmpty(getChannelId().getChannelId()) && getBeginTimeMillis() != null);
		return areDataFieldsValid;
	}
}
