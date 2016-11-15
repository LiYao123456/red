package com.android_red.intenter;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class Test {
	int sum = 0;
	 static List<String> list = new ArrayList<String>();
	 
    //红包金额的list<String>,总份数，雷值，总金额
	public boolean AA(List<String> list,String fs,String lz,String summoney){
		list.add("12.23");
		list.add("12.23");
		list.add("12.23");
		list.add("12.23");
		list.add("12.23");
		if(Integer.parseInt(fs)-list.size()==1){   //只有最后一份了
			for(int i = 0;i<list.size();i++){
				double s = (Double.parseDouble(list.get(i))*100);
				sum = sum+ (int)(s);    //获取当前所有的值
			}
			list.clear();
			System.out.println(sum);
			int money = (int)(Double.parseDouble(summoney)*100)-sum;	
			System.out.println(money);
			int yuan=(int)money/100;
			int jiao=(int)(money%100)/10;
			int fen=(int)(money%10);
			System.out.println(money+"圆="+yuan+"圆"+jiao+"角"+fen+"分");
			//看尾数设置的是什么，
			if(Const.rbyouxian==1){//优先个人设置
				System.out.println("优先个人设置");
				if(Const.rb==1){//元
					System.out.println("用于比较的雷值（元）："+yuan);
					if(Const.ws == yuan){
						return false;
					}
				}else if(Const.rb==2){//角
					System.out.println("用于比较的雷值（角）："+jiao);
					if(Const.ws == jiao){
						return false;
					}
				}else{//分
					System.out.println("用于比较的雷值（分）："+fen);
					if(Const.ws == fen){
						return false;
					}
				}
			}else{//优先程序分析   -- 根据程序自动设置雷值
				System.out.println("优先程序分析");
				System.out.println("用于比较的雷值："+Integer.parseInt(lz));
				if(Integer.parseInt(lz) == fen){
					return false;
				}
			}

		}else{
			return false;   //此时不是最后一份
		}
		list.clear();
		return true;
	}

	//红包金额的余额（String）,雷值(int),当前已经抢的份数（String）,总份数（String）
		public int AA1(String yu,int lz,String outfs,String numfs){
			int outfsint = Integer.parseInt(outfs);
			int numfsint = Integer.parseInt(numfs);
			if(numfsint - outfsint == 1){//最后一份
				int money = (int)(Double.parseDouble(yu)*100);	 //最后一份的金额
				System.out.println(money);
				int yuan=(int)money/100;
				int jiao=(int)(money%100)/10;
				int fen=(int)(money%10);
				System.out.println(money+"：圆="+yuan+"圆"+jiao+"角"+fen+"分");
				//看尾数设置的是什么，
				if(Const.rbyouxian==1){//优先个人设置
					System.out.println("优先个人设置");
					if(Const.rb==1){//元
						System.out.println("个人设置的尾数（元）："+Const.ws);
						System.out.println("用于比较的雷值（元）："+yuan);
						if(Const.ws == yuan){
							return 2;
						}
					}else if(Const.rb==2){//角
						System.out.println("个人设置的尾数（角）："+Const.ws);
						System.out.println("用于比较的雷值（角）："+jiao);
						if(Const.ws == jiao){
							return 2;
						}
					}else{//分
						System.out.println("个人设置的尾数（分）："+Const.ws);
						System.out.println("用于比较的雷值（分）："+fen);
						if(Const.ws == fen){
							return 2;
						}
					}
				}else{//优先程序分析   -- 根据程序自动设置雷值
					System.out.println("优先程序分析");
					System.out.println("程序分析的尾数（分）："+lz);
					System.out.println("用于比较的雷值："+fen);
					if(lz==10||lz == fen){
						return 2;
					}
				}
			}else{
				return 1;   //此时不是最后一份
			}
			return 0;
		}
		
	public void BB(){	
		if(Const.switch5==false ||AA(list, "6", "4", "121.2")  ){  //Const.switch5==false 未开启排雷，直接抢
//		 try {
//			 if(Const.switch2==true){
//			     System.out.println("延时2秒...");
//				 Thread.currentThread().sleep(2000);//阻断2秒 
//			 }
//		        
//		 } catch (InterruptedException e) {
//		         e.printStackTrace();
//		 }
			//抢红包
			System.out.println("开始抢红包");
		}else{
			System.out.println("此时不抢");
			//此时不抢
		}
	}

	
	public void BB1(){	
		if(Const.switch5==false ||AA1("60.05", 4,"19","20")==0 ){  //Const.switch5==false 未开启排雷，直接抢
//		 try {
//			 if(Const.switch2==true){
//					System.out.println("延时2秒...");
//				 Thread.currentThread().sleep(2000);//阻断2秒 
//			 }
//		        
//		 } catch (InterruptedException e) {
//		         e.printStackTrace();
//		 }
			//抢红包
			System.out.println("开始抢红包");
		}else{
			System.out.println("此时不抢");
			//此时不抢
		}
	}


	


}
