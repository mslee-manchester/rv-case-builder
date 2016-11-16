package main.java.owl.cs.man.ac.uk.cases.lists;

import java.util.Set;

public class SubcaseData {
	private Subcase sc;
	private String reasoner;
	private MetaData metadata;
	private Evidence evidence;
	private Set<ReasonerVerdict> verdicts;
	public SubcaseData(Subcase sc, String reasoner,  MetaData metadata, Evidence evidence, Set<ReasonerVerdict> verdicts) {
		this.sc = sc;
		this.reasoner = reasoner;
		this.metadata = metadata;
		this.evidence = evidence;
		this.verdicts = verdicts;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((evidence == null) ? 0 : evidence.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((reasoner == null) ? 0 : reasoner.hashCode());
		result = prime * result + ((sc == null) ? 0 : sc.hashCode());
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
		SubcaseData other = (SubcaseData) obj;
		if (evidence == null) {
			if (other.evidence != null)
				return false;
		} else if (!evidence.equals(other.evidence))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		if (reasoner == null) {
			if (other.reasoner != null)
				return false;
		} else if (!reasoner.equals(other.reasoner))
			return false;
		if (sc == null) {
			if (other.sc != null)
				return false;
		} else if (!sc.equals(other.sc))
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
	
	public Subcase getCase(){
		return this.sc;
	}
	
	public Set<ReasonerVerdict> getReasonerVerdicts(){
		return this.verdicts;
	}
	
	public String getGeneratingReasoner(){
		return this.reasoner;
	}
}