package main.java.owl.cs.man.ac.uk.test;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.xml.sax.SAXException;

import main.java.owl.cs.man.ac.uk.cases.lists.Case;
import main.java.owl.cs.man.ac.uk.cases.lists.CaseData;
import main.java.owl.cs.man.ac.uk.cases.lists.CaseList;
import main.java.owl.cs.man.ac.uk.cases.lists.ReasonerVerdict;
import main.java.owl.cs.man.ac.uk.cases.lists.Subcase;
import main.java.owl.cs.man.ac.uk.cases.lists.SubcaseData;
import main.java.owl.cs.man.ac.uk.cases.managers.ListManager;
import main.java.owl.cs.man.ac.uk.cases.writer.OWLXMLWriter;
import main.java.owl.cs.man.ac.uk.cases.writer.XMLCaseWriter;

public final class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, OWLOntologyCreationException, OWLOntologyStorageException, SAXException {
		// TODO Auto-generated method stub
		File dis = new File(args[0]);
		File csv = new File(args[1]);
		String dir = args[2];
		ListManager lm = new ListManager();
		List<Case> caseList = lm.constructCaseListFromCSV(csv);
		Map<String,List<Case>> splitMap = new HashMap<String,List<Case>>();
		
		for(Case c:caseList)
		{
			if(!splitMap.keySet().contains(c.getOntology()))
			{
				List<Case> list = new ArrayList<Case>();
				list.add(c);
				splitMap.put(c.getOntology().toString(), list);
				
			}
			else
			{
				List<Case> list = splitMap.get(c.getOntology().toString());
				list.add(c);
				splitMap.replace(c.getOntology().toString(), list);
				
			}
		}
		
		
		for(String key:splitMap.keySet())
		{
			List<Case> clist = splitMap.get(key);
			XMLCaseWriter xcw = new XMLCaseWriter();
			File xml = new File(dir + key + ".cases.xml");
			xml.canWrite();
			xcw.createXMLCaseFileFromList(csv, xml, clist, "iswc-2014", key);
			
		}
		//List<Case> list = lm.constructCaseListFromDisFile(dis);
		//Map<Case,ArrayList<String>> m =lm.getCaseListMetaData(list, csv);
		
		/**
		for(Case c:m.keySet())
		{
			System.out.println(c.getOntology().toString());
			System.out.println(c.getEntailment().toString());
			for(String s: m.get(c))
			{
				System.out.println(s);
			}
		}
		**/
		/**
		ArrayList<CaseData> cd = lm.constructCaseDataFromCSV(list, csv);
		for(CaseData data:cd)
		{
			System.out.println(data.getCase().getOntology());
			System.out.println(data.getCase().getEntailment().toString());
			System.out.println("METADATA: " + data.getMetaData().getMetaData());
			System.out.println("EVIDENCE " + data.getEvidence().getMetaData());
			System.out.println("RV:");
			for(ReasonerVerdict rv:data.getReasonerVerdicts())
			{
				System.out.println(rv.getReasoner());
				System.out.println(rv.getValue());
			}
			System.out.println("SUBCASES:");
			for(Subcase s:data.getSubcases())
			{
				System.out.println(s.getJustification());
			}
			System.out.println("");
		}
		List<Subcase> sc = lm.extractSubcasesFromCaseData(cd);
		ArrayList<SubcaseData> scd = lm.constructSubCaseDataFromCSV(sc, csv);
		for(SubcaseData data:scd)
		{
			System.out.println(data.getCase().getOntology());
			System.out.println(data.getCase().getEntailment().toString());
			System.out.println(data.getCase().getJustification());
			System.out.println("GEN REASONER: " + data.getGeneratingReasoner());
			System.out.println("METADATA: " + data.getMetaData().getMetaData());
			System.out.println("EVIDENCE " + data.getEvidence().getMetaData());
			System.out.println("RV:");
			for(ReasonerVerdict rv:data.getReasonerVerdicts())
			{
				System.out.println(rv.getReasoner());
				System.out.println(rv.getValue());
			}
			System.out.println("");
		}
		
		XMLCaseWriter xcw = new XMLCaseWriter();
		xcw.createXMLCaseFileFromDisAndCSV(dis, csv, xml, "iswc-2014");
		//xcw.createXMLCaseFileFromDis(csv, xml, "iswc");
		 * 
		 */
		}
		
}


