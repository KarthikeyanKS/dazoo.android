
package com.mitv.models.objects.mitvapi;



import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVSeriesJSON;



public class TVSeries
	extends TVSeriesJSON 
	implements GSONDataFieldValidation
{
	@Override
	public boolean areDataFieldsValid()
	{
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getSeriesId()) && !TextUtils.isEmpty(getName()));		
		
		return areDataFieldsValid;
	}
}
