package com.android_red.slideswitch;

import com.android_red.intenter.Const;
import com.android_red.intenter.Test;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.os.Bundle;

public class HightLeizhiActivity extends Activity implements
		OnSwitchChangedListener {
	Button btnsave;
	RadioGroup rgStorageWay;
	RadioButton rbyuan, rbjiao, rbfen;
	private static final String[] m = { "0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9" };
	private Spinner spinner;
	private ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.hightleizhi);

		rgStorageWay = (RadioGroup) findViewById(R.id.rgStorageWay);
		rbyuan = (RadioButton) findViewById(R.id.rbyuan);
		rbjiao = (RadioButton) findViewById(R.id.rbjiao);
		rbfen = (RadioButton) findViewById(R.id.rbfen);

		spinner = (Spinner) findViewById(R.id.Spinner01);  // 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, m);
     	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // 设置下拉列表的风格
		spinner.setAdapter(adapter);  // 将adapter 添加到spinner中
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());// 添加事件Spinner事件监听v
		
		//spinner.setVisibility(View.VISIBLE);   

		spinner.setAdapter(adapter);
        spinner.setSelection(Const.ws,true);  // 设置默认值
        
		SlideSwitch switch5 = (SlideSwitch) findViewById(R.id.slideSwitch5); // 排雷功能设置开关
		switch5.setStatus(Const.switch5);
		switch5.setOnSwitchChangedListener(this);

		if (Const.rb == 1) {
			rbyuan.setChecked(true);
		} else if (Const.rb == 2) {
			rbjiao.setChecked(true);
		} else {
			rbfen.setChecked(true);
		}

		rgStorageWay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rbyuan.getId()) {
					Const.rb = 1;
				} else if (checkedId == rbjiao.getId()) {
					Const.rb = 2;
				} else {
					Const.rb = 3;
				}
			}
		});

		btnsave = (Button) findViewById(R.id.save);
		btnsave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HightLeizhiActivity.this,
						HightPaileiActivity.class);
				startActivity(intent);

			}
		});
	}

	public native int writeToFile(String content, boolean append);

	public native String stringFromJNI();

	public native String unimplementedStringFromJNI();

	public void onSwitchChanged(SlideSwitch obj, int status) {
		// TextView tipTextView = (TextView)findViewById(R.id.textViewTip);
		switch (obj.getId()) {
		case R.id.slideSwitch5:
			// tipTextView.setText("slideSwitch1 "+ status);
			if (status == 1) {
				Const.switch5 = true;
				Toast.makeText(getApplicationContext(), "雷值设置开启！",
						Toast.LENGTH_SHORT).show();
			}
			if (status == 0) {
				Const.switch5 = false;
				Toast.makeText(getApplicationContext(), "雷值设置关闭！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		Log.e("onSwitchChanged", obj + " status=" + status);

	}

	// 使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Const.ws = Integer.parseInt(m[arg2]);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
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
