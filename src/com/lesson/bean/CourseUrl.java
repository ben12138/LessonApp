package com.lesson.bean;

public class CourseUrl {
	private int id;
	private int courseinfid;
	private String coursename;
	private String courseurl;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCourseinfid() {
		return courseinfid;
	}
	public void setCourseinfid(int courseinfid) {
		this.courseinfid = courseinfid;
	}
	public String getCoursename() {
		return coursename;
	}
	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}
	public String getCourseurl() {
		return courseurl;
	}
	public void setCourseurl(String courseurl) {
		this.courseurl = courseurl;
	}
	@Override
	public String toString() {
		return "CourseUrl [id=" + id + ", courseinfid=" + courseinfid
				+ ", coursename=" + coursename + ", courseurl=" + courseurl
				+ "]";
	}
	
}
