package com.rabbit.magazine.view;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;

public class GroupView extends RelativeLayout implements OnTouchListener, OnGestureListener {

	private static final int HEIGHT = 768;
	private Group group;
	private int partNum;
	private GestureDetector mGestureDetector;
	private int total = 1;
	private Context context;
	private PageView page;

	private static final float FLING_MIN_DISTANCE = 20;
	private static final float FLING_MIN_VELOCITY = 2;

	public GroupView(Context context, PageView page, Group group) {
		super(context);
		this.context = context;
		this.group = group;
		this.page = page;
		partNum = 0;
		String contentSize = group.getContentSize();
		if (contentSize != null) {
			Integer[] size = FrameUtil.getContentSize(contentSize);
			Integer f = size[1] / HEIGHT;
			total = (int) Math.ceil(Double.parseDouble(f.toString()));
		}
		mGestureDetector = new GestureDetector(context, this);
		setOnTouchListener(this);
		String frame = group.getFrame();
		Float[] frames = FrameUtil.getFrames(frame);
		String paged = group.getPaged();
		List<Picture> pictures = group.getPictures();
		for (Iterator iterator = pictures.iterator(); iterator.hasNext();) {
			Picture picture = (Picture) iterator.next();
			Float[] current = FrameUtil.getFrames(picture.getFrame());
			/*PictureView picV = new PictureView(context, group, new Float(0.0), picture);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
//			lp.setMargins(arg0, arg1, arg2, arg3)
			lp.addRule(RelativeLayout.ALIGN_LEFT);
			this.addView(picV, lp);*/
		}
		List<PictureSet> pictureSets = group.getPictureSets();
		for (Iterator iterator = pictureSets.iterator(); iterator.hasNext();) {
			PictureSet pictureSet = (PictureSet) iterator.next();
			PictureSetView view = new PictureSetView(context, pictureSet);
			String framestr = pictureSet.getFrame();
			Float[] frames2 = FrameUtil.getFrames(framestr);
			addView(view);
			view.setVisibility(View.VISIBLE);
		}
		List<Group> groups = group.getGroups();
		for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
			Group group2 = (Group) iterator.next();
			GroupView gv = new GroupView(context,page, group2);
			addView(gv);
		}
		List<Hot> hots = group.getHots();
		for (Iterator iterator = hots.iterator(); iterator.hasNext();) {
			Hot hot = (Hot) iterator.next();
			Float[] temp = FrameUtil.getFrames(hot.getFrame());
			/*HotView hotView = new HotView(context, hot, group);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.setMargins(50, 50, 50, 50);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			this.addView(hotView, lp);*/
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//		boolean flag = false;
//		if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//			partNum += 1;
//			flag = true;
//			// Fling left
//		} else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//			// Fling right
//			partNum -= 1;
//			flag = true;
//		}
//		if (flag) {
//			Toast.makeText(getContext(), "下半页 ", 1).show();
//			showEditPart(context, partNum);
//			getParent().requestDisallowInterceptTouchEvent(true);
//			return false;
//		}
//		getParent().requestDisallowInterceptTouchEvent(false);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//		int pagenum = page.getPagenum();
//		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
//			pagenum += 1;
//			// Fling left
//			Toast.makeText(getContext(), "后一页", 1).show();
//		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
//			// Fling right
//			Toast.makeText(getContext(), "前一页", 2).show();
//			pagenum -= 1;
//		}
//		if (pagenum >= 0 && pagenum < page.getPageTotal()) {
//			page.setPagenum(pagenum);
//			page.moveNextPage();
//			return true;
//		}
//		getParent().requestDisallowInterceptTouchEvent(false);
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	private void showEditPart(Context context, int partNum) {
		removeAllViews();
		Float partTop = (float) (partNum * HEIGHT);
		List<Picture> pictures = group.getPictures();
		for (Iterator iterator = pictures.iterator(); iterator.hasNext();) {
			Picture picture = (Picture) iterator.next();
			Float[] current = FrameUtil.getFrames(picture.getFrame());
			/*PictureView picV = new PictureView(context, group, partTop, picture);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			lp.addRule(RelativeLayout.ALIGN_LEFT);
			this.addView(picV, lp);*/
		}
		List<PictureSet> pictureSets = group.getPictureSets();
		for (Iterator iterator = pictureSets.iterator(); iterator.hasNext();) {
			PictureSet pictureSet = (PictureSet) iterator.next();
			PictureSetView view = new PictureSetView(context, pictureSet);
			String framestr = pictureSet.getFrame();
			Float[] frames2 = FrameUtil.getFrames(framestr);
			addView(view);
			view.setVisibility(View.VISIBLE);
		}
		List<Group> groups = group.getGroups();
		for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
			Group group2 = (Group) iterator.next();
			GroupView gv = new GroupView(context,page, group2);
			addView(gv);
		}
		List<Hot> hots = group.getHots();
		for (Iterator iterator = hots.iterator(); iterator.hasNext();) {
			Hot hot = (Hot) iterator.next();
			Float[] temp = FrameUtil.getFrames(hot.getFrame());
			/*HotView hotView = new HotView(context, hot, group);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lp.setMargins(50, 50, 50, 50);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			this.addView(hotView, lp);*/
		}
	}

}
