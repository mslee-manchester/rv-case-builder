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

import main.java.owl.cs.man.ac.uk.cases.lists.CaseList;
import main.java.owl.cs.man.ac.uk.cases.lists.ReasonerVerdict;
import main.java.owl.cs.man.ac.uk.cases.lists.Subcase;

import main.java.owl.cs.man.ac.uk.cases.managers.ListManager;
import main.java.owl.cs.man.ac.uk.cases.writer.OWLXMLWriter;
import main.java.owl.cs.man.ac.uk.cases.writer.XMLCaseWriter;

import owl.cs.man.ac.uk.experiment.csv.*;

public final class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, OWLOntologyCreationException, OWLOntologyStorageException, SAXException {
		// TODO Auto-generated method stub
		File dis = new File(args[0]);
		File csv = new File(args[1]);
		String dir = args[2];
		File justDir = new File(args[3]);
		File equvCSV = new File(args[4]);
		/**
		ListManager lm = new ListManager();
		List<Case> caseList = lm.constructCaseListFromCSV(csv);
		XMLCaseWriter xcw = new XMLCaseWriter();
		File xml = new File(dir + "/cases.xml");
		xml.canWrite();
		xcw.createXMLCaseFileFromList(csv, xml, caseList, "iswc-2014");
		/**
		int i = 0;
		List<Map<String,String>> records = CSVUtilities.getRecords(csv);
		for(Map<String,String> r:records)
		{
			
			System.out.println("Map " + i);
			for(String k:r.keySet())
			{
				System.out.println(k);
				System.out.println(r.get(k));
				System.out.println("");
			}
			System.out.println("");
			i++;
		}
		**/
		
		ListManager lm = new ListManager();
		List<Case> caseList = lm.constructCaseListFromCSV(csv);
		Map<String,List<Case>> splitMap = new HashMap<String,List<Case>>();
		Map<String,List<String>> equvMap = lm.getIDMapForEQClasses(equvCSV);
		XMLCaseWriter xcw = new XMLCaseWriter();
		xcw.createOntologySortedXMLCaseFilesFromList(csv, new File(dir), justDir, equvMap, caseList, "iswc-2014");
		/**
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
			xcw.createXMLCaseFileFromList(csv, xml, justDir, equvCSV, clist, "iswc-2014");
			
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


