package uk.ac.qmul.sbcs.evolution.convergence.runners;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlModel;
import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.parsers.codeml.*;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileReader;
import uk.ac.qmul.sbcs.evolution.convergence.util.RstFileFilter;
import uk.ac.qmul.sbcs.evolution.convergence.util.stats.LinearRegression;

/**
 * Parse output from codeml 'rst' files, including where multiple site models have been run concurrently (e.g. "NSsites = 0 1 2" etc).
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @see CodemlModel
 * @see BasicFileReader
 * @see File
 */
public class CodemlRstParser {

	private File rstFile;
	private ArrayList<String> rawData;
	private LinkedHashMap<File,ArrayList<CodemlModel>> models;
	public boolean printIntervals = false;	// print the intervals of each rst?
	
	public CodemlRstParser(String string) {
		this.rstFile = new File(string);
		this.models = new LinkedHashMap<File,ArrayList<CodemlModel>>();
	}

	/**
	 * Parses output from the codeml 'rst' files, using a uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.CodemlModel as the data representation
	 * @param args
	 */
	public static void main(String[] args) {
		CodemlRstParser rst = new CodemlRstParser(args[0]);
		if(args.length>1){
			rst.printIntervals = Boolean.parseBoolean(args[1]); // print the intervals of each rst?
		}
		if(rst.rstFile.isDirectory()){
			// print headers (new)
			System.out.print("file");
			System.out.print("\tlnL_M1");
			System.out.print("\tlnL_M2");
			System.out.print("\tdeltaLnL");
			System.out.print("\tN");
			System.out.print("\t#sites");
			System.out.print("\tratio");
			System.out.print("\tRsq");
			System.out.print("\tslope");
			System.out.print("\tintercept");
			System.out.print("\tmedianInterval");
			System.out.println();

			//get the files themselves
			String[] rstFiles = rst.rstFile.list(new RstFileFilter());
			for(String oneFile:rstFiles){
				File singleRstFile = new File(rst.rstFile.getAbsolutePath()+"/"+oneFile);
				if(singleRstFile.exists()){
//					rst.parseRstFile(singleRstFile); // initial approach, too memory heavy as all CodemlModels lists in heap
					// New approach after GT/user feedback; print then clear heap for each file
					rst.parseAndPrintRstFile(singleRstFile);
				}
			}
		}else{
			// print headers (new)
			System.out.print("file");
			System.out.print("\tlnL_M1");
			System.out.print("\tlnL_M2");
			System.out.print("\tdeltaLnL");
			System.out.print("\tN");
			System.out.print("\t#sites");
			System.out.print("\tratio");
			System.out.print("\tRsq");
			System.out.print("\tslope");
			System.out.print("\tintercept");
			System.out.print("\tmedianInterval");
			System.out.println();

			//rst.parseRstFile(rst.rstFile);//old approach
			rst.parseAndPrintRstFile(rst.rstFile);
		}
//		rst.printSummaryByFileM1M2();//old approach
	}

