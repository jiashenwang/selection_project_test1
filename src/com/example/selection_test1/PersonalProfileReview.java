package com.example.selection_test1;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.androidquery.AQuery;
import com.example.selection_test1.adapters.ListViewAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class PersonalProfileReview extends Activity {
	
	private Map<String, Object>  personInfo;
	private TextView name;
	private TextView field;
	private TextView location;
	private ExpandableListView el;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_profile_review);
        
        InitializeVar();
       
		String imgUrl = getPicUrl(personInfo);
		if(imgUrl!=""){
			AQuery aq = new AQuery(getWindow().getDecorView().findViewById(android.R.id.content));
			aq.id(R.id.image).progress(R.id.image_progress_bar).image(imgUrl, false, false);
		}
		
		if(personInfo.get("name")!=null){
			name.setText(personInfo.get("name").toString());	
			name.setVisibility(View.VISIBLE);
		}
		
		if(getField(personInfo)!=""){
			field.setText(getField(personInfo));
			field.setVisibility(View.VISIBLE);
		}
		
		if(getPlace(personInfo)!=""){
			location.setText(getPlace(personInfo));
			location.setVisibility(View.VISIBLE);
		}
		
		ListViewAdapter lva = new ListViewAdapter(getApplicationContext(), personInfo);
		el.setAdapter(lva);
		// expend all the expendable views
		for(int i=0; i < lva.getGroupCount(); i++)
			el.expandGroup(i);
        
		
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			onBackPressed();
		}
		return true;
    }

	private void InitializeVar() {
		name = (TextView) findViewById(R.id.title);
		field = (TextView) findViewById(R.id.field);
		location = (TextView) findViewById(R.id.loc);
		el = (ExpandableListView) findViewById(R.id.person_info);
        Intent intent = getIntent();
        personInfo = (Map<String, Object>) intent.getSerializableExtra("PERSON");
        
        
	}
	
	private String getPicUrl(Map<String, Object> profile){
		String result =   (String) profile.get("pic");
		if(result == null || result == ""){
			return "";
		}else{
			return result;
		}
	}
	private String getOrg(Map<String, Object> profile){
		List<Map<String,Object>>map =   (List<Map<String, Object>>) profile.get("org");
		if(map == null || map.size()<=0){
			return "";
		}else{
			return map.get(0).get("name").toString();
		}
	}
	private String getField(Map<String, Object> profile){
		List<String> map =  (List<String>) profile.get("field");
		if(map!=null){
			if(map.size()>=1){
				return map.get(0).toString();
			}else{
				return "";
			}
		}else{
			return "";
		}
	}
	private String getPlace(Map<String, Object> profile){
		List<Map<String,Object>>map =   (List<Map<String, Object>>) profile.get("loc");
		if(map == null || map.size()<=0){
			return "";
		}else{
			return map.get(0).get("name").toString();
		}
	}
}
