package main.java.owl.cs.man.ac.uk.cases.lists;

import java.util.Set;

public class Evidence {
	private Set<String> set;
	
	public Evidence(Set<String> set) {
			this.set = set;
		}
		
	public Set<String> getMetaData(){
		return set;
	}
		
	public int amountOfEvidence(){
		return set.size();
	}
}

