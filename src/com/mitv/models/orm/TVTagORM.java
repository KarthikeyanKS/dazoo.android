
package com.mitv.models.orm;



import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mitv.models.TVTag;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



@DatabaseTable()
public class TVTagORM 
	extends AbstractOrmLiteClassWithAsyncSave<TVTagORM>
{
	@DatabaseField(id = true, index = true)
	private String id;
	
	@DatabaseField()
	private String displayName;


	
	public TVTagORM(TVTag tvTag)
	{
		this.id = tvTag.getId();
		this.displayName = tvTag.getDisplayName();
	}
}