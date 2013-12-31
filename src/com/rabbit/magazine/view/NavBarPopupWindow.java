package com.rabbit.magazine.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.R;
import com.rabbit.magazine.adapter.NavBarAdapter;
import com.rabbit.magazine.util.ImageUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.FrameLayout.LayoutParams;

public class NavBarPopupWindow extends PopupWindow {

	private Gallery gallery;
	
	private String[] filenames;
	
	private int[] indexs={0,1,2,3,4};
	
	private int index=0;
	
	private FlipperPageView2 flipperPageView;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	private NavBarAdapter adapter;
	
	private Bitmap[] bms;
	
	private int count;
	
	private Context context;
	
	public NavBarPopupWindow(Context context,FlipperPageView2 flipperPageView){
		super(context);
		this.context=context;
		this.flipperPageView=flipperPageView;
		View layout= ((Activity)context).getLayoutInflater().inflate(R.layout.navbar, null);
		setContentView(layout);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		String orientation;
		if (AppConfigUtil.WIDTH_ADJUST>AppConfigUtil.HEIGHT_ADJUST){
			orientation="landscape";
		}else{
			orientation="portrait";
		}
		File navBar=new File(AppConfigUtil.getAppExtDir(AppConfigUtil.MAGAZINE_ID+File.separator+"NavBar"+File.separator+orientation));
		filenames=navBar.list();
		
		if(filenames!=null){
			Arrays.sort(filenames, new Comparator<String>() {

				@Override
				public int compare(String name1, String name2) {
					String str1=name1.substring(0,name1.lastIndexOf("."));
					String str2=name2.substring(0,name2.lastIndexOf("."));
					int f1=Integer.parseInt(str1);
					int f2=Integer.parseInt(str2);
					if(f1>f2){
						return 1;
					}else if(f1<f2){
						return -1;
					}else{
						return 0;
					}
				}
			});
			bms=new Bitmap[filenames.length];
			count=filenames.length;
			gallery=(Gallery) layout.findViewById(R.id.gallery);
			adapter=new NavBarAdapter(bms,context);
			gallery.setAdapter(adapter);
			gallery.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					gallery.setSelection(arg2);
					int curItem=NavBarPopupWindow.this.flipperPageView.getCurrentItem();
					if(curItem!=arg2){
						NavBarPopupWindow.this.flipperPageView.videoPause();
						NavBarPopupWindow.this.flipperPageView.gotoPage(arg2);
					}
				}
			});
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					index=position;
					int k=0;
					for(int i=2;i>=1;i--){
						if(position-i>=0){
							indexs[k++]=position-i;
						}else{
							indexs[k++]=-1;
						}
					}
					indexs[k++]=position;
					for(int i=1;i<=2;i++){
						if(position+i<count){
							indexs[k++]=position+i;
						}else{
							indexs[k++]=-1;
						}
					}
					loadBitmap();
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					for(int i=0;i<bms.length;i++){
						recycleBitmap(i);
					}
				}
			});
		}
	}
	
	public void loadBitmap(){
		for(int i=0;i<count;i++){
			boolean flag=false;
			for(int j=0;j<5;j++){
				if(indexs[j]==i){
					flag=true;
					break;
				}
			}
			if(flag){
				if(adapter.bms[i]==null){
					String orientation;
					if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
						orientation="landscape";
					}else{
						orientation="portrait";
					}
					String imgPath=AppConfigUtil.getAppExtDir(AppConfigUtil.MAGAZINE_ID+File.separator+"NavBar"+File.separator+orientation+File.separator+filenames[i]);
					Bitmap bm=ImageUtil.loadImage(imgPath);
					adapter.bms[i]=bm;
				}
			}else{
				recycleBitmap(i);
			}
		}
		adapter.notifyDataSetChanged();
		gallery.setSelection(index);
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		loadBitmap();
		super.showAtLocation(parent, gravity, x, y);
	}
	
	private void recycleBitmap(int i){
		if(bms[i]!=null&&!bms[i].isRecycled()){
			bms[i].recycle();
			bms[i]=null;
			System.gc();
		}
	}
}
