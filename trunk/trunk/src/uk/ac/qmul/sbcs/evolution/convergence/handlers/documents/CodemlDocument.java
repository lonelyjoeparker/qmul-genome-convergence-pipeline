package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents;

import java.io.*;

import uk.ac.qmul.sbcs.evolution.convergence.util.CustomFileWriter;

public class CodemlDocument extends PamlDocument {
	private String activeTemplate;

	public CodemlDocument() {
		// TODO Auto-generated constructor stub
		activeTemplate = this.codemlTemplate;
		this.setParameter(CodemlParameters.SEQFILE, "seqfile = stewart.aa");
		this.setParameter("TREEFILE", "treefile = stewart.trees");
		this.setParameter("OUTFILE", "outfile = mlc");
		this.setParameter("NOISY", "noisy = 9");
		this.setParameter("VERBOSE", "verbose = 1");
		this.setParameter("RUNMODE", "runmode = 0");
		this.setParameter("SEQTYPE", "seqtype = 2");
		this.setParameter("CODONFREQ", "CodonFreq = 2");
		this.setParameter("NDATA", "ndata = 10");
		this.setParameter("CLOCK", "clock = 0");
		this.setParameter("AADIST", "aaDist = 0");
		this.setParameter("AARATEFILE", "aaRatefile = dat/jones.dat");	// TODO ideally should check for location of this and throw error if appropriate
		this.setParameter("MODEL", "model = 2");
		this.setParameter("NSSITES", "NSsites = 0");
		this.setParameter("ICODE", "icode = 0");
		this.setParameter("MGENE", "Mgene = 0");
		this.setParameter("FIX_KAPPA", "fix_kappa = 0");
		this.setParameter("KAPPA", "kappa = 2");
		this.setParameter("FIX_OMEGA", "fix_omega = 0");
		this.setParameter("OMEGA", "omega = .4");
		this.setParameter("FIX_ALPHA", "fix_alpha = 1");
		this.setParameter("ALPHA", "alpha = 0.");
		this.setParameter("MALPHA", "Malpha = 0");
		this.setParameter("NCATG", "ncatG = 8");
		this.setParameter("GETSE", "getSE = 0");
		this.setParameter("RATEANCESTOR", "RateAncestor = 1");
		this.setParameter("SMALL_DIFF", "Small_Diff = .5e-6");
		this.setParameter("CLEANDATA", "cleandata = 1");		
		this.setParameter("FIX_BLENGTH", "fix_blength = -1");
		this.setParameter("METHOD", "method = 0");
	}

	/**
	 * @since 11/10/2011
	 * @param param - Enum CodemlParameters
	 * @param value - String value of this parameter in 'param = value' syntax
	 */
	public void setParameter(CodemlParameters param, String value){
		if(!parameterSet.containsKey(param.toString())){
			parameterSet.put(param.toString(), value);
		}else{
			parameterSet.remove(param.toString());
			parameterSet.put(param.toString(), value);
		}
	}

	public void printTemplate(){
		System.out.println(activeTemplate);
	}
	
	public void finalizeParameters(){
		for(String param: parameterSet.keySet()){
			String value = parameterSet.get(param);
			activeTemplate = activeTemplate.replaceFirst("PARAM_"+param, value);
			assert(activeTemplate.hashCode() != aamlTemplate.hashCode());
			System.out.println(param+" set to "+value+"... hopefully?");
			assert(activeTemplate.hashCode() != codemlTemplate.hashCode());
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
	
	public void writeSerialized(File outputFile){
		this.finalizeParameters();
		assert(activeTemplate.length()>0);
		assert(outputFile.canWrite());
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(activeTemplate);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeBuffered(File outputFile){
		this.finalizeParameters();
		assert(activeTemplate.length()>0);
		assert(outputFile.canWrite());
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.append(activeTemplate);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