	/**
	 * Parse the output.
	 * As each new model is encountered, create a CodemlModel for it.
	 */
	private void parseRstFile(File singleRstFile) {
		ArrayList<CodemlModel> modelsList = new ArrayList<CodemlModel>();
		ArrayList<String> rawData;			// raw data from rst file
		ArrayList<String> bufData;			// buffer to hold data from each model within a file (to account for multiple models concurrently written to same rst file
		CodemlModelNSsitesTypes NSsites = null;		// enum defining which NSsites (0 1a 2a 3 4 5 6 7 8) used for dN/dS modelling
		CodemlModelType modelType = null;			// enum defining which model type (0=sitewise; 1=free-rates; 2=branch-site; 3=clade-selection (C & D)
		int lastRatesCount = -1;
		int guessedLastModelNum = -1;
		rawData = new BasicFileReader().loadSequences(singleRstFile,false, false);
		bufData = new ArrayList<String>();
		Pattern p_Model  = Pattern.compile("Model"); 
		Pattern p_rates  = Pattern.compile("w:");
		Pattern p_branch = Pattern.compile("branch type");
		Pattern p_nums   = Pattern.compile("[0-9\\.]+");
		for(String line:rawData){
			if(p_Model.matcher(line).find()){
				// We're in a "Model: " line, probably a sitewise model then.. try and guess which NSsites (at least dump existing buffer)
				modelType = CodemlModelType.MODEL_TYPE_0_DEFAULT;

				// is lastRatesCount available? guess NSsites
				if(lastRatesCount>0){
					// the rates count ('w: ' line) is > -1
					// we can have a good guess at the NSsites from this which might be safer than Model line
					switch(lastRatesCount){
					case 2: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL; break; // only one possibility, M1a
					case 3: {
						// could be M2a or M3
						if(guessedLastModelNum == 2){
							NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION; break;
						}else{
							NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_3_DISCRETE; break;
						}
					}
					case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_4_FREQS; break;
					case 10: {
						// could be M5, M6 or M7. Most likely to be M7 so we'll switch and default to M7
						switch(guessedLastModelNum){
						case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_5_GAMMA; break;
						case 6: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_6_2GAMMA; break;
						case 7: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
						default: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
						}
					}
					case 11: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_8_BETAW; break;
					default: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W; break;
					}
				}else{
					// either they don't agree(?) or last rates count ==1 in which case both are -1
					switch(guessedLastModelNum){
					case 0: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W; break;		// TODO FIXME Big problems ahead if this value..
					case 1: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL; break;
					case 2: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION; break;
					case 3: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_3_DISCRETE; break;
					case 4: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_4_FREQS; break;
					case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_5_GAMMA; break;
					case 6: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_6_2GAMMA; break;
					case 7: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
					case 8: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_8_BETAW; break;
					default: {
						// Fallthrough, both lastRatesCount and guessedModelNum should == -1. 
						// I have literally no idea what would make this happen.
						// TODO check this
						assert(false); //throw an assertion for now..
						NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W;
						break;
					}
					}
				}

				// having worked out NSsites and modelType for the last model (hopefully), create CodemlModel

				if((NSsites == CodemlModelNSsitesTypes.NSsites_TYPE_0_W)||(NSsites == null)){
					// not safe to add this model..
					modelsList.add(null);
					// re-init model guessing numbers
					NSsites			= null;
					modelType		= null;
					bufData.clear();
					lastRatesCount 	= -1;
				}else{
					modelsList.add(this.createCodemlModel(bufData,NSsites,modelType));
					// re-init model guessing numbers
					NSsites			= null;
					modelType		= null;
					bufData.clear();
					lastRatesCount 	= -1;
				}

				// now try and guess the new ones
				guessedLastModelNum = -1;
				Matcher matchNums = p_nums.matcher(line);
				if(matchNums.find()){
					guessedLastModelNum = Integer.parseInt(matchNums.group());
				}
			}else{
				if(p_rates.matcher(line).find()){
					// We're in a rates line, try and guess how many rates this way. probably model=0 and sitewise
					lastRatesCount = 0;
					Matcher numbers = p_nums.matcher(line);
					while(numbers.find()){lastRatesCount++;}
				}else if(p_branch.matcher(line).find()){
					// We're ALSO in a rates line but probably Model C or D
					modelType = CodemlModelType.MODEL_TYPE_3_CLADE;
					lastRatesCount = 0;
					Matcher numbers = p_nums.matcher(line);
					while(numbers.find()){lastRatesCount++;}
				}
			}
			bufData.add(line);
		}

		// add the last model in the list

		// is lastRatesCount available? guess NSsites
		if(lastRatesCount>0){
			// the rates count ('w: ' line) is > -1
			// we can have a good guess at the NSsites from this which might be safer than Model line
			switch(lastRatesCount){
			case 2: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL; break; // only one possibility, M1a
			case 3: {
				// could be M2a or M3
				if(guessedLastModelNum == 2){
					NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION; break;
				}else{
					NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_3_DISCRETE; break;
				}
			}
			case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_4_FREQS; break;
			case 10: {
				// could be M5, M6 or M7. Most likely to be M7 so we'll switch and default to M7
				switch(guessedLastModelNum){
				case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_5_GAMMA; break;
				case 6: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_6_2GAMMA; break;
				case 7: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
				default: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
				}
			}
			case 11: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_8_BETAW; break;
			default: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W; break;
			}
		}else{
			// either they don't agree(?) or last rates count ==1 in which case both are -1
			switch(guessedLastModelNum){
			case 0: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W; break;		// TODO FIXME Big problems ahead if this value..
			case 1: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL; break;
			case 2: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION; break;
			case 3: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_3_DISCRETE; break;
			case 4: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_4_FREQS; break;
			case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_5_GAMMA; break;
			case 6: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_6_2GAMMA; break;
			case 7: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
			case 8: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_8_BETAW; break;
			default: {
				// Fallthrough, both lastRatesCount and guessedModelNum should == -1. 
				// I have literally no idea what would make this happen.
				// TODO check this
				assert(false); //throw an assertion for now..
				NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W;
				break;
			}
			}
		}

		// having worked out NSsites and modelType for the last model (hopefully), create CodemlModel

		if((NSsites == CodemlModelNSsitesTypes.NSsites_TYPE_0_W)||(NSsites == null)){
			// not safe to add this model..
			modelsList.add(null);
		}else{
			if(modelType != CodemlModelType.MODEL_TYPE_3_CLADE){modelType = CodemlModelType.MODEL_TYPE_0_DEFAULT;} //TODO FIXME for now, guess that if the last model isn't Clade, it's sitewise
			modelsList.add(this.createCodemlModel(bufData,NSsites,modelType));
			// re-init model guessing numbers
			NSsites			= null;
			modelType		= null;
			bufData.clear();
			lastRatesCount 	= -1;
		}

		// should have finished parsing all the models now
		models.put(singleRstFile.getAbsoluteFile(),modelsList);
	}

