package com.android_red.Thread;

import android.view.accessibility.AccessibilityNodeInfo;

public class Tobottom extends Thread{
	
	private AccessibilityNodeInfo redInfo;
	
	
public Tobottom(AccessibilityNodeInfo redInfo) {
		super();
		this.redInfo = redInfo;
	}


public void run() {
	try {
		sleep(500);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	redInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
	super.run();
}
}
