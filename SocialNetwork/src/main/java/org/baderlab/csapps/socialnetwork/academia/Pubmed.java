package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;

/**
 * Methods & fields for manipulating PubMed data
 * @author Victor Kofia
 */
public class Pubmed {
	/**
	 * The author of a specific publication. Globally referenced to enable 
	 * addition of multiple authors into a publication
	 */
	private static Author author = null;
	/**
 	 * A publication's journal
 	 */
 	private static String journal = null;
	/**
	 * A list containing all authors found in a particular publication
	 */
	private static ArrayList<Author> pubAuthorList = new ArrayList<Author>();
	/**
	 * A publication's date
	 */
 	private static String pubDate = null;
	/**
	 * A list containing all the results that search session has yielded
	 */
	private static List<Publication> pubList = new ArrayList<Publication>();
	/**
	 * Unique queryKey. Necessary for retrieving search results
	 */
	private static String queryKey = null;
	/**
	 * The number of UIDs returned in search at one go
	 */
	private static String retMax = null;
	/**
	 * The index of the first record returned in search
	 */
	private static String retStart = null;
	/**
 	 * A publication's title
 	 */
 	private static String title = null;
 	/**
	 * The total number of publications found in search
	 */
	private static String totalPubs = null;
 	/**
	 * Unique WebEnv. Necessary for retrieving search results
	 */
	private static String webEnv = null;
 	
