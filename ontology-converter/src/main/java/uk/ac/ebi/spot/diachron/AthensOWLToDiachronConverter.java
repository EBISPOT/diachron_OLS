package uk.ac.ebi.spot.diachron;

import org.apache.commons.cli.*;
import org.athena.imis.diachron.archive.datamapping.OntologyConverter;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Simon Jupp
 * @date 18/12/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class AthensOWLToDiachronConverter {
    private Logger log = LoggerFactory.getLogger(getClass());

    public AthensOWLToDiachronConverter(String ontologyName, String apikey, int count, File outputDir ) {

        final BioportalOntologyRetriever ret = new BioportalOntologyRetriever(apikey);
        Map<String, String> versionInfo = ret.getAllSubmissionId(ontologyName, count);

        Collection<URI> filter = new HashSet<URI>();
        filter.add(OWLRDFVocabulary.RDFS_LABEL.getIRI().toURI());
        filter.add(URI.create("http://www.ebi.ac.uk/efo/reason_for_obsolescence"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/definition"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/alternative_term"));

        OntologyConverter converter = new OntologyConverter();
        for (final String id :versionInfo.keySet()) {

            final String version = versionInfo.get(id);

            log.info("reading " + ontologyName + " " + version);

            InputStream stream  = ret.getOntologyBySubmissionId(ontologyName, id);

            try {

                File original = new File(outputDir, ontologyName + "-" + version + ".owl");
                File output = new File(outputDir, ontologyName + "-diachronic-" + version + ".owl");
                FileOutputStream fos = new FileOutputStream(original);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = stream.read(bytes)) != -1) {
                    fos.write(bytes, 0, read);
                }
               log.info("Finished writing " + ontologyName + " " + version);

                converter.convert(new FileInputStream(original), new FileOutputStream(output), ontologyName,filter );


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
