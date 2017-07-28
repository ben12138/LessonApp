package com.lesson.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
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
import com.lesson.databasehelper.MyDataBasesHelper;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;
import com.lesson.util.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * 密码框的自动获取还待修改
 * @author 19339
 *
 */
public class LoginActivity extends Activity{
	
	private Context context = null;
	private EditText userNameEditText = null;
	private EditText passwordEditText = null;
	private Button loginButton = null;
	private Button registeButton = null;
	private Button findpasswordButton = null;
	private String userName = "";
	private String password = "";
	private LinearLayout user;
	private HttpClient client;
	private ImageView headImageImageView;
	private Bitmap bitmap;
	private Handler handler;
	private String email;
	
	private MyDataBasesHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
		init();
		initEvents();
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
		user = (LinearLayout) findViewById(R.id.login_user);
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		registeButton = (Button) findViewById(R.id.register);
		registeButton.setOnClickListener(registeButtonOnClickListener);
		findpasswordButton = (Button) findViewById(R.id.find_password);
		findpasswordButton.setOnClickListener(findpasswordOnClickListsner);
		loginButton = (Button) findViewById(R.id.login);
		loginButton.setOnClickListener(loginButtonOnClickListener);
		headImageImageView = (ImageView) findViewById(R.id.imageview);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				headImageImageView.setImageBitmap(bitmap);
				Utils.savePhoto(bitmap, Environment.getExternalStorageDirectory()+ "/Lesson/headImage/", email);
			}
		};
		if(GOLBALVALUE.user.getHeadImage() != null){
			headImageImageView.setImageBitmap(Utils.getBitmap(GOLBALVALUE.user.getHeadImage()));
			userNameEditText.setText(GOLBALVALUE.user.getEmail());
			passwordEditText.setText("");
		}
	}
	private OnClickListener loginButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			userName = userNameEditText.getText().toString().trim();
			password = passwordEditText.getText().toString().trim();
			if(userName == null || userName.equals("")){
				Toast.makeText(context, "请输入用户名", Toast.LENGTH_SHORT).show();
			}
			if(password == null || password.equals("")){
				Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
			}else{
				trylogin();
			}
		}
	};
	
	private OnClickListener registeButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,RegisteActivity1.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener findpasswordOnClickListsner = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,FindPasswordActivity.class);
			startActivity(intent);
		}
	};
	
	public void initEvents() {
		Animation anim = AnimationUtils.loadAnimation(context,
				R.anim.login_anim);
		anim.setFillAfter(true);
		user.startAnimation(anim);
		loginButton.startAnimation(anim);
		registeButton.startAnimation(anim);
		findpasswordButton.startAnimation(anim);
	}
	
	private void trylogin(){
		String url = NetConnectionDate.url+"LoginServlet?username="+userName+"&password="+password+"&device=phone";
		new AsyncTask<String, Void, Integer>() {
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				Toast.makeText(context, "正在登陆", Toast.LENGTH_SHORT).show();
			}
			@Override
			protected Integer doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				HttpGet get = new HttpGet(urlString);
				client = new DefaultHttpClient();
				String json = null;
				try {
					HttpResponse response = client.execute(get);
					json = EntityUtils.toString(response.getEntity());
					System.out.println(json);
					try {
						JSONObject jsonobj = new JSONObject(json);
						String result = jsonobj.getString("result");
						System.out.println(result);
						if(result.equals("success")){
							GOLBALVALUE.user.setBirthday(jsonobj.getString("birthday"));
							GOLBALVALUE.user.setEmail(jsonobj.getString("email"));
							GOLBALVALUE.user.setIntroduction(jsonobj.getString("introduction"));
							if(jsonobj.getString("nickname") == null){
								GOLBALVALUE.user.setNickname(jsonobj.getString("email"));
							}else{
								GOLBALVALUE.user.setNickname(jsonobj.getString("nickname"));
							}
							GOLBALVALUE.user.setSchool(jsonobj.getString("school"));
							GOLBALVALUE.user.setSex(Integer.parseInt(jsonobj.getString("sex")));
							GOLBALVALUE.user.setUsername(jsonobj.getString("username"));
							email = jsonobj.getString("email");
							loadImage(jsonobj.getString("headimage"));
							GOLBALVALUE.headimageUrl = jsonobj.getString("headimage").toString().trim();
							GOLBALVALUE.user.setHeadImage(Environment.getExternalStorageDirectory()+ "/Lesson/headImage/"+jsonobj.getString("email")+".png");
							return 1;
						}else if(result.equals("username_not_exist")){
							return 2;
						}else if(result.equals("password_error")){
							return 3;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return 0;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return 0;
				}
				return 0;
			}
			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				switch(result){
				case 0:Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();break;
				case 1://登陆成功
					dbHelper = new MyDataBasesHelper(context, "user.db3", 1);
					insertUser();
					LoginActivity.this.finish();
					ActivityCollector.removeActivity(LoginActivity.this);
					Intent intent = new Intent(context,MainActivity.class);
					startActivity(intent);
					break;
				case 2:Toast.makeText(context, "用户名不存在", Toast.LENGTH_SHORT).show();break;
				case 3:Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();break;
				default:break;
				}
			}
		}.execute(url);
	}
	
	public Bitmap loadImage(final String imageUrl){
		new Thread(){
			public void run() {
				URL url;
				try {
					url = new URL(imageUrl);
					InputStream is = url.openStream();
					bitmap = BitmapFactory.decodeStream(is);
					Message msg = new Message();
					handler.sendMessage(msg);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		return bitmap;
	}
	
	private void insertUser(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.execSQL("insert into user values(null,?,?,?,?,?,?,?,?,?)", new String[]{
				GOLBALVALUE.user.getUsername(),
				GOLBALVALUE.user.getPassword(),
				GOLBALVALUE.user.getNickname(),
				GOLBALVALUE.user.getHeadImage(),
				GOLBALVALUE.user.getEmail(),
				GOLBALVALUE.user.getSex()+"",
				GOLBALVALUE.user.getSchool(),
				GOLBALVALUE.user.getBirthday(),
				GOLBALVALUE.user.getIntroduction()
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(dbHelper != null){
			dbHelper.close();
		}
	}
	
}
