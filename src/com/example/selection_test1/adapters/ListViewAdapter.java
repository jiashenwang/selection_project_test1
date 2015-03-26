package com.example.selection_test1.adapters;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.example.selection_test1.com.andtinder.view.ProfileUtil;
import com.example.selection_test1.ChildData.ChildType;
import com.example.selection_test1.GroupData.GroupType;
import com.example.selection_test1.R.id;
import com.example.selection_test1.R.layout;
import com.example.selection_test1.ChildData;
import com.example.selection_test1.GroupData;
import com.example.selection_test1.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewAdapter extends BaseExpandableListAdapter {

	protected List<GroupData> mData;
	protected Map<String, Object> mProfile;
	protected Context mContext;

	//protected EditType mEditType = EditType.EDIT_ALL;

	public static final int MAX_CONN_SHOW = 3;
	public static final int MAX_CHILD_SHOW = 2;

	private static final String TAG = "DetailExListAdapter";

	private String mLnConnectStr = "Connect";

	private int mCategoryIndx = 0;
	String []parentList={"Affiliations", "Education", "Social Accounts"};
	
	
	public ListViewAdapter(Context c, Map<String, Object> map){
		mContext = c;
		mProfile = map;
		mData = prepareData(map);
	}
	
	protected List<GroupData> prepareData(Map<String, Object> profile) {
		List<GroupData> dataList = new ArrayList<GroupData>();

		// convert profile format to ex-list format
		GroupData contactData = convertContactInfo(profile);
		if (contactData != null) {
			dataList.add(contactData);
			mCategoryIndx++;
		}
		GroupData bioData = convertBio(profile);
		if (bioData != null) {
			dataList.add(bioData);
			mCategoryIndx++;
		}
		GroupData orgData = convertOrg(profile);
		if (orgData != null) {
			dataList.add(orgData);
			mCategoryIndx++;
		}
		GroupData eduData = convertEdu(profile);
		if (eduData != null) {
			dataList.add(eduData);
			mCategoryIndx++;
		}
		GroupData personalData = convertPersonal(profile);
		if (personalData != null) {
			dataList.add(personalData);
			mCategoryIndx++;
		}
		GroupData socialData = null;
		GroupData otherData = null;
		List<GroupData> socialAndOther = convertSocialAndOther(profile);
		if (socialAndOther != null) {
			for (GroupData groupData : socialAndOther) {
				if (groupData.type == GroupType.SOCIAL) {
					socialData = groupData;
				} else if (groupData.type == GroupType.OTHER) {
					otherData = groupData;
				}
			}
		}

		if (socialData != null) {
			dataList.add(socialData);
			mCategoryIndx++;
		}
		GroupData connData = convertConnection(profile);
		if (connData != null) {
			dataList.add(connData);
			mCategoryIndx++;
		}
		GroupData patentData = convertPatents(profile);
		if (patentData != null) {
			dataList.add(patentData);
			mCategoryIndx++;
		}
		GroupData pubData = convertPub(profile);
		if (pubData != null) {
			dataList.add(pubData);
			mCategoryIndx++;
		}

		if (otherData != null) {
			dataList.add(otherData);
			mCategoryIndx++;
		}
		return dataList;
    }
	
	protected GroupData convertOrg(Map<String, Object> profile) {
		//List orgList = Util.verifyList(profile.get("org"));
		List orgList = verifyList(profile.get("org"));
		if (orgList == null) {
			return null;
		}
		GroupData groupData = new GroupData();
		groupData.type = GroupType.ORG;

		boolean shouldAddMoreItem = false;

		for (Map<String, Object> orgMap : (List<Map<String, Object>>) orgList) {
			String name = verifyString(orgMap.get("name"));
			if (name == null) {
				continue;
			}
			ChildData childData = new ChildData();
			groupData.children.add(childData);
			childData.parentData = groupData;
			childData.type = ChildType.TWO_TEXT;
			// name
			childData.texts.add(name);
			// more details on org
			List<String> details = new ArrayList<String>(); // temp use

			// job position
			String position = verifyString(orgMap.get("pos"));
			if (position != null) {
				details.add(position);
			}
			// time period
			String start = formatRandomTimeString(verifyString(orgMap.get("sd")));
			String end = formatRandomTimeString(verifyString(orgMap.get("ed")));
			if (start != null && end != null) {
				String period = start + " - " + end;
				details.add(period);
			}

			if (details.size() > 0) {
				childData.texts.add(StringUtils.join(details, "\n"));
			}
			// link and icon
			String linkURL = verifyString(orgMap.get("url"));
			if (linkURL != null) {
				childData.linkURL = linkURL;
			}
			String iconURL = verifyString(orgMap.get("pic"));
			if (iconURL != null) {
				childData.iconURL = iconURL;
			}

			if (groupData.children.size() > MAX_CHILD_SHOW) {
				shouldAddMoreItem = true;
				break; // only show MAX_CHILD_SHOW
			}
		} // end for

		if (groupData.children.size() > 0) {
		    try{
//				groupData.title = mContext.getResources().getString(R.string.title_org,
//				        groupData.children.size());
				/*
		    	groupData.title = mContext.getResources().getString(R.string.title_org,
				        orgList.size());*/
				groupData.title = ("ORG");
		    }catch(Exception e){
//		    	String tmpTitle = "Affiliations(" + groupData.children.size() + ")";
		    	String tmpTitle = "Affiliations(" + orgList.size() + ")";
		    	groupData.title = tmpTitle;
		        e.printStackTrace();
		    }

			if (shouldAddMoreItem) {
				// add a more item to list
				ChildData childData = new ChildData();
				childData.texts.add("Show more affiliations");
				childData.type = ChildType.MORE;
				childData.parentData = groupData;
				groupData.children.add(childData);
			}

			return groupData;

		} else {
			return null;
		}
	}
	
	protected GroupData convertEdu(Map<String, Object> profile) {
		List eduList = verifyList(profile.get("edu"));
		if (eduList == null) {
			return null;
		}
		GroupData groupData = new GroupData();
		groupData.type = GroupType.EDU;

		boolean shouldAddMoreItem = false;

		for (Map<String, Object> eduMap : (List<Map<String, Object>>) eduList) {
			String name = verifyString(eduMap.get("name"));
			if (name == null) {
				continue;
			}
			ChildData childData = new ChildData();
			groupData.children.add(childData);
			childData.parentData = groupData;
			childData.type = ChildType.TWO_TEXT;
			// name
			childData.texts.add(name);
			// more details on edu
			List<String> details = new ArrayList<String>(); // temp use

			// major and degree
			String major = verifyString(eduMap.get("major"));
			if (major != null) {
				details.add(major);
			}
			String degree = verifyString(eduMap.get("degree"));
			if (degree != null) {
				details.add(degree);
			}
			// time period
			String start = verifyString(eduMap.get("sd"));
			String end = verifyString(eduMap.get("ed"));
			if (start != null && end != null) {
				String period = start + " to " + end;
				details.add(period);
			}

			if (details.size() > 0) {
				childData.texts.add(StringUtils.join(details, "\n"));
			}
			// link and icon
			String linkURL = verifyString(eduMap.get("url"));
			if (linkURL != null) {
				childData.linkURL = linkURL;
			}
			String iconURL = verifyString(eduMap.get("pic"));
			if (iconURL != null) {
				childData.iconURL = iconURL;
			}

			if(groupData.children.size()>MAX_CHILD_SHOW){
				shouldAddMoreItem = true;
				break;
			}

		} // end for

		if (groupData.children.size() > 0) {
		    try{
//				groupData.title = mContext.getResources().getString(R.string.title_edu,
//				        groupData.children.size());
				groupData.title = "EDU";
		    }catch(Exception e){
		        e.printStackTrace();
		        groupData = null;
		    }

			if (shouldAddMoreItem) {
				// add a more item to list
				ChildData childData = new ChildData();
				childData.texts.add("Show more education");
				childData.type = ChildType.MORE;
				childData.parentData = groupData;
				groupData.children.add(childData);
			}
			return groupData;

		} else {
			return null;
		}
	}

	protected GroupData convertBio(Map<String, Object> profile) {
		Map miscMap = verifyMap(profile.get("misc"));
		if (miscMap == null) {
			return null;
		}
		String bio = verifyString(miscMap.get("bio_detail"));
		if (bio == null) {
			return null;
		}
		GroupData groupData = new GroupData();
		groupData.type = GroupType.BIO;

		ChildData childData = new ChildData();
		groupData.children.add(childData);
		childData.parentData = groupData;
		childData.type = ChildType.BIO;
		childData.texts.add(bio);

		try{
			//groupData.title = mContext.getResources().getString(R.string.title_bio);
			groupData.title = "BIO";
		}catch(Exception e){
			groupData.title = "Bio";
		}
		return groupData;
	}

	protected GroupData convertPersonal(Map<String, Object> profile) {
		Map urlclassMap = verifyMap(profile.get("urlclass"));
		if (urlclassMap == null) {
			return null;
		}
		List biosList = verifyList(urlclassMap.get("bios"));
		if (biosList == null) {
			return null;
		}

		GroupData groupData = new GroupData();
		groupData.type = GroupType.PERSONAL;

		for (Map bioMap : (List<Map>) biosList) {
			String url = verifyString(bioMap.get("url"));
			if (url == null) {
				continue;
			}
			ChildData childData = new ChildData();
			groupData.children.add(childData);
			childData.parentData = groupData;
			childData.type = ChildType.ONE_TEXT;

			childData.linkURL = url;
			childData.iconURL = getWebsiteIconURL(url);
			childData.texts.add(url);
		} // end for

		if (groupData.children.size() > 0) {
			try{
				/*
				groupData.title = mContext.getResources().getString(R.string.title_personal,
				        groupData.children.size());*/
				groupData.title = "PERSONAL";
				return groupData;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	protected GroupData convertConnection(Map<String, Object> profile) {
		List connList = verifyList(profile.get("conn"));
		if (connList == null) {
			return null;
		}
		GroupData groupData = new GroupData();
		groupData.type = GroupType.CONN;

		boolean shouldAddMoreItem = false;

		for (Map<String, Object> connMap : (List<Map<String, Object>>) connList) {
			String connName = verifyString(connMap.get("name"));
			if (connName == null) {
				continue;
			}
			ChildData childData = new ChildData();
			groupData.children.add(childData);
			childData.parentData = groupData;
			childData.type = ChildType.TWO_TEXT;
			childData.texts.add(connName);
			childData.iconURL = verifyString(connMap.get("pic"));
			childData.pid = verifyString(connMap.get("id"));

			List<String> connDesc = null;
			try {
				connDesc = ProfileUtil.connDescription(connMap, profile);
			} catch (Exception e) {
				Log.d(TAG, "", e);
			}
			if (connDesc != null && connDesc.size() > 0) {
				childData.texts.add(StringUtils.join(connDesc, "\n"));
			}
			if (groupData.children.size() >= MAX_CONN_SHOW) {
				shouldAddMoreItem = true;
				break; // only show MAX_CONN_SHOW
			}
		} // end for

		if (groupData.children.size() > 0) {

			try {
				Integer connCount = (Integer) profile.get("conn_total");
				if (connCount == null) {
					//groupData.title = mContext.getResources().getString(R.string.title_conn_nocount);
					groupData.title = "CONN_NOCOUNT";
				} else {
					//groupData.title = mContext.getResources().getString(R.string.title_conn, connCount);
					groupData.title = "TITLE_CONN";
				}
			} catch (Exception e) {
			    if(mContext!=null)
			        //groupData.title = mContext.getResources().getString(R.string.title_conn_nocount);
			    	groupData.title = "CONN_NOCOUNT";
			}
			if (shouldAddMoreItem) {
				// add a more item to list
				ChildData childData = new ChildData();
				childData.texts.add("Show All Connections");
				childData.type = ChildType.MORE;
				childData.parentData = groupData;
				groupData.children.add(childData);
			}
			return groupData;
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param profile
	 * @return social and/or other or empty, won't be null
	 */
	protected List<GroupData> convertSocialAndOther(Map<String, Object> profile) {
		List<GroupData> result = new ArrayList<GroupData>();

		Map urlclassMap = verifyMap(profile.get("urlclass"));
		if (urlclassMap == null) {
			return null;
		}

		boolean socialDataPrepareFlag = false;
		// social
		List socialList = verifyList(urlclassMap.get("social"));
		if (socialList != null) {
			GroupData socialGroupData = new GroupData();
			socialGroupData.type = GroupType.SOCIAL;

			for (Map socialMap : (List<Map>) socialList) {
				String url = verifyString(socialMap.get("url"));
				String type = verifyString(socialMap.get("type"));
				if (url == null || type == null) {
					continue;
				}
				if (type.equalsIgnoreCase("Google")) {
					type = type + "+";
				}
				String linkTitle = type + " Account";
				ChildData childData = new ChildData();
				socialGroupData.children.add(childData);
				childData.parentData = socialGroupData;
				childData.type = ChildType.ONE_TEXT;

				childData.linkURL = url;
				childData.iconURL = getWebsiteIconURL(url);
				childData.texts.add(linkTitle);
			} // end for
			if (socialGroupData.children.size() > 0) {
			    try{
			    	/*
					socialGroupData.title = mContext.getResources().getString(R.string.title_social,
					        socialGroupData.children.size());*/
			    	socialGroupData.title = "SOCIAL";
					result.add(socialGroupData);
					socialDataPrepareFlag = true;
			    }catch(Exception e){
			    	String tmpTitle = "Social Profiles (" + socialGroupData.children.size() + ")";
			        socialGroupData.title = tmpTitle;
			        e.printStackTrace();
			    }
			}
		}


		// others
		List othersList = verifyList(urlclassMap.get("others"));
		if (othersList != null) {
			GroupData otherGroupData = new GroupData();
			otherGroupData.type = GroupType.OTHER;

			int childIndx=0;
			for (Map otherMap : (List<Map>) othersList) {
				String url = verifyString(otherMap.get("url"));
				if (url == null) {
					continue;
				}
				ChildData childData = new ChildData();
				otherGroupData.children.add(childData);
				childData.parentData = otherGroupData;
				childData.type = ChildType.ONE_TEXT;

				childData.linkURL = url;
				childData.iconURL = getWebsiteIconURL(url);
				childData.texts.add(url);

				/*
				// only if there's a valid url, then we try to fetch the html page title
				if(url.startsWith("http") || url.startsWith("www")){
					int groupIndx = mCategoryIndx;
					if(socialDataPrepareFlag)
						groupIndx++;

					try {
						String titleStr = new FetchHtmlPageTitleTask(url, groupIndx, childIndx).execute().get();
						childData.texts.clear();
						childData.texts.add(titleStr);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				*/

				childIndx++;
			} // end for
			if (otherGroupData.children.size() > 0) {
			    try{
			    	/*
					otherGroupData.title = mContext.getResources().getString(R.string.title_other,
					        otherGroupData.children.size());*/
			    	otherGroupData.title = "OTHER";
					result.add(otherGroupData);
			    }catch(Exception e){
			        e.printStackTrace();
			    }
			}
		}
		return result;
	}

	protected GroupData convertPatents(Map<String, Object> profile) {
		Map urlclassMap = verifyMap(profile.get("urlclass"));
		if (urlclassMap == null) {
			return null;
		}
		List<Map> patentList = verifyList(urlclassMap.get("GPatent"));
		if (null == patentList) {
			return null;
		}

		GroupData groupData = new GroupData();
		groupData.type = GroupType.PATENT;

		for (Map patentMap : patentList) {
			String patentLink = verifyString(patentMap.get("url"));
			String patentTitle = verifyString(patentMap.get("title"));
			if (patentTitle == null || patentLink == null) {
				continue;
			}
			ChildData childData = new ChildData(ChildType.ONE_TEXT, groupData,
			        getWebsiteIconURL(patentLink), patentLink);
			groupData.children.add(childData);
			childData.texts.add(patentTitle);
		}

		if (groupData.children.size() > 0) {
			/*
			groupData.title = mContext.getResources().getString(R.string.title_patent,
			        groupData.children.size());*/
			groupData.title = "PATENT";
			return groupData;
		} else {
			return null;
		}
	}

	protected GroupData convertPub(Map<String, Object> profile) {
		Map urlclassMap = verifyMap(profile.get("urlclass"));
		if (urlclassMap == null) {
			return null;
		}
		List<Map> dblpList = verifyList(urlclassMap.get("DBLP"));
		if (null == dblpList) {
			return null;
		}

		GroupData groupData = new GroupData();
		groupData.type = GroupType.PUB;

		for (Map dblpMap : dblpList) {

			String link = verifyString(dblpMap.get("url"));
			String title = verifyString(dblpMap.get("title"));
			if (link == null || title == null) {
				continue;
			}

			ChildData childData = new ChildData(ChildType.ONE_TEXT, groupData,
			        getWebsiteIconURL(link), link);
			groupData.children.add(childData);
			childData.texts.add(title);
		} // end for

		if (groupData!=null && groupData.children.size() > 0) {
			try{
				//groupData.title = mContext.getResources().getString(R.string.title_pub);
				groupData.title = "PUB";
				String dblpLink = null;
				try {
					dblpLink = (String) ((Map) profile.get("source")).get("DBLP");
				} catch (Exception e) {
					Log.v(TAG, "error getting dblp link", e);
				}
				if (dblpLink != null) {
					// add DBLP link list item
					ChildData childData = new ChildData(ChildType.MORE, groupData, null, dblpLink);
					childData.texts.add("See All Publications");
					groupData.children.add(childData);
				}

				return groupData;
			}catch(Exception e){
				return null;
			}
		} else {
			return null;
		}
	}

	protected GroupData convertContactInfo(Map<String, Object> profile) {
		List<String> emailList = verifyList(profile.get("email"));
		List<String> phoneList = verifyList(profile.get("phone"));

		//emailList = EventUtil.getEmailsForPid(ProfileUtil.getPID(profile));
		//emailList = verifyList(emailList);
		emailList = null;
		phoneList = null;

		if (emailList == null && phoneList == null) {
			return null;
		}
		GroupData groupData = new GroupData();
		groupData.type = GroupType.CONTACT;

		if (emailList != null) {
			for (String email : emailList) {
				if (email.length() == 0) {
					continue;
				}
				ChildData childData = new ChildData();
				groupData.children.add(childData);
				childData.parentData = groupData;
				childData.type = ChildType.ONE_TEXT;

				childData.texts.add("Email: " + email);
			} // end for
		}

		if (phoneList != null) {
			for (String phone : phoneList) {
				if (phone.length() == 0) {
					continue;
				}
				ChildData childData = new ChildData();
				groupData.children.add(childData);
				childData.parentData = groupData;
				childData.type = ChildType.ONE_TEXT;

				childData.texts.add("Phone: " + phone);
			} // end for
		}

		if (groupData.children.size() > 0) {
			/*
			groupData.title = mContext.getResources().getString(R.string.title_contact,
			        groupData.children.size());*/
			groupData.title = "CONTACT";
			return groupData;
		} else {
			return null;
		}
	}

	
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mData.get(groupPosition).children.size();
	}
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mData.get(groupPosition);
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return mData.get(groupPosition).children.get(childPosition);
	}
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return groupPosition * 100 + childPosition;
	}
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		
		View groupView = null;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if (convertView != null) {
			groupView = convertView;
		} else {
			groupView = inflater.inflate(R.layout.new_detail_group_view, null);
		}
		TextView groupTitleTextView = (TextView) groupView.findViewById(R.id.group_text);

		GroupData groupData = (GroupData) getGroup(groupPosition);
		groupTitleTextView.setText(groupData.title);

		return groupView;
		
	}
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View childView = null;
		ChildData childData = (ChildData) getChild(groupPosition, childPosition);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if (childData.type == ChildType.ONE_TEXT) {
			childView = inflater.inflate(R.layout.detail_one_text_item, null);
			// fill in content
			ImageView imageView = (ImageView) childView.findViewById(R.id.image1);
			TextView textView1 = (TextView) childView.findViewById(R.id.text1);
			TextView buttonTextView = (TextView)childView.findViewById(R.id.text_button);

			if (childData.parentData.type == GroupType.CONTACT) {
				// remove indicator only for contact section
				//((RelativeLayout) childView).removeView(childView.findViewById(R.id.indicator));
			} else if(childData.parentData.type == GroupType.PERSONAL ||
					childData.parentData.type == GroupType.OTHER){
				try{
					//imageView.setImageResource(R.drawable.fa_globe_gray);
				}catch(Exception e){
					//ImageLoader.getInstance().displayImage(childData.iconURL, imageView,
					        //R.drawable.fa_globe_gray);
				}
			}else{
				//ImageLoader.getInstance().displayImage(childData.iconURL, imageView,
				        //R.drawable.default_website);
			}

			if (childData.texts.size() == 1) {
				textView1.setText(childData.texts.get(0));
			} else {
				Log.w(TAG, "childData.texts empty, some thing is wrong!");
			}

			// Handle the click listener of Linkedin connect button
			/*
			String itemText = childData.texts.get(0);
			String[] itemTextTokens = itemText.split(" ");
			if(StringUtils.containsIgnoreCase(itemTextTokens[0], "linkedin")){
				final String profileID = ProfileUtil.getPID(mProfile);
				String currentUserPid = EventUtil.getUserSignupPid();
				if(false == profileID.equals(currentUserPid)){
					buttonTextView.setVisibility(View.VISIBLE);
					buttonTextView.setText(mLnConnectStr);
					buttonTextView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v){
							String mProfileImgUrl = (String)mProfile.get("pic");
							String mProfileName = (String)mProfile.get("name");

							Intent intent = new Intent(mContext, LinkedinConnectionActivity.class);
							intent.putExtra(LinkedinConnectionActivity.PROFILE_ID, profileID);
							intent.putExtra(LinkedinConnectionActivity.PROFILE_IMAGE_URL, mProfileImgUrl);
							intent.putExtra(LinkedinConnectionActivity.PROFILE_NAME, mProfileName);
							intent.putExtra(LinkedinConnectionActivity.CONNECTION_SOURCE,
															Constants.LN_CONNECTION_SRC_EVENT);
							intent.putExtra(LinkedinConnectionActivity.CONNECTION_STATUS,
															Constants.LN_CONNECTION_STATUS_YES);
							mContext.startActivity(intent);
						}
					});
				}
			}*/
		} else if (childData.type == ChildType.TWO_TEXT) {
			childView = inflater.inflate(R.layout.detail_two_text_item, null);

			// fill in content
			ImageView imageView = (ImageView) childView.findViewById(R.id.image1);
			TextView textView1 = (TextView) childView.findViewById(R.id.text1);
			TextView textView2 = (TextView) childView.findViewById(R.id.text2);

			// icon
			
			if (childData.parentData.type == GroupType.CONN) {
				// CONN
				DisplayImageOptions option = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.p1)
					.build();
				ImageLoader imageLoader;
				imageLoader = ImageLoader.getInstance();
				imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
				ImageLoader.getInstance().displayImage(childData.iconURL, imageView,option);
			} else {
				// ORG and EDU icon
				/*int default_icon = R.drawable.fa_building_gray;
				if(childData.parentData.type == GroupType.EDU)
					default_icon = R.drawable.fa_graduation_cap_gray;
				ImageLoader.getInstance().displayImage(childData.iconURL, imageView,
				        default_icon);*/
				//Log.wtf("ORG", childData.iconURL);
				DisplayImageOptions option = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.p1)
					.build();
				ImageLoader imageLoader;
				imageLoader = ImageLoader.getInstance();
				imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
				ImageLoader.getInstance().displayImage(childData.iconURL, imageView,option);
			}
  
			if (childData.texts.size() == 1) {
				textView1.setText(childData.texts.get(0));
				textView2.setText("");
			} else if (childData.texts.size() == 2) {
				textView1.setText(childData.texts.get(0));
				textView2.setText(childData.texts.get(1));
			} else {
				Log.w(TAG, "childData.texts empty, some thing is wrong!");
			}
			if (childData.parentData.type == GroupType.CONN) {
				// hide indicator for connections
				//childView.findViewById(R.id.indicator).setVisibility(View.INVISIBLE);
			}

		} else if (childData.type == ChildType.MORE) {
			childView = inflater.inflate(R.layout.detail_one_text_item, null);
			ImageView imageView = (ImageView) childView.findViewById(R.id.image1);
			TextView textView = (TextView) childView.findViewById(R.id.text1);
			textView.setText(childData.texts.get(0));
			imageView.setVisibility(View.GONE);

		} else if (childData.type == ChildType.BIO) {
			childView = inflater.inflate(R.layout.detail_one_text_item, null);
			ImageView imageView = (ImageView) childView.findViewById(R.id.image1);
			TextView textView = (TextView) childView.findViewById(R.id.text1);
			//EditText textView = (EditText)childView.findViewById(R.id.text);
			textView.setText(childData.texts.get(0));
			imageView.setVisibility(View.GONE);

			
		} else if(childData.type == ChildType.AUTH){
			childView = inflater.inflate(R.layout.detail_social_auth, null);
			ImageView linkedinImageView = (ImageView)childView.findViewById(R.id.image_ln);
			ImageView facebookImageView = (ImageView)childView.findViewById(R.id.image_fb);
			ImageView googleplusImageView = (ImageView)childView.findViewById(R.id.image_gp);
			linkedinImageView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					//UIUtil.showShortToast(mContext, ">>>> You are trying to auth with linkedin?");
					Toast.makeText(mContext, ">>>> You are trying to auth with linkedin?", Toast.LENGTH_SHORT).show();
				}
			});
			facebookImageView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					//UIUtil.showShortToast(mContext, ">>>> You are trying to auth with facebook?");
					Toast.makeText(mContext, ">>>> You are trying to auth with facebook?", Toast.LENGTH_SHORT).show();
				}
			});
			googleplusImageView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					//UIUtil.showShortToast(mContext, ">>>> You are trying to auth with google plus?");
					Toast.makeText(mContext, ">>>> You are trying to auth with google plus?", Toast.LENGTH_SHORT).show();
				}
			});
		}
		return childView;
	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/*
	public ListViewAdapter(Context simpleCardStackAdapter) {
		// TODO Auto-generated constructor stub
		context = simpleCardStackAdapter;
	}

	public ListViewAdapter(SimpleCardStackAdapter simpleCardStackAdapter) {
		// TODO Auto-generated constructor stub
		context = simpleCardStackAdapter.getContext();
	}

	public ListViewAdapter(Context context, Person p) {
		// TODO Auto-generated constructor stub
		this.context = context;
		person = new Person(p.getPersonId(), p.getPersonName(),
				p.getPersonMid(), p.getPersonFields(), p.getPersonWork(), p.getPersonEdu());
		works = (ArrayList<Work>) person.getPersonWork().clone();
		education = (ArrayList<Education>) person.getPersonEdu().clone();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return parentList.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(parentList[groupPosition]=="Affiliations"){
			return works.size();
		}else if(parentList[groupPosition]=="Education"){
			return education.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return parentList[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		if(parentList[groupPosition]=="Affiliations"){
			return works.get(childPosition);
		}else if(parentList[groupPosition]=="Education"){
			return education.get(childPosition);
		}else{
			return "";
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		//\ TODO Auto-generated method stub
		
		TextView tv = new TextView(context);
		tv.setText(parentList[groupPosition]);
		tv.setPadding(250, 30, 30, 30);
		tv.setBackgroundResource(R.color.whova_blue);
		tv.setTextColor(Color.WHITE);
		return tv;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView tv = new TextView(context);
		tv.setPadding(120, 30, 30, 30);
		
		
		if(parentList[groupPosition]=="Affiliations"){
			String tempCompany = works.get(childPosition).getcompany();
			String tempPeriod = "";
			if(works.get(childPosition).ifCurrent()){
				tempPeriod = works.get(childPosition).getStartYear() + " - Present";
			}else{
				tempPeriod = works.get(childPosition).getStartYear() + " - " +
						works.get(childPosition).getEndYear();
			}
			String tempPos = works.get(childPosition).getPosition();
			tv.setLines(3);
			tv.setText(tempCompany+"\n"+tempPos+"\n"+tempPeriod);

		}else if(parentList[groupPosition]=="Education"){
			String tempSchool = education.get(childPosition).getSchoolName();
			String tempMajor = "";
			String tempPeriod = "";
			for(int i=0; i<education.get(childPosition).getMajor().size(); i++){
				if(i==0)
					tempMajor = education.get(childPosition).getMajor().get(i);
				else
					tempMajor += ", "+ education.get(childPosition).getMajor().get(i);
			}
			if(education.get(childPosition).ifCurrent()){
				tempPeriod = education.get(childPosition).getStartYear() + " - Present";
			}else{
				tempPeriod = education.get(childPosition).getStartYear() + " - " +
						education.get(childPosition).getEndYear();
			}
			tv.setLines(3);
			tv.setText(tempSchool+"\n"+tempMajor+"\n"+tempPeriod);
		}else{

		}
		
		tv.setTextColor(Color.BLACK);
		return tv;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}*/
	
	public static String verifyString(Object o) {

		if (o != null && o instanceof String && ((String) o).length() > 0) {

			return (String) o;

		} else {

			return null;

		}

	}
	public static List verifyList(Object o) {

		if (o != null && o instanceof List && ((List) o).size() > 0) {

			return (List) o;

		} else {

			return null;

		}

	}



		public static Map verifyMap(Object o) {

		if (o != null && o instanceof Map && ((Map) o).size() > 0) {

			return (Map) o;

		} else {

			return null;

		}

	}
		
	public static String formatRandomTimeString(String timeStr){

		if(timeStr==null || timeStr.length()==0)

			return timeStr;

		if(timeStr.contains("-")){

			String[] timeStrTokens = timeStr.split("-");

			String yearStr = timeStrTokens[0];

			String monthStr = randomStringToMonthName(timeStrTokens[1]);

			return monthStr + ", " + yearStr;

		}

		return timeStr;

	}
	
	protected static String randomStringToMonthName(String monthStr){

		if(monthStr==null || monthStr.length()==0)

			return monthStr;

		try{

			int monthIndx = Integer.valueOf(monthStr) - 1;

			return MONTH_NAME_LIST[monthIndx];

		}catch(Exception e){

			e.printStackTrace();

		}

			return monthStr;

		}



	public static final String[] MONTH_NAME_LIST = {"Jan", "Feb", "Mar",

	"Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	
	public static String getWebsiteIconURL(String websiteURL) {



		if (websiteURL == null || websiteURL.length() == 0) {

			return null;

		}



	String[] slashSeparate = StringUtils.split(websiteURL, "/");

		if (slashSeparate.length >= 2) {

			String[] dotSeparate = StringUtils.split(slashSeparate[1], ".");

			if (dotSeparate.length >= 2) {
	
				int length = dotSeparate.length;
		
				/*
		
				String iconURL = String.format(
		
				"https://plus.google.com/_/favicon?domain=%1$s.%2$s",
		
				dotSeparate[length - 2], dotSeparate[length - 1]);
		
				*/
		
				String iconURL = String.format("http://www.google.com/s2/favicons?domain=www.%1$s.%2$s",
		
				dotSeparate[length-2], dotSeparate[length -1]);
		
				Log.v(TAG, "iconURL: " + iconURL);
		
				return iconURL;
	
			}

		}

		return null;

	}







}