	private CodemlModel createCodemlModel(ArrayList<String> bufData, CodemlModelNSsitesTypes nSsites, CodemlModelType modelType) {
		CodemlModel newModel = null;
		switch(modelType){
		case MODEL_TYPE_0_DEFAULT:{
			switch(nSsites){
			case NSsites_TYPE_1_NEUTRAL: 		{ newModel = new CodemlParserM1(bufData).getModelData(); break;}
			case NSsites_TYPE_2_SELECTION: 		{ newModel = new CodemlParserM2(bufData).getModelData(); break;}
			case NSsites_TYPE_3_DISCRETE:		{ break;}	//TODO not implemented yet
			case NSsites_TYPE_4_FREQS:			{ break;}	//TODO not implemented yet
			case NSsites_TYPE_5_GAMMA:			{ break;}	//TODO not implemented yet
			case NSsites_TYPE_6_2GAMMA:			{ break;}	//TODO not implemented yet
			case NSsites_TYPE_7_BETA:			{ break;}	//TODO not implemented yet
			case NSsites_TYPE_8_BETAW:			{ break;}	//TODO not implemented yet
			default:							{ break;}	//TODO not implemented yet
			}
		}
		case MODEL_TYPE_1_FREERATIOS:{ break;}	//TODO not implemented yet
		case MODEL_TYPE_2_BRANCHSITE:{ break;}	//TODO not implemented yet
		case MODEL_TYPE_3_CLADE:{
			switch(nSsites){
			case NSsites_TYPE_1_NEUTRAL: 		{ break;}	//TODO not implemented yet
			case NSsites_TYPE_2_SELECTION: 		{ break;}	//TODO not implemented yet
			default:							{ break;}	//TODO not implemented yet
			}
			
		}
		default:{ break;}	//TODO not implemented yet
		}
		return newModel;
	}

