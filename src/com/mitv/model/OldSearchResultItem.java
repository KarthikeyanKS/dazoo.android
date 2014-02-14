package com.mitv.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.millicom.mitv.enums.ContentTypeEnum;
import com.mitv.Consts;
import com.mitv.manager.ContentParser;

public class OldSearchResultItem {
	private String displayText;
	private ContentTypeEnum entityType;
	private ArrayList<OldBroadcast> broadcasts;
	private Object entity;

	public OldBroadcast getNextBroadcast() {
		OldBroadcast broadcast = null;
		
		if(broadcasts != null) {
			if(broadcasts.size() > 0) {
				broadcast = broadcasts.get(0);
			}
		}
	
		return broadcast;
	}
	
	public OldSearchResultItem(JSONObject jsonObject) {
		String displayText = jsonObject.optString(Consts.JSON_KEY_SEARCH_RESULT_ITEM_DISPLAY_TEXT);
		String entityTypeString = jsonObject.optString(Consts.JSON_KEY_SEARCH_RESULT_ITEM_ENTITY_TYPE);
		ContentTypeEnum entityType = ContentTypeEnum.valueOf(entityTypeString);

		Object entity = null;
		JSONObject entityJsonObject;
		try {
			entityJsonObject = jsonObject.getJSONObject(Consts.JSON_KEY_SEARCH_RESULT_ITEM_ENTITY);

			boolean parseBroadcasts = false;
			switch (entityType) {
			case SERIES: {
				entity = new OldSeries(entityJsonObject);
				parseBroadcasts = true;
				break;
			}
			case CHANNEL: {
				entity = new OldTVChannel(entityJsonObject);
				break;
			}
			case PROGRAM: {
				entity = new OldProgram(entityJsonObject);
				parseBroadcasts = true;
				break;
			}
			}
			
			if(parseBroadcasts) {
				JSONArray broadcastsJson = entityJsonObject.getJSONArray(Consts.FEED_ITEM_BROADCASTS);
				if(broadcastsJson != null) {
					ArrayList<OldBroadcast> broadcasts = ContentParser.parseBroadcasts(broadcastsJson);
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

	public ArrayList<OldBroadcast> getBroadcasts() {
		return broadcasts;
	}

	public void setBroadcasts(ArrayList<OldBroadcast> broadcasts) {
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

	public ContentTypeEnum getEntityType() {
		return entityType;
	}

	public void setEntityType(ContentTypeEnum entityType) {
		this.entityType = entityType;
	}

}
