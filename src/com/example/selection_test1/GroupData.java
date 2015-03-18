package com.example.selection_test1;

import java.util.ArrayList;
import java.util.List;


public class GroupData {
	public GroupType type;
	public List<ChildData> children;
	public String title;

	public GroupData() {
		children = new ArrayList<ChildData>();
	}
	public enum GroupType {

		ORG, EDU, PERSONAL, SOCIAL, CONN, PATENT, PUB, OTHER, CONTACT, BIO,  SOCIAL_AUTH

		}
	
}