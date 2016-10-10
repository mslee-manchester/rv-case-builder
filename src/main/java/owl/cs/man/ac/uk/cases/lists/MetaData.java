package main.java.owl.cs.man.ac.uk.cases.lists;

import java.util.Set;

public class MetaData {
	private Set<String> set;
	
	public MetaData(Set<String> set) {
		this.set = set;
	}
	
	public Set<String> getMetaData(){
		return set;
	}
	
	public int numberOfDatatypes(){
		return set.size();
	}
	
	public void addMetaData(String s){
		set.add(s);
	}
	
}
