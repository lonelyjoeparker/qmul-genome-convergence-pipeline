package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import javax.swing.JFileChooser;

import com.thoughtworks.xstream.XStream;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.NewickTreeRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.analyses.SiteSpecificLikelihoodSupportAnalysis;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.EmptyAlignmentsListException;
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
			menuBarController.addAddPhylogeniesMenuListener(phylogeniesController.addPhylogenyListener);
		}
		if((analysesController != null)&&(menuBarController != null)){
			// Adds a listener to load previously written SSLS analyses as XMLs
			menuBarController.addAddAnalysesMenuListener(analysesController.addAnalysesListener);
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
			model.setTaxonNamesSet(this.alignmentsController.updateTaxonSet(model.getTaxonNamesSet()));
		} catch (EmptyAlignmentsListException e) {
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
	
	/**
	 * Class to create a set of SitewiseSpecificLikelihoodSupportAnalyses when global 'Create analyses...' action triggered
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 * @see uk.ac.qmul.sbcs.evolution.convergence.analyses.SiteSpecificLikelihoodSupportAnalysis
	 */
	public class CreateAnalysesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent ev){
			// TODO - create analyses from the alignment / phylogeny / global models
			System.out.println("Creating analyses from active sets...");
			Object[][] alignmentsData = alignmentsController.model.getData();
			Object[][] phylogeniesData = phylogeniesController.model.getData();
			File binariesLocation = model.getUserBinariesLocation();
			File workdirLocation = model.getUserWorkdirLocation();
			int thisFilter = 100;
			boolean doFactor = true;
			String[] modelsList = { "wag", "jones" };
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
		/*			
					try {
						mainTreesFile.createNewFile();
						constraintTreeFile.createNewFile();
						labelledTreesFile.createNewFile();
						randomTreesFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			*/		
					// write trees files: write them
					Object[] phylogenyRow = phylogeniesData[0];
					NewickTreeRepresentation phylogeny = ((DisplayPhylogeny)phylogenyRow[0]).getNewickTree();
					phylogeny.write(mainTreesFile);
					phylogeny.write(constraintTreeFile);
					phylogeny.write(labelledTreesFile);
					phylogeny.write(randomTreesFile);
					
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

