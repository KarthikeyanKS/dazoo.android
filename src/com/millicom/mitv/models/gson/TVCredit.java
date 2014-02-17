package com.millicom.mitv.models.gson;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class TVCredit implements JsonDeserializer<TVCredit>{
	
	private String name;
	private String type;
	
	public TVCredit() {
		
	}
	
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}

	@Override
	public TVCredit deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		return null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
