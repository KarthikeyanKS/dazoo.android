
package com.millicom.mitv.models;



import android.text.TextUtils;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.TVCreditJSON;



public class TVCredit
	extends TVCreditJSON implements GSONDataFieldValidation
{

	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getName()) && !TextUtils.isEmpty(getType()));
		return areDataFieldsValid;
	}
	
}