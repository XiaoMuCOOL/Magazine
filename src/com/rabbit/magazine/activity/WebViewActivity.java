package com.rabbit.magazine.activity;

import com.rabbit.magazine.R;
import com.rabbit.magazine.R.id;
import com.rabbit.magazine.R.layout;
import com.rabbit.magazine.R.string;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebViewActivity extends Activity {

	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		Button back=(Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(webview.canGoBack()){
					webview.goBack();
				}
			}
		});
		Button forward=(Button) findViewById(R.id.forward);
		forward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(webview.canGoForward()){
					webview.goForward();
				}
			}
		});
		Button close=(Button) findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				webview.destroy();
				finish();
			}
		});
		String url=getIntent().getExtras().getString("url");
		webview=(WebView) findViewById(R.id.webview);
		webview.setWebChromeClient(new WebChromeClient(){             
            @Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}

			public void onProgressChanged(WebView view, int progress){               
                setTitle("Loading...");          
                setProgress(progress * 100);        
                if(progress == 100)               
                    setTitle(R.string.app_name);          
                }            
            }   
        
        );           
		webview.setWebViewClient(new WebViewClient(){
			 public boolean shouldOverrideUrlLoading(WebView view, String url) {  
                 view.loadUrl(url);  
                 return true;  
             }  
			 @Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
		webview.loadUrl(url);
	}
	
}
