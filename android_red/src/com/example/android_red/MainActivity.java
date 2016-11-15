package com.example.android_red;


import com.android_red.slideswitch.HightMainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Button btnwhat, btnvip,btnbest;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//按钮跳转
		btnwhat = (Button) findViewById(R.id.what);
		btnwhat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, BangzhuActivity.class);
				startActivity(intent);
			}
		});
		
		
		btnvip = (Button) findViewById(R.id.vip);
		btnvip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, VipActivity.class);
				startActivity(intent);
			}
		});
		
		
		btnbest = (Button) findViewById(R.id.best);
		btnbest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, HightMainActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	// 使当前Activity返回键无效
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
