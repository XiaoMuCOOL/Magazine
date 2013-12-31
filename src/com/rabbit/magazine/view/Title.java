package com.rabbit.magazine.view;

import com.rabbit.magazine.R;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class Title {
	
	private Activity activity;
	
	private WebView webview;
	
	public Title(Activity activity,WebView webview){
		this.activity=activity;
		this.webview=webview;
	}
	
	public void setTitle() {
		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		Button back=(Button) activity.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(webview.canGoBack()){
					webview.goBack();
				}
			}
		});
		Button forward=(Button)activity.findViewById(R.id.forward);
		forward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(webview.canGoForward()){
					webview.goForward();
				}
			}
		});
		Button close=(Button) activity.findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				webview.destroy();
				activity.finish();
			}
		});
	}

}

