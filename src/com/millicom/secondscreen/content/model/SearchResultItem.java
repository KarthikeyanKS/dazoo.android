package com.millicom.secondscreen.content.model;

import org.json.JSONObject;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.ITEM_TYPE;

public class SearchResultItem {
	private String displayText;
	private ITEM_TYPE itemType;
	
	public SearchResultItem(JSONObject jsonObject) {
		String displayText = jsonObject.optString(Consts.JSON_KEY_SEARCH_RESULT_ITEM_DISPLAY_TEXT);
		String itemTypeString = jsonObject.optString(Consts.JSON_KEY_SEARCH_RESULT_ITEM_TYPE);
		ITEM_TYPE itemType = ITEM_TYPE.valueOf(itemTypeString);
		
		this.setDisplayText(displayText);
		this.setItemType(itemType);
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public ITEM_TYPE getItemType() {
		return itemType;
	}

	public void setItemType(ITEM_TYPE itemType) {
		this.itemType = itemType;
	}
	
}
