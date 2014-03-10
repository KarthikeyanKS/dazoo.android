package com.mitv.models;

import java.io.Serializable;

import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.ProfileImageJSON;

public class ProfileImage extends ProfileImageJSON implements GSONDataFieldValidation, Serializable {

	private static final long serialVersionUID = -6325419468398787236L;

	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getUrl()));
		return areDataFieldsValid;
	}
	
}
