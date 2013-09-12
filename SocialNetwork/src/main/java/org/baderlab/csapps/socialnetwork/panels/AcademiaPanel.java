package org.baderlab.csapps.socialnetwork.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Incites_InstitutionLocationMap;
import org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import org.cytoscape.util.swing.FileUtil;


/**
 * Tools for building / working with the Academia Info-Panel
 * @author Victor Kofia
 */
public class AcademiaPanel {

	/**
	 * Reference to academia info panel
	 */
	private  JPanel academiaInfoPanelRef = null;

	/**
	 * A reference to the faculty text field. Used to verify correct faculty input.
	 */
	private  JTextField facultyTextFieldRef = new JTextField();

	/**
	 * A reference to the load data text field. Used to verify correct file path.
	 */
	private  JTextField pathTextFieldRef = new JTextField();
	
	private JRadioButton incitesRadioButton =null;
	private JRadioButton scopusRadioButton =null;
	
	/**
	 * A reference to a data file. Used to verify correct file path.
	 */
	private  File selectedFileRef = null;
	
	/**
	 * A reference to the app Manager
	 */
	private SocialNetworkAppManager appManager = null;
	
	
	
	public AcademiaPanel(SocialNetworkAppManager appManager) {
		super();
		this.appManager = appManager;
	}

	/**
	 * Create academia info panel. In addition to Pubmed specific features, 
	 * this panel will also enable users to load Incites data.
	 * @param null
	 * @return JPanel academiaInfoPanel
	 */
	public  JPanel createAcademiaInfoPanel() {
		JPanel academiaInfoPanel = new JPanel();
		academiaInfoPanel.setLayout(new BorderLayout());
		academiaInfoPanel.setName("Academia");
	    academiaInfoPanel.setBorder(BorderFactory.createTitledBorder("Academia"));
		academiaInfoPanel.add(this.createDatabaseInfoPanel(), 
				              BorderLayout.NORTH);
		// Set a reference to this panel for later access
		this.setAcademiaInfoPanelRef(academiaInfoPanel);
		return academiaInfoPanel;
	}

	/**
	 * Create Database info panel.
	 * Allows user to load Incites or Scopus derived data files
	 * @param null
	 * @return JPanel databaseInfoPanel
	 */
	public  JPanel createDatabaseInfoPanel() {
	
		// Create new Database info panel.
		JPanel databaseInfoPanel = new JPanel();
		databaseInfoPanel.setBorder(BorderFactory.createTitledBorder("Database"));
	
		// Set layout
		databaseInfoPanel.setLayout(new BoxLayout(databaseInfoPanel
				, BoxLayout.Y_AXIS));	
		
		// Add database panel
		databaseInfoPanel.add(createDatabasePanel());
	
		// Add load panel
		databaseInfoPanel.add(createLoadDataPanel());
	
		// Add faculty panel
		databaseInfoPanel.add(createSpecifyNetworkNamePanel());
	
		// Add 'create network button' to panel
		// Button wrapper added for cosmetic reasons
		JPanel buttonWrapper = new JPanel();
		buttonWrapper.add(this.createNetworkButton(), BorderLayout.CENTER);
		databaseInfoPanel.add(buttonWrapper);
	
		return databaseInfoPanel;
	}

	/**
	 * Create database panel
	 * @param null
	 * @return JPanel databasePanel
	 */
	 public JPanel createDatabasePanel() {
		JPanel databasePanel = new JPanel();
		
		// Set bordered title
		databasePanel.setBorder(BorderFactory.createTitledBorder("Select Database"));
		
		// Organize panel horizontally.
		databasePanel.setLayout(new BoxLayout(databasePanel, BoxLayout.X_AXIS));
				
		// Create Incites radio button
	    this.incitesRadioButton = new JRadioButton("Incites", true);
	    incitesRadioButton.setFocusable(true);	   
	    
	    // Create Scopus radio button
	    this.scopusRadioButton = new JRadioButton("Scopus", false);
	    scopusRadioButton.setFocusable(false);
	    
	    // Ensures that only one button is selected at a time
	    ButtonGroup buttonGroup = new ButtonGroup();
	    buttonGroup.add(incitesRadioButton);
	    buttonGroup.add(scopusRadioButton);
	
	    databasePanel.add(incitesRadioButton);
	    databasePanel.add(scopusRadioButton);
	    
		return databasePanel;
	
	}

