package com.lesson.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.lesson.R;
import com.lesson.adapter.CourseMenuAdapter;
import com.lesson.adapter.DownLoadsAdapter;
import com.lesson.bean.CourseInf;
import com.lesson.bean.CourseUrl;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.databasehelper.MyDataBasesHelper;
import com.lesson.databasehelper.MyDownLoadsDataBasesHelper;
import com.lesson.fragment.CourseCommentsFragment;
import com.lesson.fragment.CourseInfFragment;
import com.lesson.fragment.CourseMenuFragment;
import com.lesson.fragment.MyCourseFragment;
import com.lesson.fragment.TuiJianFragment;
import com.lesson.net.NetConnectionDate;
import com.lesson.util.ActivityCollector;
import com.lesson.util.DownLoadUtil;
import com.lesson.view.MyVideoView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class SpecificCourseActivity extends Activity{
	
	private Context context;
	
	private CourseInf course;
	
	private static MyVideoView videoView;
	private static LinearLayout controllerLinearLayout;
	private ImageView pauseImageView;
	private static TextView currentTimeTextView;
	private static TextView totalTimeTextView;
	private static SeekBar playSeekBar;
	private SeekBar volumeSeekBar;
	private ImageView screenImageView;
	private LinearLayout rightLayout;
	private LinearLayout middleLayout;
	private LinearLayout leftLinearLayout;
	private int screen_width,screen_height;
	private RelativeLayout videoRelativeLayout; 
	private AudioManager audioManager;
	private ImageView volumeImageView;
	private boolean isFullScreen = false;
	private boolean isAdjust = false;//防误触标志
	private int threshold = 0;//防误触
	float last_x = 0;
	float last_y = 0;
	private float mBrightness;
	private ImageView operationbgImageView;
	private TextView percentTextView;
	private LinearLayout progressLinearLayout;
	private static boolean isBottomShow = false;
	private LinearLayout bottomOperateLinearLayout;
	private RelativeLayout videoViewControlRelativeLayout;
	private LinearLayout topControlLinearLayout;
	private ImageView backImageView;
	private TextView classNameTextView;
	private static ImageView videoImageView;
	private static ImageView startPlayImageView;
	private TextView courseIntroductionTextView;
	private TextView courseMenuTextView;
	private TextView courseCommentsTextView;
	private LinearLayout mainLinearLayout;
	private Button submitButton;
	private TextView commentsTextView;
	private TextView testTextView;
	private ImageView downLoadImageView;
	
	private CourseInfFragment courseInfFragment;
	private CourseMenuFragment courseMenuFragment;
	private CourseCommentsFragment commentsFragment;
	private FragmentManager fragmentManager;
	private FragmentTransaction transaction;
	
	private static final int UPDATE_UI = 1;
	private static final int UPDATE_VIDEOIMAGEVIEW = 2;
	private static final int PRESSED = 3;
	private static final int NORMAL = 4;
	public static final int CHANGEVIDEOVIEW = 5;
	
	private boolean isMyCourse = false;
	
	private int notification_id;
	
	private List<CourseUrl> courseMenuList = new ArrayList<>();;
	
	private HttpClient client;
	
	private MyDownLoadsDataBasesHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		course = (CourseInf) intent.getSerializableExtra("course");
		GOLBALVALUE.course = course;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.specific_course_layout);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		init();
		setPlayerEvent();
		/**
		 * 视频播放，网络视频
		 */
