package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.thoughtworks.xstream.XStream;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.PhylogenyConvergenceContext;
import uk.ac.qmul.sbcs.evolution.convergence.RequiredPhylogenyNotSpecifiedException;
import uk.ac.qmul.sbcs.evolution.convergence.TaxaListsMismatchException;
import uk.ac.qmul.sbcs.evolution.convergence.analyses.SiteSpecificLikelihoodSupportAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.EmptyAlignmentsListException;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AnalysesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.GlobalModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.GlobalApplicationView;
import uk.ac.qmul.sbcs.evolution.convergence.runners.GeneralCongruenceRunnerXML;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.SerfileFilter;
import uk.ac.qmul.sbcs.evolution.convergence.util.VerboseSystemCommand;
import uk.ac.qmul.sbcs.evolution.sandbox.ResultsPrinterSimple;

public class GlobalController {

	GlobalModel model;
	GlobalApplicationView view;
	AlignmentsController alignmentsController;
	PhylogeniesController phylogeniesController;
	AnalysesController analysesController;
	ResultsController resultsController;
	MenuBarController menuBarController;
	CreateAnalysesListener createAnalysesListener = new CreateAnalysesListener();

	/**
	 * No-arg constructor. Deprecated
	 */
	@Deprecated
	public GlobalController(){}

	/**
	 * Preferred constructor.
	 * @param globalModel
	 * @param globalView
	 */
	public GlobalController(GlobalModel globalModel, GlobalApplicationView globalView) {
		model = globalModel;
		view = globalView;
		view.sidePanelUpdateGlobalSettingsButton.addActionListener(new UpdateGlobalVariablesListener());
		view.sidePanelSaveGlobalSettingsButton.addActionListener(new SaveGlobalVariablesListener());
		view.setBinariesLocation.addActionListener(new SetBinariesLocationListener());
		view.setWorkdirLocation.addActionListener(new SetWorkdirLocationListener());
	}

	/**
	 * Add the Controller for the alignments panel / model.
	 * @param alignmentsController
	 */
	public void addAlignmentsController(AlignmentsController addAlignmentsController) {
		this.alignmentsController = addAlignmentsController;
		view.addTab(this.alignmentsController.getView(), "Alignments");
	}

	/**
	 * Add the Controller for the phylogenies panel / model.
	 * @param addPhylogeniesController
	 */
	public void addPhylogeniesController(PhylogeniesController addPhylogeniesController) {
		this.phylogeniesController = addPhylogeniesController;
		view.addTab(this.phylogeniesController.getView(), "Phylogenies");
	}

	/**
	 * Add the Controller for the analyses panel / model.
	 * @param addPhylogeniesController
	 */
	public void addAnalysesController(AnalysesController addAnalysesController) {
		this.analysesController = addAnalysesController;
		view.addTab(this.analysesController.getView(), "Analyses");
	}


	/**
	 * Add the Controller for the results panel / model.
	 * @param addResultsController
	 */
	public void addResultsController(ResultsController addResultsController) {
		this.resultsController = addResultsController;
		view.addTab(this.resultsController.getView(), "Results");
	}

	/**
	 * Add the Controller for the menu bar.
	 * @param menuController
	 */
	public void addMenuBarController(MenuBarController menuController) {
		this.menuBarController = menuController;
		view.setANewJMenuBar(menuBarController.getTheMenuView());
	}

	/**
	 * Add those listeners which are global e.g. span multiple models/views/controllers
	 */
	public void addGlobalActionListeners(){
		if((alignmentsController != null)&&(menuBarController != null)){
			menuBarController.addAddAlignmentsMenuListenerSingle(alignmentsController.addAlignmentsListenerSingle);
			menuBarController.addAddAlignmentsMenuListenerBatch(alignmentsController.addAlignmentsListenerBatch);
			menuBarController.addShowParameterWindowListener(new ShowParametersWindowListener());
			alignmentsController.setGlobalController(this);
		}
		if((phylogeniesController != null)&&(menuBarController != null)){
			menuBarController.addAddPhylogeniesMenuListener(phylogeniesController.addSinglePhylogenyListener);
		}
		if((analysesController != null)&&(menuBarController != null)){
			// Adds a listener to load previously written SSLS analyses as XMLs
			menuBarController.addAddAnalysesMenuListener(analysesController.addAnalysesListener);
			// Add an additional listener with access to global variables, to apply the XMLs' taxonlist/workdir info to global params
			analysesController.addAnalysesListener.registerSecondActionListener(new UpdateGlobalVariablesFromAnalysisXMLsListener());
			menuBarController.addRunLocalAnalysesListener(new RunLocalAnalysesListener());
		}
		if((resultsController != null)&&(menuBarController != null)){
			menuBarController.addAddResultsMenuListener(resultsController.addResultsListener);
		}
		if((alignmentsController != null)&&(phylogeniesController != null)&&(analysesController != null)&&(resultsController != null)&&(menuBarController != null)){
			// Adds a listener to build new SSLS analyses from active taxon set/alignments/phylogenies/parameters
			menuBarController.addCreateAnalysesMenuListener(createAnalysesListener);
		}
	}

