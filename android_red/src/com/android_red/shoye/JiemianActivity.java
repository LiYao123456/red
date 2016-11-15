//广告显示界面
package com.android_red.shoye;

import java.io.File;
import java.util.ArrayList;

import com.example.android_red.MainActivity;
import com.example.android_red.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class JiemianActivity extends Activity {
	// 定义ViewPager对象
	private ViewPager viewPager;

	// 定义ViewPager适配器
	private ViewPagerAdapter vpAdapter;

	// 定义一个ArrayList来存放View
	private ArrayList<View> views;

	// 定义各个界面View对象
	private View view1, view2, view3, view4, view5, view6;

	// 定义底部小点图片
	private ImageView pointImage0, pointImage1, pointImage2, pointImage3,
			pointImage4, pointImage5;

	// 定义开始按钮对象
	private Button startBt;

	// 当前的位置索引值
	private int currIndex = 0;
	public static int i	= 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_jiemian);

		initView();
		initData();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		// 实例化各个界面的布局对象
		LayoutInflater mLi = LayoutInflater.from(this);
		view1 = mLi.inflate(R.layout.guide_view01, null);
		view2 = mLi.inflate(R.layout.guide_view02, null);
		view3 = mLi.inflate(R.layout.guide_view03, null);
		view4 = mLi.inflate(R.layout.guide_view04, null);
		view5 = mLi.inflate(R.layout.guide_view05, null);
		view6 = mLi.inflate(R.layout.guide_view06, null);

		File dirFile = new File(Environment.getExternalStorageDirectory() + "/download_image/");  
        if(!dirFile.exists()){  
            dirFile.mkdir();  
        } 
		File file0 = new File(Environment.getExternalStorageDirectory() + "/download_image/1.jpg");
		if(file0.exists()){
			RelativeLayout rLayout01 = (RelativeLayout)view1. findViewById (R.id.rela01);
			String photo1="/mnt/sdcard/download_image/1.jpg"; 
			rLayout01.setBackgroundDrawable(Drawable.createFromPath(photo1)); 
		}
		File file1 = new File(Environment.getExternalStorageDirectory() + "/download_image/2.jpg");
		if(file1.exists()){
			RelativeLayout rLayout02 = (RelativeLayout)view2. findViewById (R.id.rela02);
			String photo2="/mnt/sdcard/download_image/2.jpg"; 
			rLayout02.setBackgroundDrawable(Drawable.createFromPath(photo2)); 
		}
