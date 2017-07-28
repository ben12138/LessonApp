package com.lesson.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.lesson.R;
import com.lesson.bean.Comments;
import com.lesson.util.ActivityCollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsActivity2 extends Activity{
	
	private Context context;
	private Button backButton;
	private ImageView headIMageImageView;
	private TextView nicknameTextView;
	private TextView contentTextView;
	private TextView sendtimeTextView;
	
	private Bitmap bitmap;
	
	private Handler handler;
	
	private static final int CHANGEHEADIMAGE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comments2_layout);
		init();
	}
	private void init(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 透明状态栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 透明导航栏
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	    }
		Intent intent = getIntent();
		Comments comment = (Comments) intent.getSerializableExtra("comment");
		context = this;
		ActivityCollector.addActivity(this);
		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClickListener);
		headIMageImageView = (ImageView) findViewById(R.id.sender_headimage);
		nicknameTextView = (TextView) findViewById(R.id.sender_nickname);
		nicknameTextView.setText(comment.getSenderNickName());
		contentTextView = (TextView) findViewById(R.id.content);
		contentTextView.setText(comment.getContent());
		sendtimeTextView = (TextView) findViewById(R.id.send_time);
		sendtimeTextView.setText(comment.getSendtime());
		loadImage(comment.getSenderheadImage());
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == CHANGEHEADIMAGE){
					headIMageImageView.setImageBitmap(bitmap);
				}
			}
		};
	}
	
	private OnClickListener backButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ActivityCollector.removeActivity(CommentsActivity2.this);
			CommentsActivity2.this.finish();
		}
	};
	
	public void onBackPressed() {
		ActivityCollector.removeActivity(CommentsActivity2.this);
		CommentsActivity2.this.finish();
	};
	
	public Bitmap loadImage(final String imageUrl){
		new Thread(){
			public void run() {
				URL url;
				try {
					url = new URL(imageUrl);
					InputStream is = url.openStream();
					bitmap = BitmapFactory.decodeStream(is);
					Message msg = new Message();
					msg.what = CHANGEHEADIMAGE;
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
	
}
