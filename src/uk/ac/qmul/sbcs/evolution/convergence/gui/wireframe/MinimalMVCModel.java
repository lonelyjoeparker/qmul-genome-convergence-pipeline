package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

/**
 * The model only holds the internal representation of the data (getters; setters) and allows certain operations to be performed on it (e.g. performActionOnModelDataSwapCase())
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class MinimalMVCModel {

	private String internalData;
	private boolean lowercase;
	
	public MinimalMVCModel(){
		internalData = "";
		lowercase = true;
	}

	public String getInternalData() {
		return internalData;
	}

	public void setInternalData(String internalData) {
		this.internalData = internalData;
	}
	
	public void performActionOnModelDataSwapCase(){
		if(lowercase){
			internalData = internalData.toUpperCase();
			lowercase = false;
		}else{
			internalData = internalData.toLowerCase();
			lowercase = true;
		}
	}
}
