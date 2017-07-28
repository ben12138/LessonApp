package com.lesson.activity;

import com.example.lesson.R;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.view.MyVideoView;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class FullScreenActivity extends Activity{
	private MyVideoView videoView;
	private LinearLayout controllerLinearLayout;
	private ImageView pauseImageView;
	private TextView currentTimeTextView;
	private TextView totalTimeTextView;
	private SeekBar playSeekBar;
	private SeekBar volumeSeekBar;
	private ImageView screenImageView;
	private static final int UPDATE_UI = 1;
	private Context context;
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
	private boolean isBottomShow = true;
	private LinearLayout bottomOperateLinearLayout;
	private LinearLayout videoViewControlLinearLayout;
	private LinearLayout topControlLinearLayout;
	private ImageView backImageView;
	private TextView classNameTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_layout);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		String courseurl = getIntent().getStringExtra("courseurl");
		String coursename = getIntent().getStringExtra("coursename");
		init(coursename);
		setPlayerEvent();
		/**
		 * 视频播放，网络视频
		 */
		videoView.setVideoURI(Uri.parse(courseurl));
		videoView.start();
		handler.sendEmptyMessage(UPDATE_UI);
	}
	
	private void init(String coursename){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		context = this;
		backImageView = (ImageView) findViewById(R.id.back);
		classNameTextView = (TextView) findViewById(R.id.class_name);
		classNameTextView.setText(coursename);
		topControlLinearLayout = (LinearLayout) findViewById(R.id.top_control);
		topControlLinearLayout.bringToFront();
		videoViewControlLinearLayout = (LinearLayout) findViewById(R.id.video_view_control);
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
		System.out.println("width:"+width+"height:"+height);
		rightLayout = (LinearLayout) findViewById(R.id.right_layout);
		middleLayout = (LinearLayout) findViewById(R.id.middle_layout);
		leftLinearLayout = (LinearLayout) findViewById(R.id.left_layout);
		videoRelativeLayout = (RelativeLayout) findViewById(R.id.video_layout);
		Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
		int ori = mConfiguration.orientation ; //获取屏幕方向
			//横屏
			LayoutParams params = rightLayout.getLayoutParams();
			params.width = width/6;
			rightLayout.setLayoutParams(params);
			params = middleLayout.getLayoutParams();
			params.width = width*4/6;
			middleLayout.setLayoutParams(params);
	}
	
	private void updateTextViewWithTimeFormat(TextView textView,int millisecond){
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
						controllerLinearLayout.setVisibility(View.GONE);
						topControlLinearLayout.setVisibility(View.GONE);
						isBottomShow = false;
					}else{
						controllerLinearLayout.setVisibility(View.VISIBLE);
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
				FullScreenActivity.this.finish();
			}
		});
		
	}
	
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
	
	private Handler handler = new Handler(){
		
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
			}
		};
		
	};
	
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
			FullScreenActivity.this.finish();
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED );
		}
	};
	
}
