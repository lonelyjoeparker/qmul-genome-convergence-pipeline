/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.*;

import uk.ac.qmul.sbcs.evolution.convergence.CONTEXTVersion;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.AddBatchAlignmentsButtonListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.PhylogeniesController;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AlignmentsTableModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.PhylogeniesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.AlignmentsView;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.MenuBarFactory;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.PhylogeniesView;

/**
 * A runnable GUI main-class application for browsing phylogenomic datasets (phylogenies and multiple sequence alignments, both batch-loadable)
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class PhlogenomicDatasetBrowser implements Runnable {

	private final static CONTEXTVersion version = new CONTEXTVersion();
	
	// Alignments M-V-C
	private AlignmentsTableModel alignmentsModel;
	private AlignmentsView alignmentsView;
	private AlignmentsController alignmentsController;

	// Phylogenies M-V-C
	private PhylogeniesModel phylogeniesModel;
	private PhylogeniesView phylogeniesView;
	private PhylogeniesController phylogeniesController;
	
	// Phylogenomic dataset browser M-V-C
	private PhylogenomicDatasetBrowserModel model;
	private PhylogenomicDatasetBrowserView view;
	private PhylogenomicDatasetBrowserController controller;
	
	/**
	 * Default no-arg constructor
	 */
	public PhlogenomicDatasetBrowser(){
		/* Instantiate the model-view-controllers */

		//Instantiate the specific model / view / controllers first, so that the controllers can be passed to globalcontroller.
		// First alignments MVC
		alignmentsModel = new AlignmentsTableModel();
		alignmentsView = new AlignmentsView();
		alignmentsController = new AlignmentsController(alignmentsModel, alignmentsView);

		// Phylogenies MVC
		phylogeniesModel = new PhylogeniesModel();
		phylogeniesView = new PhylogeniesView();
		phylogeniesController = new PhylogeniesController(phylogeniesModel, phylogeniesView);
		
		// PhylogenomicDatasetBrowser MVC
		model = new PhylogenomicDatasetBrowserModel();
		view = new PhylogenomicDatasetBrowserView(phylogeniesView, alignmentsView);
		controller = new PhylogenomicDatasetBrowserController(model, view, alignmentsController, phylogeniesController);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    try {
            // Set System L&F
        UIManager.setLookAndFeel(
        	UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	    	// handle exception
	    }
	    catch (ClassNotFoundException e) {
	    	// handle exception
	    }
	    catch (InstantiationException e) {
	    	// handle exception
	    }
	    catch (IllegalAccessException e) {
	    	// handle exception
	    }

        javax.swing.SwingUtilities.invokeLater(new PhlogenomicDatasetBrowser());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Internal class corresponding to global application model
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	private class PhylogenomicDatasetBrowserModel{}
	
	/**
	 * Internal class corresponding to global application view
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	private class PhylogenomicDatasetBrowserView extends JFrame{
		PhylogeniesView phylogeniesView;
		AlignmentsView alignmentsView;
		AboutFrame aboutFrame;
		JTabbedPane mainTabPane;
		JPanel taskPanel, layoutPanel;
		JToolBar toolBar;
		JLabel taskLabel = new JLabel("Progress: ");
		JProgressBar taskbar;
		JFileChooser alignmentsFileChooser, alignmentsDirectoryChooser, phylogeniesFileChooser, phylogeniesDirectoryChooser;
		JMenuBar menuBar;
		JMenu fileMenu, helpMenu, loadAlignments, loadTrees;
		JMenuItem loadAlignmentsSingle, loadAlignmentsBatch, loadTreesSingle, loadTreesBatch, close, about;				// fileMenu sub-items
		JMenuItem help, reportBugs, contributeCode;														// helpMenu sub-items
		
		public PhylogenomicDatasetBrowserView(PhylogeniesView phy_view, AlignmentsView align_view){
			super("CONTEXT - "+version.getVersionString());
			phylogeniesView = phy_view;
			alignmentsView = align_view;
			mainTabPane = new JTabbedPane();
			mainTabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			mainTabPane.addTab("Alignments", alignmentsView.getPanel());
			mainTabPane.addTab("Phylogenies", phylogeniesView.getPanel());
			mainTabPane.setOpaque(true);
			layoutPanel = new JPanel();
			layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
			layoutPanel.add(mainTabPane);
			createMenuBar();
			createTaskBar();
			add(layoutPanel);
			pack();
			setSize(1080,960);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
			aboutFrame = new AboutFrame();
			aboutFrame.setVisible(false);
		}
		
		void createTaskBar(){
			/* A JPanel to hold the task / progress bar */
			taskPanel = new JPanel();
			taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.X_AXIS));
			// The task / progress bar itself
			taskbar = new JProgressBar(0,100);
			taskbar.setValue(0);
			// Set the progress / taskbar width. Could also use a JPanel in a JToolBar - TODO
			taskbar.setPreferredSize(new Dimension(200,15));
			taskPanel.add(taskLabel);
			taskPanel.add(taskbar);
			toolBar = new JToolBar();
			toolBar.add(taskPanel);
			layoutPanel.add(toolBar);
		}
		
		/**
		 * instantiate menu bar
		 */
		void createMenuBar(){
			// Instantiate menu bar
			menuBar = new JMenuBar();
			
			// Instantiate top-level menus
			fileMenu = new JMenu("File");
			helpMenu = new JMenu("Help");
			
			// Instantiate the File menu items
			about = new JMenuItem("About CONTEXT, the Phylogenomic Dataset Browser");
			loadAlignments = new JMenu("Load input alignments as .nex, .fa. .fasta or .phy files...");
			loadAlignmentsSingle = new JMenuItem("As single file");
			loadAlignmentsBatch = new JMenuItem("As directory of files (batch operation)");
			loadAlignments.add(loadAlignmentsSingle);
			loadAlignments.add(loadAlignmentsBatch);
			loadTrees = new JMenu("Load input phylogenetic trees as .nw, .tre, .tree or .trees files...");
			loadTreesSingle = new JMenuItem("As single file");
			loadTreesBatch = new JMenuItem("As directory of files (batch operation)");
			loadTrees.add(loadTreesSingle);
			loadTrees.add(loadTreesBatch);
			close = new JMenuItem("Quit CONTEXT/Phylogenomic Dataset Browser");
						
			// Instantiate the Help menu items
			help = new JMenuItem("Help...");
			reportBugs = new JMenuItem("Report a bug, error, or request a feature...");
			contributeCode = new JMenuItem("Contribute to/fork the CONTEXT codebase on GitHub...");
			
			// Add items to menus, first File menu
			fileMenu.add(about);
			fileMenu.add(loadAlignments);
			fileMenu.add(loadTrees);
			fileMenu.add(close);
			
			// Add items to Help menu
			helpMenu.add(help);
			helpMenu.add(reportBugs);
			helpMenu.add(contributeCode);

			// Add menus to the menu bar
			menuBar.add(fileMenu);
			menuBar.add(helpMenu);
			alignmentsFileChooser = new JFileChooser("Select alignment file");
			alignmentsDirectoryChooser = new JFileChooser("Select alignments directory");
			alignmentsDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			phylogeniesFileChooser = new JFileChooser("Select phylogeny file");
			phylogeniesDirectoryChooser = new JFileChooser("Select phylogenies directory");
			phylogeniesDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			// Add menu bar to this
			this.setJMenuBar(menuBar);
		}

		class AboutFrame extends JFrame{
			public AboutFrame(){
				super("About CONTEXT (v"+version.getVersion()+")");
				JPanel panel = new JPanel(new FlowLayout());
				/*
				panel.add(new JLabel("Phylogenomic Dataset Browser - alpha version."));
				panel.add(new JLabel("This is a development-only private alpha: use at your own risk."));
				panel.add(new JLabel("(c) Joe Parker / Queen Mary University of London, 2013-5."));
				 */
				panel.add(new JLabel("<html><center>"+version.getHTMLCredits()+"</html>"));
				add(panel);
				setSize(650,700);
				setLocationRelativeTo(null);
				setVisible(true);
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		}
}
	
	/**
	 * Internal class corresponding to global application controller
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	private class PhylogenomicDatasetBrowserController{

		PhylogenomicDatasetBrowserModel model;
		PhylogenomicDatasetBrowserView view;
		
		PhylogenomicDatasetBrowserController(PhylogenomicDatasetBrowserModel arg_model, PhylogenomicDatasetBrowserView arg_view, AlignmentsController alignmentsController, PhylogeniesController phylogeniesController){
			model = arg_model;
			view = arg_view;
			
			/* Add action listeners for tree / alignment menu items */
			view.loadAlignmentsSingle.addActionListener(alignmentsController.getAddSingleAlignmentsButtonListener());
			AddBatchAlignmentsButtonListener batchAddAlignmentsListener = alignmentsController.getAddBatchAlignmentsButtonListener();				// get the listener for alignments batch and set taskbar components
			batchAddAlignmentsListener.setTaskBarComponents(view.taskLabel, view.taskbar);				// get the listener for alignments batch and set taskbar components
			view.loadAlignmentsBatch.addActionListener(batchAddAlignmentsListener);
			view.loadTreesSingle.addActionListener(phylogeniesController.getAddSinglePhylogenyButtonListener());
			view.loadTreesBatch.addActionListener(phylogeniesController.getAddBatchPhylogenyButtonListener());
			
			/* Add action listeners for other menu items */
			view.about.addActionListener(new AboutMenuListener());
			view.close.addActionListener(new CloseApplicationListener());
			view.help.addActionListener(new OpenURLListener("https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/master/CONTEXT.md"));
			view.reportBugs.addActionListener(new OpenURLListener("https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline/blob/wiki/ReportBugsRequestFeatures.md"));
			view.contributeCode.addActionListener(new OpenURLListener("https://github.com/lonelyjoeparker/qmul-genome-convergence-pipeline"));
			/*
			 * Old project repository (googlecode) destination URLs (pre May 2015)
			view.help.addActionListener(new OpenURLListener("https://code.google.com/a/eclipselabs.org/p/qmul-genome-convergence-pipeline/wiki/HelpPages"));
			view.reportBugs.addActionListener(new OpenURLListener("https://code.google.com/a/eclipselabs.org/p/qmul-genome-convergence-pipeline/wiki/ReportBugsRequestFeatures"));
			view.contributeCode.addActionListener(new OpenURLListener("https://code.google.com/a/eclipselabs.org/p/qmul-genome-convergence-pipeline"));
			 */
		}
		
		/**
		 * Show the application's About message.
		 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
		 */
		class AboutMenuListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				view.aboutFrame.setVisible(true);
			}
		}

		/**
		 * Close the entire application. This is a hard exit, terminating all threads. 
		 * <p>For more options see {@linkplain http://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe} </p>
		 * TODO Eventually a more thread-safe actionListener (e.g at least checking user intends to terminate all running threads, which may incluse local analyses) needs to be implemented. This may need to be refactored to GlobalController.
		 * 
		 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
		 *
		 */
		class CloseApplicationListener implements ActionListener{
			/**
			 * Performs a hard exit, including all running threads.
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		}

		/**
		 * Opens a URL in response to user action. Follows pattern example at {@linkplain http://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button}
		 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
		 *
		 */
		class OpenURLListener implements ActionListener{
			String URL;
			
			/**
			 * No-arg constructor. Points to default project URL https://github.com/lonelyjoeparker/
			 */
			public OpenURLListener(){
				URL = "https://github.com/lonelyjoeparker";
			}
		
			/**
			 * String arg constructor. Points to URLtoOpen URL.
			 * @param URLtoOpen - url to point to.
			 */
			public OpenURLListener(String URLtoOpen){
				URL = URLtoOpen;
			}
		
			/**
			 * Opens the URL using the default desktop environment browser.
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openWebpage(URL);
			}
		
			void openWebpage(URI uri) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(uri);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		
			void openWebpage(String url) {
				try {
					java.net.URL formedURL = new java.net.URL(url);
					openWebpage(formedURL.toURI());
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
	}
}
