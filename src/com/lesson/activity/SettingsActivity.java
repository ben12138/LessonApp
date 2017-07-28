package com.lesson.activity;

import com.example.lesson.R;
import com.lesson.util.ActivityCollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class SettingsActivity extends Activity{
	
	private Context context;
	private LinearLayout aboutLinearLayout;
	private LinearLayout exitLinearLayout;
	private Button backButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings_layout);
		init();
	}
	
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		ActivityCollector.addActivity(this);
		context = this;
		aboutLinearLayout = (LinearLayout) findViewById(R.id.about);
		aboutLinearLayout.setOnClickListener(aboutLinearLayoutOnClickListener);
		exitLinearLayout = (LinearLayout) findViewById(R.id.exit);
		exitLinearLayout.setOnClickListener(exitLinearLayoutOnClickListener);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
	}
	
	private OnClickListener aboutLinearLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,AboutActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener exitLinearLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.finishAll();
			Intent intent = new Intent(context,LoginActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(SettingsActivity.this);
			SettingsActivity.this.finish();
		}
	};
	
	public void onBackPressed() {
		ActivityCollector.removeActivity(SettingsActivity.this);
		SettingsActivity.this.finish();
	};
	
}
