package com.millicom.mitv.models;

import android.text.TextUtils;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.ImageSetOrientationJSON;

/**
 * Extra helper methods in this class
 * @author consultant_hdme
 *
 */
public class ImageSetOrientation extends ImageSetOrientationJSON implements GSONDataFieldValidation {

	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = ((getPortrait() != null) && (getLandscape() != null) &&
				!TextUtils.isEmpty(getPortrait().getSmall()) && !TextUtils.isEmpty(getPortrait().getMedium()) && 
				!TextUtils.isEmpty(getPortrait().getLarge()) && !TextUtils.isEmpty(getLandscape().getSmall()) && 
				!TextUtils.isEmpty(getLandscape().getMedium()) && !TextUtils.isEmpty(getLandscape().getLarge())
				);
		return areDataFieldsValid;
	}
}
