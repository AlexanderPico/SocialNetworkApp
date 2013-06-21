package main.java.org.baderlab.csapps.socialnetwork.academia;

import java.util.HashMap;
import java.util.Map;

import main.java.org.baderlab.csapps.socialnetwork.AbstractNode;

/**
 * The author of an article, journal review, or scientific paper
 * @author Victor Kofia
 *
 */
public class Author extends AbstractNode {
	/**
	 * Author's first name
	 */
	private String firstName = "N/A";
	/**
	 * Author's last name
	 */
	private String lastName = "N/A";
	/**
	 * Author's first initial
	 */
	private String firstInitial = "N/A";
	/**
	 * Author's middle initial
	 */
	private String middleInitial = "N/A";
	/**
	 * Author's primary institution
	 */
	private String institution = "N/A";
	/**
	 * Author's primary location
	 */
	private String location = "N/A";
	/**
	 * Author's total number of publications
	 */
	private int totalPubs = 0;
	/**
	 * Incites (IP = 167.68.24.112)
	 */
	final static public int INCITES = (167 << 24) + (68 << 16) + (24 << 8) + 112;
	/**
	 * PubMed (IP = 130.14.29.110)
	 */
	final public static int PUBMED = (130 << 24) + (14 << 16) + (29 << 8) + 110;
	
	/**
	 * Capitalize the first letter of string and return. If string is 
	 * a single character letter, it will be capitalized. Empty strings will yield empty strings. 
	 * @param null
	 * @return null
	 */
	public void format() {
		// Verify that the first letters in both first and last names are uppercase, and all following letters
		// are in lowercase
		this.firstName = this.firstName.substring(0,1).toUpperCase() + this.firstName.substring(1).toLowerCase();
		this.lastName = this.lastName.substring(0,1).toUpperCase() + this.lastName.substring(1).toLowerCase();
		// Ensure that the first and middle initials are capitalized
		this.firstInitial = this.firstInitial.toUpperCase();
		this.middleInitial = this.middleInitial.toUpperCase();
	}

	/**
	 * Create a new author with the first name, last name and middle initial specified in rawAuthorText.
	 * Source file origin is specified by a special int value
	 * @param String rawAuthorText
	 * @param int origin
	 * @return null
	 */
	public Author(String rawAuthorText, int origin) {
		if (origin == Author.PUBMED) {
			String[] names = rawAuthorText.split("\\s");
			if (names.length == 2) {
				this.lastName = names[0];
				if (names[1].length() == 2) {
					//Extract both first initial & middle initial
					this.firstInitial = names[1].substring(0,1);
					this.middleInitial = names[1].substring(1);
				} else {
					//If no middle initial is specified, it will be marked as unknown
					this.firstInitial = names[1];
				}
			} else if (names.length == 1) {
				this.lastName = names[0];
			}
		} else if (origin == Author.INCITES){
			this.firstName = Incites.parseFirstName(rawAuthorText);
			this.middleInitial = Incites.parseMiddleInitial(rawAuthorText);
			this.lastName = Incites.parseLastName(rawAuthorText);
			this.institution = Incites.parseInstitution(rawAuthorText);
		}
		
		constructAttrMap();
		
		// Format names to ensure consistency
		format();
	}

	/**
	 * Return a string representation of author in the format [Last Name] [First Initial]
	 * @param null
	 * @return String author
	 */
	public String toString() {
		return "Name: " + lastName + "-" + firstInitial
			+  "\nInstitution: " + institution + "\n\n";
	}

	/**
	 * Get author's institution
	 * @return String institution
	 */
	public String getInstitution() {
		return this.institution;
	}

	/**
	 * Get author's location
	 * @param null
	 * @return String location
	 */
	public String getLocation() {
		return location;
	}

	/**Set author's location
	 * @param String location
	 * @return null
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Get author's total number of publications
	 * @param null
	 * @return String totalPubs
	 */
	public int getTotalPubs() {
		return totalPubs;
	}

	/**
	 * Set author's total number of publications
	 * @param int totalPubs
	 * @return null
	 */
	public void setTotalPubs(int totalPubs) {
		this.totalPubs = totalPubs;
	}

	
	/**
	 * Get author's last name
	 * @param null
	 * @return String lastName
	 */
	public String getLastName() {
		return this.lastName;
	}
	
	/**
	 * Return true iff author is the same as other
	 * @param Object other
	 * @return boolean
	 */
	public boolean equals(Object other) {
		return this.lastName.equalsIgnoreCase(((Author)other).lastName) &&
			   this.firstInitial.equalsIgnoreCase(((Author)other).firstInitial);
	} 

	/**
	 * Return author hash code. Hash code uniquely identifies each specific
	 * last name - first initial combo. Thus, this system considers individuals 
	 * with matching last names and first initials to be one and the same.
	 * @param null
	 * @return int hashCode
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result += prime * result
				+ ((firstInitial == null) ? 0 : firstInitial.hashCode());
//		result = prime * result
//				+ ((institution == null) ? 0 : institution.hashCode());
		result += prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
//		result = prime * result
//				+ ((location == null) ? 0 : location.hashCode());
//		result = prime * result
//				+ ((middleInitial == null) ? 0 : middleInitial.hashCode());
//		result = prime * result + totalPubs;
		return result;
	}

	/**
	 * NOTE: As author identification becomes more refined it may be wise 
	 * to use more parameters in hashCode(). 
	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((firstInitial == null) ? 0 : firstInitial.hashCode());
//		result = prime * result
//				+ ((firstName == null) ? 0 : firstName.hashCode());
//		result = prime * result
//				+ ((institution == null) ? 0 : institution.hashCode());
//		result = prime * result
//				+ ((lastName == null) ? 0 : lastName.hashCode());
//		result = prime * result
//				+ ((location == null) ? 0 : location.hashCode());
//		result = prime * result
//				+ ((middleInitial == null) ? 0 : middleInitial.hashCode());
//		result = prime * result + totalPubs;
//		return result;
//	}

	/**
	 * NOTE: As author identification becomes more refined it may be wise 
	 * to use more parameters in equals(). 
	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Author other = (Author) obj;
//		if (firstInitial == null) {
//			if (other.firstInitial != null)
//				return false;
//		} else if (!firstInitial.equals(other.firstInitial))
//			return false;
//		if (firstName == null) {
//			if (other.firstName != null)
//				return false;
//		} else if (!firstName.equals(other.firstName))
//			return false;
//		if (institution == null) {
//			if (other.institution != null)
//				return false;
//		} else if (!institution.equals(other.institution))
//			return false;
//		if (lastName == null) {
//			if (other.lastName != null)
//				return false;
//		} else if (!lastName.equals(other.lastName))
//			return false;
//		if (location == null) {
//			if (other.location != null)
//				return false;
//		} else if (!location.equals(other.location))
//			return false;
//		if (middleInitial == null) {
//			if (other.middleInitial != null)
//				return false;
//		} else if (!middleInitial.equals(other.middleInitial))
//			return false;
//		if (totalPubs != other.totalPubs)
//			return false;
//		return true;
//	}
	
	/**
	 * Construct attribute map
	 * @param null
	 * @return null
	 */
	public void constructAttrMap() {
		attrMap = new HashMap<String, String>();
		attrMap.put("Last Name", this.lastName);
		attrMap.put("First Name", this.firstName);
		attrMap.put("Institution", this.institution);
	}
	
	/**
	 * Get attribute map
	 * @param null
	 * @return Map attrMap
	 */
	public Map<String, String> getAttrMap() {
		return this.attrMap;
	}
 
}