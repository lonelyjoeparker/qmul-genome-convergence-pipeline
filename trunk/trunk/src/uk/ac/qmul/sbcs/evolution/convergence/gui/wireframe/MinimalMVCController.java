package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The controller specifies how the view and model objects interact in two ways:
 * <ol>
 * <li>By specifying which ActionListeners link which UI components in the view</li>
 * <li>By specifying which/how values are passed from the UI to the model within the ActionListeners themselves</li>
 * </ol>
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class MinimalMVCController {

	MinimalMVCView view;
	MinimalMVCModel model;
	
	/**
	 * Default no-arg constructor. Deprecated.
	 */
	@Deprecated
	public MinimalMVCController() {
		// TODO Auto-generated constructor stub
		view = null;
		model = null;
	}

	public MinimalMVCController(MinimalMVCView someView, MinimalMVCModel someModel) {
		view = someView;
		model = someModel;

		/*
		// this is where we link which button / user actions to which logic routines (listeners). 
		// the listeners themselves determine how UI elements and data model interact
		 */
		view.setButtonActionListerner(new SwapButtonListener());
	}

	class SwapButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			/*
			 * we will take a value from the UI and
			 * pass it to the model.
			 * 
			 * at the same time (cos I can't be arsed with
			 * another button at the mo) we will perform
			 * an operation on said data model (in other
			 * words repeated button clicks even without
			 * updating the text field in the view will 
			 * still do something..
			 */
			
			// get value from UI
			String newText = view.getText();
			// pass that value to the data model
			model.setInternalData(newText);
			// do some operation on it
			model.performActionOnModelDataSwapCase();
			// get the output from the data model
			String replaceText = model.getInternalData();
			// pass that value back to the UI
			view.setText(replaceText);
			
			// so the flow goes:
			// view -> controller -> model -> controller -> view
		}
		
	}
}
