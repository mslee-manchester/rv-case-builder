package main.java.owl.cs.man.ac.uk.cases.writer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.xml.sax.SAXException;

import main.java.owl.cs.man.ac.uk.cases.lists.Case;
import main.java.owl.cs.man.ac.uk.cases.lists.CaseData;
import main.java.owl.cs.man.ac.uk.cases.lists.CaseList;
import main.java.owl.cs.man.ac.uk.cases.lists.ReasonerVerdict;
import main.java.owl.cs.man.ac.uk.cases.lists.Subcase;
import main.java.owl.cs.man.ac.uk.cases.lists.SubcaseData;
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
	
	public void createXMLCaseFileFromList(File csv, File xmlfile, List<Case> list, String experiment, String ontology) throws OWLOntologyCreationException, IOException, ParserConfigurationException, OWLOntologyStorageException, SAXException, TransformerException{
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
		ArrayList<CaseData> cld = lm.constructCaseDataFromCSV(list, csv);
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
					Element r = doc.createElement("reasoner");
					Attr genName = doc.createAttribute("name");
					r.setAttributeNode(genName);
					genName.setValue(scdat.getGeneratingReasoner());
					genReasoner.appendChild(r);
					e.appendChild(genReasoner);
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
						/**
						for(int i = 0;i < e.getParentNode().getChildNodes().getLength();i++)
						{
							if(e.getParentNode().getChildNodes().item(i).getLocalName().equals("evidence"))
							{
								Element evidenceCase = doc.createElement("reasoner-consensus");
								e.getParentNode().getChildNodes().item(i).appendChild(evidenceCase);
							
							}
						}
						**/
					}
					for(String s:scdat.getEvidence().getMetaData())
					{
						if(s.equals("Axiom Swallowing"))
						{
							Element axswal = doc.createElement("self-just");
							evidence.appendChild(axswal);
							dec.setValue("true");
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
