
package com.mitv.ui.elements;



import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import com.mitv.FontManager;
import com.mitv.utilities.GenericUtils;



public class InstantAutoCompleteView 
	extends AutoCompleteTextView 
	implements OnTouchListener 
{
	
	@SuppressWarnings("unused")
	private static final String TAG = InstantAutoCompleteView.class.getName();
	
	
	private Activity activity;
	private boolean searchComplete = false;
	
	
	
	public InstantAutoCompleteView(Context context) 
	{
        super(context);
       
        setup(context);
    }

	
    public InstantAutoCompleteView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        
        setup(context);
    }
    
    
    public void setSearchComplete(boolean searchComplete) 
    {
		this.searchComplete = searchComplete;
	}

    
	@Override
    public void onFilterComplete(int count) 
	{
    	showDropDown();
    }
    
	
    public void setActivity(Activity activity)
    {
    	this.activity = activity;
    }

    
    public InstantAutoCompleteView(Context context, AttributeSet attrs, int defStyle) 
    {
        super(context, attrs, defStyle);
        setup(context);
    }
    
    
    private void setup(Context context) 
    {
    	setTypeface(FontManager.getFontLight(context));
    }

    
    @Override
    public boolean enoughToFilter() 
    {
    	return getText().length() > getThreshold();	
    }
	
    
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
	{
		if (focused) 
		{
			showDropDown();
		} 
		else 
		{
			dismissDropDown();
		}
	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		showDropDown();
		
		return false;
	}
    
	
	@Override
	public void showDropDown() 
	{
		if(GenericUtils.isActivityNotNullAndNotFinishing(activity) && enoughToFilter() && searchComplete) 
		{
			super.showDropDown();
		}
	}

	
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) 
	{
	    if(KeyEvent.KEYCODE_BACK == event.getKeyCode()) 
	    {
	    	boolean popup = isPopupShowing();
	    	
	    	boolean wasVisible = GenericUtils.hideKeyboard(activity);
	    	
			Log.i("wasVisible", Boolean.toString(wasVisible));
			
			Log.i("popup", Boolean.toString(popup));
			
		    if(wasVisible && popup)
		    {
		    	showDropDown();
		    	
		    	return true;
		    } 
		    else if (wasVisible) 
		    {
		    	return true;
		    }
	    }
	    
	    return super.dispatchKeyEventPreIme(event);
	}
}
