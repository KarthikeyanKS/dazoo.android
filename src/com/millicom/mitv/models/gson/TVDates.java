package com.millicom.mitv.models.gson;

import java.io.Serializable;
import java.util.Date;

public class TVDates implements Serializable{

	private static final long serialVersionUID = 6577684283992548155L;
	
	private String id;
	private Date date;
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
	
}
