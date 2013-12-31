package com.rabbit.magazine.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.rabbit.magazine.MagazineActivity;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.util.StringUtil;

/**
 * 页视图
 * 
 * @author litingwen
 * 
 */
public class PageView extends RelativeLayout implements OnTouchListener, OnGestureListener {

	private MagazineActivity activity;

	private List<Page> pages;

	private Page currentPage;

	private GestureDetector mGestureDetector;

	private int pagenum;

	private int pageTotal;

	/**
	 * 分屏列表
	 */
	private Map<Integer, GroupSplitView> splitViews = new HashMap<Integer, GroupSplitView>();

	/**
	 * 分屏数
	 */
	private int splitNum;
	/**
	 * 分屏数
	 */
	private int currentSplit;

	private boolean pageSplit = false;

	private static final float FLING_MIN_DISTANCE = 200;
	private static final float FLING_MIN_VELOCITY = 2;

	public PageView(Activity context) {
		super(context);
	}

	public PageView(Activity context, List<Page> pages, int begin) {
		super(context);
		this.activity = (MagazineActivity) context;
		this.pages = pages;
		this.pagenum = begin;
		this.currentSplit = 0;
		this.pageTotal = pages.size();
		this.currentPage = this.pages.get(pagenum);
		this.mGestureDetector = new GestureDetector(context, this);
		setOnTouchListener(this);
		this.splitNum = 5;
		String bgColor = this.currentPage.getBgColor();
		if (bgColor != null) {
			setBackgroundColor(Color.parseColor(bgColor));
		}
		List<Group> groups = this.currentPage.getGroups();
		if (groups != null && groups.size() > 0) {
			Group group = groups.get(0);
			visitGroup(group);
		}
		showEditPart(context, 0);
	}

	private void visitGroup(Group group) {
		String groupframe = group.getFrame();
		Float[] pos = FrameUtil.getFrames(groupframe);
		String contentSize = group.getContentSize();
		// 单屏页面
		if (StringUtil.isNull(contentSize)) {
			List<Picture> pictures = group.getPictures();
			for (Iterator iterator = pictures.iterator(); iterator.hasNext();) {
				Picture picture = (Picture) iterator.next();
				/*PictureView pictureView = new PictureView(activity, group, 0, picture);
				GroupSplitView splitView = getSplitViews().get(0);
				if (splitView != null) {
					splitView.addView(pictureView);
				} else {
					splitView = new GroupSplitView(activity);
					splitView.addView(pictureView);
					getSplitViews().put(0, splitView);
				}*/
			}
		} else if (StringUtil.isNotNull(contentSize)) {
			Integer[] groupSize = FrameUtil.getContentSize(contentSize);
			int split = groupSize[1] / FrameUtil.UNIT;
			String paged = group.getPaged();
			String style = group.getStyle();
			paged = paged.toLowerCase();
			boolean pageFlag = Boolean.parseBoolean(paged);
			// 需要单页滚动但是不分屏的实现
			if (style.equals("scroll") && (!pageFlag)) {
				ScrollImageView scroll = new ScrollImageView(getContext());
				scroll.setGestureDetector(this.mGestureDetector);
				List<Picture> pictures = group.getPictures();
				for (Iterator iterator = pictures.iterator(); iterator.hasNext();) {
					Picture picture = (Picture) iterator.next();
					String size = picture.getSize();
					if (size != null) {
						Integer[] size2 = FrameUtil.getContentSize(size);
					}
					/*PictureView pictureView = new PictureView(activity, group, 0, picture);
					scroll.addView(pictureView);*/
				}
				GroupSplitView splitView = getSplitViews().get(0);
				if (splitView != null) {
					splitView.addView(scroll);
				} else {
					splitView = new GroupSplitView(activity);
					splitView.addView(scroll);
					getSplitViews().put(0, splitView);
				}
			}
			// 需要单页分屏处理
			else if (split > 1 && pageFlag) {
				pageSplit = true;
				List<Picture> pictures = group.getPictures();
				// 计算图片分布在不同的屏里面
				for (Iterator iterator = pictures.iterator(); iterator.hasNext();) {
					Picture picture = (Picture) iterator.next();
					String frame = picture.getFrame();
					// 坐标计算
					if (frame != null) {
						Integer[] frames = FrameUtil.getIntFrames(frame);
						Integer coordinateY = frames[1];
						Integer index = coordinateY / FrameUtil.UNIT;
						GroupSplitView splitView = getSplitViews().get(index);
						/*PictureView pictureView = new PictureView(activity, group, 0, picture);
						if (splitView != null) {
							splitView.addView(pictureView);
						} else {
							splitView = new GroupSplitView(activity);
							splitView.addView(pictureView);
							getSplitViews().put(index, splitView);
						}*/
					}
				}
			} else {
				// String top = String.valueOf(Math.floor(pos[2] / 768));
				String top = "0";
				List<Picture> pictures = group.getPictures();
				for (Iterator iterator = pictures.iterator(); iterator.hasNext();) {
					Picture picture = (Picture) iterator.next();
					String size = picture.getSize();
					String frame = picture.getFrame();
					Float[] frames = FrameUtil.getFrames(frame);
					Integer topIndex = Integer.valueOf(top);
					/*PictureView pictureView = new PictureView(activity, group, topIndex, picture);
					GroupSplitView splitView = getSplitViews().get(topIndex);
					if (splitView != null) {
						splitView.addView(pictureView);
					} else {
						splitView = new GroupSplitView(activity);
						splitView.addView(pictureView);
						getSplitViews().put(topIndex, splitView);
					}*/
				}
			}
		}
		// 容器类Group
		List<Group> groups = group.getGroups();
		for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
			Group group2 = (Group) iterator.next();
			visitGroup(group2);
		}
	}

	public void showPageContent() {
		GroupSplitView splitView = getSplitViews().get(currentSplit);
		if (splitView != null) {
			this.addView(splitView);
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// 不分屏的情况
		if (!pageSplit) {
			return false;
		}
		boolean flag = false;
		if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
			this.currentSplit += 1;
			flag = true;
			showEditPart(getContext(), this.currentSplit);
			return true;
		} else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
			this.currentSplit -= 1;
			flag = true;
			showEditPart(getContext(), this.currentSplit);
			return true;
		}
		return flag;
	}

	private void showEditPart(Context context, int partNum) {
		GroupSplitView groupSplitView = getSplitViews().get(partNum);
		if (groupSplitView != null) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
			// params.setMargins(0, 0, this.activity.getWidth(),
			// this.activity.getHeight());
			params.leftMargin = 0;
			params.topMargin = 0;
			params.width = this.activity.getWidth();
			params.height = this.activity.getHeight();
			// groupSplitView.setLayoutParams(params);
			this.addView(groupSplitView, params);
		} 
