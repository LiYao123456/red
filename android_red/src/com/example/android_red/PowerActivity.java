package com.example.android_red;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import com.android_red.intenter.AsyncTaskManager;
import com.android_red.intenter.MD5;
import com.android_red.intenter.NetWork;
import com.android_red.intenter.AsyncTaskSoap.OnDataRecvListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PowerActivity extends Activity {

	AsyncTaskManager atm = new AsyncTaskManager(); // 异步通信管理器 （向服务器发送请求时使用）
	AsyncTaskManager atm1 = new AsyncTaskManager();
	// Handler和AsyncTask，都是为了不阻塞主线程（UI线程），且UI的更新只能在主线程中完成，因此异步处理是不可避免
	// ProcessDlgAction pda = new ProcessDlgAction(); // 进度对话框（加载）
	//CustomProgressDialog pda;
	Button btnpower, btnfanhui, btnpowersucc,btnpowerqqlist;
	EditText power_uyid, power_qq;
	public static String imei = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.power);
		power_uyid = (EditText) findViewById(R.id.power_uyid);
		power_qq = (EditText) findViewById(R.id.power_qq);
		
		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
        String szImei = TelephonyMgr.getDeviceId();
	      //wifi mac地址  
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
      //android6.0 后获取mac地址
        String wifimac = recupAdresseMAC(wifi);
        String sum = szImei+wifimac;
        MD5 md = new MD5();
		String code = md.toDigest(sum);	
		imei = code.toString().substring(8,24);
		
		
		btnpower = (Button) findViewById(R.id.power);
		btnpower.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

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

		btnpowerqqlist = (Button) findViewById(R.id.powerqqlist);
		btnpowerqqlist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 查看当前授权码所授权的qq
				if (NetWork.isNetworkAvailable(PowerActivity.this)) { // 判断此时是否有网络
					atm1.startAsyncTask("qqlist", "imei=" + PowerActivity.imei, odrLsn1, 1); // 访问服务器
				} else {
					Toast.makeText(getApplicationContext(),
							"当前没有可用网络！", Toast.LENGTH_LONG).show();
				}

			}
		});
		
		btnpowersucc = (Button) findViewById(R.id.powersucc);
		btnpowersucc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 向服务器发送当前的授权码和qq,根据服务器的返回值来判断此时是否授权成功，成功则跳转
				//String code = md.toDigest("312378590742952");
				TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
		        String szImei = TelephonyMgr.getDeviceId();
			      //wifi mac地址  
		        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
		      //android6.0 后获取mac地址
		        String wifimac = recupAdresseMAC(wifi);
		        String sum = szImei+wifimac;
		        MD5 md = new MD5();
			//	String code = md.toDigest(sum);		
				if (power_uyid.getText().toString() == null
						|| power_uyid.getText().toString() == ""
						|| (power_uyid.getText().toString()).equals("")) {
					Toast.makeText(getApplicationContext(), "授权码不能为空！",
							Toast.LENGTH_LONG).show();
				} else if (power_qq.getText().toString() == null
						|| power_qq.getText().toString() == ""
						|| (power_qq.getText().toString()).equals("")) {
					Toast.makeText(getApplicationContext(), "QQ号不能为空！",
							Toast.LENGTH_LONG).show();
				} else if (checkQQ1(power_qq.getText().toString())) {
						if (NetWork.isNetworkAvailable(PowerActivity.this)) { // 判断此时是否有网络
							atm.startAsyncTask("insert", "powerid="
									+ power_uyid.getText().toString()
									+ "&powerqq="
									+ power_qq.getText().toString(), odrLsn, 1); // 访问服务器
						} else {
							Toast.makeText(getApplicationContext(),
									"当前没有可用网络！", Toast.LENGTH_LONG).show();
						}
				} else {
						Toast.makeText(getApplicationContext(), "QQ号输入错误！",
								Toast.LENGTH_LONG).show();
					}
				}
			//}
		});
	}

	OnDataRecvListener odrLsn = new OnDataRecvListener() {
		@Override
		public void onDataRecv(JSONObject jsonObj, int code) {
			//pda.dismiss();
			if (null == jsonObj) { // 网络不好时会出现程序崩溃的情况，获取不到ret
				Toast.makeText(PowerActivity.this, "请检查网络", Toast.LENGTH_LONG)
						.show();
			} else {
				try {
					int ret = jsonObj.getInt("ret");
					if (0 == ret) {
//						final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
//						imei = "" + tm.getDeviceId();   //IMEI
						//powerid = power_uyid.getText().toString();
						Intent intent = new Intent(PowerActivity.this,
								PowerSuccActivity.class);
						startActivity(intent);
						Toast.makeText(getApplicationContext(), "成功授权！",
								Toast.LENGTH_LONG).show();
					} else if (1 == ret) {
						Toast.makeText(PowerActivity.this, "授权码错误",
								Toast.LENGTH_LONG).show();
					} else if (2 == ret) {
//						Toast.makeText(PowerActivity.this, "当前qq已经授权过",
//								Toast.LENGTH_LONG).show();
					} else if (3 == ret) {
						Toast.makeText(PowerActivity.this, "当前qq已经授权过",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(PowerActivity.this, "授权码已经达到使用上限",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "服务器连接异常！",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	OnDataRecvListener odrLsn1 = new OnDataRecvListener() {
		@Override
		public void onDataRecv(JSONObject jsonObj, int code) {
			//pda.dismiss();
			if(null==jsonObj){   //网络不好时会出现程序崩溃的情况，获取不到ret
				Toast.makeText(PowerActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
			}else{
			try {
				int ret = jsonObj.getInt("ret");
				if (0 == ret) {
					Intent intent = new Intent(PowerActivity.this,
							PowerSuccActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(PowerActivity.this, "当前手机没有经过授权！", Toast.LENGTH_LONG)
					.show();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "服务器连接异常！",
						Toast.LENGTH_LONG).show();
			  }
			}
		}
	};
	
	private static boolean checkQQ1(String qqNum){  
        String message = null;  
        char[] chArr;  
        boolean flag=true;  
        if(qqNum.length()>=6&&qqNum.length()<=10){  
            chArr=qqNum.toCharArray();  
            for(char ch:chArr){  
                if(!(ch>='0'&&ch<='9')){
                    flag=false;  
                    break;  
                }  
            }  
            if(flag){  
                if(!qqNum.startsWith("0")){  
                    message=qqNum+"：QQ号码合法"; 
                    return true;
                }else{  
                    message=qqNum+"：QQ号码以零开头不合法";  
                }  
            }else{  
                message=qqNum+"：QQ号码有非法字符不合法";  
            }  
        }else{  
            message=qqNum+"：QQ号码过长或过短不合法";  
        }  
        return false;  
    } 

	private static final String marshmallowMacAddress = "02:00:00:00:00:00";  
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";  
  
    public static String recupAdresseMAC(WifiManager wifiMan) {  
        WifiInfo wifiInf = wifiMan.getConnectionInfo();  
        if(wifiInf.getMacAddress().equals(marshmallowMacAddress)){  
            String ret = null;  
            try {  
                ret= getAdressMacByInterface();  
                if (ret != null){  
                    return ret;  
                } else {  
                    ret = getAddressMacByFile(wifiMan);  
                    return ret;  
                }  
            } catch (IOException e) {  
                Log.e("MobileAccess", "Erreur lecture propriete Adresse MAC");  
            } catch (Exception e) {  
                Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");  
            }  
        } else{  
            return wifiInf.getMacAddress();  
        }  
        return marshmallowMacAddress;  
    }  
  
    @SuppressLint("NewApi")
	private static String getAdressMacByInterface(){  
        try {  
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());  
            for (NetworkInterface nif : all) {  
                if (nif.getName().equalsIgnoreCase("wlan0")) {  
                    byte[] macBytes = nif.getHardwareAddress();  
                    if (macBytes == null) {  
                        return "";  
                    }  
                    StringBuilder res1 = new StringBuilder();  
                    for (byte b : macBytes) {  
                        res1.append(String.format("%02X:",b));  
                    }  
  
                    if (res1.length() > 0) {  
                        res1.deleteCharAt(res1.length() - 1);  
                    }  
                    return res1.toString();  
                }  
            }  
  
        } catch (Exception e) {  
            Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");  
        }  
        return null;  
    }  
  
    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {  
        String ret;  
        int wifiState = wifiMan.getWifiState();  
  
        wifiMan.setWifiEnabled(true);  
        File fl = new File(fileAddressMac);  
        FileInputStream fin = new FileInputStream(fl);  
        ret = crunchifyGetStringFromStream(fin);  
        fin.close();  
  
        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;  
        wifiMan.setWifiEnabled(enabled);  
        return ret;  
    }  
  
    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {  
        if (crunchifyStream != null) {  
            Writer crunchifyWriter = new StringWriter();  
  
            char[] crunchifyBuffer = new char[2048];  
            try {  
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));  
                int counter;  
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {  
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);  
                }  
            } finally {  
                crunchifyStream.close();  
            }  
            return crunchifyWriter.toString();  
        } else {  
            return "No Contents";  
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
