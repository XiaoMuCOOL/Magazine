package com.rabbit.magazine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import com.rabbit.magazine.util.FileUtil;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class DownloadFileAsync extends AsyncTask<String, Integer, String> {
	
	private Button btn;
	private ProgressBar bar;
	private byte buffer[] = new byte[1024*10];
	private static final int REQUEST_TIMEOUT = 10 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	
	public DownloadFileAsync(Button btn,ProgressBar bar){
		this.btn=btn;
		this.bar=bar;
		this.bar.setProgress(0);
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String zipUrl=params[0];
			String id=params[1];
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			String appExtDir = AppConfigUtil.getAppExtDir();
			File file = new File(appExtDir);
			if (!file.exists()) {
				file.mkdir();
			}
			// Zip文件目录
			String path = AppConfigUtil.getAppExtDir(id + ".zip");
			File zipFile = new File(path);
			String dest = AppConfigUtil.getAppResource(id);
			if (zipFile.exists()) {
				FileUtil.unZip(path, dest);
				return null;
			}
			zipFile.createNewFile();
			HttpGet httpGet = new HttpGet(zipUrl);
			HttpResponse httpResponse = client.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			float length = entity.getContentLength();
			InputStream inputStream = entity.getContent();
			OutputStream outputStream = new FileOutputStream(zipFile);
			int len=0;
			int hasRead=0;
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
				hasRead += len;
				Float percent=(hasRead/length)*100;
				publishProgress(percent.intValue());
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			FileUtil.unZip(path, dest);
		} catch (ClientProtocolException e) {
			//Log.e("DownloadFileAsync", e.getMessage());
			//e.printStackTrace();
		} catch (IllegalStateException e) {
			//Log.e("DownloadFileAsync", e.getMessage());
			//e.printStackTrace();
		} catch (FileNotFoundException e) {
			//Log.e("DownloadFileAsync", e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			//Log.e("DownloadFileAsync", e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		bar.setVisibility(View.GONE);
		btn.setVisibility(View.VISIBLE);
		btn.setText("阅读");
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		bar.setVisibility(View.VISIBLE);
		btn.setVisibility(View.GONE);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		bar.setProgress(values[0]);
	}

	
}
