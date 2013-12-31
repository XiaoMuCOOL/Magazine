package com.rabbit.magazine;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.view.FlipperPageView2;
import com.rabbit.magazine.view.GalleryPageView;

public class MagazinePageAdapter extends BaseAdapter {

	private static final int PAGE = 8;
	private List<Page> pages;
	private MagazineLoaderActivity activity;

	private GalleryPageView view;
	private int page;
	
	public MagazinePageAdapter(Context context, GalleryPageView galleryPageView, int page2) {
		super();
		this.activity=(MagazineLoaderActivity) context;
		this.view=galleryPageView;
		List<Category> categorys = activity.getMagazine().getCategorys();
		pages = categorys.get(0).getPages();
		this.setPage(page2);
	}

	public MagazinePageAdapter(MagazineLoaderActivity magazineActivity, Magazine magazine) {
		this.activity = magazineActivity;
		List<Category> categorys = magazine.getCategorys();
		pages = categorys.get(0).getPages();
	}

	public MagazinePageAdapter(Context activity) {
		this.activity = (MagazineLoaderActivity) activity;
	}

	public MagazinePageAdapter(Context context, GalleryPageView galleryPageView) {
		this.activity = (MagazineLoaderActivity) context;
		view=galleryPageView;
	}

	@Override
	public int getCount() {
		return PAGE;
	}

	@Override
	public Object getItem(int position) {
		return pages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		String appCache = AppConfigUtil.getAppCache(AppConfigUtil.MAGAZINE_ID);
		String path = appCache + "/" + position + ".png";
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if (bitmap == null) {
			MagazineActivity act=(MagazineActivity) activity;
			FlipperPageView2 flipperPageView = act.getFlipperPageView();
			flipperPageView.setCurrentItem(position);
			flipperPageView.buildDrawingCache();
			bitmap= flipperPageView.getDrawingCache();
			if(bitmap!=null){
				act.addCacheImage(path, bitmap);
			}
			else if(bitmap==null){
				bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
			}
		}
		ImageView image = new ImageView(activity);
		image.setTag(position);
		image.setBackgroundColor(Color.BLUE);
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		Gallery.LayoutParams params = new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,
				Gallery.LayoutParams.FILL_PARENT);
		params.width = 500;
		params.height = 400;
		image.setScaleType(ScaleType.FIT_XY);
		image.setLayoutParams(params);
		image.setImageBitmap(bitmap);
		if(view!=null){
			view.addCache(bitmap);
		}
		return image;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

}
