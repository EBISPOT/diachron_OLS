package uk.ac.ebi.spot.diachron;

import org.athena.imis.diachron.archive.datamapping.OntologyConverter;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simon Jupp
 * @date 18/12/2014
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class AthensOWLToDiachronConverter {
    private Logger log = LoggerFactory.getLogger(getClass());

    public String versionRegexFilter = "*";
    private String integrationLayer;
    private String archiver;
    private String changeDetector;
    private String output;
    private String datasetUri;
    private PropertiesManager propertiesManager;
    private Properties properties;

    public AthensOWLToDiachronConverter() {
        this.propertiesManager = PropertiesManager.getPropertiesManager();
        this.properties = propertiesManager.getProperties();

        this.integrationLayer = properties.getProperty("IntegrationLayer");
        this.archiver = properties.getProperty("Archiver");
        this.changeDetector = properties.getProperty("ChangeDetector");

        this.output = properties.getProperty("OutputFolder");
        this.datasetUri = properties.getProperty("Dataset_URI");

    }

    public String getVersionRegexFilter() {
        return versionRegexFilter;
    }

    public void setVersionRegexFilter(String versionRegexFilter) {
        this.versionRegexFilter = versionRegexFilter;
    }

    public void convert(String ontologyName, String apikey, int count, File outputDir) {
       // convertAndArchive(ontologyName, apikey, count, outputDir, null, null);
    }

    public void convertAndArchive (String ontologyName, String apikey, int count) {
        final BioportalOntologyRetriever ret = new BioportalOntologyRetriever(apikey);
        Map<String, String> versionInfo = ret.getAllSubmissionId(ontologyName, count);
        Collection<URI> filter = new HashSet<URI>();
        filter.add(OWLRDFVocabulary.RDFS_LABEL.getIRI().toURI());
       // filter.add(URI.create("http://www.w3.org/2004/02/skos/core#prefLabel"));
       // filter.add(URI.create("http://www.w3.org/2002/07/owl#deprecated"));
       // filter.add(URI.create("http://www.w3.org/2004/02/skos/core#definition"));
       // filter.add(URI.create("http://www.w3.org/2004/02/skos/core#altLabel"));
   //     filter.add(URI.create("http://www.w3.org/2002/07/owl#deprecated"));
   //     filter.add(URI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
   //     filter.add(URI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/reason_for_obsolescence"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/definition"));
        filter.add(URI.create("http://www.ebi.ac.uk/efo/alternative_term"));

        Pattern p = Pattern.compile(getVersionRegexFilter());

        DiachronArchiverService archiveService = null;
        if (this.integrationLayer != null) {
            archiveService = new DiachronArchiverService(this.integrationLayer, this.archiver, this.changeDetector);
        }

        OntologyConverter converter = new OntologyConverter();
        String newerVersion = null;
        for (final String version :versionInfo.keySet()) {

            Matcher m = p.matcher(version);
            if (m.matches()) {

                String versionTest = version.replace("releases/","");
                versionTest = versionTest.replace("-",".");

                final String id = versionInfo.get(version);

                log.info("reading " + ontologyName + " " + version);

                InputStream stream = ret.getOntologyBySubmissionId(ontologyName, id);

                try {

                    File original = new File(this.output, ontologyName + "-" + versionTest + ".owl");
                    File output = new File(this.output, ontologyName + "-diachronic-" + versionTest + ".owl");
                    if(!original.exists()) {
                        FileOutputStream fos = new FileOutputStream(original);

                        int read = 0;
                        byte[] bytes = new byte[1024];

                        while ((read = stream.read(bytes)) != -1) {
                            fos.write(bytes, 0, read);
                        }
                    }
                    log.info("Finished writing " + ontologyName + " " + version);
                    log.info("Starting to convert to diachron: " + ontologyName + " " + version);

                    if(!output.exists()) {
                        String reasoner;
                        if (!output.exists()) {

                            if (ontologyName.contains("EFO")) {
                                reasoner = "hermit";
                            } else {
                                reasoner = "elk";
                            }
                            converter.convert(new FileInputStream(original), new FileOutputStream(output), ontologyName, filter, reasoner);
                        }
                    }

                    log.info("Finished converting to diachron:  " + ontologyName + " " + version);

                    if (archiveService != null) {

                        try {
                            String datasetId = archiveService.createDiachronicDatasetId(ontologyName, ontologyName, "EMBL-EBI");
                            log.info("Archiving dataset " + ontologyName + " with archive id " + datasetId);
                            String instanceId = archiveService.archive(output, datasetId, version);

                            log.info("Archive successful, instance id = " + instanceId);
                            Utils utils = new Utils();
                            String recordSetId =  utils.getLatestDatasetsInfo(this.archiver, datasetId,"recordSet", instanceId); //archiveService.getVersionId(instanceId);
                            // String recordSetId = archiveService.getVersionId(instanceId);
                            log.info("Recordset id for version " + version + " = " + recordSetId);

                            if (newerVersion != null) {
                                log.info("Running change detection between " + recordSetId + " and " + newerVersion);
                                archiveService.runChangeDetection(newerVersion, recordSetId, this.datasetUri + ontologyName.toLowerCase());
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
