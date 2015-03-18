package com.example.selection_test1;

import java.util.ArrayList;
import java.util.List;


public class ChildData {
	public ChildType type;
	public GroupData parentData;
	public List<String> texts;
	public String iconURL;
	public String linkURL;
	/**
	 * only used for connection navigation
	 */
	public String pid;

	public ChildData() {
		texts = new ArrayList<String>();
	}

	public ChildData(ChildType type, GroupData parent, String iconURL, String linkURL) {
		this();
		this.type = type;
		this.parentData = parent;
		this.iconURL = iconURL;
		this.linkURL = linkURL;
	}
	
	public enum ChildType {

		ONE_TEXT, TWO_TEXT, MORE, BIO, AUTH

	}
}