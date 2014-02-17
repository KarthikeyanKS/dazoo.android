package com.millicom.mitv.models.gson;

import java.io.Serializable;

public class Logo implements Serializable {

	private static final long serialVersionUID = -8393293523562617131L;
	
	private String small;
	private String medium;
	private String large;
	
	public String getSmall() {
		return small;
	}
	public void setSmall(String small) {
		this.small = small;
	}
	public String getMedium() {
		return medium;
	}
	public void setMedium(String medium) {
		this.medium = medium;
	}
	public String getLarge() {
		return large;
	}
	public void setLarge(String large) {
		this.large = large;
	}

	
}
