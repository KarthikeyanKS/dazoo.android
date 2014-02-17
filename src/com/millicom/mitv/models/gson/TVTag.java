package com.millicom.mitv.models.gson;

import java.io.Serializable;

public class TVTag implements Serializable {

	private static final long serialVersionUID = 5062447937304870213L;

	private String id;
	private String displayName;
	
	public String getId() {
		return id;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	
}
