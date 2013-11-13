package com.millicom.secondscreen.content.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class FeedScrollView extends ScrollView{
	private FeedScrollViewListener scrollViewListener = null;
    public FeedScrollView(Context context) {
        super(context);
    }

    public FeedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FeedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(FeedScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}
