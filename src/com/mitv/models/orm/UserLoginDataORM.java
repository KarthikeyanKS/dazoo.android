package com.mitv.models.orm;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.mitv.models.ProfileImage;
import com.mitv.models.UserFieldsData;
import com.mitv.models.UserLoginData;

public class UserLoginDataORM extends AbstractOrmLiteClass<UserLoginDataORM> {
	
	@DatabaseField()
	protected String token;
	
	/* UserFieldsData */
	@DatabaseField()
	private String userId;
	
	@DatabaseField()
	private String email;
	
	@DatabaseField()
	private String firstName;
	
	@DatabaseField()
	private String lastName;
	
	@DatabaseField()
	private boolean created;
	
	/* ProfileImage */
	@DatabaseField()
	protected String url;
	
	@DatabaseField()
	protected boolean isDefault;
	
	@DatabaseField(columnName = "modifydate")
	public Date modifydate;
	
	
	
	public UserLoginDataORM(UserLoginData userLoginData) {
		UserFieldsData user = userLoginData.getUser();
		
		this.token = userLoginData.getToken();
		this.userId = user.getUserId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.created = user.isCreated();

		ProfileImage profileImage = userLoginData.getProfileImage();
		this.url = profileImage.getUrl();
		this.isDefault = profileImage.isDefault();
	}

	@Override
	protected void onBeforeSave() {
		this.modifydate = new Date();
	}

	@Override
	protected void onAfterSave() {}

}
