package main.java.owl.cs.man.ac.uk.cases.lists;

import org.semanticweb.owlapi.model.OWLAxiom;

public class Case<String,OWLAxiom>{
	private String ontology;
	private OWLAxiom entailment;
	
	public Case(String ontology, OWLAxiom entailment) {
		//A single case is an ontology entailment pair
		this.ontology = ontology;
		this.entailment = entailment;
	}
	public String getOntology(){ return ontology; }
    public OWLAxiom getEntailment(){ return entailment; }
    public void setOntology(String ont){ this.ontology = ont; }
    public void setEntailment(OWLAxiom ax){ this.entailment = entailment; }
}
