package com.mitv.models.gson.serialization;



public class UserPasswordResetConfirmationData 
{
	/*
	 * The names of these variables should not be changed unless the backend API call parameters changes too.
	 */
	private String email;
	private String resetPasswordToken;
	private String newPassword;
	
	
	
	/*
	 * The empty constructor is needed by gson. Do not remove.
	 */
	public UserPasswordResetConfirmationData()
	{}


	
	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getResetPasswordToken() {
		return resetPasswordToken;
	}


	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}


	public String getNewPassword() {
		return newPassword;
	}


	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}	
}
