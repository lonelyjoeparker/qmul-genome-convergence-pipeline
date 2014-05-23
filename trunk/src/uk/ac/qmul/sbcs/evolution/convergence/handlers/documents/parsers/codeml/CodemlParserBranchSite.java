/**
 * 
 */
package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlModel;

/**
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * Concrete implementation of {@link CodemlParser} to parse codeml sitewise model M1 (Nearly Neutral)
 */
public class CodemlParserBranchSite extends CodemlParser {

	/**
	 * @param data
	 */
	public CodemlParserBranchSite(ArrayList<String> data) {
		super(data);
		// set model type
		this.modelType = CodemlModelType.MODEL_TYPE_3_CLADE;
		this.NSsitesType = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION;
		this.model = new CodemlModel();
		model.setModelString(this.toString());
		model.setRawData((String[]) data.toArray(new String[data.size()]));

		// prepare to parse
		boolean inNEB = false;
		boolean inBEB = false;
		float[] global_dNdS = new float[4]; 		// global omegas (3 for M2)
		float[] global_proportions = new float[4]; // global omegas proportions (estimates; 3 for M2)
		float lnL;
		ArrayList<Float> estimated_omegas = new ArrayList<Float>();
		ArrayList<float[]> estimated_proportions = new ArrayList<float[]>();
		Pattern p_NEB = Pattern.compile("NEB"); 
		Pattern p_BEB = Pattern.compile("BEB"); 
		Pattern p_POS = Pattern.compile("Positively"); 
		Pattern p_num = Pattern.compile("^[\\ ]{1,}[0-9]{1,}");
		Pattern rates = Pattern.compile("foreground");
		Pattern props = Pattern.compile("proportion");
		Pattern p_lnL = Pattern.compile("lnL");
		//Pattern positive = Pattern.compile("11\\)");

		// actually parse the data
		for(String line:data){
			Matcher isNEB = p_NEB.matcher(line);
			Matcher isBEB = p_BEB.matcher(line);
			Matcher isPOS = p_POS.matcher(line);
			Matcher isNum = p_num.matcher(line);
			Matcher isRat = rates.matcher(line);
			Matcher isPro = props.matcher(line);
			Matcher isLnL = p_lnL.matcher(line);
			//Matcher isPos = positive.matcher(line);
			
			if(isNum.find() && inBEB && line.length()>2){
				// We are in a data line, get probabilities
				String[] tokens = line.split("\\ {1,}");
				float[] probabilities = new float[4];
				
				//  iterate over all omega cat BEB probabilities to get full omega estimate
				float w_full = 0.0f;
				for(int i=0;i<probabilities.length;i++){
					probabilities[i] = Float.parseFloat(tokens[(i+3)]);
			//		w_full += (probabilities[i] * global_dNdS[i]);
				}
				// DON'T iterate through all prob products - we only need the 0, 1 and 2b cats, not the 2a (background)
				w_full += (probabilities[0] * global_dNdS[0]);
				w_full += (probabilities[1] * global_dNdS[1]);
				w_full += (probabilities[3] * global_dNdS[3]);
				estimated_proportions.add(probabilities);
				estimated_omegas.add(w_full);
			}
			if(isNEB.find()){inNEB=true; inBEB=false;}	// in naive empirical Bayes block
			if(isBEB.find()){inNEB=false;inBEB=true;}	// in Bayes empirical Bayes (BEB) block
			if(isPOS.find()){inNEB=false;inBEB=false;}	// in positive sites summary; neither NEB nor Bayes empirical Bayes (BEB) block
			if(isPro.find()){
				String[] tokens = line.split("\\ {1,}");
				global_proportions[0] = Float.parseFloat(tokens[1]);
				global_proportions[1] = Float.parseFloat(tokens[2]);
				global_proportions[2] = Float.parseFloat(tokens[3]);
				global_proportions[3] = Float.parseFloat(tokens[4]);
				model.setGlobalProportions(global_proportions);
			}
			if(isRat.find()){
				String[] tokens = line.split("\\ {1,}");
				global_dNdS[0] = Float.parseFloat(tokens[2]);
				global_dNdS[1] = Float.parseFloat(tokens[3]);
				global_dNdS[2] = Float.parseFloat(tokens[4]);
				global_dNdS[3] = Float.parseFloat(tokens[5]);
				model.setGlobalOmegaRates(global_dNdS);
			}
			if(isLnL.find()){
				String[] tokens = line.split("\\ {1,}");
				lnL = Float.parseFloat(tokens[2]);
				model.setLnL(lnL);
			}
		}
		
		float[][] estimated_proportions_matrix = new float[estimated_proportions.size()][4];
		float[] omegasArray = new float[estimated_omegas.size()];
		for(int i=0;i<omegasArray.length;i++){
			omegasArray[i] = estimated_omegas.get(i);
			estimated_proportions_matrix[i] = estimated_proportions.get(i);
		}
		model.setEstimatedOmegas(omegasArray);
		model.setEstimatedProportions(estimated_proportions_matrix);
		model.setNumberOfRates(4);
		model.setCodemlModelType(this.modelType);
		model.setCodemlModelNSsitesType(this.NSsitesType);
		
		/*
		 * check integrity of CodemlModel before setting parseSuccessful to TRUE
		 * 
		 * e.g., are relevant values not NaN (or at least not null) etc
		 */		
		if(model.selfValidate()){this.parseSuccessful=true;}
	}

	/* (non-Javadoc)
	 * @see uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParser#toString()
	 */
	public String toString() {
		return this.modelType+"_"+NSsitesType+"_"+this.parseSuccessful;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParser#guessWhichModel()
	 */
	@Override
	public String guessWhichModel() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.CodemlParser#getModelData()
	 */
	public CodemlModel getModelData() {
		if(this.parseSuccessful){
			return this.model;
		}else{
			return null;
		}
	}

}
