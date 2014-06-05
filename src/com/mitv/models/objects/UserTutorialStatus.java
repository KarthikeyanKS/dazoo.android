package com.mitv.models.objects;

import com.mitv.enums.UserTutorialStatusEnum;
import com.mitv.models.orm.UserTutorialStatusORM;

public class UserTutorialStatus {
	
	private UserTutorialStatusEnum status;
	private String dateUserLastOpendApp;
	
	
	
	public UserTutorialStatus() 
	{
		this.status = UserTutorialStatusEnum.NEVER_SEEN_TUTORIAL;
		this.dateUserLastOpendApp = null;
	}
	
	
	public UserTutorialStatus(UserTutorialStatusORM userTutorialORM) {
		this.status = userTutorialORM.getUserTutorialStatus();
		
		this.dateUserLastOpendApp = userTutorialORM.getdDateUserLastOpendApp();
	}



	public UserTutorialStatusEnum getUserTutorialStatus() {
		return status;
	}



	public String getDateUserLastOpendApp() {
		return dateUserLastOpendApp;
	}



	public void setStatus(UserTutorialStatusEnum status) {
		this.status = status;
	}



	public void setDateUserLastOpendApp(String dateUserLastOpendApp) {
		this.dateUserLastOpendApp = dateUserLastOpendApp;
	}
	
	
}
