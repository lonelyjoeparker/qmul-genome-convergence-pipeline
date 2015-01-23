package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuBar;

import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.AddBatchAlignmentsButtonListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.AddSingleAlignmentsButtonListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AnalysesController.AddAnalysesListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.GlobalController.CreateAnalysesListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.GlobalController.RunLocalAnalysesListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.PhylogeniesController.AddPhylogeniesListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.GlobalModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.MenuBarFactory;

/**
 * Controller class for the menu bar. Uses the globalModel for data, as menu bar only interacts with globals.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class MenuBarController {

	GlobalModel model;
	MenuBarFactory view;

	/**
	 * No-arg constructor is deprecated.
	 */
	@Deprecated
	public MenuBarController(){}

	/**
	 * Constructor.
	 * @param globalModel
	 * @param menuBar
	 */
	public MenuBarController(GlobalModel globalModel, MenuBarFactory menuBar) {
		model = globalModel;
		view = menuBar;

		// Add actionlisteners etc for File menu
		view.about.addActionListener(new AboutMenuListener());
		view.close.addActionListener(new CloseApplicationListener());

		// Add actionlisteners etc for Help menu
		view.help.addActionListener(new OpenURLListener("https://code.google.com/a/eclipselabs.org/p/qmul-genome-convergence-pipeline/wiki/HelpPages"));
		view.reportBugs.addActionListener(new OpenURLListener("https://code.google.com/a/eclipselabs.org/p/qmul-genome-convergence-pipeline/wiki/ReportBugsRequestFeatures"));
		view.contributeCode.addActionListener(new OpenURLListener("https://code.google.com/a/eclipselabs.org/p/qmul-genome-convergence-pipeline"));
	}

	public JMenuBar getTheMenuView() {
		return view;
	}


	/**
	 * Add an actionListener to the 'Add alignemtns...> single' menu item.
	 * @param addAlignmentsListener
	 */
	public void addAddAlignmentsMenuListenerSingle(AddSingleAlignmentsButtonListener addAlignmentsListener) {
		view.loadAlignmentsSingle.addActionListener(addAlignmentsListener);
	}
	
	/**
	 * Add an actionListener to the 'Add alignemtns...> batch' menu item.
	 * @param addAlignmentsListener
	 */
	public void addAddAlignmentsMenuListenerBatch(AddBatchAlignmentsButtonListener addAlignmentsListener) {
		view.loadAlignmentsBatch.addActionListener(addAlignmentsListener);
	}

	/**
	 * Add an actionListener to the 'Add phylogenies...' menu item.
	 * @param addAlignmentsListener
	 */
	public void addAddPhylogeniesMenuListener(AddPhylogeniesListener addPhylogeniesListener) {
		view.loadTrees.addActionListener(addPhylogeniesListener);
	}

	/**
	 * Add an actionListener to the 'Load pre-specified as XML...' menu item.
	 * @param al
	 */
	public void addAddAnalysesMenuListener(AddAnalysesListener addAnalysesListener) {
		view.loadAnalysisXMLs.addActionListener(addAnalysesListener);
	}
	
	/**
	 * Add an actionListener to the 'Add Results...' menu item.
	 * @param al
	 */
	public void addAddResultsMenuListener(ActionListener al){
		view.loadResults.addActionListener(al);
	}

	/**
	 * Add an action listener to the 'Show Parameters window' menu item.
	 * @param al
	 */
	public void addShowParameterWindowListener(ActionListener al){
		view.showParameters.addActionListener(al);
	}

	/**
	 * Add an ActionListener to handle creating SSLS uk.ac.qmul.sbcs.evolution.convergence.analyses.SiteSpecificLikelihoodSupportAnalysis from active phylogenies/alignments
	 * @param createAnalysesListener
	 */
	public void addCreateAnalysesMenuListener(CreateAnalysesListener createAnalysesListener) {
		view.createConvergenceAnalyses.addActionListener(createAnalysesListener);
	}
	
	/**
	 * Show the application's About message.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 */
	class AboutMenuListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			new MenuBarFactory.AboutFrame();
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
		 * No-arg constructor. Points to default project URL https://code.google.com/a/eclipselabs.org/p/qmul-genome-convergence-pipeline/w/list
		 */
		public OpenURLListener(){
			URL = "https://code.google.com/a/eclipselabs.org/p/qmul-genome-convergence-pipeline/w/list";
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

	/**
	 * ActionListener for running local convergence analyses. Triggered when user selects 'run convergence analyses on this computer' menu item
	 * @param runLocalAnalysesListener
	 */
	public void addRunLocalAnalysesListener(RunLocalAnalysesListener runLocalAnalysesListener) {
		this.view.runLocalAnalysis.addActionListener(runLocalAnalysesListener);
	}
}
