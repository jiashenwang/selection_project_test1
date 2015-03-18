package com.example.selection_test1.com.andtinder.view;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.util.Log;

public class ProfileUtil {

	public static Set<String> ALL_SOCIAL_KEYS;
	public static Set<String> ALL_GENERAL_PAGE_KEYS;

	private static String TAG = "ProfileUtil";

	private static Map<String, String> CONN_DESC_LOOPUP;

	private static XPathExpression exprHref;
	private static XPathExpression exprTitle;
	private static DocumentBuilder docBuilder;

	static {
		CONN_DESC_LOOPUP = new HashMap<String, String>();
		CONN_DESC_LOOPUP.put("Intelius", "Family member");
		CONN_DESC_LOOPUP.put("LinkedIn", "Close colleagues");
		CONN_DESC_LOOPUP.put("Facebook", "Connected on Facebook");
		CONN_DESC_LOOPUP.put("Google", "Connected on Google+");
		CONN_DESC_LOOPUP.put("Twitter", "Connected on Twitter");

		ALL_SOCIAL_KEYS = new HashSet<String>();
		ALL_SOCIAL_KEYS.add("Facebook");
		ALL_SOCIAL_KEYS.add("LinkedIn");
		ALL_SOCIAL_KEYS.add("Google");
		ALL_SOCIAL_KEYS.add("Twitter");
		ALL_SOCIAL_KEYS.add("Quora");
		ALL_SOCIAL_KEYS.add("Github");
		ALL_SOCIAL_KEYS.add("Pinterest");
		ALL_SOCIAL_KEYS.add("Foursquare");
		ALL_SOCIAL_KEYS.add("Meetup");
		ALL_SOCIAL_KEYS.add("AngelList");
		ALL_SOCIAL_KEYS.add("Slideshare");

		ALL_GENERAL_PAGE_KEYS = new HashSet<String>();
		ALL_GENERAL_PAGE_KEYS.add("DomainUniv");
		ALL_GENERAL_PAGE_KEYS.add("DomainGeneral");
		ALL_GENERAL_PAGE_KEYS.add("Homepage");

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			docBuilder = factory.newDocumentBuilder();

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			exprHref = xpath.compile("/a/@href");
			exprTitle = xpath.compile("/a/@title");
		} catch (Exception e) {
			Log.w(TAG, "xpath init error", e);
		}

	}

	/**
	 * verify profile, should at least have id and name as key, this method
	 * might change profile for some data cleaning purpose
	 * 
	 * @param profile
	 * @return the origin profile if profile is legal, or null if profile is
	 *         illegal
	 */
	public static Map<String, Object> verifyProfile(Map<String, Object> profile) {
		if (profile == null) {
			return null;
		}
		if (profile.get("id") == null || profile.get("name") == null) {
			return null;
		}

		return profile;
	}

	public static String getPID(Map<String, Object> profile) {
		if (profile == null) {
	        return "";
        }
		Object object = profile.get("id");
		if (object != null) {
			return object.toString();
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @param connMap
	 *            inside "conn" list
	 * @param profile
	 * @return could be empty list
	 */
	public static List<String> connDescription(Map<String, Object> connMap,
	        Map<String, Object> profile) {

		List<String> results = new ArrayList<String>();
		int patents = 0, otherSource = 0;
		boolean isHomePage = false, isAdvisor = false, isTeam = false;

		List idxList = verifyList(connMap.get("idx"));
		if (idxList == null) {
			return results;
		}
		Map<String, Object> typeMap = verifyMap(profile.get("type"));
		Map othersMap = verifyMap(connMap.get("others")); // could be null

		for (String idx : (List<String>) idxList) {
			try {
				String source = idx.split(":")[0].replace("+", "");
				String result = null;

				String lookupResult = CONN_DESC_LOOPUP.get(source);
				if (lookupResult != null) {
					result = lookupResult;
				} else {
					if ("DBLP".equals(source)) {
						Integer numOfPaper = (Integer) othersMap.get(idx);
						if (numOfPaper > 1) {
							result = "Coauthored " + numOfPaper + " papers";
						} else {
							result = "Coauthored 1 paper";
						}
					} // DBLP end
					else if ("GPatent".equals(source)) {
						patents++;
					} else if (!isHomePage && typeMap != null
					        && "personal".equals(typeMap.get(idx))) {
						result = "Linked on homepage";
						isHomePage = true;
					} else if (!isAdvisor && typeMap != null
					        && "univ_homepage".equals(typeMap.get(idx))
					        && "advisor".equals(othersMap.get(idx))) {
						result = "Advisor";
						isAdvisor = true;
					} else if (!isTeam && typeMap != null
					        && "comp_multi_bio".equals(typeMap.get(idx))) {
						result = "Same management team";
						isTeam = true;
					} else {
						otherSource++;
					}
				}
				if (result != null) {
					results.add(result);
				}
			} catch (Exception e) {
				Log.d(TAG, "conn desc error", e);
				continue;
			}
		}

		if (patents > 0) {
			if (patents > 1) {
				results.add("Filed " + patents + " patents together");
			} else {
				results.add("Filed 1 patent together");
			}
		}

		if (otherSource > 0) {
			if (idxList.size() > otherSource) {
				if (otherSource == 1) {
					results.add("Connected on 1 other page");
				} else {
					results.add("Connected on " + otherSource + " other page");
				}
			} else {
				// otherSouce == total number of source
				if (otherSource == 1) {
					results.add("Connected on 1 web page");
				} else {
					results.add("Connected on " + otherSource + " web page");
				}
			}
		}

		return results;
	}

	public static String profileSummary(Map<String, Object> profile) {
		List<String> summary = new ArrayList<String>();

		// location
		List<Map<String, Object>> locationsList = verifyList(profile.get("loc"));
		if (locationsList != null) {
			summary.add((String) locationsList.get(0).get("name"));
		}
		// org
		List<Map<String, Object>> orgList = verifyList(profile.get("org"));
		if (orgList != null) {
			for (Map<String, Object> oneOrg : orgList) {
				summary.add((String) oneOrg.get("name"));
				break;
			}
		}
		// field
		List<String> fieldList = verifyList(profile.get("field"));
		if (fieldList != null) {
			summary.add(fieldList.get(0));
		}
		// edu
		List<Map<String, Object>> eduList = verifyList(profile.get("edu"));
		if (eduList != null) {
			for (Map<String, Object> oneEdu : eduList) {
				summary.add((String) oneEdu.get("name"));
				break;
			}
		}
		return StringUtils.join(summary, "\n");
	}

	/**
	 * use xpath to get link and title from html string <a
	 * href='http://doi.acm.org/10.1145/2025113.2025121' title = 'How do fixes
	 * become bugs?'>How do fixes become bugs?</a>
	 * 
	 * @param htmlString
	 * @return [0]: link, [1]: title
	 * @throws Exception
	 */
	public static String[] parsePubHTML(String htmlString) throws Exception {

		Document doc = docBuilder.parse(new InputSource(new ByteArrayInputStream(htmlString
		        .getBytes("utf-8"))));

		String href = exprHref.evaluate(doc);
		String title = exprTitle.evaluate(doc);

		String[] result = { href, title };
		return result;
	}
	
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
}
