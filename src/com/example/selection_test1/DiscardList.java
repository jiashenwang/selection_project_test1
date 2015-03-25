package com.example.selection_test1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;

import com.example.selection_test1.InterestList.getInterestAttendees;
import com.example.selection_test1.adapters.ListAdapter;

public class DiscardList extends Activity {
	private ImageView loadingImg;
	private ListView lv;
	private ArrayList<Map<String, Object>> allInterestAttendees;
	private ArrayList<Map<String, Object>> outPutAllInterestAttendees;
	
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
				
			    Intent intent = new Intent(DiscardList.this, PersonalProfileReview.class);
			    intent.putExtra("PERSON", item);
			    startActivity(intent);
			}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.interest_list_menu, menu);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new OnQueryTextListener() { 

                @Override 
                public boolean onQueryTextChange(String query) {
                	
                	onQueryTextSubmit(query);
                    return true; 
                }

				@Override
				public boolean onQueryTextSubmit(String query) {
					Log.wtf("!!!!!!!!!!!!", query);
					findAttendees(query);
					return true;
				}

            });

        }
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }
    
	private void findAttendees(String query) {
		if(query.equals("")){
			outPutAllInterestAttendees = (ArrayList<Map<String, Object>>) allInterestAttendees.clone();
		}else{
			outPutAllInterestAttendees.clear();
			for(int i=0; i<allInterestAttendees.size(); i++){
				if(allInterestAttendees.get(i).toString().toLowerCase().contains(query)){
					outPutAllInterestAttendees.add(allInterestAttendees.get(i));
				}
			}
		}
		lv.setAdapter(new ListAdapter(getApplicationContext(), outPutAllInterestAttendees));
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent i;
		switch(item.getItemId()){
		case android.R.id.home:
			onDestroy();
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
        outPutAllInterestAttendees = new ArrayList();
        
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
				response = Utilities.GetRequest(DATA.attendees_url+"?type=discarded");
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
					outPutAllInterestAttendees = (ArrayList<Map<String, Object>>) allInterestAttendees.clone();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		protected void onPostExecute(Void v) {

				exitLoadingMode();
		        lv.setAdapter(new ListAdapter(getApplicationContext(), outPutAllInterestAttendees));
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		loadingImg.destroyDrawingCache();
		lv.destroyDrawingCache();
		allInterestAttendees.clear();
		outPutAllInterestAttendees.clear();
	}
}
