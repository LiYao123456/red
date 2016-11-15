package com.example.android_red;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android_red.Thread.CheckRedMessThread;
import com.android_red.Thread.GetRedUtil;
import com.android_red.Thread.MsgHandler;
import com.android_red.intenter.Const;
import com.android_red.intenter.Test;
import com.android_red.slideswitch.HightMainActivity;
import com.test.threads.ListenThread;
import com.test.threads.SendDataThread;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

@SuppressLint("NewApi")
public class QiangHongBaoService extends AccessibilityService
{

	public static Thread thread,threadlis;
	static final String TAG = "QiangHongBao";
	SQLiteDatabase db;
	/** 微信的包名 */
	static final String WECHAT_PACKAGENAME = "com.tencent.mm";
	/** 红包消息的关键字 */
	static final String QQ_HONGBAO_TEXT_KEY = "[QQ红包]";
	private boolean caihongbao = false;
	private static boolean chaikou = false;
	private static int chaikoustep = 0;
	private static boolean permit = false;
	private static AccessibilityNodeInfo nowRed=null;
	private static CheckRedMessThread checkRedThread=null;
	public static String putMess=null;
	public static List<AccessibilityNodeInfo> qqList=null;
	Handler handler1;
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event)
	{
		final int eventType = event.getEventType(); // ClassName:
													// com.tencent.mm.ui.LauncherUI
		
		if(putMess!=null){
		Toast.makeText(this, putMess, Toast.LENGTH_SHORT).show();
		Log.i(TAG, "--------------;"+putMess);
		putMess=null;
		}
		Log.i(TAG, "--->进入AccessibilityEvent"+eventType);
		Log.i(TAG, "--->进入class类"+event.getClassName());
		 if (eventType ==2048)
		{
			Log.i(TAG, "--->新的红包");
			// 处理微信聊天界面
			openHongBao(event);
		}else if(eventType ==32){
			  if("cooperation.qwallet.plugin.QWalletPluginProxyActivity".equals(event.getClassName()))
				{
					// 在聊天界面,去点中红包
					Log.i(TAG, "检查雷值");
					Map<String,String> redMap= jianchalei(getRootInActiveWindow());
					 if(CheckRedMessThread.outWalletType==2){
					if(CheckRedMessThread.outWalletEnable){
					performGlobalAction(1);
					Log.i("QiangHongBaoThread", "退出红包记录");
					CheckRedMessThread.outWalletEnable=false;
					}
					CheckRedMessThread.outWalletType=0;
					 }else if(CheckRedMessThread.outWalletType==1){
						 CheckRedMessThread.outWalletType=2;
					 }
					if(checkRedThread!=null&&redMap!=null)
					checkRedThread.setRedMap(redMap);
					CheckRedMessThread.inRedMess=true;
				}else{
					openHongBao(event);
				}
		}
	}

	@Override
	public void onInterrupt()
	{
		Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onServiceConnected()
	{
		super.onServiceConnected();
		Toast.makeText(this, "连接抢红包服务", Toast.LENGTH_SHORT).show();
		handler1 = new Handler() {   //接收
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					Toast.makeText(QiangHongBaoService.this, msg.obj.toString(),
							1000).show();
					break;
				case 2:
					Toast.makeText(QiangHongBaoService.this, msg.obj.toString(),
							1000).show();
					break;
				default:
					break;
				}
			}

		};
		
		if(Const.ser == 1){    //开启接收线程
		threadlis = new ListenThread(Const.port,handler1);
		threadlis.start();
		Log.i("server", "开启接收消息线程");
		Const.ser++;
		 }
		
//		if(Const.qiang == 2){
//			handler.postDelayed(runnable, 4000);//开启发送线程
//		}

		
		//handler2.postDelayed(runnable2, 500);//测试ping
	}
	
//	Handler handler=new Handler();   //开启发送线程
//	Runnable runnable=new Runnable() {  
//	    @Override  
//	    public void run() {  
//	        // TODO Auto-generated method stub  
//	        //要做的事情  
//	    	thread = new SendDataThread("192.168.0.102");
//	    	thread.start();  //向秒抢段发送消息
//	    	handler.postDelayed(this, 4000); 
//	    }  
//	};
	
