package com.rabbit.magazine.view;

import java.util.ArrayList;
import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.R;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.BasicView;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.kernel.Slider;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.util.AnimationUtil;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class PageView2 extends ScrollView{
	
	private Context context;
	
	private FlipperPageView2 flipperPageView;
	
	private Group firstGroup;
	
	public Group getFirstGroup() {
		return firstGroup;
	}

	public void setFirstGroup(Group firstGroup) {
		this.firstGroup = firstGroup;
	}

	private boolean paged=false;
	
	public boolean isPaged() {
		return paged;
	}

	public void setPaged(boolean paged) {
		this.paged = paged;
	}

	private ArrayList<LayerView> layerViewList=new ArrayList<LayerView>();
	
	private boolean isBottom=false;
	
	public boolean isBottom() {
		return isBottom;
	}

	public void setBottom(boolean isBottom) {
		this.isBottom = isBottom;
	}

	private int index;
	
	private int UNIT=AppConfigUtil.HEIGHT_ADJUST;
	
	public int getUNIT(){
		return UNIT;
	}
	
	public int Y=0;//Y轴位置
	
	public void setY(int y) {
		Y = y;
	}
	
	/**
	 * 整个FrameLayout
	 */
	private FrameLayout frameLayout;
	
	private View bigGroupView;
	
	public FrameLayout getFrameLayout() {
		return frameLayout;
	}

	public PageView2(Context context,Page page,int pageIndex,FlipperPageView2 flipperPageView,int index,Group group){
		super(context);
		this.context=context;
		this.flipperPageView=flipperPageView;
		this.setIndex(index);
		
		UNIT=AppConfigUtil.HEIGHTPIXELS;
		LayoutParams params=new LayoutParams(AppConfigUtil.WIDTHPIXELS, AppConfigUtil.HEIGHTPIXELS);
		setLayoutParams(params);
		setFocusableInTouchMode(true);
		setFrameLayout(new FrameLayout(context));
		params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		getFrameLayout().setLayoutParams(params);
		addView(getFrameLayout());
		firstGroup=group;
		intialFirstGroupView(firstGroup);
		buildGroupView(firstGroup);
		//上下滑动翻页
		if(paged){
			setOnTouchListener(new OnTouchListener() {
				float downYValue = 0;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					setVerticalScrollBarEnabled(true);
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_DOWN: {
						downYValue = event.getY();
						return true;
					}
					case MotionEvent.ACTION_UP: {
						int height=getFrameLayout().getHeight();
						int childCount = getFrameLayout().getChildCount();
						float currentY = event.getY();
						if ((downYValue - currentY) > 50) {// 向下滑动
							scrollDown(v,childCount,height);
						}else if ((currentY - downYValue) > 50) {// 向上滑动
							scrollUp(v,childCount);
						}else {
							PageView2.this.flipperPageView.showNavBar();
						}
						break;
					}
					}
					return false;
				}
			});
		}else{
			((MagazineActivity)context).runOnUiThread(new Runnable(){
				@Override
				public void run() {
					final GestureDetector mGesture = new GestureDetector(new OnGestureListener(){
						@Override
						public boolean onDown(MotionEvent arg0) {
							return false;
						}
						@Override
						public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY) {
							return false;
						}
						@Override
						public void onLongPress(MotionEvent e) {
						}
						@Override
						public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) {
							return false;
						}
						@Override
						public void onShowPress(MotionEvent e) {
						}
						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							PageView2.this.flipperPageView.showNavBar();
							return false;
						}
					});
					setOnTouchListener(new OnTouchListener(){
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							setVerticalScrollBarEnabled(true);
							return mGesture.onTouchEvent(event);
						}
					});
				}
			});
			
			setVerticalFadingEdgeEnabled(false);
			setHorizontalFadingEdgeEnabled(false);
		}
		LayoutInflater inflater=((Activity) context).getLayoutInflater();
		RelativeLayout loadingView=(RelativeLayout) inflater.inflate(R.layout.loading,null);
		params=new LayoutParams(AppConfigUtil.WIDTHPIXELS, AppConfigUtil.HEIGHTPIXELS);
		params.setMargins(0, 0, 0, 0);
		loadingView.setLayoutParams(params);
		frameLayout.addView(loadingView);
	}
	
	public void scrollUp(View v,int childCount){
		if(Y-UNIT>=0){
			Y=Y-UNIT;
			loadResource(Y,childCount,null);
			((PageView2)v).smoothScrollTo(0, Y);
			isBottom=false;
		}
		String offset=getFirstGroup().getContentOffset();
		if(offset!=null){
			int ofs=Integer.parseInt(offset.split(",")[1].trim());
			if(ofs>0&&ofs<UNIT){
				loadResource(0,childCount,null);
				((PageView2)v).smoothScrollTo(0, Y);
				isBottom=false;
			}
		}
	}
	
	public void scrollDown(View v,int childCount,int height){
		if(height<=UNIT){
			isBottom=true;
		}
		
		if(!isBottom){
			Y=Y+UNIT;
			loadResource(Y,childCount,null);
			((PageView2)v).smoothScrollTo(0, Y);
			if(Y>=(height-UNIT)){
				isBottom=true;
			}
		}
	}
	
	/**
	 * 上下滑动时资源的回收和加载,加载上中下3屏的图片资源，其余释放
	 * @param Y
	 * @param childCount
	 */
	protected void loadResource(int Y,int childCount,Handler handler){
		int minY=Y;
		int maxY=Y+UNIT;
		for (int i = 0; i < childCount; i++) {
			View childView=getFrameLayout().getChildAt(i);
			if(childView.getClass().equals(FirstGroupView.class)){
				FirstGroupView firstGroupView=(FirstGroupView)childView;
				firstGroupView.loadGroupViewImg(Y,null);
			}else if(childView.getClass().equals(GroupView2.class)){
				GroupView2 groupView=(GroupView2)childView;
				LayoutParams params = (LayoutParams)groupView.getLayoutParams();
				if(minY<=params.topMargin&&params.topMargin<maxY){//加载GroupView资源
					String offset=groupView.getGroup().getContentOffset();
					if(offset!=null){
						int ofs=Integer.parseInt(offset.split(",")[1].trim());
						int y=ofs/groupView.getUNIT()*groupView.getUNIT();
						groupView.setDistanceY(y);
						groupView.scrollTo(0, ofs);
					}
					groupView.loadGroupViewImg(handler);
				}else{//释放GroupView资源
					groupView.releaseGroupViewImg();
				}
			}else if(childView.getClass().equals(HorizontalGroupView.class)){
				HorizontalGroupView groupView=(HorizontalGroupView)childView;
				LayoutParams params = (LayoutParams)groupView.getLayoutParams();
				if(minY<=params.topMargin&&params.topMargin<maxY){//加载GroupView资源
					String offset=groupView.getGroup().getContentOffset();
					if(offset!=null){
						int ofs=Integer.parseInt(offset.split(",")[0].trim());
						groupView.scrollTo(ofs, 0);
					}
					groupView.loadGroupViewImg(handler);
				}else{//释放GroupView资源
					groupView.releaseGroupViewImg();
				}
			}
		}
	}
	
	/**
	 * 初始化第一个GroupView
	 * @param group
	 */
	private void intialFirstGroupView(Group group){
		List<Hot> hots=group.getHots();
		List<Picture> pictures=group.getPictures();
		List<Layer> layers=group.getLayers();
		List<Animation> animations=group.getAnimations();
		List<Video> videos=group.getVideos();
		List<PictureSet> pictureSets=group.getPictureSets();
		List<Slider> sliders=group.getSliders();
		List<Shutter> shutters=group.getShutters();
		List<Group> groups=group.getGroups();
		List<Rotater> rotater=group.getRotaters();
		int[] frames=FrameUtil.frame2int(group.getFrame());
		if(frames[2]>=AppConfigUtil.WIDTH_ADJUST&&frames[3]>=AppConfigUtil.HEIGHT_ADJUST
				&&
				(hots.size()!=0
				||layers.size()!=0
				||animations.size()!=0
				||videos.size()!=0
				||pictureSets.size()!=0
				||pictures.size()!=0
				||sliders.size()!=0
				||shutters.size()!=0
				||groups.size()>1
				||rotater.size()!=0)){
			FirstGroupView firstGroupView=new FirstGroupView(context,group,flipperPageView,this);
			getFrameLayout().addView(firstGroupView);
			if(group.getPaged()!=null&&"true".equalsIgnoreCase(group.getPaged().trim())){
				paged=true;
			}
			firstGroup=group;
		}else{
			if(group.getGroups().size()>0){
				intialFirstGroupView(group.getGroups().get(0));
			}
		}
	}
	

	/**
	 * 递归构建GroupView
	 * @param group
	 */
	private void buildGroupView(Group group){
		if(group==null){
			return;
		}
		List<Group> groups=group.getGroups();
		if(groups!=null&&groups.size()!=0){
			for(Group g:groups){
				String oritentation=g.getOrientation();
				View groupView=null;
				int childCount;
				if(oritentation.equals(BasicView.LANDSCAPE)){
					HorizontalGroupView hGroupView=new HorizontalGroupView(context,g,flipperPageView,this);
					childCount=hGroupView.getFrameLayout().getChildCount();
					groupView=hGroupView;
					
				}else{
					GroupView2 vGroupView=new GroupView2(context,g,flipperPageView,this);
					childCount=vGroupView.getFrameLayout().getChildCount();
					groupView=vGroupView;
				}
				//如果Group里只有嵌套的Group子控件则不添加到PageView里
				if(childCount!=0){
					String frame=g.getFrame();
					int[] frames=FrameUtil.frame2int(frame);
					//如果该层Group覆盖了整个屏幕，那么就将该Group下的子控件添加到该Group下
					if(getBigGroupView()!=null){
						((GroupView2)getBigGroupView()).getFrameLayout().addView(groupView);
					}else{
						getFrameLayout().addView(groupView);
					}
					if(frames[0]==0&&frames[1]==0&&frames[2]>=AppConfigUtil.WIDTH_ADJUST&&frames[3]>=AppConfigUtil.HEIGHT_ADJUST&&oritentation.equals(BasicView.PORTRAIT)){
						setBigGroupView(groupView);
					}
				}else{
					groupView=null;
				}
				List<Group> groups_=g.getGroups();
				if(groups_!=null&&groups_.size()!=0){
					for(Group g_:groups_){
						buildGroupView(g_);
					}
				}
			}
		}else{
			if(firstGroup!=group){
				List<Hot> hots=group.getHots();
				List<Picture> pictures=group.getPictures();
				List<Layer> layers=group.getLayers();
				List<Animation> animations=group.getAnimations();
				List<Video> videos=group.getVideos();
				List<PictureSet> pictureSets=group.getPictureSets();
				List<Slider> sliders=group.getSliders();
				List<Shutter> shutters=group.getShutters();
				if(hots.size()!=0||layers.size()!=0||animations.size()!=0||videos.size()!=0||pictureSets.size()!=0||pictures.size()!=0||sliders.size()!=0||shutters.size()!=0){
					View groupView=null;
					String oritentation=group.getOrientation();
					if(oritentation.equals(BasicView.LANDSCAPE)){
						groupView=new HorizontalGroupView(context, group, flipperPageView,this);
					}else{
						groupView=new GroupView2(context,group,flipperPageView,this);
					}
					if(getBigGroupView()!=null){
						View bigGroupView=getBigGroupView();
						if(bigGroupView.getClass()==HorizontalGroupView.class){
							((HorizontalGroupView)bigGroupView).getFrameLayout().addView(groupView);
						}else if(bigGroupView.getClass()==GroupView2.class){
							((GroupView2)bigGroupView).getFrameLayout().addView(groupView);
						}
					}else{
						getFrameLayout().addView(groupView);
					}
				}
			}
		}
	}
	
	public void showLayer(String name,String tag){
		for(LayerView layerView:layerViewList){
			Layer layer=layerView.getLayer();
			String effect=layer.getEffect();
			if(layer.getName().equals(name)&&layer.getTag().equals(tag)){
				if(layerView.isLoad()&&layerView.getVisibility()==View.VISIBLE){
					continue;
				}
				layerView.loadImg();
				HotView hotView=layerView.getHotView();
				if(hotView!=null){
					hotView.setVisibility(View.VISIBLE);
					hotView.bringToFront();
					layerView.setLoad(true);
				}
				if(effect!=null){
					AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
					alpha.setDuration(800);
					layerView.setAnimation(alpha);
					alpha.startNow();
				}
			}else if(layer.getName().equals(name)){
				if(effect!=null){
					int w=layerView.getLayoutParams().width;
					int h=layerView.getLayoutParams().height;
					if("FADE".equals(effect)){
						AnimationUtil.startAlphaAnimation(layerView);
					}else if("SLIDE_LEFT".equals(effect)){
						AnimationUtil.startTranslateAnimation(layerView,0, -w, 0, 0);
					}else if("SLIDE_RIGHT".equals(effect)){
						AnimationUtil.startTranslateAnimation(layerView,0, w, 0, 0);
					}else if("SLIDE_TOP".equals(effect)){
						AnimationUtil.startTranslateAnimation(layerView,0, 0, 0, -h);
					}else if("SLIDE_BOTTOM".equals(effect)){
						AnimationUtil.startTranslateAnimation(layerView,0, 0, 0, h);
					}else if("FLIP_LEFT".equals(effect)){
						AnimationUtil.start3Danimation(layerView,0,-180,"Y");
					}else if("FLIP_TOP".equals(effect)){
						AnimationUtil.start3Danimation(layerView,0,180,"X");
					}else if("FLIP_RIGHT".equals(effect)){
						AnimationUtil.start3Danimation(layerView,0,180,"Y");
					}else if("FLIP_BOTTOM".equals(effect)){
						AnimationUtil.start3Danimation(layerView,0,-180,"X");
					}
				}else{
					ImageUtil.recycle(layerView);
					HotView hotView=layerView.getHotView();
					if(hotView!=null){
						hotView.setVisibility(View.INVISIBLE);
						layerView.setLoad(false);
					}
				}
			}
				
		}
	}

	public void setFrameLayout(FrameLayout frameLayout) {
		this.frameLayout = frameLayout;
	}

	public View getBigGroupView() {
		return bigGroupView;
	}

	public void setBigGroupView(View bigGroupView) {
		this.bigGroupView = bigGroupView;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ArrayList<LayerView> getLayerViewList() {
		return layerViewList;
	}

	public void setLayerViewList(ArrayList<LayerView> layerViewList) {
		this.layerViewList = layerViewList;
	}

}