package com.millicom.mitv.models.gson;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mitv.Consts;
import com.mitv.utilities.DateUtilities;

public class TVDates implements JsonDeserializer<TVDates> {

	private String id;
	private transient Date date;
	private String displayName;
	
	public String getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	@Override
    public TVDates deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {	
		
		/* Custom parsing of some variable fields */
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonElement dateJsonElement = jsonObject.get(Consts.DATE_DATE);
		String dateString = dateJsonElement.getAsString();
		
		SimpleDateFormat dfmInput = DateUtilities.getDateFormat(Consts.TVDATE_DATE_FORMAT);
		Date date = null;
		try {
			date = dfmInput.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		this.date = date;
		
		/* Automatic parsing of non transient variable fields, store in tmp object */
		Gson gson = new Gson();
		TVDates tvDate = gson.fromJson(jsonElement, TVDates.class);
		this.id = tvDate.id;
		this.displayName = tvDate.displayName;
		
		return this;
	}
	
}
