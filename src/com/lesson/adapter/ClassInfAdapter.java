package com.lesson.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.lesson.R;
import com.example.lesson.R.id;
import com.lesson.activity.CourseType3AllCourseActivity;
import com.lesson.bean.GOLBALVALUE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ClassInfAdapter extends BaseAdapter{
	
	private int resourceId;
	private Context context;
	private HashMap<String, HashMap<String, String>> map;
	public ClassInfAdapter(Context context,int textViewResourecId,HashMap<String, HashMap<String, String>> map) {
		this.context = context;
		this.map = map;
        resourceId = textViewResourecId;
    }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
//		HashMap<String, ArrayList<String>> map = getItem(position);
		View view;
		ViewHolder viewHolder;
		int num = getCount()/3+1;
		final String coursetype2;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.coursetype2TextView = (TextView) view.findViewById(R.id.coursetype2_name);
			viewHolder.classesInfGridView = (GridView) view.findViewById(R.id.classesinf_gridview);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		view.setTag(R.id.key, position);
		int i=0;
		for(String key:map.keySet()){
			if(i==position){
				viewHolder.coursetype2TextView.setText(key);
				coursetype2 = key;
				List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
				String[] from = {"coursetype_name3","id"};
				int j=0;
				for(String key1:map.get(key).keySet()){
					HashMap<String,String> map1 = new HashMap<String,String>();
					map1.put("coursetype_name3", map.get(key).get(key1));
					map1.put("id", key1);
					list.add(map1);
				}
				int[] to={R.id.courseInf3_name};
				SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),(List<? extends Map<String, ?>>) list,R.layout.coursetype3_layout,from,to);
				viewHolder.classesInfGridView.setAdapter(simpleAdapter);
				viewHolder.classesInfGridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
						// TODO Auto-generated method stub
						String coursetype3 = null;
						int i=0;
						for(String str:map.get(coursetype2).keySet()){
							if(i==position){
								coursetype3 = map.get(coursetype2).get(str);
								break;
							}
							i++;
						}
						if (!GOLBALVALUE.isStartActivity) {
							Intent intent = new Intent(getContext(),
									CourseType3AllCourseActivity.class);
							intent.putExtra("coursetype1",
									GOLBALVALUE.coursetype1);
							intent.putExtra("coursetype2", coursetype2);
							intent.putExtra("coursetype3", coursetype3);
							getContext().startActivity(intent);
						}else{
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("coursetype1", GOLBALVALUE.coursetype1);
							bundle.putString("coursetype2", coursetype2);
							bundle.putString("coursetype3", coursetype3);
							msg.setData(bundle);
							msg.what = CourseType3AllCourseActivity.CHANGECOURSE;
							CourseType3AllCourseActivity.handler.sendMessage(msg);
						}
					}
				});
				break;
			}
			i++;
		}
		return view;
	}
	private Context getContext() {
		// TODO Auto-generated method stub
		return context;
	}
	private class ViewHolder{
		TextView coursetype2TextView;
		GridView classesInfGridView;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return map.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
