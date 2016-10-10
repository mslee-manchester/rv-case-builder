package main.java.owl.cs.man.ac.uk.cases.lists;

import java.util.Set;

public class CaseData {
	private Case c;
	private Set<Subcase> subcaseSet;
	private MetaData metadata;
	private Evidence evidence;
	private Set<Verdict> verdicts;
	public CaseData(Case c, Set<Subcase> subcaseSet, MetaData metadata, Evidence evidence, Set<Verdict> verdicts) {
		this.c = c;
		this.subcaseSet = subcaseSet;
		this.metadata = metadata;
		this.evidence = evidence;
		this.verdicts = verdicts;
	}

}
