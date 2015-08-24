package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GlobalApplicationView extends JFrame {

	JTabbedPane mainTabPane;
	JPanel sidePanel, taskPanel;
	public JLabel taskLabel = new JLabel("Task: ");
	public JLabel binariesLocationLabel, workdirLocationLabel, binariesLocation, workdirLocation;
	public JFrame parametersWindow;
	public JTextArea sidePanelTaxonListText;
	public JCheckBox sidePanelDebugIndicator; 
	public JButton sidePanelUpdateGlobalSettingsButton, sidePanelSaveGlobalSettingsButton, validateBinariesLocation, validateWorkdirLocation, setBinariesLocation, setWorkdirLocation;
	public JProgressBar taskbar;
	public JFileChooser setBinariesLocationChooser, setWorkdirLocationChooser;
	JMenuBar mainMenuBar;
	
	public GlobalApplicationView(){
		super("General Convergence Pipeline - alpha");
		//this.setLayout(new GridLayout(1,2));
		mainTabPane = new JTabbedPane();
		mainTabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		mainTabPane.setOpaque(true);
		add(mainTabPane);
		createAndAddSidePanel();
		parametersWindow = new JFrame("General Convergence Pipeline - alpha - parameters window");
		parametersWindow.add(sidePanel);
		parametersWindow.pack();
		parametersWindow.setSize(960,320);
		parametersWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
		setSize(1080,960);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		parametersWindow.setLocation(64, 128);
		parametersWindow.setVisible(true);
		
		setBinariesLocationChooser = new JFileChooser("Select binaries directory");
		setBinariesLocationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		setWorkdirLocationChooser = new JFileChooser("Select working directory");
		setWorkdirLocationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	public void createAndAddSidePanel(){
		// Panel to hold the side / parameter panel elements.
		sidePanel = new JPanel();
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		// Active taxon list display.
		JPanel taxonPanel  = new JPanel(new FlowLayout());
		taxonPanel.add(new JLabel("Active taxon list"));
		sidePanelTaxonListText = new JTextArea("Taxon list shown here");
		sidePanelTaxonListText.setColumns(20);
		taxonPanel.add(sidePanelTaxonListText);
		sidePanelDebugIndicator = new JCheckBox("Debug?");
		taxonPanel.add(sidePanelDebugIndicator);
		sidePanelUpdateGlobalSettingsButton = new JButton("Update and validate taxon list");
		taxonPanel.add(sidePanelUpdateGlobalSettingsButton);
		sidePanelSaveGlobalSettingsButton = new JButton("Save settings");
		taxonPanel.add(sidePanelSaveGlobalSettingsButton);
		sidePanel.add(taxonPanel);
		
		/* Text fields to show/set/validate the binaries and workdir settings */
		// Panels to hold them all
		JPanel validatePanel = new JPanel();
		validatePanel.setLayout(new BoxLayout(validatePanel, BoxLayout.Y_AXIS));
		JPanel binariesPanel = new JPanel();
		binariesPanel.setLayout(new BoxLayout(binariesPanel, BoxLayout.X_AXIS));
		JPanel workdirPanel = new JPanel();
		workdirPanel.setLayout(new BoxLayout(workdirPanel, BoxLayout.X_AXIS));
		// Binaries location
		binariesLocationLabel = new JLabel("Required binaries folder: ");
		binariesLocation = new JLabel("<not set>");
		setBinariesLocation = new JButton("Set binaries location");
		validateBinariesLocation = new JButton("Validate");
		binariesPanel.add(binariesLocationLabel);
		binariesPanel.add(binariesLocation);
		binariesPanel.add(setBinariesLocation);
		binariesPanel.add(validateBinariesLocation);
		validatePanel.add(binariesPanel);
		// Workdir location
		workdirLocationLabel = new JLabel("Working directory for analyses: ");
		workdirLocation = new JLabel("<not set>");
		setWorkdirLocation = new JButton("Set working directory");
		validateWorkdirLocation = new JButton("Validate");
		workdirPanel.add(workdirLocationLabel);
		workdirPanel.add(workdirLocation);
		workdirPanel.add(setWorkdirLocation);
		workdirPanel.add(validateWorkdirLocation);
		validatePanel.add(workdirPanel);
		// Add validation panel to side panel
		sidePanel.add(validatePanel);
		/* A JPanel to hold the task / progress bar */
		taskPanel = new JPanel();
		taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
		// The task / progress bar itself
		taskbar = new JProgressBar(0,100);
		taskbar.setValue(0);
		// Set the progress / taskbar width. Could also use a JPanel in a JToolBar - TODO
		taskbar.setPreferredSize(new Dimension(200,15));
		taskPanel.add(taskbar);
		taskPanel.add(taskLabel);
		sidePanel.add(taskPanel);
	}
		
	/**
	 * Method to add tabs to main tab pane
	 * @param comp
	 * @param tabName
	 */
	public void addTab(JComponent comp, String tabName){
		mainTabPane.add(comp, tabName);
	}

	public void setANewJMenuBar(JMenuBar menuBarToSet) {
		mainMenuBar = menuBarToSet;
		JMenuBar bar = new JMenuBar();
		bar.add(new JMenu("a menu"));
		setJMenuBar(mainMenuBar);
	}

	/**
	 * Sets the JLabel displaying the GlobalModel.binariesLocation state to the user
	 * @param absolutePath
	 */
	public void setBinariesLocationLabel(String absolutePath) {
		binariesLocation.setText(absolutePath);
	}

	/**
	 * Sets the JLabel displaying the GlobalModel.workdirLocation state to the user
	 * @param absolutePath
	 */
	public void setWorkdirLocationLabel(String absolutePath) {
		workdirLocation.setText(absolutePath);
	}
}
