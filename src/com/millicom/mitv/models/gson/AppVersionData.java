package com.millicom.mitv.models.gson;

import java.io.Serializable;
import java.util.List;

public class AppVersionData implements Serializable {

	private static final long serialVersionUID = 6136783665576140935L;
	
	private List<AppVersionDataPart> appDataList;

	
	//TODO maybe we need to rename this
	public String getApiVersion() {
		return null;
	}
}
