package com.android_red.slideswitch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.android_red.intenter.Const;
import com.android_red.intenter.Test;
import com.android_red.slideswitch.MyDoubleSeekBar.OnRangeSeekBarChangeListener;
import com.android_red.slideswitch.SlideSwitch.OnSwitchChangedListener;
import com.example.android_red.MainActivity;
import com.example.android_red.R;
import com.test.service.LocalService;
import com.test.service.LocalService.LocalBinder;
import com.test.threads.ListenThread;
import com.test.threads.SendDataThread;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class HightMainActivity extends Activity implements
		OnSwitchChangedListener, OnClickListener {
	private static final String[] m = { "0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9" };
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	public static TextView textviewP;
	private Context mContext = null;
	private LinearLayout ll = null;
	WifiManager wifi = null;
	Button open = null;
	Button gets = null;
	Button sends = null;
	Button openget = null;
	boolean flag = false;
	LocalService myservice;
	Handler handler;
	TextView text;
	public static EditText textip;
	RadioGroup rgshezhi1,rgshezhi2;
	RadioButton rbliang, rbyi,rbmiao,rbts,rbxposed;
	String ip = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hightmain);
		SlideSwitch switch3 = (SlideSwitch) findViewById(R.id.slideSwitch3); // 基本功能设置开关
		SlideSwitch switch5 = (SlideSwitch) findViewById(R.id.slideSwitch5); // 排雷功能设置开关
		
		wifi = (WifiManager) getSystemService(WIFI_SERVICE);
		open = (Button) findViewById(R.id.openheat);
		gets = (Button) findViewById(R.id.getphone);           //获取当前连接下所有IP
		sends = (Button) findViewById(R.id.startsocket);       //测试对方IP是否填写正确
		text = (TextView) findViewById(R.id.text);             //显示本机IP
		textip = (EditText) findViewById(R.id.textip);         //填写对方IP      
		
		rgshezhi1 = (RadioGroup) findViewById(R.id.rgshezhi1); //网络连接设置
		rbliang = (RadioButton) findViewById(R.id.rbliang);    //两台手机同时连入一个wifi
		rbyi = (RadioButton) findViewById(R.id.rbyi);          //一台手机作为热点

		
		rgshezhi2 = (RadioGroup) findViewById(R.id.rgshezhi2); //抢红包方式
		rbmiao = (RadioButton) findViewById(R.id.rbmiao);      //秒抢
		rbts = (RadioButton) findViewById(R.id.rbts);          //扫描抢
		rbxposed = (RadioButton) findViewById(R.id.rbxposed);  //透视抢
		
		textviewP = (TextView) findViewById(R.id.textviewP);   //显示扫描间隔时间
		mContext = getApplicationContext();
		ll = (LinearLayout) findViewById(R.id.llLayout);  //双向滑动条
		final MyDoubleSeekBar<Integer> doubleSeekbar = new MyDoubleSeekBar<Integer>(
				0, 150, mContext);
		doubleSeekbar.setSelectedMinValue(Const.jdt_min);
		doubleSeekbar.setSelectedMaxValue(Const.jdt_max);
		doubleSeekbar
				.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
					@Override
					public void onRangeSeekBarValuesChanged(
							MyDoubleSeekBar<?> bar, Integer minValue,
							Integer maxValue) {
						// TODO 左右游标滑动监听事件
						// textviewP.setText("扫描时间间隔:"+minValue/10.0+"-"+maxValue/10.0+"秒");
					}
				});
		ll.addView(doubleSeekbar);

		spinner = (Spinner) findViewById(R.id.Spinner01); // 尾数下拉框
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 设置下拉列表的风格
		spinner.setAdapter(adapter); // 将adapter 添加到spinner中
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());// 添加事件Spinner事件监听v
		spinner.setAdapter(adapter);
		spinner.setSelection(Const.ws, true); // 设置默认值

		
		switch3.setStatus(Const.switch3);// 基本功能设置开关
		switch3.setOnSwitchChangedListener(this);
		switch5.setStatus(Const.switch5);// 排雷功能设置开关
		switch5.setOnSwitchChangedListener(this);
		findViewById(R.id.openService).setOnClickListener(this);   //打开辅助服务按钮
		
		if (Const.wifi == 1) {
			rbliang.setChecked(true);
			// 获取wifi服务
			textip.setEnabled(true);
			gets.setClickable(true);
			sends.setClickable(true);
			// 获取wifi服务
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			ip = intToIp(ipAddress);
			text.setText("本机IP：" + ip);
			textip.setText(Const.ip);
		} else {
			rbyi.setChecked(true);
			text.setText("当前是手机开热点，不需要填写IP");
			textip.setText("不需要填写IP");
			gets.setClickable(false);
			sends.setClickable(false);
			textip.setEnabled(false);
		}

		

		
		rgshezhi1.setOnCheckedChangeListener(new OnCheckedChangeListener() {    //网络连接设置
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rbliang.getId()) {
					textip.setText("");
					Toast.makeText(getApplicationContext(), "接入同一个WIFI", 3000)
							.show();
					textip.setEnabled(true);
					gets.setClickable(true);
					sends.setClickable(true);
					// 获取wifi服务
					WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					int ipAddress = wifiInfo.getIpAddress();
					ip = intToIp(ipAddress);
					text.setText("本机IP：" + ip);
					
					Const.wifi = 1;

				} else {
					Toast.makeText(getApplicationContext(), "一方开热点", 3000)
							.show();
					text.setText("当前是手机开热点，不需要填写IP");
					textip.setText("不需要填写IP");
					gets.setClickable(false);
					sends.setClickable(false);
					textip.setEnabled(false);
					Const.wifi = 2;
				} 
			}
		});

	
		if(Const.qiang == 1){
			rbmiao.setChecked(true);   //秒抢客户端
			textip.setEnabled(false);
			sends.setEnabled(false);
			spinner.setEnabled(false);
			doubleSeekbar.setEnabled(false);
		}else if(Const.qiang == 2){
			rbts.setChecked(true);     //扫描客户端
			textip.setEnabled(true);
			sends.setEnabled(true);
			spinner.setEnabled(true);
			doubleSeekbar.setEnabled(true);
		}else{
			rbxposed.setChecked(true); //透视客户端
			textip.setEnabled(true);
			sends.setEnabled(true);
			spinner.setEnabled(true);
			doubleSeekbar.setEnabled(true);
		}
		
		rgshezhi2.setOnCheckedChangeListener(new OnCheckedChangeListener() {    //抢红包方式
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rbmiao.getId()) {				
					Toast.makeText(getApplicationContext(), "秒抢客户端", 3000)
							.show();				
					Const.qiang = 1;
					textip.setEnabled(false);
					sends.setEnabled(false);
					spinner.setEnabled(false);
					doubleSeekbar.setEnabled(false);
				} else if(checkedId == rbts.getId()){
					Toast.makeText(getApplicationContext(), "扫描客户端", 3000)
					.show();
					Const.qiang = 2;
					textip.setEnabled(true);
					sends.setEnabled(true);
					spinner.setEnabled(true);
					doubleSeekbar.setEnabled(true);
				} else{
					Toast.makeText(getApplicationContext(), "透视客户端", 3000)
					.show();
					Const.qiang = 3;
					textip.setEnabled(true);
					sends.setEnabled(true);
					spinner.setEnabled(true);
					doubleSeekbar.setEnabled(true);
				}
			}
		});
		
		Button fanhui = (Button) findViewById(R.id.fanhui);    // 返回按钮
		fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Const.ip = textip.getText().toString();
				
				Test test = new Test();
