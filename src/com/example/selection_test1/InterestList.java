package com.example.selection_test1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.selection_test1.adapters.ListAdapter;
import com.example.selection_test1.com.andtinder.model.CardModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.camera2.TotalCaptureResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class InterestList extends Activity {
	
	private ImageView loadingImg;
	private ListView lv;
	private ArrayList<Map<String, Object>> allInterestAttendees;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest_list);
        
        InitializeVar();

        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, Object> item =  (HashMap<String, Object>) lv.getItemAtPosition(position);
				
			    Intent intent = new Intent(InterestList.this, PersonalProfileReview.class);
			    intent.putExtra("PERSON", item);
			    startActivity(intent);
			}
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent i;
		switch(item.getItemId()){
		case android.R.id.home:
			onBackPressed();
		}
		return true;
    }
    
    private void InitializeVar(){
        //list = new ArrayList<Person>();
        //initializeFakeData();
        lv = (ListView)findViewById(R.id.list);
        loadingImg = (ImageView)findViewById(R.id.loading);
        loadingImg.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getBackground();
        ad.start();
        
        allInterestAttendees = new ArrayList();
        
        getInterestAttendees gia = new getInterestAttendees(getApplicationContext(), "");
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

	
	class getInterestAttendees extends AsyncTask<String, Void, Void>{

		private Context context;
		private String tempStr;
		public getInterestAttendees(Context c, String s){
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
						allInterestAttendees.add(map3.get(i));
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
		        lv.setAdapter(new ListAdapter(getApplicationContext(), allInterestAttendees));
		}
		
	}
}
