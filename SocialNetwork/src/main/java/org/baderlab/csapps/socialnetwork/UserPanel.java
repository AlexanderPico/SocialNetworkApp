package main.java.org.baderlab.csapps.socialnetwork;
	
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import main.java.org.baderlab.csapps.socialnetwork.pubmed.Pubmed;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.xml.sax.SAXException;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.ParserConfigurationException;
	
/**
 * Main panel for Social Network App
 * @author Victor Kofia
 */
public class UserPanel extends JPanel implements CytoPanelComponent {
	
	private static int selectedWebsite = Search.DEFAULT;
		
	private JPanel searchBox = null;
		
	private JPanel controlPanel = null;
	
	private JPanel currentInfoPanel = null;
	
	private static UserPanel currentObjectRef = null;
		
	private static final long serialVersionUID = 8292806967891823933L;
	
	/**
	 * Create a new panel
	 * @param null
	 * @return null
	 */
	public UserPanel() {
		
		// Set panel layout
		this.setLayout(new BorderLayout());
		
		// Set preferred size
		this.setPreferredSize(new Dimension(400,200));
		
		// Add search box
		this.searchBox = UserPanel.createSearchBox();
		this.add(this.searchBox, BorderLayout.NORTH);
		
		// Add default info panel
		this.setCurrentInfoPanel(getDefaultInfoPanel());
		this.add(currentInfoPanel, BorderLayout.CENTER);
		
		// Add control toolbar
		this.controlPanel = UserPanel.createControlPanel();
		this.add(this.controlPanel, BorderLayout.SOUTH);
		
		// Set UserPanel object reference to current object
		UserPanel.setCurrentObjectRef(this);
		
	}

	/**
	 * Get current info panel
	 * @param null
	 * @return JPanel currentInfoPanel
	 */
	private JPanel getCurrentInfoPanel() {
		return this.currentInfoPanel;
	}
	
	/**
	 * Set current info panel
	 * @param JPanel infoPanel
	 * @return null
	 */
	private void setCurrentInfoPanel(JPanel infoPanel) {
		this.currentInfoPanel = infoPanel;
	}
	
	/**
	 * Get current object reference
	 * @param null
	 * @return UserPanel currentObject
	 */
	private static UserPanel getCurrentObjectRef() {
		return UserPanel.currentObjectRef;
	}
	
	/**
	 * Set current object reference
	 * @param UserPanel currentObjectRef
	 * @return null
	 */
	private static void setCurrentObjectRef(UserPanel currentObjectRef) {
		UserPanel.currentObjectRef = currentObjectRef;
	}
	
	/**
	 * Set selected website
	 * @param int website
	 * @return null
	 */
	private static void setSelectedWebsite(int website) {
		UserPanel.selectedWebsite = website;
	}
	
	/**
	 * Get selected website
	 * @param null
	 * @return int selectedWebsite
	 */
	private static int getSelectedWebsite() {
		return UserPanel.selectedWebsite;
	}
	
	/**
	 * Return new control panel for use in main app panel.
	 * Will allow user to reset or close panel at their convenience.
	 * @param null
	 * @return JPanel control panel
	 */
	private static JPanel createControlPanel() {
		// Create new toolbar.
		JPanel controlPanel = new JPanel();
			
		// Organize panel as a grid
		controlPanel
		.setLayout(new FlowLayout());
	
		controlPanel.add(UserPanel.createCloseButton());
		
		return controlPanel;
	}
	
