package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import main.java.org.baderlab.csapps.socialnetwork.Category;

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
	 * The author of a specific publication. This variable is globally referenced to allow for 
	 * multiple additions in to a publication
	 */
	private Author author = null;
	/**
 	 * A publication's journal
 	 */
 	private String journal = null;
	/**
	 * A list containing all authors found in a particular publication
	 */
	private ArrayList<Author> pubAuthorList = new ArrayList<Author>();
	/**
	 * A publication's date
	 */
 	private String pubDate = null;
 	/**
	 * A list containing all the results that search session has yielded
	 */
	private List<Publication> pubList = new ArrayList<Publication>();
	/**
	 * Unique queryKey. Necessary for retrieving search results
	 */
	private String queryKey = null;
	/**
	 * The number of UIDs returned in search at one go
	 */
	private String retMax = null;
	/**
	 * The index of the first record returned in search
	 */
	private String retStart = null;
	/**
 	 * A publication's total number of citations
 	 */
 	private String timesCited = null;
	/**
 	 * A publication's title
 	 */
 	private String title = null;
 	/**
	 * The total number of publications found in search
	 */
	private String totalPubs = null;
 	/**
	 * Unique WebEnv. Necessary for retrieving search results
	 */
	private String webEnv = null;
	
	/**
	 * Create a new Pubmed search session
	 * @param String searchTerm
	 * @return null
	 */
	public Pubmed(String searchTerm) {
 		//Query
		Query query = new Query(searchTerm);
		try {
			// Create new SAXParser
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			// Get Query Key & Web Env
			saxParser.parse("http://eutils.ncbi.nlm.nih.gov/entrez" +
	        "/eutils/esearch.fcgi?db=pubmed&term=" + query, getSearchHandler());
			// Once all required fields have been filled commit to search
			commitPubMedSearch();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Encountered temporary server issues. Please " +
		             "try again some other time.");
		} catch (SAXException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Encountered temporary server issues. Please " +
		             "try again some other time.");
		} catch (IOException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Unable to connect to PubMed. Please check your " +
		             "internet connection.");
		}
	}
 	
 	/**
	 * Commit search using: (queryKey, webEnv, retStart and retMax)
	 * @param null
	 * @return null
	 */
	public void commitPubMedSearch() {
		try {
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			if (Integer.parseInt(totalPubs) > 500) {
				// WIP (Work In Progress)
				// On the event that a search yields 500+ publications, these publications will
				// need to be accessed incrementally as pubmed places sanctions on users with
				// extremely large requests
			}
			else {
				// Use newly discovered queryKey and webEnv to build a tag
				Tag tag = new Tag(queryKey, webEnv, retStart, retMax);
				System.out.println("http://eutils.ncbi.nlm.nih.gov/entrez" +
						"/eutils/esummary.fcgi?db=pubmed" + tag );
				// Load all publications at once
				saxParser.parse("http://eutils.ncbi.nlm.nih.gov/entrez/eutils" +
					"/esummary.fcgi?db=pubmed" + tag , getPublicationHandler());
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Encountered temporary server issues. Please " +
					             "try again some other time.");
		} catch (SAXException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Encountered temporary server issues. Please " +
					             "try again some other time.");
		} catch (IOException e) {
			e.printStackTrace();
			Cytoscape.notifyUser("Unable to connect to PubMed. Please check your " +
					             "internet connection.");
		}
	}
 	
 	/**
 	 * Return a list of all the publications (& co-authors) found for User's specified 
 	 * authorName, MeSH term or Institution name.
 	 * @param null
 	 * @return List pubList
 	 */
 	public List<Publication> getListOfPublications() {	// Return all results
		return pubList;
 	}
 	
	/**
	 * Get publication handler
	 * @param null
	 * @return DefaultHandler publicationHandler
	 */
	public DefaultHandler getPublicationHandler() {
		DefaultHandler publicationHandler = new DefaultHandler() {
			
			/**
			 * XML Parsing variables. Used to temporarily store data.
			 */
			boolean isPubDate = false, isAuthor = false, isTitle = false, isJournal = false, 
					isTimesCited = false;
			
			// Reset variable contents
			public void startElement(String uri, String localName, String qName, Attributes attributes) 
					                                                              throws SAXException {
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
				if (contains(attributes, "PmcRefCount")) {
					isTimesCited = true;
				}
			}
			
			// Collect tag contents (if applicable)
			public void characters(char ch[], int start, int length) throws SAXException {
				if (isPubDate) {
					pubDate = new String(ch, start, length);
					isPubDate = false;
				}
				if (isAuthor) {
					author = new Author(new String(ch, start, length), Category.PUBMED);
					// Add author to publication author list
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
				if (isTimesCited) {
					timesCited = new String(ch, start, length);
					isTimesCited = false;
				}
			}
			
			// Create new publication and add it to overall publist
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (qName.equalsIgnoreCase("DocSum")) {
					pubList.add(new Publication(title, pubDate, journal, timesCited, null, pubAuthorList));
					pubAuthorList.clear();
				}
			}
			
			/**
			 * Returns true iff attributes contains the specified  text
			 * @param Attribute attributes
			 * @param String text
			 * @return Boolean bool
			 */
			public boolean contains(Attributes attributes, String text) {
				for (int i = 0; i < attributes.getLength(); i++) {
					if(attributes.getValue(i).equalsIgnoreCase(text)) {
						return true;
					}
				}
				return false;
			}
		};
		
		return publicationHandler;
		
	}
	
	/**
	 * Get search handler
	 * @param null
	 * @return DefaultHandler searchHandler
	 */
	public DefaultHandler getSearchHandler() throws SAXException, 
	                                                IOException, 
	                                                ParserConfigurationException {
		DefaultHandler searchHandler = new DefaultHandler() {
			
			/**
			 * XML Parsing variables. Used to temporarily store data. 
			 */
			boolean isQueryKey = false, isWebEnv = false, isTotalPubs = false;
			
			// Reset XML variables
			public void startElement(String uri, 
									 String localName, 
									 String qName, 
					                 Attributes attributes) throws SAXException {
				if (qName.equalsIgnoreCase("Count")) {
					isTotalPubs = true;
				}
				if (qName.equalsIgnoreCase("QueryKey")) {
					isQueryKey = true;
				}
				if (qName.equalsIgnoreCase("WebEnv")) {
					isWebEnv = true;
				}
			}

			// Collect tag contents (if applicable)
			public void characters(char ch[], int start, int length) 
					                                         throws SAXException {
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
			
			public void endElement(String uri, 
					               String localName, 
					               String qName) throws SAXException {
			
			}
			
		};
		
		return searchHandler;
		
	}
	
	/**
 	 * Return total # of publications yielded from search.
 	 * @param null
 	 * @return int totalPubs
 	 */
 	public int getTotalPubs() {
 		if (totalPubs == null) {
 			return -1;
 		} else {
 			return Integer.parseInt(this.totalPubs);
 		}
 	}
 	
}
