package com.rabbit.magazine.view;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.OnFullScreenListener;
import com.rabbit.magazine.R;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import android.view.View.OnClickListener;

public class CustVideoView2 extends FrameLayout implements OnClickListener,OnFullScreenListener{
	
	private Timer progressTimer;
	
	//private Handler handler;
	
	private  VideoView videoView;
	
	private Video video;
	
	private TextView timeTextView;
	
	private String videoId;
	
	private String fullscreen;
	
	private SeekBar videoSeekBar;
	
	private View controllerBar;
	
	public View getControllerBar() {
		return controllerBar;
	}

	private Timer timer;
	
	private Context context;
	
	private String automatic;
	
	private boolean autoClose=false;
	
	private VideoPreview videoPreview;
	
	public VideoPreview getVideoPreview() {
		return videoPreview;
	}

	public void setVideoPreview(VideoPreview videoPreview) {
		this.videoPreview = videoPreview;
	}

	private boolean isStart;
	
	private ImageButton fullBtn;
	
	private int[] frames;
	
	public ImageButton getFullBtn() {
		return fullBtn;
	}

	public boolean isStart() {
		return isStart;
	}

	private Handler timerHandler;

	public CustVideoView2(Context context,Video video) {
		super(context);
		this.setVideo(video);
		this.context=context;
		videoId=UUID.randomUUID().toString();
		((MagazineActivity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				timerHandler=new Handler(){
					public void handleMessage(android.os.Message msg) {
						if(msg.what==1){
							controllerBar.setVisibility(View.INVISIBLE);
						}
					};
				};
			}
		});
        setBackgroundColor(Color.BLACK);
		
		String frame = video.getFrame();
		frames = FrameUtil.frame2int(frame);
		frames=FrameUtil.autoAdjust(frames, context);
		String closesOnEnd = video.getClosesOnEnd();
		if (closesOnEnd!=null&&closesOnEnd.equalsIgnoreCase("true")) {
			autoClose = true;
		}
		automatic=video.getAutomatic();
		fullscreen = video.getFullscreen();
		LayoutParams frameLayoutparams=new LayoutParams(frames[2], frames[3]);
		frameLayoutparams.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(frameLayoutparams);
		
