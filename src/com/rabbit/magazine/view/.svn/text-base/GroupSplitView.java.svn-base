package com.rabbit.magazine.view;

import com.rabbit.magazine.MagazineActivity;
import com.rabbit.magazine.MagazineExtActivity;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

public class GroupSplitView extends FrameLayout {

	private MagazineExtActivity activity;

	public GroupSplitView(Context context) {
		super(context);
	}

	public GroupSplitView(Activity activity) {
		super(activity);
		this.activity = (MagazineExtActivity) activity;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = getChildAt(i);
			android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			if (layoutParams != null) {
				layoutParams.height = this.activity.getHeight();
				layoutParams.width = this.activity.getWidth();
				view.setLayoutParams(layoutParams);
			} else {
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, this.activity.getWidth(), this.activity.getHeight());
				params.gravity = Gravity.CENTER;
				view.setLayoutParams(params);
			}
		}
		if (right == 0) {
			right = this.activity.getWidth();
			bottom = this.activity.getHeight();
		}
		super.onLayout(changed, left, top, this.activity.getWidth(), this.activity.getHeight());
	}

}
