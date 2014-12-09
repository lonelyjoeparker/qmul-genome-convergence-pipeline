package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class GlobalApplicationView extends JFrame {

	JTabbedPane mainTabPane;
	JPanel sidePanel;
	public JTextArea sidePanelTaxonListText;
	public JCheckBox sidePanelDebugIndicator; 
	public JButton sidePanelUpdateGlobalSettingsButton, sidePanelSaveGlobalSettingsButton;
	JMenuBar mainMenuBar;
	
	public GlobalApplicationView(){
		super("General Convergence Pipeline - alpha");
		this.setLayout(new GridLayout(1,2));
		mainTabPane = new JTabbedPane();
		mainTabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		mainTabPane.setOpaque(true);
		add(mainTabPane);
		createAndAddSidePanel();
		add(sidePanel);
		pack();
		setSize(1080,960);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void createAndAddSidePanel(){
		sidePanel = new JPanel(new FlowLayout());
		sidePanel.add(new JLabel("Active taxon list"));
		sidePanelTaxonListText = new JTextArea("Taxon list shown here");
		sidePanelTaxonListText.setColumns(40);
		sidePanel.add(sidePanelTaxonListText);
		sidePanelDebugIndicator = new JCheckBox("Debug?");
		sidePanel.add(sidePanelDebugIndicator);
		sidePanelUpdateGlobalSettingsButton = new JButton("Update settings");
		sidePanel.add(sidePanelUpdateGlobalSettingsButton);
		sidePanelSaveGlobalSettingsButton = new JButton("Save settings");
		sidePanel.add(sidePanelSaveGlobalSettingsButton);
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
}
