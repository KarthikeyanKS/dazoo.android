package com.millicom.secondscreen.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBarBroadThumb extends SeekBar {

	private static final int textMargin = 6;
	private static final int leftPlusRightTextMargins = textMargin + textMargin;
	private static final int maxFontSize = 18;
	private static final int minFontSize = 10;

	protected String overlayText;
	protected Paint textPaint;

	public VerticalSeekBarBroadThumb(Context context) {
		super(context);
		setup();
	}

	public VerticalSeekBarBroadThumb(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
	}

	public VerticalSeekBarBroadThumb(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}
	
	private void setup() {
		// Set up drawn text attributes here
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		textPaint.setTextAlign(Align.LEFT);
		textPaint.setColor(Color.WHITE);
		overlayText = "12:34 AM";
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
		int newFontSize = w - leftPlusRightTextMargins;
		setFontSmallEnoughToFit(newFontSize);
		String msg = String.format("Size changed: w: %d (old: %d), h: %d (old: %d)", w, oldw, h, oldh);
//		Log.e("onSizeChange", msg);
	}

	// Clients use this to change the displayed text
	public void setOverlayText(String text) {
		this.overlayText = text;
		invalidate();
	}

	// Finds the largest text size that will fit
	protected void setFontSmallEnoughToFit(int width) {
		int textSize = maxFontSize;
		textPaint.setTextSize(textSize);
		while ((textPaint.measureText(overlayText) > width) && (textSize > minFontSize)) {
			textSize--;
			textPaint.setTextSize(textSize);
		}
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	protected void onDraw(Canvas c) {

		c.rotate(90);
		c.translate(0, -(int)(1.5f*(float)getWidth()));
		
		super.onDraw(c);
		
		/* Drawing text on the thumb image */
		
		/* WAY 1 */
		
//		BitmapDrawable thumbWithText = writeOnDrawable(R.drawable.time_bar_container, overlayText);
//		setThumb(thumbWithText);
		
		/* WAY 2 */
		
//		c.save();
//        c.rotate(-90);
//        c.translate(-getHeight(),0);
//
//	    //Here are a few parameters that could be useful in calculating where to put the text
//	    int width = this.getWidth() - leftPlusRightTextMargins;
//	    int height = this.getHeight();
//
//	    //A somewhat fat finger takes up about seven digits of space 
//	    // on each side of the thumb; YFMV
//	    int fatFingerThumbHangover = (int) textPaint.measureText("XX:YY AM");
//
//	    float textWidth = textPaint.measureText(overlayText);
//
//	    int progress = this.getProgress();
//	    int maxProgress = this.getMax();
//	    double percentProgress = (double) progress / (double) maxProgress;
//	    int textHeight = (int) (Math.abs(textPaint.ascent()) + textPaint.descent() + 1);
//
//	    int thumbOffset = this.getThumbOffset();
//
//	    //These are measured from the point textMargin in from the left of the SeekBarWithText view.
//	    int middleOfThumbControl = (int) ((double) height * percentProgress); 
//	    int spaceToLeftOfFatFinger = middleOfThumbControl - fatFingerThumbHangover;
//	    int spaceToRightOfFatFinger = (width - middleOfThumbControl) - fatFingerThumbHangover; 
//
//	    int spaceToLeftOfThumbControl = middleOfThumbControl - thumbOffset;
//	    int spaceToRightOfThumbControl = (width - middleOfThumbControl) - thumbOffset; 
//
//	    int bottomPadding = this.getPaddingBottom();
//	    int topPadding = this.getPaddingTop();
//
//	    //Here you will use the above and possibly other information to decide where you would 
//	    // like to draw the text.  One policy might be to draw it on the extreme right when the thumb
//	    // is left of center, and on the extreme left when the thumb is right of center.  These
//	    // methods will receive any parameters from the above calculations that you need to
//	    // implement your own policy.
//	    int x = 0;
//	    int y = middleOfThumbControl;
//
//	    Log.e("VerticalSeekBar", "x: " + x);
//	    Log.e("VerticalSeekBar", "y: " + y);
//
//	    //Finally, just draw the text on top of the SeekBar
//	    c.drawText(overlayText, x, y, textPaint);
//	    c.restore();
		
	}
	
	public BitmapDrawable writeOnDrawable(int drawableId, String text){
	    Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

	    Paint paint = new Paint(); 
	    paint.setStyle(Style.FILL);  
	    paint.setColor(Color.BLACK); 
	    paint.setTextSize(20); 

	    Canvas canvas = new Canvas(bm);
	    canvas.drawText(text, 0, bm.getHeight()/2, paint);

	    return new BitmapDrawable(bm);
	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			int i = 0;
			i = getMax() - (int) (getMax() * event.getY() / getHeight());
			setProgress(getMax() - i);
			Log.i("Progress", getProgress() + "");
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			String textToDisplay = getProgress()+""; 
			Log.e("onTouchEvent", "textToDisplay" + textToDisplay);
			setOverlayText(textToDisplay);
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}

}