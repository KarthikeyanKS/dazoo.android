
package com.millicom.mitv.models.gson;



import java.util.List;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.TVSearchResult;



public class TVSearchResultsJSON
	implements GSONDataFieldValidation
{
	private List<TVSearchResult> results;



	public TVSearchResultsJSON()
	{}

	
	
	public List<TVSearchResult> getResults() 
	{
		return results;
	}



	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (results != null && results.isEmpty() == false);
		
		if(areFieldsValid)
		{
			for(TVSearchResult result : results)
			{
				boolean isElementDataValid = result.areDataFieldsValid();
				
				if(isElementDataValid == false)
				{
					areFieldsValid = false;
					break;
				}
			}
		}
		
		return areFieldsValid;
	}
}