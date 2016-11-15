package com.android_red.slideswitch;

import com.android_red.intenter.Const;
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
import android.widget.Toast;
import android.os.Bundle;


public class HightPaileiActivity extends Activity implements OnSwitchChangedListener
{
	Button btn,btn1,btn2;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hightpailei);
        SlideSwitch switch4 = (SlideSwitch)findViewById(R.id.slideSwitch4);   //排雷功能设置开关
        

        switch4.setStatus(Const.switch4);
        switch4.setOnSwitchChangedListener(this);
      //按钮跳转
        btn = (Button) findViewById(R.id.btn);
      		
        btn.setOnClickListener(new OnClickListener() {
      							
      							@Override
      							public void onClick(View v) {
      								// TODO Auto-generated method stub
   
      							}
      						});
      	//按钮跳转
        btn1 = (Button) findViewById(R.id.btn1);
    		
        btn1.setOnClickListener(new OnClickListener() {
    							
    							@Override
    							public void onClick(View v) {
    								// TODO Auto-generated method stub
    								Intent intent = new Intent(HightPaileiActivity.this, HightLeizhiActivity.class);
    								startActivity(intent);
    							}
    						});
    		
        //按钮跳转 
//        btn2 = (Button) findViewById(R.id.btn2);	
//        btn2.setOnClickListener(new OnClickListener() {
//    							
//    							@Override
//    							public void onClick(View v) {
//    								Intent intent = new Intent(HightPaileiActivity.this, HightLeizhibestActivity.class);
//    								startActivity(intent);
//    							}
//    						});
        
        Button fanhui = (Button) findViewById(R.id.fanhui);
        
        fanhui.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HightPaileiActivity.this, HightMainActivity .class);
				startActivity(intent);
			}
		});
        

    }
    
    public native int writeToFile(String content, boolean append);
    public native String  stringFromJNI();
    public native String  unimplementedStringFromJNI();

	public void onSwitchChanged(SlideSwitch obj, int status)
	{
		//TextView tipTextView = (TextView)findViewById(R.id.textViewTip);
		switch (obj.getId()) 
		{
		case R.id.slideSwitch4:
			//tipTextView.setText("slideSwitch1 "+ status);
			if(status==1){
				Const.switch4 = true;
			Toast.makeText(getApplicationContext(), "排雷功能设置开启！",
			Toast.LENGTH_SHORT).show();
			}
			if(status==0){
				Const.switch4 = false;
			Toast.makeText(getApplicationContext(), "排雷功能设置关闭！",
			Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		Log.e("onSwitchChanged", obj + " status=" + status);
		
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
