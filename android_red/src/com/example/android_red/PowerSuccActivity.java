package com.example.android_red;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.android_red.intenter.AsyncTaskManager;
import com.android_red.intenter.CustomProgressDialog;
import com.android_red.intenter.AsyncTaskSoap.OnDataRecvListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PowerSuccActivity extends Activity {

	AsyncTaskManager atm = new AsyncTaskManager(); // 异步通信管理器 （向服务器发送请求时使用）
	// Handler和AsyncTask，都是为了不阻塞主线程（UI线程），且UI的更新只能在主线程中完成，因此异步处理是不可避免
	// ProcessDlgAction pda = new ProcessDlgAction(); // 进度对话框（加载）
	CustomProgressDialog pda;
	Button btnpower, btnfanhui, btnpowersucc;
	TextView txqqlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.powersucc);
		pda =new CustomProgressDialog(this, "",R.anim.frame);   //加载动画
		pda.show();
		atm.startAsyncTask("qqlist", "imei=" + PowerActivity.imei, odrLsn, 1);
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

	OnDataRecvListener odrLsn = new OnDataRecvListener() {
		@Override
		public void onDataRecv(JSONObject jsonObj, int code) {
			pda.dismiss();
			if(null==jsonObj){   //网络不好时会出现程序崩溃的情况，获取不到ret
				Toast.makeText(PowerSuccActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
			}else{
			try {
				int ret = jsonObj.getInt("ret");
				if (0 == ret) {
					List list = new ArrayList();
					String qqlist = "\n";
					JSONArray arr = jsonObj.getJSONArray("list");
					for(int i=0;i<arr.length();i++){
						list.add(arr.get(i));
						qqlist = qqlist+"已经授权qq:"+arr.get(i).toString()+"\n";
					}
					txqqlist = (TextView) findViewById(R.id.qqlist);
					txqqlist.setText(qqlist);
				}else{
					Toast.makeText(PowerSuccActivity.this, "当前手机没有经过授权！", Toast.LENGTH_LONG)
					.show();
					Intent intent = new Intent(PowerSuccActivity.this,
							PowerActivity.class);
					startActivity(intent);
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "服务器连接异常！",
						Toast.LENGTH_LONG).show();
			  }
			}
		}
	};
	
	// 使当前Activity返回键无效
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
