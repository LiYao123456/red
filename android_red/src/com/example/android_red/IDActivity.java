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
import com.android_red.intenter.MD5;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
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

public class IDActivity extends Activity {


	Button btntyid, btncopyuyid,btnpay,btnpower,btnfanhui;
	EditText etmyuyid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.id);
		etmyuyid = (EditText) findViewById(R.id.myuyid);
		//按钮跳转
		btntyid = (Button) findViewById(R.id.tyid);
		
		btntyid.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//获取当前手机的唯一机器码
								TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
						        String szImei = TelephonyMgr.getDeviceId();
							      //wifi mac地址  
						        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
						      //android6.0 后获取mac地址
						        String wifimac = recupAdresseMAC(wifi);
						        String sum = szImei+wifimac;
						        MD5 md = new MD5();
								String code = md.toDigest(sum);
								System.out.println("16位：result: " + code.toString().substring(8,24));//16位的加密 
								etmyuyid.setText(code.toString().substring(8,24));  //16位
							}
						});
						
						
		btncopyuyid = (Button) findViewById(R.id.copyuyid);
		btncopyuyid.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
								String copyuyid = etmyuyid.getText().toString();
								clip.setText(copyuyid); // 复制
							}
						});
						
						
		btnpay = (Button) findViewById(R.id.pay);
		btnpay.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(IDActivity.this, PayActivity.class);
								startActivity(intent);
							}
						});
				
		btnpower = (Button) findViewById(R.id.power);
		btnpower.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(IDActivity.this, PowerActivity.class);
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
}
