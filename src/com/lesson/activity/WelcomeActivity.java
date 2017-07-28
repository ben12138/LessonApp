package com.lesson.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.lesson.R;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.bean.UserInf;
import com.lesson.databasehelper.MyDataBasesHelper;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

public class WelcomeActivity extends Activity{
	private Context context = null;
	private ImageView welcomeImageView = null;
	private MyDataBasesHelper dbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_layout);
		init();
	}
	
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		context = this;
		ActivityCollector.addActivity(this);
		welcomeImageView = (ImageView) findViewById(R.id.welcome);
		dbHelper = new MyDataBasesHelper(context, "user.db3", 1);
		welcomeImageView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
						"select * from user", new String[]{});
				List<UserInf> users = getUser(cursor);
				if(users.size() == 0){
					Intent intent  =new Intent(context,LoginActivity.class);
					startActivity(intent);
					finish();
				}else{
					GOLBALVALUE.user = users.get(users.size()-1);
					GOLBALVALUE.headimageUrl = NetConnectionDate.url+"/headImage/"+GOLBALVALUE.user.getEmail()+".png";
					Intent intent  =new Intent(context,MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}, 2000);
	}
	
	private List<UserInf> getUser(Cursor cursor){
		List<UserInf> users = new ArrayList<>();
		while (cursor.moveToNext()) {
			UserInf user = new UserInf();
			user.setUsername(cursor.getString(1));
			user.setBirthday(cursor.getString(8));
			user.setSex(cursor.getInt(6));
			user.setEmail(cursor.getString(5));
			user.setHeadImage(cursor.getString(4));
			user.setIntroduction(cursor.getString(9));
			user.setSchool(cursor.getString(7));
			user.setNickname(cursor.getString(3));
			user.setPassword(cursor.getString(2));
			users.add(user);
		}

		return users;
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
