package main.java.owl.cs.man.ac.uk.cases.lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubcaseList {
	private List<String[]> subcaseList;

	//A subcase is a filename containing a set of axioms, 
	//an ontology the set is taken from, a declared entailment and
	//a generating reasoner. This is represented with an
	// list of arrays always of length 4.
	
	public SubcaseList(List<String[]> cl) {
		//check to see if appropriate. Returns bad entries
		Boolean cond = false;
		ArrayList<String[]> badList = new ArrayList<String[]>();
		for(String [] putativeQuad:cl){
			if(putativeQuad.length != 4)
			{
				cond = true;
				badList.add(putativeQuad);
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
		subcaseList = cl;	
	}
}
