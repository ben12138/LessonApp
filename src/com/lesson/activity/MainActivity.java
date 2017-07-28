package com.lesson.activity;

import com.example.lesson.R;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.fragment.AllCourseFragment;
import com.lesson.fragment.MainFragment;
import com.lesson.fragment.MyCourseFragment;
import com.lesson.fragment.MyInfFragment;
import com.lesson.fragment.TuiJianFragment;
import com.lesson.util.ActivityCollector;
import com.lesson.util.Utils;
import com.mysql.jdbc.Util;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static final int PRESSED = 0;
	public static final int NORMAL = 1;
	
	private LinearLayout isConnectedLinearLayout;
	private LinearLayout isNotConnectedLinearLayout;
	private LinearLayout reConnectLinearLayout;
	
	private Context context;
	private ImageView drawMenuHeadImageImageView;
	private TextView drawMenuNickNameTextView;
	private TextView drawMenuIntroductionTextView;
	private Button exitButton;
	private Button changeAccountButton;
	private LinearLayout homeLinearLayout;
	private ImageView homeImageView;
	private TextView homeTextView;
	private LinearLayout allCourseLinearLayout;
	private ImageView allCourseImageView;
	private TextView allcourseTextView;
	private LinearLayout myCourseLinearLayout;
	private ImageView myCourseImageView;
	private TextView myCourseTextView;
	private LinearLayout myInfLinearLayout;
	private ImageView myInfImageView;
	private TextView myInfTextView;

	private FragmentManager fragmentManager = null;
	private FragmentTransaction transaction = null;
	private MyInfFragment myinfFragment = null;
	private MainFragment mainFragment = null;
	private AllCourseFragment allCourseFragment = null;
	private MyCourseFragment myCourseFragment = null;
	
	public static final int CHANGESTATE = 0;
	public static final int CHANGEUSER = 1;
	public static Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}
	
	private void isNotConnectedInit(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		reConnectLinearLayout = (LinearLayout) findViewById(R.id.reconnect);
		reConnectLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = manager.getActiveNetworkInfo();
				if(info != null && info.isConnected()){
					//联网状态
					isNotConnectedLinearLayout.setVisibility(View.GONE);
					isConnectedLinearLayout.setVisibility(View.VISIBLE);
					init();
				}else{
					//断网状态
					isNotConnectedLinearLayout.setVisibility(View.VISIBLE);
					isConnectedLinearLayout.setVisibility(View.GONE);
					isNotConnectedInit();
				}
			}
		});
	}
	
	@SuppressLint("NewApi")
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		myinfFragment = new MyInfFragment();
		mainFragment = new MainFragment();
		allCourseFragment = new AllCourseFragment();
		myCourseFragment = new MyCourseFragment();
		ActivityCollector.addActivity(this);
		context = this;
		drawMenuHeadImageImageView = (ImageView) findViewById(R.id.drawer_menu_head_image);
		drawMenuHeadImageImageView.setImageBitmap(Utils.getBitmap(GOLBALVALUE.user.getHeadImage()));
		drawMenuIntroductionTextView = (TextView) findViewById(R.id.drawer_menu_introduction);
		String introduction = GOLBALVALUE.user.getIntroduction();
		if(introduction == null){
			drawMenuIntroductionTextView.setText("这个人很懒，什么也没有留下");
		}else{
			drawMenuIntroductionTextView.setText(introduction);
		}
		drawMenuNickNameTextView = (TextView) findViewById(R.id.drawer_menu_nick_name);
		String nickname = GOLBALVALUE.user.getNickname();
		if(nickname == null){
			drawMenuNickNameTextView.setText(GOLBALVALUE.user.getEmail());
		}else{
			drawMenuNickNameTextView.setText(nickname);
		}
		exitButton = (Button) findViewById(R.id.exit);
		exitButton.setOnClickListener(exitButtonOnClickListener);
		changeAccountButton = (Button) findViewById(R.id.change_account);
		changeAccountButton.setOnClickListener(changeAccountButtonOnClickListener);
		homeLinearLayout = (LinearLayout) findViewById(R.id.home);
		homeLinearLayout.setOnClickListener(homeLinearLayoutOnClickListener);
		homeImageView = (ImageView) findViewById(R.id.home_imageview);
		homeTextView = (TextView) findViewById(R.id.home_textview);
		allCourseLinearLayout = (LinearLayout) findViewById(R.id.allcourse);
		allCourseLinearLayout.setOnClickListener(allCourseLinearLayoutOnClickListener);
		allCourseImageView = (ImageView) findViewById(R.id.allcourse_imageview);
		allcourseTextView = (TextView) findViewById(R.id.allcourse_textview);
		myCourseLinearLayout = (LinearLayout) findViewById(R.id.mycourse);
		myCourseLinearLayout.setOnClickListener(myCourseLinearLayoutOnClickListener);
		myCourseImageView = (ImageView) findViewById(R.id.mycourse_imageview);
		myCourseTextView = (TextView) findViewById(R.id.mycourse_textview);
		myInfLinearLayout = (LinearLayout) findViewById(R.id.my);
		myInfLinearLayout.setOnClickListener(myInfLinearLayoutOnClickListener);
		myInfImageView = (ImageView) findViewById(R.id.my_imageview);
		myInfTextView = (TextView) findViewById(R.id.my_textview);
		changeBottomState(PRESSED, NORMAL, NORMAL, NORMAL);
		Message msg = new Message();
		msg.what = CHANGESTATE;
		MainFragment.handler.sendMessage(msg);
		fragmentManager = getFragmentManager();
		transaction = fragmentManager.beginTransaction();
		if(!mainFragment.isAdded()){
			transaction.add(R.id.body_fragment,mainFragment);
		}else{
			transaction.hide(myinfFragment);
			transaction.show(mainFragment);
		}
		transaction.commit();
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == CHANGESTATE){
					drawMenuHeadImageImageView.setImageBitmap((Bitmap)msg.obj);
				}else if(msg.what == CHANGEUSER){
					drawMenuIntroductionTextView.setText(GOLBALVALUE.user.getIntroduction());
					drawMenuNickNameTextView.setText(GOLBALVALUE.user.getNickname());
				}
			}
		};
	}
	
	private OnClickListener exitButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			MainActivity.this.finish();
			ActivityCollector.removeActivity(MainActivity.this);
			Intent intent = new Intent(context,LoginActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener changeAccountButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			MainActivity.this.finish();
			ActivityCollector.removeActivity(MainActivity.this);
			Intent intent = new Intent(context,LoginActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener homeLinearLayoutOnClickListener = new OnClickListener() {
		
		@SuppressLint("NewApi")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			changeBottomState(PRESSED, NORMAL, NORMAL, NORMAL);
			Message msg = new Message();
			msg.what = CHANGESTATE;
			MainFragment.handler.sendMessage(msg);
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			if(!mainFragment.isAdded()){
				transaction.add(R.id.body_fragment,mainFragment);
			}else{
				transaction.hide(myinfFragment);
				transaction.hide(allCourseFragment);
				transaction.hide(myCourseFragment);
				transaction.show(mainFragment);
			}
			transaction.commit();
		}
	};
	
	private OnClickListener allCourseLinearLayoutOnClickListener = new OnClickListener() {
		
		@SuppressLint("NewApi")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			changeBottomState(NORMAL, PRESSED, NORMAL, NORMAL);
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			if(!allCourseFragment.isAdded()){
				transaction.add(R.id.body_fragment,allCourseFragment);
			}else{
				transaction.hide(mainFragment);
				transaction.hide(myinfFragment);
				transaction.hide(myCourseFragment);
				transaction.show(allCourseFragment);
			}
			transaction.commit();
		}
	};
	
	private OnClickListener myCourseLinearLayoutOnClickListener = new OnClickListener() {
		
		@SuppressLint("NewApi")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			changeBottomState(NORMAL, NORMAL, PRESSED, NORMAL);
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			if(!myCourseFragment.isAdded()){
				transaction.add(R.id.body_fragment,myCourseFragment);
			}else{
				transaction.hide(mainFragment);
				transaction.hide(allCourseFragment);
				transaction.hide(myinfFragment);
				transaction.show(myCourseFragment);
			}
			transaction.commit();
		}
	};
	
	private OnClickListener myInfLinearLayoutOnClickListener = new OnClickListener() {
		
		@SuppressLint("NewApi")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			changeBottomState(NORMAL, NORMAL, NORMAL, PRESSED);
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			if(!myinfFragment.isAdded()){
				transaction.add(R.id.body_fragment,myinfFragment);
			}else{
				transaction.hide(mainFragment);
				transaction.hide(allCourseFragment);
				transaction.hide(myCourseFragment);
				transaction.show(myinfFragment);
			}
			transaction.commit();
		}
	};
	/**
	 * 传入值来更改底部按钮的背景
	 * context.getResources().getColor取到真正的颜色值
	 * 
	 */
	@SuppressLint("ResourceAsColor")
	private void changeBottomState(int home,int allcourse ,int mycourse,int myinf){
		if(home == NORMAL){
			homeImageView.setImageResource(R.drawable.home_normal);
			homeTextView.setTextColor(context.getResources().getColor(R.color.gray));
		}else if(home == PRESSED){
			homeImageView.setImageResource(R.drawable.home_pressed);
			homeTextView.setTextColor(context.getResources().getColor(R.color.title_background));
		}
		if(allcourse == NORMAL){
			allCourseImageView.setImageResource(R.drawable.allcourse_normal);
			allcourseTextView.setTextColor(context.getResources().getColor(R.color.gray));
		}else if(allcourse == PRESSED){
			allCourseImageView.setImageResource(R.drawable.allcourse_pressed);
			allcourseTextView.setTextColor(context.getResources().getColor(R.color.title_background));
		}
		if (mycourse == NORMAL) {
			myCourseImageView.setImageResource(R.drawable.mycourse_normal);
			myCourseTextView.setTextColor(context.getResources().getColor(R.color.gray));
		} else if (mycourse == PRESSED) {
			myCourseImageView.setImageResource(R.drawable.mycourse_pressed);
			myCourseTextView.setTextColor(context.getResources().getColor(R.color.title_background));
		}
		if (myinf == NORMAL) {
			myInfImageView.setImageResource(R.drawable.my_normal);
			myInfTextView.setTextColor(context.getResources().getColor(R.color.gray));
		} else if (myinf == PRESSED) {
			myInfImageView.setImageResource(R.drawable.my_pressed);
			myInfTextView.setTextColor(context.getResources().getColor(R.color.title_background));
		}
	}
	
}
