package com.millicom.mitv.models;

import java.util.ArrayList;

import com.mitv.model.OldTVChannelGuide;
import com.mitv.model.OldTVDate;

/**
 * This is an object, not created using JSON.
 * @author consultant_hdme
 *
 */
public class TVGuide {

	private OldTVDate tvDate;
	private ArrayList<OldTVChannelGuide> tvChannelGuides;
	
	public TVGuide(OldTVDate tvDate, ArrayList<OldTVChannelGuide> tvChannelGuides) {
		this.tvDate = tvDate;
		this.tvChannelGuides = tvChannelGuides;
	}
	
	public OldTVDate getTvDate() {
		return tvDate;
	}

	public ArrayList<OldTVChannelGuide> getTvChannelGuides() {
		return tvChannelGuides;
	}

}
