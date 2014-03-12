
package com.mitv.models.orm;



import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.models.TVTag;



@DatabaseTable()
public class TVTagORM 
	extends AbstractOrmLiteClass<TVTagORM>
{
	@DatabaseField(id = true, index = true)
	private String id;
	
	@DatabaseField()
	private String displayName;
	
	@DatabaseField(columnName = "modifydate")
	public Date modifydate;
	
	
	
	public TVTagORM(TVTag tvTag)
	{
		this.id = tvTag.getId();
		this.displayName = tvTag.getDisplayName();
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