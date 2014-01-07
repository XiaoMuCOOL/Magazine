package com.rabbit.magazine.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.FavoriteInfo;
import com.rabbit.magazine.GerenInfo;
import com.rabbit.magazine.Magazineinfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.adapter.FavoriteAdapter;
import com.rabbit.magazine.adapter.FlipperPagerAdapter;
import com.rabbit.magazine.adapter.GerenGridAdapter;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.download.FileDownloader;
import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.parser.MagazineReader;
import com.rabbit.magazine.service.DownloadService;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.view.FlipperPageView2;
import com.rabbit.magazine.view.PageView2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 书架浏览
 * 
 * @author litingwen
 * 
 */
public class BookshelfActivity extends Activity{

	// Forbes
	//private static final String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=19&debugger=true";
	// Sony
	//private static final String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=21&debugger=true";
	
	//http://imag.nexdoor.cn/api/getmagazinedata.php?code=12

	//	http://imag.nexdoor.cn/api/getmagazinedata.php?code=12&debugger=true  这个接口可以看到未上架的书 可能会比较多一点
	
	//private static final String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=15&debugger=true";
	
	private int curMagIndex;
	
	private List<Magazineinfo> magList;
	
	private GerenGridAdapter gerenAdapter;
	
	private GridView gerenGrid;
	
	private GridView favoriteGrid;
	
	private FavoriteAdapter favoriteAdapter;
	
	private LinearLayout scroll_layout;
	
	private boolean fromMagazineActivity=false;
	
