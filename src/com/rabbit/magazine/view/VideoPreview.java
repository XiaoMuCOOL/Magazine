package com.rabbit.magazine.view;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.util.StringUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;

public class VideoPreview extends ImageButton implements OnClickListener {
	
	private Video video;
	
	private Context context;
	
	private View groupView;
	
	private Bitmap previewBitmap;
	
	private Bitmap playIconBitmap;
	
	private CustVideoView2 videoView;
	
	public VideoPreview(Context context,Video video,CustVideoView2 videoView) {
		super(context);
		this.video=video;
		this.context=context;
		this.videoView=videoView;
		//loadPreviewImg();
		String frame = video.getFrame();
		int[] frames = FrameUtil.frame2int(frame);
		frames=FrameUtil.autoAdjust(frames, context);
		LayoutParams params=new LayoutParams(frames[2],frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		setOnClickListener(this);
	}
	
	public void loadPreviewImg(){
		String previewPath=video.getPreview();
		String imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, previewPath);
		previewBitmap=ImageUtil.loadImage(imgPath);
		setBackgroundDrawable(new BitmapDrawable(previewBitmap));
		String playIconPath = video.getPlayIcon();
		if(StringUtil.isNotNull(playIconPath)){
			imgPath = AppConfigUtil.getAppResourceImage(AppConfigUtil.MAGAZINE_ID, playIconPath);
			playIconBitmap=ImageUtil.loadImage(imgPath);
			setImageBitmap(playIconBitmap);
		}
		setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if(video.getFullscreen().equalsIgnoreCase("true")){
			videoView.fullPlay();
		}
		videoView.start();
	}

	public void releasePreview(){
		if(previewBitmap!=null&&!previewBitmap.isRecycled()){
			previewBitmap.recycle();
		}
		if(playIconBitmap!=null&&!playIconBitmap.isRecycled()){
			playIconBitmap.recycle();
		}
		setImageBitmap(null);
		setBackgroundDrawable(null);
	}
	
	private void addVideoView(CustVideoView2 videoView){
		if(groupView.getClass().equals(FirstGroupView.class)){
			FirstGroupView view=(FirstGroupView)groupView;
			view.addView(videoView);
		}
		if(groupView.getClass().equals(FrameLayout.class)){
			FrameLayout view=((GroupView2)groupView).getFrameLayout();
			view.addView(videoView);
		}
	}
}
