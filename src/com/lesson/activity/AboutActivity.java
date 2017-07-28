package com.lesson.activity;

import com.example.lesson.R;
import com.lesson.util.ActivityCollector;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends Activity{
	
	private Context context;
	private Button backButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_layout);
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
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
	}
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(AboutActivity.this);
			AboutActivity.this.finish();
		}
	};
	
	public void onBackPressed() {
		ActivityCollector.removeActivity(AboutActivity.this);
		AboutActivity.this.finish();
	};
	
}
