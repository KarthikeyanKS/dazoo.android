
package com.millicom.mitv.models.gson;



import java.util.List;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;



public class TVSearchResults
	implements GSONDataFieldValidation
{
	private List<TVSearchResult> results;



	public TVSearchResults()
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