package main.java.owl.cs.man.ac.uk.test;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import main.java.owl.cs.man.ac.uk.cases.lists.CaseList;
import main.java.owl.cs.man.ac.uk.cases.managers.ListManager;
import main.java.owl.cs.man.ac.uk.cases.writer.XMLCaseWriter;

public final class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, OWLOntologyCreationException {
		// TODO Auto-generated method stub
		File csv = new File(args[0]);
		String xml = args[1];
		XMLCaseWriter xcw = new XMLCaseWriter();
		xcw.createXMLCaseFileFromDis(csv, xml, "iswc");
		}
}


