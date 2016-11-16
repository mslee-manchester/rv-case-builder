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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((justification == null) ? 0 : justification.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subcase other = (Subcase) obj;
		if (justification == null) {
			if (other.justification != null)
				return false;
		} else if (!justification.equals(other.justification))
			return false;
		return true;
	}
}
