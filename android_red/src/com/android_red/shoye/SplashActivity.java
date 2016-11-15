//打开软件时的图片加载页面

package com.android_red.shoye;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android_red.intenter.AsyncTaskManager;
import com.android_red.intenter.AsyncTaskSoap.OnDataRecvListener;
import com.android_red.intenter.CustomProgressDialog;
import com.android_red.intenter.NetWork;
import com.example.android_red.MainActivity;
import com.example.android_red.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 功能：使用ViewPager实现初次进入应用时的引导页
 * 
 * (1)判断是否是首次加载应用--采取读取SharedPreferences的方法 (2)是，则进入引导activity；否，则进入MainActivity
 * (3)5s后执行(2)操作
 * 
 * @author sz082093
 * 
 */
public class SplashActivity extends Activity{

	AsyncTaskManager atm = new AsyncTaskManager(); // 异步通信管理器  （向服务器发送请求时使用）
//  Handler和AsyncTask，都是为了不阻塞主线程（UI线程），且UI的更新只能在主线程中完成，因此异步处理是不可避免
//	ProcessDlgAction pda = new ProcessDlgAction(); // 进度对话框（加载）
	CustomProgressDialog pda;
	
	String fileEx, fileNa, filename;
	static int fileSize;
	static int downLoadFileSize;
	private final static String TAG = "SplashActivity";  
    private final static String ALBUM_PATH  
            = Environment.getExternalStorageDirectory() + "/download_image/";  
    private Bitmap mBitmap;  
    private String mFileName;  
    private String mSaveMessage; 
    private static List list = new ArrayList();
    Timer timer = new Timer();  
    private int recLen = 6;  
    private TextView txtView;   
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imagelogin);
		txtView = (TextView)findViewById(R.id.texttime);    
		boolean mFirst = isFirstEnter(SplashActivity.this, SplashActivity.this
				.getClass().getName());
		//判断此时是否有网络
