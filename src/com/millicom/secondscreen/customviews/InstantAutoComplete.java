package com.millicom.secondscreen.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

public class InstantAutoComplete extends AutoCompleteTextView {
	
	private Handler mHandler= new Handler();
	
	public InstantAutoComplete(Context context) {
        super(context);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public boolean enoughToFilter() {
    	return getText().length() > getThreshold();	
    }
    
	@Override
	protected void replaceText (CharSequence text) {
		return;
	}
	
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
//        if (focused) {
//        	performFiltering(getText(), 0);
//        }
    }
    
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
	    if(KeyEvent.KEYCODE_BACK == event.getKeyCode()) {

	    	boolean popup = isPopupShowing();
	    	InputMethodManager inputMethodManager = (InputMethodManager)  getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
		    boolean wasVisible = inputMethodManager.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
		   
			Log.i("wasVisible", Boolean.toString(wasVisible));
			Log.i("popup", Boolean.toString(popup));
			
		    if(wasVisible && popup){
		    	showDropDown();
		    	return true;
		    } else if (wasVisible) {
		    	return true;
		    }
	    }
	    return super.dispatchKeyEventPreIme(event);
	}
}
