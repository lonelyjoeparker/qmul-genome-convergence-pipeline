package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class GlobalApplicationView extends JFrame {

	JTabbedPane mainTabPane;
	
	public GlobalApplicationView(){
		super("General Convergence Pipeline - alpha");
		mainTabPane = new JTabbedPane();
		add(mainTabPane);
		setSize(960,720);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void addTab(JComponent comp, String tabName){
		mainTabPane.add(comp, tabName);
	}
}
