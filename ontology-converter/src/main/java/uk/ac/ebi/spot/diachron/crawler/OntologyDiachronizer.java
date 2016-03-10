package uk.ac.ebi.spot.diachron.crawler;

import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.codehaus.jackson.node.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.diachron.changes.ComplexChangesManager;
import uk.ac.ebi.spot.diachron.utils.DiachronException;
import uk.ac.ebi.spot.diachron.utils.HttpRequestHandler;
import uk.ac.ebi.spot.diachron.utils.*;

import java.util.ArrayList;


/**
 * Created by olgavrou on 22/10/2015.
 */
public class OntologyDiachronizer {

    private String integrationLayer;
    private String archiver;
    private String changeDetector;
    private String output;
    private HashMap<String, ArrayList> changeToPropertyMap;
    private String datasetUri;
    private String newDatasetUri;
    private String definedComplexChanges;
    private PropertiesManager propertiesManager;
    private Properties properties;
    private String olsApi;
    private String storeChangesArguments;
    private static String onologyName;
    private Logger log = LoggerFactory.getLogger(getClass());



    public OntologyDiachronizer() {
        this.propertiesManager = PropertiesManager.getPropertiesManager();
        this.properties = propertiesManager.getProperties();

        this.integrationLayer = properties.getProperty("IntegrationLayer");
        this.archiver = properties.getProperty("Archiver");
        this.changeDetector = properties.getProperty("ChangeDetector");

        this.output = properties.getProperty("OutputFolder");
        this.datasetUri = properties.getProperty("Dataset_URI");
        this.definedComplexChanges = properties.getProperty("Complex_Changes");

        this.olsApi = properties.getProperty("OLS_API");
        this.storeChangesArguments = properties.getProperty("Store_Argumets");
        this.changeToPropertyMap = new HashMap<>();// add to the map as the properties are created
    }


