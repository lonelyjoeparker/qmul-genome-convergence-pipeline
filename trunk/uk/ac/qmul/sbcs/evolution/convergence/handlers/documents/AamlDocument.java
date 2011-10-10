package uk.ac.qmul.sbcs.evolution.convergence.handlers.documents;

public class AamlDocument extends PamlDocument {
	private String activeTemplate;
	
	public AamlDocument() {
		// TODO Auto-generated constructor stub
		activeTemplate = aamlTemplate;
		// TODO SET UP DEFAULT PARAMETERS...

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
			// FIXME why aren't we writing this activeTemplate correctly?
		}
	}
}
