package uk.ac.ebi.spot.diachron;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import eu.diachron.ebi.model.DiachronDataset;
import eu.diachron.ebi.model.DiachronFactory;
import eu.diachron.ebi.model.DiachronJenaModel;
import org.coode.owlapi.rdf.model.RDFGraph;
import org.coode.owlapi.rdf.model.RDFLiteralNode;
import org.coode.owlapi.rdf.model.RDFResourceNode;
import org.coode.owlapi.rdf.model.RDFTriple;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class OWLOntologyToDiachronConverter {

    private OWLOntologyManager manager;
    private DiachronDataset dataset;

    public OWLOntologyToDiachronConverter (File ontologyFile, String name, String version, Collection<URI> predicateFilters) {


        try {
            this.manager = OWLManager.createOWLOntologyManager();
            // load ontology into OWLAPI
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);

            // create a reasoner factory and classify ontology
            Reasoner.ReasonerFactory owlReasonerFactory = new Reasoner.ReasonerFactory();
            OWLReasoner owlReasoner = owlReasonerFactory.createReasoner(ontology);

            OWLOntology reasonedOntology = owlReasoner.getRootOntology();
            FilteredRDFVisitor visitor = new FilteredRDFVisitor(manager, reasonedOntology, true, predicateFilters);

            for (OWLClass entity : reasonedOntology.getClassesInSignature()) {

                for (OWLDeclarationAxiom declarationAxiom: reasonedOntology.getDeclarationAxioms(entity))
                    visitor.visit(declarationAxiom);

                for (OWLSubClassOfAxiom subClasses : reasonedOntology.getSubClassAxiomsForSubClass(entity))
                    visitor.visit(subClasses);

                for (OWLAnnotationAssertionAxiom annotation : reasonedOntology.getAnnotationAssertionAxioms(entity.getIRI()))
                    visitor.visit(annotation);

            }

            // create a new diachronic dataset based on this ontology instance
            this.dataset = new DiachronDataset(ontology.getOntologyID().getOntologyIRI().toURI(), name, version);

            RDFGraph graph = visitor.getGraph();
            int limit = 10;
            int x = 0;
            for (OWLClass entity : reasonedOntology.getClassesInSignature()) {

                if (x < limit) {
                    x++;
                }
                else {
                    break;
                }

                System.out.println("Triples for " + entity.getIRI().toURI().toString());

                for (RDFTriple triple : graph.getTriplesForSubject(new RDFResourceNode(entity.getIRI()), true)) {

                    System.out.println("\t" + triple.toString());

                    if (triple.getObject().isLiteral()) {
                        dataset.addRecord(
                                triple.getSubject().getIRI().toURI(),
                                triple.getProperty().getIRI().toURI(),
                                ((RDFLiteralNode)triple.getObject()).getLiteral());
                    }
                    else {
                        dataset.addRecord(
                                triple.getSubject().getIRI().toURI(),
                                triple.getProperty().getIRI().toURI(),
                                triple.getObject().getIRI().toURI());
                    }
                }
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

    public DiachronDataset getDiachronDataset() {
        return dataset;
    }

    public static void main(String[] args) {

        Collection<URI> filter = new HashSet<URI>();
        filter.add(OWLRDFVocabulary.RDFS_LABEL.getIRI().toURI());
        OWLOntologyToDiachronConverter converter = new OWLOntologyToDiachronConverter(new File("/Users/jupp/dev/ontology_dev/efo/svn/ExFactorInOWL/currentrelease/eforelease/efo.owl"), "efo", "2.44", filter );

        DiachronJenaModel model = new DiachronJenaModel(converter.getDiachronDataset());
        try {
            model.save(new File("/Users/jupp/tmp/diachron/diachron-efo-2.44.rdf.xml"), "RDF/XML");
            model.save(new File("/Users/jupp/tmp/diachron/diachron-efo-2.44.rdf.n3"), "N3");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
