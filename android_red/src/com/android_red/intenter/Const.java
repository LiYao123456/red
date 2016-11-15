package com.android_red.intenter;

import java.net.Socket;

public class Const {
	public static String WEB_PATH ="http://test.tx15.eccentertool.com:39011/cms/";
	//http://ds.tx15.dianshangshikong.com:39003/Testimg/test?name=1
	//public static String WEB_IP ="192.168.191.1";
	public static boolean switch1 = false;   //开启防封设置开关
	public static boolean switch2 = false;   //随机延时抢红包开关
	public static boolean switch3 = false;   //基本功能设置开关
	public static boolean switch4 = false;   //排雷功能设置开关
	public static boolean switch5 = false;   //雷值设置开关
	public static String time = "";
	public static int  rbyouxian= 2;         //优先设置-默认优先程序设置
	public static int  rb= 3;                //雷值设置-元角分设置
	public static int  ws= 0;                //雷值尾数设置
	public static int  jdt_min= 20;               //进度条-限时抢红包
	public static int  jdt_max= 150;               //进度条-限时抢红包
	public static int  wifi= 1;               //连接情况          1：同时连接路由器    2：一台开热点
	public static int  cs= 1;               //当前是测试IP  1：发起测试     2：收到测试请求回复    3：发送抢红包请求
	public static int  ser= 1;               //当前server
	public static String  ip= "";               //当前ip
	public static int  qiang= 1;               //抢红包方式    1：秒抢  2：扫描
	public static boolean  miaoqiang= false;               //抢红包
	public static String  mess= "";               //消息
	public static String  socketip= "";               //判断socket
	public static Socket socket=null;
	public static int port=12348;     //端口
}
