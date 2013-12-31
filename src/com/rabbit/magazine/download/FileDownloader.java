package com.rabbit.magazine.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.GerenInfo;
import com.rabbit.magazine.activity.BookshelfActivity;
import com.rabbit.magazine.activity.MagazineActivity;
import com.rabbit.magazine.adapter.FlipperPagerAdapter;
import com.rabbit.magazine.db.FileService;
import com.rabbit.magazine.db.MagazineService;
import com.rabbit.magazine.kernel.Animation;
import com.rabbit.magazine.kernel.Category;
import com.rabbit.magazine.kernel.Group;
import com.rabbit.magazine.kernel.Hot;
import com.rabbit.magazine.kernel.Layer;
import com.rabbit.magazine.kernel.Magazine;
import com.rabbit.magazine.kernel.Page;
import com.rabbit.magazine.kernel.Picture;
import com.rabbit.magazine.kernel.PictureSet;
import com.rabbit.magazine.kernel.Rotater;
import com.rabbit.magazine.kernel.Shutter;
import com.rabbit.magazine.kernel.Slider;
import com.rabbit.magazine.kernel.Video;
import com.rabbit.magazine.parser.MagazineReader;
import com.rabbit.magazine.service.DownloadService;
import com.rabbit.magazine.util.FileUtil;
import com.rabbit.magazine.util.FrameUtil;
import com.rabbit.magazine.util.ImageUtil;
import com.rabbit.magazine.view.FirstGroupView;
import com.rabbit.magazine.view.FlipperPageView2;
import com.rabbit.magazine.view.PageView2;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FileDownloader {
	private static final String TAG = "FileDownloader";
	private Context context;
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private FileService fileService;	
	
	private String fileName;
	
	private String fileType;
	
	private String fileId;
	
	private String fileUrl;
	
	private String absolutePath;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public int getCurretnSize() {
		return curretnSize;
	}

	public void setCurretnSize(int curretnSize) {
		this.curretnSize = curretnSize;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/* 已下载文件长度 */
	private int curretnSize = 0;
	
	/* 原始文件长度 */
	private long fileSize = 0;
	
	/* 线程数 */
	private DownloadThread[] threads;
	
	/* 本地保存文件 */
	private File saveFile;
	
	/* 缓存各线程下载的长度*/
	private Map<Integer, Long> data = new ConcurrentHashMap<Integer, Long>();
	
	/* 每条线程下载的长度 */
	private long block;
	
	/* 下载路径  */
	private String downloadUrl;
	
	private static final int REQUEST_TIMEOUT = 10 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	
	private boolean isCanceled=false;
	
	private final static byte[] _writeLock = new byte[0];
	
	public boolean isCancelled() {
		return isCanceled;
	}

	public void cancel(boolean cancel){
		for(DownloadThread thread:threads){
			thread.cancel(true);
			thread=null;
			isCanceled=cancel;
		}
	}
	
	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return threads.length;
	}
	
	/**
	 * 获取文件大小
	 * @return
	 */
	public long getFileSize() {
		return fileSize;
	}
	
	/**
	 * 累计已下载大小
	 * @param size
	 */
	protected synchronized void append(int size) {
		curretnSize += size;
	}
	
	/**
	 * 更新指定线程最后下载的位置
	 * @param threadId 线程id
	 * @param pos 最后下载的位置
	 */
	protected synchronized void update(int threadId, long pos) {
		this.data.put(threadId, pos);
		this.fileService.update(this.downloadUrl, this.data);
	}
	
	/**
	 * 构建文件下载器
	 * @param downloadUrl 下载路径
	 * @param fileSaveDir 文件保存目录
	 * @param threadNum 下载线程数
	 */
	public FileDownloader(Context context, String downloadUrl, String filePath, int threadCount) {
		try {
			this.context = context;
			this.fileUrl=downloadUrl;
			this.downloadUrl = downloadUrl;
			fileService = new FileService(this.context);
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			this.threads = new DownloadThread[threadCount];		
			HttpGet httpGet = new HttpGet(downloadUrl);
			HttpResponse httpResponse=client.execute(httpGet);
			if(httpResponse.getStatusLine().getStatusCode()==200){
				HttpEntity entity = httpResponse.getEntity();
				this.fileSize = entity.getContentLength();//根据响应获取文件大小
				if (this.fileSize <= 0) throw new RuntimeException("Unkown file size ");
						
				fileName = getFileName(filePath);//获取文件名称
				fileType=fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
				saveFile = new File(filePath);//构建保存文件
				File parent=new File(saveFile.getParent());
				if(!parent.exists()){
					parent.mkdirs();
				}
				if(!saveFile.exists()){
					saveFile.createNewFile();
				}
				absolutePath=this.saveFile.getAbsolutePath();
				Map<Integer, Long> logdata = fileService.getData(downloadUrl);//获取下载记录
				if(logdata.size()>0){//如果存在下载记录
					for(Map.Entry<Integer, Long> entry : logdata.entrySet())
						data.put(entry.getKey(), entry.getValue());//把各条线程已经下载的数据长度放入data中
				}
				if(this.data.size()==this.threads.length){//下面计算所有线程已经下载的数据长度
					for (int i = 0; i < this.threads.length; i++) {
						this.curretnSize += this.data.get(i+1);
					}
				}
				//计算每条线程下载的数据长度
				this.block = (this.fileSize % this.threads.length)==0? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
			}else{
				throw new RuntimeException("server no response ");
			}
		} catch (Exception e) {
			throw new RuntimeException("don't connection this url:"+this.fileUrl);
		}
	}
	
	/**
	 * 获取文件名
	 * @param conn
	 * @return
	 */
	private String getFileName(String filePath) {
		String filename=null;
		if(filePath.contains("\\")){
			filename = filePath.substring(filePath.lastIndexOf('\\') + 1);
		}else{
			filename= filePath.substring(filePath.lastIndexOf('/') + 1);
		}
		return filename;
	}
	
	/**
	 *  开始下载文件
	 * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	 * @return 已下载文件大小
	 * @throws Exception
	 */
	public int download(){
		RandomAccessFile randOut=null;
		try {
			randOut = new RandomAccessFile(this.saveFile, "rw");
			if(this.fileSize>0) randOut.setLength(this.fileSize);
			randOut.close();
			if(this.data.size() != this.threads.length){
				this.data.clear();
				for (int i = 0; i < this.threads.length; i++) {
					this.data.put(i+1, 0l);//初始化每条线程已经下载的数据长度为0
				}
			}
			for (int i = 0; i < this.threads.length; i++) {//开启线程进行下载
				long downLength = this.data.get(i+1);
				if(downLength < this.block && this.curretnSize<this.fileSize){//判断线程是否已经完成下载,否则继续下载	
					this.threads[i] = new DownloadThread(this, this.downloadUrl, this.saveFile, this.block, this.data.get(i+1), i+1);
					this.threads[i].setPriority(7);
					this.threads[i].start();
				}else{
					this.threads[i] = null;
				}
			}
			this.fileService.save(this.downloadUrl,this.data);
			if(context.getClass().equals(DownloadService.class)){
				DownloadService service=(DownloadService)context;
				String magid=service.getMagid();
				MagazineService magService=new MagazineService(context);
				magService.updateMagazineStatus(magid, 1);
				Intent intent = new Intent();
				intent.setAction("com_rabbit_magazine_download");
			    intent.putExtra("code", 0);
			    intent.putExtra("position", service.getPosition());
				context.sendBroadcast(intent);
			}
			boolean notFinish = true;//下载未完成
			while (notFinish) {// 循环判断所有线程是否完成下载
				if(isCanceled){
					return -1;
				}
				Thread.sleep(900);
				notFinish = false;//假定全部线程下载完成
				for (int i = 0; i < this.threads.length; i++){
					if (this.threads[i] != null && !this.threads[i].isFinish()) {//如果发现线程未完成下载
						notFinish = true;//设置标志为下载没有完成
						if(this.threads[i].getDownLength() == -1){//如果下载失败,再重新下载
							this.threads[i] = new DownloadThread(this, this.downloadUrl, this.saveFile, this.block, this.data.get(i+1), i+1);
							this.threads[i].setPriority(7);
							this.threads[i].start();
						}
					}
				}	
				if(context.getClass().equals(DownloadService.class)){
					Intent intent = new Intent();
					int cur=curretnSize/(1024*1024);
					long size=fileSize/(1024*1024);
					int progress=0;
					if(size==0){
						size=fileSize/1024;
						cur=curretnSize/1024;
						progress=(int) (cur*100/size);
						intent.putExtra("desc", cur+"/"+size+"K");
					}else{
						progress=(int) (cur*100/size);
						intent.putExtra("desc", cur+"/"+size+"M");
					}
					
					intent.setAction("com_rabbit_magazine_download");
				    intent.putExtra("code", 1);
				    intent.putExtra("progress", progress);
				    intent.putExtra("position", ((DownloadService)context).getPosition());
					context.sendBroadcast(intent);
				}
			}
			fileService.delete(this.downloadUrl);
			if(context.getClass().equals(DownloadService.class)){
				DownloadService service=(DownloadService)context;
				String magid=service.getMagid();
				MagazineService magService=new MagazineService(context);
				magService.updateMagazineStatus(magid, 2);
				
				Intent intent = new Intent();
				intent.setAction("com_rabbit_magazine_download");
			    intent.putExtra("code", 2);
			    intent.putExtra("position", service.getPosition());
				context.sendBroadcast(intent);
			}
			if(fileName.endsWith(".zip")){
				Thread t=new Thread(new Runnable(){
					@Override
					public void run() {
						String dirName=fileName.substring(0, fileName.lastIndexOf("."));
						File idDir=new File(AppConfigUtil.getAppExtDir(),dirName+File.separator+"resource");
						FileUtil.unZip(saveFile.getAbsolutePath(),idDir.getAbsolutePath());
						if(context.getClass().equals(DownloadService.class)){
							DownloadService service=(DownloadService)context;
							String magid=service.getMagid();
							MagazineService magService=new MagazineService(context);
							magService.updateMagazineStatus(magid, 3);
							
							Intent intent = new Intent();
							intent.setAction("com_rabbit_magazine_download");
						    intent.putExtra("code", 3);
						    intent.putExtra("position",service.getPosition());
							context.sendBroadcast(intent);
							
							String error=ImageUtil.createNavBar(context,magid);
							if(error==null){
								magService.updateMagazineStatus(magid, 4);
								GerenInfo info=new GerenInfo();
								info.setCover(AppConfigUtil.getAppExtDir()+File.separator+"covers"+File.separator+service.getMagid()+File.separator+"cover");
								info.setMagId(service.getMagid());
								info.setPrice(service.getIosprice());
								info.setTitle(service.getTitle());
								magService.saveGeren(info);
								
								intent = new Intent();
								intent.setAction("com_rabbit_magazine_download");
							    intent.putExtra("code", 4);
							    intent.putExtra("position", service.getPosition());
								context.sendBroadcast(intent);
							}else{
								saveFile.delete();
								FileUtil.deleteDir(idDir);
								idDir.delete();
								service.sendErrorMsg(error);
							}
						}
					}
				});
				t.start();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}finally{
			if(randOut!=null){
				try {
					randOut.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
		return this.curretnSize;
	}
	
	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
