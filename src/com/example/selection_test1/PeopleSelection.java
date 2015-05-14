package com.example.selection_test1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.example.selection_test1.R.color;
import com.example.selection_test1.adapters.ListAdapter;
import com.example.selection_test1.com.andtinder.model.CardModel;
import com.example.selection_test1.com.andtinder.model.CardModel.OnCardDimissedListener;
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
import android.view.inputmethod.InputMethodManager;
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

	/*
	 * 1. card swiping contains three cardContainer 
	 * 		a) mCardContainer
	 * 		b) mCardContainer2
	 * 		c) mCardContainer_search
	 * 2. As long as we do search for a specific attendee, the mCardContainer_search will be placed
	 * 		at the top of those three containers
	 * 3. mCardContainer and mCardContainer2 will alternate shows up until running out of cards
	 * 4. adapter, adapter2 and adapter_search are three adapters for those three cardContainers
	 * 5. bufferToggle is for recording which cardContainer is using between mCardContainer and mCardContainer2
	 * 		a) true - using mCardContainer
	 * 		b) false - using mCardContainer2
	 * 
	 * */
	
	private RelativeLayout deckLayout;
	private CardContainer mCardContainer;
	private CardContainer mCardContainer2;
	private CardContainer mCardContainer_search;
	SimpleCardStackAdapter adapter;
	SimpleCardStackAdapter adapter2;
	SimpleCardStackAdapter adapter_search;
	private ArrayList<CardModel> attendeesBuffer;
	private ArrayList<Map<String, Object>> attendees;
	private ArrayList<Map<String, Object>> attendees_search;
	private boolean bufferToggle;
	
	private Button interest, not_interest;
	private TextView lookingForMore, toDiscard, noMoreNewAttendees;
	
	private AQuery aq;
	private ImageView loadingImg;
	private RelativeLayout interesterBtn;
	View loadingView;
	private ListView attendeesSearchHint;
	private TextView no_attendee_matched;

	Resources r;
	
	private boolean more;
	private boolean noMoreSearch;
	private ShowcaseView sv;
	private int tutorialPageNum;
	
	// coach mark targets
	private Target tLike,tDislike,tSearch,tList;
	
	// these will data are all for testing
	private int total_attendees = 12;
	private int attendees_loaded_sofar = 0;
	private int attendees_viewed = 0;
	private int cards_per_deck = 4;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_selection);
        // initialize all the components and variables here
        InitializeVar();
        // set all the button listeners in this activity
        SetButtonListeners();
        // set action bar's listener
        SetSearchBarListener();
        
		// start to grab attendees from api
		getAllAttendees gaa = new getAllAttendees(this, "");
		gaa.execute();
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
	protected void onDestroy() {
		super.onDestroy();
		deckLayout.destroyDrawingCache();
		mCardContainer.destroyDrawingCache();
		interest.destroyDrawingCache();
		not_interest.destroyDrawingCache();;
		adapter.clear();
		adapter_search.clear();
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
	}

	private void findAttendees(String query) {
		
		if(query.equals("")){
			no_attendee_matched.setVisibility(View.GONE);
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
					
						if(map3.size()>0){
							no_attendee_matched.setVisibility(View.GONE);
							attendeesSearchHint.setVisibility(View.VISIBLE);
							attendeesSearchHint.setAdapter(new ListAdapter(getApplicationContext(), 
									(ArrayList<Map<String, Object>>) map3));
						}else{
							no_attendee_matched.setVisibility(View.VISIBLE);
							attendeesSearchHint.setVisibility(View.GONE);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    });
		}

	}
    
	protected void holdButton() {
		// once holdButton is trigger, it will disable all both interesting and not interesting buttons
		not_interest.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
		not_interest.setEnabled(false);
		interest.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
		interest.setEnabled(false);
		Timer buttonTimer = new Timer();
		buttonTimer.schedule(new TimerTask() {
		    public void run() {
		        runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		                if(more){
			            	not_interest.setEnabled(true);
			            	not_interest.getBackground().clearColorFilter();
			            	//not_interest.invalidate();
			            	interest.setEnabled(true);
			            	interest.getBackground().clearColorFilter();
			            	//interest.invalidate();
		                }
		            }
		        });
		    }
		}, 600);
	}

	private void InitializeVar() {
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
        no_attendee_matched = (TextView)findViewById(R.id.text_view_no_attendee_matched);

    	more = true;
    	bufferToggle = true;
    	noMoreSearch = true;
    	
    	attendeesBuffer = new ArrayList();
        attendees = new ArrayList();
        attendees_search = new ArrayList();
		aq = new AQuery(this.findViewById(android.R.id.content));
        r = getResources();
		adapter = new SimpleCardStackAdapter(this);
		adapter2 = new SimpleCardStackAdapter(this);
		adapter_search = new SimpleCardStackAdapter(this);
		
		
		// check if it is the first time that user access this activity
		// if yes enable coach mark
		if(isFirstTime()){
			tLike = new ViewTarget(R.id.interest, this);
			tDislike = new ViewTarget(R.id.not_interest, this);
			tSearch = new ActionItemTarget(this, R.id.search);
			tList = new ActionItemTarget(this, R.id.interest_list);
			
			sv = new ShowcaseView.Builder(this)
				.setTarget(tLike)
				.setContentText("Interesting Profile? \n You Can Save This Attendee to Your Interesting List")
				.setOnClickListener(this)
				.build();
			sv.setButtonText("NEXT");
	        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        lps.addRule(RelativeLayout.CENTER_IN_PARENT);
	        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
	        lps.setMargins(margin, margin, margin, margin);
			sv.setButtonPosition(lps);
			tutorialPageNum = 1;
		}
		
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
		if(noMoreSearch){
			loadingMode();
			loadingImg.setImageResource(R.drawable.p1);
			toDiscard.setTextColor(getResources().getColor(R.color.whova_blue));
			toDiscard.setTypeface(null, Typeface.BOLD);
			lookingForMore.setVisibility(View.GONE);
			noMoreNewAttendees.setVisibility(View.VISIBLE);
			more = false;
		}
	}
	private void exitNoCardMode(){
		exitLoadingMode();
		noMoreNewAttendees.setVisibility(View.GONE);
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
			if(attendees_loaded_sofar == 0){
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
				// try to parse the result which was retreved
				try {
					Map map = Utilities.jsonToMap(resultJson);
					Map<String, Object> map2 = (Map<String, Object>) map.get("result");
					List<Map<String, Object>> map3 = new ArrayList();
					map3 = (List<Map<String, Object>>) map2.get("attendees");
					// get attendees
					for(int i=0; i<map3.size(); i++){
						attendees.add(map3.get(i));
					}
					for(int i=0; i<attendees.size(); i++){
						attendeesBuffer.add(new CardModel(attendees.get(i), getResources().getDrawable(R.drawable.picture1)));
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		protected void onPostExecute(Void v) {
			if(attendeesBuffer.size()==0){
				noCardMode();
			}else{
				if(attendees_loaded_sofar == 0){
					bufferToggle = false;
					// if there are more than two attendees, put the first attendee in mCardContainer
					// and the second one in mCardContainer2
					if(attendeesBuffer.size() >=2){
						setListener(attendeesBuffer.get(0));
						setListener(attendeesBuffer.get(1));
						adapter.add(attendeesBuffer.get(0));
						mCardContainer.setAdapter(adapter);		

						adapter2.add(attendeesBuffer.get(1));
						mCardContainer2.setAdapter(adapter2);
						
						attendeesBuffer.remove(0);
						attendeesBuffer.remove(0);
						
						// put mCardContainer on top of mCardContainer2
						deckLayout.removeView(mCardContainer2);
						deckLayout.removeView(mCardContainer);
						deckLayout.addView(mCardContainer2);
						deckLayout.addView(mCardContainer);
					}else{
						if(attendees_loaded_sofar == 1){
							setListener(attendeesBuffer.get(0));
							mCardContainer.setAdapter(adapter);		
							attendeesBuffer.remove(0);
						}
					}
					exitLoadingMode();
				}
				attendees_loaded_sofar += cards_per_deck;
			}

			
		}
		
	}
	private void switchTDeck(){

		if(!bufferToggle){
			// put mCardContainer2 on top of mCardContainer
			deckLayout.removeView(mCardContainer2);
			deckLayout.removeView(mCardContainer);
			deckLayout.addView(mCardContainer);
			deckLayout.addView(mCardContainer2);
			
			adapter.clear();
			setListener(attendeesBuffer.get(0));
			adapter.add(attendeesBuffer.get(0));
			mCardContainer.setAdapter(adapter);
			attendeesBuffer.remove(0);
			
			bufferToggle = true;
				
		}else{
			// put mCardContainer on top of mCardContainer2
			deckLayout.removeView(mCardContainer2);
			deckLayout.removeView(mCardContainer);
			deckLayout.addView(mCardContainer2);
			deckLayout.addView(mCardContainer);
			
			adapter2.clear();
			setListener(attendeesBuffer.get(0));
			adapter2.add(attendeesBuffer.get(0));
			mCardContainer2.setAdapter(adapter2);
			attendeesBuffer.remove(0);
			
			bufferToggle = false; 

		}			
		
	}

	private void setListener(final CardModel cardModel){
		
    	cardModel.setOnCardDimissedListener(new OnCardDimissedListener() {
			
			@Override
			public void onLike() {
				attendees_viewed++;
				// Caution!!! current email and pid are empty, so i set them to empty string
				//cardModel.getProfile().get("email").toString();
                // temp.getProfile().get("pid").toString()
                decisionPost(""/*email*/,
                		""/*pid*/, 
                		true);
                if(attendeesBuffer.size()>0){
                	switchTDeck();
                }
                if(attendees_viewed == total_attendees){
            		noCardMode();
            	}
                if(attendees_viewed%cards_per_deck == 1 && attendees_loaded_sofar < total_attendees){
                	loadMore();
                }
			}
			
			@Override 
			public void onDislike() {
				attendees_viewed++;
                // Caution!!! current email and pid are empty, so i set them to empty string
                // temp.getProfile().get("email").toString()
                // temp.getProfile().get("pid").toString()
                decisionPost(""/*email*/,
                		""/*pid*/, 
                		false);
                if(attendeesBuffer.size()>0){
                	switchTDeck();
                }
                if(attendees_viewed == total_attendees){
            		noCardMode();
            	}
                if(attendees_viewed%cards_per_deck == 1 && attendees_loaded_sofar < total_attendees){
                	loadMore();
                }
			}
		});
	}
	private void loadMore(){
		getAllAttendees temp = new getAllAttendees(getApplicationContext(), "");
		temp.execute();
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
	    		}
	    	}
	    };
	    aq.post(url, new JSONObject(params), JSONObject.class, cb);

	        
	}

	// click listener for coach mark
	@Override
	public void onClick(View v) {
		switch(tutorialPageNum){

			case 1:
				sv.setShowcase(tDislike, true);
				sv.setContentText("Not Interesting? \n You Can Discard This Attendee and You Won't See Him Again");
			break;
			case 2:
				sv.setShowcase(tSearch, true);
				sv.setContentText("Search For A Specific Attendee At Any Time");
			break;
			case 3:
				sv.setShowcase(tList, true);
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
	} 
	

	
	private void SetButtonListeners(){
        // set not interest btn listener
        not_interest.setOnTouchListener(new OnTouchListener() {
			
    		@Override
    		public boolean onTouch(View v, MotionEvent event) {
    	          switch (event.getAction()) {
    		          case MotionEvent.ACTION_DOWN: {
    		              Button view = (Button) v;
    		              view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
    		              v.invalidate();
    		              
    		        	  interest.setEnabled(false);
    		              break;
    		          }
    		          case MotionEvent.ACTION_UP:
    		              // decide if not interest action should be token on which card container
    		        	  if(noMoreSearch){
    		        		  if(!bufferToggle){
    		        			  mCardContainer.notLike();
    		        		  }else{
    		        			  mCardContainer2.notLike();
    		        		  }
    		        		  
    		        	  }else
    		        		  mCardContainer_search.notLike();
    		          case MotionEvent.ACTION_CANCEL: {
    		        	    // disable button for half second
  							holdButton();
    		              break;
    		          }
    	          }
    	          return true;
    	        
    		}
		});
     // set interest btn listener
        interest.setOnTouchListener(new OnTouchListener() {
			
    		@Override
    		public boolean onTouch(View v, MotionEvent event) {
    	          switch (event.getAction()) {
    		          case MotionEvent.ACTION_DOWN: {
    		              Button view = (Button) v;
    		              view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
    		              v.invalidate();
    		              
    		        	  not_interest.setEnabled(false);
    		              break;
    		          }
    		          case MotionEvent.ACTION_UP:
    		        	  if(noMoreSearch){
    		        		  if(!bufferToggle){
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
        
        // set click listener for the "list" button in the action bar
        interesterBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  Intent i = new Intent(PeopleSelection.this, InteresterList.class);
		      	  startActivity(i);
			}
		});
        // set click listener for the discard list btn
        toDiscard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(PeopleSelection.this, DiscardList.class);
	        	startActivity(i);
			}
		});
	}
	
	private void SetSearchBarListener(){
        // set listener for the search bar
        attendeesSearchHint.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				attendeesSearchHint.setVisibility(View.GONE);
				HashMap<String, Object> item = 
						(HashMap<String, Object>) attendeesSearchHint.getItemAtPosition(position);
				
				// using event data here 
				String email = "yuelu.duan@whova.com";//item.get("email").toString();
				String event = "CSE25";
				String pid = item.get("pid").toString();
				Map<String, Object> params = new HashMap<String, Object>();

				
				aq.ajax("http://whova.net:6565/mobile/pipldetail2/?email="+email+
						"&event="+event+"&pid="+pid, JSONObject.class, new AjaxCallback<JSONObject>(){
			    	@Override  
		            public void callback(String url, JSONObject json, AjaxStatus status) { 
						Log.wtf("!!!!!", json.toString());
			    		try {
			    			noMoreSearch = false;
			    			exitNoCardMode();
			    			Map map1 = Utilities.toMap(json);
							Map<String, Object> map2 = (Map<String, Object>) map1.get("result");
							Map<String, Object> map3 = (Map<String, Object>) map2.get("profile");

							CardModel searchResult = new CardModel(map3);
							// set onlike and ondislike listener for a card
							searchResult.setOnCardDimissedListener(new CardModel.OnCardDimissedListener(){
								@Override
					            public void onLike() {
									decisionPost(""/*email*/,""/*pid*/, true);
									noMoreSearch = true;
									if(attendees_viewed == total_attendees){
										noCardMode();
									}
								}
								public void onDislike() {
									decisionPost(""/*email*/,""/*pid*/, false);
									noMoreSearch = true;
									if(attendees_viewed == total_attendees){
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
							
							// hide keyboard
							InputMethodManager imm = (InputMethodManager)getSystemService(
								      Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
			    	}
			    });
			}
        });
	}

}