    private void diachronizeOntology(String ontologyName) {
        try {
            HttpRequestHandler httpRequest = new HttpRequestHandler();
            String jsonResponse = null;

            jsonResponse = httpRequest.executeHttpGet(this.olsApi + "/" + ontologyName, null);


            ObjectMapper mapper = new ObjectMapper();
          //  JsonNode rootNode = null;
          //  rootNode = mapper.readTree(jsonResponse);

            JsonNode ontology = mapper.readTree(jsonResponse);

            //ONTOLOGY CRAWLER
/*            ArrayList<Iterator> ontologies = new ArrayList<>();
            ontologies.add(rootNode.get("_embedded").get("ontologies").getElements());
            JsonNode next = rootNode.get("_links").get("next");
            while (next != null) {
                String nextPage = next.get("href").getTextValue();
                try {
                    jsonResponse = httpRequest.executeHttpGet(nextPage, null);
                    rootNode = mapper.readTree(jsonResponse);
                    ontologies.add(rootNode.get("_embedded").get("ontologies").getElements());
                    next = rootNode.get("_links").get("next");
                } catch (IOException | URISyntaxException | NullPointerException e) {
                    log.info("Couldn't get the page contents from: " + nextPage);
                    log.info(e.toString());
                }
            }*/

           // for (Iterator iter : ontologies) {
            //    while (iter.hasNext()) {
              //      JsonNode ontology = (JsonNode) iter.next();
                    if (ontology.get("status").getTextValue().equals("LOADED")) {
                        JsonNode config = ontology.get("config");
                        Collection<URI> filter = new HashSet<URI>();
                        ArrayList<String> definitionProperties = new ArrayList<>();
                        ArrayList<String> synonymProperties = new ArrayList<>();
                        ArrayList<String> labelProperty = new ArrayList<>();
                        ArrayList<String> obsolesenceProperty = new ArrayList<>();
                        ArrayList<String> classProperties = new ArrayList<>();

                        //get properties
                        String preferredPrefix = config.get("preferredPrefix").getTextValue();
                        String namespace = config.get("namespace").getTextValue();
                        String fileLocation = config.get("fileLocation").getTextValue();

                        String version = config.get("version").getTextValue();
                        labelProperty.add(config.get("labelProperty").getTextValue());
                        if (!labelProperty.contains("http://www.w3.org/2000/01/rdf-schema#label")) {
                            classProperties.addAll(labelProperty);
                            changeToPropertyMap.put("ADD LABEL", labelProperty);
                            changeToPropertyMap.put("DELETE LABEL", labelProperty);
                        } else {
                            changeToPropertyMap.put("ADD LABEL", new ArrayList()); //empty array list
                            changeToPropertyMap.put("DELETE LABEL", new ArrayList()); // empty array list
                        } // if it contains the rdf label property, we don't define it in adding/deleting label/class


                        Iterator definitions = config.get("definitionProperties").getElements();
                        while (definitions.hasNext()) {
                            String definition = ((TextNode) definitions.next()).getTextValue();
                            definitionProperties.add(definition);
                            filter.add(URI.create(definition));
                        }
                        changeToPropertyMap.put("Add Definition", definitionProperties);
                        changeToPropertyMap.put("Delete Definition", definitionProperties);
                        classProperties.addAll(definitionProperties);


                        Iterator synonyms = config.get("synonymProperties").getElements();
                        while (synonyms.hasNext()) {
                            String synonym = ((TextNode) synonyms.next()).getTextValue();
                            synonymProperties.add(synonym);
                            filter.add(URI.create(synonym));
                            //synonymProperties.add("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym");
                        }
                        changeToPropertyMap.put("Add Synonym", synonymProperties);
                        changeToPropertyMap.put("Delete Synonym", synonymProperties);
                        classProperties.addAll(synonymProperties);


                        for (String label : labelProperty){
                            filter.add(URI.create(label));
                        }
                        if (preferredPrefix.contains("EFO")) {
                            filter.add(URI.create("http://www.ebi.ac.uk/efo/reason_for_obsolescence"));
                            obsolesenceProperty.add("http://www.ebi.ac.uk/efo/reason_for_obsolescence");
                            obsolesenceProperty.add("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass");
                        } else {
                            filter.add(URI.create("http://www.w3.org/2002/07/owl#deprecated"));
                            filter.add(URI.create("http://www.geneontology.org/formats/oboInOwl#consider"));
                            filter.add(URI.create("http://www.geneontology.org/formats/oboInOwl#replacedBy"));
                            obsolesenceProperty.add("http://www.w3.org/2002/07/owl#deprecated");
                            obsolesenceProperty.add("http://www.geneontology.org/formats/oboInOwl#consider");
                            obsolesenceProperty.add("http://www.geneontology.org/formats/oboInOwl#replacedBy");
                        }
                        changeToPropertyMap.put("Mark as Obsolete", obsolesenceProperty);

                        //ADD CLASS and DELETE CLASS
                        changeToPropertyMap.put("ADD CLASS", classProperties);
                        changeToPropertyMap.put("DELETE CLASS", classProperties);

                        log.info("--------------- " + preferredPrefix + " ---------------------");


                        Utils utils = new Utils();

                        this.newDatasetUri = datasetUri + "/" + namespace;
                        DiachronRunner runner = new DiachronRunner(this.newDatasetUri, this.storeChangesArguments);

                        ComplexChangesManager complexChangesManager = new ComplexChangesManager(this.newDatasetUri, this.changeDetector);
                        String[] complexChanges = definedComplexChanges.split(",");

                        String datasetId = utils.getDiachronicDataset(this.archiver, preferredPrefix);

                        if ((datasetId == null)) { // has no data, ontology archive not found, make the first archive of the ontology
                            log.info("First archive.");
                            //Add a new graph for the changes to be stored for this ontology

                            for (String complexChange : complexChanges) {
                                try {
                                    complexChangesManager.manageComplexChange(complexChange, false, definitionProperties, synonymProperties, obsolesenceProperty, labelProperty, newDatasetUri);
                                } catch (IOException | DiachronException e) {
                                    log.info("Error while creating the complex change scheme of: " + preferredPrefix);
                                    log.info(e.toString());
                                    utils.writeInFile(this.storeChangesArguments + "/Report.txt", "System exit 1 for ontology: " + namespace.toUpperCase() + " EXCEPTION: " + e.toString());
                                    //System exit
                                    System.exit(1);
                                }
                            }
                            runner.convertArchiveAndChangeDetection(preferredPrefix, fileLocation, version, null, new File(this.output), filter, this.integrationLayer, this.archiver, this.changeDetector);
                        } else { //the dataset exists. Find out if there is a new one loaded
                            // if a different version was loaded, then we will need to archive the new version and run the change detection
                            log.info("New ontology version.");
                            String latestVersion = null;
                            String latestRecordId = null;

                            latestVersion = utils.getLatestDatasetsInfo(this.archiver, datasetId, "versionNumber", null);
                            log.info("latest version " + latestVersion);
                            latestRecordId = utils.getLatestDatasetsInfo(this.archiver, datasetId, "recordSet", null);
                            log.info("latest record id: " + latestRecordId);

                            version = version.replace("releases/", "");
                            version = version.replace("-", ".");

                            if ((latestVersion == null) || (latestRecordId == null)) {
                                //dataset exists, so the change scheme should exist
                                for (String complexChange : complexChanges) {
                                    try {
                                        complexChangesManager.manageComplexChange(complexChange, true, definitionProperties, synonymProperties, obsolesenceProperty, labelProperty, newDatasetUri);
                                    } catch (IOException | DiachronException e) {
                                        log.info("Error while updating the complex change scheme of: " + preferredPrefix);
                                        log.info(e.toString());
                                        utils.writeInFile(this.storeChangesArguments + "/Report.txt", "System exit 1 for ontology: " + namespace.toUpperCase() + " EXCEPTION: " + e.toString());
                                        //System exit
                                        System.exit(1);
                                    }
                                }
                                log.info("Archiving without defining complex changes test.");
                                runner.convertArchiveAndChangeDetection(preferredPrefix, fileLocation, version, null, new File(this.output), filter, this.integrationLayer, this.archiver, this.changeDetector);
                             //   archived++;
                            } else {
                                if (!latestVersion.equals(version)) {
                                    log.info("unequal versions " + version + " " + latestVersion);
                                    for (String complexChange : complexChanges) {
                                        ArrayList selectionFilters = null;
                                        selectionFilters = complexChangesManager.getSelectionFilters(complexChange);
                                        try {
                                            if (selectionFilters == null) {
                                                complexChangesManager.manageComplexChange(complexChange, false, definitionProperties, synonymProperties, obsolesenceProperty, labelProperty, newDatasetUri);
                                            } else if (!utils.areEqual(selectionFilters, changeToPropertyMap.get(complexChange))) {// if it finds one of the properties, it is fine. If it finds none then the property needs updating
                                                complexChangesManager.manageComplexChange(complexChange, true, definitionProperties, synonymProperties, obsolesenceProperty, labelProperty, newDatasetUri);
                                            }
                                        } catch (IOException | DiachronException e) {
                                            log.info("Error while updating the complex change scheme of: " + preferredPrefix);
                                            log.info(e.toString());
                                            //System exit
                                            utils.writeInFile(this.storeChangesArguments + "/Report.txt", "System exit 1 for ontology: " + namespace.toUpperCase() + " EXCEPTION: " + e.toString());
                                            System.exit(1);
                                        }
                                    }
                                    // Archive and run change detection between the two versions
                                    runner.convertArchiveAndChangeDetection(preferredPrefix, fileLocation, version, latestRecordId, new File(this.output), filter, this.integrationLayer, this.archiver, this.changeDetector);
                                }
                            }
                        }
                    }
              //  }
            //}
        } catch (RuntimeException | IOException | URISyntaxException e){
            Utils utils = new Utils();
            utils.writeInFile(this.storeChangesArguments + "/Report.txt", e.toString());
            e.printStackTrace();
        }
        }

