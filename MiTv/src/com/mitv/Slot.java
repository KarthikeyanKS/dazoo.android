package com.mitv;

import android.content.Context;
import android.os.Bundle;

import com.facebook.SessionLoginBehavior;
import com.facebook.SharedPreferencesTokenCachingStrategy;
import com.facebook.model.GraphUser;

public class Slot {

	private static final String CACHE_NAME_FORMAT = "TokenCache%d";
    private static final String CACHE_USER_ID_KEY = "MiTVUserId";
    private static final String CACHE_USER_NAME_KEY = "MiTVUserName";

    private String tokenCacheName;
    private String userName;
    private String userId;
    private SharedPreferencesTokenCachingStrategy tokenCache;
    private SessionLoginBehavior loginBehavior;

    public Slot(Context context, int slotNumber, SessionLoginBehavior loginBehavior) {
        this.loginBehavior = loginBehavior;
        this.tokenCacheName = String.format(CACHE_NAME_FORMAT, slotNumber);
        this.tokenCache = new SharedPreferencesTokenCachingStrategy(
                context,
                tokenCacheName);

        restore();
    }

    public String getTokenCacheName() {
        return tokenCacheName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public SessionLoginBehavior getLoginBehavior() {
        return loginBehavior;
    }

    public SharedPreferencesTokenCachingStrategy getTokenCache() {
        return tokenCache;
    }

    public void update(GraphUser user) {
        if (user == null) {
            return;
        }

        userId = user.getId();
        userName = user.getName();

        Bundle userInfo = tokenCache.load();
        userInfo.putString(CACHE_USER_ID_KEY, userId);
        userInfo.putString(CACHE_USER_NAME_KEY, userName);

        tokenCache.save(userInfo);
    }

    public void clear() {
        tokenCache.clear();
        restore();
    }

    private void restore() {
        Bundle userInfo = tokenCache.load();
        userId = userInfo.getString(CACHE_USER_ID_KEY);
        userName = userInfo.getString(CACHE_USER_NAME_KEY);
    }
	
}
