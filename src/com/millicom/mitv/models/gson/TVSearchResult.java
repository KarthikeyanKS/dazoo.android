
package com.millicom.mitv.models.gson;



import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.interfaces.GSONDataFieldValidation;



public class TVSearchResult
	implements GSONDataFieldValidation
{
	private String displayText;
	
	private int searchScore;
	
	private String entityType;
	
	private TVSearchResultEntity entity;
	
	
	
	public TVSearchResult()
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
								  entity.areDataFieldsValid());

		return areFieldsValid;
	}
}
