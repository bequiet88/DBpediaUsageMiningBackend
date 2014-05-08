package de.unimannheim.dws.model;

/**
 * one simple RDF triple with a subject, a predicate and an object
 */

public class ExchangeRDFTriple {

	private String sub;
	private String pred;
	private String obj;
	private String group;


	public ExchangeRDFTriple(String sub, String pred,String obj, String group)  {
		this.sub = sub;
		this.pred = pred;
		this.obj = obj;
		this.group = group;
	}

	public String getSub() {
		return sub;
	}

	public String getPred() {
		return pred;
	}


	public String getObj() {
		return obj;
	}

	public String getGroup() {
		return group;
	}
}