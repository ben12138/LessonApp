package com.lesson.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.VideoView;

public class MyVideoView extends VideoView{
	
	int default_width = 1920;
	int default_height = 1080;

	public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getDefaultSize(default_width, widthMeasureSpec);
		int height = getDefaultSize(default_height, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
	
}