	/**
	 * Return new search panel for use in main app panel. Search panel will
	 * allow user to perform searches.
	 * 
	 * @param null
	 * @return JPanel searchPanel
	 */
	private static JPanel createSearchBox() {
		
		// Create new panel.
		JPanel searchControls = new JPanel();
		
		// Organize panel horizontally.
		searchControls
		.setLayout(new BoxLayout(searchControls, BoxLayout.X_AXIS));
		
		// Set borders.
		searchControls.setBorder(BorderFactory.createTitledBorder("Search"));

		// Create new JLabel.
		JLabel searchLabel = new JLabel("Search: ");
		searchLabel.setBorder(BorderFactory.createEmptyBorder(0, 7, 10, 0));
		
		// Set font.
		searchLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		
		// Add 'Search' label to toolbar.
		searchControls.add(searchLabel);
		
		// Create new text field. It will be used to search through library
		// contents using some user-defined parameter.
		final JTextField searchBox = new JTextField();
		
		// Allow user to enter a query of their choosing
		searchBox.setEditable(true);
		
		searchBox.getDocument().addDocumentListener(new DocumentListener() {
			// Update auto generated list as user changes attributes.
			public void changedUpdate(DocumentEvent e) {
				
			}

			// Update auto generated list as user inputs data.
			public void insertUpdate(DocumentEvent e) {
				
			}

			// Update auto generated list once user erases previously written data.
			public void removeUpdate(DocumentEvent e) {
				
			}
		});
		
		searchBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					// Tapping enter will result in the automatic creation of a network
					Cytoscape.createNetwork(searchBox.getText(), UserPanel.getSelectedWebsite());
				} catch (ParserConfigurationException e) {
					Cytoscape.notifyUser("Check createSearchBox() in panel.java. An exception occurred there.");
				} catch (SAXException e) {
					Cytoscape.notifyUser("Check createSearchBox() in panel.java. An exception occurred there.");
				} catch (IOException e) {
					Cytoscape.notifyUser("Check createSearchBox() in panel.java. An exception occurred there.");
				}
			}
		});
		
		// Add ?? (search bar) to toolbar.
		searchControls.add(searchBox);
		
		// Add option selector to toolbar
		searchControls.add(UserPanel.createOptionSelector());
		
		// Return fully configured search controls.
		return searchControls;
		
	}
	
	/**
	 * Load default info panel
	 * @param null
	 * @return null
	 */
	private static JPanel getDefaultInfoPanel() {
		JPanel defaultInfoPanel = new JPanel();
		defaultInfoPanel.setName("--SELECT--");
		return defaultInfoPanel;
	}
	
	/**
	 * Switch current panel with indicated panel
	 * @param String panel
	 * @return null
	 */
	private void switcharoo(String websitePanel) {
		if (! websitePanel.trim().equalsIgnoreCase(this.getCurrentInfoPanel().getName())) {
			this.remove(this.getCurrentInfoPanel());
			switch (UserPanel.getSelectedWebsite()) {
				case Search.DEFAULT:
					setCurrentInfoPanel(UserPanel.getDefaultInfoPanel());
					break;
				case Search.PUBMED: 
					setCurrentInfoPanel(Pubmed.getPubmedInfoPanel());
					break;
			}
			this.add(this.getCurrentInfoPanel(), BorderLayout.CENTER);	
			this.revalidate();
			this.repaint();
		}
	}
	
	/**
	 * Return panel's primary option selector.
	 * @param null 
	 * @return JComboBox optionSelector
	 */
	private static JComboBox createOptionSelector() {
		//Create new JComboBox
		JComboBox optionSelector = new JComboBox(Search.getSiteList());
		optionSelector.setEditable(false);
		optionSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
		optionSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox jcmbType = (JComboBox) e.getSource();
				String website = (String) jcmbType.getSelectedItem();
				UserPanel.setSelectedWebsite(Search.getSiteMap().get(website));
				UserPanel.getCurrentObjectRef().switcharoo(website);
			}
		});
		return optionSelector;
	}
	


	/**
	 *Create close button. Close button closes current panel
	 *@param null
	 *@return JButton close
	 */
	private static JButton createCloseButton() {
		// Use icon object to create a new close button. 
		JButton closeButton = new JButton("Close");
		// Add ToolTipText.
		closeButton.setToolTipText("Close Social Network Panel");
		// Clicking of button results in the closing of current panel
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Cytoscape.closeUserPanel();
			}
		});
		return closeButton;
	}	
	
	
	public Component getComponent() {
		return this;
	}

	
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	
	/**
	 * Return panel title
	 * @param null
	 * @return String panelTitle
	 */
	public String getTitle() {
		return "Social Network";
	}

	
	/**
	 * Return panel icon
	 * @param null
	 * @return Icon panelIcon
	 */
	public Icon getIcon() {
		return null;
	}
	
}
