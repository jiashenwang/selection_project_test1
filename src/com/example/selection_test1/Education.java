package com.example.selection_test1;

import java.util.ArrayList;

public class Education {
	private String schoolName;
	private ArrayList<String> major;
	private int startDate;
	private int endDate;
	boolean current;
	private String degree;
	
	public Education(){
		schoolName = "";
		major = new ArrayList();
		startDate = 0;
		endDate = 0;
		current = false;
	}
	public Education(String SCHOOLNAME, ArrayList<String> MAJOR, int START, int END, boolean CURRENT, String DEG){
		this.schoolName = SCHOOLNAME;
		this.major = (ArrayList<String>) MAJOR.clone();
		this.startDate = START;
		this.endDate = END;
		this.current = CURRENT;
		degree = DEG;
	}
	
	public String getSchoolName(){
		return this.schoolName;
	}
	public ArrayList<String> getMajor(){
		return this.major;
	}
	public int getStartYear(){
		return this.startDate;
	}
	public int getEndYear(){
		return this.endDate;
	}
	public boolean ifCurrent(){
		return current;
	}
	public String getDegree(){
		return this.degree;
	}
	
}
