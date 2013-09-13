package org.baderlab.csapps.socialnetwork.model;

import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * All the categories currently supported
 * by the Social Network App. 
 * <br>NOTE: IPs are merely used to identify 
 * each attribute. They do not have a 
 * hidden significance.
 * Values can therefore be changed without
 *  compromising functionality.
 * @author Victor Kofia
 */
public class Category {
	/**
	 * Academia (IP = 74.125.226.112)
	 */
	final public static int ACADEMIA = (74 << 24) + (125 << 16) + (226 << 8) + 112;
	/**
	 * Default category
	 */
	final public static int DEFAULT = -1;
	/**
	 * Faculty category 
	 */
	final public static int FACULTY = -2;
	/**
	 * Incites (IP = 84.18.180.87)
	 */
	final public static int INCITES = (84 << 24) + (18 << 16) + (180 << 8) + 87;
	/**
	 * LinkedIn (IP = 216.52.242.80)
	 */
	final public static int LINKEDIN = (216 << 24) + (52 << 16) + (242 << 8) + 80;
	/**
	 * Pubmed (IP = 130.14.29.110)
	 */
	final public static int PUBMED = (130 << 24) + (14 << 16) + (29 << 8) + 110;
	/**
	 * Scopus (IP = 198.185.19.57)
	 */
	final public static int SCOPUS = (198 << 24) + (185 << 16) + (19 << 8) + 57;
	/**
	 * Twitter (IP = 199.59.150.39)
	 */
	final public static int TWITTER = (199 << 24) + (59 << 16) + (150 << 8) + 39;
	/**
	 * Youtube (IP = 74.125.226.101)
	 */
	final public static int YOUTUBE = (74 << 24) + (125 << 16) + (226 << 8) + 101;
	/** 
	 * A category map 
	 *<br> key: String representation of category
	 *<br> value: category ID
	 */
	private static Map<String, Integer> categoryMap = null;
	
	/**
	 * Create default info panel
	 * @param null
	 * @return JPanel defaultInfoPanel
	 */
	public static JPanel createDefaultInfoPanel() {
		JPanel defaultInfoPanel = new JPanel();
		defaultInfoPanel.setName("--SELECT CATEGORY--");
		return defaultInfoPanel;
	}

	/**
	 * Create LinkedIn info panel
	 * @param null
	 * @return JPanel linkedInInfoPanel
	 */
	public static JPanel createLinkedInInfoPanel() {
		JPanel linkedInInfoPanel = new JPanel();
		linkedInInfoPanel.setName("LinkedIn");
		linkedInInfoPanel.setBorder(BorderFactory.createTitledBorder("LinkedIn"));
		return linkedInInfoPanel;
	}

	/**
	 * Create Twitter info panel
	 * @param null
	 * @return JPanel twitterInfoPanel
	 */
	public static JPanel createTwitterInfoPanel() {
		JPanel twitterInfoPanel = new JPanel();
		twitterInfoPanel.setName("Twitter");
		twitterInfoPanel.setBorder(BorderFactory.createTitledBorder("Twitter"));
		return twitterInfoPanel;
	}

	/**
	 * Create Youtube info panel
	 * @param null
	 * @return JPanel youtubeInfoPanel
	 */
	public static JPanel createYoutubeInfoPanel() {
		JPanel youtubeInfoPanel = new JPanel();
		youtubeInfoPanel.setName("Youtube");
		youtubeInfoPanel.setBorder(BorderFactory.createTitledBorder("Youtube"));
		return youtubeInfoPanel;
	}
	
	/**
	 * Get unique id (numeral) associated with category
	 * @param String category
	 * @return int categoryID
	 */
	public static int getCategoryID(String category) {
		if (Category.categoryMap == null) {
			Category.categoryMap = new HashMap<String, Integer>();
			String[] columns = new String[] {"--SELECT CATEGORY--",
										     "Academia", "Twitter", 
											 "LinkedIn", "Youtube", 
											 "Incites", "Scopus", 
											 "Pubmed"};
			int[] ids = new int[] {DEFAULT, ACADEMIA, TWITTER, LINKEDIN, 
					               YOUTUBE, INCITES, SCOPUS, PUBMED};
			for (int i = 0; i < 7; i++) {
				categoryMap.put(columns[i], ids[i]);
			}
		}
		return categoryMap.get(category);
	}

	/**
	 * Get list of all categories currently supported by app
	 * @param null
	 * @return List categoryList
	 */
	public static String[] getCategoryList() {
		String[] categoryList = {"Academia"};
		return categoryList;
	}

	/**
	 * Get list of search filters.
	 * Filter type varies with category.
	 * @param int selectedCategory
	 * @return String[] searchFilterList
	 */
	public static String[] getSearchFilterList(int selectedCategory) {
		String[] searchFilterList = null;
		switch (selectedCategory) {
			case Category.DEFAULT:
				searchFilterList = new String[] {"--SELECT FILTER--"};
				break;
			case Category.ACADEMIA:
				searchFilterList = new String[] {"Authors"};
				break;
		}
		return searchFilterList;
	}

	/**
	 * Return string representation of category
	 * @param int categoryID
	 * @return String category
	 */
	public static String toString(int categoryID) {
		String category = null;
		switch (categoryID) {
			case Category.DEFAULT:
				category = "--SELECT CATEGORY--";
				break;
			case Category.ACADEMIA:
				category = "Academia";
				break;
			case Category.INCITES:
				category = "Incites";
				break;
			case Category.SCOPUS:
				category = "Scopus";
				break;
			case Category.PUBMED:
				category = "Pubmed";
				break;
		}
		return category;
	}

}