package com.rabbit.magazine.adapter;

import com.rabbit.magazine.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class NavBarAdapter extends BaseAdapter {
	
	public Bitmap[] bms;
	
	private Context context;
	
	public NavBarAdapter(Bitmap[] bms,Context context){
		this.bms=bms;
		this.context=context;
	}

	@Override
	public int getCount() {
		return bms.length;
	}

	@Override
	public Object getItem(int position) {
		return bms[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView img;
		if (convertView == null) {
			img = new ImageView(context);
			img.setLayoutParams(new Gallery.LayoutParams(200,200));
			Bitmap bm=bms[position];
			img.setImageBitmap(bm);
			img.setLayoutParams(new Gallery.LayoutParams(200,200));
			img.setBackgroundResource(R.drawable.imageview_background_unselected);
		}else{
			img=(ImageView)convertView;
		}
		return img;
	}

}
