package com.rabbit.magazine.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.R;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.BasicView;
import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.kernel.Slider;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.parser.MagazineReader;
import com.rabbit.magazine.view.FirstGroupView;
import com.rabbit.magazine.view.GroupView2;
import com.rabbit.magazine.view.HorizontalGroupView;
import com.rabbit.magazine.view.HotView;
import com.rabbit.magazine.view.LayerView;
import com.rabbit.magazine.view.PageView2;
import com.rabbit.magazine.view.PictureView;
import com.rabbit.magazine.view.PopupWindowPictureView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ImageUtil {

	/**
	 * 回收资源
	 * 
	 * @param img
	 */
	public static void recycle(ImageView img) {
		BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
		img.setImageBitmap(null);
		if (img instanceof PictureView) {
			((PictureView) img).setLoad(false);
		} else if (img instanceof LayerView) {
			((LayerView) img).setLoad(false);
		} else if (img instanceof PopupWindowPictureView) {
			((PopupWindowPictureView) img).setLoad(false);
		}else if(img instanceof HotView){
			((HotView)img).setLoad(false);
		}
		if (drawable != null) {
			Bitmap bmp = drawable.getBitmap();
			drawable = null;
			if (bmp != null && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
				System.gc();
			}
		}
	}

	/**
	 * 加载图片资源
	 * 
	 * @param imgPath
	 * @return
	 */
	public static Bitmap loadImage(String imgPath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;//图形的参数应该由两个字节来表示，应该是一种16位的位图
		opt.inPurgeable = true;//设为True的话表示使用BitmapFactory创建的Bitmap,inPurgeable为false时表示创建的Bitmap的Pixel内存空间不能被回收
		opt.inInputShareable = true;//是否深拷贝
		// 获取资源图片
		imgPath=imgPath.replace("\n", "");
		imgPath=imgPath.trim();
		InputStream is=null;
		Bitmap bitmap=null;
		try {
			is = new FileInputStream(imgPath);
			bitmap=BitmapFactory.decodeStream(is, null, opt);//直接调用 JNI >> nativeDecodeAsset（） 来完成decode，无需再使用java层的createBitmap，从而节省了java层的空间.
			if(is!=null){
				is.close();
			}
		} catch (FileNotFoundException e) {
			Log.e("ImageUtil", "文件："+imgPath+"  找不到");
		} catch (IOException e) {
			Log.e("ImageUtil", e.getMessage());
		}
		return bitmap;
	}

	/**
	 * 释放该页的图片资源
	 * 
	 * @param pageView
	 */
	public static void releasePageViewImg(PageView2 pageView) {
		pageView.setVerticalFadingEdgeEnabled(false);
		FrameLayout frameLayout = pageView.getFrameLayout();
		int childCount = frameLayout.getChildCount();
		List<View> views=new ArrayList<View>();
		for (int i = 0; i < childCount; i++) {
			View childView = frameLayout.getChildAt(i);
			if (childView.getClass().equals(FirstGroupView.class)) {
				((FirstGroupView) childView).releaseGroupViewImg();
			} else if (childView.getClass().equals(GroupView2.class)) {
				GroupView2 groupView=(GroupView2) childView;
				groupView.setVerticalFadingEdgeEnabled(false);
				groupView.releaseGroupViewImg();
				groupView.setVerticalScrollBarEnabled(false);
			}else if(childView.getClass().equals(HorizontalGroupView.class)){
				((HorizontalGroupView) childView).releaseGroupViewImg();
				((HorizontalGroupView) childView).setHorizontalScrollBarEnabled(false);
			}else if(childView.getClass().equals(RelativeLayout.class)){
				
			}else{
				views.add(childView);
			}
		}
		for(View v:views){
			frameLayout.removeView(v);
		}
	}

	/**
	 * 加载该页的图片资源
	 * 
	 * @param sv
	 */
	public static void loadPageViewImg(PageView2 pageView,Handler handler) {
		FrameLayout frameLayout = pageView.getFrameLayout();
		int childCount = frameLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = frameLayout.getChildAt(i);
			if (childView.getClass().equals(FirstGroupView.class)) {
				FirstGroupView firstGroupView = (FirstGroupView) childView;
				String offset=pageView.getFirstGroup().getContentOffset();
				if(offset!=null){
					int ofs=Integer.parseInt(offset.split(",")[1].trim());
					firstGroupView.loadGroupViewImg(ofs,handler);
					Message msg=new Message();
					msg.what=9;
					msg.obj=pageView;
					msg.arg1=ofs;
					handler.sendMessage(msg);
					//pageView.scrollTo(0, ofs);
					int y=ofs/pageView.getUNIT()*pageView.getUNIT();
					pageView.setY(y);
				}else{
					firstGroupView.loadGroupViewImg(0,handler);
				}
			} else if (childView.getClass().equals(GroupView2.class)) {
				GroupView2 groupView = (GroupView2) childView;
				Group group = groupView.getGroup();
				int[] frames = FrameUtil.frame2int(group.getFrame());
				if(frames[1]<AppConfigUtil.HEIGHT_ADJUST){
					String offset=group.getContentOffset();
					if(offset!=null){
						int ofs=Integer.parseInt(offset.split(",")[1].trim());
						int y=ofs/groupView.getUNIT()*groupView.getUNIT();
						groupView.setDistanceY(y);
						Message msg=new Message();
						msg.what=10;
						msg.obj=groupView;
						msg.arg1=ofs;
						handler.sendMessage(msg);
						//groupView.scrollTo(0, ofs);
					}
					groupView.loadGroupViewImg(handler);
				}
				groupView.setVerticalScrollBarEnabled(false);
			}else if (childView.getClass().equals(HorizontalGroupView.class)) {
				HorizontalGroupView groupView = (HorizontalGroupView) childView;
				Group group = groupView.getGroup();
				int[] frames = FrameUtil.frame2int(group.getFrame());
				if (frames[1] < AppConfigUtil.HEIGHT_ADJUST) {
					String offset=group.getContentOffset();
					if(offset!=null){
						int ofs=Integer.parseInt(offset.split(",")[0].trim());
						Message msg=new Message();
						msg.what=11;
						msg.obj=groupView;
						msg.arg1=ofs;
						handler.sendMessage(msg);
						//groupView.scrollTo(ofs, 0);
					}
					groupView.loadGroupViewImg(handler);
				}
				groupView.setVerticalScrollBarEnabled(false);
			}
		}
	}
	
	/**
	 * 生成缩略图
	 * @param imgPath
	 * @param height
	 * @return
	 */
	public static Bitmap createThumbnail(String imgPath,int height){
		InputStream is=null;
		Bitmap bitmap=null;
		try {
			is = new FileInputStream(imgPath);
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inJustDecodeBounds=true;
			BitmapFactory.decodeStream(is, null, options);
			options.inJustDecodeBounds=false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int be=(int)(options.outHeight/(float)height);
			if(be<=0){
				be=1;
			}
			options.inSampleSize=be;
			is.close();
			is = new FileInputStream(imgPath);
			bitmap=BitmapFactory.decodeStream(is, null, options);
			is.close();
		}catch (FileNotFoundException e) {
			Log.e("ImageUtil", "文件："+imgPath+"  找不到");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("ImageUtil", e.getMessage());
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static Bitmap createThumbnail(Bitmap bm,int height){
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] buf=baos.toByteArray();
		BitmapFactory.decodeByteArray(buf, 0, buf.length, options);
		options.inJustDecodeBounds=false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		int be=(int)(options.outHeight/(float)height);
		if(be<=0){
			be=1;
		}
		options.inSampleSize=be;
		Bitmap bitmap=BitmapFactory.decodeByteArray(buf, 0, buf.length, options);
		return bitmap;
	}

	public static void writeBitmap(Bitmap drawingCache, File bitmap) {
		try {
			bitmap.createNewFile();
			FileOutputStream ot = new FileOutputStream(bitmap);
			drawingCache.compress(CompressFormat.PNG, 50, ot);
			ot.flush();
			ot.close();
		} catch (Exception e) {
			
		}
	}
	
	public static int[] getBitmapSize(String imgPath){
		InputStream is=null;
		int height=0;
		int width=0;
		int[] size=new int[2];
		try {
			is = new FileInputStream(imgPath);
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inJustDecodeBounds=true;
			BitmapFactory.decodeStream(is, null, options);
			options.inJustDecodeBounds=false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			height=options.outHeight;
			width=options.outWidth;
			size[0]=width;
			size[1]=height;
		}catch (FileNotFoundException e) {
			Log.e("ImageUtil-->getBitmapSize", "文件："+imgPath+"  找不到");
			e.printStackTrace();
		}
		return size;
	}
	
	public static int[] getBitmapSize(Bitmap bm){
		int height=0;
		int width=0;
		int[] size=new int[2];
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();        
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] buf=baos.toByteArray();
		BitmapFactory.decodeByteArray(buf, 0, buf.length, options);
		options.inJustDecodeBounds=false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		height=options.outHeight;
		width=options.outWidth;
		size[0]=width;
		size[1]=height;
		return size;
	}
	
	private static void createNavBarImg(Group group,int index,String magId,String orientation,int w,int h){
		List<Picture> pics = group.getPictures();
		File indexImg=new File(AppConfigUtil.getAppExtDir(magId+File.separator+"NavBar"+File.separator+orientation+File.separator+index+".png"));
		if(!indexImg.exists()){
			if(pics.size()>0){
				File navBar=new File(AppConfigUtil.getAppExtDir(magId+File.separator+"NavBar"+File.separator+orientation));
				if(!navBar.exists()){
					navBar.mkdirs();
				}
				File file=new File(navBar,index+".png");
				if(!file.exists()){
					try {
						String resource=pics.get(0).getResource();
						if(resource!=null&&!"".equals(resource.trim())){
							String imgPath=AppConfigUtil.getAppResourceImage(magId, "/"+resource);
							int height=ImageUtil.getBitmapSize(imgPath)[1];
							Bitmap cutBitmap=null;
							Bitmap bm=null;
							Bitmap bitmap=null;
							if(height>h){
								bm=ImageUtil.loadImage(imgPath);
								cutBitmap=Bitmap.createBitmap(bm, 0, 0,w,h);
								bitmap=ImageUtil.createThumbnail(cutBitmap, 200);
							}else{
								bitmap=ImageUtil.createThumbnail(imgPath, 200);
							}
							
							FileOutputStream out=new FileOutputStream(file);
							if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){
							    out.flush();
							    out.close();
							}
							bitmap.recycle();
							if(bm!=null){
								bm.recycle();
							}
							if(cutBitmap!=null){
								cutBitmap.recycle();
							}
							System.gc();
						}else{
							file.createNewFile();
						}
					} catch (FileNotFoundException e) {
						Log.e("FileNotFoundException", e.getMessage());
					} catch (IOException e) {
						Log.e("IOException", e.getMessage());
					}
				}
			}
		}
	}

	public  static  String createNavBar(Context context,String magid) {
		MagazineReader magareader = new MagazineReader();
		File content = new File(AppConfigUtil.getAppContent(magid));
		if (!content.exists()) {
			return "content.xml文件不存在";
		}
		try {
			InputStream stream = new FileInputStream(content);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(magareader.getRootElement().getContentHandler());
			reader.parse(new InputSource(stream));
			Magazine magazine=magareader.getMagazine();
			magazine.rebuild();
			
			List<Category> categorys = magazine.getCategorys();
			// 避免解析不正确直接程序退出
			int flag=0;
			if(categorys!=null&&categorys.size()>0){
				Category category = categorys.get(0);
				List<Page> pages = category.getPages();
				for (int i = 0; i < pages.size(); i++) {
					Page page=pages.get(i);
					List<Group> groups = page.getGroups();
					for(Group g:groups){
						if(g.getOrientation().equals(BasicView.LANDSCAPE)){
							Group firstGroup=findFirstGroup(g,1024,768);
							if(firstGroup!=null){
								createNavBarImg(firstGroup, i, magid,"landscape",1024,768);
							}else{
								flag++;
							}
						}else{
							Group firstGroup=findFirstGroup(g,768,1024);
							if(firstGroup!=null){
								createNavBarImg(firstGroup, i, magid,"portrait",768,1024);
							}else{
								flag++;
							}
						}
					}
				} 
			}
			magazine=null;
			if(flag==2){
				return "解析FirstGroup失败,请稍后重试";
			}
		} catch (FileNotFoundException e) {
		} catch (ParserConfigurationException e) {
			return "解析content.xml失败";
		} catch (SAXException e) {
			return "解析content.xml失败";
		} catch (IOException e) {
			return "读取content.xml失败";
		}
		return null;
	}
	
	public static Group findFirstGroup(Group group,int width,int height){
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
		
		if(frames[2]>=width&&frames[3]>=height&&(hots.size()!=0||layers.size()!=0||animations.size()!=0||videos.size()!=0||pictureSets.size()!=0
				||pictures.size()!=0||sliders.size()!=0||shutters.size()!=0||groups.size()>1||rotater.size()!=0)){
			return group;
		}else{
			if(group.getGroups().size()>0){
				return findFirstGroup(group.getGroups().get(0),width,height);
			}else{
				return null;
			}
		}
	}
	
	public static void recycleBookshelfResource(List<Object> objs){
		for(Object obj:objs){
			if(obj instanceof ImageView){
				ImageView img=(ImageView)obj;
				ImageUtil.recycle(img);
			}else if(obj instanceof LinearLayout){
				LinearLayout layout=(LinearLayout)obj;
				if(layout.getId()==R.id.scroll_layout){
					int childCount=layout.getChildCount();
					for(int i=0;i<childCount;i++){
						LinearLayout itemLayout=(LinearLayout)layout.getChildAt(i);
						ImageView imgView=(ImageView) itemLayout.findViewById(R.id.img);
						ImageUtil.recycle(imgView);
					}
					layout.removeAllViews();
				}else{
					int countImgLayout=layout.getChildCount();
					for(int i=0;i<countImgLayout;i++){
						ImageView img=(ImageView)layout.getChildAt(i);
						ImageUtil.recycle(img);
					}
				}
			}else if(obj instanceof HorizontalScrollView){
				HorizontalScrollView scrollView=(HorizontalScrollView)obj;
				Drawable  bottomDrawable=scrollView.getBackground();
				if(bottomDrawable!=null){
					Bitmap bottomBm=((BitmapDrawable)bottomDrawable).getBitmap();
					if(bottomBm!=null){
						bottomBm.recycle();
					}
				}
				scrollView.setBackgroundDrawable(null);
			}else if(obj instanceof GridView){
				GridView grid=(GridView)obj;
				int childCount=grid.getChildCount();
				for(int i=0;i<childCount;i++){
					LinearLayout itemLayout=(LinearLayout)grid.getChildAt(i);
					ImageView imgView=(ImageView) itemLayout.findViewById(R.id.img);
					ImageUtil.recycle(imgView);
					imgView=null;
				}
			}else if(obj instanceof RelativeLayout){
				RelativeLayout layout=(RelativeLayout)obj;
				Drawable bgDrawable=layout.getBackground();
				if(bgDrawable!=null){
					Bitmap bgBm=((BitmapDrawable)bgDrawable).getBitmap();
					if(bgBm!=null){
						bgBm.recycle();
					}
				}
				layout.setBackgroundDrawable(null);
			}
		}
		System.gc();
	}
}
