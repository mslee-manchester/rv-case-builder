package main.java.owl.cs.man.ac.uk.cases.lists;

public class ReasonerVerdict {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reasoner == null) ? 0 : reasoner.hashCode());
		result = prime * result + ((verdict == null) ? 0 : verdict.hashCode());
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
		ReasonerVerdict other = (ReasonerVerdict) obj;
		if (reasoner == null) {
			if (other.reasoner != null)
				return false;
		} else if (!reasoner.equals(other.reasoner))
			return false;
		if (verdict == null) {
			if (other.verdict != null)
				return false;
		} else if (!verdict.equals(other.verdict))
			return false;
		return true;
	}

	private String reasoner;
	private Boolean verdict;
	public ReasonerVerdict(Boolean verdict, String reasoner) {
		this.verdict = verdict;
		this.reasoner = reasoner;
	}
	
	public void changeReasonerName(String name){
		this.reasoner = name;
	}
	
	public String getReasoner(){
		return reasoner;
	}
	
	public Boolean getValue(){
		return verdict;
	}
}
