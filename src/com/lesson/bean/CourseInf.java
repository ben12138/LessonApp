package com.lesson.bean;

import java.io.Serializable;

public class CourseInf implements Serializable{
	private int id;
	private int courseid;
	private int teacherid;
	private String coursename;
	private String courseintroduction;
	private double coursedegree;
	private String coursecomments;
	private String catalogue;
	private String androidimage;
	private String image;
	private int watchernum;
	
	public void setId(int id) {
		this.id = id;
	}
	public void setCourseid(int courseid) {
		this.courseid = courseid;
	}
	public void setTeacherid(int teacherid) {
		this.teacherid = teacherid;
	}
	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}
	public void setCourseintroduction(String courseintroduction) {
		this.courseintroduction = courseintroduction;
	}
	public void setCoursedegree(double coursedegree) {
		this.coursedegree = coursedegree;
	}
	public void setCoursecomments(String coursecomments) {
		this.coursecomments = coursecomments;
	}
	public void setCatalogue(String catalogue) {
		this.catalogue = catalogue;
	}
	public void setAndroidimage(String androidimage) {
		this.androidimage = androidimage;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public void setWatchernum(int watchernum) {
		this.watchernum = watchernum;
	}
	public int getId() {
		return id;
	}
	public int getCourseid() {
		return courseid;
	}
	public int getTeacherid() {
		return teacherid;
	}
	public String getCoursename() {
		return coursename;
	}
	public String getCourseintroduction() {
		return courseintroduction;
	}
	public double getCoursedegree() {
		return coursedegree;
	}
	public String getCoursecomments() {
		return coursecomments;
	}
	public String getCatalogue() {
		return catalogue;
	}
	public String getAndroidimage() {
		return androidimage;
	}
	public String getImage() {
		return image;
	}
	public int getWatchernum() {
		return watchernum;
	}
	@Override
	public String toString() {
		return "CourseInf [id=" + id + ", courseid=" + courseid
				+ ", teacherid=" + teacherid + ", coursename=" + coursename
				+ ", courseintroduction=" + courseintroduction
				+ ", coursedegree=" + coursedegree + ", coursecomments="
				+ coursecomments + ", catalogue=" + catalogue
				+ ", androidimage=" + androidimage + ", image=" + image
				+ ", watchernum=" + watchernum + "]";
	}
	
}
