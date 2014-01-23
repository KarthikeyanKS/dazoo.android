package com.millicom.secondscreen.content.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.ENTITY_TYPE;
import com.millicom.secondscreen.manager.ContentParser;

public class SearchResultItem {
	private String displayText;
	private ENTITY_TYPE entityType;
	private ArrayList<Broadcast> broadcasts;
	private Object entity;

	public SearchResultItem(JSONObject jsonObject) {
		String displayText = jsonObject.optString(Consts.JSON_KEY_SEARCH_RESULT_ITEM_DISPLAY_TEXT);
		String entityTypeString = jsonObject.optString(Consts.JSON_KEY_SEARCH_RESULT_ITEM_ENTITY_TYPE);
		ENTITY_TYPE entityType = ENTITY_TYPE.valueOf(entityTypeString);

		Object entity = null;
		JSONObject entityJsonObject;
		try {
			entityJsonObject = jsonObject.getJSONObject(Consts.JSON_KEY_SEARCH_RESULT_ITEM_ENTITY);

			boolean parseBroadcasts = false;
			switch (entityType) {
			case SERIES: {
				entity = new Series(entityJsonObject);
				parseBroadcasts = true;
				break;
			}
			case CHANNEL: {
				entity = new Channel(entityJsonObject);
				break;
			}
			case PROGRAM: {
				entity = new Program(entityJsonObject);
				parseBroadcasts = true;
				break;
			}
			}
			
			if(parseBroadcasts) {
				JSONArray broadcastsJson = entityJsonObject.getJSONArray(Consts.DAZOO_FEED_ITEM_BROADCASTS);
				if(broadcastsJson != null) {
					ArrayList<Broadcast> broadcasts = ContentParser.parseBroadcasts(broadcastsJson);
					this.setBroadcasts(broadcasts);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		this.setDisplayText(displayText);
		this.setEntityType(entityType);
		this.setEntity(entity);
	}

	public String getDisplayText() {
		return displayText;
	}

	public ArrayList<Broadcast> getBroadcasts() {
		return broadcasts;
	}

	public void setBroadcasts(ArrayList<Broadcast> broadcasts) {
		this.broadcasts = broadcasts;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public ENTITY_TYPE getEntityType() {
		return entityType;
	}

	public void setEntityType(ENTITY_TYPE entityType) {
		this.entityType = entityType;
	}

}
