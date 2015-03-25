package com.example.selection_test1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.example.selection_test1.adapters.ListViewAdapter;
import com.example.selection_test1.com.andtinder.model.CardModel;
import com.example.selection_test1.com.andtinder.view.CardContainer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

public class PersonalProfileReview extends Activity {
	
	private Map<String, Object>  personInfo;
	private Map<String, Object>  personInfoDetail;
	private TextView name;
	private TextView field;
	private TextView company;
	private TextView position;
	private ExpandableListView el;
	private AQuery aq;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_profile_review);
        
        InitializeVar();
        
		// using fake data
        if(personInfo.get("email")!=null){
    		Map<String, Object> params = new HashMap<String, Object>();
    		params.put("email", personInfo.get("email"));
    		params.put("event", "cse25");
    		params.put("pid", "");
    		
    		String imgUrl = getPicUrl(personInfo);
    		if(imgUrl!=""){
    			AQuery aq = new AQuery(getWindow().getDecorView().findViewById(android.R.id.content));
    			aq.id(R.id.image).progress(R.id.image_progress_bar).image(imgUrl, false, false);
    		}
    		
    		if(personInfo.get("name")!=null){
    			name.setText(personInfo.get("name").toString());	
    			name.setVisibility(View.VISIBLE);
    		}
    		
    		if(getOrg(personInfo)!=""){
    			company.setText(getField(personInfo));
    			company.setVisibility(View.VISIBLE);
    		}
    		
    		if(getField(personInfo)!=""){
    			position.setText(getPlace(personInfo));
    			position.setVisibility(View.VISIBLE);
    		}
    		if(getPlace(personInfo)!=""){
    			field.setText(getPlace(personInfo));
    			field.setVisibility(View.VISIBLE);
    		}
    		
    		
    		aq.ajax(DATA.single_attendee_detail, params, JSONObject.class, new AjaxCallback<JSONObject>(){
    	    	@Override  
                public void callback(String url, JSONObject json, AjaxStatus status) {

    	    		try {
						Map map1 = Utilities.toMap(json);
						Map personInfoDetail = (Map<String,Object>) map1.get("result");
	    	    		ListViewAdapter lva = new ListViewAdapter(getApplicationContext(), personInfoDetail);
	    	    		el.setAdapter(lva);
	    	    		// expend all the expendable views
	    	    		for(int i=0; i < lva.getGroupCount(); i++)
	    	    			el.expandGroup(i);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	    	}
    	    });
        }

    }

    
    private void InitializeVar() {
    	name = (TextView) findViewById(R.id.title);
    	position = (TextView) findViewById(R.id.pos);
    	company = (TextView) findViewById(R.id.comp);
    	field = (TextView) findViewById(R.id.field);
    	el = (ExpandableListView) findViewById(R.id.person_info);
    	Intent intent = getIntent();
    	personInfo = (Map<String, Object>) intent.getSerializableExtra("PERSON"); 
    	//personInfoDetail = new map();
    	aq = new AQuery(this.findViewById(android.R.id.content));
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
		List<String>map = (List<String>) profile.get("summary");
		if(map.size()>=1){
			return map.get(0).toString();
		}else{
			return "";
		}
	}
	private String getField(Map<String, Object> profile){
		List<String>map = (List<String>) profile.get("summary");
		if(map.size()>=2){
			return map.get(1).toString();
		}else{
			return "";
		}
	}
	private String getPlace(Map<String, Object> profile){
		List<String>map = (List<String>) profile.get("summary");
		if(map.size()>=3){
			return map.get(2).toString();
		}else{
			return "";
		}
	}

}
