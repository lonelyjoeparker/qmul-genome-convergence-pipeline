package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

import java.util.ArrayList;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlModel;

public abstract class CodemlParser {
	
	private ArrayList<String> input;
	private CodemlModelType modelType;
	private CodemlModelNSsitesTypes NSsitesType;
	
	public CodemlParser(){}
	
	public abstract String toString();
	
	public abstract String guessWhichModel();
	
	public abstract CodemlModel getModelData();
}
