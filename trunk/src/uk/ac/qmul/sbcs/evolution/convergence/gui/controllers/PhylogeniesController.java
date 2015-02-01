package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.AddBatchAlignmentsButtonListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.AddSingleAlignmentsButtonListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.AlignmentsImportTask;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.EmptyAlignmentsListException;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.PhylogeniesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.PhylogeniesView;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

public class PhylogeniesController {
	PhylogeniesModel model;
	PhylogeniesView view;
	AddSinglePhylogeniesListener addSinglePhylogenyListener = new AddSinglePhylogeniesListener();
	AddBatchPhylogeniesListener addBatchPhylogeniesListener = new AddBatchPhylogeniesListener();
	
	/**
	 * No-arg constructor is deprecated
	 */
	@Deprecated
	public PhylogeniesController(){}
	
	public PhylogeniesController(PhylogeniesModel initModel, PhylogeniesView initView){
		model = initModel;
		view = initView;
		view.addTable(model);
		view.addRowSelectionListener(new PhylogeniesRowListener());
		
	}

	public PhylogeniesModel getModel() {
		return model;
	}

	public JComponent getView() {
		return view.getPanel();
	}
	
	public class AddSinglePhylogeniesListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			view.getFileChooser().showOpenDialog(view);
			File phylogenyFile = view.getFileChooser().getSelectedFile();
			// do nothing for now
			model.addPhylogenyRowAsStringTree(phylogenyFile);
			Object[][] modelData = model.getData();
			view.updatePhylogenyDisplay((DisplayPhylogeny) modelData[modelData.length-1][0]);
//			view.repaint();
		}
	}
	
	public class AddBatchPhylogeniesListener implements ActionListener, PropertyChangeListener{
		PhylogeniesImportTask task;
		JLabel taskLabel;
		JProgressBar taskBar;
		public final String completeText = "Done ";
		public final int completeInt = 100;
		
		/**
		 * Set an optional tasklabel/bar
		 * @param label
		 * @param bar
		 */
		public void setTaskBarComponents(JLabel label, JProgressBar bar){
			taskLabel = label;
			taskBar = bar;
		}
		
		@Override
		public void actionPerformed(ActionEvent ev) {
			int returnVal = view.getDirectoryChooser().showOpenDialog(view);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File alignmentDirectory = view.getDirectoryChooser().getSelectedFile();
				if(alignmentDirectory.isDirectory()){
			        if((taskLabel != null)&&(taskBar != null)){
				        task = new PhylogeniesImportTask(taskLabel, taskBar);
			        }else{
			        	task = new PhylogeniesImportTask();
			        }
			        task.addPropertyChangeListener((PropertyChangeListener) this);
			        task.execute();
			        if((taskLabel != null)&&(taskBar != null)){
			        	taskLabel.setText(completeText);
			        	taskBar.setValue(completeInt);
			        }
				}			
			}
		}

		/**
	     * Invoked when task's progress property changes.
	     */
	    public void propertyChange(PropertyChangeEvent evt) {
	        if ("progress" == evt.getPropertyName()) {
	        	int progress = task.getProgress();
	            String message = "Adding phylogenies ("+progress+";%)...";
		        if((taskLabel != null)&&(taskBar != null)){
		        	taskLabel.setText(message);
		        	taskBar.setValue(progress);
		        }
	        } 
	    }
	}

    /**
     * Add phylogenies as a batch
     * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
     * @see SwingWorker
     */
	class PhylogeniesImportTask extends SwingWorker<Void, Void> {
		JLabel taskLabel;
		JProgressBar taskBar;
		public final String completeText = "Done ";
		public final int completeInt = 100;
		
		/**
		 * Set an optional tasklabel/bar
		 * @param label
		 * @param bar
		 */
		public PhylogeniesImportTask(JLabel label, JProgressBar bar){
			taskLabel = label;
			taskBar = bar;
		}

		/**
		 * No-arg constructor - risky since taskLabel and taskBar will not be instantiated.
		 */
		public PhylogeniesImportTask() {
			// TODO Auto-generated constructor stub
		}

		/*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
			File[] files  = view.getDirectoryChooser().getSelectedFile().listFiles();
			int totalFiles = files.length;
			int filesTried = 0;
			for(File phylogenyFile:files){
				try {
					model.addPhylogenyRowAsStringTree(phylogenyFile);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				filesTried++;
				progress = Math.round(((float)filesTried / (float)totalFiles)*100f);
				String message = "Adding phylogenies ("+filesTried+"; "+progress+"%)...";
		        if((taskLabel != null)&&(taskBar != null)){
		        	taskLabel.setText(message);
		        	taskBar.setValue(progress);
		        }
                setProgress(Math.min(progress, 100));
				System.out.println("Adding phylogenies ("+filesTried+"; "+progress+"%)...");
			}
          return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
	        if((taskLabel != null)&&(taskBar != null)){
	        	taskLabel.setText(completeText);
	        	taskBar.setValue(completeInt);
	        }
        }
    }
	
	/**
	 * Listener for phylogenies table row selections
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	public class PhylogeniesRowListener implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			/* Get the table and print out some debug info... */
			JTable phylogenyTable = view.getTable();
			/* Get the selected table row, via view */
			int viewModelRow = phylogenyTable.getSelectedRow();
			Object[] a_row = model.getData()[viewModelRow];
			String nTaxa = a_row[1].toString();
			String treeString = a_row[2].toString();
			System.out.println("VIEW ROW ("+viewModelRow+") selected n taxa : "+nTaxa+", tree "+treeString);
			/* Get the selected row, via model */
			int tableModelRow = phylogenyTable.convertRowIndexToModel(viewModelRow);
			a_row = model.getData()[tableModelRow];
			nTaxa = a_row[1].toString();
			treeString = a_row[2].toString();
			System.out.println("MODEL ROW ("+tableModelRow+") selected n taxa: "+nTaxa+", tree "+treeString);
			System.out.println(a_row[3]);
			/* Attempt to update the view representation */
			DisplayPhylogeny dp = (DisplayPhylogeny) a_row[0];
			view.updatePhylogenyDisplay(dp);
//			view.repaint();
		}
		
	}
	
	public HashSet<String> updateTaxonSet(HashSet<String> taxonNamesSet) throws Exception{
		Object[][] data = model.getData();
		if(data != null){
			for(Object[] alignment:data){
				DisplayPhylogeny a = (DisplayPhylogeny)alignment[0];
				try {
					taxonNamesSet = a.expandTaxonNameSet(taxonNamesSet);
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return taxonNamesSet;
		}else{
			throw new Exception();
		}
	}

	public AddSinglePhylogeniesListener getAddSinglePhylogenyButtonListener(){
		return this.addSinglePhylogenyListener;
	}

	public AddBatchPhylogeniesListener getAddBatchPhylogenyButtonListener(){
		return this.addBatchPhylogeniesListener;
	}
}
