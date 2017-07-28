package com.lesson.fragment;

import java.util.ArrayList;

import com.example.lesson.R;
import com.lesson.activity.SearchCourseActivity;
import com.lesson.bean.GOLBALVALUE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainFragment extends Fragment{
	
	public static final int PRESSED = 0;
	public static final int NORMAL = 1;
	
	private LinearLayout tuijianLinearLayout;
	private LinearLayout classificationLayout;
	private TextView tuijianTextView;
	private ImageView tuijianImageView;
	private TextView classificationTextView;
	private ImageView classificationImageView;
	private ImageView searchImageView;
	
	private FragmentManager fragmentManager = null;
	private FragmentTransaction transaction = null;
	private TuiJianFragment tuiJianFragment = null;
	private ClassificationFragment classificationFragment = null;
	private static View view1;
	public static Handler handler;
	
	public static final int CHANGESTATE = 0;
	public static final int CHANGEBUTTONSTATE = 1;
	
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
		if (view1 != null) {
	        ViewGroup parent = (ViewGroup) view1.getParent();
	        if (parent != null)
	            parent.removeView(view1);
	    }
		View view = inflater.inflate(R.layout.home_layout, container,false);
		tuiJianFragment = new TuiJianFragment();
		classificationFragment = new ClassificationFragment();
		fragmentManager = getFragmentManager();
		TuiJianFragment test = (TuiJianFragment) getFragmentManager().findFragmentById(R.id.tuijian_fragment);
		if(test!=null){
			getFragmentManager().beginTransaction().remove(test).commit();
		}else{
			getFragmentManager().beginTransaction().add(R.id.up_fragment, tuiJianFragment).commit();
		}
		searchImageView = (ImageView) view.findViewById(R.id.search);
		searchImageView.setOnClickListener(searchImageViewOnClickListener);
		tuijianLinearLayout = (LinearLayout) view.findViewById(R.id.tuijian);
		tuijianLinearLayout.setOnClickListener(tuijianLayoutOnClickListener);
		tuijianTextView = (TextView) view.findViewById(R.id.tuijian_textview);
		tuijianImageView = (ImageView) view.findViewById(R.id.tuijian_imageview);
		classificationLayout = (LinearLayout) view.findViewById(R.id.classification);
		classificationLayout.setOnClickListener(classificationOnClickListener);
		classificationImageView = (ImageView) view.findViewById(R.id.classification_imageview);
		classificationTextView = (TextView) view.findViewById(R.id.classification_textview);
		view1 = view;
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == CHANGESTATE){
					if(msg.arg1 == 1){
						changeButtonState(PRESSED, NORMAL);
					}else if (msg.arg1 == 2) {
						changeButtonState(NORMAL, PRESSED);
					}
				}
			}
		};
		return view;
	}
	
	private OnClickListener searchImageViewOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(context,SearchCourseActivity.class);
			context.startActivity(intent);
		}
	};
	
	private OnClickListener tuijianLayoutOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			changeButtonState(PRESSED, NORMAL);
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			GOLBALVALUE.HOME_TYPE = 1;
			if(!tuiJianFragment.isAdded()){
				transaction.add(R.id.up_fragment,tuiJianFragment);
			}else{
				transaction.hide(classificationFragment);
				transaction.show(tuiJianFragment);
			}
			ClassificationFragment test2 = (ClassificationFragment) getFragmentManager().findFragmentById(R.id.tuijian_fragment);
			if(test2!=null){
				System.out.println(test2);
				getFragmentManager().beginTransaction().remove(test2).commit();
			}
			transaction.commit();
		}
	};
	
	private OnClickListener classificationOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			changeButtonState(NORMAL, PRESSED);
			GOLBALVALUE.HOME_TYPE = 2;
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			if(!classificationFragment.isAdded()){
				transaction.add(R.id.up_fragment,classificationFragment);
			}else{
				transaction.hide(tuiJianFragment);
				transaction.show(classificationFragment);
			}
			transaction.commit();
		}
	};
	
	/**
	 * 更改点击图标的颜色变化
	 * @param tuijian
	 * @param classification
	 */
	private void changeButtonState(int tuijian,int classification){
		if(tuijian == NORMAL){
			tuijianTextView.setTextColor((getResources().getColor(R.color.light_title_background)));
			tuijianImageView.setBackgroundColor(getResources().getColor(R.color.title_background));
		}else if(tuijian == PRESSED){
			tuijianTextView.setTextColor((getResources().getColor(R.color.white_font)));
			tuijianImageView.setBackgroundColor(getResources().getColor(R.color.white_font));
		}
		if(classification == NORMAL){
			classificationTextView.setTextColor((getResources().getColor(R.color.light_title_background)));
			classificationImageView.setBackgroundColor(getResources().getColor(R.color.title_background));
		}else if(classification == PRESSED){
			classificationTextView.setTextColor((getResources().getColor(R.color.white_font)));
			classificationImageView.setBackgroundColor(getResources().getColor(R.color.white_font));
		}
	}
	
	@Override
	public void onDestroyView() {

		super.onDestroyView();
		System.out.println("ondestroy");
		// 内嵌的Fragment UserProfileFragment
		TuiJianFragment test = (TuiJianFragment) getFragmentManager().findFragmentById(R.id.tuijian_fragment);
		if(test != null){
			getFragmentManager().beginTransaction().remove(test).commit();
		}
		ClassificationFragment test2 = (ClassificationFragment) getFragmentManager().findFragmentById(R.id.tuijian_fragment);
		if(test2 != null){
			getFragmentManager().beginTransaction().remove(test2).commit();
		}
	}
}
