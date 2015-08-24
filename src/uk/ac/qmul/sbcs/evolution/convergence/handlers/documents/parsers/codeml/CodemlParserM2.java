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
public class CodemlParserM2 extends CodemlParser {

	/**
	 * @param data
	 */
	public CodemlParserM2(ArrayList<String> data) {
		super(data);
		// set model type
		this.modelType = CodemlModelType.MODEL_TYPE_0_DEFAULT;
		this.NSsitesType = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION;
		this.model = new CodemlModel();
		model.setModelString(this.toString());
		model.setRawData((String[]) data.toArray(new String[data.size()]));

		// prepare to parse
		boolean inNEB = false;
		boolean inBEB = false;
		float[] global_dNdS = new float[3]; 		// global omegas (3 for M2)
		float[] global_proportions = new float[3]; // global omegas proportions (estimates; 3 for M2)
		float lnL;
		ArrayList<Float> global_omegas = new ArrayList<Float>();
		ArrayList<float[]> estimated_proportions = new ArrayList<float[]>();
		Pattern p_NEB = Pattern.compile("NEB"); 
		Pattern p_BEB = Pattern.compile("BEB"); 
		Pattern p_POS = Pattern.compile("Positively"); 
		Pattern p_num = Pattern.compile("^[\\ ]{1,}[0-9]{1,}");
		Pattern rates = Pattern.compile("w:");
		Pattern props = Pattern.compile("p:");
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
				float[] probabilities = new float[3];
				
				//  iterate over all omega cat BEB probabilities to get full omega estimate
				float w_full = 0.0f;
				for(int i=0;i<probabilities.length;i++){
					probabilities[i] = Float.parseFloat(tokens[(i+3)]);
					w_full += (probabilities[i] * global_dNdS[i]);
				}
				estimated_proportions.add(probabilities);
				global_omegas.add(w_full);
			}
			if(isNEB.find()){inNEB=true; inBEB=false;}	// in naive empirical Bayes block
			if(isBEB.find()){inNEB=false;inBEB=true;}	// in Bayes empirical Bayes (BEB) block
			if(isPOS.find()){inNEB=false;inBEB=false;}	// in positive sites summary; neither NEB nor Bayes empirical Bayes (BEB) block
			if(isPro.find()){
				String[] tokens = line.split("\\ {1,}");
				global_proportions[0] = Float.parseFloat(tokens[1]);
				global_proportions[1] = Float.parseFloat(tokens[2]);
				global_proportions[2] = Float.parseFloat(tokens[3]);
				model.setGlobalProportions(global_proportions);
			}
			if(isRat.find()){
				String[] tokens = line.split("\\ {1,}");
				global_dNdS[0] = Float.parseFloat(tokens[1]);
				global_dNdS[1] = Float.parseFloat(tokens[2]);
				global_dNdS[2] = Float.parseFloat(tokens[3]);
				model.setGlobalOmegaRates(global_dNdS);
			}
			if(isLnL.find()){
				String[] tokens = line.split("\\ {1,}");
				lnL = Float.parseFloat(tokens[2]);
				model.setLnL(lnL);
			}
		}
		
		float[][] estimated_proportions_matrix = new float[estimated_proportions.size()][3];
		float[] omegasArray = new float[global_omegas.size()];
		for(int i=0;i<omegasArray.length;i++){
			omegasArray[i] = global_omegas.get(i);
			estimated_proportions_matrix[i] = estimated_proportions.get(i);
		}
		model.setEstimatedOmegas(omegasArray);
		model.setEstimatedProportions(estimated_proportions_matrix);
		model.setNumberOfRates(3);
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
