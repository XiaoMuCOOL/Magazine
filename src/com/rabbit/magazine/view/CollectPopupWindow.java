package com.rabbit.magazine.view;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.FavoriteInfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.kernel.BasicView;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

public class CollectPopupWindow extends PopupWindow {

	private FlipperPageView2 mFlipperPageView;
	
	private Context mContext;
	
	public Button btn;

	public CollectPopupWindow(Context context,FlipperPageView2 flipperPageView){
		super(context);
		this.mFlipperPageView=flipperPageView;
		this.mContext=context;
		View layout= ((Activity)context).getLayoutInflater().inflate(R.layout.collect, null);
		setContentView(layout);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		btn=(Button) layout.findViewById(R.id.collect);
		Button btn1=(Button) layout.findViewById(R.id.collect1);
		int curIndex=mFlipperPageView.getCurrentItem();
		String orientation;
		if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			orientation=BasicView.LANDSCAPE;
		}else{
			orientation=BasicView.PORTRAIT;
		}
		MagazineService magService=new MagazineService(mContext);
		List<FavoriteInfo> list=magService.queryFavorite(AppConfigUtil.MAGAZINE_ID, curIndex,orientation);
		if(list.size()>0){
			btn.setBackgroundResource(R.drawable.bt_collection);
		}else{
			btn.setBackgroundResource(R.drawable.new_sc1);
		}
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button btn=(Button) v;
				int curIndex=mFlipperPageView.getCurrentItem();
				String orientation;
				if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
					orientation=BasicView.LANDSCAPE;
				}else{
					orientation=BasicView.PORTRAIT;
				}
				MagazineService magService=new MagazineService(mContext);
				List<FavoriteInfo> list=magService.queryFavorite(AppConfigUtil.MAGAZINE_ID, curIndex,orientation);
				if(list.size()>0){
					FavoriteInfo info=list.get(0);
					magService.deleteFavorite(info.getId());
					list.remove(0);
					//Toast.makeText(mContext, "此页已收藏", Toast.LENGTH_SHORT).show();
					Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
					btn.setBackgroundResource(R.drawable.new_sc1);
				}else{
					FavoriteInfo info=new FavoriteInfo();
					String imgPath=AppConfigUtil.getAppExtDir()+File.separator+AppConfigUtil.MAGAZINE_ID+File.separator+"NavBar"+File.separator+orientation+File.separator+curIndex+".png";
					info.setImgPath(imgPath);
					info.setIndex(curIndex);
					info.setMagId(AppConfigUtil.MAGAZINE_ID);
					info.setPageSize(+mFlipperPageView.getmPages().size());
					info.setTitle(AppConfigUtil.MAGAZINE_TITLE);
					info.setOrientation(orientation);
					magService.saveFavorite(info);
					Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
					btn.setBackgroundResource(R.drawable.bt_collection);
				}
				//mFlipperPageView.showNavBar();
			}
		});
	}
}
