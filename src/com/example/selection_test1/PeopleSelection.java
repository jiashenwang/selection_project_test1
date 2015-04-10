package com.example.selection_test1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.example.selection_test1.R.color;
import com.example.selection_test1.adapters.ListAdapter;
import com.example.selection_test1.com.andtinder.model.CardModel;
import com.example.selection_test1.com.andtinder.view.CardContainer;
import com.example.selection_test1.com.andtinder.view.CardStackAdapter;
import com.example.selection_test1.com.andtinder.view.SimpleCardStackAdapter;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PeopleSelection extends Activity implements View.OnClickListener {
	
	final private int amountPerDeck = 4;
	private int temp_counter = 0;
	
	private RelativeLayout deckLayout;
	private CardContainer mCardContainer;
	private CardContainer mCardContainer2;
	private CardContainer mCardContainer_search;
	private Button interest, not_interest;
	SimpleCardStackAdapter adapter;
	SimpleCardStackAdapter adapter_search;
	private ArrayList<CardModel> currentCards;
	private ArrayList<CardModel> attendeesBuffer1;
	private ArrayList<CardModel> attendeesBuffer2;
	private ArrayList<Map<String, Object>> attendees;
	private ArrayList<Map<String, Object>> attendees_search;
	private TextView lookingForMore, toDiscard, noMoreNewAttendees;
	private AQuery aq;
	private ImageView loadingImg;
	private RelativeLayout interesterBtn;
	View loadingView;
	private ListView attendeesSearchHint;

	Resources r;
	
	private boolean CardContainerToggle;
	private boolean read = false;
	private boolean more;
	private boolean noMoreAttendee;
	private boolean noMoreSearch;
	// if true use buffer1 else use buffer 2
	private boolean bufferToggle;
	private int bufferCounter1;
	private int bufferCounter2;
	private ShowcaseView sv;
	private int tutorialPageNum;
	
	// coach mark targets
	private Target tLike,tDislike,tSearch,tList;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_selection);
        

        // initialize all the components here
        InitializeVar();
        
        
        
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
    		        	  if(noMoreSearch){
    		        		  if(CardContainerToggle){
    		        			  mCardContainer.notLike();
    		        		  }else{
    		        			  mCardContainer2.notLike();
    		        		  }
    		        		  
    		        	  }else
    		        		  mCardContainer_search.notLike();
    		          case MotionEvent.ACTION_CANCEL: {
  							holdButton();
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
    		        	  if(noMoreSearch){
    		        		  if(CardContainerToggle){
    		        			  mCardContainer.like();
    		        		  }else{
    		        			  mCardContainer2.like();
    		        		  }
    		        	  }
    		        	  else
    		        		  mCardContainer_search.like();
    		             
    		          case MotionEvent.ACTION_CANCEL: {
  							holdButton();
    		              break;
    		          }
    	          }
    	          return true;
    	        
    		}
		});
        interesterBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  Intent i = new Intent(PeopleSelection.this, InteresterList.class);
		      	  startActivity(i);
			}
		});
        
        toDiscard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PeopleSelection.this, DiscardList.class);
	        	startActivity(i);
			}
		});
        
        attendeesSearchHint.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				attendeesSearchHint.setVisibility(View.GONE);
				HashMap<String, Object> item = 
						(HashMap<String, Object>) attendeesSearchHint.getItemAtPosition(position);
				
				// using fake data
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("email", item.get("email"));
				params.put("event", "cse25");
				params.put("pid", "");
				
				aq.ajax(DATA.single_attendee_detail, params, JSONObject.class, new AjaxCallback<JSONObject>(){
			    	@Override  
		            public void callback(String url, JSONObject json, AjaxStatus status) { 
			    		try {
			    			noMoreSearch = false;
			    			exitNoCardMode();
			    			Map map1 = Utilities.toMap(json);
							Map<String, Object> map2 = (Map<String, Object>) map1.get("result");
							Map<String, Object> map3 = (Map<String, Object>) map2.get("profile");
							CardModel searchResult = new CardModel(map3);
							searchResult.setOnCardDimissedListener(new CardModel.OnCardDimissedListener(){
								@Override
					            public void onLike() {
									Log.wtf("!!!!!!!!!","Search card liked");
									decisionPost(""/*email*/,""/*pid*/, true);
									noMoreSearch = true;
									if(noMoreAttendee){
										noCardMode();
									}
								}
								public void onDislike() {
									Log.wtf("!!!!!!!!!","Search card not liked");
									decisionPost(""/*email*/,""/*pid*/, false);
									noMoreSearch = true;
									if(noMoreAttendee){
										noCardMode();
									}
								}
							});
						 
							adapter_search.clear();
							adapter_search.add(searchResult);
							mCardContainer_search = 
									(CardContainer) findViewById(R.id.layoutview_search);
							mCardContainer_search.setVisibility(View.VISIBLE);
							mCardContainer_search.bringToFront();
							mCardContainer_search.setAdapter(adapter_search);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}
			    });
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
					findAttendees(query);
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
			onDestroy();
			
			onBackPressed();
		}
		return true;
    }

	private void findAttendees(String query) {
		
		if(query.equals("")){
			attendeesSearchHint.setVisibility(View.GONE);
		}else{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("keyword", query);
			
			aq.ajax(DATA.attendees_search, params, JSONObject.class, new AjaxCallback<JSONObject>(){
		    	@Override  
	            public void callback(String url, JSONObject json, AjaxStatus status) { 
		    		try {
						Map map1 = Utilities.toMap(json);
						Map<String, Object> map2 = (Map<String, Object>) map1.get("result");
						List<Map<String, Object>> map3 = new ArrayList();
						map3 = (List<Map<String, Object>>) map2.get("attendees");
						attendeesSearchHint.setVisibility(View.VISIBLE);
						attendeesSearchHint.setAdapter(new ListAdapter(getApplicationContext(), 
								(ArrayList<Map<String, Object>>) map3));
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    });
		}

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
		//loadingMode();
		noCardMode();
	}
	private void InitializeVar() {
		// TODO Auto-generated method stub
		deckLayout = (RelativeLayout)findViewById(R.id.deck_layout);
        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        mCardContainer2 = (CardContainer) findViewById(R.id.layoutview2);
        not_interest = (Button)findViewById(R.id.not_interest);
        interest = (Button)findViewById(R.id.interest);
        loadingView = (View) findViewById(R.id.loading_background);
        loadingView.setVisibility(View.GONE);
        loadingImg = (ImageView) findViewById(R.id.loading);
        loadingImg.setVisibility(View.GONE);
        lookingForMore = (TextView)findViewById(R.id.looking_for_more);
        toDiscard = (TextView)findViewById(R.id.to_discard);
        noMoreNewAttendees = (TextView)findViewById(R.id.no_more_new_attendees);
        interesterBtn = (RelativeLayout) findViewById(R.id.interester_btn);
        attendeesSearchHint = (ListView)findViewById(R.id.auto_complete_attendees_search);

    	more = true;
    	bufferCounter1 = 0;
    	bufferCounter2 = 0;
    	bufferToggle = true;
    	CardContainerToggle = true;
    	
    	noMoreAttendee = true;
    	noMoreSearch = true;
    	
    	currentCards = new ArrayList();
        attendeesBuffer1 = new ArrayList();
        attendeesBuffer2 = new ArrayList();
        attendees = new ArrayList();
        attendees_search = new ArrayList();
		//interestList = new ArrayList();
		//notInterestList = new ArrayList();
		aq = new AQuery(this.findViewById(android.R.id.content));
        r = getResources();
		adapter = new SimpleCardStackAdapter(this);
		adapter_search = new SimpleCardStackAdapter(this);
		
		tLike = new ViewTarget(R.id.interest, this);
		tDislike = new ViewTarget(R.id.not_interest, this);
		tSearch = new ActionItemTarget(this, R.id.search);
		tList = new ActionItemTarget(this, R.id.interest_list);
		
		if(isFirstTime()){
			sv = new ShowcaseView.Builder(this)
				.setOnClickListener(this)
				.build();
			sv.setButtonText("NEXT");
	        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        lps.addRule(RelativeLayout.CENTER_IN_PARENT);
	        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
	        lps.setMargins(margin, margin, margin, margin);
			sv.setButtonPosition(lps);
			tutorialPageNum = 0;
		}
		
		
		getAllAttendees gaa = new getAllAttendees(this, "");
		gaa.execute();
		
	}
	
	private void loadingMode(){
        loadingImg.setBackgroundResource(R.xml.loading_animation);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getBackground();
        ad.start();
		toDiscard.setTextColor(Color.GRAY);
		toDiscard.setTypeface(null, Typeface.NORMAL);
		
		mCardContainer.setVisibility(View.GONE);
		loadingImg.setVisibility(View.VISIBLE);
		loadingView.setVisibility(View.VISIBLE);
		interest.setEnabled(false);
		not_interest.setEnabled(false);
		lookingForMore.setVisibility(View.VISIBLE);
		toDiscard.setVisibility(View.VISIBLE);
		deckLayout.setVisibility(View.GONE);
		noMoreNewAttendees.setVisibility(View.GONE);
		
		not_interest.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
		interest.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);

	}
	private void exitLoadingMode(){
		mCardContainer.setVisibility(View.VISIBLE);
		loadingImg.setVisibility(View.GONE);
		loadingView.setVisibility(View.GONE);
		interest.setEnabled(true);
		not_interest.setEnabled(true);
		lookingForMore.setVisibility(View.GONE);
		toDiscard.setVisibility(View.GONE);
		deckLayout.setVisibility(View.VISIBLE);
		noMoreNewAttendees.setVisibility(View.GONE);
		
    	not_interest.getBackground().clearColorFilter();
    	interest.getBackground().clearColorFilter();
	}
	private void noCardMode(){
		noMoreAttendee = true;
		if(noMoreSearch){
			loadingMode();
			
			loadingImg.setImageResource(R.drawable.p1);
			toDiscard.setTextColor(getResources().getColor(R.color.whova_blue));
			toDiscard.setTypeface(null, Typeface.BOLD);
			
			lookingForMore.setVisibility(View.GONE);
			noMoreNewAttendees.setVisibility(View.VISIBLE);
		}
	}
	private void exitNoCardMode(){
		exitLoadingMode();
		noMoreNewAttendees.setVisibility(View.GONE);
	}

	private class getAllAttendees extends AsyncTask<String, Void, Void>{

		private Context context;
		private String tempStr;
		private boolean search = false;
		public getAllAttendees(Context c, String s){
			context = c;
			tempStr = s;
		}
		public getAllAttendees(Context c, String s, boolean Search){
			context = c;
			tempStr = s;
			search = Search;
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
					noMoreAttendee = false;
					
					
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
						read = true;
					}

				}else{
					
					Log.wtf("!!!!!!!", "getting buffer1");
					for(int i=0; i<attendees.size(); i++){
						attendeesBuffer1.add(new CardModel(attendees.get(i), getResources().getDrawable(R.drawable.picture1)));
					}
					read = true;
					
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

	                // Caution!!! current email and pid are empty, so i set them to empty string
	                // temp.getProfile().get("email").toString()
	                // temp.getProfile().get("pid").toString()
	                decisionPost(""/*email*/,
	                		""/*pid*/, 
	                		true);

	                if(bufferToggle){
	                	bufferCounter1++;
	                	if(bufferCounter1 == amountPerDeck -1){
	                		showNewDeck();
	                	}
	                	if(!read && bufferCounter1 >= (int)(amountPerDeck*0.7)){
	                		Log.wtf("!!!!!!!!!!!!", "Like Load more");
	                		if(temp_counter<2)
	                			loadMore();
	                	}
	                	if(more && bufferCounter1 >= amountPerDeck){
	                		if(temp_counter>=2){
	                			noCardMode();
	                		}else{
		                		swicthToBuffer2();
	                		}
	                	}
	                }else{
	                	bufferCounter2++;
	                	if(bufferCounter2 == amountPerDeck -1){
	                		showNewDeck();
	                	}
	                	if(!read && bufferCounter2 >= (int)(amountPerDeck*0.7)){
	                		if(temp_counter<2)
	                			loadMore();
	                	}
	                	if(more && bufferCounter2 >= amountPerDeck){
	                		if(temp_counter>=2){
	                			noCardMode();
	                		}else{
		                		swicthToBuffer1();
	                		}
	                	}
	                }
	                if(bufferCounter1 >= amountPerDeck){
	                	cleanUp();
	                }
	            }

 
				@Override
	            public void onDislike() {
	                Log.wtf("Swipeable Cards","I dislike the card");
	                // Caution!!! current email and pid are empty, so i set them to empty string
	                // temp.getProfile().get("email").toString()
	                // temp.getProfile().get("pid").toString()
	                decisionPost(""/*email*/,
	                		""/*pid*/, 
	                		false);
	                
	                if(bufferToggle){
	                	bufferCounter1++;
	                	if(bufferCounter1 == amountPerDeck -1){
	                		showNewDeck();
	                	}
	                	if(!read && bufferCounter1 >= (int)(amountPerDeck*0.7)){
	                		if(temp_counter<2)
	                			loadMore();
	                	}
	                	if(more && bufferCounter1 >= amountPerDeck){
	                		if(temp_counter>=2){
	                			noCardMode();
	                		}else{
		                		swicthToBuffer2();
	                		}
	                	}
	                }else{
	                	bufferCounter2++;
	                	if(bufferCounter2 == amountPerDeck -1){
	                		showNewDeck();
	                	}
	                	if(!read && bufferCounter2 >= (int)(amountPerDeck*0.7)){
	                		if(temp_counter<2)
	                			loadMore();
	                	}
	                	if(more && bufferCounter2 >= amountPerDeck){
	                		if(temp_counter>=2){
	                			noCardMode();
	                		}else{
		                		swicthToBuffer1();
	                		}
	                	}
	                }
	                if(bufferCounter1 >= amountPerDeck){
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

		Log.wtf("!!!!!!!!!!!!!!!!", "Switch to buffer 2");
		bufferToggle = false;
		bufferCounter1 = 0;
		attendeesBuffer1.clear();

		temp_counter++;
		read = false;
		CardContainerToggle = false;
		//loadingMode();	
		

	}
	private void swicthToBuffer1(){

		Log.wtf("!!!!!!!!!!!!!!!!", "Switch to buffer 1");
		
		bufferToggle = true;
		bufferCounter2 = 0;
		attendeesBuffer2.clear();

		temp_counter++;
		read = false;
		CardContainerToggle = true;
		//loadingMode();	
		
	}
	
	private void showNewDeck(){
		
		if(bufferToggle){
			if(attendeesBuffer2 != null && attendeesBuffer2.size()!=0){
				mCardContainer2.setAdapter(adapter);
				deckLayout.removeView(mCardContainer2);
				deckLayout.addView(mCardContainer2);
				deckLayout.removeView(mCardContainer);
				deckLayout.addView(mCardContainer);
			}
		}else{
			if(attendeesBuffer1 != null && attendeesBuffer1.size()!=0){

				mCardContainer.setAdapter(adapter);
				deckLayout.removeView(mCardContainer);
				deckLayout.addView(mCardContainer);
				deckLayout.removeView(mCardContainer2);
				deckLayout.addView(mCardContainer2);
			}
		}

			
			/*
			final RelativeLayout.LayoutParams params = 
	                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
	                                                RelativeLayout.LayoutParams.WRAP_CONTENT);

			params.addRule(RelativeLayout.BELOW, previousId);
			
			previousId++;*/

	}
	
	public void decisionPost(String email, String pid, boolean interested){
        
	    //do a twiiter search with a http post
	        
	    String url = DATA.attendees_choice_url;
	        
	    Map<String, Object> params = new HashMap<String, Object>();
	    //params.put("email", email);
	    //params.put("pid", pid);
	    if(interested)
	    	params.put("interested", "yes");
	    else
	    	params.put("interested", "no");
	        
	    AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
	    	@Override  
            public void callback(String url, JSONObject json, AjaxStatus status) {  
	    		if(!status.getMessage().toLowerCase().equals("ok")){
	    			// todo offline system
	    		}
	    	}
	    };
	    aq.post(url, new JSONObject(params), JSONObject.class, cb);

	        
	}

	@Override
	public void onClick(View v) {
		switch(tutorialPageNum){
			case 0:
				sv.setShowcase(tLike, true);
				sv.setContentTitle("Tutorial Page 1");
				sv.setContentText("Interesting Profile? \n You Can Save This Attendee to Your Interesting List");
			break;
			case 1:
				sv.setShowcase(tDislike, true);
				sv.setContentTitle("Tutorial Page 2");
				sv.setContentText("Not Interesting? \n You Can Discard This Attendee and You Won't See Him Again");
			break;
			case 2:
				sv.setShowcase(tSearch, true);
				sv.setContentTitle("Tutorial Page 3");
				sv.setContentText("Search For A Specific Attendee At Any Time");
			break;
			case 3:
				sv.setShowcase(tList, true);
				sv.setContentTitle("Tutorial Page 4");
				sv.setContentText("You Can Find All Attendees Which You Are Interested In");
			break;
			case 4:
				sv.hide();
			break;
		}
		tutorialPageNum++;
	}
	
	private boolean isFirstTime()
	{
		
	    SharedPreferences preferences = getPreferences(MODE_PRIVATE);
	    boolean ranBefore = preferences.getBoolean("PeopleSelectionTutorial", false);
	    if (!ranBefore) {
	        // first time
	        SharedPreferences.Editor editor = preferences.edit();
	        editor.putBoolean("PeopleSelectionTutorial", true);
	        editor.commit();
	    }
	    return !ranBefore;
		//return true;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		deckLayout.destroyDrawingCache();
		mCardContainer.destroyDrawingCache();
		interest.destroyDrawingCache();
		not_interest.destroyDrawingCache();;
		adapter.clear();
		adapter_search.clear();;
		currentCards.clear();
		attendeesBuffer1.clear();
		attendeesBuffer2.clear();;
		attendees.clear();
		attendees_search.clear();;
		lookingForMore.destroyDrawingCache(); 
		toDiscard.destroyDrawingCache();
		noMoreNewAttendees.destroyDrawingCache();
		aq.clear();
		loadingImg.destroyDrawingCache();
		interesterBtn.destroyDrawingCache();
		loadingView.destroyDrawingCache();
		attendeesSearchHint.destroyDrawingCache();;
		//sv.destroyDrawingCache();
	}
	
	

}
