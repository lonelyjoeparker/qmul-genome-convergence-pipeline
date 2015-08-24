package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents;

import java.io.File;

import uk.ac.qmul.sbcs.evolution.convergence.handlers.documents.PamlDocument.AamlParameters;
import uk.ac.qmul.sbcs.evolution.convergence.util.*;

public class BasemlDocument extends PamlDocument {
	private String activeTemplate;
	
	public BasemlDocument() {
		// TODO Auto-generated constructor stub
		activeTemplate = this.basemlTemplate;
		this.setParameter("SEQFILE", "seqfile = brown.nuc");
		this.setParameter("TREEFILE", "treefile = brown.trees");
		this.setParameter("OUTFILE", "outfile = mlc");
		this.setParameter("NOISY", "noisy = 2");
		this.setParameter("VERBOSE", "verbose = 0");
		this.setParameter("RUNMODE", "runmode = 0");
		this.setParameter("SEQTYPE", "seqtype = 2");
		this.setParameter("MODEL", "model = 7");
		this.setParameter("MGENE", "Mgene = 0");
		this.setParameter("NDATA", "ndata = 2");
		this.setParameter("CLOCK", "clock = 0");
		this.setParameter("FIX_KAPPA", "fix_kappa = 0");
		this.setParameter("KAPPA", "kappa = 5");
		this.setParameter("FIX_ALPHA", "fix_alpha = 0");
		this.setParameter("ALPHA", "alpha = 0.5");
		this.setParameter("MALPHA", "Malpha = 0");
		this.setParameter("NCATG", "ncatG = 5");
		this.setParameter("NPARK", "nparK = 0");
		this.setParameter("NHOMO", "nhomo = 0");
		this.setParameter("GETSE", "getSE = 0");
		this.setParameter("RATEANCESTOR", "RateAncestor = 0");
		this.setParameter("SMALL_DIFF", "Small_Diff = 7e-6");
		this.setParameter("CLEANDATA", "cleandata = 1");
		this.setParameter("ICODE", "icode = 0");
		this.setParameter("FIX_BLENGTH", "fix_blength = -1");
		this.setParameter("METHOD", "method = 0");
	}

	public void printTemplate(){
		System.out.println(activeTemplate);
	}

	/**
	 * @since 11/10/2011
	 * @param param - Enum BasemlParameters
	 * @param value - String value of this parameter in 'param = value' syntax
	 */
	public void setParameter(BasemlParameters param, String value){
		if(!parameterSet.containsKey(param.toString())){
			parameterSet.put(param.toString(), value);
		}else{
			parameterSet.remove(param.toString());
			parameterSet.put(param.toString(), value);
		}
	}
	
	public void finalizeParameters(){
		for(String param: parameterSet.keySet()){
			String value = parameterSet.get(param);
			activeTemplate = activeTemplate.replaceFirst("PARAM_"+param, value);
			assert(activeTemplate.hashCode() != aamlTemplate.hashCode());
			System.out.println(param+" set to "+value+"... hopefully?");
			assert(activeTemplate.hashCode() != codemlTemplate.hashCode());
			// FIXME why aren't we writing this activeTemplate correctly?
		}
	}

	public void write(String workingdir, String filename){
		finalizeParameters();
		try {
			BasicFileWriter w = new BasicFileWriter(workingdir+filename, activeTemplate);
			this.hasBeenWritten = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(File outputFile){
		finalizeParameters();
		try {
			assert(activeTemplate.length()>0);
			CustomFileWriter w = new CustomFileWriter(outputFile, activeTemplate);
			this.hasBeenWritten = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
