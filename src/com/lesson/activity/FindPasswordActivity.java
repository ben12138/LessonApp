package com.lesson.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.lesson.R;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;
import com.lesson.util.Utils;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.util.Base64Decoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FindPasswordActivity extends Activity{
	
	private Context context = null;
	private EditText emailEditText = null;
	private Button confirmButton = null;
	private EditText yanzhengmaEditText = null;
	private Button confirmYanzhengmaButton = null;
	private ImageView headImageImageView = null;
	private TextView tipTextView = null;
	private Button backButton = null;
	private TextView nicknameTextView = null;
	private String email = "";
	private HttpClient client = null;
	private String yanzhengma = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.find_password_layout);
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
		emailEditText = (EditText) findViewById(R.id.email);
		confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(confirmButtonOnClickListener);
		yanzhengmaEditText = (EditText) findViewById(R.id.yanzhengma);
		yanzhengmaEditText.setVisibility(View.GONE);
		confirmYanzhengmaButton = (Button) findViewById(R.id.confirm_yanzhengma);
		confirmYanzhengmaButton.setVisibility(View.GONE);
		headImageImageView = (ImageView) findViewById(R.id.head_image);
		headImageImageView.setVisibility(View.GONE);
		tipTextView = (TextView) findViewById(R.id.tip);
		tipTextView.setVisibility(View.GONE);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
		nicknameTextView = (TextView) findViewById(R.id.nickName);
		nicknameTextView.setVisibility(View.GONE);
	}
	
	private OnClickListener confirmButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			email = emailEditText.getText().toString().trim();
			if(email == null || email.equals("")){
				Toast.makeText(context, "请输入邮箱", Toast.LENGTH_SHORT).show();
			}else if(isEmail(email)){
				submitEmail(email);
			}else{
				Toast.makeText(context, "请输入正确的邮箱格式", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private OnClickListener confirmYanzhengmaButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String inputYanzhengma = yanzhengmaEditText.getText().toString().trim();
			if(inputYanzhengma == null || inputYanzhengma.equals("")){
				Toast.makeText(context, "请输入验证码", Toast.LENGTH_SHORT).show();
			}
			if(inputYanzhengma.equals(yanzhengma.trim())){
				getUserInf();
			}else{
				Toast.makeText(context, "验证码有误", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private OnClickListener headImageImageViewOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,MainActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(FindPasswordActivity.this);
			FindPasswordActivity.this.finish();
		}
	};
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		ActivityCollector.removeActivity(this);
		FindPasswordActivity.this.finish();
	}
	
	private void submitEmail(String email){
		String url = NetConnectionDate.url+"FindPasswordServlet?email="+email+"&type=getYanzhengma";
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
				HttpGet get = new HttpGet(urlString);
				client = new DefaultHttpClient();
				try {
					HttpResponse response = client.execute(get);
					yanzhengma = EntityUtils.toString(response.getEntity());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return 0;
				}
				return 1;
			}
			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				switch(result){
				case 0:Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();break;
				case 1://验证码获取到，输入验证码和验证按钮设置可见,获取验证码不可再次点击
					confirmButton.setClickable(false);
					confirmButton.setBackgroundColor(Color.GRAY);
					emailEditText.setEnabled(false);
					yanzhengmaEditText.setVisibility(View.VISIBLE);
					confirmYanzhengmaButton.setVisibility(View.VISIBLE);
					nicknameTextView.setVisibility(View.VISIBLE);
					confirmYanzhengmaButton.setOnClickListener(confirmYanzhengmaButtonOnClickListener);
					break;
				default:break;
				}
			}
		}.execute(url);
	}
	
	private void getUserInf(){
		String url = NetConnectionDate.url+"FindPasswordServlet?email="+email+"&type=getUserInf";
		new AsyncTask<String, Void, String>(){
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				HttpGet get = new HttpGet(urlString);
				client = new DefaultHttpClient();
				try {
					HttpResponse response = client.execute(get);
					String str = EntityUtils.toString(response.getEntity());
					return str;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				JSONObject json;
				try {
					json = new JSONObject(result);
					GOLBALVALUE.user.setBirthday(json.getString("birthday"));
					GOLBALVALUE.user.setEmail(json.getString("email"));
					GOLBALVALUE.user.setIntroduction(json.getString("introduction"));
					if(json.getString("nickname") == null){
						GOLBALVALUE.user.setNickname(json.getString("email"));
					}else{
						GOLBALVALUE.user.setNickname(json.getString("nickname"));
					}
					GOLBALVALUE.user.setSchool(json.getString("school"));
					GOLBALVALUE.user.setSex(Integer.parseInt(json.getString("sex")));
					GOLBALVALUE.user.setUsername(json.getString("username"));
					GOLBALVALUE.headimageUrl = json.getString("headimage").toString().trim();
					GOLBALVALUE.user.setHeadImage(Environment.getExternalStorageDirectory()+ "/Lesson/headImage/"+json.getString("email")+".png");
					headImageImageView.setVisibility(View.VISIBLE);
					headImageImageView.setOnClickListener(headImageImageViewOnClickListener);
					tipTextView.setVisibility(View.VISIBLE); 
					confirmYanzhengmaButton.setEnabled(false);
					confirmYanzhengmaButton.setBackgroundColor(Color.GRAY);
					yanzhengmaEditText.setEnabled(false);
					loadImageVolly(json.getString("headimage"));
					nicknameTextView.setText(json.getString("nickname"));
					Toast.makeText(context, "密码已发送至"+email+"请查看", Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
	public void loadImageVolly(String imageUrl){
		RequestQueue requsetQueue = Volley.newRequestQueue(this);
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		//缓存操作
		final LruCache<String, Bitmap> lurcache = new LruCache<String, Bitmap>(20);
		ImageCache imageCache = new ImageCache() {
			
			@Override
			public void putBitmap(String key, Bitmap value) {
				// TODO Auto-generated method stub
				lurcache.put(key, value);
			}
			
			@Override
			public Bitmap getBitmap(String key) {
				// TODO Auto-generated method stub
				return lurcache.get(key);
			}
		};
		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		ImageListener listener = ImageLoader.getImageListener(headImageImageView, R.drawable.ic_launcher, R.drawable.ic_launcher);
		imageLoader.get(imageUrl, listener);
	}
	
}
