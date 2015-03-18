package com.example.selection_test1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.selection_test1.InterestList.getInterestAttendees;
import com.example.selection_test1.adapters.InteresterListAdapter;
import com.example.selection_test1.adapters.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class InteresterList extends Activity {
	
	private ImageView loadingImg;
	private ListView lv;
	private ArrayList<Map<String, Object>> peopInterestedInMe;
	private TextView tutorialText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interester_list);

        InitializeVar();
        
    }

	private void InitializeVar() {
	    tutorialText = (TextView)findViewById(R.id.interester_list_tutorial_message);
        lv = (ListView)findViewById(R.id.list);
        loadingImg = (ImageView)findViewById(R.id.loading);
        loadingImg.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getBackground();
        ad.start();
        
        peopInterestedInMe = new ArrayList();
        
        getInteresterAttendees gia = new getInteresterAttendees(getApplicationContext(), "");
        gia.execute();
	}
	
    private void loadingMode(){
    	lv.setVisibility(View.GONE);
    	loadingImg.setVisibility(View.VISIBLE);
    }
    private void exitLoadingMode(){
    	loadingImg.setVisibility(View.GONE);
    	lv.setVisibility(View.VISIBLE);
    }

	
	class getInteresterAttendees extends AsyncTask<String, Void, Void>{

		private Context context;
		private String tempStr;
		public getInteresterAttendees(Context c, String s){
			context = c;
			tempStr = s;
		}
		
		@Override
		protected void onPreExecute(){		
			loadingMode();
		}
		@Override 
		protected Void doInBackground(String... params) {
			
			// retrive list
			JSONObject resultJson = null;
			HttpResponse response = null;
			
			// keep sending get request if the mobile is offline
			while(response==null){
				response = Utilities.GetRequest(DATA.attendees_url);
			}
			resultJson = Utilities.ToJson(response);
			
			if(resultJson!=null){
				try { 
					Map map = Utilities.jsonToMap(resultJson);
					Map<String, Object> map2 = (Map<String, Object>) map.get("result");
					List<Map<String, Object>> map3 = new ArrayList();
					map3 = (List<Map<String, Object>>) map2.get("attendees");
					for(int i=0; i<map3.size(); i++){
						peopInterestedInMe.add(map3.get(i));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		protected void onPostExecute(Void v) {
				exitLoadingMode();
		        lv.setAdapter(new InteresterListAdapter(getApplicationContext(), peopInterestedInMe));
		}
		
	}
}