    private static int parseArguments(String[] args) throws IOException {

        CommandLineParser parser = new GnuParser();
        HelpFormatter help = new HelpFormatter();
        Options options = bindOptions();
        int parseArgs = 0;

        try {
            CommandLine cl = parser.parse(options, args, true);

            if (cl.hasOption(""))  {
                // print out mode help
                help.printHelp("diachron", options, true);
                parseArgs += 1;
            } else {
                onologyName = cl.getOptionValue("n");
            }

        } catch (ParseException e) {
            e.printStackTrace();
            parseArgs = 1;
        }

        return parseArgs;
    }

    private static Options bindOptions() {
        Options options = new Options();
        Option ontologyOption = new Option(
                "n",
                "name",
                true,
                "Short name of the ontology e.g. efo");
        ontologyOption.setRequired(true);
        options.addOption(ontologyOption);

        return options;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        try {
            int statusCode = parseArguments(args);

            if (statusCode == 0) {

                if (onologyName != null) {
                    OntologyDiachronizer crawler = new OntologyDiachronizer();
                    crawler.diachronizeOntology(onologyName);
                }
            }
            else {
                System.exit(statusCode);
            }
        }
        catch (IOException e) {
            System.err.println("A read/write problem occurred: " + e.getMessage());
            System.exit(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Diachron archiver did not complete successfully: " + e.getMessage());
            System.exit(1);
        }
    }
}
