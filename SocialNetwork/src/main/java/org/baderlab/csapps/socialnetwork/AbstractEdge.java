package main.java.org.baderlab.csapps.socialnetwork;

import java.util.List;
import java.util.Map;

/**
 * A Cytoscape edge
 * @author Victor Kofia
 */
public abstract class AbstractEdge {
	
	/**
	 * A map containing all of edge's attributes
	 */
	Map<String, String> attrMap;
	
	/**
	 * Return map containing all of edge's attributes
	 * @param null
	 * @return Map attrMap
	 */
	public abstract Map<String, String> getAttrMap();
	
	/**
	 * Construct an attribute map for edge
	 * @param null
	 * @return null
	 */
	public abstract void constructAttrMap();
	
	/**
	 * Return all nodes attached to edge
	 * @param null
	 * @return List nodes
	 */
	public abstract List<? extends AbstractNode> getNodes();
	
}