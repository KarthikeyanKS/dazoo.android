package com.millicom.mitv.models;

import com.millicom.mitv.models.gson.TVTagJSON;

public class TVTag extends TVTagJSON {
	
	public TVTag(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}
}
