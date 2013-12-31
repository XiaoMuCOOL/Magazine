package com.rabbit.magazine.service;

import java.io.File;
import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.GerenInfo;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.download.FileDownloader;
import com.rabbit.magazine.util.FileUtil;
import com.rabbit.magazine.util.ImageUtil;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class DownloadService extends Service {
	
	private String magid;
	
	private int position;
	
	private String iosprice;
	
	private String title;
	
	private String zipUrl;
	
	public String getZipUrl() {
		return zipUrl;
	}

	public void setZipUrl(String zipUrl) {
		this.zipUrl = zipUrl;
	}

	private int status;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(intent==null||intent.getExtras()==null){
			return;
		}
		position=intent.getExtras().getInt("position");
		magid=intent.getExtras().getString("magid");
		zipUrl=intent.getExtras().getString("zipUrl");
		status=intent.getExtras().getInt("status");
		iosprice=intent.getExtras().getString("iosprice");
		title=intent.getExtras().getString("title");
		if(status==0||status==1){
			new Thread(new Runnable(){
				@Override
				public void run() {
					File zipFile=new File(AppConfigUtil.getAppExtDir()+File.separator+magid+".zip");
					FileDownloader downloader=new FileDownloader(DownloadService.this, zipUrl, zipFile.getAbsolutePath(), 3);
					AppConfigUtil.curDownloader=downloader;
					AppConfigUtil.servicerunning=true;
					downloader.download();
					AppConfigUtil.curDownloader=null;
					AppConfigUtil.servicerunning=false;
				}
			}).start();
		}else if(status==2){
			unziping();
		}else if(status==3){
			navbar();
		}
	}

	private void navbar() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				AppConfigUtil.servicerunning=true;
				
				
				Intent intent = new Intent();
				intent.setAction("com_rabbit_magazine_download");
			    intent.putExtra("code", 3);
			    intent.putExtra("position", position);
				sendBroadcast(intent);
				String error=ImageUtil.createNavBar(DownloadService.this,magid);
				if(error==null){
					MagazineService magService=new MagazineService(DownloadService.this);
					magService.updateMagazineStatus(magid, 4);
					GerenInfo info=new GerenInfo();
					info.setCover(AppConfigUtil.getAppExtDir()+File.separator+"covers"+File.separator+magid+File.separator+"cover");
					info.setMagId(magid);
					info.setPrice(iosprice);
					info.setTitle(title);
					magService.saveGeren(info);
					
					intent = new Intent();
					intent.setAction("com_rabbit_magazine_download");
				    intent.putExtra("code", 4);
				    intent.putExtra("position", position);
					sendBroadcast(intent);
				}else{
					sendErrorMsg(error);
				}
				
				AppConfigUtil.servicerunning=false;
			}
		}).start();
	}

	private void unziping() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				File idDir=new File(AppConfigUtil.getAppExtDir(magid));
				if(!idDir.exists()){
					idDir.mkdir();
				}
				File resourceDir=new File(AppConfigUtil.getAppResource(magid));
				if(resourceDir.exists()){
					FileUtil.deleteDir(resourceDir);
				}
				resourceDir.mkdir();
				File zip=new File(AppConfigUtil.getAppExtDir(),magid+".zip");
				if(!zip.exists()){
					Toast.makeText(DownloadService.this, zip+"不存在", Toast.LENGTH_SHORT).show();
					return;
				}
				
				AppConfigUtil.servicerunning=true;
				
				Intent intent = new Intent();
				intent.setAction("com_rabbit_magazine_download");
			    intent.putExtra("code", 2);
			    intent.putExtra("position",position);
				sendBroadcast(intent);
				
				FileUtil.unZip(zip.getAbsolutePath(),resourceDir.getAbsolutePath());
				MagazineService magService=new MagazineService(DownloadService.this);
				magService.updateMagazineStatus(magid, 3);
				
				intent = new Intent();
				intent.setAction("com_rabbit_magazine_download");
			    intent.putExtra("code", 3);
			    intent.putExtra("position",position);
				sendBroadcast(intent);
				
				String error=ImageUtil.createNavBar(DownloadService.this,magid);
				if(error==null){
					magService.updateMagazineStatus(magid, 4);
					GerenInfo info=new GerenInfo();
					info.setCover(AppConfigUtil.getAppExtDir()+File.separator+"covers"+File.separator+magid+File.separator+"cover");
					info.setMagId(magid);
					info.setPrice(iosprice);
					info.setTitle(title);
					magService.saveGeren(info);
					
					intent = new Intent();
					intent.setAction("com_rabbit_magazine_download");
				    intent.putExtra("code", 4);
				    intent.putExtra("position", position);
					sendBroadcast(intent);
				}else{
					FileUtil.deleteDir(idDir);
					zip.delete();
					sendErrorMsg(error);
				}
				
				AppConfigUtil.servicerunning=false;
			}
		}).start();
	}
	
	public void sendErrorMsg(String str){
		MagazineService magService=new MagazineService(this);
		magService.updateMagazineStatus(magid, 0);
		Intent intent = new Intent();
		intent.setAction("com_rabbit_magazine_download");
	    intent.putExtra("code", 5);
	    intent.putExtra("error", str);
	    intent.putExtra("position", position);
		sendBroadcast(intent);
	}

	public String getMagid() {
		return magid;
	}

	public void setMagid(String magid) {
		this.magid = magid;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getIosprice() {
		return iosprice;
	}

	public void setIosprice(String iosprice) {
		this.iosprice = iosprice;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
