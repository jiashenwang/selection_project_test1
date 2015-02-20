package com.example.selection_test1.com.andtinder.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.selection_test1.R;
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

		((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
		((TextView) convertView.findViewById(R.id.title)).setText(model.getPerson().getPersonName());
		//((TextView) convertView.findViewById(R.id.description)).setText(model.getDescription());
		
		String current = "";
		String positionName = "";
		if(model.getPerson().getPersonWork().size() == 0){
			for(int i=0; i<model.getPerson().getPersonEdu().size(); i++){
				if(model.getPerson().getPersonEdu().get(i).ifCurrent()){
					current = model.getPerson().getPersonEdu().get(i).getSchoolName();
					for(int j=0; j<model.getPerson().getPersonEdu().get(i).getMajor().size(); j++){
						if(j==0)
							positionName = model.getPerson().getPersonEdu().get(i).getMajor().get(j);
						else
							positionName = positionName + ", " + model.getPerson().getPersonEdu().get(i).getMajor().get(j);
					}
					break;
				}
			}
		}else{
			for(int i=0; i<model.getPerson().getPersonWork().size(); i++){
				if(model.getPerson().getPersonWork().get(i).ifCurrent()){
					current = model.getPerson().getPersonWork().get(i).getcompany();
					positionName = model.getPerson().getPersonWork().get(i).getPosition();
				}
				break;
			}
		}
		((TextView) convertView.findViewById(R.id.position)).setText(current);
		((TextView) convertView.findViewById(R.id.company)).setText(positionName);

		ExpandableListView person_info = (ExpandableListView)convertView.findViewById(R.id.person_info);
		person_info.setAdapter(new ListViewAdapter(convertView.getContext(), model.getPerson()));
		person_info.expandGroup(0);
		person_info.expandGroup(1);
		person_info.expandGroup(2);
		return convertView;
	}
}
