package com.rabbit.magazine.download;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.util.Log;

/**
 * 下载线程类
 * @author Administrator
 *
 */
public class DownloadThread extends Thread {
	private static final String TAG = "DownloadThread";
	private File saveFile;
	private String downUrl;
	private long block;
	private boolean isCancel=false;
	
	private static final int REQUEST_TIMEOUT = 10 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	
	public void cancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	/* 下载开始位置  */
	private int threadId = -1;	
	private long downLength;
	private boolean finish = false;
	private FileDownloader downloader;
	
	/**
	 * @param downloader:下载器
	 * @param downUrl:下载地址
	 * @param saveFile:下载路径
	 * 
	 */
	public DownloadThread(FileDownloader downloader, String downUrl, File saveFile, long block, long downLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
	}
	
	@Override
	public void run() {
		if(downLength < block&&!isCancel){//未下载完成
			InputStream inStream=null;
			RandomAccessFile threadfile=null;
				try {
					//使用Get方式下载
					BasicHttpParams httpParams = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
					HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
					HttpClient client = new DefaultHttpClient(httpParams);
					HttpGet httpGet = new HttpGet(this.downUrl);
					long startPos = block * (threadId - 1) + downLength;//开始位置
					long endPos = block * threadId -1;//结束位置
					httpGet.setHeader("Range", "bytes=" + startPos + "-"+ endPos);//设置获取实体数据的范围
					HttpResponse httpResponse = client.execute(httpGet);
					inStream=httpResponse.getEntity().getContent();
					byte[] buffer = new byte[1024*1024];
					int offset = 0;
					threadfile = new RandomAccessFile(this.saveFile, "rwd");
					threadfile.seek(startPos);
					
					while ((!isCancel)&&(offset = inStream.read(buffer, 0, 1024*1024)) != -1) {
						threadfile.write(buffer, 0, offset);
						downLength += offset;
						downloader.update(this.threadId, downLength);
						downloader.append(offset);
					}
					if(!isCancel){
						this.finish = true;
					}
				} catch (ProtocolException e) {
					this.downLength = -1;
				} catch (FileNotFoundException e) {
					this.downLength = -1;
				} catch (IOException e) {
					this.downLength = -1;
				}finally{
					if(threadfile!=null){
						try {
							threadfile.close();
						} catch (IOException e) {
							Log.e(TAG, e.getMessage());
						}
					}
					if(inStream!=null){
						try {
							inStream.close();
						} catch (IOException e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}
		}
	}
	
	/**
	 * 下载是否完成
	 * @return
	 */
	public boolean isFinish() {
		return finish;
	}
	
	/**
	 * 已经下载的内容大小
	 * @return 如果返回值为-1,代表下载失败
	 */
	public long getDownLength() {
		return downLength;
	}
}
