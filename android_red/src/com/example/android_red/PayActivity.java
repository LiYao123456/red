package com.example.android_red;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PayActivity extends Activity {

	Button btnfanhui;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		btnfanhui = (Button) findViewById(R.id.fanhui);
		btnfanhui.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// ·µ»Ø
						Intent in = getIntent();
						setResult(RESULT_OK, in);
						finish();
					}
				});
	}
}
