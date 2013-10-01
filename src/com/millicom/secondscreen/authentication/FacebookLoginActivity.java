package com.millicom.secondscreen.authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.facebook.*;
import com.facebook.model.*;
import com.millicom.secondscreen.R;

import android.widget.TextView;
import android.content.Intent;

public class FacebookLoginActivity extends FragmentActivity{

	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int FRAGMENT_COUNT = SELECTION +1;
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	// flag to indicate a visible activity. It is used to enable session state change checks
	private boolean isResumed = false;

	// use the UiLifecycleHelper to track the session and trigger a session state change listener
	private UiLifecycleHelper uiHelper;
	
	private Session.StatusCallback callback = 
		    new Session.StatusCallback() {
		    @Override
		    public void call(Session session, 
		            SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.layout_facebook_login);
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);

	    FragmentManager fm = getSupportFragmentManager();
	    fragments[SPLASH] = fm.findFragmentById(R.id.layout_facebook_login_splash_fragment);
	    fragments[SELECTION] = fm.findFragmentById(R.id.layout_facebook_login_selection_fragment);

	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = 0; i < fragments.length; i++) {
	        transaction.hide(fragments[i]);
	    }
	    transaction.commit();
	}
	
	// method to show the relevant fragment based on the person's authenticated state. 
	// Only handle UI changes when activity is visible by making use of the isResumed flag.
	// The fragment back stack is first cleared before the showFragment() method is called with the appropriate fragment info
 	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	            showFragment(SELECTION, false);
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(SPLASH, false);
	        }
	    }
	}
 	
 	// handle the case where fragments are newly instantiated and the authenticated versus nonauthenticated UI needs to be properly set.
 	// onResumeFragments() method to handle the session changes
 	@Override
 	protected void onResumeFragments() {
 	    super.onResumeFragments();
 	    Session session = Session.getActiveSession();

 	    if (session != null && session.isOpened()) {
 	        // if the session is already open,
 	        // try to show the selection fragment
 	        showFragment(SELECTION, false);
 	    } else {
 	        // otherwise present the splash screen
 	        // and ask the person to login.
 	        showFragment(SPLASH, false);
 	    }
 	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  uiHelper.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
