package com.mitv.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.mitv.SecondScreenApplication;
import com.mitv.myprofile.MyProfileActivity;
import com.mitv.storage.MiTVStore;

public class LoginManager {
	private static LoginManager selfInstance;
	private static SecondScreenApplication application = SecondScreenApplication.getInstance();
	
	public static LoginManager getInstance() {
		if (selfInstance == null) {
			selfInstance = new LoginManager();
		}
		return selfInstance;
	}

	public static boolean isLoggedIn() {
		boolean isLoggedIn = false;

		String loginToken = application.getAccessToken();

		if (loginToken != null && TextUtils.isEmpty(loginToken) != true) {
			isLoggedIn = true;
		}
		return isLoggedIn;
	}

	public static void forceLogin() {
		if(isLoggedIn()) {
			logout();
		}
		
		login();
	}

	public static void login() {
		if (!isLoggedIn()) {
			Context applicationContext = application.getApplicationContext();
			Intent intent = new Intent(applicationContext, MyProfileActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			applicationContext.startActivity(intent);
		}
	}

	public static void logout() 
	{
		application.setAccessToken(null);
		application.setUserFirstName(null);
		application.setUserLastName(null);
		application.setUserEmail(null);
		application.setUserId(null);
		application.setUserExistringFlag(false);
		
		//TODO do something more effective here??
		MiTVStore.getInstance().clearChannelsAndIdsAndGuide();
		
		ContentManager.updateContent();

//		MiTVStore.getInstance().clearAll();
//		MiTVStore.getInstance().reinitializeAll();
	}
}
