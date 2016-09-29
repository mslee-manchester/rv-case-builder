package main.java.owl.cs.man.ac.uk.cases.lists;

import java.util.ArrayList;
import java.util.List;

public class CaseList {
	
	// A case consists of an ontology
	// and a declared entailment.
	// We store this in a list of arrays
	// which are always of length 2.
	
	private List<String[]> caseList;
	
	public CaseList(List<String[]> cl) {
		//check to see if appropriate. Returns bad entries
		Boolean cond = false;
		ArrayList<String[]> badList = new ArrayList<String[]>();
		for(String [] putativePair:cl){
			if(putativePair.length != 2)
			{
				cond = true;
				badList.add(putativePair);
			}
		}
		if(cond)
		{
			System.out.println("Bad entries: ");
			for(String[] s:badList)
			{
				System.out.println(s);
			}
			throw new RuntimeException(
					"List of cases is inappropriate.");	
		}
		caseList = cl;
	}
	
	public String[] getCaseAtIndex(int index){
		return caseList.get(index);
	}
	
	public int length(){
		return caseList.size();
	}

}
