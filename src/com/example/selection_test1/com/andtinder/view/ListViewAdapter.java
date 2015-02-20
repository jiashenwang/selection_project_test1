package com.example.selection_test1.com.andtinder.view;


import java.util.ArrayList;

import com.example.selection_test1.Education;
import com.example.selection_test1.Person;
import com.example.selection_test1.R;
import com.example.selection_test1.Work;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewAdapter extends BaseExpandableListAdapter {

	private Person person=null;
	private Context context;
	String []parentList={"Affiliations", "Education", "Social Accounts"};
	ArrayList<Work> works;
	ArrayList<Education> education;
	public ListViewAdapter(Context simpleCardStackAdapter) {
		// TODO Auto-generated constructor stub
		context = simpleCardStackAdapter;
	}

	public ListViewAdapter(SimpleCardStackAdapter simpleCardStackAdapter) {
		// TODO Auto-generated constructor stub
		context = simpleCardStackAdapter.getContext();
	}

	public ListViewAdapter(Context context, Person p) {
		// TODO Auto-generated constructor stub
		this.context = context;
		person = new Person(p.getPersonId(), p.getPersonName(),
				p.getPersonMid(), p.getPersonFields(), p.getPersonWork(), p.getPersonEdu());
		works = (ArrayList<Work>) person.getPersonWork().clone();
		education = (ArrayList<Education>) person.getPersonEdu().clone();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return parentList.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(parentList[groupPosition]=="Affiliations"){
			return works.size();
		}else if(parentList[groupPosition]=="Education"){
			return education.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return parentList[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		if(parentList[groupPosition]=="Affiliations"){
			return works.get(childPosition);
		}else if(parentList[groupPosition]=="Education"){
			return education.get(childPosition);
		}else{
			return "";
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		//\ TODO Auto-generated method stub
		
		TextView tv = new TextView(context);
		tv.setText(parentList[groupPosition]);
		tv.setPadding(250, 30, 30, 30);
		tv.setBackgroundResource(R.color.whova_blue);
		tv.setTextColor(Color.WHITE);
		return tv;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView tv = new TextView(context);
		tv.setPadding(120, 30, 30, 30);
		
		
		if(parentList[groupPosition]=="Affiliations"){
			String tempCompany = works.get(childPosition).getcompany();
			String tempPeriod = "";
			if(works.get(childPosition).ifCurrent()){
				tempPeriod = works.get(childPosition).getStartYear() + " - Present";
			}else{
				tempPeriod = works.get(childPosition).getStartYear() + " - " +
						works.get(childPosition).getEndYear();
			}
			String tempPos = works.get(childPosition).getPosition();
			tv.setLines(3);
			tv.setText(tempCompany+"\n"+tempPos+"\n"+tempPeriod);

		}else if(parentList[groupPosition]=="Education"){
			String tempSchool = education.get(childPosition).getSchoolName();
			String tempMajor = "";
			String tempPeriod = "";
			for(int i=0; i<education.get(childPosition).getMajor().size(); i++){
				if(i==0)
					tempMajor = education.get(childPosition).getMajor().get(i);
				else
					tempMajor += ", "+ education.get(childPosition).getMajor().get(i);
			}
			if(education.get(childPosition).ifCurrent()){
				tempPeriod = education.get(childPosition).getStartYear() + " - Present";
			}else{
				tempPeriod = education.get(childPosition).getStartYear() + " - " +
						education.get(childPosition).getEndYear();
			}
			tv.setLines(3);
			tv.setText(tempSchool+"\n"+tempMajor+"\n"+tempPeriod);
		}else{

		}
		
		tv.setTextColor(Color.BLACK);
		return tv;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