//				if (Const.switch5 == false
//						|| test.AA1("60.05", 4, "19", "20") == 0) { // Const.switch5==false
//																	// 未开启排雷，直接抢
//					// 抢红包
//					System.out.println("开始抢红包");
//				} else {
//					System.out.println("此时不抢");
//					// 此时不抢
//				}
				Intent intent = new Intent(HightMainActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});


		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					Toast.makeText(HightMainActivity.this, msg.obj.toString(),
							5000).show();
					break;
				case 2:
					Toast.makeText(HightMainActivity.this, msg.obj.toString(),
							5000).show();
					break;
				default:
					break;
				}
			}

		};

		sc = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d("aaa", "绑定成功");
				LocalBinder binder = (LocalBinder) service; // 通过IBinder获取Service
				myservice = binder.getService();
				myservice.startWaitDataThread(handler);// 完成绑定后打开另外一条线程等待消息接收
			}
		};
		open.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = !flag;
				setWifiApEnabled(flag);// 开启热点
			}
		});
		gets.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {// 获取连接到消息发送的手机IP地址
				ArrayList<String> connectedIP = getConnectedIP();
				StringBuilder resultList = new StringBuilder();

				for (String ip : connectedIP) {
					resultList.append(ip);
					resultList.append("\n");
				}
				Toast.makeText(getApplicationContext(),
						"对方" + resultList.toString(), 3000).show();
			}
		});
		sends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				ArrayList<String> connectedIP = getConnectedIP();
