
package com.mitv.models.orm;



import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;
import com.mitv.models.TVChannelGuide;
import com.mitv.models.TVDate;
import com.mitv.models.TVGuide;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



public class TVGuideORM 
	extends AbstractOrmLiteClassWithAsyncSave<TVGuideORM> 
{	
	/* TVDate */
	@DatabaseField()
	protected String id;
	
	@DatabaseField()
	protected String date;
	
	@DatabaseField()
	protected String displayName;
	
	@DatabaseField()
	private ArrayList<TVChannelGuide> tvChannelGuides;

	
	
	public TVGuideORM(TVGuide tvGuide) 
	{
		TVDate tvDate = tvGuide.getTvDate();
		
		this.id = tvDate.getId();
		
		this.displayName = tvDate.getDisplayName();
		
		this.tvChannelGuides = tvGuide.getTvChannelGuides();
	}
}
