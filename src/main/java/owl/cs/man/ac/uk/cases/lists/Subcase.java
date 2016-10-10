package main.java.owl.cs.man.ac.uk.cases.lists;

import org.semanticweb.owlapi.model.OWLAxiom;

public class Subcase extends Case{
	private String justification;
	
	public Subcase(String ontology, OWLAxiom entailment, String justification) {
	super(ontology,entailment);
	this.justification = justification;
	}
	
	public void setJustification(String justification){
		this.justification = justification;
	}
	
	public String getJustification(){
		return justification;
	}
}
