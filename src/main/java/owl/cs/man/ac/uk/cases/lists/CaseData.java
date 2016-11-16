package main.java.owl.cs.man.ac.uk.cases.lists;

import java.util.Set;

public class CaseData {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + ((evidence == null) ? 0 : evidence.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((subcaseSet == null) ? 0 : subcaseSet.hashCode());
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
		CaseData other = (CaseData) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
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
		if (subcaseSet == null) {
			if (other.subcaseSet != null)
				return false;
		} else if (!subcaseSet.equals(other.subcaseSet))
			return false;
		if (verdicts == null) {
			if (other.verdicts != null)
				return false;
		} else if (!verdicts.equals(other.verdicts))
			return false;
		return true;
	}

	private Case c;
	private Set<Subcase> subcaseSet;
	private MetaData metadata;
	private Evidence evidence;
	private Set<ReasonerVerdict> verdicts;
	public CaseData(Case c, Set<Subcase> subcaseSet, MetaData metadata, Evidence evidence, Set<ReasonerVerdict> verdicts) {
		this.c = c;
		this.subcaseSet = subcaseSet;
		this.metadata = metadata;
		this.evidence = evidence;
		this.verdicts = verdicts;
	}
	
	public Set<Subcase> getSubcases(){
		return this.subcaseSet;
	}
	
	public MetaData getMetaData(){
		return this.metadata;
	}
	
	public Evidence getEvidence(){
		return this.evidence;
	}
	
	public Case getCase(){
		return this.c;
	}
	
	public Set<ReasonerVerdict> getReasonerVerdicts(){
		return this.verdicts;
	}
}
