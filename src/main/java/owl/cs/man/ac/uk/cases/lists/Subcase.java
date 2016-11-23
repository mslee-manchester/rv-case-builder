package main.java.owl.cs.man.ac.uk.cases.lists;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

public class Subcase{
	private String ontology;
	private OWLAxiom entailment;
	private String justification;
	private String reasoner;
	private MetaData metadata;
	private Evidence evidence;
	private Set<ReasonerVerdict> verdicts;
	public Subcase(String ontology, OWLAxiom entailment, String justification, String reasoner,  MetaData metadata, Evidence evidence, Set<ReasonerVerdict> verdicts) {
		this.ontology = ontology;
		this.entailment = entailment;
		this.justification = justification;
		this.reasoner = reasoner;
		this.metadata = metadata;
		this.evidence = evidence;
		this.verdicts = verdicts;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entailment == null) ? 0 : entailment.hashCode());
		result = prime * result + ((evidence == null) ? 0 : evidence.hashCode());
		result = prime * result + ((justification == null) ? 0 : justification.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((ontology == null) ? 0 : ontology.hashCode());
		result = prime * result + ((reasoner == null) ? 0 : reasoner.hashCode());
		result = prime * result + ((verdicts == null) ? 0 : verdicts.hashCode());
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
		Subcase other = (Subcase) obj;
		if (entailment == null) {
			if (other.entailment != null)
				return false;
		} else if (!entailment.equals(other.entailment))
			return false;
		if (evidence == null) {
			if (other.evidence != null)
				return false;
		} else if (!evidence.equals(other.evidence))
			return false;
		if (justification == null) {
			if (other.justification != null)
				return false;
		} else if (!justification.equals(other.justification))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		if (ontology == null) {
			if (other.ontology != null)
				return false;
		} else if (!ontology.equals(other.ontology))
			return false;
		if (reasoner == null) {
			if (other.reasoner != null)
				return false;
		} else if (!reasoner.equals(other.reasoner))
			return false;
		if (verdicts == null) {
			if (other.verdicts != null)
				return false;
		} else if (!verdicts.equals(other.verdicts))
			return false;
		return true;
	}

	public MetaData getMetaData(){
		return this.metadata;
	}
	
	public Evidence getEvidence(){
		return this.evidence;
	}
	
	public String getOntology(){
		return this.ontology;
	}
	
	public OWLAxiom getEntailment(){
		return this.entailment;
	}
	
	public String getJustification(){
		return this.justification;
	}
	
	public Set<ReasonerVerdict> getReasonerVerdicts(){
		return this.verdicts;
	}
	
	public String getGeneratingReasoner(){
		return this.reasoner;
	}
}