
package com.mitv.models.orm;



import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.ImageSetOrientation;
import com.mitv.models.objects.mitvapi.TVCredit;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.models.objects.mitvapi.TVSeries;
import com.mitv.models.objects.mitvapi.TVSeriesSeason;
import com.mitv.models.objects.mitvapi.TVSportType;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



@DatabaseTable()
public class TVProgramORM 
	extends AbstractOrmLiteClassWithAsyncSave<TVProgram> 
{
	private static final String TAG = TVProgramORM.class.getName();
	
	
	
	@DatabaseField()
	protected ProgramTypeEnum programType;
	
	@DatabaseField(id=true)
	protected String programId;
	
	@DatabaseField()
	protected String title;
	
	@DatabaseField()
	protected String synopsisShort;
	
	@DatabaseField()
	protected String synopsisLong;
	
	@DatabaseField()
	protected ImageSetOrientation images;
	
	@ForeignCollectionField(eager = true)
	protected List<String> tags;
	
	@ForeignCollectionField(eager = true)
	protected List<TVCredit> credits;
	
	@DatabaseField()
	protected String category; 
	
	@DatabaseField()
	protected TVSeries series;
	
	@DatabaseField()
	protected TVSeriesSeason season;

	@DatabaseField()
	protected Integer episodeNumber;
	
	@DatabaseField()
	protected Integer year;
	
	@DatabaseField()
	protected String genre;
	
	@DatabaseField()
	protected TVSportType sportType;

	@DatabaseField()
	protected String tournament;
	
	
	
	private TVProgramORM(){}
	
	
	
	public TVProgramORM(TVProgram program)
	{
		this.programType = program.getProgramType();
		this.programId = program.getProgramId();
		this.title = program.getTitle();
		this.synopsisShort = program.getSynopsisShort();
		this.synopsisLong = program.getSynopsisLong();
		this.images = program.getImages();
		this.tags = program.getTags();
		this.credits = program.getCredits();
		this.season = program.getSeason();
		this.episodeNumber = program.getEpisodeNumber();
		this.year = program.getYear();
		this.genre = program.getGenre();
		this.sportType = program.getSportType();
		this.tournament = program.getTournament();
	}
	
	
	
	public static TVProgram getTVProgramByID(String id) 
	{
		TVProgram data;
		
		TVProgramORM ormData = new TVProgramORM().getTVProgramORM(id);
		
		if(ormData != null)
		{
			data = new TVProgram(ormData);
			
			return data;
		}
		else
		{
			return null;
		}
	}
	

	
	private TVProgramORM getTVProgramORM(String id) 
	{
		List<TVProgramORM> data;
		
		try 
		{
			@SuppressWarnings("unchecked")
			QueryBuilder<TVProgramORM, Integer> queryBuilder = (QueryBuilder<TVProgramORM, Integer>) getDao().queryBuilder();
			
			data = (List<TVProgramORM>) queryBuilder.query();
		} 
		catch (SQLException sqlex) 
		{
			data = null;
			
			Log.w(TAG, sqlex.getMessage(), sqlex);
		}
		
		if(data != null && data.isEmpty() == false)
		{
			return data.get(0);
		}
		else
		{
			return null;
		}
	}



	public ProgramTypeEnum getProgramType() {
		return programType;
	}



	public String getProgramId() {
		return programId;
	}



	public String getTitle() {
		return title;
	}



	public String getSynopsisShort() {
		return synopsisShort;
	}



	public String getSynopsisLong() {
		return synopsisLong;
	}



	public ImageSetOrientation getImages() {
		return images;
	}



	public List<String> getTags() {
		return tags;
	}



	public List<TVCredit> getCredits() {
		return credits;
	}



	public String getCategory() {
		return category;
	}



	public TVSeries getSeries() {
		return series;
	}



	public TVSeriesSeason getSeason() {
		return season;
	}



	public Integer getEpisodeNumber() {
		return episodeNumber;
	}



	public Integer getYear() {
		return year;
	}



	public String getGenre() {
		return genre;
	}



	public TVSportType getSportType() {
		return sportType;
	}



	public String getTournament() {
		return tournament;
	}
}