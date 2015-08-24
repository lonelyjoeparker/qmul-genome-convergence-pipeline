package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.ResultsTableModel;

public class SecondWireframeResultsController {

	ResultsTableModel model;
	WireframeResultsPanel view;

	public SecondWireframeResultsController(ResultsTableModel aModel, WireframeResultsPanel aView, JMenuItem menuItem){
		model = aModel;
		view = aView;
		menuItem.addActionListener(new AdditionalAddResultsButtonListener());
	}

	class AdditionalAddResultsButtonListener implements ActionListener{
		String chooserText = "choose a results file..";
		@Override
		public void actionPerformed(ActionEvent ev) {
			int returnVal = view.getFileChooser().showOpenDialog(view);
			File resultFile = view.getFileChooser().getSelectedFile();
			// do nothing for now
			model.addRow(resultFile);
			view.repaint();
		}
	}
	
	
	public JComponent getView() {
		return view.getPanel();
	}
}

