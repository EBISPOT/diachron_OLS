package uk.ac.ebi.spot.diachron;

import org.athena.imis.diachron.archive.datamapping.OntologyConverter;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simon Jupp
 * @date 18/12/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class AthensOWLToDiachronConverter {
    private Logger log = LoggerFactory.getLogger(getClass());

    public String versionRegexFilter = "\\d*\\.?\\d*";

    public AthensOWLToDiachronConverter() {

    }

    public String getVersionRegexFilter() {
        return versionRegexFilter;
    }

    public void setVersionRegexFilter(String versionRegexFilter) {
        this.versionRegexFilter = versionRegexFilter;
    }

    public void convert(String ontologyName, String apikey, int count, File outputDir) {
        convertAndArchive(ontologyName, apikey, count, outputDir, null, null);
    }

    public void convertAndArchive (String ontologyName, String apikey, int count, File outputDir, String integrationUrl, String archiveUrls) {
        final BioportalOntologyRetriever ret = new BioportalOntologyRetriever(apikey);
        Map<String, String> versionInfo = ret.getAllSubmissionId(ontologyName, count);
        Collection<URI> filter = new HashSet<URI>();
        filter.add(OWLRDFVocabulary.RDFS_LABEL.getIRI().toURI());
        filter.add(URI.create("http://www.ebi.ac.uk/efo/reason_for_obsolescence"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/definition"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/alternative_term"));

        Pattern p = Pattern.compile(getVersionRegexFilter());

        DiachronArchiverService archiveService = null;
        if (integrationUrl != null) {
            archiveService = new DiachronArchiverService(integrationUrl, archiveUrls);
        }

        OntologyConverter converter = new OntologyConverter();
        String newerVersion = null;
        for (final String version :versionInfo.keySet()) {

            Matcher m = p.matcher(version);
            if (m.matches()) {

                final String id = versionInfo.get(version);

                log.info("reading " + ontologyName + " " + version);

                InputStream stream = ret.getOntologyBySubmissionId(ontologyName, id);

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

                    converter.convert(new FileInputStream(original), new FileOutputStream(output), ontologyName, filter);
                    log.info("Finished converting to diachron:  " + ontologyName + " " + version);

                    if (archiveService != null) {

                        try {
                            String datasetId = archiveService.createDiachronicDatasetId(ontologyName, ontologyName, "EMBL-EBI");
                            log.info("Archiving dataset " + ontologyName + " with archive id " + datasetId);
                            String instanceId = archiveService.archive(output, datasetId, version);
                            log.info("Archive successful, instance id = " + instanceId);
                            String recordSetId = archiveService.getVersionId(instanceId);
                            log.info("Recordset id for version " + version + " = " + recordSetId);

                            if (newerVersion != null) {
                                log.info("Running change detection between " + recordSetId + " and " + newerVersion);
                                archiveService.runChangeDetection(newerVersion, recordSetId);
                            }

                            newerVersion = recordSetId;


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
}
