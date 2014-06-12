
package com.mitv.models.objects.mitvapi;



import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVSeriesSeasonJSON;



public class TVSeriesSeason
	extends TVSeriesSeasonJSON implements GSONDataFieldValidation
{
	@SuppressWarnings("unused")
	private static final String TAG = TVSeriesSeason.class.getName();
	
	
	
	public TVSeriesSeason()
	{}
	
	
	
	public void setNumber(Integer number) 
	{
		this.number = number;
	}
	
	
	
	@Override
	public boolean areDataFieldsValid()
	{
		boolean areDataFieldsValid = (getNumber() != null);
		return areDataFieldsValid;
	}
}