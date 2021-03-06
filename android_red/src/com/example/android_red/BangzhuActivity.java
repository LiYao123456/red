package com.example.android_red;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BangzhuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bangzhu);
		init();  
		 } 
	
	private void init(){
		WebView webView = (WebView) findViewById(R.id.webView01);  
        //WebView加载web资源
       webView.loadUrl("http://baidu.com");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
       webView.setWebViewClient(new WebViewClient(){
           @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
             view.loadUrl(url);
            return true;
        }
       });
    }
	// 使当前Activity返回键无效
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (KeyEvent.KEYCODE_BACK == keyCode) {
//			return false;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	}
