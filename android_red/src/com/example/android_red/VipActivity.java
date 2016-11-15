package com.example.android_red;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class VipActivity extends Activity {

	Button btnregister, btntyid,btnpower,btnwhat,btnfanhui;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vip);
		
		//按钮跳转
		btnregister = (Button) findViewById(R.id.register);
		btnregister.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//						Intent intent = new Intent(VipActivity.this, BangzhuActivity.class);
//						startActivity(intent);
					}
				});
				
				
		btntyid = (Button) findViewById(R.id.tyid);
		btntyid.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(VipActivity.this, IDActivity.class);
						startActivity(intent);
					}
				});
				
				
		btnpower = (Button) findViewById(R.id.power);
		btnpower.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(VipActivity.this, PowerActivity.class);
						startActivity(intent);
					}
				});
		
		btnwhat = (Button) findViewById(R.id.what);
		btnwhat.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(VipActivity.this, BangzhuActivity.class);
						startActivity(intent);
					}
				});
		btnfanhui = (Button) findViewById(R.id.fanhui);
		btnfanhui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 返回
				Intent in = getIntent();
				setResult(RESULT_OK, in);
				finish();
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
