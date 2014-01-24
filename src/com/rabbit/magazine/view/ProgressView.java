package com.rabbit.magazine.view;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.content.Context;  
import android.graphics.Canvas;  
import android.graphics.Paint;   
import android.graphics.RectF;  
import android.graphics.Color;
import android.graphics.Typeface;

public class ProgressView extends View {
	
	private int _progress = 0;             //百分比  0~100
	private int arcProgress = 0;           //弧形角度 0~360

	private int outer_radius = 101;
	private int inner_radius = 78;
	
	Paint vPaint = new Paint();  //绘制样式物件
	
	
	public ProgressView(Context context){
		
		super(context);
	}
	
	public ProgressView(Context context, AttributeSet attrs) {
		
		super(context,attrs);
	}
	
	public ProgressView(Context context, AttributeSet attrs, int defStyle)  {
		
		super(context,attrs,defStyle);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		//水平margin
		int margin_w = (ProgressView.this.getWidth() - outer_radius)/ 2;
	    //垂直
	    int margin_h = (ProgressView.this.getHeight() - outer_radius)/ 2;
		RectF circleRect = new RectF(margin_w, margin_h, ProgressView.this.getWidth() - margin_w, ProgressView.this.getHeight() - margin_h);
		//_progress = 50;
		
		//System.out.printf("onDraw in Myview!");
        super.onDraw(canvas);
//        vPaint.setTypeface(Typeface.MONOSPACE);
//        vPaint.setTextAlign(Paint.Align.CENTER);
        Log.v("PView", _progress+"");
        vPaint.setStrokeWidth(1);
        vPaint.setStyle(Paint.Style.FILL);
        vPaint.setAntiAlias(true);
        vPaint.setColor(Color.WHITE);
        vPaint.setTextSize(30f);
        vPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(_progress+"%",ProgressView.this.getWidth()/2,ProgressView.this.getHeight()/2 + 15,vPaint);

        //绘制底部条
        vPaint.setColor(Color.argb(64, 255, 255, 255));
        vPaint.setAntiAlias( true );   //反锯齿
        vPaint.setStyle( Paint.Style.STROKE );
        vPaint.setStrokeWidth(outer_radius - inner_radius);
        canvas.drawArc(circleRect, 0, 360, false, vPaint);
        
        //绘制进度条
        vPaint.setColor( Color.argb(255, 145, 196, 20) ); 
        arcProgress = 360 * _progress / 100;
        canvas.drawArc(circleRect, -90, arcProgress, false, vPaint);
        
	}
	
	public void setProgress(int p){
		this._progress = p;
		// 重绘, 再一次执行onDraw 程序
		invalidate();
	}
	

}