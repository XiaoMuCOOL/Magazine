package com.rabbit.magazine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollImageView extends ScrollView {

	GestureDetector gestureDetector;

	public ScrollImageView(Context context) {
		super(context);
	}

	public ScrollImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		return gestureDetector.onTouchEvent(ev);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return true;
		// if (super.dispatchTouchEvent(ev)) {
		// return true;
		// } else {
		// gestureDetector.onTouchEvent(ev);
		// }
		// return true;
	}

}
