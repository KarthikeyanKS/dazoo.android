
package com.millicom.mitv.models;



import android.text.TextUtils;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.TVSeriesJSON;


public class TVSeries
	extends TVSeriesJSON implements GSONDataFieldValidation
{

	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getSeriesId()) && !TextUtils.isEmpty(getName()));		
		return areDataFieldsValid;
	}
	
}
