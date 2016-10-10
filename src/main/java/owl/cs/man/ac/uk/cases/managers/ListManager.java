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
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import main.java.owl.cs.man.ac.uk.cases.lists.Case;
import main.java.owl.cs.man.ac.uk.cases.lists.CaseList;

public class ListManager {

	private OWLOntologyManager ontoman;
	private OWLDataFactory df;
	public ListManager() {
		this.ontoman = OWLManager.createOWLOntologyManager();
		this.df = ontoman.getOWLDataFactory();
	}
	
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
				Case c = new Case(ontology, ax.getAxiomWithoutAnnotations());
				caselist.add(c);
			}
		}
		return caselist;
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
	
	public ArrayList<String> getCaseListMetaData(List<Case> list, File csv){
		
		return null;
	}
	
	public Boolean lineContainsCase(String s, Case c){
		return s.contains(c.getOntology().toString()) && s.contains(c.getEntailment().toString());
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
