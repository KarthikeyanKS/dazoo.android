package com.millicom.mitv.models.gson;

import java.io.Serializable;
import java.util.Date;

public class AppVersionDataPart implements Serializable {

	private static final long serialVersionUID = 6136783665576140935L;
	
	private String name;
	private String value;
	private Date expires;
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public Date getExpires() {
		return expires;
	}
	
}