//		new Thread() {
//			public void run() {
//				String path = "/sdcard/download_image/";
//				getFiles(path);
//				System.out.println(path + " 文件夹下面共有 " + i + " 张图片文件");
//				if(i==6){
//					RelativeLayout rLayout01 = (RelativeLayout)view1. findViewById (R.id.rela01);
//					String photo1="/mnt/sdcard/download_image/1.jpg"; 
//					rLayout01.setBackgroundDrawable(Drawable.createFromPath(photo1)); 
//					
//					RelativeLayout rLayout02 = (RelativeLayout)view2. findViewById (R.id.rela02);
//					String photo2="/mnt/sdcard/download_image/2.jpg"; 
//					rLayout02.setBackgroundDrawable(Drawable.createFromPath(photo2)); 
//					
//					RelativeLayout rLayout03 = (RelativeLayout)view3. findViewById (R.id.rela03);
//					String photo3="/mnt/sdcard/download_image/3.jpg"; 
//					rLayout03.setBackgroundDrawable(Drawable.createFromPath(photo3)); 
//					
//					RelativeLayout rLayout04 = (RelativeLayout)view4. findViewById (R.id.rela04);
//					String photo4="/mnt/sdcard/download_image/4.jpg"; 
//					rLayout04.setBackgroundDrawable(Drawable.createFromPath(photo4)); 
//					
//					RelativeLayout rLayout05 = (RelativeLayout)view5. findViewById (R.id.rela05);
//					String photo5="/mnt/sdcard/download_image/5.jpg"; 
//					rLayout05.setBackgroundDrawable(Drawable.createFromPath(photo5)); 
//				
//					RelativeLayout rLayout06 = (RelativeLayout)view6. findViewById (R.id.rela06);
//					String photo6="/mnt/sdcard/download_image/6.jpg"; 
//					rLayout06.setBackgroundDrawable(Drawable.createFromPath(photo6)); 
//					i = 0;
//				}
//			};
//		}.start();
		

	
		

		// 实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);

		// 实例化ArrayList对象
		views = new ArrayList<View>();

		// 实例化ViewPager适配器
		vpAdapter = new ViewPagerAdapter(views);

		// 实例化底部小点图片对象
		pointImage0 = (ImageView) findViewById(R.id.page0);
		pointImage1 = (ImageView) findViewById(R.id.page1);
		pointImage2 = (ImageView) findViewById(R.id.page2);
		pointImage3 = (ImageView) findViewById(R.id.page3);
		pointImage4 = (ImageView) findViewById(R.id.page4);
		pointImage5 = (ImageView) findViewById(R.id.page5);

		// 实例化开始按钮
		startBt = (Button) view6.findViewById(R.id.startBtn);
	}

	/*
	 * 1.判断此时是否有网络
	 * 2.有网络，访问服务器下载图片，存放在本地文件夹，调用本地文件夹图片
	 * 3.没有网络，调用本地文件夹图片
	 */
	
	private static final String SHAREDPREFERENCES_NAME = "my_pref";
	private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

	private void setGuided() {
		SharedPreferences settings = getSharedPreferences(
				SHAREDPREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEY_GUIDE_ACTIVITY, "false");
		editor.commit();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 将要分页显示的View装入数组中
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		views.add(view5);
		views.add(view6);

		// 设置监听
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		// 设置适配器数据
		viewPager.setAdapter(vpAdapter);

		// 给开始按钮设置监听
		startBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startbutton();
			}
		});
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			switch (position) {
			case 0:
				File file1 = new File(Environment.getExternalStorageDirectory() + "/download_image/2.jpg");
				if(file1.exists()){
					RelativeLayout rLayout02 = (RelativeLayout)view2. findViewById (R.id.rela02);
					String photo2="/mnt/sdcard/download_image/2.jpg"; 
					rLayout02.setBackgroundDrawable(Drawable.createFromPath(photo2)); 
				}
				pointImage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_focused));
				pointImage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				break;
			case 1:
				File file2 = new File(Environment.getExternalStorageDirectory() + "/download_image/3.jpg");
				if(file2.exists()){
					RelativeLayout rLayout03 = (RelativeLayout)view3. findViewById (R.id.rela03);
					String photo3="/mnt/sdcard/download_image/3.jpg"; 
					rLayout03.setBackgroundDrawable(Drawable.createFromPath(photo3)); 
				}
				pointImage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_focused));
				pointImage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				pointImage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				break;
			case 2:
				File file3 = new File(Environment.getExternalStorageDirectory() + "/download_image/4.jpg");
				if(file3.exists()){
					RelativeLayout rLayout04 = (RelativeLayout)view4. findViewById (R.id.rela04);
					String photo4="/mnt/sdcard/download_image/4.jpg"; 
					rLayout04.setBackgroundDrawable(Drawable.createFromPath(photo4)); 
				}
				pointImage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_focused));
				pointImage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				pointImage3.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				break;
			case 3:
				File file4 = new File(Environment.getExternalStorageDirectory() + "/download_image/5.jpg");
				if(file4.exists()){
					RelativeLayout rLayout05 = (RelativeLayout)view5. findViewById (R.id.rela05);
					String photo5="/mnt/sdcard/download_image/5.jpg"; 
					rLayout05.setBackgroundDrawable(Drawable.createFromPath(photo5)); 
				}
				pointImage3.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_focused));
				pointImage4.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				pointImage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				break;
			case 4:
				File file5 = new File(Environment.getExternalStorageDirectory() + "/download_image/6.jpg");
				if(file5.exists()){
					RelativeLayout rLayout06 = (RelativeLayout)view6. findViewById (R.id.rela06);
					String photo6="/mnt/sdcard/download_image/6.jpg"; 
					rLayout06.setBackgroundDrawable(Drawable.createFromPath(photo6)); 
				}
				pointImage4.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_focused));
				pointImage3.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				pointImage5.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				break;
			case 5:

				pointImage5.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_focused));
				pointImage4.setImageDrawable(getResources().getDrawable(
						R.drawable.page_indicator_unfocused));
				break;
			}
			currIndex = position;
			// animation.setFillAfter(true);// True:图片停在动画结束位置
			// animation.setDuration(300);
			// mPageImg.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	//文件夹下图片的数目
	private void getFiles(String string) {
		// TODO Auto-generated method stub 
		File file = new File(string);
		File[] files = file.listFiles();
		for (int j = 0; j < files.length; j++) {
			String name = files[j].getName();
			if (files[j].isDirectory()) {
				String dirPath = files[j].toString().toLowerCase(); 
				System.out.println(dirPath);
				getFiles(dirPath + "/");
			} else if (files[j].isFile() & name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".bmp") || name.endsWith(".gif") || name.endsWith(".jpeg")) {
				System.out.println("FileName===" + files[j].getName());
				i++;
			}
		}
		
	}
	
	/**
	 * 相应按钮点击事件
	 */
	private void startbutton() {
		setGuided();
		Intent intent = new Intent();
		intent.setClass(JiemianActivity.this, MainActivity.class);
		startActivity(intent);
		this.finish();
	}
	


}
