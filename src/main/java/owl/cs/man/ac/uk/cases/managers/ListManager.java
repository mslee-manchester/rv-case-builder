package main.java.owl.cs.man.ac.uk.cases.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import main.java.owl.cs.man.ac.uk.cases.lists.Case;
import main.java.owl.cs.man.ac.uk.cases.lists.CaseList;
import main.java.owl.cs.man.ac.uk.cases.lists.Evidence;
import main.java.owl.cs.man.ac.uk.cases.lists.MetaData;
import main.java.owl.cs.man.ac.uk.cases.lists.ReasonerVerdict;
import main.java.owl.cs.man.ac.uk.cases.lists.Subcase;
import owl.cs.man.ac.uk.experiment.csv.CSVUtilities;

public class ListManager {

	private OWLOntologyManager ontoman;
	private OWLDataFactory df;
	public ListManager() {
		this.ontoman = OWLManager.createOWLOntologyManager();
		this.df = ontoman.getOWLDataFactory();
	}
	//TODO edit this for dis files
	public List<Case> constructCaseListFromDisFile(File disagreementfile) throws IOException, OWLOntologyCreationException{
		OWLOntology disont = ontoman.loadOntologyFromOntologyDocument(disagreementfile);
		String ontology = "";
		for(OWLAnnotation ant:disont.getAnnotations()){
			if(ant.getSignature().contains(df.getOWLAnnotationProperty(IRI.create("http://owl.cs.manchester.ac.uk/reasoner_verification/vocabulary#ontology")))){
				ontology = ontology + ant.getValue().toString().substring(1, ant.getValue().toString().lastIndexOf("^^") - 1);
			}
		}
		List<Case> caselist = new ArrayList<Case>();
		for(OWLAxiom ax:disont.getAxioms())
		{
			if(!ax.isAnnotationAxiom() && !ax.isOfType(AxiomType.DECLARATION))
			{
				//Case c = new Case(ontology, ax.getAxiomWithoutAnnotations());
				//caselist.add(c);
			}
		}
		return caselist;
	}
	
	public List<List<String>> getEquivalenceClassMemberList(File csv){
		List<List<String>> equivalenceClassMemberList = new ArrayList<>();
		List<Map<String,String>> csvData = CSVUtilities.getRecords(csv);
		for(Map<String,String> data:csvData)
		{
			String[] line = data.get("members").split(";");
			List<String> memberList = new ArrayList<String>();
			for(String member:line)
			{
				memberList.add(member.substring(1));
			}
			equivalenceClassMemberList.add(memberList);
		}
		return equivalenceClassMemberList;
		}
		
	public Map<String,List<String>> getIDMapForEQClasses(File csv){
		Map<String,List<String>> equivalenceClassMemberList = new HashMap<String,List<String>>();
		List<Map<String,String>> csvData = CSVUtilities.getRecords(csv);
		int id = 1;
		for(Map<String,String> data:csvData)
		{
			String[] line = data.get("members").split(";");
			List<String> memberList = new ArrayList<String>();
			for(String member:line)
			{
				memberList.add(member.substring(1));
			}
			equivalenceClassMemberList.put("EC" + id, memberList);
			id++;
		}
		return equivalenceClassMemberList;
	}
	