//		else {
//			TextView view = new TextView(this.activity);
//			view.setText("当前位置>分屏" + this.currentSplit);
//			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//					LayoutParams.WRAP_CONTENT);
//			params.setMargins(200, 200, 400, 200);
//			this.addView(view, params);
//		}
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			this.pagenum += 1;
			// Fling left
//			 setAnimation(AnimationUtils.loadAnimation(activity,com.rabbit.magazine.R.anim.push_left_in));
//			 setAnimation(AnimationUtils.loadAnimation(activity,com.rabbit.magazine.R.anim.push_left_out));
			 moveNextPage();
			Toast.makeText(getContext(), "后一页", 1).show();
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// Fling right
			Toast.makeText(getContext(), "前一页", 2).show();
			pagenum -= 1;
			moveNextPage();
		}
//		if (this.pagenum >= 0 && this.pagenum < pages.size()) {
//			
//			return true;
//		}
		return false;
	}

	public void moveNextPage() {
		// 设置杂志翻页界限
		if (this.pagenum >= 0 && this.pagenum < pages.size()) {
			destroyDrawingCache();
			PageView view=null;
			view = new PageView(this.activity, this.pages, this.pagenum);
//			view = PageViewContainer.getPage(String.valueOf(this.pagenum));
//			if (view != null) {
//			} else {
//				view = new PageView(this.activity, this.pages, this.pagenum);
//				PageViewContainer.addPage(String.valueOf(this.pagenum), view);
//			}
			this.setVisibility(View.GONE);
			android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			ViewGroup parent2 = (ViewGroup) view.getParent();
			if (layoutParams != null) {
				layoutParams.width = this.activity.getWidth();
				layoutParams.height = this.activity.getHeight();
				// ViewParent parent2 = view.getParent();
				// if(parent2!=null){
				// view.setVisibility(View.VISIBLE);
				// parent2.bringChildToFront(view);
				// }else {
//				if(parent2!=null){
//					parent2.bringChildToFront(view);
//					parent2.postInvalidate();
//				}
//				else {
				this.activity.getWindow().addContentView(view, layoutParams);
//				}
				// }
			} else {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT);
				params.setMargins(0, 0, this.activity.getWidth(), this.activity.getHeight());
				// if(parent2!=null){
				// view.setVisibility(View.VISIBLE);
				// parent2.bringChildToFront(view);
				// }else {
				this.activity.getWindow().addContentView(view, params);
				// }
			}
		}
		
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.mGestureDetector.onTouchEvent(event);
		return true;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public static void main(String[] args) {
		String top = String.valueOf(Math.floor(800 / 768));
		System.out.println(top);
	}

	public void setSplitViews(Map<Integer, GroupSplitView> splitViews) {
		this.splitViews = splitViews;
	}

	public Map<Integer, GroupSplitView> getSplitViews() {
		return splitViews;
	}

}
