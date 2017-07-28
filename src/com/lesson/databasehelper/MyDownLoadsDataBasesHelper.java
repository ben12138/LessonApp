package com.lesson.databasehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDownLoadsDataBasesHelper extends SQLiteOpenHelper{
	
	private final String CREATE_TABLE_SQL = "create table downloads(_id integer primary key autoincrement,coursename varchar(200),path varchar(200))";
	
	public MyDownLoadsDataBasesHelper(Context context,String name,int version) {
		// TODO Auto-generated constructor stub
		super(context, name, null, version);
	}
	
	public void onCreate(android.database.sqlite.SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SQL);
	};
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