	// Set the global model (analysis parameters) using the global view values
	public void saveGlobalVariableModelFromView(){
		// first the debug flag
		boolean setDebug = view.sidePanelDebugIndicator.isSelected();
		System.out.println("Set global debug to: "+setDebug);
		model.setDEBUG(setDebug);
		// not the TaxonList as we have no reliable way to parse the JTextArea at present.		
	}

	public void updateGlobalVariableView(){
		// See if we can update the taxonList
		try {
			model.setTaxonNamesSet(this.phylogeniesController.updateTaxonSet(model.getTaxonNamesSet()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Update the view panels with actual values
		view.sidePanelDebugIndicator.setSelected(model.isDEBUG());
		view.sidePanelTaxonListText.setText(model.getTaxonNamesSetAsMultilineString());
	}

	public void updateTaskbar(String message, int percentComplete){
		view.taskLabel.setText(message);
		view.taskbar.setValue(percentComplete);
	}

	class ShowParametersWindowListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0){
			if(view.parametersWindow.isVisible()){
				view.parametersWindow.setVisible(false);
				menuBarController.view.showParameters.setSelected(false);
			}else{
				//view.parametersWindow.setLocation(64, 128); // to re-set the location back to the start. we'll disable this so the parameters window reappears where it was last positioned by the user.
				view.parametersWindow.setVisible(true);
				menuBarController.view.showParameters.setSelected(true);
			}
		}
	}

	class SaveGlobalVariablesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveGlobalVariableModelFromView();
		}
	}

	class UpdateGlobalVariablesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			updateGlobalVariableView();			
		}
	}

	/**
	 * Use a JFileChooser to select a directory for the required binaries.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	class SetBinariesLocationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			int returnVal = view.setBinariesLocationChooser.showOpenDialog(view);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File userBinariesLocation = view.setBinariesLocationChooser.getSelectedFile();
				if(userBinariesLocation.canRead()){
					view.setBinariesLocationLabel(userBinariesLocation.getAbsolutePath());
					model.setUserBinariesLocation(userBinariesLocation);
				}else{
					System.err.println("Unable to read "+userBinariesLocation.getAbsolutePath());
					view.setBinariesLocationLabel("WARNING! Unable to read "+userBinariesLocation.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Use a JFileChooser to select a directory for the working directory.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	class SetWorkdirLocationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			int returnVal = view.setWorkdirLocationChooser.showOpenDialog(view);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File userWorkdirLocation = view.setWorkdirLocationChooser.getSelectedFile();
				if(userWorkdirLocation.canRead()){
					view.setWorkdirLocationLabel(userWorkdirLocation.getAbsolutePath());
					model.setUserWorkdirLocation(userWorkdirLocation);
				}else{
					System.err.println("Unable to read "+userWorkdirLocation.getAbsolutePath());
					view.setWorkdirLocationLabel("WARNING! Unable to read "+userWorkdirLocation.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Validate the binaries directory: check that each of the
	 * required binaries can be found, executed, and gives 
	 * expected output.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	class ValidateBinariesLocationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			/*
			 * Validate the binaries directory: check that each of the
			 * required binaries can be found, executed, and gives 
			 * expected output.
			 */
		}
	}

	/**
	 * Validate the working directory: check that files / 
	 * directories can be written and created.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	class ValidateWorkdirLocationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			/*
			 * Validate the working directory: check that files / 
			 * directories can be written and created.
			 */
		}
	}

	/**
	 * Attempts to run convergence SSLSAnalysis analyses on this computer.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 * @see SiteSpecificLikelihoodSupportAnalysis
	 * @see AnalysesModel
	 * @see AnalysesController
	 */
	class RunLocalAnalysesListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			// try and set the working directory to workingdir
			System.setProperty("user.dir", model.getUserWorkdirLocation().getAbsolutePath());
			// try another means
			try {
				Runtime.getRuntime().exec("pwd");
				Runtime.getRuntime().exec("cd "+model.getUserWorkdirLocation().getAbsolutePath());
				Runtime.getRuntime().exec("pwd");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// try yet again
			new VerboseSystemCommand("pwd");
			new VerboseSystemCommand("cd "+model.getUserWorkdirLocation().getAbsolutePath());
			new VerboseSystemCommand("pwd");

			/* iterate through the active analyses */
			Object[][] data = analysesController.getModel().getData();
			if(data != null){
				try{
					for(Object[] row:data){
						if(row.length>0){
							SiteSpecificLikelihoodSupportAnalysis analysis = (SiteSpecificLikelihoodSupportAnalysis)row[0];
							int numberOfTrees = analysis.getMainTrees().getNumberOfTrees();
							try{
								analysis.run();
								File localAnalysisSubDir = analysis.getWorkDir();
								if(localAnalysisSubDir.isDirectory()){
									// try and read the serfiles
									File[] subdirContents = localAnalysisSubDir.listFiles(new SerfileFilter());
									for(File resultsSerfile:subdirContents){
										resultsController.addSerfileResults(resultsSerfile);
									}
									// attempt to analyse them here with a ResultsPrinterSimple
									ResultsPrinterSimple rps = new ResultsPrinterSimple(localAnalysisSubDir.getAbsolutePath(), "1", ""+numberOfTrees);
									rps.go();
								}
							}catch(Exception ex){
								System.err.println("Unable to run analysis.");
								ex.printStackTrace();
							}
						}
					}
				}catch(Exception ex){
					System.err.println("Unable to retrieve data array.");
					ex.printStackTrace();
				}
			}
		}
	}
	
	public class UpdateGlobalVariablesFromAnalysisXMLsListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			try{
				/* Try and get binaries dir, work dir, and taxon list from analyses */
				SiteSpecificLikelihoodSupportAnalysis analysis = analysesController.model.getLastRowSSLSAnalysis();
				File parametisedWorkDir = analysis.getWorkDir();
				File parametisedBinariesDir = analysis.getBinariesLocation();
				TreeSet<String> parametisedTaxonNamesSet = analysis.getTaxonNamesSet();
				/* Try and set these in the global model */
				model.setUserWorkdirLocation(parametisedWorkDir);
				model.setUserBinariesLocation(parametisedBinariesDir);
				model.setTaxonNamesSet(parametisedTaxonNamesSet);
				/* Try and set these in the global view (params pane view) */
				view.workdirLocation.setText(parametisedWorkDir.getAbsolutePath());
				view.binariesLocation.setText(parametisedBinariesDir.getAbsolutePath());
				view.sidePanelTaxonListText.setText(model.getTaxonNamesSetAsMultilineString());
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
		
	}

	/**
	 * Class to create a set of SitewiseSpecificLikelihoodSupportAnalyses when global 'Create analyses...' action triggered
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 * @see uk.ac.qmul.sbcs.evolution.convergence.analyses.SiteSpecificLikelihoodSupportAnalysis
	 */
	public class CreateAnalysesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent ev){
			// create analyses from the alignment / phylogeny / global models
			try{
				constructAnalyses();
			}catch (NullPointerException nex){
				String[] completeErrorMessage = "Unable to proceed.\n \nTo set up an analysis, you need:\n\t\t* At least one sequence alignment,\n\t\t* Exactly one species/reference phylogeny,\n\t\t* At least one alternative/test tree,\n\t\t* Location of directory for binaries,\n\t\t* Location of working directory, and\n\t\t* An active taxon list.\n \nError message: ".split("\n");
				String stackTraceMessage = "\nNullPointerException.";
				for(StackTraceElement elem:nex.getStackTrace()){
					stackTraceMessage += "\n\tat: " + elem.toString() ;
				}
				JTextArea textArea = new JTextArea();
				textArea.setEditable(false);
				textArea.setText(stackTraceMessage);
				
				// stuff it in a scrollpane with a controlled size.
				JScrollPane scrollPane = new JScrollPane(textArea);		
				scrollPane.setPreferredSize(new Dimension(350, 150));
				
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
				for(String message:completeErrorMessage){
					panel.add(new JLabel(message));
				}
				panel.add(scrollPane);

				JOptionPane.showMessageDialog(null, panel, "Unable to create analyses", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"));
				nex.printStackTrace();
				return;
				//throw new Exception("Need at least one alternative/test tree, and exactly one species tree, to proceed.");
			}
		}
		
		private void constructAnalyses(){
			System.out.println("Creating analyses from active sets...");
			Object[][] alignmentsData = alignmentsController.model.getData();
			Object[][] phylogeniesData = phylogeniesController.model.getData();
			File binariesLocation = model.getUserBinariesLocation();
			File workdirLocation = model.getUserWorkdirLocation();
			int thisFilter = 100;
			boolean doFactor = true;
			String[] modelsList = { "wag" };
			String[] begins = { "/bin/pwd"	, "/bin/echo poo" };
			String[] ends   = { "/bin/ls -t", "/bin/echo oop" };
			String runID;
			TreeSet<String> taxaList = new TreeSet<String>();
			taxaList.addAll(model.getTaxonNamesSet());
			File mainTreesFile, constraintTreeFile, labelledTreesFile, randomTreesFile; //not instantiated yet

			/* 
			 * 
			 * for each alignment, attempt to create a 
			 * uk.ac.qmul.sbcs.evolution.convergence.analyses.SiteSpecificLikelihoodSupportAnalysis 
			 * object using the alignment, trees, and other parameters..
			 * 
			 * first basic != null checking:
			 */
			if((alignmentsData.length>0) && (phylogeniesData.length>0) && (binariesLocation.canRead()) && (workdirLocation.canRead()) && (workdirLocation.canWrite())){
				// for each alignment
				for(Object[] alignmentData: alignmentsData){
					SiteSpecificLikelihoodSupportAnalysis analysis;
					AlignedSequenceRepresentation alignment;
					File alignmentFile;
					alignment = (AlignedSequenceRepresentation)alignmentData[alignmentData.length-1];	// assumes the AlignedSequenceRepresentation is the last column in the AlignmentsTableModel. See r340

					// set up a subdir in rundir
					String alignmentBaseName = alignment.file.getName().split("\\.")[0];
					File locusSubdirLocation = new File(workdirLocation.getAbsolutePath()+"/"+alignmentBaseName);
					if(!locusSubdirLocation.exists()){
						locusSubdirLocation.mkdir();
					}

					// write alignment file
					alignmentFile = new File(locusSubdirLocation.getAbsolutePath()+"/"+alignmentBaseName+".phy");
					/*	
				try {
					alignmentFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("could not create "+locusSubdirLocation.getAbsolutePath()+"/"+alignmentBaseName+".phy");
					e.printStackTrace();
				}
					 */			
					alignment.writePhylipFile(alignmentFile, true);

					// write trees files: make the file paths
					mainTreesFile 		= new File(locusSubdirLocation.getAbsolutePath()+"/"+"mainTrees.tre");
					constraintTreeFile 	= new File(locusSubdirLocation.getAbsolutePath()+"/"+"constraintTree.tre");
					labelledTreesFile 	= new File(locusSubdirLocation.getAbsolutePath()+"/"+"labelledTrees.tre");
					randomTreesFile 	= new File(locusSubdirLocation.getAbsolutePath()+"/"+"randomTrees.tre");

					/* write trees files: Collect all trees of each type from phylogeny model:
					 * 
					 *  mainTrees: speciesTree and alternativeTrees (concatenated together)
					 *  speciesTree: a single phylogeny representing the null hypothesis (normally the species tree/ancestry)
					 *  alternativeTrees: one or more alternative (hypothesis) trees, for instance a discordant gene tree
					 *  contstraintTrees: one or more partially-resolved phylogenies (i.e., include soft polyomies), to be resolved with RAxML
					 *  randomTrees: randomly generated phylogenies with the same tip labelling, used to form the expected random control trees distribution (required to calculate the Uc statistic)
					 *  labelledTrees: trees with internal node labelling in PAML format, used for downstream codeml analyses outside of java runtime but parsed/pruned here for convenience
					 *
					 *
					 * Then write them to canonical outputs.
					 */

					NewickTreeRepresentation mainTrees, speciesTree, alternativeTrees, constraintTrees, labelledTrees, randomTrees;
					mainTrees = null;
					// main tress
					try {
						speciesTree = phylogeniesController.getReferenceSpeciesPhylogeny();
						alternativeTrees = phylogeniesController.getAlternativeTestPhylogenies();
						if(speciesTree != null && alternativeTrees != null){
							try {
								mainTrees = speciesTree.concatenate(alternativeTrees);
								mainTrees.write(mainTreesFile);
							} catch (TaxaListsMismatchException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							JOptionPane.showMessageDialog(null, "Need at least one alternative/test tree, and exactly one species tree, to proceed.", "Phylogenies missing!", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"));
							return;
							//throw new Exception("Need at least one alternative/test tree, and exactly one species tree, to proceed.");
						}
					} catch (RequiredPhylogenyNotSpecifiedException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Need at least one alternative/test tree, and exactly one species tree, to proceed.", "Phylogenies missing!", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"));
						e1.printStackTrace();
						return;
						//throw new Exception("Need at least one alternative/test tree, and exactly one species tree, to proceed.");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Need at least one alternative/test tree, and exactly one species tree, to proceed.", "Phylogenies missing!", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"));
						e.printStackTrace();
						return;
						//throw new Exception("Need at least one alternative/test tree, and exactly one species tree, to proceed.");
					}
					// constraint trees
					try {
						constraintTrees = phylogeniesController.getPhylogeniesByContext(PhylogenyConvergenceContext.RAXML_RESOLVED_PARTIALLY_CONSTRAINED_PHYLOGENY);
						// check to see if the constraint trees match the main trees' taxon list
						if(!constraintTrees.taxaListsMatch(mainTrees)){
							JOptionPane.showMessageDialog(null, "Taxon lists do not match (main and constraint trees) - analysis likely to fail!", "Warning!", JOptionPane.WARNING_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"));
						}
						constraintTrees.write(constraintTreeFile);
					} catch (RequiredPhylogenyNotSpecifiedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// random
					try {
						randomTrees = phylogeniesController.getPhylogeniesByContext(PhylogenyConvergenceContext.RANDOM_CONTROL_PHYLOGENY);
						// check to see if the random trees match the main trees' taxon list
						if(!randomTrees.taxaListsMatch(mainTrees)){
							JOptionPane.showMessageDialog(null, "Taxon lists do not match (main and random trees) - analysis likely to fail!", "Warning!", JOptionPane.WARNING_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"));
						}
						randomTrees.write(randomTreesFile);
					} catch (RequiredPhylogenyNotSpecifiedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// labelled
					try {
						labelledTrees = phylogeniesController.getPhylogeniesByContext(PhylogenyConvergenceContext.NO_CONVERGENCE_CONTEXT_CODEML_NODE_LABELLING);
						// check to see if the labelled trees match the main trees' taxon list
						if(!labelledTrees.taxaListsMatch(mainTrees)){
							JOptionPane.showMessageDialog(null, "Taxon lists do not match (main and labelled trees) - analysis likely to fail!", "Warning!", JOptionPane.WARNING_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"));
						}
						labelledTrees.write(labelledTreesFile);
					} catch (RequiredPhylogenyNotSpecifiedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}


					// run ID is hardcoded for now
					runID = "run_"+System.currentTimeMillis()+"_"+alignment.hashCode();

					// convert etc
					// how to handle phylogenies...?
					analysis = new SiteSpecificLikelihoodSupportAnalysis(
							alignmentFile, 
							mainTreesFile, 
							constraintTreeFile,
							labelledTreesFile, 
							randomTreesFile, 
							locusSubdirLocation, 
							binariesLocation, 
							runID, 
							taxaList,
							modelsList, 
							thisFilter, 
							doFactor, 
							begins, 
							ends
					);
					analysis.setDoFullyUnconstrainedRAxML(true); // do this through constructor eventually
					analysis.preValidate();

					// add to analyses panel
					analysesController.model.addAnalysisRow(analysis);
					// write XML (?)
					File XMLFile 	= new File(locusSubdirLocation.getAbsolutePath()+"/SSLSanalysis.xml");
					try {
						XStream xstream = new XStream();
						new BasicFileWriter(XMLFile,xstream.toXML(analysis));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}

