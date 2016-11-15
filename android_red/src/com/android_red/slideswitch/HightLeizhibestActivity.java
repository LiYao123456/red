package com.android_red.slideswitch;

import com.android_red.slideswitch.SlideSwitch.OnSwitchChangedListener;
import com.example.android_red.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;


public class HightLeizhibestActivity extends Activity 
{
	Button btnsave;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hightleizhibest);
    
      //按钮跳转
//        btn = (Button) findViewById(R.id.btn);
//      		
//        btn.setOnClickListener(new OnClickListener() {
//      							
//      							@Override
//      							public void onClick(View v) {
//      								// TODO Auto-generated method stub
//   
//      							}
//      						});
//      	//按钮跳转
//        btn1 = (Button) findViewById(R.id.btn1);
//    		
//        btn1.setOnClickListener(new OnClickListener() {
//    							
//    							@Override
//    							public void onClick(View v) {
//    								// TODO Auto-generated method stub
//    								Intent intent = new Intent(HightLeizhiActivity.this, HightLeizhiActivity.class);
//    								startActivity(intent);
//    							}
//    						});
//    		
//        //按钮跳转
//        btn2 = (Button) findViewById(R.id.btn2);	
//        btn2.setOnClickListener(new OnClickListener() {
//    							
//    							@Override
//    							public void onClick(View v) {
//    								// TODO Auto-generated method stub
//    								Intent intent = new Intent(HightLeizhiActivity.this, HightLeizhiActivity.class);
//    								startActivity(intent);
//    							}
//    						});
      btnsave = (Button) findViewById(R.id.save);	
      btnsave.setOnClickListener(new OnClickListener() {
  							
  							@Override
  							public void onClick(View v) {
  								// TODO Auto-generated method stub
  								Intent intent = new Intent(HightLeizhibestActivity.this, HightPaileiActivity.class);
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
