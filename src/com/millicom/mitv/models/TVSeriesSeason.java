
package com.millicom.mitv.models;



import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.TVSeriesSeasonJSON;



public class TVSeriesSeason
	extends TVSeriesSeasonJSON implements GSONDataFieldValidation
{
	public void setNumber(Integer number) 
	{
		this.number = number;
	}

	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (getNumber() != null);
		return areDataFieldsValid;
	}
}