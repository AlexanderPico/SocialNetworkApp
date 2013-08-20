package main.java.org.baderlab.csapps.socialnetwork.academia;

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

import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
import main.java.org.baderlab.csapps.socialnetwork.panels.CollapsiblePanel;

/**
 * Tools for building / working with the Academia Info-Panel
 * @author Victor Kofia
 */
public class AcademiaFactory {

	/**
	 * A reference to the faculty text field. Used to verify correct faculty input.
	 */
	public static JTextField facultyTextFieldRef = new JTextField();

	/**
	 * A reference to the load data text field. Used to verify correct file path.
	 */
	public static JTextField pathTextFieldRef = new JTextField();

	/**
	 * A reference to a data file. Used to verify correct file path.
	 */
	public static File selectedFileRef = null;

	/**
	 * Create academia info panel. In addition to Pubmed specific features, 
	 * this panel will also enable users to load Incites data.
	 * @param null
	 * @return JPanel academiaInfoPanel
	 */
	public static JPanel createAcademiaInfoPanel() {
		JPanel academiaInfoPanel = new JPanel();
		academiaInfoPanel.setLayout(new BorderLayout());
		academiaInfoPanel.setName("Academia");
	    academiaInfoPanel.setBorder(BorderFactory.createTitledBorder("Academia"));
		academiaInfoPanel.add(AcademiaFactory.createDatabaseInfoPanel(), 
				              BorderLayout.NORTH);
		return academiaInfoPanel;
	}

