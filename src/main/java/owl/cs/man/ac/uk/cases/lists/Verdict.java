package main.java.owl.cs.man.ac.uk.cases.lists;

public abstract class Verdict {
	private Boolean verdict;
	
	public Verdict(Boolean verdict) {
		this.verdict = verdict;
	}
	
	public void changeVerdict(Boolean verdict){
		this.verdict = verdict;
	}

	public Boolean getValue(){
		return verdict;
	}
}