//	if (NetWork.isNetworkAvailable(SplashActivity.this)) {  //判断此时是否有网络
//				pda =new CustomProgressDialog(this, "",R.anim.frame);   //加载动画
//				pda.show();
//				// 开启异步线程，回调，访问服务器，获取服务器返回的ret，判断是否登录成功
//				atm.startAsyncTask("imagelist", "", odrLsn, 1);//访问服务器,服务器返还一个list
//		
//	}else{
//		Toast.makeText(getApplicationContext(), "当前没有可用网络！",
//				Toast.LENGTH_LONG).show();
//	}
//		 new Thread(connectNet).start(); //下载图片
		Message message = mHandler.obtainMessage(1);     // Message  
		if (mFirst)
		mHandler.sendEmptyMessageDelayed(1, 1000);
	else
		mHandler.sendEmptyMessageDelayed(1, 1000);
		

 
	}

	// 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段

	private static final String SHAREDPREFERENCES_NAME = "my_pref";
	private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

	private boolean isFirstEnter(Context context, String className) {
		if (context == null || className == null
				|| "".equalsIgnoreCase(className))
			return false;
		String mResultStr = context.getSharedPreferences(//通过Context.getSharedPreferences方法获取SharedPreferences对象,参数分别为存储的文件名和存储模式。
				SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(
				KEY_GUIDE_ACTIVITY, "");// 取得所有类名 如 com.my.MainActivity
		if (mResultStr.equalsIgnoreCase("false")) //字符串比较
			return false;
		else
			return true;
	}

	// *************************************************
	// Handler:跳转至不同页面
	// *************************************************
	private final static int SWITCH_MAINACTIVITY = 1000;
	private final static int SWITCH_GUIDACTIVITY = 1001;
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:   //已经存在，跳转广告
				recLen--;    
                txtView.setText("正在加载资源文件，倒计时：" + recLen+"秒");    
   
                if(recLen > 0){    
                    Message message = mHandler.obtainMessage(1);    
                    mHandler.sendMessageDelayed(message, 1000);      // send message    
                }else{    
                    txtView.setVisibility(View.GONE);
            		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            		startActivity(intent);
                }  
			super.handleMessage(msg);
		}
	};
	};
	
	// 异步网络请求完毕回调主线程代码
		OnDataRecvListener odrLsn = new OnDataRecvListener() {
			@Override
			public void onDataRecv(JSONObject jsonObj, int code) {
				pda.dismiss();
				if(null==jsonObj){   //网络不好时会出现程序崩溃的情况，获取不到ret
					Toast.makeText(SplashActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
				}else{
				try {
					int ret = jsonObj.getInt("ret");
					if (0 == ret) {
						JSONArray arr = jsonObj.getJSONArray("list"); //6张图片的url
						if(arr.length()==6){
							File root = new File(Environment.getExternalStorageDirectory()+ "/download_image");
							deleteAllFiles(root);
							Toast.makeText(getApplicationContext(), "此时图片是6张，开始！",
									Toast.LENGTH_LONG).show();
						}
						for(int i=0;i<arr.length();i++){
							list.add(arr.get(i));	
						}
						Toast.makeText(getApplicationContext(), "加载图片！",
								Toast.LENGTH_LONG).show();
						        new Thread(connectNet).start(); //下载图片
					} else {
						Toast.makeText(SplashActivity.this, "服务器端广告图片错误，从本地进行获取", Toast.LENGTH_LONG)
								.show();
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "服务器连接异常！",
							Toast.LENGTH_LONG).show();
				  }
				}
			}
		};
    /** 
     * Get image from newwork 
     * @param path The path of image 
     * @return byte[] 
     * @throws Exception 
     */  
    public byte[] getImage(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        InputStream inStream = conn.getInputStream();  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return readStream(inStream);  
        }  
        return null;  
    }  
  
    /** 
     * Get image from newwork 
     * @param path The path of image 
     * @return InputStream 
     * @throws Exception 
     */  
    public InputStream getImageStream(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return conn.getInputStream();  
        }  
        return null;  
    }  
    /** 
     * Get data from stream 
     * @param inStream 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        inStream.close();  
        return outStream.toByteArray();  
    }  
  
    /** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
    public void saveFile(Bitmap bm, String fileName) throws IOException {  
        File dirFile = new File(ALBUM_PATH);  
        if(!dirFile.exists()){  
            dirFile.mkdir();  
        }  
        File myCaptureFile = new File(ALBUM_PATH + fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
        bos.flush();  
        bos.close();  
    }  
  
    
    private Runnable saveFileRunnable = new Runnable(){  
        @Override  
        public void run() {  
            try {  
                saveFile(mBitmap, mFileName); 
                System.out.println(mFileName+"图片保存成功！");
                mSaveMessage = "图片保存成功！";  
            } catch (IOException e) {  
            	System.out.println("图片保存失败！");
                mSaveMessage = "图片保存失败！";  
                e.printStackTrace();  
            }   
        }  
    };  

    /* 
     * 连接网络 
     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问 
     */  
    private Runnable connectNet = new Runnable(){  
        @Override  
        public void run() {  
            try { 
            	for(int i = 0;i<list.size();i++){
            		String filePath = "http://test.tx15.eccentertool.com:39011/ueditor/jsp/upload/image/"+list.get(i).toString(); 
                    mFileName =i+".jpg";  
                    System.out.println(mFileName);
                    //////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap  
                    byte[] data = getImage(filePath);  
                    if(data!=null){  
                        mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
                        new Thread(saveFileRunnable).start(); 
                    }else{  
                    	System.out.println(mFileName+"error");
                        Toast.makeText(SplashActivity.this, "Image error!", 1).show();  
                    }
                    if(i == list.size()-1){
                    	filePath = "http://test.tx15.eccentertool.com:39011/ueditor/jsp/upload/image/"+list.get(5).toString(); 
                        mFileName =6+".jpg";  
                        System.out.println(mFileName);
                        data = getImage(filePath);  
                        if(data!=null){  
                            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
                            new Thread(saveFileRunnable).start(); 
                        }else{  
                        	System.out.println(mFileName+"error");
                            Toast.makeText(SplashActivity.this, "Image error!", 1).show();  
                        } 
                    }
            	} 
            } catch (Exception e) {  
                Toast.makeText(SplashActivity.this,"无法链接网络！", 1).show();  
                e.printStackTrace();  
            }  
        }  
    };  
   
    //删除文件夹下所有文件
	private void deleteAllFiles(File root) {  
        File files[] = root.listFiles();  
        if (files != null) 
            for (File f : files) {  
                    if (f.exists()) {         // 判断是否存在  
                        deleteAllFiles(f);  
                        try {  
                            f.delete();  

                        } catch (Exception e) {  
                        }  
                    }  
            }  
    } 
}