	/**
	 * Create 'create network button'. Create network button 
	 * attempts to create a network out of a file specified
	 * by the user.
	 * @param null
	 * @return JButton createNetworkButton
	 */
	public static JButton createNetworkButton() {
		JButton createNetworkButton = new JButton("Create Network");
		createNetworkButton.setToolTipText("Create network");
		createNetworkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (! Incites.getIncitesRadioButton().isSelected() &&
						! Scopus.getScopusRadioButton().isSelected()) {
					Cytoscape.notifyUser("Please select a database");
				} else {
					if (getSelectedFileRef() == null || 
							getFacultyTextFieldRef().getText() == null) {
						Cytoscape.notifyUser("Please select a file and/or specify network name.");
					} else { 
						if (! getSelectedFileRef().getAbsolutePath().trim()
								.equalsIgnoreCase(AcademiaFactory.getPathTextFieldRef()
										.getText().trim())) {
							Cytoscape.notifyUser("Please select a file.");
						} else if (getFacultyTextFieldRef().getText().trim()
								.isEmpty()) {
							Cytoscape.notifyUser("Please specify network name.");
						} else {
							try {
								// Create network
								Cytoscape.createNetwork(getSelectedFileRef());						
							} catch (FileNotFoundException e) {
								Cytoscape.notifyUser(AcademiaFactory.getPathTextFieldRef().getText()
										+ " does not exist");
							}
						}
					}
				}
			}
		});
		return createNetworkButton;
	}

	/**
	 * Create Database info panel.
	 * Allows user to load Incites or Scopus derived text files
	 * @param null
	 * @return JPanel databaseInfoPanel
	 */
	public static JPanel createDatabaseInfoPanel() {
	
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
		buttonWrapper.add(AcademiaFactory.createNetworkButton(), BorderLayout.CENTER);
		databaseInfoPanel.add(buttonWrapper);
	
		return databaseInfoPanel;
	}

	/**
	 * Create database panel
	 * @param null
	 * @return JPanel databasePanel
	 */
	static JPanel createDatabasePanel() {
		CollapsiblePanel databasePanel = new CollapsiblePanel("Select Database");
		databasePanel.setCollapsed(false);
		
		// Organize panel horizontally.
		databasePanel
		.getContentPane().setLayout(new BoxLayout(databasePanel.getContentPane(), BoxLayout.X_AXIS));
				
		// Create Incites radio button
	    final JRadioButton incitesRadioButton = new JRadioButton("Incites", true);
	    incitesRadioButton.setFocusable(true);
	    
	    Incites.setIncitesRadioButton(incitesRadioButton);
	    
	    // Create Scopus radio button
	    final JRadioButton scopusRadioButton = new JRadioButton("Scopus", false);
	    scopusRadioButton.setFocusable(false);
	
	    Scopus.setScopusRadioButton(scopusRadioButton);
	    
	    // Ensures that only one button is selected at a time
	    ButtonGroup buttonGroup = new ButtonGroup();
	    buttonGroup.add(incitesRadioButton);
	    buttonGroup.add(scopusRadioButton);
	
	    databasePanel.getContentPane().add(Incites.getIncitesRadioButton());
	    databasePanel.getContentPane().add(Scopus.getScopusRadioButton());
	    
		return databasePanel;
	
	}

	/**
	 *Create load button. Load button loads data file onto Cytoscape for parsing
	 *@param null
	 *@return JButton load
	 */
	public static JButton createLoadButton() {
	
		JButton loadButton = new JButton("...");
		loadButton.setToolTipText("Load Incites / Scopus data");
	
		// Clicking of button results in the popping up of a dialog box that implores the user
		// to select a new data file. 
		loadButton.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent event) {
				// Ask user to select the appropriate data file.
				JFileChooser chooser = new JFileChooser();
				// Initialize the chooser dialog box to desktop
				File directory = new File("~/Desktop");
				chooser.setCurrentDirectory(directory);
				chooser.setDialogTitle("Data Selection");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int check = chooser.showDialog(null, "OK");
				// Only attempt to read data file if user clicks "OK"
				if (check == JFileChooser.APPROVE_OPTION) {
					File textFile = chooser.getSelectedFile();
					AcademiaFactory.setDataFile(textFile);
					AcademiaFactory.getPathTextFieldRef().setText(textFile.getAbsolutePath());
					getFacultyTextFieldRef().setText(AcademiaFactory.parseFileName(textFile.getAbsolutePath()));
				} else {
					AcademiaFactory.setDataFile(null);
					AcademiaFactory.getPathTextFieldRef().setText(null);
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
	public static JPanel createLoadDataPanel() {
		JPanel loadDataPanel = new JPanel();
		loadDataPanel.setBorder(BorderFactory.createTitledBorder("Load File"));
		loadDataPanel.setLayout(new BoxLayout(loadDataPanel, BoxLayout.X_AXIS));
		// Create new text field and set reference. Reference will be used later on to verify
		// correct file path
		AcademiaFactory.setLoadTextField(new JTextField());
		AcademiaFactory.getPathTextFieldRef().setEditable(true);
		// Add text field 
		loadDataPanel.add(AcademiaFactory.getPathTextFieldRef());
		// Add load data button 
		loadDataPanel.add(AcademiaFactory.createLoadButton());
		return loadDataPanel;
	}

	/**Create specify network name panel. 
	 * @param null
	 * @return JPanel networkNamePanel
	 */
	public static JPanel createSpecifyNetworkNamePanel() {
		JPanel specifyNetworkNamePanel = new JPanel();
		specifyNetworkNamePanel.setBorder(BorderFactory.createTitledBorder("Specify Network Name"));
		specifyNetworkNamePanel.setLayout(new BoxLayout(specifyNetworkNamePanel, BoxLayout.X_AXIS));
		// Create new text field and set reference. Reference will be used later on to verify
		// correct file path
		AcademiaFactory.setFacultyTextFieldRef(new JTextField());
		getFacultyTextFieldRef().setEditable(true);
		// Add text field 
		specifyNetworkNamePanel.add(getFacultyTextFieldRef());
		return specifyNetworkNamePanel;
	}

	/**
	 * Get faculty text field
	 * @param null
	 * @return JTextField facultyTextField
	 */
	public static JTextField getFacultyTextFieldRef() {
		return AcademiaFactory.facultyTextFieldRef;
	}

	/**
	 * Get path text field
	 * @param null
	 * @return JTextField pathTextField
	 */
	public static JTextField getPathTextFieldRef() {
		return AcademiaFactory.pathTextFieldRef;
	}

	/**
	 * Get selected data file
	 * @param null
	 * @return File selectedFile
	 */
	public static File getSelectedFileRef() {
		return AcademiaFactory.selectedFileRef;
	}
	/**
	 * Extract filename from path
	 * @param String path
	 * @return String filename
	 */
	public static String parseFileName(String path) {
		Pattern pattern = Pattern.compile("([^\\\\/]+?)(\\.xlsx|\\.txt|\\.csv)$");
		Matcher matcher = pattern.matcher(path);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "N/A";
	}
	/**
	 * Set selected data file
	 * @param File data
	 * @return null
	 */
	public static void setDataFile(File selectedFile) {
		AcademiaFactory.selectedFileRef = selectedFile;
	}

	/**
	 * Set faculty text field
	 * @param JTextField facultyTextField
	 * @return null
	 */
	public static void setFacultyTextFieldRef(JTextField facultyTextField) {
		AcademiaFactory.facultyTextFieldRef = facultyTextField;
	}
	
	/**
	 * Set path text field
	 * @param JTextField pathTextField
	 * @return null
	 */
	public static void setLoadTextField(JTextField pathTextField) {
		AcademiaFactory.pathTextFieldRef = pathTextField;
	}

}