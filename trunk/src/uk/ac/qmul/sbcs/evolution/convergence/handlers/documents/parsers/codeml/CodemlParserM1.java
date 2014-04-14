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
public class CodemlParserM1 extends CodemlParser {

	/**
	 * @param data
	 */
	public CodemlParserM1(ArrayList<String> data) {
		super(data);
		this.modelType = CodemlModelType.MODEL_TYPE_0_DEFAULT;
		this.NSsitesType = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL;
		this.model = new CodemlModel();
		model.setModelString(this.toString());
		model.setRawData((String[]) data.toArray(new String[data.size()]));

		// prepare to parse
		boolean inNEB = false;
		float[] dNdS = new float[2]; // global omegas
		float[] proportions = new float[2]; // global omegas proportions (estimates)
		float lnL;
		ArrayList<Float> omegas = new ArrayList<Float>();
		Pattern p_NEB = Pattern.compile("NEB"); 
		Pattern p_num = Pattern.compile("^[\\ ]{1,}[0-9]{1,}");
		Pattern rates = Pattern.compile("w:");
		Pattern props = Pattern.compile("p:");
		Pattern p_lnL = Pattern.compile("lnL");
		//Pattern positive = Pattern.compile("11\\)");

		// actually parse the data
		for(String line:data){
			Matcher isNEB = p_NEB.matcher(line);
			Matcher isNum = p_num.matcher(line);
			Matcher isRat = rates.matcher(line);
			Matcher isPro = props.matcher(line);
			Matcher isLnL = p_lnL.matcher(line);
			//Matcher isPos = positive.matcher(line);
			
			if(isNum.find() && inNEB && line.length()>2){
				// We are in a data line, get probabilities
				String[] tokens = line.split("\\ {1,}");
				float[] probabilities = new float[2];
				
				//  iterate over all omega cat BEB probabilities to get full omega estimate
				float w_full = 0.0f;
				for(int i=0;i<probabilities.length;i++){
					probabilities[i] = Float.parseFloat(tokens[(i+3)]);
					w_full += (probabilities[i] * dNdS[i]);
				}
				omegas.add(w_full);
			}
			if(isNEB.find()){inNEB=true;}
			if(isPro.find()){
				String[] tokens = line.split("\\ {1,}");
				proportions[0] = Float.parseFloat(tokens[1]);
				proportions[1] = Float.parseFloat(tokens[2]);
				model.setGlobalProportions(proportions);
			}
			if(isRat.find()){
				String[] tokens = line.split("\\ {1,}");
				dNdS[0] = Float.parseFloat(tokens[1]);
				dNdS[1] = Float.parseFloat(tokens[2]);
				model.setGlobalOmegaRates(dNdS);
			}
			if(isLnL.find()){
				String[] tokens = line.split("\\ {1,}");
				lnL = Float.parseFloat(tokens[2]);
				model.setLnL(lnL);
			}
		}
		
		float[] omegasArray = new float[omegas.size()];
		for(int i=0;i<omegasArray.length;i++){
			omegasArray[i] = omegas.get(i);
		}
		model.setEstimatedOmegas(omegasArray);
		model.setNumberOfRates(2);
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
