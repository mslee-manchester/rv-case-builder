package main.java.owl.cs.man.ac.uk.cases.writer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

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
import main.java.owl.cs.man.ac.uk.cases.lists.CaseList;
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
