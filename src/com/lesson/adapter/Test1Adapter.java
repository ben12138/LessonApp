package com.lesson.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.lesson.R;
import com.lesson.activity.CourseTestActivity;
import com.lesson.bean.Test;

public class Test1Adapter extends ArrayAdapter<Test>{
	
	private int resourceId;
	private Context context;
	
//	private List<Test> tests;
	
	public Test1Adapter(Context context,int textViewResourceId,List<Test> tests) {
		// TODO Auto-generated constructor stub
		super(context, textViewResourceId, tests);
		resourceId = textViewResourceId;
		this.context = context;
//		this.tests = tests;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		final ViewHolder viewHolder;
//		System.out.println(position+":"+tests.get(position).toString());
		Test test = getItem(position);
		if(convertView == null){
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.test1_item, null);
			viewHolder.questionTextView = (TextView) view.findViewById(R.id.question);
			viewHolder.answerGroup = (RadioGroup) view.findViewById(R.id.choice);
			viewHolder.choiceARadioButton = (RadioButton) view.findViewById(R.id.choiceA);
			viewHolder.choiceBRadioButton = (RadioButton) view.findViewById(R.id.choiceB);
			viewHolder.choiceCRadioButton = (RadioButton) view.findViewById(R.id.choiceC);
			viewHolder.choiceDRadioButton = (RadioButton) view.findViewById(R.id.choiceD);
			viewHolder.answerTextView = (TextView) view.findViewById(R.id.answer_tv);
			viewHolder.correctanswerTextView = (TextView) view.findViewById(R.id.correct_answer_tv);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		if(CourseTestActivity.answers.get(position).equals("")){
			viewHolder.questionTextView.setText(position+1+". "+test.getQuestion());
			viewHolder.choiceARadioButton.setChecked(false);
			viewHolder.choiceBRadioButton.setChecked(false);
			viewHolder.choiceCRadioButton.setChecked(false);
			viewHolder.choiceDRadioButton.setChecked(false);
			viewHolder.choiceARadioButton.setText(test.getChoiceA());
			viewHolder.choiceBRadioButton.setText(test.getChoiceB());
			viewHolder.choiceCRadioButton.setText(test.getChoiceC());
			viewHolder.choiceDRadioButton.setText(test.getChoiceD());
			viewHolder.answerTextView.setText("您还没有做出选择");
		}else{
			viewHolder.questionTextView.setText(position+1+". "+test.getQuestion());
			viewHolder.choiceARadioButton.setText(test.getChoiceA());
			viewHolder.choiceBRadioButton.setText(test.getChoiceB());
			viewHolder.choiceCRadioButton.setText(test.getChoiceC());
			viewHolder.choiceDRadioButton.setText(test.getChoiceD());
			System.out.println(CourseTestActivity.answers.get(position));
			if(CourseTestActivity.answers.get(position).equals("a")){
				viewHolder.answerGroup.check(viewHolder.choiceARadioButton.getId());
			}else if(CourseTestActivity.answers.get(position).equals("b")){
				viewHolder.answerGroup.check(viewHolder.choiceBRadioButton.getId());
			}else if(CourseTestActivity.answers.get(position).equals("c")){
				viewHolder.answerGroup.check(viewHolder.choiceCRadioButton.getId());
			}else if(CourseTestActivity.answers.get(position).equals("d")){
				viewHolder.answerGroup.check(viewHolder.choiceDRadioButton.getId());
			}
			viewHolder.answerTextView.setText("您的选择是："+(CourseTestActivity.answers.get(position)));
		}
		viewHolder.answerTextView.setText("您的选择是："+CourseTestActivity.answers.get(position));
		viewHolder.correctanswerTextView.setText("正确答案是："+CourseTestActivity.correctAnswers.get(position));
		viewHolder.answerGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedid) {
				// TODO Auto-generated method stub
				StringBuffer answersb = new StringBuffer("您的选择是：");
				if(checkedid == R.id.choiceA){
					answersb.append("A");
					CourseTestActivity.answers.set(position, "a");
				}else if(checkedid == R.id.choiceB){
					answersb.append("B");
					CourseTestActivity.answers.set(position, "b");
				}else if(checkedid == R.id.choiceC){
					answersb.append("C");
					CourseTestActivity.answers.set(position, "c");
				}else{
					answersb.append("D");
					CourseTestActivity.answers.set(position, "d");
				}
				viewHolder.answerTextView.setText(answersb.toString());
			}
		});
		return view;
	}
	
	private class ViewHolder{
		TextView questionTextView;
		RadioGroup answerGroup;
		RadioButton choiceARadioButton;
		RadioButton choiceBRadioButton;
		RadioButton choiceCRadioButton;
		RadioButton choiceDRadioButton;
		TextView answerTextView;
		TextView correctanswerTextView;
	}
	
}
