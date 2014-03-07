
package com.mitv.models;



import java.util.ArrayList;



/**
 * This is an object, not created using JSON.
 * @author consultant_hdme
 *
 */
public class TVGuide 
{
	private TVDate tvDate;
	private ArrayList<TVChannelGuide> tvChannelGuides;

	
	
	public TVGuide(TVDate tvDate, ArrayList<TVChannelGuide> tvChannelGuides) 
	{
		this.tvDate = tvDate;
		this.tvChannelGuides = tvChannelGuides;
	}
	
	public TVDate getTvDate() {
		return tvDate;
	}

	public ArrayList<TVChannelGuide> getTvChannelGuides() {
		return tvChannelGuides;
	}

}