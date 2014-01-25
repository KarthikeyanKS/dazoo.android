package com.millicom.secondscreen.content.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;

public class SearchResult {
	private String suggestion;
	private int numberOfResults;
	private ArrayList<SearchResultItem> items;

	public SearchResult(JSONObject jsonObject) {
		String suggestion = jsonObject.optString(Consts.JSON_KEY_SEARCH_RESULT_SUGGESTION);

		ArrayList<SearchResultItem> items = new ArrayList<SearchResultItem>();
		try {
			JSONArray itemsJson = jsonObject.getJSONArray(Consts.JSON_KEY_SEARCH_RESULT_RESULTS);
			if (itemsJson != null) {
				if (itemsJson.length() > 0) {
					JSONObject contentsObject;
					for (int i = 0; i < itemsJson.length(); ++i) {
						contentsObject = (JSONObject) itemsJson.get(i);
						SearchResultItem item = new SearchResultItem(contentsObject);
						items.add(item);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		String numberOfResultsString = jsonObject.optString(Consts.JSON_KEY_SEARCH_RESULT_NUMBER_OF_RESULTS);
		int numberOfResults = items.size();
		if(numberOfResultsString != null && numberOfResultsString.length() > 0) {
			numberOfResults = Integer.valueOf(numberOfResultsString);
		}
		
		this.setSuggestion(suggestion);
		this.setNumberOfResults(numberOfResults);
		this.setItems(items);
	
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public int getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(int numberOfResults) {
		this.numberOfResults = numberOfResults;
	}

	public ArrayList<SearchResultItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<SearchResultItem> items) {
		this.items = items;
	}

}