//				for (String ip : connectedIP) {
//					if (ip.contains(".")) {
//						new SendDataThread(text.getText().toString()).start();// 消息发送方启动线程发送消息"192.168.0.101"
//					}
//				}
//				Const.cs = 1;
				Const.mess = "abc";
				if(textip.getText().toString().contains(".")){
					new SendDataThread(textip.getText().toString()).start();
				}else{
					Toast.makeText(getApplicationContext(),
							"IP地址输入错误", 3000).show();
				}
				}
		});		
		 //connection();//启动服务	
		 if(Const.ser == 1){ 
		new ListenThread(12345,handler).start();
		Const.ser++;
		 }
	}

	class SpinnerSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Const.ws = Integer.parseInt(m[arg2]);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public native int writeToFile(String content, boolean append);
	public native String stringFromJNI();
	public native String unimplementedStringFromJNI();

	@Override
	public void onSwitchChanged(SlideSwitch obj, int status) {
		switch (obj.getId()) {
		case R.id.slideSwitch3:
			if (status == 1) {
				Const.switch3 = true;
				Toast.makeText(getApplicationContext(), "基本设置开启！",
						Toast.LENGTH_SHORT).show();
			}
			if (status == 0) {
				Const.switch3 = false;
				Toast.makeText(getApplicationContext(), "基本设置关闭！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.slideSwitch5:
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

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		try {
			Intent intent = new Intent(
					android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(intent);
			Toast.makeText(this, "找到“抢红包外挂”，然后开启服务即可", Toast.LENGTH_LONG)
					.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	private ArrayList<String> getConnectedIP() {// 获取连接到本机热点上的手机ip
		ArrayList<String> connectedIP = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split(" +");
				if (splitted != null && splitted.length >= 4) {
					String ip = splitted[0];
					connectedIP.add(ip);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connectedIP;
	}

	public boolean setWifiApEnabled(boolean enabled) {
		if (enabled) { // disable WiFi in any case
			// wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
			wifi.setWifiEnabled(false);
		}
		try {
			// 热点的配置类
			WifiConfiguration apConfig = new WifiConfiguration();
			// 配置热点的名称(可以在名字后面加点随机数什么的)
			apConfig.SSID = "scorewizard";
			String password = "password";
			// 配置热点的密码
			apConfig.preSharedKey = password;
			apConfig.hiddenSSID = true;
			apConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			apConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			apConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			apConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			apConfig.status = WifiConfiguration.Status.ENABLED;
			// 通过反射调用设置热点
			Method method = wifi.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, Boolean.TYPE);
			// Toast.makeText(MainActivity.this,
			// "连接账号："+apConfig.SSID+",密码是："+apConfig.preSharedKey,
			// 5000).show();//提示信息接收方要连接的热点账号和密码
			// 返回热点打开状态
			return (Boolean) method.invoke(wifi, apConfig, enabled);
		} catch (Exception e) {
			return false;
		}
	}

	private ServiceConnection sc = null;

	private void connection() {
		try {
			//Intent intent = new Intent("com.deng.bindService");
			//bindService(intent, sc, this.BIND_AUTO_CREATE);
			if(Const.ser == 1){   //判断是否是第一次连接
				Intent intent= new Intent(HightMainActivity.this,LocalService.class);
				//启动服务
				//startService(intent);          //开启服务
				bindService(intent, sc, this.BIND_AUTO_CREATE);
			}
			Const.ser++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		unbindService(sc);
		super.onDestroy();
	}

}
