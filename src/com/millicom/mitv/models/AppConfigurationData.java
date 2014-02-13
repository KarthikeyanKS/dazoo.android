package com.millicom.mitv.models;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class AppConfigurationData implements Serializable, JsonDeserializer<AppConfigurationData>{

	private static final long serialVersionUID = -1644608436707247798L;

	@Override
	public AppConfigurationData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return null;
	}

}
