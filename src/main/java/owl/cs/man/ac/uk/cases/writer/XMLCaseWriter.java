package main.java.owl.cs.man.ac.uk.cases.writer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import main.java.owl.cs.man.ac.uk.cases.lists.Case;
import main.java.owl.cs.man.ac.uk.cases.lists.CaseList;
import main.java.owl.cs.man.ac.uk.cases.lists.ReasonerVerdict;
import main.java.owl.cs.man.ac.uk.cases.lists.Subcase;
import main.java.owl.cs.man.ac.uk.cases.managers.ListManager;

public class XMLCaseWriter {

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Transformer transformer;
	public XMLCaseWriter() throws ParserConfigurationException, TransformerConfigurationException {
		docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		docBuilder = docFactory.newDocumentBuilder();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformer = transformerFactory.newTransformer();
	}
	
	//Takes a CSV and finds all relevant ontologies and their respective cases and produces case files for each ontology.
	public void createOntologySortedXMLCaseFilesFromList(File csv, File saveDir, File justDir, Map<String,List<String>> equivalenceClasses, List<Case> list, String experiment){
		//Spliting cases into relevant ontologies
		Map<String,List<Case>> splitMap = new HashMap<String,List<Case>>();
		for(Case c:list)
		{
			if(!splitMap.keySet().contains(c.getOntology()))
			{
				List<Case> newList = new ArrayList<Case>();
				newList.add(c);
				splitMap.put(c.getOntology().toString(), newList);
				
			}
			else
			{
				List<Case> newList = splitMap.get(c.getOntology().toString());
				newList.add(c);
				splitMap.replace(c.getOntology().toString(), newList);
				
			}
		}
		

		for(String key:splitMap.keySet())
		{
			List<Case> clist = splitMap.get(key);
			File xml = new File(saveDir + "/"+  key + ".cases.xml");
			xml.canWrite();
			try {
				this.createXMLCaseFileFromList(csv, xml, justDir, equivalenceClasses, clist, "iswc-2014");
			} catch (OWLOntologyCreationException | OWLOntologyStorageException | IOException
					| ParserConfigurationException | SAXException | TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
		
	}
	
	
	
	public void createXMLCaseFileFromList(File csv, File xmlfile, File dir, Map<String,List<String>> equivalenceClasses, List<Case> list, String experiment) throws OWLOntologyCreationException, IOException, ParserConfigurationException, OWLOntologyStorageException, SAXException, TransformerException{
		//check CSV non-empty
		if(csv.length() == 0) {
				throw new RuntimeException("Empty CSV file error");
		}
		System.out.println("First test passed, non-empty files.");
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("cases");
		doc.appendChild(rootElement);
		//Constructing casedata dependent on list
		ListManager lm = new ListManager();
		//ArrayList<CaseData> cld = lm.constructCaseDataFromCSV(list, csv);
		//List<Subcase> scl = lm.extractSubcasesFromCaseData(cld);
		//ArrayList<SubcaseData> scd = lm.constructSubCaseDataFromCSV(scl, csv);
		//constructing info for relevant attributes. Because this is grouped by ontology, we can get it once!
		BasicFileAttributes attr = Files.readAttributes(csv.toPath(), BasicFileAttributes.class);
		String date = attr.lastModifiedTime().toString().substring(0, attr.lastModifiedTime().toString().indexOf("T"));
		//Root attributes
		Attr xsd = doc.createAttribute("xmlns:xsi");
		Attr xsi = doc.createAttribute("xsi:noNamespaceSchemaLocation");
		//Attr owl = doc.createAttribute("xmlns:owl");
		Attr grouping = doc.createAttribute("grouping");
		rootElement.setAttributeNode(xsi);
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(xsd);
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:owl", "http://www.w3.org/2002/07/owl#");
		rootElement.setAttributeNode(grouping);
		xsd.setValue("http://www.w3.org/2001/XMLSchema-instance");
		xsi.setValue("case.xsd");
		grouping.setValue("ontology");
		OWLXMLWriter oxwriter = new OWLXMLWriter();
		for(Case c:list)
		{
			System.out.println(c.getOntology());
			System.out.println(c.getEntailment());
			System.out.println(c.getEvidence().getMetaData());
			System.out.println(c.getMetaData().getMetaData());
			for(ReasonerVerdict rv:c.getReasonerVerdicts())
			{
				System.out.println(rv.getReasoner());
				System.out.println(rv.getValue());
			}
			
			Element xmlcase = doc.createElement("case");
			rootElement.appendChild(xmlcase);
			Attr generationDate = doc.createAttribute("generation-date");
			xmlcase.setAttributeNode(generationDate);
			generationDate.setValue(date);
			Attr experimentAttr = doc.createAttribute("experiment");
			xmlcase.setAttributeNode(experimentAttr);
			experimentAttr.setValue(experiment);
			Attr ontologyAttr = doc.createAttribute("ontology");
			xmlcase.setAttributeNode(ontologyAttr);
			ontologyAttr.setValue(c.getOntology());
			Element entailment = doc.createElement("entailment");
			xmlcase.appendChild(entailment);
			Element n = (Element) oxwriter.getEntailmentAsDocElement((OWLAxiom) c.getEntailment(), xmlfile.getParent());
			Element elementWithNS = oxwriter.addOWLNameSpace(n, doc);
			doc.adoptNode(elementWithNS);
			entailment.appendChild(elementWithNS);
			Element reasonerVerdict = doc.createElement("reasoner-verdict");
			xmlcase.appendChild(reasonerVerdict);
			Element reasonerAssent = doc.createElement("reasoner-assent");
			Element reasonerDissent = doc.createElement("reasoner-dissent");
			Element reasonerFailure = doc.createElement("reasoner-failure");
			reasonerVerdict.appendChild(reasonerAssent);
			reasonerVerdict.appendChild(reasonerDissent);
			reasonerVerdict.appendChild(reasonerFailure);
			
			for(ReasonerVerdict rv:c.getReasonerVerdicts())
			{
				Element reasoner = doc.createElement("reasoner");
				Attr rname = doc.createAttribute("name");
				rname.setValue(rv.getReasoner());
				reasoner.setAttributeNode(rname);
				if(rv.getValue())
				{
					reasonerAssent.appendChild(reasoner);
				}
				else if(!rv.getValue())
				{
					reasonerDissent.appendChild(reasoner);
				}
				else
				{
					reasonerFailure.appendChild(reasoner);
				}
			}
			Element decision = doc.createElement("decision");
			Attr dec = doc.createAttribute("value");
			decision.setAttributeNode(dec);
			dec.setValue("na");
			xmlcase.appendChild(decision);
			Element evidence = doc.createElement("evidence");
			decision.appendChild(evidence);
			for(String e:c.getEvidence().getMetaData())
			{
				if(e.equals("Verdict: odd"))
				{
					Element odd = doc.createElement("odd");
					evidence.appendChild(odd);
				}
				else if(e.equals("Axiom Swallowing"))
				{
					Element axswal = doc.createElement("self-just");
					evidence.appendChild(axswal);
					dec.setValue("true");
				}
			}
			Element subcases = doc.createElement("subcases");
			xmlcase.appendChild(subcases);
			Boolean cons = true;
			for(Subcase sc:c.getSubcases())
			{
				System.out.println(sc.getJustification());
				System.out.println(sc.getEvidence().getMetaData());
				System.out.println(sc.getMetaData().getMetaData());
				for(ReasonerVerdict rv:sc.getReasonerVerdicts())
				{
					System.out.println(rv.getReasoner());
					System.out.println(rv.getValue());
				}
				Element subcase = doc.createElement("subcase");
				subcases.appendChild(subcase);
				Attr scname = doc.createAttribute("name");
				subcase.setAttributeNode(scname);
				scname.setValue(sc.getJustification());
				Element justification = oxwriter.getJustificationElement(new File(dir + "/" + sc.getJustification()));
				for(int i = 0;i < justification.getChildNodes().getLength();i++)
				{
					Element child = (Element) justification.getChildNodes().item(i);
					oxwriter.addOWLNameSpace(child, doc);
				}
				doc.adoptNode(justification);
				subcase.appendChild(justification);
				Element SCreasonerVerdict = doc.createElement("reasoner-verdict");
				subcase.appendChild(SCreasonerVerdict);
				Element SCreasonerAssent = doc.createElement("reasoner-assent");
				Element SCreasonerDissent = doc.createElement("reasoner-dissent");
				Element SCreasonerFailure = doc.createElement("reasoner-failure");
				SCreasonerVerdict.appendChild(SCreasonerAssent);
				SCreasonerVerdict.appendChild(SCreasonerDissent);
				SCreasonerVerdict.appendChild(SCreasonerFailure);
				Boolean consensus = true;
				for(ReasonerVerdict rv:sc.getReasonerVerdicts())
				{
					Element reasoner = doc.createElement("reasoner");
					Attr rname = doc.createAttribute("name");
					rname.setValue(rv.getReasoner());
					reasoner.setAttributeNode(rname);
					if(rv.getValue())
					{
						SCreasonerAssent.appendChild(reasoner);
					}
					else if(!rv.getValue())
					{
						SCreasonerDissent.appendChild(reasoner);
						consensus = false;
						cons = false;
					}
					else
					{
						SCreasonerFailure.appendChild(reasoner);
						consensus = false;
						cons = false;
					}
				}
				Element genReasoner = doc.createElement("generating-reasoner");
				Element r = doc.createElement("reasoner");
				Attr genName = doc.createAttribute("name");
				r.setAttributeNode(genName);
				genName.setValue(sc.getGeneratingReasoner());
				genReasoner.appendChild(r);
				subcase.appendChild(genReasoner);
				Element SCdecision = doc.createElement("decision");
				Attr SCdec = doc.createAttribute("value");
				SCdecision.setAttributeNode(SCdec);
				SCdec.setValue("na");
				subcase.appendChild(SCdecision);
				Element SCevidence = doc.createElement("evidence");
				SCdecision.appendChild(SCevidence);
				if(consensus)
				{
					Element SCreasonerConsensus = doc.createElement("reasoner-consensus");
					SCevidence.appendChild(SCreasonerConsensus);
				}
				for(String s:sc.getEvidence().getMetaData())
				{
					if(s.equals("Axiom Swallowing"))
					{
						System.out.println("axiomw was swallowed");
						Element axswal = doc.createElement("self-just");
						SCevidence.appendChild(axswal);
						dec.setValue("true");
						SCdec.setValue("true");
					}
					else if(s.equals("Verdict: odd"))
					{
						System.out.println("verdict was odd");
						Element SCodd = doc.createElement("odd");
						SCevidence.appendChild(SCodd);
					}
				}
				Element subcaseMetadata = doc.createElement("subcase-metadata");
				subcase.appendChild(subcaseMetadata);
				for(String s:sc.getMetaData().getMetaData())
				{
					Element datatype = doc.createElement("datatype");
					Attr type = doc.createAttribute("type");
					datatype.setAttributeNode(type);
					type.setValue(s);
					subcaseMetadata.appendChild(datatype);
				}
			}
			if(cons)
			{
				Element reasonerConsensus = doc.createElement("reasoner-consensus");
				evidence.appendChild(reasonerConsensus);
			}
			System.out.println("");
		}				
	
		NodeList justifications = doc.getElementsByTagName("subcase");

		for(String id:equivalenceClasses.keySet())
		{
			for(String member:equivalenceClasses.get(id))
			{
				for(int i = 0;i < justifications.getLength();i++)
				{
					Element justification = (Element) justifications.item(i);
					if(member.equals(justification.getAttributes().getNamedItem("name").getNodeValue()))
					{
						Attr equivalenceClass = doc.createAttribute("equivalence-class");
						justification.setAttributeNode(equivalenceClass);
						equivalenceClass.setValue(id);
					}
				}
			}
		}
		
		//writing content to xml file
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(xmlfile);
				
		// Output to console for testing
		transformer.transform(source, result);

		System.out.println("File saved!");
	}
	
	public void createXMLCaseFileFromCSV(File csv, File xmlfile, String experiment, String ontology) throws OWLOntologyCreationException, IOException, ParserConfigurationException, OWLOntologyStorageException, SAXException, TransformerException{
		//check CSV non-empty
		if(csv.length() == 0) {
				throw new RuntimeException("Empty CSV file error");
		}
		System.out.println("First test passed, non-empty files.");
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("cases");
		doc.appendChild(rootElement);
		//Constructing casedata dependent on list
		ListManager lm = new ListManager();
		List<Case> cl = lm.constructCaseListFromCSV(csv);
		ArrayList<CaseData> cld = lm.constructCaseDataFromCSV(cl, csv);
		List<Subcase> scl = lm.extractSubcasesFromCaseData(cld);
		ArrayList<SubcaseData> scd = lm.constructSubCaseDataFromCSV(scl, csv);
		//constructing info for relevant attributes. Because this is grouped by ontology, we can get it once!
		BasicFileAttributes attr = Files.readAttributes(csv.toPath(), BasicFileAttributes.class);
		String date = attr.lastModifiedTime().toString().substring(0, attr.lastModifiedTime().toString().indexOf("T"));
		//Root attributes
		Attr xsd = doc.createAttribute("xmlns:xsi");
		Attr xsi = doc.createAttribute("xsi:noNamespaceSchemaLocation");
		//Attr owl = doc.createAttribute("xmlns:owl");
		Attr grouping = doc.createAttribute("grouping");
		//rootElement.setAttributeNode(xsi);
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(xsd);
		//rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:owl", "http://www.w3.org/2002/07/owl#");
		rootElement.setAttributeNode(grouping);
		xsd.setValue("http://www.w3.org/2001/XMLSchema-instance");
		xsi.setValue("case.xsd");
				
		grouping.setValue("ontology");
		OWLXMLWriter oxwriter = new OWLXMLWriter();
		Set<Element> subcaseSet = new HashSet<Element>();
		for(CaseData cd:cld)
		{
			Element xmlcase = doc.createElement("case");
			rootElement.appendChild(xmlcase);
			Attr generationDate = doc.createAttribute("generation-date");
			xmlcase.setAttributeNode(generationDate);
			generationDate.setValue(date);
			Attr experimentAttr = doc.createAttribute("experiment");
			xmlcase.setAttributeNode(experimentAttr);
			experimentAttr.setValue(experiment);
			Attr ontologyAttr = doc.createAttribute("ontology");
			xmlcase.setAttributeNode(ontologyAttr);
			ontologyAttr.setValue(ontology);
			Element entailment = doc.createElement("entailment");
			xmlcase.appendChild(entailment);
			Element n = (Element) oxwriter.getEntailmentAsDocElement((OWLAxiom) cd.getCase().getEntailment(), xmlfile.getParent());
			Element elementWithNS = oxwriter.addOWLNameSpace(n, doc);
			doc.adoptNode(elementWithNS);
			entailment.appendChild(elementWithNS);
			Element reasonerVerdict = doc.createElement("reasoner-verdict");
			xmlcase.appendChild(reasonerVerdict);
			Element reasonerAssent = doc.createElement("reasoner-assent");
			Element reasonerDissent = doc.createElement("reasoner-dissent");
			Element reasonerFailure = doc.createElement("reasoner-failure");
			reasonerVerdict.appendChild(reasonerAssent);
			reasonerVerdict.appendChild(reasonerDissent);
			reasonerVerdict.appendChild(reasonerFailure);
			
			for(ReasonerVerdict rv:cd.getReasonerVerdicts())
			{
				Element reasoner = doc.createElement("reasoner");
				Attr rname = doc.createAttribute("name");
				rname.setValue(rv.getReasoner());
				reasoner.setAttributeNode(rname);
				if(rv.getValue())
				{
					reasonerAssent.appendChild(reasoner);
				}
				else if(!rv.getValue())
				{
					reasonerDissent.appendChild(reasoner);
				}
				else
				{
					reasonerFailure.appendChild(reasoner);
				}
			}
			Element decision = doc.createElement("decision");
			Attr dec = doc.createAttribute("value");
			decision.setAttributeNode(dec);
			dec.setValue("na");
			xmlcase.appendChild(decision);
			Element evidence = doc.createElement("evidence");
			decision.appendChild(evidence);
			for(String e:cd.getEvidence().getMetaData())
			{
				if(e.equals("Verdict: odd"))
				{
					Element odd = doc.createElement("odd");
					xmlcase.appendChild(odd);
				}
				else if(e.equals("Axiom Swallowing"))
				{
					Element axswal = doc.createElement("self-just");
					xmlcase.appendChild(axswal);
					dec.setValue("true");
				}
			}
			Element subcases = doc.createElement("subcases");
			xmlcase.appendChild(subcases);
			for(Subcase sc:cd.getSubcases())
			{
				Element subcase = doc.createElement("subcase");
				subcases.appendChild(subcase);
				Attr scname = doc.createAttribute("name");
				subcase.setAttributeNode(scname);
				scname.setValue(sc.getJustification());
				subcaseSet.add(subcase);
			}
		}
		
		for(SubcaseData scdat:scd)
		{
			for(Element e:subcaseSet)
			{
				if(e.getAttributeNode("name").getValue().equals(scdat.getCase().getJustification()))
				{
					Element reasonerVerdict = doc.createElement("reasoner-verdict");
					e.appendChild(reasonerVerdict);
					Element reasonerAssent = doc.createElement("reasoner-assent");
					Element reasonerDissent = doc.createElement("reasoner-dissent");
					Element reasonerFailure = doc.createElement("reasoner-failure");
					reasonerVerdict.appendChild(reasonerAssent);
					reasonerVerdict.appendChild(reasonerDissent);
					reasonerVerdict.appendChild(reasonerFailure);
					Boolean consensus = true;
					for(ReasonerVerdict rv:scdat.getReasonerVerdicts())
					{
						Element reasoner = doc.createElement("reasoner");
						Attr rname = doc.createAttribute("name");
						rname.setValue(rv.getReasoner());
						reasoner.setAttributeNode(rname);
						if(rv.getValue())
						{
							reasonerAssent.appendChild(reasoner);
						}
						else if(!rv.getValue())
						{
							reasonerDissent.appendChild(reasoner);
							consensus = false;
						}
						else
						{
							reasonerFailure.appendChild(reasoner);
							consensus = false;
						}
					}
					Element genReasoner = doc.createElement("generating-reasoner");
					e.appendChild(genReasoner);
					Element r = doc.createElement("reasoner");
					Attr genName = doc.createAttribute("name");
					r.setAttributeNode(genName);
					genName.setValue(scdat.getGeneratingReasoner());
					Element decision = doc.createElement("decision");
					Attr dec = doc.createAttribute("value");
					decision.setAttributeNode(dec);
					dec.setValue("na");
					e.appendChild(decision);
					Element evidence = doc.createElement("evidence");
					decision.appendChild(evidence);
					if(consensus)
					{
						Element reasonerConsensus = doc.createElement("reasoner-consensus");
						evidence.appendChild(reasonerConsensus);
						for(int i = 0;i < e.getParentNode().getChildNodes().getLength();i++)
						{
							if(e.getParentNode().getChildNodes().item(i).getLocalName().equals("evidence"))
							{
								Element evidenceCase = doc.createElement("reasoner-consensus");
								e.getParentNode().getChildNodes().item(i).appendChild(evidenceCase);
							}
						}
					}
					
					
					Element subcaseMetadata = doc.createElement("subcase-metadata");
					e.appendChild(subcaseMetadata);
					for(String s:scdat.getMetaData().getMetaData())
					{
						Element datatype = doc.createElement("datatype");
						Attr type = doc.createAttribute("type");
						datatype.setAttributeNode(type);
						type.setValue(s);
						e.appendChild(datatype);
					}
					
				}
			}
		}
		
		//writing content to xml file
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(xmlfile);
				
		// Output to console for testing
		transformer.transform(source, result);

		System.out.println("File saved!");
	}
	
	public void createXMLCaseFileFromDisAndCSV(File dis, File csv, File xmlfile, String experiment) throws OWLOntologyCreationException, IOException, ParserConfigurationException, OWLOntologyStorageException, SAXException, TransformerException{
		//check dis non-empty
		if(dis.length() == 0) {
				throw new RuntimeException("Empty disagreement file error");
		}
		//check CSV non-empty
		if(csv.length() == 0) {
				throw new RuntimeException("Empty CSV file error");
		}
		System.out.println("First test passed, non-empty files.");
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("cases");
		doc.appendChild(rootElement);
		//Constructing casedata dependent on list
		ListManager lm = new ListManager();
		List<Case> cl = lm.constructCaseListFromDisFile(dis);
		ArrayList<CaseData> cld = lm.constructCaseDataFromCSV(cl, csv);
		List<Subcase> scl = lm.extractSubcasesFromCaseData(cld);
		ArrayList<SubcaseData> scd = lm.constructSubCaseDataFromCSV(scl, csv);
		//constructing info for relevant attributes. Because this is grouped by ontology, we can get it once!
		BasicFileAttributes attr = Files.readAttributes(dis.toPath(), BasicFileAttributes.class);
		String date = attr.lastModifiedTime().toString().substring(0, attr.lastModifiedTime().toString().indexOf("T"));
		String ontology	= cl.get(0).getOntology().toString();
		//Root attributes
		Attr xsd = doc.createAttribute("xmlns:xsi");
		Attr xsi = doc.createAttribute("xsi:noNamespaceSchemaLocation");
		//Attr owl = doc.createAttribute("xmlns:owl");
		Attr grouping = doc.createAttribute("grouping");
		//rootElement.setAttributeNode(xsi);
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(xsd);
		//rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:owl", "http://www.w3.org/2002/07/owl#");
		rootElement.setAttributeNode(grouping);
		xsd.setValue("http://www.w3.org/2001/XMLSchema-instance");
		xsi.setValue("case.xsd");
				
		grouping.setValue("ontology");
		OWLXMLWriter oxwriter = new OWLXMLWriter();
		Set<Element> subcaseSet = new HashSet<Element>();
		for(CaseData cd:cld)
		{
			Element xmlcase = doc.createElement("case");
			rootElement.appendChild(xmlcase);
			Attr generationDate = doc.createAttribute("generation-date");
			xmlcase.setAttributeNode(generationDate);
			generationDate.setValue(date);
			Attr experimentAttr = doc.createAttribute("experiment");
			xmlcase.setAttributeNode(experimentAttr);
			experimentAttr.setValue(experiment);
			Attr ontologyAttr = doc.createAttribute("ontology");
			xmlcase.setAttributeNode(ontologyAttr);
			ontologyAttr.setValue(ontology);
			Element entailment = doc.createElement("entailment");
			xmlcase.appendChild(entailment);
			Element n = (Element) oxwriter.getEntailmentAsDocElement((OWLAxiom) cd.getCase().getEntailment(), xmlfile.getParent());
			Element elementWithNS = oxwriter.addOWLNameSpace(n, doc);
			doc.adoptNode(elementWithNS);
			entailment.appendChild(elementWithNS);
			Element reasonerVerdict = doc.createElement("reasoner-verdict");
			xmlcase.appendChild(reasonerVerdict);
			Element reasonerAssent = doc.createElement("reasoner-assent");
			Element reasonerDissent = doc.createElement("reasoner-dissent");
			Element reasonerFailure = doc.createElement("reasoner-failure");
			reasonerVerdict.appendChild(reasonerAssent);
			reasonerVerdict.appendChild(reasonerDissent);
			reasonerVerdict.appendChild(reasonerFailure);
			
			for(ReasonerVerdict rv:cd.getReasonerVerdicts())
			{
				Element reasoner = doc.createElement("reasoner");
				Attr rname = doc.createAttribute("name");
				rname.setValue(rv.getReasoner());
				reasoner.setAttributeNode(rname);
				if(rv.getValue())
				{
					reasonerAssent.appendChild(reasoner);
				}
				else if(!rv.getValue())
				{
					reasonerDissent.appendChild(reasoner);
				}
				else
				{
					reasonerFailure.appendChild(reasoner);
				}
			}
			Element decision = doc.createElement("decision");
			Attr dec = doc.createAttribute("value");
			decision.setAttributeNode(dec);
			dec.setValue("na");
			xmlcase.appendChild(decision);
			Element evidence = doc.createElement("evidence");
			decision.appendChild(evidence);
			for(String e:cd.getEvidence().getMetaData())
			{
				if(e.equals("Verdict: odd"))
				{
					Element odd = doc.createElement("odd");
					xmlcase.appendChild(odd);
				}
				else if(e.equals("Axiom Swallowing"))
				{
					Element axswal = doc.createElement("self-just");
					xmlcase.appendChild(axswal);
					dec.setValue("true");
				}
			}
			Element subcases = doc.createElement("subcases");
			xmlcase.appendChild(subcases);
			for(Subcase sc:cd.getSubcases())
			{
				Element subcase = doc.createElement("subcase");
				subcases.appendChild(subcase);
				Attr scname = doc.createAttribute("name");
				subcase.setAttributeNode(scname);
				scname.setValue(sc.getJustification());
				subcaseSet.add(subcase);
			}
		}
		
		for(SubcaseData scdat:scd)
		{
			for(Element e:subcaseSet)
			{
				if(e.getAttributeNode("name").getValue().equals(scdat.getCase().getJustification()))
				{
					Element reasonerVerdict = doc.createElement("reasoner-verdict");
					e.appendChild(reasonerVerdict);
					Element reasonerAssent = doc.createElement("reasoner-assent");
					Element reasonerDissent = doc.createElement("reasoner-dissent");
					Element reasonerFailure = doc.createElement("reasoner-failure");
					reasonerVerdict.appendChild(reasonerAssent);
					reasonerVerdict.appendChild(reasonerDissent);
					reasonerVerdict.appendChild(reasonerFailure);
					Boolean consensus = true;
					for(ReasonerVerdict rv:scdat.getReasonerVerdicts())
					{
						Element reasoner = doc.createElement("reasoner");
						Attr rname = doc.createAttribute("name");
						rname.setValue(rv.getReasoner());
						reasoner.setAttributeNode(rname);
						if(rv.getValue())
						{
							reasonerAssent.appendChild(reasoner);
						}
						else if(!rv.getValue())
						{
							reasonerDissent.appendChild(reasoner);
							consensus = false;
						}
						else
						{
							reasonerFailure.appendChild(reasoner);
							consensus = false;
						}
					}
					Element genReasoner = doc.createElement("generating-reasoner");
					e.appendChild(genReasoner);
					Element r = doc.createElement("reasoner");
					Attr genName = doc.createAttribute("name");
					r.setAttributeNode(genName);
					genName.setValue(scdat.getGeneratingReasoner());
					Element decision = doc.createElement("decision");
					Attr dec = doc.createAttribute("value");
					decision.setAttributeNode(dec);
					dec.setValue("na");
					e.appendChild(decision);
					Element evidence = doc.createElement("evidence");
					decision.appendChild(evidence);
					if(consensus)
					{
						Element reasonerConsensus = doc.createElement("reasoner-consensus");
						evidence.appendChild(reasonerConsensus);
						for(int i = 0;i < e.getParentNode().getChildNodes().getLength();i++)
						{
							if(e.getParentNode().getChildNodes().item(i).getLocalName().equals("evidence"))
							{
								Element evidenceCase = doc.createElement("reasoner-consensus");
								e.getParentNode().getChildNodes().item(i).appendChild(evidenceCase);
							}
						}
					}
					
					
					Element subcaseMetadata = doc.createElement("subcase-metadata");
					e.appendChild(subcaseMetadata);
					for(String s:scdat.getMetaData().getMetaData())
					{
						Element datatype = doc.createElement("datatype");
						Attr type = doc.createAttribute("type");
						datatype.setAttributeNode(type);
						type.setValue(s);
						e.appendChild(datatype);
					}
					
				}
			}
		}
		
		//writing content to xml file
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(xmlfile);
				
		// Output to console for testing
		transformer.transform(source, result);

		System.out.println("File saved!");
	}
		
	public void createXMLCaseFileFromDis(File dis, File xmlfile, String experiment) throws IOException, TransformerException, OWLOntologyCreationException, ParserConfigurationException, OWLOntologyStorageException, SAXException{
		//check dis non-empty
		if(dis.length() == 0) {
			throw new RuntimeException("Empty file error");
		}
		System.out.println("First test passed, non-empty.");
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("cases");
		doc.appendChild(rootElement);
		
		//Constructing cases dependent on list
		ListManager lm = new ListManager();
		List<Case> cl = lm.constructCaseListFromDisFile(dis);
		//constructing info for relevant attributes. Because this is grouped by ontology, we can get it once!
		BasicFileAttributes attr = Files.readAttributes(dis.toPath(), BasicFileAttributes.class);
		String date = attr.lastModifiedTime().toString().substring(0, attr.lastModifiedTime().toString().indexOf("T"));
		String ontology	= cl.get(0).getOntology().toString();
		
		//Root attributes
		Attr xsd = doc.createAttribute("xmlns:xsi");
		Attr xsi = doc.createAttribute("xsi:noNamespaceSchemaLocation");
		//Attr owl = doc.createAttribute("xmlns:owl");
		Attr grouping = doc.createAttribute("grouping");
		//rootElement.setAttributeNode(xsi);
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(xsd);
		//rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:owl", "http://www.w3.org/2002/07/owl#");
		rootElement.setAttributeNode(grouping);
		xsd.setValue("http://www.w3.org/2001/XMLSchema-instance");
		xsi.setValue("case.xsd");
		
		grouping.setValue("ontology");
		OWLXMLWriter oxwriter = new OWLXMLWriter();
		for(int i = 0;i < cl.size();i++){
			Element xmlcase = doc.createElement("case");
			rootElement.appendChild(xmlcase);
			Attr generationDate = doc.createAttribute("generation-date");
			xmlcase.setAttributeNode(generationDate);
			generationDate.setValue(date);
			Attr experimentAttr = doc.createAttribute("experiment");
			xmlcase.setAttributeNode(experimentAttr);
			experimentAttr.setValue(experiment);
			Attr ontologyAttr = doc.createAttribute("ontology");
			xmlcase.setAttributeNode(ontologyAttr);
			ontologyAttr.setValue(ontology);
			Element entailment = doc.createElement("entailment");
			xmlcase.appendChild(entailment);
			Element n = (Element) oxwriter.getEntailmentAsDocElement((OWLAxiom) cl.get(i).getEntailment(), xmlfile.getParent());
			//Element elementWithNS = doc.createElementNS("http://www.w3.org/2002/07/owl#", n.getNodeName());
			//elementWithNS.setPrefix("owl");
			Element elementWithNS = oxwriter.addOWLNameSpace(n, doc);
			doc.adoptNode(elementWithNS);
			entailment.appendChild(elementWithNS);
			
			//Attr owl = doc.createAttribute("xmlns:owl");
			//owl.setValue("http://www.w3.org/2002/07/owl#");
			//n.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:owl", "http://www.w3.org/2002/07/owl#");
			//n.setPrefix("owl");
			
			//n.setAttributeNode(owl);		
		}
		
		
		//writing content to xml file
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(xmlfile);
		
		// Output to console for testing
		transformer.transform(source, result);

		System.out.println("File saved!");
	}
}
