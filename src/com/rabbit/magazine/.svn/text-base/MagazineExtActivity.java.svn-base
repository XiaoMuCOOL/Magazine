package com.rabbit.magazine;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.view.FlipperPageView;

public class MagazineExtActivity extends MagazineLoaderActivity {

	private static final int LIMIT = 3;
	private ViewFlipper viewFlipper;
	private int pos;
	/**
	 * 列表定位器
	 */
	private int offset;
	private int pageNum;

	@Override
	public void showMagazinePage() {
		Category category = getMagazine().getCategorys().get(0);
		pages = category.getPages();
		pageNum = pages.size();
		pos = 0;
		offset = 0;
		ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipper2);
		setViewFlipper(flipper);
		FlipperPageView pv = new FlipperPageView(MagazineExtActivity.this, pages, 0);
		// 虚拟节点
//		for (int i = 0; i < LIMIT; i++) {
//			flipper.addView(new TextView(getApplicationContext()), i);
//		}
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.setMargins(0, 0, width, height);
		flipper.addView(pv, 0, params);
		viewFlipper.setDisplayedChild(0);
	}

	public void showNext() {
		ViewFlipper flipper = getViewFlipper();
		View oldPage = flipper.getChildAt(offset);
		recycle(oldPage);
		pos += 1;
		offset += 1;
		// 不能超出杂志页数
		if (pos > 0 && pos < pageNum) {
			View child = flipper.getChildAt(offset);
			if (child ==null) {
//				if (child instanceof TextView) {
				FlipperPageView pv = new FlipperPageView(this, pages, pos);
				child = pv;
				if (pv != null) {
					LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					params.setMargins(0, 0, width, height);
					if (offset == LIMIT - 1) {
						flipper.removeViewAt(0);
						offset -= 1;
					}
					viewFlipper.addView(pv, offset, params);
					flipper.setInAnimation(AnimationUtils.loadAnimation(MagazineExtActivity.this, R.anim.push_left_in));
					flipper.setOutAnimation(AnimationUtils
							.loadAnimation(MagazineExtActivity.this, R.anim.push_left_out));
					flipper.showNext();
				}
			}
		}

	}

	public void showPrevious() {
		pos -= 1;
		// 不能超出杂志页数
		if (pos >=0 && pos < pageNum) {
			offset -= 1;
			View currentView = getViewFlipper().getCurrentView();
			recycle(currentView);
			getViewFlipper().setInAnimation(
					AnimationUtils.loadAnimation(MagazineExtActivity.this, R.anim.push_right_in));
			getViewFlipper().setOutAnimation(
					AnimationUtils.loadAnimation(MagazineExtActivity.this, R.anim.push_right_out));
			getViewFlipper().showPrevious();
		}
	}

	private void recycle(View currentView) {
		recycle();
		// if (currentView instanceof FlipperPageView) {
		// FlipperPageView page = (FlipperPageView) currentView;
		// Map<Integer, GroupSplitView> splitViews = page.getSplitViews();
		// Set<Entry<Integer, GroupSplitView>> entrySet2 =
		// splitViews.entrySet();
		// for (Iterator iterator2 = entrySet2.iterator(); iterator2.hasNext();)
		// {
		// Entry<Integer, GroupSplitView> entry2 = (Entry<Integer,
		// GroupSplitView>) iterator2.next();
		// GroupSplitView value = entry2.getValue();
		// for (int j = 0; j < value.getChildCount(); j++) {
		// View view1 = value.getChildAt(j);
		// if (view1 instanceof ImageView) {
		// ImageView vi = (ImageView) view1;
		// BitmapDrawable drawable = (BitmapDrawable) vi.getDrawable();
		// if (drawable != null) {
		// Bitmap bitmap = drawable.getBitmap();
		// if (bitmap != null && !bitmap.isRecycled()) {
		// bitmap.recycle();
		// bitmap = null;
		// }
		// }
		// }
		// }
		// }
		// System.gc();
		// }
	}

	public void setViewFlipper(ViewFlipper viewFlipper) {
		this.viewFlipper = viewFlipper;
	}

	public ViewFlipper getViewFlipper() {
		return viewFlipper;
	}

	public void gotoPage(int pageNumber) {
		recycle();
		viewFlipper.removeAllViews();
		offset = 0;
		// 直接跳转到目标页的情况,如果存在当前页面则显示,否则把ViewFlipper全部回收,然后再构造目标页
		FlipperPageView pv = new FlipperPageView(this, pages, pageNumber);
		if (pv != null) {
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			params.setMargins(0, 0, width, height);
			viewFlipper.addView(pv, offset, params);
		}
	}

}
