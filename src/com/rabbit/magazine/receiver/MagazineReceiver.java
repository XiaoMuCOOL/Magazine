package com.rabbit.magazine.receiver;

import java.util.List;

import com.rabbit.magazine.AppConfigUtil;
import com.rabbit.magazine.activity.BookshelfActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MagazineReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String topActivity=getTopActivity(context);
		if(topActivity.equals(BookshelfActivity.class.getName())){
			if(AppConfigUtil.bookshelfActivity!=null){
				Bundle bundle=intent.getExtras();
				int code=bundle.getInt("code");
				int position=bundle.getInt("position");
				switch(code){
				case 0:
					AppConfigUtil.bookshelfActivity.update(code, -1, null, position, null);
					break;
				case 1:
					int progress=bundle.getInt("progress");
					String desc=bundle.getString("desc");
					AppConfigUtil.bookshelfActivity.update(code, progress, desc, position, null);
					break;
				case 2:
				case 3:
					AppConfigUtil.bookshelfActivity.update(code, -1, null, position, null);
					break;
				case 4:
					AppConfigUtil.bookshelfActivity.update(code, -1, null, position, null);
					break;
				case 5:
					String error=bundle.getString("error");
					AppConfigUtil.bookshelfActivity.update(code, -1, null, position, error);
					break;
				}
			}
		}
	}

	private String getTopActivity(Context context){
	      ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
	      List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
	      if(runningTaskInfos != null){
	        return (runningTaskInfos.get(0).topActivity).getClassName();
	      }
	      return null;
	 }
}
