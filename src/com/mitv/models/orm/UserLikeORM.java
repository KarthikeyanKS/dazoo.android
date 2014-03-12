
package com.mitv.models.orm;



import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.enums.LikeTypeResponseEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.UserLike;
import com.mitv.models.UserLikeNextBroadcast;



@DatabaseTable()
public class UserLikeORM 
	extends AbstractOrmLiteClass<UserLikeORM>
{
	@DatabaseField(canBeNull = false, index = true)
	protected LikeTypeResponseEnum likeType;
	
	@DatabaseField(canBeNull = false, index = true)
	protected String title;
	
	@DatabaseField(canBeNull = false)
	protected Integer broadcastCount;
	
	/* This will be null if broadcastCount == 0 */
	@DatabaseField()
	protected UserLikeNextBroadcast nextBroadcast;
	
	/* This variable is used if likeType == "SERIES" */
	@DatabaseField()
	protected String seriesId;

	/* This variable is used if likeType == "SPORT_TYPE" */
	@DatabaseField()
	protected String sportTypeId;
	
	/* This variable is used if likeType == "PROGRAM" */
	@DatabaseField()
	protected ProgramTypeEnum programType;
	
	@DatabaseField()
	protected String programId;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "MOVIE" */
	@DatabaseField()
	protected String genre;
	
	@DatabaseField()
	protected Integer year;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "OTHER" */
	@DatabaseField()
	protected String category;
	
	
	@DatabaseField(columnName = "modifydate")
	public Date modifydate;
	
	
	
	public UserLikeORM(UserLike userLike)
	{
		this.likeType = userLike.getLikeType();
		this.title = userLike.getTitle();
		this.broadcastCount = userLike.getBroadcastCount();
		this.nextBroadcast = userLike.getNextBroadcast();
		this.seriesId = userLike.getSeriesId();
		this.sportTypeId = userLike.getSportTypeId();
		this.programType = userLike.getProgramType();
		this.programId = userLike.getProgramId();
		this.genre = userLike.getGenre();
		this.year = userLike.getYear();
		this.category = userLike.getCategory();
	}
	

	
	@Override
	protected void onBeforeSave()
	{
		this.modifydate = new Date();
	}



	@Override
	protected void onAfterSave() 
	{}
}