	public List<Case> constructCaseListFromCSV(File csv) throws IOException{
		List<Case> caseList = new ArrayList<Case>();
		List<Map<String,String>> csvData = CSVUtilities.getRecords(csv);
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = ontologyManager.getOWLDataFactory();
		Set<String> values = new HashSet<>();
		Set<String> values2 = new HashSet<>();
		Map<String,Subcase> valuesMap = new HashMap<String,Subcase>();
		for(Map<String,String> data:csvData)
		{
			String ontology = data.get("ontology");			
			String subClass = data.get("subclass");
			String supClass = data.get("superclass");
			values.add(ontology+","+subClass+","+supClass);
			String jname = data.get("justname");
			values2.add(ontology+","+subClass+","+supClass+","+jname);
		}
		
		for(String value:values2)
		{
			String[] s = value.split(",");
			String subClass = s[1];
			String supClass = s[2];
			String just = s[3];
			OWLAxiom entailment = df.getOWLSubClassOfAxiom(df.getOWLClass(IRI.create(subClass)), 
					  df.getOWLClass(IRI.create(supClass)));
			Set<ReasonerVerdict> reasonerVerdicts = new HashSet<>();
			Set<String> evidence = new HashSet<>();
			Set<String> metadata = new HashSet<>();
			String genReasoner = "";
			for(Map<String,String> data:csvData)
			{
				if(data.get("ontology").equals(s[0]) && 
				   data.get("subclass").equals(subClass) &&
				   data.get("superclass").equals(supClass) &&
				   data.get("justname").equals(just))
				{
					genReasoner = data.get("reasoner_just");
					String reasoner = data.get("reasoner");
					if(data.get("entailed_by_j").equals("1"))
					{
						reasonerVerdicts.add(new ReasonerVerdict(true,reasoner));
					}
					else if(data.get("entailed_by_j").equals("0"))
					{
						reasonerVerdicts.add(new ReasonerVerdict(false,reasoner));

					}
					else
					{
						reasonerVerdicts.add(new ReasonerVerdict(null,reasoner));
					}
					
					if(!data.get("dts").isEmpty())
					{
						String[] dt = data.get("dts").split(" ");
						for(String datatype:dt)
						{
							metadata.add(datatype);
						}
					}
					
					if(data.get("category").equals("odd"))
					{
						evidence.add("Verdict: odd");
					}
					
					if(data.get("just_size").equals("1"))
					{
						evidence.add("Axiom Swallowing");
					}					
				}
				
				valuesMap.put(just, new Subcase(s[0],entailment,just,genReasoner, new MetaData(metadata),new Evidence(evidence),reasonerVerdicts));
			}
		}
		
		for(String value:values)
		{
			String[] s = value.split(",");
			String subClass = s[1];
			String supClass = s[2];
			OWLAxiom entailment = df.getOWLSubClassOfAxiom(df.getOWLClass(IRI.create(subClass)), 
								  df.getOWLClass(IRI.create(supClass)));
			Boolean axsw = true;
			Set<String> evidence = new HashSet<>();
			Set<ReasonerVerdict> reasonerVerdicts = new HashSet<>();
			Set<String> metadata = new HashSet<>();
			Set<String> justNames = new HashSet<>();
			for(Map<String,String> data:csvData)
			{
				if(data.get("ontology").equals(s[0]) && 
				   data.get("subclass").equals(subClass) &&
				   data.get("superclass").equals(supClass))
				{
					if(data.get("just_size").equals("1"))
					{
						axsw = true;
						
					}
					if(data.get("category").equals("odd"))
					{
						evidence.add("Verdict: odd");
					}
					String reasoner = data.get("reasoner");
	
					if(data.get("in_cl_o").equals("1"))
					{
						reasonerVerdicts.add(new ReasonerVerdict(true,reasoner));
					}
					else if(data.get("in_cl_o").equals("0"))
					{
						reasonerVerdicts.add(new ReasonerVerdict(false,reasoner));
					}
					else
					{
						reasonerVerdicts.add(new ReasonerVerdict(null,reasoner));
					}
					justNames.add(data.get("justname"));
				}
			}
			Set<Subcase> subcaseSet = new HashSet<>();
			for(String j:justNames)
			{
				subcaseSet.add(valuesMap.get(j));
				if(valuesMap.get(j).getEvidence().getMetaData().contains("Reasoner Consensus"))
				{
					evidence.add("Reasoner Consensus");
				}
			}
			Case c = new Case(s[0],
							  entailment, 
							  subcaseSet, 
							  new MetaData(new HashSet<String>()), 
							  new Evidence(evidence), 
							  reasonerVerdicts);
			caseList.add(c);
		}
		//OWLAxiom entailment = df.getOWLSubClassOfAxiom(df.getOWLClass(IRI.create(subClass)), 
				// df.getOWLClass(IRI.create(supClass)));
		
		return caseList;
	}
	
