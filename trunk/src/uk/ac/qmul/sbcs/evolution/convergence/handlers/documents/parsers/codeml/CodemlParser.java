package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

import java.util.ArrayList;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlModel;

/**
 * Abstract class defining actions of a CodemlParser.
 * <p>Implementation detail of parsing for each model (M0, M1, Clade Model C etc) handled by constructor.
 * Default constructor should be with {@link java.lang.util.ArrayList}&lt;{@link String}&gt; input data.
 * <p>Resulting {@link CodemlModel} is returned with {@link CodemlParser}.getModelData() call.
 * @see CodemlModel
 * @see CodemlResultReader
 * @see ArrayList
 * @see uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileReader
 * @see uk.ac.qmul.sbcs.evolution.convergence.util.CapitalisedFileReader
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public abstract class CodemlParser {
	
	protected ArrayList<String> input;
	protected CodemlModel model;
	protected CodemlModelType modelType;
	protected CodemlModelNSsitesTypes NSsitesType;
	protected boolean parseSuccessful;
	
	/**
	 * Default constructor with {@link java.lang.util.ArrayList}&lt;{@link String}&gt; input data.
	 * <p>Parsing of input data takes place in constructor; resulting {@link CodemlModel} is returned with {@link CodemlParser}.getModelData() call.
	 * @param data
	 * @see CodemlModel
	 * @see CodemlResultReader
	 */
	public CodemlParser(ArrayList<String> data){
		this.parseSuccessful = false;
		this.input = data;
	}

	/**
	 * No-arg constructor, should never be called as there will be no data to parse. Use new CodemlParser(ArrayList<String> data) instead.
	 */
	@Deprecated
	public CodemlParser(){}
	
	public abstract String toString();
	
	public abstract String guessWhichModel();
	
	public abstract CodemlModel getModelData();
}
