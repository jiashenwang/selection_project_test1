package com.example.selection_test1;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.selection_test1.com.andtinder.model.CardModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class InterestList extends Activity {
	
	ArrayList<Person> list;
	ListView lv;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest_list);
        
        list = new ArrayList<Person>();
        initializeFakeData();
        lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(new InterestAdapter(getApplicationContext(), list));
        
    }
    
    private void initializeFakeData() {
		// TODO Auto-generated method stub
		// making fake person here!!!!
		Work w1 = new Work("Company1", "Software Engineering", 2014, 0, true);
		Work w2 = new Work("Company2", "Software Engineering intern", 2012, 2013, false);
		Work w3 = new Work("Company3", "UX designer", 2012, 2015, true);
		Education e1 = new Education("UC San Diego", 
				new ArrayList(Arrays.asList("Computer Science")),
				2013, 0, true, "BS");
		Education e2 = new Education("UC San Diego", 
				new ArrayList(Arrays.asList("Computer Science")),
				2011, 2014, false, "BS");
		Education e3 = new Education("UC Los Angeles", 
				new ArrayList(Arrays.asList("Computer Engineering", "Physics")),
				2013, 0, true, "MS");
		
		Person p1 = new Person("1", "Person A", "", 
				new ArrayList(Arrays.asList("Computer Science", "Computer Engineering")),
				new ArrayList<Work>(Arrays.asList(w1, w2, w3)),
				new ArrayList<Education>(Arrays.asList(e1)));
		Person p2 = new Person("1", "Person B", "", 
				new ArrayList(Arrays.asList("Computer Science", "Computer Engineering")),
				new ArrayList<Work>(Arrays.asList(w1)),
				new ArrayList<Education>(Arrays.asList(e2, e3)));
		Person p3 = new Person("1", "Person C", "mid", 
				new ArrayList(Arrays.asList("Computer Science", "Computer Engineering")),
				new ArrayList<Work>(Arrays.asList(w3)),
				new ArrayList<Education>(Arrays.asList(e3)));
		Person p4 = new Person("1", "Person D", "", 
				new ArrayList(Arrays.asList("Computer Science", "Computer Engineering")),
				new ArrayList<Work>(),
				new ArrayList<Education>(Arrays.asList(e3)));
		list.add(p1);
		list.add(p2);
		list.add(p3);
		list.add(p4);
		// end of making fake person
	}

	class InterestAdapter extends BaseAdapter{

		private Context c;
		private ArrayList<Person> p;
		
		public InterestAdapter(Context context, ArrayList<Person> person){
			c = context;
			p = (ArrayList<Person>) person.clone();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return p.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return p.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return Integer.parseInt(p.get(position).getPersonId());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row=inflater.inflate(R.layout.single_row, parent, false);
			TextView name = (TextView)row.findViewById(R.id.name_in_list);
			TextView place = (TextView)row.findViewById(R.id.place_in_list);
			TextView pos = (TextView)row.findViewById(R.id.position_in_list);
			
			Person temp = p.get(position);
			
			String placeTemp="";
			String posTemp="";
			if(temp.getPersonWork().size() == 0){
				for(int i=0; i<temp.getPersonEdu().size(); i++){
					if(temp.getPersonEdu().get(i).ifCurrent()){
						placeTemp = temp.getPersonEdu().get(i).getSchoolName();
						for(int j=0; j<temp.getPersonEdu().get(i).getMajor().size(); j++){
							if(j==0)
								posTemp = temp.getPersonEdu().get(i).getMajor().get(j);
							else
								posTemp +=  ", " + temp.getPersonEdu().get(i).getMajor().get(j);
						}
						break;
					}
				}
			}else{
				for(int i=0; i<temp.getPersonWork().size(); i++){
					if(temp.getPersonWork().get(i).ifCurrent()){
						placeTemp = temp.getPersonWork().get(i).getcompany();
						posTemp = temp.getPersonWork().get(i).getPosition();
					}
					break;
				}
			}
			
			name.setText(temp.getPersonName());
			place.setText(placeTemp);
			pos.setText(posTemp);
			
			
			return row;
		}
    	
    }
}
