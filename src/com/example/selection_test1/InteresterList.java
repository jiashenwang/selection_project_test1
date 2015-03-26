package com.example.selection_test1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.selection_test1.InterestList.getInterestAttendees;
import com.example.selection_test1.adapters.InteresterListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

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
        
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, Object> item =  (HashMap<String, Object>) lv.getItemAtPosition(position);				
			    Intent intent = new Intent(InteresterList.this, PersonalProfileReview.class);
			    Log.wtf("!!!!!!!!!!!!!!!!!", item.toString());
			    intent.putExtra("PERSON", item);
			    startActivity(intent);
			}
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.interester_list_menu, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.clear:
			new AlertDialog.Builder(this)
		    .setTitle("Clear List")
		    .setMessage("Are you sure you want to clear this list?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with clear
		        	peopInterestedInMe.clear();
		        	lv.setAdapter(new InteresterListAdapter(getApplicationContext(), peopInterestedInMe));
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
			break;
			
		case android.R.id.home:
			onDestroy();
			onBackPressed();
		}
		return true;
    }
	private void InitializeVar() {
	    tutorialText = (TextView)findViewById(R.id.interester_list_tutorial_message);
        lv = (ListView)findViewById(R.id.list);
        loadingImg = (ImageView)findViewById(R.id.loading);
        loadingImg.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getBackground();
        ad.start();
        
        peopInterestedInMe = new ArrayList();
        
        getInteresterAttendees gia = new getInteresterAttendees(this, "");
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
				response = Utilities.GetRequest(DATA.attendees_url+"?type=interester/");
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
		        lv.setAdapter(new InteresterListAdapter(context, peopInterestedInMe));
		        
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		loadingImg.destroyDrawingCache();
		lv.destroyDrawingCache();
		peopInterestedInMe.clear();
		tutorialText.destroyDrawingCache();
	}

}
