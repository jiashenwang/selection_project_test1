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
import com.example.selection_test1.adapters.InteresterListAdapter;
import com.example.selection_test1.adapters.ListViewAdapter;
import com.example.selection_test1.com.andtinder.model.CardModel;
import com.example.selection_test1.com.andtinder.view.CardContainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
	private RadioGroup radioGroup;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			onDestroy();
			onBackPressed();
		}
		return true;
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_profile_review);
        
        InitializeVar();
		// using fake data
    		
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
    		
        if(personInfo.get("email")!=null){
    		Map<String, Object> params = new HashMap<String, Object>();
    		params.put("email", "yuelu.duan@whova.com");
    		//params.put("event", "cse25");
    		//params.put("pid", "");
    		
    		aq.ajax(DATA.single_attendee_detail, params, JSONObject.class, new AjaxCallback<JSONObject>(){
    	    	@Override  
                public void callback(String url, JSONObject json, AjaxStatus status) {

    	    		try {
						Map<String,Object> map1 = Utilities.toMap(json);
						Map<String,Object> map2 = (Map<String,Object>) map1.get("result");
						Map<String,Object> personInfoDetail = (Map<String,Object>) map2.get("profile");
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
    	radioGroup = (RadioGroup) findViewById(R.id.radio);
    	el = (ExpandableListView) findViewById(R.id.person_info);
    	Intent intent = getIntent();
    	personInfo = (Map<String, Object>) intent.getSerializableExtra("PERSON"); 
    	//personInfoDetail = new map();
    	aq = new AQuery(this.findViewById(android.R.id.content));
    	
    	radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
		        case R.id.btn1:
		            if (((RadioButton)findViewById(R.id.btn1)).isChecked())
		                el.setVisibility(View.GONE);
		            break;
		        case R.id.btn2:
		            if  (((RadioButton)findViewById(R.id.btn2)).isChecked())
		            	el.setVisibility(View.VISIBLE);
		            break;
		    }
			}


		});
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

