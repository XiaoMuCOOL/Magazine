package com.rabbit.magazine.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Stack;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.Magazineinfo;
import com.rabbit.magazine.R;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.download.FileDownloader;
import com.rabbit.magazine.util.FrameUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class EntranceActivity extends Activity {
	
	private static  String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=15&debugger=true";
	
	//private static  String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=12&debugger=true";
	
	//private static  String Server = "http://imag.nexdoor.cn/api/getmagazinedata.php?code=12&debugger=1";
	
	private File appDir;
	
	private File coversDir;
	
	private File previewsDir;
	
	private int downloadCount=0;
	
	private Stack<FileDownloader> stack=new Stack<FileDownloader>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entrance);
		DisplayMetrics  metrics=getResources().getDisplayMetrics();
		if(metrics.widthPixels>metrics.heightPixels){
			AppConfigUtil.WIDTHPIXELS=getResources().getDisplayMetrics().widthPixels;
			AppConfigUtil.HEIGHTPIXELS=getResources().getDisplayMetrics().heightPixels;
		}else{
			AppConfigUtil.WIDTHPIXELS=getResources().getDisplayMetrics().heightPixels;
			AppConfigUtil.HEIGHTPIXELS=getResources().getDisplayMetrics().widthPixels;
		}
		Server=getIntent().getExtras().getString("server");
		int[] frames=FrameUtil.autoAdjust(new int[]{0,150}, this);
		LinearLayout layout=(LinearLayout)findViewById(R.id.layout);
		LayoutParams  params=(LayoutParams) layout.getLayoutParams();
		params.setMargins(0, 0, 0, frames[1]);
		boolean hasSDCard=Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if(!hasSDCard){
			Toast.makeText(this, "请检查SD卡是否装载", Toast.LENGTH_LONG).show();
			return;
		}
		String appDirPath = Environment.getExternalStorageDirectory()+ File.separator+ "magazine";
		appDir=new File(appDirPath);
		if(!appDir.exists()){
			if(!appDir.mkdir()){
				Toast.makeText(this, "创建magazine目录失败", Toast.LENGTH_LONG).show();
				return;
			}
		}
		coversDir=new File(appDir,"covers");
		if(!coversDir.exists()){
			if(!coversDir.mkdir()){
				Toast.makeText(this, "创建covers目录失败", Toast.LENGTH_LONG).show();
				return;
			}
		}
		previewsDir=new File(appDir,"previews");
		if(!previewsDir.exists()){
			if(!previewsDir.mkdir()){
				Toast.makeText(this, "创建previews目录失败", Toast.LENGTH_LONG).show();
				return;
			}
		}
		boolean isConnected=((ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo().isConnected();
		if(!isConnected){
			MagazineService magService=new MagazineService(this);
			if(magService.getAllMagazines().size()==0){
				Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
				return;
			}else{
				Intent intent=new Intent(EntranceActivity.this,BookshelfActivity.class);
				startActivity(intent);
				finish();
			}
		}else{
			new DownloadMagazineData().execute();
		}
	}
	
	
	class DownloadMagazineData extends AsyncTask<Void, Integer, Boolean>{
		@Override
		protected Boolean doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = new StringBuilder();
			HttpGet get = new HttpGet(Server);
			try {
				HttpResponse response = client.execute(get);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				String jsonStr=builder.toString();
				String oldStr="preview_image\":\"\"";
				if(jsonStr.contains(oldStr)){
					String newStr="preview_image\":[]";
					jsonStr=jsonStr.replace(oldStr, newStr);
				}
				
				Gson gson=new Gson();
				List<Magazineinfo> mags = gson.fromJson(jsonStr, new TypeToken<List<Magazineinfo>>(){}.getType());
				for(int i=0;i<mags.size();i++){
					Magazineinfo info=mags.get(i);
					MagazineService magazineService=new MagazineService(EntranceActivity.this);
					magazineService.saveMagazine(info);
					String id=info.getId();
					File coverIdDir=new File(coversDir,id);
					if(!coverIdDir.exists()){
						coverIdDir.mkdir();
					}
					//下载封面
					File coverImg=new File(coverIdDir,"cover");
					if(!coverImg.exists()){
						String url=info.getCover_image();
						FileDownloader downloader=new FileDownloader(EntranceActivity.this, url, coverImg.getAbsolutePath(), 1);
						stack.push(downloader);
					}
					//下载预览图
					if(i==0){
						File prevewIdDir=new File(previewsDir,id);
						if(!prevewIdDir.exists()){
							prevewIdDir.mkdir();
						}
						List<String> preimgs=info.getPreview_image();
						for(int j=0;j<preimgs.size();j++){
							String url=preimgs.get(j);
							File previewImg=new File(prevewIdDir,"preview"+j);
							if(!previewImg.exists()){
								FileDownloader downloader=new FileDownloader(EntranceActivity.this, url, previewImg.getAbsolutePath(), 1);
								stack.push(downloader);
							}
						}
					}
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
						Thread.sleep(900);
					}
				}
				while(downloadCount>0){
					Thread.sleep(900);
				}
				return true;
			} catch (Exception e) {
				Log.e("DownloadMagazineData", e.getMessage());
				return true;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if(success){
				Intent intent=new Intent(EntranceActivity.this,BookshelfActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		RelativeLayout layout=(RelativeLayout)findViewById(R.id.relativeLayout);
		Bitmap bm=((BitmapDrawable)layout.getBackground()).getBitmap();
		layout.setBackgroundDrawable(null);
		bm.recycle();
		System.gc();
	}
}
