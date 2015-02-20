package com.example.selection_test1;

import java.io.Serializable;
import java.util.ArrayList;

public class Person implements Serializable{
	private String id;
	private String name;
	private String mid;
	private ArrayList<String> fields;
	private ArrayList<Work> jobs;
	private ArrayList<Education> edu;
	
	public Person(){
		this.id = "";
		this.name = "";
		this.mid = "";
		this.fields = new ArrayList();
		this.jobs = new ArrayList();
		this.edu = new ArrayList();
	}
	public Person(String ID, String NAME, String MID, ArrayList<String> FIELDS, ArrayList<Work> JOBS, ArrayList<Education> EDU){
		this.id = ID;
		this.name = NAME;
		this.mid = MID;
		this.fields = (ArrayList<String>) FIELDS.clone();
		this.jobs = (ArrayList<Work>) JOBS.clone();
		this.edu = (ArrayList<Education>) EDU.clone();
	}
	
	public String getPersonId(){
		return this.id;
	}
	public String getPersonName(){
		return this.name;
	}
	public String getPersonMid(){
		return this.mid;
	}
	public ArrayList<String> getPersonFields(){
		return fields;
	}
	public ArrayList<Work> getPersonWork(){
		return jobs;
	}
	public ArrayList<Education> getPersonEdu(){
		return edu;
	}
}

