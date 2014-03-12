package com.mitv.models.orm;

import java.util.ArrayList;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVDate;
import com.mitv.models.TVGuide;

public class TVGuideORM extends AbstractOrmLiteClass<TVGuideORM> {
	
	/* TVDate */
	@DatabaseField()
	protected String id;
	
	@DatabaseField()
	protected String date;
	
	@DatabaseField()
	protected String displayName;
	
	@DatabaseField()
	private ArrayList<TVChannelGuide> tvChannelGuides;

	
	/* Date */
	@DatabaseField(columnName = "modifydate")
	public Date modifydate;
	
	
	public TVGuideORM(TVGuide tvGuide) {
		TVDate tvDate = tvGuide.getTvDate();
		this.id = tvDate.getId();
		//this.date = not save??
		this.displayName = tvDate.getDisplayName();
		
		this.tvChannelGuides = tvGuide.getTvChannelGuides();
	}

	@Override
	protected void onBeforeSave() {
		this.modifydate = new Date();
	}

	@Override
	protected void onAfterSave() {}

}
