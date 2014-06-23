package eu.diachron.ebi.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class DiachronJenaModel {

    private Model model;
    private DiachronDataset dataset;

    public  DiachronJenaModel (DiachronDataset dataset) {
        this.model = ModelFactory.createDefaultModel();
        this.dataset = dataset;
    }

    public Model getJenaModelFromDataset() {

        // type the properties (only needed to stop them being treated as annotation props in Protege)
//        model.add(new ResourceImpl(DiachronVocabulary.OBJECT.getURI().toString()), RDF.type, OWL.DatatypeProperty);
//        model.add(new ResourceImpl(DiachronVocabulary.SUBJECT.getURI().toString()), RDF.type, OWL.ObjectProperty);
//        model.add(new ResourceImpl(DiachronVocabulary.PREDICATE.getURI().toString()), RDF.type, OWL.ObjectProperty);
//        model.add(new ResourceImpl(DiachronVocabulary.HASRECORDATTRIBUTE.getURI().toString()), RDF.type, OWL.ObjectProperty);
//        model.add(new ResourceImpl(DiachronVocabulary.HASINSTANTIATION.getURI().toString()), RDF.type, OWL.ObjectProperty);
//        model.add(new ResourceImpl(DiachronVocabulary.HASPART.getURI().toString()), RDF.type, OWL.ObjectProperty);
//        model.add(new ResourceImpl(DiachronVocabulary.HASRECORD.getURI().toString()), RDF.type, OWL.ObjectProperty);
//        model.add(new ResourceImpl(DiachronVocabulary.HASRECORDSET.getURI().toString()), RDF.type, OWL.ObjectProperty);
//        model.add(new ResourceImpl(DiachronVocabulary.HASSCHEMASET.getURI().toString()), RDF.type, OWL.ObjectProperty);

        // create the diachron dataset
        Statement s1 = new StatementImpl(new ResourceImpl(dataset.getDatasetURI().toString()),
                RDF.type,
                new ResourceImpl(DiachronVocabulary.DIACHRONICDATASET.getURI().toString()));


        // create the dataset
        Statement s2 = new StatementImpl(new ResourceImpl(dataset.getDiachronDatasetURI().toString()),
                RDF.type,
                new ResourceImpl(DiachronVocabulary.DATASET.getURI().toString()));

        model.add(s1);
        model.add(s2);
        model.add(s1.getSubject(), new PropertyImpl(DiachronVocabulary.HASINSTANTIATION.getURI().toString()), s2.getSubject());
        model.add(s2.getSubject(), DC.creator, "EBI");
        model.add(s2.getSubject(), DC.title, "EFO Ontology");

        // create the dataset
        Statement s3 = new StatementImpl(
                new ResourceImpl(dataset.getDiachronRecordSetURI().toString()),
                RDF.type,
                new ResourceImpl(DiachronVocabulary.RECORDSET.getURI().toString()));

        model.add(s3);
        model.add(s2.getSubject(), new PropertyImpl(DiachronVocabulary.HASRECORDSET.getURI().toString()), s3.getSubject());

        for (DiachronRecord record : dataset.getRecords()) {

            Statement s4 = new StatementImpl(
                    new ResourceImpl(record.getUri().toString()),
                    RDF.type,
                    new ResourceImpl(DiachronVocabulary.RECORD.getURI().toString()));
            model.add(s4);
            model.add(s3.getSubject(), new PropertyImpl(DiachronVocabulary.HASRECORD.getURI().toString()), s4.getSubject());
            model.add(s4.getSubject(), new PropertyImpl(DiachronVocabulary.SUBJECT.getURI().toString()), new ResourceImpl(record.getSubject().toString()));

            for (DiachronRecordAttribute attribute : record.getAttributes()) {

                Statement s5 = new StatementImpl(
                        new ResourceImpl(attribute.getUri().toString()),
                        RDF.type,
                        new ResourceImpl(DiachronVocabulary.RECORDATTRIBUTE.getURI().toString()));
                model.add(s5);
                model.add(s4.getSubject(), new PropertyImpl(DiachronVocabulary.HASRECORDATTRIBUTE.getURI().toString()), s5.getSubject());

                model.add(s5.getSubject(), new PropertyImpl(DiachronVocabulary.PREDICATE.getURI().toString()), new ResourceImpl(attribute.getPropertyName().toString()));

                if (attribute instanceof ResourceAttribute) {
                    model.add(s5.getSubject(), new PropertyImpl(DiachronVocabulary.OBJECT.getURI().toString()), new ResourceImpl(((ResourceAttribute) attribute).getObject().toString()));
                }
                else if (attribute instanceof LiteralAttribute) {
                    model.add(s5.getSubject(), new PropertyImpl(DiachronVocabulary.OBJECT.getURI().toString()), ((LiteralAttribute)attribute).getValue());
                }
                else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        return model;
    }

    public void save(File file, String format) throws FileNotFoundException {
        getJenaModelFromDataset().write(new BufferedOutputStream(new FileOutputStream(file)), format);
    }
}
