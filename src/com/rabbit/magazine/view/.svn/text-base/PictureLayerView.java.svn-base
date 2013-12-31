package com.rabbit.magazine.view;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.rabbit.magazine.kernel.Picture;

public class PictureLayerView extends ImageView {

	private Context context;
	private Picture picture;

	public PictureLayerView(Context context) {
		super(context);
	}

	//
	// public PictureLayerView(Context context, Picture picture) {
	// // TODO Auto-generated constructor stub
	// }

	public PictureLayerView(Context context, Picture picture) {
		super(context);
		this.context = context;
		this.picture = picture;
		this.picture = picture;
		String resource = picture.getResource();
		try {
			String substring = resource.trim().substring(1, resource.trim().length());
			Log.i("magazine", substring);
			InputStream image = context.getAssets().open(substring);
			BitmapDrawable drawable = new BitmapDrawable(image);
			setImageDrawable(drawable);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
