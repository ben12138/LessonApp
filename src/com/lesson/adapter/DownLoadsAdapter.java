package com.lesson.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.lesson.R;
import com.lesson.bean.CourseInf;
import com.lesson.bean.CourseUrl;
import com.lesson.util.LoadImage;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class DownLoadsAdapter extends ArrayAdapter<CourseUrl>{

	private int resourceId;
	private Context context;
	private List<Integer> clicks;
	
	public static List<String> myDownUrls = new ArrayList<String>();
	
	public DownLoadsAdapter(Context context,int textViewResourceId,List<CourseUrl> courses ,ListView listView) {
		// TODO Auto-generated constructor stub
		super(context, textViewResourceId, courses);
		resourceId = textViewResourceId;
		this.context = context;
		clicks = new ArrayList<Integer>();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view;
		ViewHolder viewHolder;
		CourseUrl coursename = getItem(position);
		final String urls = coursename.getCourseurl();
		if(convertView == null){
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.download_item, null);
			viewHolder.menuTextView = (TextView) view.findViewById(R.id.menu);
			viewHolder.checkBox = (CheckBox) view.findViewById(R.id.check);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		if(clicks.contains(position)){
			viewHolder.checkBox.setChecked(true);
		}else{
			viewHolder.checkBox.setChecked(false);
		}
		viewHolder.menuTextView.setText(coursename.getCoursename());
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isClicked) {
				// TODO Auto-generated method stub
				if(isClicked){
					myDownUrls.add(urls);
					clicks.add(position);
				}else{
					myDownUrls.remove(urls);
					clicks.remove(position);
				}
			}
		});
		return view;
	}
	
	private class ViewHolder{
		TextView menuTextView;
		CheckBox checkBox;
	}
	
}
