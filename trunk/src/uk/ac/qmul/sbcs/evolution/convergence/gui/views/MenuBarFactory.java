/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.FlowLayout;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;



/**
 * View for global menu bar.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class MenuBarFactory extends JMenuBar {
	public JMenu fileMenu, actionMenu, viewMenu, helpMenu;
	public JMenuItem loadAlignments, loadAlignmentsSingle, loadAlignmentsBatch, loadAnalysisXMLs, loadResults, loadTrees, close, about;				// fileMenu sub-items
	public JMenuItem createConvergenceAnalyses, verifyDependencies, runLocalAnalysis, runRemoteAnalysis;	// actionMenu sub-items
	public JCheckBoxMenuItem showParameters;		// viewMenu sub-items
	public JMenuItem help, reportBugs, contributeCode;														// helpMenu sub-items
	
	public MenuBarFactory(){
		// Instantiate top-level menus
		fileMenu = new JMenu("File");
		actionMenu = new JMenu("Actions");
		viewMenu = new JMenu("View");
		helpMenu = new JMenu("Help");
		
		// Instantiate the File menu items
		about = new JMenuItem("About Genome Convergence Pipeline");
		loadAlignments = new JMenu("Load input alignments as .nex, .fa. .fasta or .phy files...");
		loadAlignmentsSingle = new JMenuItem("As single file");
		loadAlignmentsBatch = new JMenuItem("As directory of files (batch operation)");
		loadAlignments.add(loadAlignmentsSingle);
		loadAlignments.add(loadAlignmentsBatch);
		loadAnalysisXMLs = new JMenuItem("Load pre-specified convergence analyses as .XML files...");
		loadTrees = new JMenuItem("Load input phylogenetic trees as .nw, .tre, .tree or .trees files...");
		loadResults = new JMenuItem("Load convergence results as .ser files...");
		close = new JMenuItem("Exit Genome Convergence Pipeline");
		
		// Instantiate the Action menu items
		createConvergenceAnalyses = new JMenuItem("Create new convergence analyses using current datasets");
		verifyDependencies = new JMenuItem("Verify dependencies (PAML, RAxML etc) for convergence analyses using this computer");
		runLocalAnalysis = new JMenuItem("Run convergence analyses on this computer");
		runRemoteAnalysis = new JMenuItem("Write batch file to run convergence analyses on another computer");

		// Instantiate the View Menu items
		showParameters = new JCheckBoxMenuItem("Show parameters window", true);
		
		// Instantiate the Help menu items
		help = new JMenuItem("Help...");
		reportBugs = new JMenuItem("Report a bug, error, or request a feature...");
		contributeCode = new JMenuItem("Contribute to the Genome Convergence Pipeline codebase...");
		
		// Add items to menus, first File menu
		fileMenu.add(about);
		fileMenu.add(loadAlignments);
		fileMenu.add(loadAnalysisXMLs);
		fileMenu.add(loadTrees);
		fileMenu.add(loadResults);
		fileMenu.add(close);
		
		// Add items to Action menu
		actionMenu.add(createConvergenceAnalyses);
		actionMenu.add(verifyDependencies);
		actionMenu.add(loadAnalysisXMLs);
		actionMenu.add(runLocalAnalysis);
		actionMenu.add(runRemoteAnalysis);

		// Add items to View menu
		viewMenu.add(showParameters);

		// Add items to Help menu
		helpMenu.add(help);
		helpMenu.add(reportBugs);
		helpMenu.add(contributeCode);

		// Add menus to the menu bar
		add(fileMenu);
		add(actionMenu);
		add(viewMenu);
		add(helpMenu);
	}
	
	public static class AboutFrame extends JFrame{
		public AboutFrame(){
			super("About");
			JPanel panel = new JPanel(new FlowLayout());
			panel.add(new JLabel("Genome Convergence Pipeline - alpha version."));
			panel.add(new JLabel("This is a development-only private alpha: use at your own risk."));
			panel.add(new JLabel("(c) Joe Parker / Queen Mary University of London, 2014."));
			add(panel);
			setSize(450,200);
			setVisible(true);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

}
