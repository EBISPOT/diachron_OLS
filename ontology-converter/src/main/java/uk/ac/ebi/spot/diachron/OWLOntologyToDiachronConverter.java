package uk.ac.ebi.spot.diachron;

import eu.diachron.ebi.model.DiachronDataset;
import eu.diachron.ebi.model.DiachronJenaModel;
import org.coode.owlapi.rdf.model.RDFGraph;
import org.coode.owlapi.rdf.model.RDFLiteralNode;
import org.coode.owlapi.rdf.model.RDFResourceNode;
import org.coode.owlapi.rdf.model.RDFTriple;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredEntityAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.*;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class OWLOntologyToDiachronConverter {

    private OWLOntologyManager manager;
    private DiachronDataset dataset;

    public OWLOntologyToDiachronConverter (InputStream ontologyFile, String name, String version, Collection<URI> predicateFilters) {


        try {
            this.manager = OWLManager.createOWLOntologyManager();
            // load ontology into OWLAPI
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);

            // create a reasoner factory and classify ontology
            Reasoner.ReasonerFactory owlReasonerFactory = new Reasoner.ReasonerFactory();
            OWLReasoner owlReasoner = owlReasonerFactory.createReasoner(ontology);

            OWLOntology reasonedOntology = owlReasoner.getRootOntology();

            FilteredRDFVisitor visitor = new FilteredRDFVisitor(manager, reasonedOntology, true, predicateFilters);

            InferredSubClassAxiomGenerator inferredAxioms = new InferredSubClassAxiomGenerator();
            Set<OWLSubClassOfAxiom> subClasses = inferredAxioms.createAxioms(manager, owlReasoner);

            for (OWLSubClassOfAxiom subClass : subClasses)
                visitor.visit(subClass);


            for (OWLClass entity : reasonedOntology.getClassesInSignature()) {

                for (OWLDeclarationAxiom declarationAxiom: reasonedOntology.getDeclarationAxioms(entity))
                    visitor.visit(declarationAxiom);

                for (OWLAnnotationAssertionAxiom annotation : reasonedOntology.getAnnotationAssertionAxioms(entity.getIRI()))
                    visitor.visit(annotation);

            }

            // create a new diachronic dataset based on this ontology instance
            URI ontologyUri = ontology.getOntologyID().getOntologyIRI().toURI();
            String ontologyUriAsString = ontologyUri.toString();
            if (ontologyUriAsString.endsWith("/")) {
                ontologyUriAsString = ontologyUriAsString.substring(0, ontologyUriAsString.lastIndexOf("/"));
            }
            this.dataset = new DiachronDataset(URI.create(ontologyUriAsString), name, version);

            RDFGraph graph = visitor.getGraph();
//            int limit = 10;
//            int x = 0;
            for (OWLClass entity : reasonedOntology.getClassesInSignature()) {

//                if (x < limit) {
//                    x++;
//                }
//                else {
//                    break;
//                }

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
        filter.add(URI.create("http://www.ebi.ac.uk/efo/reason_for_obsolescence"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/definition"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/alternative_term"));


        File inputFolder  = new File("/Users/jupp/tmp/diachron/efo-last-15-owl");

        String regex = "efo-(\\d\\.\\d+).owl";
        Pattern pattern = Pattern.compile(regex);
        for (File file : inputFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("37.owl");
            }
        })) {

            String fileName = file.getName();
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find())  {

                String id = matcher.group(1);
                System.out.println(id);

                OWLOntologyToDiachronConverter converter = null;
                try {
                    converter = new OWLOntologyToDiachronConverter(
                            new BufferedInputStream(
                                    new FileInputStream(file)),
                            "efo",
                            id,
                            filter );
                    DiachronJenaModel model = new DiachronJenaModel(converter.getDiachronDataset());
                    try {
//                        model.save(new File("/Users/jupp/tmp/diachron/diachron-efo-" + id + ".rdf.xml"), "RDF/XML");
//                        model.save(new File("/Users/jupp/tmp/diachron/diachron-efo-" + id + ".rdf.ttl"), "TURTLE");
                        model.save(new File("/Users/jupp/tmp/diachron/diachron-efo-" + id + ".rdf.n3"), "N3");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }





    }

}
