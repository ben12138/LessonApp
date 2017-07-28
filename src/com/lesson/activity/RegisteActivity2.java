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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisteActivity2 extends Activity{
	
	private Context context;
	private Button backButton;
	private EditText yanzhengmaEditText;
	private Button checkButton;
	private TextView emailTextView;
	private String yanzhengma;
	private String email;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registe2_layout);
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
		backButton.setOnClickListener(backButtonOnclickListener);
		yanzhengmaEditText = (EditText) findViewById(R.id.yanzhengma);
		checkButton = (Button) findViewById(R.id.check);
		checkButton.setOnClickListener(checkButtonOnclickListener);
		emailTextView = (TextView) findViewById(R.id.email);
		Intent intent = getIntent();
		email = intent.getStringExtra("email");
		yanzhengma = intent.getStringExtra("yanzhengma");
		emailTextView.setText(email);
	}
	
	private OnClickListener backButtonOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(RegisteActivity2.this);
			RegisteActivity2.this.finish();
		}
	};
	
	private OnClickListener checkButtonOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String inputyanzhengma = yanzhengmaEditText.getText().toString().trim();
			if(inputyanzhengma == null || inputyanzhengma.equals("")){
				Toast.makeText(context, "请输入验证码", Toast.LENGTH_SHORT).show();
			}else if(inputyanzhengma.equals(yanzhengma)){
				Intent intent = new Intent(context,RegisterActivity3.class);
				intent.putExtra("email", email);
				startActivity(intent);
			}else {
				Toast.makeText(context, "验证码有误", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		ActivityCollector.removeActivity(this);
		this.finish();
	}
	
}