		((MagazineActivity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				videoView=new VideoView(CustVideoView2.this.context);
				LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				params.gravity=Gravity.CENTER;
				videoView.setLayoutParams(params);
				String path = AppConfigUtil.getAppResource(AppConfigUtil.MAGAZINE_ID) + CustVideoView2.this.video.getResource();
				videoView.setVideoPath(path);
				videoView.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						if (autoClose) {
							normalPlay();
							mp.stop();
							videoView.setVisibility(View.GONE);
							controllerBar.setVisibility(View.GONE);
							//setVisibility(View.INVISIBLE);
							if(getVideoPreview()!=null){
								getVideoPreview().setVisibility(View.VISIBLE);
								getVideoPreview().bringToFront();
							}
						}
					}
				});
				
		        addView(videoView);
			}
		});
		
		
		controllerBar=((MagazineActivity)context).getLayoutInflater().inflate(R.layout.video_controller_bar, null);
        LayoutParams cbParams=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        controllerBar.setLayoutParams(cbParams);
        controllerBar.setAlpha(0.8f);
        controllerBar.setVisibility(View.INVISIBLE);
        controllerBar.setFocusable(true);
        controllerBar.bringToFront();
        controllerBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return true;
			}
		});
        
        videoSeekBar = (SeekBar) controllerBar.findViewById(R.id.videoSeekBar);
		videoSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				startTimer();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				stopTimer();
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					videoView.seekTo((int) (progress * 1.0/ seekBar.getMax() * videoView.getDuration()));
					seekBar.setProgress(progress);
				}
			}
		});
		timeTextView = (TextView) controllerBar.findViewById(R.id.timeText);
		ImageButton playBtn=(ImageButton)controllerBar.findViewById(R.id.playBtn);
		playBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isPlaying=videoView.isPlaying();
				ImageButton btn=(ImageButton)v;
				if(isStart){
					if(isPlaying){
						videoView.pause();
						btn.setImageResource(R.drawable.pause);
					}else{
						videoView.start();
						btn.setImageResource(R.drawable.play);
					}
				}else{
					start();
				}
			}
		});
		fullBtn=(ImageButton) controllerBar.findViewById(R.id.fullBtn);
		fullBtn.setTag("normal");
		fullBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object obj=v.getTag();
				if("normal".equals(obj.toString())){
					fullPlay();
				}
				if("full".equals(obj.toString())){
					normalPlay();
				}
			}
		});
		addView(controllerBar);
		setOnClickListener(this);
		
		((MagazineActivity)context).addVideos(this);
	}
	
	public VideoView getVideoView() {
		return videoView;
	}

	private static String getTimeFormatValue(long time) {
		return MessageFormat.format("{0,number,00}:{1,number,00}:{2,number,00}",time / 1000 / 60 / 60, time / 1000 / 60 % 60, time / 1000 % 60);
	}

	@Override
	public void onClick(View v) {
		if(controllerBar.getVisibility()==View.VISIBLE){
			controllerBar.setVisibility(View.INVISIBLE);
		}else{
			controllerBar.setVisibility(View.VISIBLE);
			controllerBar.bringToFront();
			stopTimer();
			startTimer();
		}
	}
	
	private void stopTimer(){
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
	}
	
	private void startTimer(){
		timer=new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				Message msg=new Message();
				msg.what=2;
				msg.obj=controllerBar;
				((MagazineActivity)context).getHandler().sendMessage(msg);
			}
		}, 5000);
	}

	public String getVideoId() {
		return videoId;
	}
	
	private void initialScheduleService(){
		if(progressTimer==null){
			progressTimer = new Timer();
			progressTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					((MagazineActivity)context).getHandler().post(new Runnable() {
						@Override
						public void run() {
							if (videoView.isPlaying()) {
								float position = videoView.getCurrentPosition();
								int duration = videoView.getDuration();
								timeTextView.setText(getTimeFormatValue((int) position)+ " / "+ getTimeFormatValue(duration));
								videoSeekBar.setProgress((int) (position/ duration * videoSeekBar.getMax()));
							}
						}
					});
					/*handler.post(new Runnable() {
						@Override
						public void run() {
							if (videoView.isPlaying()) {
								float position = videoView.getCurrentPosition();
								int duration = videoView.getDuration();
								timeTextView.setText(getTimeFormatValue((int) position)+ " / "+ getTimeFormatValue(duration));
								videoSeekBar.setProgress((int) (position/ duration * videoSeekBar.getMax()));
							}
						}
					});*/
				}
			}, 1000, 1000);
		}
	}
	
	public void start(){
		videoView.setVisibility(View.VISIBLE);
		setVisibility(View.VISIBLE);
		videoView.start();
		initialScheduleService();
		isStart=true;
		if(videoPreview!=null){
			videoPreview.setVisibility(View.INVISIBLE);
		}
		
	}

	//全屏关闭
	@Override
	public void close() {
		ArrayList<CustVideoView2> videos=((MagazineActivity)CustVideoView2.this.context).videos;
		for(CustVideoView2 video:videos){
			if(!video.getVideoId().equals(videoId)){
				if(!"true".equals(video.getVideo().getFullscreen().toLowerCase())){
					video.getVideoView().setVisibility(View.VISIBLE);
				}
			}
		}
		videoView.pause();
		videoView.setVisibility(View.GONE);
		setVisibility(View.INVISIBLE);
	}
	
	public void release(){
		if(progressTimer!=null){
			progressTimer.cancel();
			progressTimer=null;
		}
		isStart=false;
		if(videoPreview!=null){
			videoPreview.releasePreview();
		}
	}
	
	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}
	
	public void loadPreview(){
		if(videoPreview!=null){
			videoPreview.loadPreviewImg();
			videoPreview.bringToFront();
		}
	}
	
	public void releasePreview(){
		if(videoPreview!=null){
			videoPreview.releasePreview();
		}
	}
	
	public void fullPlay(){
		ArrayList<CustVideoView2> videos=((MagazineActivity)context).videos;
		for(CustVideoView2 video:videos){
			if(!video.getVideoId().equals(videoId)){
				boolean isPlaying=video.getVideoView().isPlaying();
				if(isPlaying){
					video.getVideoView().pause();
				}
			}
		}
		((MagazineActivity)context).isFullPlay=true;
		int widthPixels = AppConfigUtil.WIDTHPIXELS;
		int heightPixels = AppConfigUtil.HEIGHTPIXELS;
		LayoutParams params=new LayoutParams(widthPixels, heightPixels);
		params.setMargins(0, 0, 0, 0);
		setLayoutParams(params);
		fullBtn.setTag("full");
	}
	
	public void normalPlay(){
		((MagazineActivity)context).isFullPlay=false;
		LayoutParams params=new LayoutParams(frames[2], frames[3]);
		params.setMargins(frames[0], frames[1], 0, 0);
		setLayoutParams(params);
		fullBtn.setTag("normal");
	}
}
