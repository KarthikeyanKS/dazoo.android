
package com.mitv.models.objects.mitvapi;



import android.text.TextUtils;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.ProfileImageJSON;
import com.mitv.models.orm.UserLoginDataORM;



public class ProfileImage 
	extends ProfileImageJSON 
	implements GSONDataFieldValidation 
{
	public ProfileImage(){}
	
	
	
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