	/**
	 *Create load button. Load button loads data file onto Cytoscape for parsing
	 *@param null
	 *@return JButton load
	 */
	public  JButton createLoadButton() {
	
		JButton loadButton = new JButton("...");
		loadButton.setToolTipText("Load Incites / Scopus data");
	
		// Clicking of button results in the popping up of a dialog box that implores the user
		// to select a new data file. 
		loadButton.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent event) {
				// Ask user to select the appropriate data file.
				JFileChooser chooser = new JFileChooser();
				// Initialize the chooser dialog box to desktop
				File directory = new File(FileUtil.LAST_DIRECTORY);
				chooser.setCurrentDirectory(directory);
				chooser.setDialogTitle("Data Selection");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int check = chooser.showDialog(null, "OK");
				// Only attempt to read data file if user clicks "OK"
				if (check == JFileChooser.APPROVE_OPTION) {
					File textFile = chooser.getSelectedFile();
					setDataFile(textFile);
					getPathTextFieldRef().setText(textFile.getAbsolutePath());
					getFacultyTextFieldRef().setText(parseFileName(textFile.getAbsolutePath()));
				} else {
					setDataFile(null);
					getPathTextFieldRef().setText(null);
				}
			}
		});
		return loadButton;
	}

	/**
	 * Create new load data panel. Allows user to specify path
	 * of desired data file
	 * @param null
	 * @return JPanel loadDataPanel
	 */
	public  JPanel createLoadDataPanel() {
		JPanel loadDataPanel = new JPanel();
		loadDataPanel.setBorder(BorderFactory.createTitledBorder("Load File"));
		loadDataPanel.setLayout(new BoxLayout(loadDataPanel, BoxLayout.X_AXIS));
		// Create new text field and set reference. Reference will be used later on to verify
		// correct file path
		this.setLoadTextField(new JTextField());
		this.getPathTextFieldRef().setEditable(true);
		// Add text field 
		loadDataPanel.add(this.getPathTextFieldRef());
		// Add load data button 
		loadDataPanel.add(this.createLoadButton());
		return loadDataPanel;
	}

	/**
	 * Create 'create network button'. Create network button 
	 * attempts to create a network out of a file specified
	 * by the user.
	 * @param null
	 * @return JButton createNetworkButton
	 */
	public  JButton createNetworkButton() {
		JButton createNetworkButton = new JButton("Create Network");
		createNetworkButton.setToolTipText("Create network");
		createNetworkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				//check to see which analysis type is selected
				if(incitesRadioButton.isSelected())
					appManager.setAnalysis_type(SocialNetworkAppManager.ANALYSISTYPE_INCITES);
				if(scopusRadioButton.isSelected())
					appManager.setAnalysis_type(SocialNetworkAppManager.ANALYSISTYPE_SCOPUS);
				
				if (!incitesRadioButton.isSelected() &&
						! scopusRadioButton.isSelected()) {
					CytoscapeUtilities.notifyUser("Please select a database");
				} else {
					if (getSelectedFileRef() == null || 
							getFacultyTextFieldRef().getText() == null) {
						CytoscapeUtilities.notifyUser("Please select a file and/or specify network name.");
					} else { 
						if (! getSelectedFileRef().getAbsolutePath().trim()
								.equalsIgnoreCase(getPathTextFieldRef()
										.getText().trim())) {
							CytoscapeUtilities.notifyUser("Please select a file.");
						} else if (getFacultyTextFieldRef().getText().trim()
								.isEmpty()) {
							CytoscapeUtilities.notifyUser("Please specify network name.");
						} else {
							try {
								// Create network
								appManager.createNetwork(getSelectedFileRef());						
							} catch (FileNotFoundException e) {
								CytoscapeUtilities.notifyUser(getPathTextFieldRef().getText()
										+ " does not exist");
							}
						}
					}
				}
			}
		});
		return createNetworkButton;
	}

	/**Create specify network name panel. 
	 * @param null
	 * @return JPanel networkNamePanel
	 */
	public  JPanel createSpecifyNetworkNamePanel() {
		JPanel specifyNetworkNamePanel = new JPanel();
		specifyNetworkNamePanel.setBorder(BorderFactory.createTitledBorder("Specify Network Name"));
		specifyNetworkNamePanel.setLayout(new BoxLayout(specifyNetworkNamePanel, BoxLayout.X_AXIS));
		// Create new text field and set reference. Reference will be used later on to verify
		// correct file path
		this.setFacultyTextFieldRef(new JTextField());
		getFacultyTextFieldRef().setEditable(true);
		// Add text field 
		specifyNetworkNamePanel.add(getFacultyTextFieldRef());
		return specifyNetworkNamePanel;
	}
	/**
	 * Get academia info panel reference
	 * @param null
	 * @return JPanel academiaInfoPanelRef
	 */
	public  JPanel getAcademiaInfoPanelRef() {
		return this.academiaInfoPanelRef;
	}
	/**
	 * Get faculty text field
	 * @param null
	 * @return JTextField facultyTextField
	 */
	public  JTextField getFacultyTextFieldRef() {
		return this.facultyTextFieldRef;
	}

	/**
	 * Get path text field
	 * @param null
	 * @return JTextField pathTextField
	 */
	public  JTextField getPathTextFieldRef() {
		return this.pathTextFieldRef;
	}

	/**
	 * Get selected data file
	 * @param null
	 * @return File selectedFile
	 */
	public  File getSelectedFileRef() {
		return this.selectedFileRef;
	}
	
	/**
	 * Extract filename from path
	 * @param String path
	 * @return String filename
	 */
	public  String parseFileName(String path) {
		Pattern pattern = Pattern.compile("([^\\\\/]+?)(\\.xlsx|\\.txt|\\.csv)$");
		Matcher matcher = pattern.matcher(path);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "N/A";
	}
	
	/**
	 * Set academia info panel reference
	 * @param JPanel academiaInfoPanelRef
	 * @return null
	 */
	public  void setAcademiaInfoPanelRef(JPanel academiaInfoPanelRef) {
		this.academiaInfoPanelRef = academiaInfoPanelRef;
	}
	
	/**
	 * Set selected data file
	 * @param File data
	 * @return null
	 */
	public  void setDataFile(File selectedFile) {
		this.selectedFileRef = selectedFile;
	}

	/**
	 * Set faculty text field
	 * @param JTextField facultyTextField
	 * @return null
	 */
	public  void setFacultyTextFieldRef(JTextField facultyTextField) {
		this.facultyTextFieldRef = facultyTextField;
	}

	/**
	 * Set path text field
	 * @param JTextField pathTextField
	 * @return null
	 */
	public  void setLoadTextField(JTextField pathTextField) {
		this.pathTextFieldRef = pathTextField;
	}
	

}
