
package com.example.selection_test1.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.selection_test1.InteresterList;
import com.example.selection_test1.MainActivity;
import com.example.selection_test1.R;
import com.example.selection_test1.R.id;
import com.example.selection_test1.R.layout;


public class InteresterListAdapter extends BaseAdapter{


	protected ArrayList<Map<String, Object>> profiles;
	protected Context mContext;
	
	public InteresterListAdapter(Context context, ArrayList<Map<String, Object>> peopleProfiles){
		mContext = context;
		profiles = new ArrayList<Map<String,Object>>();
		profiles = peopleProfiles;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return profiles.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return profiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View row=inflater.inflate(R.layout.interster_single_row, parent, false);
		//ImageView img = (ImageView)row.findViewById(R.id.pic_in_list);
		TextView name = (TextView)row.findViewById(R.id.name_in_list);
		TextView org = (TextView)row.findViewById(R.id.org_in_list);
		TextView field = (TextView)row.findViewById(R.id.field_in_list);
		TextView place = (TextView)row.findViewById(R.id.place_in_list);

		
		Button share = (Button)row.findViewById(R.id.share);
		
		
		 final Map<String, Object> temp = profiles.get(position);
		
		String imgUrl = getPicUrl(temp);
		if(imgUrl!=""){
			AQuery aq = new AQuery(row);
			aq.id(R.id.pic_in_list).progress(R.id.image_progress_bar).image(imgUrl, false, false);
		}
		
		if(temp.get("name")!=null){
			name.setText(temp.get("name").toString());	
			name.setVisibility(View.VISIBLE);
		}
		
		if(getOrg(temp)!=""){
			org.setText(getOrg(temp));
			org.setVisibility(View.VISIBLE);
		}
		 
		if(getField(temp)!=""){
			field.setText(getField(temp));
			field.setVisibility(View.VISIBLE);
		}
		
		if(getPlace(temp)!=""){
			place.setText(getPlace(temp));
			place.setVisibility(View.VISIBLE);
		}
		
		share.setOnClickListener(new OnClickListener() {	
			//Activity context = new Activity(mContext);
			
			@Override
			public void onClick(View v) {
				
				if(!descardTutorial()){
					final Dialog dialog=new Dialog(mContext);
				    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				    dialog.setContentView(R.layout.share_tutorial);
				    dialog.show();
				    
				    Button dismiss = (Button) dialog.findViewById(R.id.dismiss_tutorial_btn);
				    TextView SendTo = (TextView) dialog.findViewById(R.id.send_to_text);
				    CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.checkbox_tutorial);
				    SendTo.setText("We Have Sent Your Business Card to "+temp.get("name").toString()+"!");
	
				    checkBox.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							profiles.remove(position);
							notifyDataSetChanged();
							dialog.dismiss();
							
							SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(mContext);
							SharedPreferences.Editor editor = preferences.edit();
					        editor.putBoolean("ShareTutorial", true);
					        editor.commit();
						}
					});
				    dismiss.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							buttonEffect(v);
							profiles.remove(position);
							notifyDataSetChanged();
							dialog.dismiss();
						}
					});
	
				}else{
					buttonEffect(v);
					profiles.remove(position);
					notifyDataSetChanged();
				}
			}
		});	
		
		return row;
	}
	
	private boolean descardTutorial()
	{
	    SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(mContext);
	    boolean ranBefore = preferences.getBoolean("ShareTutorial", false);
	    /*
	    if (!ranBefore) {
	        // first time
	        SharedPreferences.Editor editor = preferences.edit();
	        editor.putBoolean("PeopleSelectionTutorial", true);
	        editor.commit();
	    }*/
	    return ranBefore;
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
		List<Map<String,Object>>map =   (List<Map<String, Object>>) profile.get("org");
		if(map == null || map.size()<=0){
			return "";
		}else{
			return map.get(0).get("name").toString();
		}
	}
	private String getField(Map<String, Object> profile){
		List<String> map =  (List<String>) profile.get("field");
		if(map!=null){
			if(map.size()>=1){
				return map.get(0).toString();
			}else{
				return "";
			}
		}else{
			return "";
		}
	}
	private String getPlace(Map<String, Object> profile){
		List<Map<String,Object>>map =   (List<Map<String, Object>>) profile.get("loc");
		if(map == null || map.size()<=0){
			return "";
		}else{
			return map.get(0).get("name").toString();
		}
	}
	public static void buttonEffect(View button){
	    button.setOnTouchListener(new OnTouchListener() {

	        public boolean onTouch(View v, MotionEvent event) {
	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN: {
	                    v.getBackground().setColorFilter(0xe0f47521,PorterDuff.Mode.SRC_ATOP);
	                    v.invalidate();
	                    break;
	                }
	                case MotionEvent.ACTION_UP: {
	                    v.getBackground().clearColorFilter();
	                    v.invalidate();
	                    break;
	                }
	            }
	            return false;
	        }
	    });
	}
}