	/** 
	 * do a quick comparison of M1 and M2, and print results on a single line
	 */
	private void printSummaryByFileM1M2(){
		// print headers
		System.out.print("file");
		System.out.print("\tlnL_M1");
		System.out.print("\tlnL_M2");
		System.out.print("\tdeltaLnL");
		System.out.print("\tN");
		System.out.print("\tRsq");
		System.out.print("\tslope");
		System.out.print("\tintercept");
		System.out.println();
		
		// iterate through files
		java.util.Iterator<File> itr = this.models.keySet().iterator();
		while(itr.hasNext()){
			File thisFile = itr.next();
			ArrayList<CodemlModel> codemlModels = models.get(thisFile);
			Float lnL_M1 = Float.NaN;
			Float lnL_M2 = Float.NaN;
			CodemlModel M2 = null;
			for(CodemlModel eachModel:codemlModels){
				if(eachModel != null){
					if((eachModel.getModelType() == CodemlModelType.MODEL_TYPE_0_DEFAULT)&&(eachModel.getNSsitesType() == CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL)){
						lnL_M1 = eachModel.getLnL();
					}
					if((eachModel.getModelType() == CodemlModelType.MODEL_TYPE_0_DEFAULT)&&(eachModel.getNSsitesType() == CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION)){
						M2 = eachModel;
					}
				}
			}
			if(M2 != null){
				Float deltaLnL 	= Float.NaN;
				Integer N 		= null;
				Double Rsq 		= Double.NaN;
				Double slope 	= Double.NaN;
				Double intercept = Double.NaN;
				
				// lnL comparisons
				lnL_M2 = M2.getLnL();
				if((!lnL_M1.isNaN())&&(!lnL_M2.isNaN())){
					deltaLnL = 2 * (lnL_M2 - lnL_M1);
				}
				
				// do the regression - NB it will fail if not enough intervals (must be > 2 intervals, so > 3 sites selected)
				try {
					LinearRegression loglinear = M2.getIntervalsRegression();
					N 			= loglinear.getN();
					Rsq 		= loglinear.getRsq();
					slope 		= loglinear.getBeta1();
					intercept 	= loglinear.getBeta0();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.print(thisFile.getAbsolutePath());
				System.out.print("\t"+lnL_M1);
				System.out.print("\t"+lnL_M2);
				System.out.print("\t"+deltaLnL);
				System.out.print("\t"+N);
				System.out.print("\t"+Rsq);
				System.out.print("\t"+slope);
				System.out.print("\t"+intercept);
				System.out.println();
			}
		}
	}

	/**
	 * Parse the output.
	 * As each new model is encountered, create a CodemlModel for it.
	 */
	private void parseAndPrintRstFile(File singleRstFile) {
		ArrayList<CodemlModel> modelsList = new ArrayList<CodemlModel>();
		ArrayList<String> rawData;			// raw data from rst file
		ArrayList<String> bufData;			// buffer to hold data from each model within a file (to account for multiple models concurrently written to same rst file
		CodemlModelNSsitesTypes NSsites = null;		// enum defining which NSsites (0 1a 2a 3 4 5 6 7 8) used for dN/dS modelling
		CodemlModelType modelType = null;			// enum defining which model type (0=sitewise; 1=free-rates; 2=branch-site; 3=clade-selection (C & D)
		int lastRatesCount = -1;
		int guessedLastModelNum = -1;
		rawData = new BasicFileReader().loadSequences(singleRstFile,false, false);
		bufData = new ArrayList<String>();
		Pattern p_Model  = Pattern.compile("Model"); 
		Pattern p_rates  = Pattern.compile("w:");
		Pattern p_branch = Pattern.compile("branch type");
		Pattern p_nums   = Pattern.compile("[0-9\\.]+");
		for(String line:rawData){
			if(p_Model.matcher(line).find()){
				// We're in a "Model: " line, probably a sitewise model then.. try and guess which NSsites (at least dump existing buffer)
				modelType = CodemlModelType.MODEL_TYPE_0_DEFAULT;
	
				// is lastRatesCount available? guess NSsites
				if(lastRatesCount>0){
					// the rates count ('w: ' line) is > -1
					// we can have a good guess at the NSsites from this which might be safer than Model line
					switch(lastRatesCount){
					case 2: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL; break; // only one possibility, M1a
					case 3: {
						// could be M2a or M3
						if(guessedLastModelNum == 2){
							NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION; break;
						}else{
							NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_3_DISCRETE; break;
						}
					}
					case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_4_FREQS; break;
					case 10: {
						// could be M5, M6 or M7. Most likely to be M7 so we'll switch and default to M7
						switch(guessedLastModelNum){
						case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_5_GAMMA; break;
						case 6: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_6_2GAMMA; break;
						case 7: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
						default: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
						}
					}
					case 11: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_8_BETAW; break;
					default: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W; break;
					}
				}else{
					// either they don't agree(?) or last rates count ==1 in which case both are -1
					switch(guessedLastModelNum){
					case 0: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W; break;		// TODO FIXME Big problems ahead if this value..
					case 1: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL; break;
					case 2: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION; break;
					case 3: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_3_DISCRETE; break;
					case 4: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_4_FREQS; break;
					case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_5_GAMMA; break;
					case 6: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_6_2GAMMA; break;
					case 7: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
					case 8: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_8_BETAW; break;
					default: {
						// Fallthrough, both lastRatesCount and guessedModelNum should == -1. 
						// I have literally no idea what would make this happen.
						// TODO check this
						assert(false); //throw an assertion for now..
						NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W;
						break;
					}
					}
				}
	
				// having worked out NSsites and modelType for the last model (hopefully), create CodemlModel
	
				if((NSsites == CodemlModelNSsitesTypes.NSsites_TYPE_0_W)||(NSsites == null)){
					// not safe to add this model..
					modelsList.add(null);
					// re-init model guessing numbers
					NSsites			= null;
					modelType		= null;
					bufData.clear();
					lastRatesCount 	= -1;
				}else{
					modelsList.add(this.createCodemlModel(bufData,NSsites,modelType));
					// re-init model guessing numbers
					NSsites			= null;
					modelType		= null;
					bufData.clear();
					lastRatesCount 	= -1;
				}
	
				// now try and guess the new ones
				guessedLastModelNum = -1;
				Matcher matchNums = p_nums.matcher(line);
				if(matchNums.find()){
					guessedLastModelNum = Integer.parseInt(matchNums.group());
				}
			}else{
				if(p_rates.matcher(line).find()){
					// We're in a rates line, try and guess how many rates this way. probably model=0 and sitewise
					lastRatesCount = 0;
					Matcher numbers = p_nums.matcher(line);
					while(numbers.find()){lastRatesCount++;}
				}else if(p_branch.matcher(line).find()){
					// We're ALSO in a rates line but probably Model C or D
					modelType = CodemlModelType.MODEL_TYPE_3_CLADE;
					lastRatesCount = 0;
					Matcher numbers = p_nums.matcher(line);
					while(numbers.find()){lastRatesCount++;}
				}
			}
			bufData.add(line);
		}
	
		// add the last model in the list
	
		// is lastRatesCount available? guess NSsites
		if(lastRatesCount>0){
			// the rates count ('w: ' line) is > -1
			// we can have a good guess at the NSsites from this which might be safer than Model line
			switch(lastRatesCount){
			case 2: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL; break; // only one possibility, M1a
			case 3: {
				// could be M2a or M3
				if(guessedLastModelNum == 2){
					NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION; break;
				}else{
					NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_3_DISCRETE; break;
				}
			}
			case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_4_FREQS; break;
			case 10: {
				// could be M5, M6 or M7. Most likely to be M7 so we'll switch and default to M7
				switch(guessedLastModelNum){
				case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_5_GAMMA; break;
				case 6: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_6_2GAMMA; break;
				case 7: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
				default: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
				}
			}
			case 11: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_8_BETAW; break;
			default: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W; break;
			}
		}else{
			// either they don't agree(?) or last rates count ==1 in which case both are -1
			switch(guessedLastModelNum){
			case 0: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W; break;		// TODO FIXME Big problems ahead if this value..
			case 1: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL; break;
			case 2: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION; break;
			case 3: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_3_DISCRETE; break;
			case 4: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_4_FREQS; break;
			case 5: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_5_GAMMA; break;
			case 6: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_6_2GAMMA; break;
			case 7: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_7_BETA; break;
			case 8: NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_8_BETAW; break;
			default: {
				// Fallthrough, both lastRatesCount and guessedModelNum should == -1. 
				// I have literally no idea what would make this happen.
				// TODO check this
				assert(false); //throw an assertion for now..
				NSsites = CodemlModelNSsitesTypes.NSsites_TYPE_0_W;
				break;
			}
			}
		}
	
		// having worked out NSsites and modelType for the last model (hopefully), create CodemlModel
	
		if((NSsites == CodemlModelNSsitesTypes.NSsites_TYPE_0_W)||(NSsites == null)){
			// not safe to add this model..
			modelsList.add(null);
		}else{
			if(modelType != CodemlModelType.MODEL_TYPE_3_CLADE){modelType = CodemlModelType.MODEL_TYPE_0_DEFAULT;} //TODO FIXME for now, guess that if the last model isn't Clade, it's sitewise
			modelsList.add(this.createCodemlModel(bufData,NSsites,modelType));
			// re-init model guessing numbers
			NSsites			= null;
			modelType		= null;
			bufData.clear();
			lastRatesCount 	= -1;
		}
	
		// should have finished parsing all the models now, print summaries
		this.printSummaryForSingleModelM1M2(singleRstFile, modelsList);
	}
	
	/**
	 * Print summary for a single models list based on M1M2 comparison
	 * @param thisFile
	 * @param codemlModels
	 */
	protected void printSummaryForSingleModelM1M2(File thisFile, ArrayList<CodemlModel> codemlModels){
		Float lnL_M1 = Float.NaN;
		Float lnL_M2 = Float.NaN;
		CodemlModel M2 = null;
		for(CodemlModel eachModel:codemlModels){
			if(eachModel != null){
				if((eachModel.getModelType() == CodemlModelType.MODEL_TYPE_0_DEFAULT)&&(eachModel.getNSsitesType() == CodemlModelNSsitesTypes.NSsites_TYPE_1_NEUTRAL)){
					lnL_M1 = eachModel.getLnL();
				}
				if((eachModel.getModelType() == CodemlModelType.MODEL_TYPE_0_DEFAULT)&&(eachModel.getNSsitesType() == CodemlModelNSsitesTypes.NSsites_TYPE_2_SELECTION)){
					M2 = eachModel;
				}
			}
		}
		if(M2 != null){
			Float deltaLnL 	= Float.NaN;
			Integer N 		= null;
			Double Rsq 		= Double.NaN;
			Double slope 	= Double.NaN;
			Double intercept = Double.NaN;
			
			// lnL comparisons
			lnL_M2 = M2.getLnL();
			if((!lnL_M1.isNaN())&&(!lnL_M2.isNaN())){
				deltaLnL = 2 * (lnL_M2 - lnL_M1);
			}
			
			// do the regression - NB it will fail if not enough intervals (must be > 2 intervals, so > 3 sites selected)
			try {
				LinearRegression loglinear = M2.getIntervalsRegression();
				N 			= loglinear.getN();
				Rsq 		= loglinear.getRsq();
				slope 		= loglinear.getBeta1();
				intercept 	= loglinear.getBeta0();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print(thisFile.getAbsolutePath());
			System.out.print("\t"+lnL_M1);
			System.out.print("\t"+lnL_M2);
			System.out.print("\t"+deltaLnL);
			System.out.print("\t"+N);
			System.out.print("\t"+M2.getEstimatedOmegas().length);
			try {
				System.out.print("\t"+(((float)N)/((float)M2.getEstimatedOmegas().length)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("\t"+Rsq);
			System.out.print("\t"+slope);
			System.out.print("\t"+intercept);
			System.out.print("\t"+getMedian(M2.getSelectionIntervals()));
			if(this.printIntervals){System.out.print("\tc("+concatenateIntervals(M2)+")");}
			System.out.println();
		}

	}
	
	/**
	 * Get the INTEGER median interval size. Does not sort; assumes already sorted values.
	 * NB uses floor (lowest) integer where intervals.length is even.
	 * @param intervals - int[] of intervals between dN/dS data
	 * @return median - integer median of intervals
	 */
	private int getMedian(int[] intervals){
		int retInt = -1;
		if(intervals.length > 2){
			float medianIndex = intervals.length / 2;
			float remainder = (float) (medianIndex - Math.floor(medianIndex));
			
			if(remainder > 0){
				medianIndex = (intervals.length -1)/2;
			}else{
				medianIndex = intervals.length/2;
			}
			int roundedIndex = (int) medianIndex;
			retInt = intervals[roundedIndex];
			
		}
		return retInt;
	}
	
	private String concatenateIntervals(CodemlModel someModel){
		String ret = "";
		int[] intervals = someModel.getSelectionIntervals();
		if(intervals.length > 2){
			for(int interval:intervals){
				ret = ret + "," + interval;
			}
			return ret.substring(1);
		}else{
			return ret;
		}
		
	}
}
