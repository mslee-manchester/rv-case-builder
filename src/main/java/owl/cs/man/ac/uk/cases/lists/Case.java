package main.java.owl.cs.man.ac.uk.cases.lists;

import org.semanticweb.owlapi.model.OWLAxiom;

public class Case<String,OWLAxiom>{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entailment == null) ? 0 : entailment.hashCode());
		result = prime * result + ((ontology == null) ? 0 : ontology.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Case other = (Case) obj;
		if (entailment == null) {
			if (other.entailment != null)
				return false;
		} else if (!entailment.equals(other.entailment))
			return false;
		if (ontology == null) {
			if (other.ontology != null)
				return false;
		} else if (!ontology.equals(other.ontology))
			return false;
		return true;
	}
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
    public void setEntailment(OWLAxiom ax){ this.entailment = ax; }
}