	private List<Object> objs=new ArrayList<Object>();
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.arg1){
			case 1:
				adjustWidgetParams();
				
				ImageButton delImgBtn=(ImageButton)findViewById(R.id.btn_del);
				delImgBtn.setImageResource(R.drawable.btn_delete_off);
				
				ImageButton listImgBtn=(ImageButton)findViewById(R.id.list);
				listImgBtn.setImageResource(R.drawable.new_btn_list_on);
				
				ImageButton dingyueImgBtn=(ImageButton)findViewById(R.id.btn_dingyue);
				dingyueImgBtn.setImageResource(R.drawable.btn_dingyue_off);
				
				ImageButton gerenImgBtn=(ImageButton)findViewById(R.id.btn_geren);
				gerenImgBtn.setImageResource(R.drawable.new_btn_geren_off);
				
				ImageButton banquanImgBtn=(ImageButton)findViewById(R.id.btn_banquan);
				banquanImgBtn.setImageResource(R.drawable.new_btn_banquan_off);
				
				//HorizontalScrollView bottomScroll=(HorizontalScrollView)findViewById(R.id.bottomScroll);
				//bottomScroll.setBackgroundResource(R.drawable.list_bg_bottom);
				
				LinearLayout imgLayout=(LinearLayout)findViewById(R.id.imgLayout);
				int imgLayoutCount=imgLayout.getChildCount();
				for(int i=0;i<imgLayoutCount;i++){
					ImageView img=(ImageView)imgLayout.getChildAt(i);
					img.setImageResource(R.drawable.ban);
				}
				
				ImageView copyrightImg=(ImageView)findViewById(R.id.copyright);
				copyrightImg.setImageResource(R.drawable.information);
				break;
			case 2:
				scroll_layout=(LinearLayout)findViewById(R.id.scroll_layout);
				for(int i=0;i<magList.size();i++){
					View view=createBottomScrollItem(magList.get(i),i);
					scroll_layout.addView(view);
				}
				setCurrentMag(0);
				
				MagazineService magService=new MagazineService(BookshelfActivity.this);
				List<GerenInfo> gerenlist=magService.getAllGerens();
				gerenAdapter=new GerenGridAdapter(BookshelfActivity.this,gerenlist);
				gerenGrid.setAdapter(gerenAdapter);
				
				
				List<FavoriteInfo> favoritelist=magService.getAllFavorites();
				favoriteAdapter=new FavoriteAdapter(BookshelfActivity.this,favoritelist);
				favoriteGrid.setAdapter(favoriteAdapter);
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if(getIntent().getExtras()!=null){
			fromMagazineActivity=getIntent().getExtras().getBoolean("fromMagazineActivity");
		}
		setContentView(R.layout.bookshelf2);
		AppConfigUtil.WIDTH_ADJUST=1024;
		AppConfigUtil.HEIGHT_ADJUST=768;
		gerenGrid=(GridView)findViewById(R.id.gerenGrid);
		favoriteGrid=(GridView)findViewById(R.id.shuqianGrid);
		RelativeLayout bgLayout=(RelativeLayout)findViewById(R.id.bg);
		bgLayout.setBackgroundResource(R.drawable.new_bg_all);
		
		Button rightDownloadBtn=(Button)findViewById(R.id.rightDownloadBtn);
		rightDownloadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rightDownload(v);
			}
		});
		Button rightReadBtn=(Button)findViewById(R.id.rightReadBtn);
		rightReadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Magazineinfo mag=BookshelfActivity.this.magList.get(BookshelfActivity.this.curMagIndex);
				openMagaine(String.valueOf(mag.getId()),mag.getTitle(),0);
			}
		});
		
		/*if(1==1)
		return;*/
		
		new AsyncTask<Void, Integer, String>(){
			ProgressDialog dialog;
			@Override
			protected void onPreExecute() {
				dialog=new ProgressDialog(BookshelfActivity.this);
				dialog.setMessage("请稍等...");
				dialog.setCancelable(false);
				dialog.show();
			};
			@Override
			protected String doInBackground(Void... params) {
				
				try {
					Thread.sleep(60);
				} catch (InterruptedException e1) {
				}
				
				Message msg1=new Message();
				msg1.arg1=1;
				handler.sendMessage(msg1);
				MagazineService magService=new MagazineService(BookshelfActivity.this);
				magList=magService.getAllMagazines();
				
				Message msg2=new Message();
				msg2.arg1=2;
				handler.sendMessage(msg2);
					
				return null;
			}
			@Override
			protected void onPostExecute(String result) {
				dialog.dismiss();
			};
		}.execute();
	}
	
	private void adjustWidgetParams(){
		//TopBar 
		RelativeLayout topLayout=(RelativeLayout)findViewById(R.id.top);
		topLayout.setPadding(0, 0, 0, 15);
		LayoutParams topParams=(LayoutParams) topLayout.getLayoutParams();
		int[] frames=FrameUtil.autoAdjust(new int[]{0,85,AppConfigUtil.WIDTH_ADJUST,55}, this);
		topParams.width=frames[2];
		topParams.height=frames[3];
		topParams.setMargins(0, 0, 0, 0);
		
		ImageButton listBtn=(ImageButton)topLayout.findViewById(R.id.list);
		LayoutParams listBtnParams=(LayoutParams) listBtn.getLayoutParams();
		frames=FrameUtil.autoAdjust(new int[]{30,0,34,34}, this);
		listBtnParams.width=frames[2];
		listBtnParams.height=frames[3];
		//listBtnParams.setMargins(frames[0], frames[1], 0, 0);
		
		ImageButton gerenBtn=(ImageButton)topLayout.findViewById(R.id.btn_geren);
		LayoutParams gerenBtnParams=(LayoutParams) gerenBtn.getLayoutParams();
		frames=FrameUtil.autoAdjust(new int[]{27,23}, this);
		gerenBtnParams.width=frames[0];
		gerenBtnParams.height=frames[1];
		
		/*ImageButton shuqianBtn=(ImageButton)topLayout.findViewById(R.id.btn_shuqian);
		LayoutParams shuqianBtnParams=(LayoutParams) shuqianBtn.getLayoutParams();
		frames=FrameUtil.autoAdjust(new int[]{34,34}, this);
		shuqianBtnParams.width=frames[0];
		shuqianBtnParams.height=frames[1];*/
		
		ImageButton banquanBtn=(ImageButton)topLayout.findViewById(R.id.btn_banquan);
		LayoutParams banquanBtnParams=(LayoutParams) banquanBtn.getLayoutParams();
		frames=FrameUtil.autoAdjust(new int[]{13,27}, this);
		banquanBtnParams.width=frames[0];
		banquanBtnParams.height=frames[1];
		
		/*ImageButton qrBtn=(ImageButton)topLayout.findViewById(R.id.btn_qr);
		LayoutParams qrBtnParams=(LayoutParams) qrBtn.getLayoutParams();
		frames=FrameUtil.autoAdjust(new int[]{34,34}, this);
		qrBtnParams.width=frames[0];
		qrBtnParams.height=frames[1];*/
		
		ImageButton fenxiangBtn=(ImageButton)topLayout.findViewById(R.id.btn_del);
		LayoutParams fenxiangBtnParams=(LayoutParams) fenxiangBtn.getLayoutParams();
		frames=FrameUtil.autoAdjust(new int[]{20,23}, this);
		fenxiangBtnParams.width=frames[0];
		fenxiangBtnParams.height=frames[1];
		
		ImageButton dignyueBtn=(ImageButton)topLayout.findViewById(R.id.btn_dingyue);
		LayoutParams dignyueBtnParams=(LayoutParams) dignyueBtn.getLayoutParams();
		frames=FrameUtil.autoAdjust(new int[]{20,31}, this);
		dignyueBtnParams.width=frames[0];
		dignyueBtnParams.height=frames[1];
		
		//BottomBar
		HorizontalScrollView bottomScroll=(HorizontalScrollView)findViewById(R.id.bottomScroll);
		bottomScroll.getLayoutParams();
		LayoutParams bottomScrollParams=(LayoutParams)bottomScroll.getLayoutParams();
		frames=FrameUtil.autoAdjust(new int[]{AppConfigUtil.WIDTH_ADJUST,211,0,35}, this);
		bottomScroll.setPadding(0, frames[3], 0, 0);
		bottomScrollParams.height=730;//frames[1];
		
		LinearLayout bottomLayout=(LinearLayout)findViewById(R.id.scroll_layout);
		android.widget.FrameLayout.LayoutParams bottomLayoutParams=(android.widget.FrameLayout.LayoutParams) bottomLayout.getLayoutParams();
		int[] btmLayoutFrames=FrameUtil.autoAdjust(new int[]{-100,138,-100,30}, this);
		bottomLayoutParams.height=btmLayoutFrames[1];
		bottomLayoutParams.setMargins(0, 0, 0, btmLayoutFrames[3]);
		
		//MiddleBar
		RelativeLayout curMagLayout=(RelativeLayout)findViewById(R.id.curMagLayout);
		LayoutParams curMagParams=(LayoutParams) curMagLayout.getLayoutParams();
		int[] curMagFrames=FrameUtil.autoAdjust(new int[]{-100,30}, this);
		curMagParams.setMargins(0, curMagFrames[1], 0, 0);
		curMagLayout.bringToFront();
		
		ImageView coverImg=(ImageView)findViewById(R.id.cover);
		LayoutParams coverParams=(LayoutParams) coverImg.getLayoutParams();
		int[] coverFrames=FrameUtil.autoAdjust(new int[]{100,-100,323,392}, this);
		coverParams.width=coverFrames[2];
		coverParams.height=coverFrames[3];
		coverParams.setMargins(coverFrames[0], 0, 0, 0);
		
		LinearLayout rightLayout=(LinearLayout)findViewById(R.id.rightLayout);
		LayoutParams rightParams=(LayoutParams) rightLayout.getLayoutParams();
		int[] marge_right=FrameUtil.autoAdjust(new int[]{30},this);
		rightParams.setMargins(0, 0, marge_right[0], 0);
		
		TextView descTv=(TextView)findViewById(R.id.description);
		android.widget.LinearLayout.LayoutParams descParams=(android.widget.LinearLayout.LayoutParams)descTv.getLayoutParams();
		int[] descFrames=FrameUtil.autoAdjust(new int[]{-100,50,-100,20}, this);
		descParams.height=descFrames[1];
		descParams.setMargins(0, descFrames[3], 0, 0);
		
		HorizontalScrollView preScroll=(HorizontalScrollView)findViewById(R.id.preScroll);
		android.widget.LinearLayout.LayoutParams preScrollParams=(android.widget.LinearLayout.LayoutParams)preScroll.getLayoutParams();
		int[] preScrollFrames=FrameUtil.autoAdjust(new int[]{540,200,-100,20}, this);
		preScrollParams.width=preScrollFrames[0];
		preScrollParams.height=preScrollFrames[1];
		preScrollParams.setMargins(0, preScrollFrames[3], 0, 0);
		
		//geren
		LinearLayout imgLayout=(LinearLayout)findViewById(R.id.imgLayout);
		android.widget.FrameLayout.LayoutParams imgLayoutParams=(android.widget.FrameLayout.LayoutParams) imgLayout.getLayoutParams();
		int[] imgLayoutFrames=FrameUtil.autoAdjust(new int[]{-100,100}, this);
		imgLayoutParams.setMargins(0, imgLayoutFrames[1], 0, 0);
		
		int childCount=imgLayout.getChildCount();
		for(int i=0;i<childCount;i++){
			ImageView imgView=(ImageView) imgLayout.getChildAt(i);
			int[] imgFrames=FrameUtil.autoAdjust(new int[]{-100,165}, this);
			android.widget.LinearLayout.LayoutParams parmas=(android.widget.LinearLayout.LayoutParams) imgView.getLayoutParams();
			parmas.height=imgFrames[1]; 
		}
		
		GridView gerenGrid=(GridView)findViewById(R.id.gerenGrid);
		int[] vertFrames=FrameUtil.autoAdjust(new int[]{-100,30}, this);
		gerenGrid.setVerticalSpacing(vertFrames[1]);
		
		GridView shuqianGrid=(GridView)findViewById(R.id.shuqianGrid);
		android.widget.FrameLayout.LayoutParams shuqianParams=(android.widget.FrameLayout.LayoutParams) shuqianGrid.getLayoutParams();
		int[] shuqianFrames=FrameUtil.autoAdjust(new int[]{800,30}, this);
		shuqianParams.width=shuqianFrames[0];
		shuqianGrid.setVerticalSpacing(shuqianFrames[1]);
		
	}
	
	private View createBottomScrollItem(final Magazineinfo mag,int index){
		View view=this.getLayoutInflater().inflate(R.layout.item_bookshef, null);
		android.widget.LinearLayout.LayoutParams vParams=new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		int[] layoutFrames=FrameUtil.autoAdjust(new int[]{20,-100,10,-100}, this);
		vParams.setMargins(layoutFrames[0], 0, layoutFrames[0], 0);
		view.setLayoutParams(vParams);
		
		LinearLayout layout=(LinearLayout)view.findViewById(R.id.layout);
		android.widget.LinearLayout.LayoutParams params=(android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
		params.setMargins(layoutFrames[2], 0, 0, 0);
		String path=AppConfigUtil.getCoverImgPath(String.valueOf(mag.getId()));
		Bitmap bm=ImageUtil.loadImage(path);
		ImageView img=(ImageView)view.findViewById(R.id.img);
		img.setImageBitmap(bm);
		img.setTag(index);
		int[] imgParams=FrameUtil.autoAdjust(new int[]{110,138}, this);
		img.setLayoutParams(new android.widget.LinearLayout.LayoutParams(imgParams[0],imgParams[1]));
		
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Integer index=(Integer) v.getTag();
				setCurrentMag(index);
			}
		});
		TextView titleTv=(TextView)view.findViewById(R.id.title);
		titleTv.setText(mag.getTitle());
		titleTv.setTextColor(Color.BLACK);
		
		TextView priceTv=(TextView)view.findViewById(R.id.price);
		priceTv.setText("售价:"+mag.getIosprice());
		priceTv.setTextColor(Color.BLACK);
		
		final Button readBtn=(Button)view.findViewById(R.id.reading);
		
		final Button previewBtn=(Button)view.findViewById(R.id.preview);
		
		previewBtn.setTag(index);
		previewBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Integer index=(Integer) v.getTag();
				setCurrentMag(index);
			}
		});
		Button downBtn=(Button)view.findViewById(R.id.download);
		final ProgressBar progressBar=(ProgressBar)view.findViewById(R.id.progress);
		android.widget.LinearLayout.LayoutParams progressBarParams=(android.widget.LinearLayout.LayoutParams) progressBar.getLayoutParams();
		int[] frames=FrameUtil.autoAdjust(new int[]{100,-100}, this);
		progressBarParams.width=frames[0];
		final TextView unzipTv=(TextView)view.findViewById(R.id.unzip);
		unzipTv.setTextColor(Color.BLACK);
		
		readBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openMagaine(String.valueOf(mag.getId()),mag.getTitle(),0);
			}
		});
		downBtn.setTag(index);
		downBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(AppConfigUtil.curDownloader!=null||AppConfigUtil.servicerunning){
					Toast.makeText(BookshelfActivity.this, "当前有杂志正在下载，请耐心等待...", Toast.LENGTH_SHORT).show();
				}else{
					Integer index=(Integer) v.getTag();
					downloadMag(index);
				}
			}
		});
		
		int status=mag.getStatus();
		if(status==4){
			progressBar.setVisibility(View.GONE);
			downBtn.setVisibility(View.GONE);
			readBtn.setVisibility(View.VISIBLE);
			previewBtn.setVisibility(View.VISIBLE);
			unzipTv.setVisibility(View.GONE);
		}else{
			if(fromMagazineActivity){
				if(status==1){
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setIndeterminate(false);
					downBtn.setVisibility(View.GONE);
					readBtn.setVisibility(View.GONE);
					previewBtn.setVisibility(View.GONE);
					unzipTv.setVisibility(View.VISIBLE);
					unzipTv.setText("下载...");
				}else if(status==2){
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setIndeterminate(true);
					downBtn.setVisibility(View.GONE);
					readBtn.setVisibility(View.GONE);
					previewBtn.setVisibility(View.GONE);
					unzipTv.setVisibility(View.VISIBLE);
					unzipTv.setText("解压...");
				}else if(status==3){
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setIndeterminate(true);
					downBtn.setVisibility(View.GONE);
					readBtn.setVisibility(View.GONE);
					previewBtn.setVisibility(View.GONE);
					unzipTv.setVisibility(View.VISIBLE);
					unzipTv.setText("生成缩略图...");
				}else if(status==0){
					progressBar.setVisibility(View.GONE);
					downBtn.setVisibility(View.VISIBLE);
					readBtn.setVisibility(View.GONE);
					previewBtn.setVisibility(View.VISIBLE);
					unzipTv.setVisibility(View.GONE);
				}
			}else{
				progressBar.setVisibility(View.GONE);
				downBtn.setVisibility(View.VISIBLE);
				readBtn.setVisibility(View.GONE);
				previewBtn.setVisibility(View.VISIBLE);
				unzipTv.setVisibility(View.GONE);
			}
		}
		return view;
	}
	
	private void downloadMag(final int magIndex) {
		boolean isConnected=((ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo().isConnected();
		if(!isConnected){
			Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
			return;
		}  
		LinearLayout layout=(LinearLayout)findViewById(R.id.scroll_layout);
		LinearLayout itemLayout=(LinearLayout) layout.getChildAt(magIndex);
		
		ProgressBar progressBar=(ProgressBar) itemLayout.findViewById(R.id.progress);
		Button downloadBtn=(Button)itemLayout.findViewById(R.id.download);
		Button readingBtn=(Button)itemLayout.findViewById(R.id.reading);
		Button previewBtn=(Button)itemLayout.findViewById(R.id.preview);
		TextView unzipTv=(TextView)itemLayout.findViewById(R.id.unzip);
		
		Magazineinfo mag=magList.get(magIndex);
		int status=mag.getStatus();
		switch(status){
		case 0:
		case 1:
			progressBar.setVisibility(View.VISIBLE);
			downloadBtn.setVisibility(View.GONE);
			readingBtn.setVisibility(View.GONE);
			previewBtn.setVisibility(View.GONE);
			unzipTv.setVisibility(View.VISIBLE);
			unzipTv.setText("下载...");
			progressBar.setIndeterminate(false);
			if(BookshelfActivity.this.curMagIndex==magIndex){
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.GONE);
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.VISIBLE);
				rightProgressBar.setIndeterminate(false);
			}
			break;
		case 2:
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setIndeterminate(true);
			downloadBtn.setVisibility(View.GONE);
			readingBtn.setVisibility(View.GONE);
			previewBtn.setVisibility(View.GONE);
			unzipTv.setVisibility(View.VISIBLE);
			unzipTv.setText("解压...");
			if(BookshelfActivity.this.curMagIndex==magIndex){
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.GONE);
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.VISIBLE);
				rightProgressBar.setIndeterminate(true);
			}
			break;
		case 3:
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setIndeterminate(true);
			downloadBtn.setVisibility(View.GONE);
			readingBtn.setVisibility(View.GONE);
			previewBtn.setVisibility(View.GONE);
			unzipTv.setVisibility(View.VISIBLE);
			unzipTv.setText("生成缩略图...");
			if(BookshelfActivity.this.curMagIndex==magIndex){
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.GONE);
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.VISIBLE);
				rightProgressBar.setIndeterminate(true);
			}
			break;
		}
		
		String magid=mag.getId();
		String zipUrl=mag.getZip_url();
		Intent intent=new Intent(BookshelfActivity.this,DownloadService.class);
		intent.putExtra("position", magIndex);
		intent.putExtra("zipUrl", zipUrl);
		intent.putExtra("magid", magid);
		intent.putExtra("status", status);
		intent.putExtra("iosprice", mag.getIosprice());
		intent.putExtra("title", mag.getTitle());
		startService(intent);
	}
	
	public void update(int code,int progress,String desc,int position,String error){
		LinearLayout layout=(LinearLayout)findViewById(R.id.scroll_layout);
		LinearLayout itemLayout=(LinearLayout) layout.getChildAt(position);
		if(itemLayout==null){
			return;
		}
		ProgressBar progressBar=(ProgressBar) itemLayout.findViewById(R.id.progress);
		TextView unzipTv=(TextView)itemLayout.findViewById(R.id.unzip);
		Button readingBtn=(Button)itemLayout.findViewById(R.id.reading);
		Button previewBtn=(Button)itemLayout.findViewById(R.id.preview);
		Button downloadBtn=(Button)itemLayout.findViewById(R.id.download);
		Magazineinfo mag=magList.get(position);
		switch(code){
		case 0:
			mag.setStatus(1);
			progressBar.setVisibility(View.VISIBLE);
			downloadBtn.setVisibility(View.GONE);
			readingBtn.setVisibility(View.GONE);
			previewBtn.setVisibility(View.GONE);
			unzipTv.setVisibility(View.VISIBLE);
			progressBar.setIndeterminate(false);
			if(BookshelfActivity.this.curMagIndex==position){
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.GONE);
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.VISIBLE);
				rightProgressBar.setIndeterminate(false);
				BookshelfActivity.this.findViewById(R.id.rightTextView).setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			mag.setStatus(1);
			progressBar.setVisibility(View.VISIBLE);
			downloadBtn.setVisibility(View.GONE);
			readingBtn.setVisibility(View.GONE);
			previewBtn.setVisibility(View.GONE);
			unzipTv.setVisibility(View.VISIBLE);
			progressBar.setIndeterminate(false);
			progressBar.setProgress(progress);
			unzipTv.setText(desc);
			if(BookshelfActivity.this.curMagIndex==position){
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.GONE);
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.VISIBLE);
				rightProgressBar.setIndeterminate(false);
				rightProgressBar.setProgress(progress);
				TextView tv=(TextView) BookshelfActivity.this.findViewById(R.id.rightTextView);
				tv.setVisibility(View.VISIBLE);
				tv.setText(desc);
			}
			break;
		case 2:
			mag.setStatus(2);
			progressBar.setVisibility(View.VISIBLE);
			downloadBtn.setVisibility(View.GONE);
			readingBtn.setVisibility(View.GONE);
			previewBtn.setVisibility(View.GONE);
			unzipTv.setVisibility(View.VISIBLE);
			progressBar.setIndeterminate(true);
			unzipTv.setText("解压...");
			if(BookshelfActivity.this.curMagIndex==position){
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.GONE);
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.VISIBLE);
				rightProgressBar.setIndeterminate(true);
				TextView tv=(TextView) BookshelfActivity.this.findViewById(R.id.rightTextView);
				tv.setVisibility(View.VISIBLE);
				tv.setText("解压...");
			}
			break;
		case 3:
			mag.setStatus(3);
			progressBar.setVisibility(View.VISIBLE);
			downloadBtn.setVisibility(View.GONE);
			readingBtn.setVisibility(View.GONE);
			previewBtn.setVisibility(View.GONE);
			unzipTv.setVisibility(View.VISIBLE);
			progressBar.setIndeterminate(true);
			unzipTv.setText("生成缩略图...");
			if(BookshelfActivity.this.curMagIndex==position){
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.GONE);
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.VISIBLE);
				rightProgressBar.setIndeterminate(true);
				TextView tv=(TextView) BookshelfActivity.this.findViewById(R.id.rightTextView);
				tv.setVisibility(View.VISIBLE);
				tv.setText("生成缩略图...");
			}
			break;
		case 4:
			mag.setStatus(4);
			GerenInfo info=new GerenInfo();
			info.setCover(AppConfigUtil.getAppExtDir()+File.separator+"covers"+File.separator+mag.getId()+File.separator+"cover");
			info.setMagId(mag.getId());
			info.setPrice(mag.getIosprice());
			info.setTitle(mag.getTitle());
			List<GerenInfo> list=gerenAdapter.getList();
			list.add(info);
			gerenAdapter.notifyDataSetChanged();
			
			downloadBtn.setVisibility(View.GONE);
			unzipTv.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			readingBtn.setVisibility(View.VISIBLE);
			previewBtn.setVisibility(View.VISIBLE);
			if(BookshelfActivity.this.curMagIndex==position){
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.VISIBLE);
				BookshelfActivity.this.findViewById(R.id.rightTextView).setVisibility(View.GONE);
			}
			break;
		case 5:
			downloadBtn.setVisibility(View.VISIBLE);
			unzipTv.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			readingBtn.setVisibility(View.GONE);
			previewBtn.setVisibility(View.VISIBLE);
			if(BookshelfActivity.this.curMagIndex==position){
				ProgressBar rightProgressBar=(ProgressBar) BookshelfActivity.this.findViewById(R.id.rightProgress);
				rightProgressBar.setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightDownloadBtn).setVisibility(View.VISIBLE);
				BookshelfActivity.this.findViewById(R.id.rightReadBtn).setVisibility(View.GONE);
				BookshelfActivity.this.findViewById(R.id.rightTextView).setVisibility(View.GONE);
			}
			mag.setStatus(0);
			Toast.makeText(BookshelfActivity.this, error, Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void setCurrentMag(int curMag){
		Magazineinfo info=magList.get(curMag);
		String magid=info.getId();
		Button rightDownloadBtn=(Button)findViewById(R.id.rightDownloadBtn);
		Button rightReadBtn=(Button)findViewById(R.id.rightReadBtn);
		ProgressBar rightProgressBar=(ProgressBar)findViewById(R.id.rightProgress);
		TextView rightTextView=(TextView)findViewById(R.id.rightTextView);
		
		LinearLayout layout2=(LinearLayout) scroll_layout.getChildAt(curMag);
		if(layout2.findViewById(R.id.download).getVisibility()==View.VISIBLE){
			rightDownloadBtn.setVisibility(View.VISIBLE);
		}else{
			rightDownloadBtn.setVisibility(View.GONE);
		}
		if(layout2.findViewById(R.id.reading).getVisibility()==View.VISIBLE){
			rightReadBtn.setVisibility(View.VISIBLE);
		}else{
			rightReadBtn.setVisibility(View.GONE);
		}
		if(layout2.findViewById(R.id.progress).getVisibility()==View.VISIBLE){
			rightProgressBar.setVisibility(View.VISIBLE);
			ProgressBar pbBar=(ProgressBar)layout2.findViewById(R.id.progress);
			if(pbBar.isIndeterminate()){
				rightProgressBar.setIndeterminate(true);
			}else{
				rightProgressBar.setIndeterminate(false);
			}
		}else{
			rightProgressBar.setVisibility(View.GONE);
		}
		TextView unzipTv=(TextView)layout2.findViewById(R.id.unzip);
		if(unzipTv.getVisibility()==View.VISIBLE){
			rightTextView.setVisibility(View.VISIBLE);
			rightTextView.setText(unzipTv.getText());
		}else{
			rightTextView.setVisibility(View.GONE);
		}
		
		curMagIndex=curMag;
		String title=info.getTitle();
		String price=info.getIosprice();
		String coverPath=AppConfigUtil.getCoverImgPath(String.valueOf(magid));
		Bitmap coverBm=ImageUtil.loadImage(coverPath);
		ImageView coverImg=(ImageView)findViewById(R.id.cover);
		ImageUtil.recycle(coverImg);
		coverImg.setImageBitmap(coverBm);
		
		rightDownloadBtn.setTag(curMagIndex);
		
		TextView titleTv=(TextView)findViewById(R.id.cur_title);
		titleTv.setText(title);
		
		TextView priceTv=(TextView)findViewById(R.id.cur_price);
		priceTv.setText("售价:"+price);
		
		String desc=info.getDescription();
		TextView descTv=(TextView)findViewById(R.id.description);
		descTv.setText(desc);
		
		
		LinearLayout layout=(LinearLayout)findViewById(R.id.previews);
		int childCount=layout.getChildCount();
		for(int i=0;i<childCount;i++){
			ImageView imgView=(ImageView)layout.getChildAt(i);
			ImageUtil.recycle(imgView);
		}
		layout.removeAllViews();
		List<String> preimgs=info.getPreview_image();
		if(preimgs.size()>0){
			File previewDir=new File(AppConfigUtil.getPreviewsPath()+File.separator+magid);
			if(previewDir.exists()){
				File[] files=previewDir.listFiles();
				for(File file:files){
					ImageView previewImg = createPreviewImg(file.getAbsolutePath());
					layout.addView(previewImg);
				}
			}else{
				if(AppConfigUtil.curDownloader==null){
					previewDir.mkdirs();
					PreviewDownloadAsync async=new PreviewDownloadAsync(layout, previewDir, info,null);
					async.execute();
				}else{
					DownloadService service=(DownloadService)AppConfigUtil.curDownloader.getContext();
					Intent intent=new Intent(BookshelfActivity.this,DownloadService.class);
					intent.putExtra("position", service.getPosition());
					intent.putExtra("zipUrl", service.getZipUrl());
					intent.putExtra("magid", service.getMagid());
					intent.putExtra("status", service.getStatus());
					intent.putExtra("iosprice", service.getIosprice());
					intent.putExtra("title", service.getTitle());
					AppConfigUtil.curDownloader.cancel(true);
					PreviewDownloadAsync async=new PreviewDownloadAsync(layout, previewDir, info,intent);
					async.execute();
				}
			}
		}
		
		
	}
	
	class PreviewDownloadAsync extends AsyncTask<Void, Integer, List<Map<String,Object>>>{
		private ProgressDialog dialog;
		private int downloadCount;
		private Stack<FileDownloader> stack=new Stack<FileDownloader>();
		private LinearLayout layout;
		private Magazineinfo info;
		private File previewDir;
		private Intent intent;
		public PreviewDownloadAsync(LinearLayout layout,File previewDir,Magazineinfo info,Intent intent){
			this.layout=layout;
			this.previewDir=previewDir;
			this.info=info;
			this.intent=intent;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog=new ProgressDialog(BookshelfActivity.this);
			dialog.setMessage("下载预览图...");
			dialog.setCancelable(false);
			dialog.show();
		}
		@Override
		protected List<Map<String,Object>> doInBackground(Void... params) {
			List<String> preimgs=info.getPreview_image();
			List<Map<String,Object>> imgList=new ArrayList<Map<String,Object>>();
			for(int i=0;i<preimgs.size();i++){
				File file=new File(previewDir,"preview"+i);
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("file", file);
				map.put("url", preimgs.get(i));
				imgList.add(map);
				FileDownloader downloader=new FileDownloader(BookshelfActivity.this, preimgs.get(i), file.getAbsolutePath(), 1);
				stack.push(downloader);
			}
			while(!stack.isEmpty()){
				if(downloadCount<3){
					new Thread(new Runnable() {
						@Override
						public void run() {
							if(!stack.isEmpty()){
								downloadCount++;
								FileDownloader downloader=stack.pop();
								downloader.download();
								downloadCount--;
							}
						}
					}).start();
				}else{
					try {
						Thread.sleep(900);
					} catch (InterruptedException e) {
					}
				}
			}
			while(downloadCount>0){
				try {
					Thread.sleep(900);
				} catch (InterruptedException e) {
				}
			}
			return imgList;
		}
		@Override
		protected void onPostExecute(List<Map<String,Object>> result) {
			super.onPostExecute(result);
			dialog.dismiss();
			for(Map<String,Object> map:result){
				File file=(File)map.get("file");
				ImageView previewImg = createPreviewImg(file.getAbsolutePath());
				layout.addView(previewImg);
			}
			if(intent!=null){
				startService(intent);
			}
		}
	}

	private ImageView createPreviewImg(String previewPath) {
		Bitmap previewBm=ImageUtil.loadImage(previewPath);
		ImageView previewImg=new ImageView(this);
		previewImg.setImageBitmap(previewBm);
		int[] imgFrames=FrameUtil.autoAdjust(new int[]{260,195}, this);
		android.widget.LinearLayout.LayoutParams imgparams=new android.widget.LinearLayout.LayoutParams(imgFrames[0],imgFrames[1]);
		imgparams.setMargins(20, 0, 20, 0);
		previewImg.setLayoutParams(imgparams);
		return previewImg;
	}
	
	public void rightDownload(View v){
			if(AppConfigUtil.curDownloader!=null||AppConfigUtil.servicerunning){
				Toast.makeText(BookshelfActivity.this, "当前有杂志正在下载，请耐心等待...", Toast.LENGTH_SHORT).show();
				return;
			}
			Button rightDownloadBtn=(Button)findViewById(R.id.rightDownloadBtn);
			rightDownloadBtn.setVisibility(View.GONE);
			Button rightReadBtn=(Button)findViewById(R.id.rightReadBtn);
			rightReadBtn.setVisibility(View.GONE);
			ProgressBar rightProgressBar=(ProgressBar)findViewById(R.id.rightProgress);
			rightProgressBar.setVisibility(View.VISIBLE);
			Integer magIndex=(Integer) v.getTag();
			downloadMag(magIndex);
			
	}

	public void listClick(View v){
		layoutInvisible();
		setImageDrawable();
		ImageButton btn=(ImageButton)v;
		btn.setImageResource(R.drawable.new_btn_list_on);
		findViewById(R.id.listLinearLayout).setVisibility(View.VISIBLE);
	}
	
	private void layoutInvisible(){
		findViewById(R.id.listLinearLayout).setVisibility(View.GONE);
		findViewById(R.id.gerenFrameLayout).setVisibility(View.GONE);
		findViewById(R.id.shuqianGrid).setVisibility(View.GONE);
		findViewById(R.id.banquanImg).setVisibility(View.GONE);
	}
	
	private void setImageDrawable(){
		ImageButton delImgBtn=(ImageButton)findViewById(R.id.btn_del);
		delImgBtn.setImageResource(R.drawable.btn_delete_off);
		
		ImageButton listImgBtn=(ImageButton)findViewById(R.id.list);
		listImgBtn.setImageResource(R.drawable.new_btn_list_off);
		
		ImageButton dingyueImgBtn=(ImageButton)findViewById(R.id.btn_dingyue);
		dingyueImgBtn.setImageResource(R.drawable.btn_dingyue_off);
		
		ImageButton gerenImgBtn=(ImageButton)findViewById(R.id.btn_geren);
		gerenImgBtn.setImageResource(R.drawable.new_btn_geren_off);
		
		ImageButton banquanImgBtn=(ImageButton)findViewById(R.id.btn_banquan);
		banquanImgBtn.setImageResource(R.drawable.new_btn_banquan_off);
	}
	
	public void gerenClick(View v){
		layoutInvisible();
		setImageDrawable();
		ImageButton btn=(ImageButton)v;
		btn.setImageResource(R.drawable.new_btn_geren_on);
		findViewById(R.id.gerenFrameLayout).setVisibility(View.VISIBLE);
	}

	public void banquanClick(View v){
		layoutInvisible();
		setImageDrawable();
		ImageButton btn=(ImageButton)v;
		btn.setImageResource(R.drawable.new_btn_banquan_on);
		findViewById(R.id.banquanImg).setVisibility(View.VISIBLE);
	}

	public void shuqianClick(View v){
		layoutInvisible();
		setImageDrawable();
		ImageButton btn=(ImageButton)v;
		btn.setImageResource(R.drawable.btn_shuqian_on);
		findViewById(R.id.shuqianGrid).setVisibility(View.VISIBLE);
	}

	public void qrClick(View v){
		setImageDrawable();
		ImageButton btn=(ImageButton)v;
		btn.setImageResource(R.drawable.btn_qr_on);
	}

	public void fenxiangClick(View v){
		setImageDrawable();
		ImageButton btn=(ImageButton)v;
		btn.setImageResource(R.drawable.btn_fenxiang_on);
	}

	public void dingyueClick(View v){
		setImageDrawable();
		ImageButton btn=(ImageButton)v;
		btn.setImageResource(R.drawable.btn_dingyue_on);
	}
	public void delClick(View v){
		setImageDrawable();
		ImageButton btn=(ImageButton)v;
		btn.setImageResource(R.drawable.btn_delete_on);
	}

	public void openMagaine(final String magId,final String magName,final int index){
		new AsyncTask<Void, Integer, String>(){
			ProgressDialog dialog;
			Magazine magazine;
			@Override
			protected void onPreExecute() {
				dialog=new ProgressDialog(BookshelfActivity.this);
				dialog.setCancelable(false);
				dialog.setMessage("加载资源...");
				dialog.show();
			};
			@Override
			protected String doInBackground(Void... params) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
				}
				MagazineReader magareader = new MagazineReader();
				String path = AppConfigUtil.getAppContent(magId);
				File content = new File(path);
				if (!content.exists()) {
					return "未找到content.xml文件";
				}
				try {
					InputStream stream = new FileInputStream(content);
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser parser = factory.newSAXParser();
					XMLReader reader = parser.getXMLReader();
					reader.setContentHandler(magareader.getRootElement().getContentHandler());
					reader.parse(new InputSource(stream));
					magazine=magareader.getMagazine();
					magazine.rebuild();
				} catch (FileNotFoundException e) {
					return "未找到"+content+"文件";
				} catch (ParserConfigurationException e) {
					return e.getMessage();
				} catch (SAXException e) {
					return e.getMessage();
				} catch (IOException e) {
					return e.getMessage();
				}
				return null;
			}
			@Override
			protected void onPostExecute(String result) {
				if(result==null){
					AppConfigUtil.MAGAZINE_ID=magId;
					AppConfigUtil.MAGAZINE_TITLE=magName;
					collectResource();
					MagazineActivity.excute(BookshelfActivity.this, objs, index,magazine);
					dialog.dismiss();
				}else{
					dialog.dismiss();
					Toast.makeText(BookshelfActivity.this, result, Toast.LENGTH_SHORT).show();
				}
			};
			
		}.execute();
	}
	
	private void collectResource(){
		ImageView coverImg=(ImageView) findViewById(R.id.cover);
		//ImageUtil.recycle(coverImg);
		objs.add(objs);
		
		LinearLayout preLayout=(LinearLayout)findViewById(R.id.previews);
		objs.add(preLayout);
		/*int childCount=preLayout.getChildCount();
		for(int i=0;i<childCount;i++){
			ImageView imgView=(ImageView)preLayout.getChildAt(i);
			ImageUtil.recycle(imgView);
		}
		preLayout.removeAllViews();*/
		
		LinearLayout bottomLayout=(LinearLayout)findViewById(R.id.scroll_layout);
		objs.add(bottomLayout);
		/*childCount=bottomLayout.getChildCount();
		for(int i=0;i<childCount;i++){
			LinearLayout itemLayout=(LinearLayout)bottomLayout.getChildAt(i);
			ImageView imgView=(ImageView) itemLayout.findViewById(R.id.img);
			ImageUtil.recycle(imgView);
		}
		bottomLayout.removeAllViews();*/
		
		HorizontalScrollView bottomScroll=(HorizontalScrollView)findViewById(R.id.bottomScroll);
		objs.add(bottomScroll);
		/*Drawable  bottomDrawable=bottomScroll.getBackground();
		if(bottomDrawable!=null){
			Bitmap bottomBm=((BitmapDrawable)bottomDrawable).getBitmap();
			if(bottomBm!=null){
				bottomBm.recycle();
			}
		}
		bottomScroll.setBackgroundDrawable(null);*/
		
		
		GridView gerenGrid=(GridView)findViewById(R.id.gerenGrid);
		objs.add(gerenGrid);
		/*childCount=gerenGrid.getChildCount();
		for(int i=0;i<childCount;i++){
			LinearLayout itemLayout=(LinearLayout)gerenGrid.getChildAt(i);
			ImageView imgView=(ImageView) itemLayout.findViewById(R.id.img);
			ImageUtil.recycle(imgView);
			imgView=null;
		}*/
		
		GridView shuqianGrid=(GridView)findViewById(R.id.shuqianGrid);
		objs.add(shuqianGrid);
		/*childCount=shuqianGrid.getChildCount();
		for(int i=0;i<childCount;i++){
			LinearLayout itemLayout=(LinearLayout)shuqianGrid.getChildAt(i);
			ImageView imgView=(ImageView) itemLayout.findViewById(R.id.img);
			ImageUtil.recycle(imgView);
			imgView=null;
		}*/
		
		RelativeLayout bgLayout=(RelativeLayout)findViewById(R.id.bg);
		objs.add(bgLayout);
		/*Drawable bgDrawable=bgLayout.getBackground();
		if(bgDrawable!=null){
			Bitmap bgBm=((BitmapDrawable)bgDrawable).getBitmap();
			if(bgBm!=null){
				bgBm.recycle();
			}
		}
		bgLayout.setBackgroundDrawable(null);*/
		
		
		
		ImageView copyrightImg=(ImageView)findViewById(R.id.copyright);
		objs.add(copyrightImg);
		//ImageUtil.recycle(copyrightImg);
		
		
		LinearLayout imgLayout=(LinearLayout)findViewById(R.id.imgLayout);
		objs.add(imgLayout);
		/*int countImgLayout=imgLayout.getChildCount();
		for(int i=0;i<countImgLayout;i++){
			ImageView img=(ImageView)imgLayout.getChildAt(i);
			ImageUtil.recycle(img);
		}
		System.gc();*/
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
			tDialog.setMessage("确定要退出吗？");
			tDialog.setCancelable(false);
			tDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(AppConfigUtil.curDownloader!=null){
						AppConfigUtil.curDownloader.cancel(true);
						AppConfigUtil.curDownloader=null;
					}
					collectResource();
					ImageUtil.recycleBookshelfResource(objs);
					finish();
				}
			});
			tDialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			tDialog.show();
			
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppConfigUtil.bookshelfActivity=this;
	}
}
