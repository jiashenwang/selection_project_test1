package com.example.selection_test1;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.example.selection_test1.com.andtinder.model.CardModel;
import com.example.selection_test1.com.andtinder.view.CardContainer;
import com.example.selection_test1.com.andtinder.view.SimpleCardStackAdapter;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaCodec.BufferInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PeopleSelection extends Activity {
	
	final public int MAX_CARDS_NUM = 2;
	final private int amountPerDeck = 4;
	
	private CardContainer mCardContainer;
	private Button interest, not_interest;
	SimpleCardStackAdapter adapter;
	private ArrayList<CardModel> currentCards;
	private ArrayList<CardModel> attendeesBuffer1;
	private ArrayList<CardModel> attendeesBuffer2;
	private ArrayList<Map<String, Object>> attendees;
	//private ArrayList<Map<String, Object>> interestList;
	//private ArrayList<Map<String, Object>> notInterestList;
	private TextView lookingForMore, toDiscard;
	private AQuery aq;
	private ImageView loadingImg;
	private Button interesterBtn;
	View loadingView;
	private List<String> searchedItems;
	private Menu menu;

	//int counter;
	Resources r;
	
	//private int totalCards;
	//private int lastCardInPage;
	//private int deckSize;
	//private int currentDeckSize;
	//private int cardsCounter;
	//private int deckIndex;
	
	private boolean more;
	// if true use buffer1 else use buffer 2
	private boolean bufferToggle;
	private int bufferCounter1;
	private int bufferCounter2;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_selection);
        
        // initialize all the components here
        InitializeVar();
        //getCards();

        /*
		mCardContainer.setAdapter(adapter);
		if(allCards.size()==0){
			loadingMode();
		}*/
		
		// the following is the onclick listener sets
        
        not_interest.setOnTouchListener(new OnTouchListener() {
			
    		@Override
    		public boolean onTouch(View v, MotionEvent event) {
    	          switch (event.getAction()) {
    		          case MotionEvent.ACTION_DOWN: {
    		              Button view = (Button) v;
    		              view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
    		              v.invalidate();
    		              break;
    		          }
    		          case MotionEvent.ACTION_UP:
    		              // Your action here on button click
    		        	  mCardContainer.notLike();
    		          case MotionEvent.ACTION_CANCEL: {
  							holdButton();
    		        	  //Button view = (Button) v;
    		              //view.getBackground().clearColorFilter();
    		              //view.invalidate();
    		              break;
    		          }
    	          }
    	          return true;
    	        
    		}
		});
        interest.setOnTouchListener(new OnTouchListener() {
			
    		@Override
    		public boolean onTouch(View v, MotionEvent event) {
    	          switch (event.getAction()) {
    		          case MotionEvent.ACTION_DOWN: {
    		              Button view = (Button) v;
    		              view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
    		              v.invalidate();
    		              break;
    		          }
    		          case MotionEvent.ACTION_UP:
    		              // Your action here on button click
    		        	  mCardContainer.like();
    		             
    		          case MotionEvent.ACTION_CANCEL: {
  							holdButton();
    		        	  //Button view = (Button) v;
    		              //view.getBackground().clearColorFilter();
    		              //view.invalidate();
    		              break;
    		          }
    	          }
    	          return true;
    	        
    		}
		});
        interesterBtn.setOnTouchListener(new OnTouchListener() {
    		@Override
    		public boolean onTouch(View v, MotionEvent event) {
    	          switch (event.getAction()) {
    		          case MotionEvent.ACTION_DOWN: {
    		              Button view = (Button) v;
    		              view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
    		              v.invalidate();
    		              break;
    		          }
    		          case MotionEvent.ACTION_UP:
    		        	  Intent i = new Intent(PeopleSelection.this, InteresterList.class);
    		        	  startActivity(i);
    		             
    		          case MotionEvent.ACTION_CANCEL: {
  							holdButton();
    		        	  Button view = (Button) v;
    		        	  view.getBackground().clearColorFilter();
    		        	  view.invalidate();
    		              break;
    		          }
    	          }
    	          return true;
    	        
    		}
		});
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attendees_list_menu, menu);
        
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
					findAtendees(query);
					return true;
				}

            });

        }
        
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
			onBackPressed();
		}
		return true;
    }

	private void findAtendees(String query) {
		
	}
    
	protected void holdButton() {
		// TODO Auto-generated method stub
		not_interest.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
		//not_interest.invalidate();
		not_interest.setClickable(false);
		interest.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
		//interest.invalidate();
		interest.setClickable(false);
		Timer buttonTimer = new Timer();
		buttonTimer.schedule(new TimerTask() {

		    public void run() {
		        runOnUiThread(new Runnable() {

		            @Override
		            public void run() {
		                if(more){
			            	not_interest.setClickable(true);
			            	not_interest.getBackground().clearColorFilter();
			            	//not_interest.invalidate();
			            	interest.setClickable(true);
			            	interest.getBackground().clearColorFilter();
			            	//interest.invalidate();
		                }
		            }
		        });
		    }
		}, 170);
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
        interesterBtn = (Button) findViewById(R.id.be_interested);
        
        /*
    	totalCards = 0;
    	lastCardInPage = 0;
    	deckSize = 0;
    	currentDeckSize = 0;
    	cardsCounter = 0;
    	deckIndex = 0;
        */
    	more = true;
    	bufferCounter1 = 0;
    	bufferCounter2 = 0;
    	bufferToggle = true;
    	
    	searchedItems = new ArrayList();
    	currentCards = new ArrayList();
        attendeesBuffer1 = new ArrayList();
        attendeesBuffer2 = new ArrayList();
        attendees = new ArrayList();
		//interestList = new ArrayList();
		//notInterestList = new ArrayList();
		aq = new AQuery(this.findViewById(android.R.id.content));
        r = getResources();
		adapter = new SimpleCardStackAdapter(this);
		
		getAllAttendees gaa = new getAllAttendees(getApplicationContext(), "");
		gaa.execute();
		
	}
	
	private void loadingMode(){
		mCardContainer.setVisibility(View.GONE);
		loadingImg.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.VISIBLE);
		interest.setEnabled(false);
		not_interest.setEnabled(false);
		lookingForMore.setVisibility(View.VISIBLE);
		toDiscard.setVisibility(View.VISIBLE);
		
		not_interest.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
		//not_interest.invalidate();

		interest.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
		//interest.invalidate();

	}
	private void exitLoadingMode(){
		mCardContainer.setVisibility(View.VISIBLE);
		loadingImg.setVisibility(View.GONE);
		loadingView.setVisibility(View.GONE);
		interest.setEnabled(true);
		not_interest.setEnabled(true);
		lookingForMore.setVisibility(View.GONE);
		toDiscard.setVisibility(View.GONE);
		
    	not_interest.getBackground().clearColorFilter();
    	//not_interest.invalidate();
    	interest.getBackground().clearColorFilter();
    	//interest.invalidate();
	}

	private class getAllAttendees extends AsyncTask<String, Void, Void>{

		private Context context;
		private String tempStr;
		public getAllAttendees(Context c, String s){
			context = c;
			tempStr = s;
		}
		
		@Override
		protected void onPreExecute(){	
			if(bufferCounter1==0 && bufferCounter2 == 0){
				loadingMode();
			}
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
			attendees.clear();
			resultJson = Utilities.ToJson(response);
			
			if(resultJson!=null){
				try {
					Map map = Utilities.jsonToMap(resultJson);
					Map<String, Object> map2 = (Map<String, Object>) map.get("result");
					List<Map<String, Object>> map3 = new ArrayList();
					map3 = (List<Map<String, Object>>) map2.get("attendees");
					// get attendees
					for(int i=0; i<map3.size(); i++){
						attendees.add(map3.get(i));
					}

					/*
					// get attendees amount
					totalCards = Integer.parseInt(map2.get("total").toString());
					// get last cards in Deck
					lastCardInPage = Integer.parseInt(map2.get("next").toString());
					// get current deck size
					deckSize = map3.size();
					// check more
					if(lastCardInPage < totalCards){
						more = false;
					}
					*/
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		protected void onPostExecute(Void v) {
			
			if(attendees.size()>0){		
				
				if(bufferToggle){
					Log.wtf("!!!!!!!", "getting buffer2");
					if(attendeesBuffer1.size()==0 && attendeesBuffer2.size()==0){
						for(int i=0; i<attendees.size(); i++){
							attendeesBuffer1.add(new CardModel(attendees.get(i), getResources().getDrawable(R.drawable.picture1)));
						}
						setListener(attendeesBuffer1);
						exitLoadingMode();
					}else{
						
						for(int i=0; i<attendees.size(); i++){
							attendeesBuffer2.add(new CardModel(attendees.get(i), getResources().getDrawable(R.drawable.picture1)));
						}
						//setListener(attendeesBuffer2);
					}

				}else{
					
					Log.wtf("!!!!!!!", "getting buffer1");
					for(int i=0; i<attendees.size(); i++){
						attendeesBuffer1.add(new CardModel(attendees.get(i), getResources().getDrawable(R.drawable.picture1)));
					}
					//setListener(attendeesBuffer1);
					
				}
				exitLoadingMode();
			}
		}
		
	}
	
	public void setListener(final ArrayList<CardModel> attendeesBuffer){
		for(int i=0; i<attendeesBuffer.size(); i++){
			
			final CardModel temp = attendeesBuffer.get(i);
			
			attendeesBuffer.get(i).setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
	        	
	            @Override
	            public void onLike() {
	                Log.wtf("Swipeable Cards","I like the card: ");
	                if(bufferToggle){
	                	bufferCounter1++;
	                	if(bufferCounter1 >= (int)amountPerDeck*0.7){
	                		loadMore();
	                	}
	                	if(more && bufferCounter1 >= amountPerDeck){
	                		swicthToBuffer2();
	                	}
	                }else{
	                	bufferCounter2++;
	                	if(bufferCounter2 >= (int)amountPerDeck*0.7){
	                		loadMore();
	                	}
	                	if(more && bufferCounter2 >= amountPerDeck){
	                		swicthToBuffer1();
	                	}
	                }
	                if(bufferCounter1 >= amountPerDeck){
	                	cleanUp();
	                }
	            }


				@Override
	            public void onDislike() {
	                Log.wtf("Swipeable Cards","I dislike the card");
	                if(bufferToggle){
	                	bufferCounter1++;
	                	if(bufferCounter1 >= (int)(amountPerDeck*0.7)){
	                		loadMore();
	                	}
	                	if(more && bufferCounter1 >= amountPerDeck){
	                		swicthToBuffer2();
	                	}
	                }else{
	                	bufferCounter2++;
	                	if(bufferCounter2 >= (int)(amountPerDeck*0.7)){
	                		loadMore();
	                	}
	                	if(more && bufferCounter2 >= amountPerDeck){
	                		swicthToBuffer1();
	                	}
	                }
	                if(more && bufferCounter1 >= amountPerDeck){
	                	cleanUp();
	                }
	            }
	        });
			adapter.add(attendeesBuffer.get(i));
		}
		mCardContainer.setAdapter(adapter);
	}
	private void loadMore(){
		getAllAttendees temp = new getAllAttendees(getApplicationContext(), "");
		temp.execute();
	}
	private void swicthToBuffer2(){
		loadingMode();
		bufferToggle = false;
		bufferCounter1 = 0;
		attendeesBuffer1.clear();
		if(attendeesBuffer2 != null && attendeesBuffer2.size()!=0){
			//setListener(attendeesBuffer2);
			mCardContainer.setAdapter(adapter);
			exitLoadingMode();
		}
		Log.wtf("!!!!!!!!!!!!!!!!", "Switch to buffer 2");
	}
	private void swicthToBuffer1(){
		loadingMode();
		bufferToggle = true;
		bufferCounter2 = 0;
		attendeesBuffer2.clear();
		if(attendeesBuffer1 != null && attendeesBuffer1.size()!=0){
			//setListener(attendeesBuffer1);
			mCardContainer.setAdapter(adapter);
			exitLoadingMode();
		}
		Log.wtf("!!!!!!!!!!!!!!!!", "Switch to buffer 1");
	}
	
	

}
