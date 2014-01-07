package com.millicom.secondscreen.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.myprofile.MyProfileActivity;
import com.millicom.secondscreen.storage.DazooStore;

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

	public static void logout() {

		application.setAccessToken(null);
		application.setUserFirstName(null);
		application.setUserLastName(null);
		application.setUserEmail(null);
		application.setUserId(null);
		application.setUserExistringFlag(false);

		DazooStore.getInstance().clearAll();
		DazooStore.getInstance().reinitializeAll();
		DazooCore.resetAll();
	}
}
