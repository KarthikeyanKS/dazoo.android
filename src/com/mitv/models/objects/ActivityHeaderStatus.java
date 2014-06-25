
package com.mitv.models.objects;



import com.mitv.enums.ActivityHeaderStatusEnum;
import com.mitv.models.orm.ActivityHeaderStatusORM;



public class ActivityHeaderStatus 
{	
	private ActivityHeaderStatusEnum status;
	private String dateUserLastClickedButton;
	
	
	
	public ActivityHeaderStatus() 
	{
		this.status = ActivityHeaderStatusEnum.SHOW_HEADER;
		this.dateUserLastClickedButton = null;
	}
	
	
	public ActivityHeaderStatus(ActivityHeaderStatusORM activityHeaderStatusORM) 
	{
		this.status = activityHeaderStatusORM.getActivityHeaderStatusEnum();
		
		this.dateUserLastClickedButton = activityHeaderStatusORM.getdDateUserLastClickedButton();
	}



	public ActivityHeaderStatusEnum getActivityHeaderStatus() 
	{
		return status;
	}



	public String getDateUserLastClickedButton() 
	{
		return dateUserLastClickedButton;
	}



	public void setStatus(ActivityHeaderStatusEnum status) 
	{
		this.status = status;
	}



	public void setDateUserLastClickedButton(String dateUserLastClickedButton) 
	{
		this.dateUserLastClickedButton = dateUserLastClickedButton;
	}
}
