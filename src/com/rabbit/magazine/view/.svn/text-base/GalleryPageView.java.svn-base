package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Gallery;
import android.widget.Toast;

import com.rabbit.magazine.MagazinePageAdapter;

public class GalleryPageView extends Gallery implements OnFocusChangeListener {

	boolean is_first = false;
	boolean is_last = false;

	List<Bitmap> cache = new ArrayList<Bitmap>();
	private MagazinePageAdapter adapter;
	private int page = 0;

	public GalleryPageView(Context context) {
		super(context);
//		setBackgroundColor(Color.RED);
		setFocusableInTouchMode(true);
		adapter = new MagazinePageAdapter(context, this, page);
		setAdapter(adapter);
		setOnFocusChangeListener(this);
	}

	public void addCache(Bitmap bitmap) {
		cache.add(bitmap);
	}

	public void recycle() {
		for (Iterator iterator = cache.iterator(); iterator.hasNext();) {
			Bitmap bitmap = (Bitmap) iterator.next();
			bitmap.recycle();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		adapter.notifyDataSetChanged();
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		MagazinePageAdapter ia = (MagazinePageAdapter) this.getAdapter();
		int p = ia.getPage();
		int count = ia.getCount();
		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			if (p == 0 && is_first) {
				Toast.makeText(this.getContext(), "left", Toast.LENGTH_SHORT).show();
			} else if (p == 0) {
				is_first = true;
			} else {
				is_last = false;
			}

			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			// Otherwise scrolling right
			if (p == count - 1 && is_last) {
				Toast.makeText(this.getContext(), "right", Toast.LENGTH_SHORT).show();
			} else if (p == count - 1) {
				is_last = true;
			} else {
				is_first = false;
			}

			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;
	}

}
