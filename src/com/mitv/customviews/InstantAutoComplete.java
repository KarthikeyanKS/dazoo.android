package com.mitv.customviews;

import com.mitv.manager.FontManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

public class InstantAutoComplete extends AutoCompleteTextView {
	
	private Handler mHandler= new Handler();
	
	public InstantAutoComplete(Context context) {
        super(context);
        setup(context);
    }

    public InstantAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
        final Context contextFinal = context;
        addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				if(arg0.toString().length()<=0){
					setTypeface(FontManager.getFontLight(contextFinal));
	            } else {
	            	setTypeface(FontManager.getFontBold(contextFinal));
	            }
			}
		});
    }

    public InstantAutoComplete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }
    
    private void setup(Context context) {
    	setTypeface(FontManager.getFontLight(context));
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
