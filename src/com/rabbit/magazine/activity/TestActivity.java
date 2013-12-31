package com.rabbit.magazine.activity;

import com.rabbit.magazine.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		Button b1=(Button)findViewById(R.id.b1);
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(TestActivity.this,EntranceActivity.class);
				intent.putExtra("server", "http://imag.nexdoor.cn/api/getmagazinedata.php?code=15&debugger=true");
				startActivity(intent);
			}
		});
		Button b2=(Button)findViewById(R.id.b2);
		b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(TestActivity.this,EntranceActivity.class);
				intent.putExtra("server", "http://imag.nexdoor.cn/api/getmagazinedata.php?code=12&debugger=true");
				startActivity(intent);
			}
		});
		
		Button b3=(Button)findViewById(R.id.b3);
		b3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(TestActivity.this,EntranceActivity.class);
				intent.putExtra("server", "http://imag.nexdoor.cn/api/getmagazinedata.php?code=12&debugger=0");
				startActivity(intent);
			}
		});
	}
	
}
