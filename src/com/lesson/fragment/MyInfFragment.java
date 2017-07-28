package com.lesson.fragment;

import com.example.lesson.R;
import com.lesson.activity.ChangeInfActivity1;
import com.lesson.activity.CommentsActivity1;
import com.lesson.activity.MyDownLoadActivity;
import com.lesson.activity.SettingsActivity;
import com.lesson.bean.GOLBALVALUE;
import com.lesson.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MyInfFragment extends Fragment {
	
	private ImageView backgroundImageView;
	private ImageView headimageImageView;
	private TextView nicknameTextView;
	private LinearLayout personalInfLinearLayout;
	private LinearLayout commentsLinearLayout;
	private LinearLayout settingLinearLayout;
	private LinearLayout myDownLoadsLinearLayout;
	public static Handler handler;
	
	public static final int CHANGESTATE = 0;
	public static final int CHANGEUSER = 1;
	
	private Context context;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.myinf_layout, container,false);
		init(view);
		return view;
	}
	private void init(View view){
		nicknameTextView = (TextView) view.findViewById(R.id.nickname);
		nicknameTextView.setText(GOLBALVALUE.user.getNickname());
		backgroundImageView = (ImageView) view.findViewById(R.id.background);
		headimageImageView = (ImageView) view.findViewById(R.id.headimage);
        headimageImageView.setImageBitmap(Utils.getBitmap(GOLBALVALUE.user.getHeadImage()));
        personalInfLinearLayout = (LinearLayout) view.findViewById(R.id.personal_inf);
        personalInfLinearLayout.setOnClickListener(personalInfLinearLayoutOnCLickListener);
        commentsLinearLayout = (LinearLayout) view.findViewById(R.id.comments);
        commentsLinearLayout.setOnClickListener(commentsLinearLayoutOnClickListener);
        settingLinearLayout = (LinearLayout) view.findViewById(R.id.settings);
        settingLinearLayout.setOnClickListener(settingLinearLayoutOnClickListener);
        myDownLoadsLinearLayout = (LinearLayout) view.findViewById(R.id.downloadcourses);
        myDownLoadsLinearLayout.setOnClickListener(myDownLoadsLinearLayoutOnClickListener);
        handler = new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        		// TODO Auto-generated method stub
        		super.handleMessage(msg);
        		if(msg.what == CHANGESTATE){
        			headimageImageView.setImageBitmap((Bitmap)msg.obj);
        		}else if(msg.what == CHANGEUSER){
        			nicknameTextView.setText(GOLBALVALUE.user.getNickname());
        		}
        	}
        };
	}
	private OnClickListener personalInfLinearLayoutOnCLickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,ChangeInfActivity1.class);
			startActivity(intent);
		}
	};
	private OnClickListener commentsLinearLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,CommentsActivity1.class);
			startActivity(intent);
		}
	};
	private OnClickListener settingLinearLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,SettingsActivity.class);
			startActivity(intent);
		}
	};
	private OnClickListener myDownLoadsLinearLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,MyDownLoadActivity.class);
			startActivity(intent);
		}
	};
}
