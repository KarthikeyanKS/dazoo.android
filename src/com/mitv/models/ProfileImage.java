
package com.mitv.models;



import java.io.Serializable;

import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.ProfileImageJSON;
import com.mitv.models.orm.UserLoginDataORM;



public class ProfileImage 
	extends ProfileImageJSON 
	implements GSONDataFieldValidation, Serializable 
{
	private static final long serialVersionUID = -6325419468398787236L;

	
	
	public ProfileImage(UserLoginDataORM userLoginDataORM)
	{
		this.url = userLoginDataORM.getUrl();
		this.isDefault = userLoginDataORM.isDefault();
	}
	
	
	
	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getUrl()));
		
		return areDataFieldsValid;
	}
	
}