 	/**
	 * Commit search once values for queryKey, webEnv, retStart and retMax
	 * have been found
	 * @param null
	 * @return null
	 */
	public static void commitPubMedSearch() {
		// Create new SAX Parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			if (Integer.parseInt(totalPubs) > 500) {
				// WIP (Work In Progress)
				// On the event that a search yields 500+ publications, these publications will
				// need to be accessed incrementally as pubmed places sanctions on people who 
				// request XML files that are too large
			}
			else {
				// Use newly discovered queryKey and webEnv to build a tag
				Tag tag = new Tag(queryKey, webEnv, retStart, retMax);
				System.out.println("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed" + tag );
				// Load all publications at once
				saxParser.parse("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed" + tag , getPublicationHandler());
			}
		} catch (ParserConfigurationException e) {
			Cytoscape.notifyUser("Encountered temporary server issues. Please try again some other time.");
		} catch (SAXException e) {
			Cytoscape.notifyUser("Encountered temporary server issues. Please try again some other time.");
		} catch (IOException e) {
			Cytoscape.notifyUser("Unable to connect to PubMed. Please check your internet connection.");
		}
	}
 	
 	/**
 	 * Return a list of all the publications (& co-authors) found for the specified authorName, 
 	 * MeSH term or Institution name.
 	 * @param String searchTerm
 	 * @return List pubList
 	 */
 	public static List<Publication> getListOfPublications(String searchTerm) {
		// Clear pubList & pubAuthorList
 		pubList.clear();
 		pubAuthorList.clear();
 		//Query
		Query query = new Query(searchTerm);
		try {
			// Create new SAXParser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser;
			saxParser = factory.newSAXParser();
			// Get Query Key & Web Env
			System.out.println("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=" + query);
			saxParser.parse("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=" + query, getSearchHandler());
			// Once all required fields have been filled commit to search
			commitPubMedSearch();
		} catch (ParserConfigurationException e) {
			Cytoscape.notifyUser("Pubmed.getListOfPublications() ran into some trouble! Parser Configuration Exception!!");
		} catch (SAXException e) {
			Cytoscape.notifyUser("Pubmed.getListOfPublications() ran into some trouble! SAX Exception!!");
		} catch (IOException e) {
			Cytoscape.notifyUser("Pubmed.getListOfPublications() ran into some trouble! IOException!!");
		}
		// Return all results
		return pubList;
 	}
 	
	/**
	 * Get publication handler
	 * @param null
	 * @return DefaultHandler publicationHandler
	 */
	public static DefaultHandler getPublicationHandler() {
		
		DefaultHandler publicationHandler = new DefaultHandler() {
			boolean isPubDate = false, isAuthor = false, isTitle = false, isJournal = false;
			
			public void characters(char ch[], int start, int length) throws SAXException {
				// start and length give both the starting index and the length (respectively)
				// of the chunk of characters inside the character array that are not elements
				if (isPubDate) {
					pubDate = new String(ch, start, length);
					isPubDate = false;
				}
				if (isAuthor) {
					author = new Author(new String(ch, start, length), Author.PUBMED);
					// add author to publication author list
					if (! pubAuthorList.contains(author)) {						
						pubAuthorList.add(author);
					}
					isAuthor = false;
				}
				if (isJournal) {
					journal = new String(ch, start, length);
					isJournal = false;
				}
				if (isTitle) {
					title = new String(ch, start, length);
					isTitle = false;
				}
			}
			
			/**
			 * Returns true iff attributes contains the specified  text
			 * @param Attribute attributes
			 * @param String text
			 * @return
			 */
			public boolean contains(Attributes attributes, String text) {
				for (int i = 0; i < attributes.getLength(); i++) {
					if(attributes.getValue(i).equalsIgnoreCase(text)) {
						return true;
					}
				}
				return false;
			}

			public void endElement(String uri, String localName, String qName) throws SAXException {
				// qName stores the element's actual designation
				if (qName.equalsIgnoreCase("DocSum")) {
					pubList.add(new Publication(title, pubDate, journal, null, null, pubAuthorList));
					pubAuthorList.clear();
				}
			}

			public void startElement(String uri, String localName, String qName, Attributes attributes) 
					                                                              throws SAXException {
				// qName stores the element's actual designation
				if (contains(attributes, "Author")) {
					isAuthor = true;
				}
				if (contains(attributes, "FullJournalName")) {
					isJournal = true;
				}
				if (contains(attributes, "PubDate")) {
					isPubDate = true;
				}
				if (contains(attributes, "Title")) {
					isTitle = true;
				}
			}
		};
		
		return publicationHandler;
	}
	
	
	/**
	 * Get search handler
	 * @param null
	 * @return DefaultHandler searchHandler
	 */
	public static DefaultHandler getSearchHandler() throws SAXException, IOException, ParserConfigurationException {
		DefaultHandler searchHandler = new DefaultHandler() {
			int i = 0;
			boolean isQueryKey = false, isWebEnv = false, isTotalPubs = false;
			
			public void characters(char ch[], int start, int length) throws SAXException {
				// start and length give both the starting index and the length (respectively)
				// of the chunk of characters inside the character array that are not elements
				if (isTotalPubs) {
					totalPubs = new String(ch, start, length);
					isTotalPubs = false;
				}
				if (isQueryKey) {
					queryKey = new String(ch, start, length);
					isQueryKey = false;
				}
				if (isWebEnv) {
					webEnv = new String(ch, start, length);
					isWebEnv = false;
				}
			}
			
			public void endElement(String uri, String localName, String qName) throws SAXException {
			
			}
			
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if (i == 0 && qName.equalsIgnoreCase("Count")) {
					isTotalPubs = true;
					i += 1;
				}
				if (qName.equalsIgnoreCase("QueryKey")) {
					isQueryKey = true;
				}
				if (qName.equalsIgnoreCase("WebEnv")) {
					isWebEnv = true;
				}
			}
		};
		
		return searchHandler;
		
	}
	
	/**
 	 * Return total # of publications yielded from search. Method has to be called right
 	 * after search has been commited. If called at any other time, there is no guarantee
 	 * that the value returned will be accurate and/or valid.
 	 * @param null
 	 * @return int totalPubs
 	 */
 	public static int getTotalPubs() {
 		if (totalPubs == null) {
 			return -1;
 		} else {
 			return Integer.parseInt(Pubmed.totalPubs);
 		}
 	}
	
}
