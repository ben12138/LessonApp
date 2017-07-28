package com.lesson.activity;

import com.example.lesson.R;
import com.lesson.fragment.ClassificationFragment;
import com.lesson.util.ActivityCollector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class AllClassesActivity extends Activity{
	
	private Context context;
	
	private Button backButton;
	private ImageView searchImageView;
	private ClassificationFragment classificationFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.all_classes_layout);
		init();
	}
	
	@SuppressLint("NewApi")
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		ActivityCollector.addActivity(this);
		context = this;
		Intent intent = getIntent();
		int selected = intent.getIntExtra("selected", 0);
		System.out.println("activity "+selected);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
		searchImageView = (ImageView) findViewById(R.id.search);
		searchImageView.setOnClickListener(searchImageViewOnClickListener);
		classificationFragment = new ClassificationFragment(selected);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if(!classificationFragment.isAdded()){
			transaction.add(R.id.all_course_classes_fragment,classificationFragment);
		}else{
			transaction.show(classificationFragment);
		}
		transaction.commit();
	}
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(AllClassesActivity.this);
			AllClassesActivity.this.finish();
		}
	};
	
	private OnClickListener searchImageViewOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,SearchCourseActivity.class);
			startActivity(intent);
		}
	};
	
	public void onBackPressed() {
		ActivityCollector.removeActivity(AllClassesActivity.this);
		AllClassesActivity.this.finish();
	};
	
}