//	Handler handler2=new Handler();  //测试ping
//	Runnable runnable2=new Runnable() {  
//	    @Override  
//	    public void run() {  
//	        // TODO Auto-generated method stub  
//	        //要做的事情  
//	    	Process p,p1;
//	    	
//			try {
//				p = Runtime.getRuntime().exec("ping -c 1 -w 100 " +"192.168.0.102");
//				//p1 = Runtime.getRuntime().exec("ping -c 1 -w 100 " +"192.168.0.102");
//				int status = p.waitFor();
//				//int status2 = p1.waitFor();
//				Log.i("server", "ping自己结果:"+status);	
//				//Log.i("server", "ping对方结果:"+status2);	
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				Log.i("server", "ping出错1");	
//				e1.printStackTrace();
////				threadlis.interrupt();
////				
////				new ListenThread(Const.port,handler1).start();
//				
//			} catch (InterruptedException e) {
//				Log.i("server", "ping出错2");	
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//			
//	    	handler2.postDelayed(this, 500); 
//	    }  
//	};
	
	/** 打开通知栏消息 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void openNotify(AccessibilityEvent event)
	{
		if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification))
		{
			return;
		}
		// 将微信的通知栏消息打开
		Notification notification = (Notification) event.getParcelableData();
		PendingIntent pendingIntent = notification.contentIntent;
		try
		{
			pendingIntent.send();
		} catch (PendingIntent.CanceledException e)
		{
			e.printStackTrace();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void openHongBao(AccessibilityEvent event)
	{
		CharSequence className = event.getClassName();
		checkScreen(getApplicationContext());

		 if ( "com.tencent.mobileqq.activity.ChatActivity".equals(className))
		{
			Log.i(TAG, "在聊天界面");
			// 在聊天界面,去点中红包
			checkKey2();
		} else if ("com.tencent.mobileqq.activity.SplashActivity".equals(className))
		{
			Log.i(TAG, "在主界面界面");
			// 在聊天界面,去点中红包
			checkKey2();
		}else{
			checkKey2();
			//checkNodeInfo(getRootInActiveWindow());
		}
	}

	private Map<String,String> jianchalei(AccessibilityNodeInfo nodeInfo){
		if(nodeInfo!=null&&nodeInfo.getChildCount()>3){
			Map<String,String> reMap=new HashMap<String, String>();
			
		AccessibilityNodeInfo redMessInfo=nodeInfo.getChild(3);
		String getStr=null;
			try{
				getStr=redMessInfo.getChild(0).getText().toString();
			}catch (Exception e){
				reMap.put("code","1");
				return reMap;
			}
		String remain="";
		int yuIndex=getStr.indexOf("余");
		if(yuIndex!=-1){
		remain=getStr.substring(yuIndex+1, getStr.length()-1); 
		Log.i(TAG, "剩余:"+remain);
		getStr=getStr.substring(3, 6);
		String[] getArr=getStr.split("/");
		Log.i(TAG, "领取字符串:"+getStr);
		Log.i(TAG, "红包信息:"+redMessInfo.getText());
		Log.i(TAG, "红包列表大小"+redMessInfo.getChildCount());
			reMap.put("code", "0");
			reMap.put("redSum", getArr[1]);
			reMap.put("redNow", getArr[0]);
			reMap.put("remain", remain);
		}else{
			reMap.put("code","2");
			Log.i(TAG, "红包领完了");
		}
		return reMap;
		}else
			return null;
		
	}
	
	
	
	private void checkNodeInfo(AccessibilityNodeInfo nodeInfo){
		
		Log.i(TAG, "界面大小："+nodeInfo.getChildCount());
		if(nodeInfo!=null&&nodeInfo.getChildCount()>0)
		for(int i=0;i<nodeInfo.getChildCount();i++){
			if(nodeInfo.getChild(i).getChildCount()>0){
				jianchalei(nodeInfo.getChild(i));
			}else{
				Log.i(TAG, "--->红包子组件说明"+i+":"+ nodeInfo.getChild(i).getText());
			}
		}else
			Log.i(TAG, "--->没有红包子组件说明:"+ nodeInfo.getText());
	}
	
	/**
	 * 
	 * @description: 检查屏幕是否亮着并且唤醒屏幕
	 * @date: 2016-1-29 下午2:08:25
	 * @author: yems
	 */
	private void checkScreen(Context context)
	{
		// TODO Auto-generated method stub
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (!pm.isScreenOn())
		{
			wakeUpAndUnlock(context);
		}

	}

	private void wakeUpAndUnlock(Context context)
	{
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
		// 解锁
		kl.disableKeyguard();
		// 获取电源管理器对象
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		// 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		// 点亮屏幕
		wl.acquire();
		// 释放
		wl.release();
	}


	
	private int chailei(String leiStr){
		Log.i(TAG, "--->进入分析雷值"+leiStr);
		 String[] fenchar={"/","-",":","--","：",".","+","=","%","#","!","！"};
		 for(int i=0;i<fenchar.length;i++){
			 if(leiStr.indexOf(fenchar[i])!=-1){
				 Log.i(TAG, "--->包含字符");
				 String[] leiArr;
				 if(i==5||i==6)
					  leiArr=leiStr.split("\\"+fenchar[i]);
				 else
					  leiArr=leiStr.split(fenchar[i]);
				 
				 if(leiArr.length==2){
					 Log.i(TAG, "--->分割字符");
					 int[] leiIntArr=new int[2];
					 leiIntArr[0]=Integer.parseInt(leiArr[0]);
					 leiIntArr[1]=Integer.parseInt(leiArr[1]);
					 if(leiIntArr[0]<leiIntArr[1])
						 return leiIntArr[0];
					 else
						 return leiIntArr[1];
				 }
			 }
		 }
		 return 10;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void checkKey2()
	{
		try {
		Log.i(TAG, "--->进入checkKey2");
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		GetRedUtil.mainInfo=nodeInfo.getChild(4);
		if (nodeInfo == null)
		{
			Log.w(TAG, "rootWindow为空");
			return;
		}
		Log.w(TAG, "主界面的大小："+nodeInfo.getChildCount());
		
		qqList = nodeInfo.findAccessibilityNodeInfosByText("QQ红包");
		
		if (!qqList.isEmpty())
		{
			Log.i(TAG, "--->检查红包:qqList的大小" + qqList.size());
			for (AccessibilityNodeInfo n : qqList)
			{
				Log.i(TAG, "--->检查红包描述:"+ n.getParent().toString());
				//n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		}
		
		 if (!qqList.isEmpty())
		{
			// 界面上的红包总个数
			//int totalCount = qqList.size();
			// 领取最近发的红包
				// 如果为领取过该红包，则执行点击、
			 for(int i=0;i<qqList.size();i++){
				 AccessibilityNodeInfo	parent = qqList.get(i).getParent();
				Log.i(TAG, "-->红包组件id:" + parent.getWindowId());
				if (parent != null&&parent.getChildCount()>1)
				{
					if(CheckRedMessThread.inRedMess==true&&checkRedThread!=null)
						checkRedThread.affirminRedMess=true;
					if(parent.getChild(1).getText().toString().equals("点击拆开")){
						/*for(int i=0;i<parent.getChildCount();i++){
							Log.i(TAG, "-->红子组件类名:" + parent.getChild(i).getContentDescription());
						}
						Log.i(TAG, "-->红包子组件文本:" + parent.getChild(1).getText());
						Log.i(TAG, "-->红包组件类名:" + parent.getClassName());
						if(nowRed!=null){
							Log.i(TAG, "上次红包"+nowRed);
							Log.i(TAG, "这次红包"+parent);
						if(parent.equals(nowRed))
							Log.i(TAG, "等于上次红包");
						}else{
							Log.i(TAG, "换了新红包");
							nowRed=parent;
						}*/
						if(Const.qiang==2){
						if(checkRedThread==null||!checkRedThread.isAlive()){
							Log.i(TAG, "进入红包记录组件:"+parent.getChild(2).getText().toString());
							int lei= chailei(parent.getChild(0).getText().toString());
							Log.i(TAG, "分析的雷值:"+lei);
							if(lei!=10){
								boolean createTh=false;
								for(int k=0;k<CheckRedMessThread.infoList.size();k++){
									if(CheckRedMessThread.infoList.get(k).equals(parent)){
										createTh=true;
										Log.i(TAG, "有重复红包，退出"+i);
										break;
									}
								}
								if(createTh)
									continue;
							checkRedThread=new CheckRedMessThread(parent,lei,nodeInfo.getChild(4));
							checkRedThread.start();
							}
							continue;
						}
						return;
					}
					}
//					else if(Const.qiang==1){
//						Log.i(TAG, "set1111111111111111111111");
//						if(GetRedUtil.getdata){
//						Log.i(TAG, "set2222222222222222");
//						AccessibilityNodeInfo parent1= parent.getParent();
//						if(parent1.getChild(parent1.getChildCount()-2).equals(GetRedUtil.redSend)){
//							Log.i(TAG, "set333333333333333");
//							GetRedUtil.setRedMess(parent);
//						}
//						return;
//						}
//					}
//					else if(parent.getChild(1).getText().toString().equals("口令红包")){
//						if(!chaikou)
//						switch (chaikoustep) {
//						case 0:
//							parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//							chaikoustep=1;
//							break;
//						case 1:
//							List<AccessibilityNodeInfo> kouling = nodeInfo.findAccessibilityNodeInfosByText("点击输入口令");
//							if(kouling!=null&&kouling.size()!=0)
//							{
//								Log.i(TAG, "-->输入口令:i" + kouling.get(0).getText());
//								kouling.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//							}
//							chaikoustep=2;
//							break;
//						case 2:
//							List<AccessibilityNodeInfo> sendbnt = nodeInfo.findAccessibilityNodeInfosByText("发送");
//							if(sendbnt!=null&&sendbnt.size()!=0)
//							{
//								Log.i(TAG, "-->点击发送:i" + sendbnt.get(0).getText());
//								sendbnt.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//							}
//							Log.i(TAG, "-->领取口令红包处理完:");
//							chaikou=true;
//							chaikoustep=0;
//							break;
//						default:
//							break;
//						}else{
//							chaikou=false;
//						}
//					}
				}
		}
	}
		 if(nodeInfo.getChild(4)!=null)
			 if(Const.qiang==2)
				 nodeInfo.getChild(4).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
		} catch (Exception e) {
			return;
		}
	}

    @Override 
    public int onStartCommand(Intent intent, int flags, int startId) { 
        return START_STICKY;
    }
    
	
}
