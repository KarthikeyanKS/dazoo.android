
package com.millicom.mitv.models;



import android.text.TextUtils;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.TVSportTypeJSON;



public class TVSportType
	extends TVSportTypeJSON implements GSONDataFieldValidation
{

	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getSportTypeId()) && !TextUtils.isEmpty(getName()));
		return areDataFieldsValid;
	}
	
}