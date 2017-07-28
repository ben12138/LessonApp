package com.lesson.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
/**
 * 活动管理，管理所有的活动
 * @author 19339
 *
 */
public class ActivityCollector {
	private static List<Activity> activities = new ArrayList<Activity>();
	private ActivityCollector(){}
	public static void addActivity(Activity activity){
		activities.add(activity);
	}
	public static void finishAll(){
		for(Activity activity:activities){
			activity.finish();
		}
	}
	public static void removeActivity(Activity activity){
		activities.remove(activity);
		activity.finish();
	}
}
