package com.android_red.Thread;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MsgHandler extends Handler {  
    private Activity activity;  
    public MsgHandler(Activity activity){  
        this.activity=new WeakReference<Activity>(activity).get();//使用弱引用<span style="font-family: Arial, Helvetica, sans-serif;">WeakReferenc</span>，以<span style="color:#ff0000;">避免Handler内存泄露</span>  
    }  
    @Override  
    public void handleMessage(Message msg) {  
  
        switch (msg.arg1) {  
        case 1:  
            showInfo("登录成功！");  
            break;  
        case 2:  
            showInfo("用户名称或者密码错误，请重新输入！");  
            break;  
        case 3:  
            showInfo("如果登录成功,以后账号和密码会自动输入!");  
            break;  
        case 4:  
            showInfo("网络未连接!");  
            break;  
        case 5:  
            showInfo("用户账户是必填项！");  
            break;  
        case 6:  
            showInfo("用户口令是必填项！");  
            break;  
        default:  
            break;  
        }  
        super.handleMessage(msg);  
    }  
      
    /**  
     * 显示提示信息  
     * @param info  
     */  
    public void showInfo(String info)  
    {  
        Toast.makeText(activity.getApplicationContext(),info, Toast.LENGTH_SHORT).show();  
    }  
  
}  
