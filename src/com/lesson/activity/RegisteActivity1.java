package com.lesson.activity;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lesson.R;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;

import android.app.Activity;
import android.app.Notification.Action;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisteActivity1 extends Activity{
	private Context context;
	private EditText emailEditText;
	private Button emailButton;
	private Button backButton;
	private HttpClient client;
	private String yanzhengma;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registe1_layout);
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
		emailEditText  =(EditText) findViewById(R.id.email);
		emailButton = (Button) findViewById(R.id.confirm);
		emailButton.setOnClickListener(emailButtonOnclickLIstener);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnclickListener);
	}
	
	private OnClickListener emailButtonOnclickLIstener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String email = emailEditText.getText().toString().trim();
			if(email == null || email.equals("")){
				Toast.makeText(context, "请输入邮箱", Toast.LENGTH_SHORT).show();
			}
			if(isEmail(email)){
				getYanZhengMa(email);
			}else{
				Toast.makeText(context, "请输入正确的邮箱地址", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private OnClickListener backButtonOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(RegisteActivity1.this);
			RegisteActivity1.this.finish();
		}
	};
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		ActivityCollector.removeActivity(this);
		RegisteActivity1.this.finish();
	}
	
	private void getYanZhengMa(final String email){
		String url = NetConnectionDate.url+"RegisterServlet?email="+email+"&type=step1";
		new AsyncTask<String, Void, Integer>() {
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected Integer doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpGet get = new HttpGet(urlString);
				try {
					HttpResponse response = client.execute(get);
					JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
					yanzhengma = json.getString("yanzhengma");
					if(yanzhengma.length() == 4){
						return 1;
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return 2;
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result == 0){
					Toast.makeText(context, "糟糕，网络开小差了", Toast.LENGTH_SHORT).show();
				}else if(result == 1){
					Intent intent = new Intent(context,RegisteActivity2.class);
					intent.putExtra("email", email);
					intent.putExtra("yanzhengma", yanzhengma);
					startActivity(intent);
				}else if(result == 2){
					Toast.makeText(context, "该邮箱已被注册，请重新填写", Toast.LENGTH_SHORT).show();
				}
			}
			
		}.execute(url);
	}
	
	public boolean isEmail(String email) {
		String format = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		if (email.matches(format)) {
			return true;
		}
		return false;
	}
	
}
