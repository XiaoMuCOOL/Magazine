package com.rabbit.magazine.view;

import java.util.ArrayList;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageViewAdapter extends BaseAdapter {
	
	private ArrayList<ImageView> thumbnails;
	
	public ImageViewAdapter(ArrayList<ImageView> thumbnails){
		this.thumbnails=thumbnails;
	}
	
	@Override
	public int getCount() {
		return thumbnails.size();
	}

	@Override
	public Object getItem(int position) {
		return thumbnails.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView img;
        if (convertView == null) {
        	img = thumbnails.get(position);
        } else {
        	img = (ImageView) convertView;
        }
        return img;
	}

	public ArrayList<ImageView> getImgs() {
		return thumbnails;
	}

	public void setImgs(ArrayList<ImageView> imgs) {
		this.thumbnails = imgs;
	}

}
