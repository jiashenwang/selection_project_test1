package com.example.selection_test1.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.selection_test1.R;
import com.example.selection_test1.R.id;
import com.example.selection_test1.R.layout;
import com.example.selection_test1.com.andtinder.model.CardModel;
import com.example.selection_test1.com.andtinder.view.ProfileUtil;


public class ListAdapter extends BaseAdapter{


	protected ArrayList<Map<String, Object>> profiles;
	protected Context mContext;
	
	public ListAdapter(Context context, ArrayList<Map<String, Object>> peopleProfiles){
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row=inflater.inflate(R.layout.single_row, parent, false);
		//ImageView img = (ImageView)row.findViewById(R.id.pic_in_list);
		TextView name = (TextView)row.findViewById(R.id.name_in_list);
		TextView org = (TextView)row.findViewById(R.id.org_in_list);
		TextView field = (TextView)row.findViewById(R.id.field_in_list);
		TextView place = (TextView)row.findViewById(R.id.place_in_list);
		
		 Map<String, Object> temp = profiles.get(position);
			
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
		
				
		
		return row;
	}
	
	public ArrayList<Map<String, Object>> getProfiles(){
		return profiles;
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
		List<String>map = (List<String>) profile.get("summary");
		if(map!=null && map.size()>=1){
			return map.get(0).toString();
		}else{
			return "";
		}
	}
	private String getField(Map<String, Object> profile){
		List<String>map = (List<String>) profile.get("summary");
		if(map!=null && map.size()>=2){
			return map.get(1).toString();
		}else{
			return "";
		}
	}
	private String getPlace(Map<String, Object> profile){
		List<String>map = (List<String>) profile.get("summary");
		if(map!=null && map.size()>=3){
			return map.get(2).toString();
		}else{
			return "";
		}
	}
}