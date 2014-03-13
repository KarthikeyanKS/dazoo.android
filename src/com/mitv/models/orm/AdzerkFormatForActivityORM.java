
package com.mitv.models.orm;



import com.j256.ormlite.field.DatabaseField;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



public class AdzerkFormatForActivityORM 
	extends AbstractOrmLiteClassWithAsyncSave<AdzerkFormatForActivityORM> 
{
	@DatabaseField(generatedId=true)
	private int id;
	
	@DatabaseField(canBeNull=false)
	private Integer value;

	
	
	public AdzerkFormatForActivityORM()
	{}
	
	
	public AdzerkFormatForActivityORM(Integer value)
	{
		this.value = value;
	}
	
	
	public int getId() 
	{
		return id;
	}

	
	public Integer getValue() 
	{
		return value;
	}
}
