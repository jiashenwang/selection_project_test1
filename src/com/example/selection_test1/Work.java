package com.example.selection_test1;

public class Work {
	private String company;
	private String position;
	private int startDate;
	private int endDate;
	boolean current;
	
	public Work(){
		company = "";
		position = "";
		startDate = 0;
		endDate = 0;
		current = false;
	}
	public Work(String COMPANY, String POSiTION, int START, int END, boolean CURRENT){
		this.company = COMPANY;
		this.position = POSiTION;
		this.startDate = START;
		this.endDate = END;
		this.current = CURRENT;
	}
	
	public String getcompany(){
		return this.company;
	}
	public String getPosition(){
		return this.position;
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
}
