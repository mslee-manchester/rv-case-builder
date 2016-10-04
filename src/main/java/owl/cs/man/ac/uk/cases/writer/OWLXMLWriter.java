package main.java.owl.cs.man.ac.uk.cases.writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OWLXMLWriter {
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Transformer transformer;
	
	
	public OWLXMLWriter() throws ParserConfigurationException, TransformerConfigurationException {
		//Produces the relevant nodes for the document being constructed. To be distingushed from the OWL API OWLXMLWriter
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docBuilder = docFactory.newDocumentBuilder();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		this.transformer = transformerFactory.newTransformer();
	}
	
	public Node getEntailmentAsDocElement(OWLAxiom ax, String temp) throws OWLOntologyCreationException, OWLOntologyStorageException, SAXException, IOException{
		OWLOntologyManager ontoman = OWLManager.createOWLOntologyManager();
		Set<OWLAxiom> axset = new HashSet<OWLAxiom>();
		axset.add(ax);
		File tempFile = File.createTempFile("temp", ".owl", new File(temp));
		OWLOntology axont = ontoman.createOntology(axset);
		ontoman.saveOntology(axont, new OWLXMLOntologyFormat(), IRI.create(tempFile));
		Document doc = docBuilder.parse(tempFile);
		tempFile.deleteOnExit();
		return doc.getFirstChild().getLastChild().getPreviousSibling();
	}
}