	public void generateCSVOfCaseList(List<Case> list, File csv) throws IOException{
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(csv)));
		pw.println("ontology,entailment");
		for(Case c:list)
		{
			pw.println(c.getOntology().toString() + "," + c.getEntailment());
		}
		pw.close();
	}
	/**
	public List<Subcase> extractSubcasesFromCaseData(ArrayList<CaseData> list){
		List<Subcase> subcases = new ArrayList<Subcase>();
		for(CaseData cd:list)
		{
			subcases.addAll(cd.getSubcases());
		}
		return subcases;
	}
	/**
	public ArrayList<CaseData> constructCaseDataFromCSV(List<Case> list, File csv) throws IOException{
		ArrayList<CaseData> casedata = new ArrayList<CaseData>();
		Map<Case,ArrayList<String>> md = this.getCaseListMetaData(list, csv);
		//TODO change to keyset loop CSV object in experiment API
		//CSVUtils.read..
		for(Case c:list)
		{
			ArrayList<String> cmd = md.get(c);
			Set<String> sc = new HashSet<>();
			Set<ReasonerVerdict> reasonerVerdicts = new HashSet<>();
			Set<String> evidence = new HashSet<>();
			for(String s:cmd)
			{
				int pos1 = this.ordinalIndexOf(s, ",", 11);
				int pos2 = this.ordinalIndexOf(s, ",", 12);
				sc.add(s.substring(pos1+ 1,pos2));
				int pos3 = this.ordinalIndexOf(s, ",", 16);
				int pos4 = this.ordinalIndexOf(s, ",", 17);
				String reasoner = s.substring(pos3 + 1, pos4);
				int pos5 = this.ordinalIndexOf(s,",", 13);
				int pos6 = this.ordinalIndexOf(s,",", 14);
				Boolean ver;
				String inCH = s.substring(pos5 +1 ,pos6);
				if(inCH.equals("1"))
				{
					ver = true;
				}
				else if(inCH.equals("0"))
				{
					ver = false;
				}
				else
				{
					ver = null;
				}
				reasonerVerdicts.add(new ReasonerVerdict(ver,reasoner));
				int pos7 = this.ordinalIndexOf(s,",", 9);
				int pos8 = this.ordinalIndexOf(s,",", 10);
				if(s.substring(pos7 +1 ,pos8).equals("odd"))
				{
					evidence.add("Verdict: odd");
				}
				int pos9 = this.ordinalIndexOf(s,",",19);
				//int pos10 = this.ordinalIndexOf(s, ",", 20);
				if(s.substring(pos9 + 1).equals("1"))
				{
					evidence.add("Axiom Swallowing");
				}
			}
			Set<Subcase> subcaseSet = new HashSet<Subcase>();
			for(String s:sc)
			{
				Subcase subc = new Subcase(c.getOntology().toString(),(OWLAxiom) c.getEntailment(),s);
				subcaseSet.add(subc);
			}
			casedata.add(new CaseData(c,subcaseSet,new MetaData(new HashSet<String>()),new Evidence(evidence),reasonerVerdicts));
			
			
			
			/**		
			Map<Subcase,Set<ReasonerVerdict>> subVerdicts = new HashMap<Subcase,Set<ReasonerVerdict>>();
			for(String s:cmd)
			{
				Set<ReasonerVerdict> reasonerVerdicts = new HashSet<ReasonerVerdict>();
				System.out.println("Working on line: " + s);
				for(Subcase s2:subcaseSet)
				{
					if(s.contains(s2.getJustification()))
					{
						System.out.println("s contains just");
						int pos1 = this.ordinalIndexOf(s, ",", 8);
						int pos2 = this.ordinalIndexOf(s, ",", 9);
						Boolean ver;
						String entailByJ = s.substring(pos1 + 1, pos2); 
						if(entailByJ.equals("1"))
						{
							ver = true;
						}
						else
						{
							ver = false;
						}
						int pos3 = this.ordinalIndexOf(s, ",", 16);
						int pos4 = this.ordinalIndexOf(s, ",", 17);
						String reasoner = s.substring(pos3 + 1, pos4);
						//System.out.println("Reasoner: " + reasoner);
						//System.out.println("Verdict " + ver);
						ReasonerVerdict rv = new ReasonerVerdict(ver,reasoner);
						reasonerVerdicts.add(rv);
					}
				}
				subVerdicts.put(s2, reasonerVerdicts);
			}
			**/	
	/**
		}
		return casedata;
	}
	**/
	/**
	public ArrayList<SubcaseData> constructSubCaseDataFromCSV(List<Subcase> list, File csv) throws IOException{
		ArrayList<SubcaseData> subcaseData = new ArrayList<SubcaseData>();
		Map<Subcase,ArrayList<String>> md = this.getSubcaseListMetaData(list, csv);
		for(Subcase sc:list)
		{
			Set<ReasonerVerdict> verdicts = new HashSet<ReasonerVerdict>();
			Set<String> metadata = new HashSet<String>();
			Set<String> evidence = new HashSet<String>();
			for(String line:md.get(sc))
			{
				int pos1 = this.ordinalIndexOf(line, ",", 16);
				int pos2 = this.ordinalIndexOf(line, ",", 17);
				String reasoner = line.substring(pos1 + 1,pos2);
				int pos3 = this.ordinalIndexOf(line, ",", 8);
				int pos4 = this.ordinalIndexOf(line, ",", 9);
				Boolean ver;
				String entByJ = line.substring(pos3 +1, pos4);
				if(entByJ.equals("1"))
				{
					ver = true;
				}
				else if(entByJ.equals("0"))
				{
					ver = false;
				}
				else
				{
					ver = null;
				}
				verdicts.add(new ReasonerVerdict(ver,reasoner));
				int pos5 = this.ordinalIndexOf(line, ",", 7);
				int pos6 = this.ordinalIndexOf(line, ",", 8);
				String datatypes = line.substring(pos5 + 1,pos6);
				if(!datatypes.equals(""))
				{
					for(String s:datatypes.split(" "))
					{
					metadata.add(s);
					}
				}
				
				int pos7 = this.ordinalIndexOf(line,",", 9);
				int pos8 = this.ordinalIndexOf(line,",", 10);
				if(line.substring(pos7 +1 ,pos8).equals("odd"))
				{
					evidence.add("Verdict: odd");
				}
				int pos9 = this.ordinalIndexOf(line,",",19);
				if(line.substring(pos9 + 1).equals("1"))
				{
					evidence.add("Axiom Swallowing");
				}
				
			}
			String firstLine = md.get(sc).get(0);
			int pos10 = this.ordinalIndexOf(firstLine, ",", 4);
			int pos11 = this.ordinalIndexOf(firstLine, ",", 5);
			String reasonerGen = firstLine.substring(pos10 + 1, pos11);
			subcaseData.add(new SubcaseData(sc,reasonerGen,new MetaData(metadata),new Evidence(evidence),verdicts));
		}
		return subcaseData;
	}
	
	public Map<Subcase,ArrayList<String>> getSubcaseListMetaData(List<Subcase> list, File csv) throws IOException{
		ArrayList<String> csvData = new ArrayList<String>();		
		BufferedReader reader = new BufferedReader(new FileReader(csv));
		while (true) {
		    String line = reader.readLine();
		    if (line == null) {
			break;
		    }
		    csvData.add(line);
		}
		reader.close();
		Map<Subcase,ArrayList<String>> subcaseMetaData = new HashMap<Subcase,ArrayList<String>>();
		for(Subcase sc:list)
		{
			ArrayList<String> data = new ArrayList<String>();
			for(String smd:csvData)
			{
				if(smd.contains(sc.getJustification()))
				{
					data.add(smd);
				}
			}
			subcaseMetaData.put(sc, data);
		}
		return subcaseMetaData;
	}
	
	public Map<Case,ArrayList<String>> getCaseListMetaData(List<Case> list, File csv) throws IOException{
		ArrayList<String> csvData = new ArrayList<String>();		
		BufferedReader reader = new BufferedReader(new FileReader(csv));
		while (true) {
		    String line = reader.readLine();
		    if (line == null) {
			break;
		    }
		    csvData.add(line);
		}
		reader.close();
		Map<Case,ArrayList<String>> caseMetaData = new HashMap<Case,ArrayList<String>>();
		for(Case c:list)
		{
			ArrayList<String> metaData = new ArrayList<String>();
			for(String s:csvData)
			{
				if(this.lineContainsCase(s, c) || this.lineContainsSubClasses(s, c))
				{
					
					metaData.add(this.getDataFromLine(s, c));
				}
			}
			caseMetaData.put(c, metaData);
		}
		return caseMetaData;
	}
			
	private String getDataFromLine(String line, Case c){
		String data = line.replace(c.getOntology().toString(), "");
		data.replaceFirst(c.getEntailment().toString(), "");
		OWLAxiom entailment = (OWLAxiom) c.getEntailment();
		String datastring = "";
		if(entailment.isOfType(AxiomType.SUBCLASS_OF))
		{
			OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom) entailment;
			String subclass = axiom.getSubClass().toString();
			String supclass = axiom.getSuperClass().toString();
			String ax = "";
			if(axiom.getSubClass().isTopEntity())
			{
				if(!axiom.getSuperClass().isBottomEntity())
				{
					ax = ax + subclass + "_" + supclass.substring(supclass.lastIndexOf("/") + 1, supclass.length() - 1);
				}
				else
				{
					ax = ax + subclass+ "_" + supclass;
				}
			}
			else if(axiom.getSuperClass().isBottomEntity())
			{
				ax = ax + subclass.substring(subclass.lastIndexOf("/") + 1, subclass.length() - 1) 
						+ "_" + supclass;
			}
			else
			{
			ax = ax + subclass.substring(subclass.lastIndexOf("/") + 1, subclass.length() - 1) 
					+ "_"  + supclass.substring(supclass.lastIndexOf("/") + 1, supclass.length() - 1);
			}
			datastring = datastring + data.replaceFirst(ax, "");
		}
		else
		{
			datastring = datastring + data;
		}
		return datastring;
	}
	
	private Boolean lineContainsCase(String s, Case c){
		return s.contains(c.getOntology().toString()) && 
				(s.contains(c.getEntailment().toString()) || this.lineContainsSubClasses(s, c));
	}
	
	//To use with old files that contain only subclass axioms of form C1_C2.
	private Boolean lineContainsSubClasses(String s, Case c){
		OWLAxiom entailment = (OWLAxiom) c.getEntailment();
		if(entailment.isOfType(AxiomType.SUBCLASS_OF))
		{
			OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom) entailment;
			String subclass = axiom.getSubClass().toString();
			String supclass = axiom.getSuperClass().toString();
			if(s.contains(subclass.substring(1,subclass.length() - 1)) && s.contains(supclass.substring(1,supclass.length() - 1)))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	//Defunct with Case class
	/**
	public CaseList constructCaseListFromDisFile(File disagreementfile) throws IOException, OWLOntologyCreationException{
			OWLOntology disont = ontoman.loadOntologyFromOntologyDocument(disagreementfile);
			String ontology = "";
			for(OWLAnnotation ant:disont.getAnnotations()){
				if(ant.getSignature().contains(df.getOWLAnnotationProperty(IRI.create("http://owl.cs.manchester.ac.uk/reasoner_verification/vocabulary#ontology")))){
					ontology = ontology + ant.getValue().toString().substring(1, ant.getValue().toString().lastIndexOf("^^") - 1);
				}
			}
			List<String[]> list = new ArrayList<String[]>();
			for(OWLAxiom ax:disont.getAxioms())
			{
				if(!ax.isAnnotationAxiom() && !ax.isOfType(AxiomType.DECLARATION))
				{
					String[] array = new String[2];
					array[0] = ontology;
					array[1] = ax.getAxiomWithoutAnnotations().toString();
					list.add(array);
				}
			}
			for(String[] se:list)
			{
				System.out.println(se[0] + ", " + se[1]);
			}
			return new CaseList(list);
	}
	**/
}
