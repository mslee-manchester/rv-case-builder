package main.java.owl.cs.man.ac.uk.cases.lists;

public class ReasonerVerdict extends Verdict {
	private String reasoner;
	
	public ReasonerVerdict(Boolean verdict, String reasoner) {
		super(verdict);
		this.reasoner = reasoner;
	}
	
	public void changeReasonerName(String name){
		this.reasoner = name;
	}
	
	public String getReasoner(){
		return reasoner;
	}
}
