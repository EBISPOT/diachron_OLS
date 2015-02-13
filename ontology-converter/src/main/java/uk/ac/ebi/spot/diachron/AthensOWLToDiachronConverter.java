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

    public AthensOWLToDiachronConverter() {


    }

    public void convert(String ontologyName, String apikey, int count, File outputDir) {
        convertAndArchive(ontologyName, apikey, count, outputDir, null);
    }

    public void convertAndArchive (String ontologyName, String apikey, int count, File outputDir, String archiveUrl) {
        final BioportalOntologyRetriever ret = new BioportalOntologyRetriever(apikey);
        Map<String, String> versionInfo = ret.getAllSubmissionId(ontologyName, count);
        Collection<URI> filter = new HashSet<URI>();
        filter.add(OWLRDFVocabulary.RDFS_LABEL.getIRI().toURI());
        filter.add(URI.create("http://www.ebi.ac.uk/efo/reason_for_obsolescence"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/definition"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/alternative_term"));

        DiachronArchiverService archiveService = null;
        if (archiveUrl != null) {
            archiveService = new DiachronArchiverService(archiveUrl);
        }

        OntologyConverter converter = new OntologyConverter();
        String previousVersion = null;
        for (final String version :versionInfo.keySet()) {

            final String id = versionInfo.get(version);

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
                log.info("Starting to convert to diachron: " + ontologyName + " " + version);

               converter.convert(new FileInputStream(original), new FileOutputStream(output), ontologyName,filter );
                log.info("Finished converting to diachron:  " + ontologyName + " " + version);

               if (archiveService != null) {

                   try {
                       String datasetId = archiveService.createDiachronicDatasetId(ontologyName, ontologyName, "EMBL-EBI");
                       log.info("Archiving dataset " + ontologyName + " with archive id " + datasetId);
                       String instanceId = archiveService.archive(output, datasetId);
                       log.info("Archive successful, instance id = " +instanceId);
                       String recordSetId = archiveService.getVersionId(instanceId);
                       log.info("Recordset id for version " + version + " = "  + recordSetId);

                       if (previousVersion != null) {
                           log.info("Running change detection between " + recordSetId + " and "  + previousVersion);
                           archiveService.runChangeDetection(recordSetId, previousVersion);
                       }

                       previousVersion = recordSetId;


                   } catch (DiachronException e) {
                       e.printStackTrace();
                   }

               }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
