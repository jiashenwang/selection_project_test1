package com.example.selection_test1;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.selection_test1.com.andtinder.model.CardModel;
import com.example.selection_test1.com.andtinder.view.CardContainer;
import com.example.selection_test1.com.andtinder.view.SimpleCardStackAdapter;

import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleSelection extends Activity {
	
	private int totalCards = 0;
	private CardContainer mCardContainer;
	private Button interest, not_interest;
	SimpleCardStackAdapter adapter;
	private ArrayList<CardModel> allCards;
	private ArrayList<Person> interestList;
	private ArrayList<Person> notInterestList;
	private TextView lookingForMore, toDiscard;
	ImageView loadingImg;
	View loadingView;

	int counter;
	Resources r;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_selection);
        
        // initialize all the components here
        InitializeVar();
        getCards();

		mCardContainer.setAdapter(adapter);
		if(allCards.size()==0){
			not_interest.setVisibility(View.INVISIBLE);
			interest.setVisibility(View.INVISIBLE);
		}
		
		// the following is the onclick listener sets
        not_interest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCardContainer.notLike();
				holdButton();
				//mCardContainer.setAdapter(adapter);
			}
		});
        interest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCardContainer.like();
				holdButton();
				//mCardContainer.setAdapter(adapter);
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attendees_list_menu, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent i;
		switch(item.getItemId()){
		case R.id.interest_list:
			i = new Intent(PeopleSelection.this, InterestList.class);
			startActivity(i);
			break;
			
		case android.R.id.home:
			i = new Intent(PeopleSelection.this, MainActivity.class);
			startActivity(i);
		}
		return true;
    }

	protected void holdButton() {
		// TODO Auto-generated method stub
		not_interest.setClickable(false);
		interest.setClickable(false);
		Timer buttonTimer = new Timer();
		buttonTimer.schedule(new TimerTask() {

		    public void run() {
		        runOnUiThread(new Runnable() {

		            @Override
		            public void run() {
		            	not_interest.setClickable(true);
		            	interest.setClickable(true);
		            }
		        });
		    }
		}, 170);
	}

	private void getCards() {
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
		allCards.add(new CardModel(p4, getResources().getDrawable(R.drawable.picture1)));
		allCards.add(new CardModel(p3, getResources().getDrawable(R.drawable.picture1)));
		allCards.add(new CardModel(p2, getResources().getDrawable(R.drawable.picture1)));
		allCards.add(new CardModel(p1, getResources().getDrawable(R.drawable.picture1)));
		// end of making fake person
		counter = allCards.size()-1;
		for(int i=0; i<allCards.size(); i++){
			
			allCards.get(i).setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
	        	
	            @Override
	            public void onLike() {
                	notInterestList.add(allCards.get(totalCards-1).getPerson());
                	//Log.wtf("!!!!!!!!!!!", notInterestList.size()+"");
	                Log.wtf("Swipeable Cards","I dislike the card");
	                totalCards--;
	                
	                if(totalCards==0){
	                	cleanUp();
	                }
	            }

				@Override
	            public void onDislike() {
                	interestList.add(allCards.get(totalCards-1).getPerson());
                	//Log.wtf("!!!!!!????!!!!", interestList.size()+"");
	                Log.wtf("Swipeable Cards","I like the card");
	                totalCards--;
	                if(totalCards==0){
	                	cleanUp();
	                }
	            }
	        }); 
			adapter.add(allCards.get(i));
		}
		totalCards = allCards.size();
	}
    private void cleanUp() {
		// TODO Auto-generated method stub
		mCardContainer.setVisibility(View.GONE);
		loadingImg.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.VISIBLE);
		interest.setEnabled(false);
		not_interest.setEnabled(false);
		
		// for testing only
		loadingMode();
	}
	private void InitializeVar() {
		// TODO Auto-generated method stub
        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        not_interest = (Button)findViewById(R.id.not_interest);
        interest = (Button)findViewById(R.id.interest);
        loadingView = (View) findViewById(R.id.loading_background);
        loadingView.setVisibility(View.GONE);
        loadingImg = (ImageView) findViewById(R.id.loading);
        loadingImg.setBackgroundResource(R.drawable.loading_animation);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getBackground();
        ad.start();
        loadingImg.setVisibility(View.GONE);
        
        lookingForMore = (TextView)findViewById(R.id.looking_for_more);
        toDiscard = (TextView)findViewById(R.id.to_discard);
        
        allCards = new ArrayList();
        r = getResources();
		adapter = new SimpleCardStackAdapter(this);
		interestList = new ArrayList();
		notInterestList = new ArrayList();
		
		ProcessLoading pl = new ProcessLoading(getApplicationContext(), "");
		pl.execute();
		
	}
	
	public void loadingMode(){
		mCardContainer.setVisibility(View.GONE);
		loadingImg.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.VISIBLE);
		interest.setEnabled(false);
		not_interest.setEnabled(false);
		lookingForMore.setVisibility(View.VISIBLE);
		toDiscard.setVisibility(View.VISIBLE);
	}
	public void exitLoadingMode(){
		mCardContainer.setVisibility(View.VISIBLE);
		loadingImg.setVisibility(View.GONE);
		loadingView.setVisibility(View.GONE);
		interest.setEnabled(true);
		not_interest.setEnabled(true);
		lookingForMore.setVisibility(View.GONE);
		toDiscard.setVisibility(View.GONE);
	}

	class ProcessLoading extends AsyncTask<String, Void, ArrayList<Person>>{

		private String attendees_url = "http://whova.net:6565/event/attendees/list/";
		private Context context;
		private String tempStr;
		public ProcessLoading(Context c, String s){
			context = c;
			tempStr = s;
		}
		
		@Override
		protected void onPreExecute(){
			
			loadingMode();
		}
		@Override
		protected ArrayList<Person> doInBackground(String... params) {
			
			// retrive list
			JSONObject resultJson = null;
			resultJson = ToJson(GetRequest(attendees_url));

			if(resultJson!=null){
				try {
					JSONObject result = (JSONObject) resultJson.get("result");
					JSONArray attendees = result.getJSONArray("attendees");
					for(int i=0; i<attendees.length(); i++){
						JSONObject singlePerson = (JSONObject) attendees.get(i);
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		protected void onPostExecute(ArrayList<Person> resultJson) {
			// TODO Auto-generated method stub
			exitLoadingMode();
			
		}
		
	}
	
	public HttpResponse GetRequest(String url){
		HttpResponse response = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			response = client.execute(request);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	public JSONObject ToJson(HttpResponse response){
		try {
			return (new JSONObject(EntityUtils.toString(response.getEntity())));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
