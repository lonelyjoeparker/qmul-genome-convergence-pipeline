package uk.ac.qmul.sbcs.evolution.convergence.util;

public class PamlParameter {
	private final String parameter;
	private String value;
	
	public PamlParameter(String parameter, String value) {
		// TODO Auto-generated constructor stub
		this.parameter = parameter;
		this.value = value;
	}

	/**
	 * @return the value of this parameter
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the parameter value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
