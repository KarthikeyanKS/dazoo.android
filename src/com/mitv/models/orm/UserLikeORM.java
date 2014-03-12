
package com.mitv.models.orm;



import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.enums.LikeTypeResponseEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.UserLike;
import com.mitv.models.UserLikeNextBroadcast;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



@DatabaseTable()
public class UserLikeORM 
	extends AbstractOrmLiteClassWithAsyncSave<UserLikeORM>
{
	@DatabaseField(canBeNull = false, index = true)
	private LikeTypeResponseEnum likeType;
	
	@DatabaseField(canBeNull = false, index = true)
	private String title;
	
	@DatabaseField(canBeNull = false)
	private Integer broadcastCount;
	
	/* This will be null if broadcastCount == 0 */
	@DatabaseField()
	private UserLikeNextBroadcast nextBroadcast;
	
	/* This variable is used if likeType == "SERIES" */
	@DatabaseField()
	private String seriesId;

	/* This variable is used if likeType == "SPORT_TYPE" */
	@DatabaseField()
	private String sportTypeId;
	
	/* This variable is used if likeType == "PROGRAM" */
	@DatabaseField()
	private ProgramTypeEnum programType;
	
	@DatabaseField()
	private String programId;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "MOVIE" */
	@DatabaseField()
	private String genre;
	
	@DatabaseField()
	private Integer year;
	
	/* This variable is used if likeType == "PROGRAM" and programType is "OTHER" */
	@DatabaseField()
	private String category;
	
	
	
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
}