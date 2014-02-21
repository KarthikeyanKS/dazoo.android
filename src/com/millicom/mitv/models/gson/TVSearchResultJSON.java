
package com.millicom.mitv.models.gson;



import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.TVSearchResultEntity;



public class TVSearchResultJSON
	implements GSONDataFieldValidation
{
	protected String displayText;
	
	protected int searchScore;
	
	protected String entityType;
	
	protected TVSearchResultEntity entity;
	
	
	
	public TVSearchResultJSON()
	{}
	

	
	public ContentTypeEnum getEntityType() 
	{
		return ContentTypeEnum.getContentTypeEnumFromStringRepresentation(entityType);
	}



	public String getDisplayText() {
		return displayText;
	}



	public int getSearchScore() {
		return searchScore;
	}



	public TVSearchResultEntity getEntity() {
		return entity;
	}



	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (displayText != null && 
								  entityType != null && entityType.length() > 0 &&
								  entity != null && entity.areDataFieldsValid());

		return areFieldsValid;
	}
}