//		videoView.setVideoURI(Uri.parse("http://192.168.191.1:8080/LessonServer/headImage/1.mp4"));
//		videoView.start();
		handler.sendEmptyMessage(UPDATE_UI);
	}
	
	@SuppressLint("NewApi")
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		context = this;
		ActivityCollector.addActivity(this);
		getcourseurl();
		courseMenuFragment = new CourseMenuFragment();
		courseInfFragment = new CourseInfFragment();
		commentsFragment = new CourseCommentsFragment();
		fragmentManager = getFragmentManager();
		CourseInfFragment test = (CourseInfFragment) getFragmentManager().findFragmentById(R.id.course_inf1);
		if(test!=null){
			getFragmentManager().beginTransaction().remove(test).commit();
		}else{
			getFragmentManager().beginTransaction().add(R.id.up_fragment, courseInfFragment).commit();
		}
		downLoadImageView = (ImageView) findViewById(R.id.download_iv);
		commentsTextView = (TextView) findViewById(R.id.course_comment);
		commentsTextView.setText("   ");
		commentsTextView.setClickable(false);
		submitButton = (Button) findViewById(R.id.submit);
		mainLinearLayout = (LinearLayout) findViewById(R.id.main);
		mainLinearLayout.setVisibility(View.VISIBLE);
		courseCommentsTextView = (TextView) findViewById(R.id.course_comments);
		courseIntroductionTextView = (TextView) findViewById(R.id.course_introduction);
		courseMenuTextView = (TextView) findViewById(R.id.course_menu);
		startPlayImageView = (ImageView) findViewById(R.id.start_play);
		startPlayImageView.bringToFront();
		videoImageView = (ImageView) findViewById(R.id.video_imageview);
		if(courseMenuList.size()>0){
			getVideoImageView(videoImageView, courseMenuList.get(0).getCourseurl());
		}else{
			loadImageVolly(videoImageView, GOLBALVALUE.course.getAndroidimage());
		}
		testTextView = (TextView) findViewById(R.id.test);
		backImageView = (ImageView) findViewById(R.id.back);
		classNameTextView = (TextView) findViewById(R.id.class_name);
		classNameTextView.setText(GOLBALVALUE.course.getCoursename());
		topControlLinearLayout = (LinearLayout) findViewById(R.id.top_control);
		topControlLinearLayout.bringToFront();
		videoViewControlRelativeLayout = (RelativeLayout) findViewById(R.id.video_view_control);
		bottomOperateLinearLayout = (LinearLayout) findViewById(R.id.bottom_operate);
		progressLinearLayout = (LinearLayout) findViewById(R.id.progress);
		progressLinearLayout.setVisibility(View.GONE);
		operationbgImageView = (ImageView) findViewById(R.id.operate_bg);
		percentTextView = (TextView) findViewById(R.id.operate_percent);
		videoView = (MyVideoView) findViewById(R.id.video_view);
		controllerLinearLayout = (LinearLayout) findViewById(R.id.controller_bar_layout);
		pauseImageView = (ImageView) findViewById(R.id.pause_img);
		screenImageView = (ImageView) findViewById(R.id.screen_img);
		screenImageView.setOnClickListener(screenImageOnClickListener);
		currentTimeTextView = (TextView) findViewById(R.id.time_current_textview);
		totalTimeTextView = (TextView) findViewById(R.id.time_total_textview);
		playSeekBar = (SeekBar) findViewById(R.id.play_seekbar);
		volumeSeekBar = (SeekBar) findViewById(R.id.volume_seekbar);
		playSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
				// TODO Auto-generated method stub
				int progress = seekbar.getProgress();
				videoView.seekTo(progress);
				handler.sendEmptyMessage(UPDATE_UI);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				// TODO Auto-generated method stub
				handler.removeMessages(UPDATE_UI);
			}
			
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				updateTextViewWithTimeFormat(currentTimeTextView, progress);
			}
		});
		//当前设备最大音量
		int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volumeSeekBar.setMax(streamMaxVolume);
		//获取设备当前音量
		int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		volumeImageView = (ImageView) findViewById(R.id.volume_img);
		volumeSeekBar.setProgress(streamVolume);
		volumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				//设置当前音量
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		});
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		screen_height = height;
		screen_width = width;
		rightLayout = (LinearLayout) findViewById(R.id.right_layout);
		middleLayout = (LinearLayout) findViewById(R.id.middle_layout);
		leftLinearLayout = (LinearLayout) findViewById(R.id.left_layout);
		videoRelativeLayout = (RelativeLayout) findViewById(R.id.video_layout);
		Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
		int ori = mConfiguration.orientation ; //获取屏幕方向
		if(ori == mConfiguration.ORIENTATION_LANDSCAPE){
			//横屏
			LayoutParams params = rightLayout.getLayoutParams();
			params.width = width/6;
			rightLayout.setLayoutParams(params);
			params = middleLayout.getLayoutParams();
			params.width = width*5/6;
			middleLayout.setLayoutParams(params);
		}else if(ori == mConfiguration.ORIENTATION_PORTRAIT){
			//竖屏
			LayoutParams params = rightLayout.getLayoutParams();
			params.width = width/3;
			rightLayout.setLayoutParams(params);
			params = middleLayout.getLayoutParams();
			params.width = width/3;
			middleLayout.setLayoutParams(params);
			params = videoView.getLayoutParams();
			params.width = width;
			params.height = height/3;
			videoView.setLayoutParams(params);
		}
		hascourse();
	}
	
	private static void updateTextViewWithTimeFormat(TextView textView,int millisecond){
		int second = millisecond/1000;
		int hour = second/3600;
		int minute = second%3600/60;
		int ss = second%60;
		String str = null;
		if(hour != 0){
			str = String.format("%02d:%02d:%02d",hour,minute,ss);
		}else{
			str = String.format("%02d:%02d",minute,ss);
		}
		textView.setText(str);
	}
	
	private void setPlayerEvent(){
		pauseImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(videoView.isPlaying()){
					pauseImageView.setImageResource(R.drawable.play_btn_style);
					videoView.pause();
					handler.removeMessages(UPDATE_UI);
				}else{
					pauseImageView.setImageResource(R.drawable.pause_btn_style);
					videoView.start();
					handler.sendEmptyMessage(UPDATE_UI);
				}
			}
		});
		
		/**
		 * 控制VideoView的手势事件
		 */
		videoView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float x = event.getX();
				float y = event.getY();
				switch(event.getAction()){
				/**
				 * 手指落下屏幕的那一刻，只会调用一次
				 */
				case MotionEvent.ACTION_DOWN:{
					last_x = x;
					last_y = y;
					if(isBottomShow){
						bottomOperateLinearLayout.setVisibility(View.GONE);
						topControlLinearLayout.setVisibility(View.GONE);
						isBottomShow = false;
					}else{
						bottomOperateLinearLayout.setVisibility(View.VISIBLE);
						topControlLinearLayout.setVisibility(View.VISIBLE);
						isBottomShow = true;
					}
					break;
				}
				/**
				 * 手指在屏幕上移动
				 */
				case MotionEvent.ACTION_MOVE:{
					float detX = x - last_x;
					float detY = y - last_y;
					float absdet_x = Math.abs(detX);
					float absdet_y = Math.abs(detY);
					if(x > screen_width/2 && last_x > screen_width/2){
						if(absdet_x < absdet_y && absdet_x < 54){
							if(detY > 0){
								changeVolume(-detY);
							}else{
								changeVolume(-detY);
							}
						}
					}
					if(x < screen_width/2 && last_x < screen_width/2){
						if(absdet_x < absdet_y && absdet_x < 54){
							if(detY > 0){
								changeBrightness(-detY);
							}else{
								changeBrightness(-detY);
							}
						}
					}
					if(absdet_y < absdet_x && absdet_y < 54){
						if(detX > 0){
							changeProgress(detX);
						}else{
							changeProgress(detX);
						}
					}
					break;
				}
				/**
				 * 手指离开屏幕那一刻
				 */
				case MotionEvent.ACTION_UP:{
					last_x = 0;
					last_y = 0;
					progressLinearLayout.setVisibility(View.GONE);
					break;
				}
				}
				return true;
			}
		});
		
		backImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SpecificCourseActivity.this.finish();
			}
		});
		
		startPlayImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				videoImageView.setVisibility(View.GONE);
				startPlayImageView.setVisibility(View.GONE);
				videoView.setVisibility(View.VISIBLE);
				if (courseMenuList.size() > 0) {
					videoView.setVideoURI(Uri
							.parse(courseMenuList.get(0).getCourseurl()));
					videoView.start();
					controllerLinearLayout.setVisibility(View.VISIBLE);
					isBottomShow = true;
				}
			}
		});
		
		courseMenuTextView.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeColor(NORMAL,PRESSED,NORMAL);
				fragmentManager = getFragmentManager();
				transaction = fragmentManager.beginTransaction();
				if(!courseMenuFragment.isAdded()){
					transaction.add(R.id.course_inf_all,courseMenuFragment);
				}else{
					transaction.hide(commentsFragment);
					transaction.hide(courseInfFragment);
					transaction.show(courseMenuFragment);
				}
				commentsTextView.setText("   ");
				transaction.commit();
			}
		});
		
		courseCommentsTextView.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeColor(NORMAL,NORMAL,PRESSED);
				fragmentManager = getFragmentManager();
				transaction = fragmentManager.beginTransaction();
				if(!commentsFragment.isAdded()){
					transaction.add(R.id.course_inf_all,commentsFragment);
				}else{
					transaction.hide(courseMenuFragment);
					transaction.hide(courseInfFragment);
					transaction.show(commentsFragment);
				}
				if (isMyCourse) {
					commentsTextView.setText("我来说两句");
					commentsTextView.setClickable(true);
				}
				transaction.commit();
			}
		});
		
		courseIntroductionTextView.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeColor(PRESSED,NORMAL,NORMAL);
				fragmentManager = getFragmentManager();
				transaction = fragmentManager.beginTransaction();
				if(!courseInfFragment.isAdded()){
					transaction.add(R.id.course_inf_all,courseInfFragment);
				}else{
					transaction.hide(commentsFragment);
					transaction.hide(courseMenuFragment);
					transaction.show(courseInfFragment);
				}
				commentsTextView.setText("   ");
				transaction.commit();
			}
		});
		
		commentsTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context ,SendCommentActivity.class);
				intent.putExtra("courseinfid", course.getId());
				startActivity(intent);
			}
		});
		
		testTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,CourseTestActivity.class);
				startActivity(intent);
			}
		});
		
		downLoadImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.download_popup_window,null,false);
				ListView downloadsListView = (ListView) view.findViewById(R.id.downloads);
				final PopupWindow popup = new PopupWindow(view,WindowManager.LayoutParams.MATCH_PARENT,800,true);
				ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
				popup.setBackgroundDrawable(dw);
				popup.setFocusable(true);
				popup.showAtLocation(findViewById(R.id.download_iv), Gravity.BOTTOM, 0, 0);
				popup.setAnimationStyle(R.style.mypopwindow_anim_style);
				popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				popup.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss() {
						popup.dismiss();
					}
				});
				DownLoadsAdapter adapter = new DownLoadsAdapter(context, R.layout.download_item, courseMenuList, downloadsListView);
				downloadsListView.setAdapter(adapter);
				Button downLoadButton = (Button) view.findViewById(R.id.start_download);
				downLoadButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						popup.dismiss();
						if(DownLoadsAdapter.myDownUrls.size() == 0){
							Toast.makeText(context, "请选择课程",
									Toast.LENGTH_SHORT).show();
						}else{
							File file = new File(Environment.getExternalStorageDirectory()+"/Lesson/downloadvideo/"+course.getCoursename());
							if(!file.exists()){
								file.mkdirs();
							}
							for(int j=0;j<DownLoadsAdapter.myDownUrls.size();j++){
								final int i = j;
								notification_id = i;
								System.out.println(DownLoadsAdapter.myDownUrls.get(i));
								File file1 = new File(Environment.getExternalStorageDirectory()+ "/Lesson/downloadvideo/"+course.getCoursename()+"/"+courseMenuList.get(i).getCoursename()+".mp4");
								if(file1.exists()){
									
								}else{
									Intent intent = new Intent(context,MainActivity.class);
									PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
									final Notification notify = new Notification(R.drawable.ic_launcher,"图标边的文字",System.currentTimeMillis());
									notify.contentView = new RemoteViews(getPackageName(),R.layout.down_item);
									notify.contentView.setProgressBar(R.id.pb, 100,0, false);
									notify.contentView.setTextViewText(R.id.coursename, courseMenuList.get(i).getCoursename());
									notify.contentView.setTextViewText(R.id.download_percent, "已下载0%");
									notify.contentIntent = pi;
									final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
									nm.notify(notification_id,notify);
									final DownLoadUtil downLoadUtil = new DownLoadUtil(DownLoadsAdapter.myDownUrls.get(i), Environment.getExternalStorageDirectory()+ "/Lesson/downloadvideo/"+course.getCoursename()+"/"+courseMenuList.get(i).getCoursename()+".mp4", 5);
									new Thread(){
										@Override
										public void run() {
											downLoadUtil.download();
											final Timer timer = new Timer();
											timer.schedule(new TimerTask() {
												
												@Override
												public void run() {
													// TODO Auto-generated method stub
													double completeRate = downLoadUtil.getCompleteRate()*100;
													int downloadStatus = (int) completeRate;
													if(downloadStatus >= 100){
														
														dbHelper = new MyDownLoadsDataBasesHelper(context, "downloads.db3", 1);
														SQLiteDatabase db = dbHelper.getReadableDatabase();
//														db.execSQL("create table downloads(_id integer primary key autoincrement,coursename varchar(200),path varchar(200))");
//														System.out.println(course.getCoursename()+"-"+courseMenuList.get(i).getCoursename());
//														System.out.println(Environment.getExternalStorageDirectory()+ "/Lesson/downloadvideo/"+course.getCoursename()+"/"+courseMenuList.get(i).getCoursename());
														ContentValues values = new ContentValues();
														values.put("coursename", course.getCoursename()+"-"+courseMenuList.get(i).getCoursename());
														values.put("path", Environment.getExternalStorageDirectory()+ "/Lesson/downloadvideo/"+course.getCoursename()+"/"+courseMenuList.get(i).getCoursename()+".mp4");
//														db.execSQL("insert into downloads values(null,?,?)", new String[]{
//																course.getCoursename()+"-"+courseMenuList.get(i).getCoursename(),
//																Environment.getExternalStorageDirectory()+ "/Lesson/downloadvideo/"+course.getCoursename()+"/"+courseMenuList.get(i).getCoursename()+".mp4"
//														});
														System.out
																.println(db.insert("downloads", null, values));
														db.close();
														dbHelper.close();
														timer.cancel();
													}
													notify.contentView.setProgressBar(R.id.pb, 100,downloadStatus, false);
													notify.contentView.setTextViewText(R.id.download_percent, "已下载"+downloadStatus+"%");
													nm.notify(notification_id, notify);
													try {
														Thread.sleep(1000);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}
											}, 0,100);
										};
									}.start();
								}
							}
						}
					}
				});
			}
		});
		
	}
	
	private OnClickListener cancelOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String url = NetConnectionDate.url+"/AddCourseServlet";
			new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					// TODO Auto-generated method stub
					String urlString = arg0[0];
					client = new DefaultHttpClient();
					HttpPost post = new HttpPost(urlString);
					List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
					list.add(new BasicNameValuePair("type", "deletecourse"));
					list.add(new BasicNameValuePair("username", GOLBALVALUE.user.getUsername()));
					list.add(new BasicNameValuePair("courseinfid", course.getId()+""));
					list.add(new BasicNameValuePair("device", "phone"));
					HttpEntity entity;
					try {
						entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);
						post.setEntity(entity);
						HttpResponse response = client.execute(post);
						String result = EntityUtils.toString(response.getEntity());
						return result.trim();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// 设置编码，防止中午乱码
					catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(String result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					System.out.println("add:"+result);
					if(result == null){
						Toast.makeText(context, "糟糕，网络出错了", Toast.LENGTH_SHORT).show();
					}else if(result.equals("success")){
						Toast.makeText(context, "取消成功", Toast.LENGTH_SHORT).show();
						submitButton.setText("立即报名");
						submitButton.setOnClickListener(submitOnClickListener);
						if (MyCourseFragment.handler != null) {
							Message msg = new Message();
							msg.what = MyCourseFragment.DELETECOURSE;
							msg.obj = course;
							MyCourseFragment.handler.sendMessage(msg);
						}
						isMyCourse = false;
						commentsTextView.setClickable(false);
					}else if(result.equals("failure")){
						Toast.makeText(context, "取消失败了", Toast.LENGTH_SHORT).show();
					}
				}
				
			}.execute(url);
		}
	};
	
	private OnClickListener submitOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String url = NetConnectionDate.url+"/AddCourseServlet";
			new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					// TODO Auto-generated method stub
					String urlString = arg0[0];
					client = new DefaultHttpClient();
					HttpPost post = new HttpPost(urlString);
					List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
					list.add(new BasicNameValuePair("type", "addcourse"));
					list.add(new BasicNameValuePair("username", GOLBALVALUE.user.getUsername()));
					list.add(new BasicNameValuePair("courseinfid", course.getId()+""));
					list.add(new BasicNameValuePair("device", "phone"));
					HttpEntity entity;
					try {
						entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);
						post.setEntity(entity);
						HttpResponse response = client.execute(post);
						String result = EntityUtils.toString(response.getEntity());
						return result.trim();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// 设置编码，防止中午乱码
					catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
				
				@Override
				protected void onPostExecute(String result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					System.out.println("add:"+result);
					if(result == null){
						Toast.makeText(context, "糟糕，网络出错了", Toast.LENGTH_SHORT).show();
					}else if(result.equals("success")){
						Toast.makeText(context, "报名成功", Toast.LENGTH_SHORT).show();
						submitButton.setText("取消报名");
						submitButton.setOnClickListener(cancelOnClickListener);
						if (MyCourseFragment.handler != null) {
							Message msg = new Message();
							msg.what = MyCourseFragment.ADDCOURSE;
							msg.obj = course;
							MyCourseFragment.handler.sendMessage(msg);
						}
						isMyCourse = true;
						commentsTextView.setClickable(true);
					}else if(result.equals("failure")){
						Toast.makeText(context, "报名失败了", Toast.LENGTH_SHORT).show();
						submitButton.setOnClickListener(submitOnClickListener);
					}
				}
				
			}.execute(url);
		}
	};
	
	private void changeVolume(float detY){
		int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int index = (int)((float)detY/screen_height*max);
		if(progressLinearLayout.getVisibility() == View.GONE){
			progressLinearLayout.setVisibility(View.VISIBLE);
		}
		int volume = Math.max(current + index, 0);
		volume = Math.min(volume, max);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		operationbgImageView.setImageResource(R.drawable.volume);
		percentTextView.setText((int)((float)volume/max*100)+"%");
	}
	
	private void changeBrightness(float detY){
		if(progressLinearLayout.getVisibility() == View.GONE){
			progressLinearLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams attributes = getWindow().getAttributes();
		mBrightness = attributes.screenBrightness;
		float index = detY/screen_height/3;
		mBrightness += index;
		if(mBrightness > 1.0f){
			mBrightness = 1.0f;
		}else if(mBrightness < 0.01f){
			mBrightness = 0.01f;
		}
		attributes.screenBrightness = mBrightness;
		getWindow().setAttributes(attributes);
		operationbgImageView.setImageResource(R.drawable.brightness);
		percentTextView.setText((int)(mBrightness*100)+"%");
	}
	
	private void changeProgress(float deltX){
		int current = videoView.getCurrentPosition();
		int max = playSeekBar.getMax();
		int index = (int)(deltX / screen_width*1000);
		int progress = index + current;
		videoView.seekTo(progress);
		playSeekBar.setProgress(progress);
		updateTextViewWithTimeFormat(currentTimeTextView, progress);
	}
	
	public static Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if(msg.what == UPDATE_UI){
				//获取当前时间和总时间
				int currentPosition = videoView.getCurrentPosition();
				int totalduration = videoView.getDuration();
				updateTextViewWithTimeFormat(currentTimeTextView, currentPosition);
				updateTextViewWithTimeFormat(totalTimeTextView, totalduration);
				playSeekBar.setMax(totalduration);
				playSeekBar.setProgress(currentPosition);
				handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
			}else if(msg.what == UPDATE_VIDEOIMAGEVIEW){
				videoImageView.setImageBitmap((Bitmap)msg.obj);
			}else if(msg.what == CHANGEVIDEOVIEW){
				videoImageView.setVisibility(View.GONE);
				startPlayImageView.setVisibility(View.GONE);
				videoView.setVisibility(View.VISIBLE);
				videoView.setVideoURI(Uri.parse((String)msg.obj));
				System.out.println((String)msg.obj);
				videoView.start();
				controllerLinearLayout.setVisibility(View.VISIBLE);
				isBottomShow = true;
			}
		};
		
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		handler.sendEmptyMessage(UPDATE_UI);
	};
	
	
	/**
	 * 监听到屏幕方向改变
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		screen_height = height;
		screen_width = width;
		Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
		int ori = mConfiguration.orientation ; //获取屏幕方向
		if(ori == mConfiguration.ORIENTATION_LANDSCAPE){
			//横屏
			LayoutParams params = rightLayout.getLayoutParams();
			params.width = width/6;
			rightLayout.setLayoutParams(params);
			params = middleLayout.getLayoutParams();
			params.width = width*4/6;
			middleLayout.setLayoutParams(params);
			setVideoViewScale(width, height);
			isFullScreen = true;
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}else if(ori == mConfiguration.ORIENTATION_PORTRAIT){
			//竖屏
			LayoutParams params = rightLayout.getLayoutParams();
			params.width = width/3;
			rightLayout.setLayoutParams(params);
			params = middleLayout.getLayoutParams();
			params.width = width*2/5;
			middleLayout.setLayoutParams(params);
			setVideoViewScale(width,height/3);
			params = videoRelativeLayout.getLayoutParams();
			params.width = screen_width;
			params.height = screen_height/3;
			videoRelativeLayout.setLayoutParams(params);
			isFullScreen = false;
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}
	
	private void setVideoViewScale(int width,int height){
		LayoutParams params = videoView.getLayoutParams();
		params.width = width;
		params.height = height;
		videoView.setLayoutParams(params);
		
		LayoutParams params1 = videoRelativeLayout.getLayoutParams();
		params1.width = width;
		params1.height = height;
		videoRelativeLayout.setLayoutParams(params1);
	}
	
	
	/**
	 * 横竖屏切换
	 */
	private OnClickListener screenImageOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (isFullScreen) {
				//竖屏
				WindowManager wm = (WindowManager) context
						.getSystemService(Context.WINDOW_SERVICE);
				int width = wm.getDefaultDisplay().getWidth();
				int height = wm.getDefaultDisplay().getHeight();
				screen_height = width;
				screen_width = height;
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				LayoutParams params = rightLayout.getLayoutParams();
				params.width = screen_width/3;
				rightLayout.setLayoutParams(params);
				params = middleLayout.getLayoutParams();
				params.width = screen_width*2/5+20;
				middleLayout.setLayoutParams(params);
				setVideoViewScale(screen_width,screen_height/5);
				params = videoRelativeLayout.getLayoutParams();
				params.width = screen_width;
				params.height = screen_height/3;
				videoRelativeLayout.setLayoutParams(params);
				mainLinearLayout.setVisibility(View.VISIBLE);
				isFullScreen = false;
			}else{
				//横屏
				WindowManager wm = (WindowManager) context
						.getSystemService(Context.WINDOW_SERVICE);
				int width = wm.getDefaultDisplay().getWidth();
				int height = wm.getDefaultDisplay().getHeight();
				screen_height = height;
				screen_width = width;
				System.out.println("Kuan:"+screen_width+"Gao:"+screen_height);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				LayoutParams params = rightLayout.getLayoutParams();
				params.width = screen_width/6;
				rightLayout.setLayoutParams(params);
				params = middleLayout.getLayoutParams();
				params.width = screen_width*4/6;
				middleLayout.setLayoutParams(params);
				setVideoViewScale(screen_width, screen_height);
				params = videoView.getLayoutParams();
				params.width = screen_width;
				params.height = screen_height/3;
				videoView.setLayoutParams(params);
				params = videoRelativeLayout.getLayoutParams();
				params.width = screen_width;
				params.height = screen_height/3;
				videoRelativeLayout.setLayoutParams(params);
				mainLinearLayout.setVisibility(View.GONE);
				isFullScreen = true;
			}
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED );
		}
	};
	
	private void getcourseurl(){
		String url = NetConnectionDate.url+"/GetCourseVideoServlet?courseinfid="+GOLBALVALUE.course.getId();
		System.out.println(url);
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String stringUrl = arg0[0];
				client = new DefaultHttpClient();
				HttpGet get = new HttpGet(stringUrl);
				try {
					HttpResponse response = client.execute(get);
					return EntityUtils.toString(response.getEntity());
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "null";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "null";
				}
			}
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(!result.equals("null")){
					JSONArray jsonarray = new JSONArray(result);
					for(int i=0;i<jsonarray.length();i++){
						JSONObject json = jsonarray.getJSONObject(i);
						CourseUrl course = new CourseUrl();
						course.setId(json.getInt("id"));
						course.setCourseinfid(json.getInt("courseinfid"));
						course.setCoursename(json.getString("coursename"));
						course.setCourseurl(json.getString("courseurl"));
						courseMenuList.add(course);
					}
				}
			}
		}.execute(url);
	}
	
	private void getVideoImageView(final ImageView videoImageView,final String url){
		new Thread(new Runnable() {
			
			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		        mmr.setDataSource(url, new HashMap<String, String>());

		        Bitmap bitmap = mmr.getFrameAtTime();//获取第一帧图片
		        mmr.release();//释放资源
		        Message msg = new Message();
		        msg.what = UPDATE_VIDEOIMAGEVIEW;
		        msg.obj = bitmap;
		        handler.sendMessage(msg);
		        
			}
		}).start();
	}
	
	private void changeColor(int introduction,int menu,int comments){
		if(introduction == PRESSED){
			courseIntroductionTextView.setTextColor((getResources().getColor(R.color.light_title_background)));
			courseMenuTextView.setTextColor((getResources().getColor(R.color.black)));
			courseCommentsTextView.setTextColor((getResources().getColor(R.color.black)));
		}else if(menu == PRESSED){
			courseIntroductionTextView.setTextColor((getResources().getColor(R.color.black)));
			courseMenuTextView.setTextColor((getResources().getColor(R.color.light_title_background)));
			courseCommentsTextView.setTextColor((getResources().getColor(R.color.black)));
		}else if(comments == PRESSED){
			courseIntroductionTextView.setTextColor((getResources().getColor(R.color.black)));
			courseMenuTextView.setTextColor((getResources().getColor(R.color.black)));
			courseCommentsTextView.setTextColor((getResources().getColor(R.color.light_title_background)));
		}
	}
	
	private void hascourse(){
		String url = NetConnectionDate.url+"/AddCourseServlet";
		new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String urlString = arg0[0];
				client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("type", "hascourse"));
				list.add(new BasicNameValuePair("username", GOLBALVALUE.user.getUsername()));
				list.add(new BasicNameValuePair("courseinfid", course.getId()+""));
				list.add(new BasicNameValuePair("device", "phone"));
				HttpEntity entity;
				try {
					entity = new UrlEncodedFormEntity(list,HTTP.UTF_8);
					post.setEntity(entity);
					HttpResponse response = client.execute(post);
					String result = EntityUtils.toString(response.getEntity());
					return result.trim();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 设置编码，防止中午乱码
				catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				System.out.println("has:"+result);
				if(result == null){
					Toast.makeText(context, "糟糕，网络出错了", Toast.LENGTH_SHORT).show();
				}else if(result.equals("has")){
					submitButton.setText("取消报名");
					submitButton.setOnClickListener(cancelOnClickListener);
					isMyCourse = true;
					commentsTextView.setClickable(true);
				}else if(result.equals("no")){
					submitButton.setText("立即报名");
					submitButton.setOnClickListener(submitOnClickListener);
					isMyCourse = false;
					commentsTextView.setClickable(false);
				}
			}
			
		}.execute(url);
	}
	
	public void loadImageVolly(ImageView imageview,String url){
		RequestQueue requsetQueue = Volley.newRequestQueue(this);
		String imageUrl = url;
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
		ImageListener listener = ImageLoader.getImageListener(imageview, R.drawable.ic_launcher, R.drawable.ic_launcher);
		imageLoader.get(imageUrl, listener);
	}
	
}
