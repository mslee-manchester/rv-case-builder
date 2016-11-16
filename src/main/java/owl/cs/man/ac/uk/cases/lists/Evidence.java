package main.java.owl.cs.man.ac.uk.cases.lists;

import java.util.Set;

public class Evidence {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((set == null) ? 0 : set.hashCode());
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
		Evidence other = (Evidence) obj;
		if (set == null) {
			if (other.set != null)
				return false;
		} else if (!set.equals(other.set))
			return false;
		return true;
	}

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

