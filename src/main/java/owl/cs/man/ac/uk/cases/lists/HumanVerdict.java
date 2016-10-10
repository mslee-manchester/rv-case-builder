package main.java.owl.cs.man.ac.uk.cases.lists;

public class HumanVerdict extends Verdict {
	private String humanID;
	
	public HumanVerdict(Boolean verdict, String humanID) {
		super(verdict);
		this.humanID = humanID;
	}
	
	public void changeHumanID(String ID){
		this.humanID = ID;
	}
	
	public String getHumanID(){
		return this.humanID;
	}
}