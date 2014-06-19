
package com.mitv.models.gson.mitvapi;



import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mitv.Constants;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.objects.mitvapi.TVSearchResult;
import com.mitv.models.objects.mitvapi.TVSearchResults;



public class TVSearchResultsJSON
	implements GSONDataFieldValidation, JsonDeserializer<TVSearchResults>
{
	private static final String TAG = TVSearchResultsJSON.class.getName();
	
	
	protected List<TVSearchResult> results;


	
	public TVSearchResultsJSON(){}

	
	
	public List<TVSearchResult> getResults() 
	{
		if(results == null)
		{
			results = Collections.emptyList();
			
			Log.w(TAG, "results is null");
		}
		
		return results;
	}

	
	
	@Override
	public TVSearchResults deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject resultsJsonObject = jsonElement.getAsJsonObject();
		JsonArray resultsJsonArray = resultsJsonObject.getAsJsonArray(Constants.JSON_KEY_SEARCH_RESULT_RESULTS);
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(TVSearchResult.class, new TVSearchResultJSON());
		Gson gson = gsonBuilder.create();
		
		List<TVSearchResult> searchResultList = new ArrayList<TVSearchResult>();
		
		for(JsonElement resultJsonElement : resultsJsonArray) 
		{
			TVSearchResult searchResult = gson.fromJson(resultJsonElement, TVSearchResult.class);
			
			searchResultList.add(searchResult);
		}
		
		TVSearchResults tvSearchResults = new TVSearchResults(searchResultList);
		
		return tvSearchResults;
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
				
				if(!isElementDataValid)
				{
					areFieldsValid = false;
					break;
				}
			}
		}
		
		return areFieldsValid;
	}
}