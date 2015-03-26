package com.example.selection_test1.com.andtinder.view;


import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.selection_test1.R;
import com.example.selection_test1.adapters.ListViewAdapter;
import com.example.selection_test1.com.andtinder.model.CardModel;

public final class SimpleCardStackAdapter extends CardStackAdapter {

	public SimpleCardStackAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.std_card_inner, parent, false);
			assert convertView != null;
		}

		
		String imgUrl = getPicUrl(model);
		if(imgUrl!=""){
			AQuery aq = new AQuery(convertView);
			aq.id(R.id.image).progress(R.id.image_progress_bar).image(imgUrl, false, false);
		}else{
			((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
		}

		
		((TextView) convertView.findViewById(R.id.title)).setText(model.getProfile().get("name").toString());
		((TextView) convertView.findViewById(R.id.field)).setText(getField(model));
		((TextView) convertView.findViewById(R.id.loc)).setText(getLoc(model));

		final ExpandableListView person_info = (ExpandableListView)convertView.findViewById(R.id.person_info);
		ListViewAdapter lva = new ListViewAdapter(convertView.getContext(), model.getProfile());
		person_info.setAdapter(lva);
		
		// expend all the expendable views
		for(int i=0; i < lva.getGroupCount(); i++){
			person_info.expandGroup(i);
		}

		
		if(position == 0){
			person_info.smoothScrollToPosition(person_info.getCount()-1);
		}
		/*
		if(position == 0){

			final long totalScrollTime = 4000;
			final int scrollPeriod = 1;
			final int heightToScroll = 1; 
			person_info.post(new Runnable() {
	            @Override
	            public void run() {
	                    new CountDownTimer(totalScrollTime, scrollPeriod ) {
	                        public void onTick(long millisUntilFinished) {
	                        	person_info.scrollBy(0, heightToScroll);
	                        }
	                    public void onFinish() {
	                        //you can add code for restarting timer here
	                    }
	                }.start();
	            }
	        });
		}*/

		
		return convertView;
	}
    
	
	
	private String getPicUrl(CardModel model){
		String result =  (String) model.getProfile().get("pic");
		if(result == null || result == ""){
			return "";
		}else{
			return result;
		}
	}
	private String getField(CardModel model){
		String field = "";
		List<String> map =  (List<String>) model.getProfile().get("field");
		for(int i=0; i<map.size(); i++){
			if(i==0)
				field = map.get(i).toString();
			else
				field += (", "+map.get(i).toString());
		}
		return field;
	}
	private String getLoc(CardModel model){
		String loc = "";
		List<Map<String,Object>> map =  (List<Map<String, Object>>) model.getProfile().get("loc");
		if(map.size()>=1){
			loc = map.get(0).get("name").toString();
		}
		return loc;
	}

	
}
