package com.lesson.adapter;

import java.util.List;
import com.example.lesson.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CLassesAdapter extends ArrayAdapter<String>{
	private int resourceId;
	private int selected;
	public CLassesAdapter(Context context,int textViewResourecId,List<String> classes,int selected){
		super(context, textViewResourecId,classes);
		resourceId = textViewResourecId;
		this.selected = selected;
	}
	@SuppressLint({ "ResourceAsColor", "NewApi" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		String classname = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.classTextView = (TextView) view.findViewById(R.id.coursetype1_name);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		if(position == selected){
			viewHolder.classTextView.setBackgroundColor(view.getResources().getColor(R.color.white));
			System.out.println(position+"----"+selected);
		}else{
			viewHolder.classTextView.setBackgroundColor(view.getResources().getColor(R.color.light_gray));
		}
		viewHolder.classTextView.setText(classname);
		return view;
	}
	
	private class ViewHolder{
		TextView classTextView;
	}
	
	@SuppressLint("ResourceAsColor")
	public void upDateItem(int position,ListView listview,List<String> classes){
		int visibleFirstItemPosition = listview.getFirstVisiblePosition();
		int visibleLastItemPosition = listview.getLastVisiblePosition();
		for(int i=0;i<classes.size();i++){
			View view = listview.getChildAt(i);
			if(view != null){
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				viewHolder.classTextView.setBackgroundColor(view.getResources().getColor(R.color.light_gray));
			}
			
		}
		if (position >= visibleFirstItemPosition
				&& position <= visibleLastItemPosition) {
			View view = listview
					.getChildAt(position - visibleFirstItemPosition);
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			viewHolder.classTextView.setBackgroundColor(view.getResources().getColor(R.color.white));
		}
	}
	
}
