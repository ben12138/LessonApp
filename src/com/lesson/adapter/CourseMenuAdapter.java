package com.lesson.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.example.lesson.R;
import com.lesson.bean.Comments;
import com.lesson.bean.CourseUrl;
import com.lesson.util.LoadImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CourseMenuAdapter extends ArrayAdapter<CourseUrl>{
	
	private int resourceId;
	private Context context;
	
	public CourseMenuAdapter(Context context,int textViewResourceId,List<CourseUrl> menus ,ListView listView) {
		// TODO Auto-generated constructor stub
		super(context, textViewResourceId, menus);
		resourceId = textViewResourceId;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		ViewHolder viewHolder ;
		CourseUrl course = getItem(position);
		if(convertView == null){
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.course_menu_item, null);
			viewHolder.menuTextView = (TextView) view.findViewById(R.id.menu);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.menuTextView.setText(course.getCoursename());
		return view;
	}
	
	private class ViewHolder{
		TextView menuTextView;
	}
	
}
