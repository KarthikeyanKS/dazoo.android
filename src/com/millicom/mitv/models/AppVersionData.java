package com.millicom.mitv.models;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class AppVersionData implements Serializable, JsonDeserializer<AppVersionData>{

	private static final long serialVersionUID = 6136783665576140935L;

	@Override
	public AppVersionData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return null;
	}

	//TODO create pojo maching http://android.api.mi.tv/versions
	
	//TODO maybe we need to rename this
	public String getApiVersion() {
		return null;
	}
}
