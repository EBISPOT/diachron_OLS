package uk.ac.ebi.spot.diachron.crawler;

import org.athena.imis.diachron.archive.datamapping.OntologyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.diachron.utils.DiachronException;
import uk.ac.ebi.spot.diachron.utils.Utils;


import java.io.*;
import java.net.SocketException;
import java.net.URI;
import java.util.Collection;

/**
 * Created by olgavrou on 06/11/2015.
 */
public class DiachronRunner {

    private Logger log = LoggerFactory.getLogger(getClass());

    private String newDatasetUri;
    private String storeChangesArguments;

    public DiachronRunner(String newDatasetUri, String storeChangesArguments) {
        this.newDatasetUri = newDatasetUri;
        this.storeChangesArguments = storeChangesArguments;
    }


    public void convertArchiveAndChangeDetection(String ontologyName, String fileLocation, String version, String oldRecordSetId, File outputDir, Collection<URI> filter, String integrationUrl, String archiveUrls, String changeDetectionUrl) {
        final OLSOntologyRetriever ret = new OLSOntologyRetriever();

        DiachronArchiverService archiveService = null;
        if (integrationUrl != null) {
            archiveService = new DiachronArchiverService(integrationUrl, archiveUrls, changeDetectionUrl);
        }

        OntologyConverter converter = new OntologyConverter();


        version = version.replace("releases/", "");
        version = version.replace("-",".");

        log.info("reading " + ontologyName + " " + version);

        InputStream stream = null;
        stream = ret.getOntology(fileLocation);
        if(stream == null){
            log.info("ERROR: Could not download ontology: " + ontologyName);
            return;
        }

        try {
            File original = new File(outputDir, ontologyName + "-" + version + ".owl");
            File output = new File(outputDir, ontologyName + "-diachronic-" + version + ".owl");
            //if(!original.exists()) {
                FileOutputStream fos = new FileOutputStream(original);
                try {
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = stream.read(bytes)) != -1) {
                        fos.write(bytes, 0, read);
                    }
                } catch (SocketException e ){
                    log.info("ERROR: Could not read: " + ontologyName);
                    log.info(e.toString());
                    return;
                } finally {
                    if (fos != null){
                        fos.close();
                    }
                    if (stream != null){
                        stream.close();
                    }
                }

            //}///else {
            //    if (stream != null){
            //        stream.close();
            //    }
            //}
            log.info("Finished writing " + ontologyName + " " + version);
            log.info("Starting to convert to diachron: " + ontologyName + " " + version);
         //   if(!output.exists()) {
                String reasoner;
                if (ontologyName.contains("EFO")){
                    reasoner = "hermit";
                } else {
                    reasoner = "elk";
                }
                FileInputStream inputStream = null;
                FileOutputStream outputStream = null;
                try {
                    inputStream = new FileInputStream(original);
                    outputStream = new FileOutputStream(output);
                    converter.convert(inputStream, outputStream, ontologyName, filter, reasoner);
                } catch (NullPointerException e ){
                    log.info("ERROR: Could not convert: " + ontologyName);
                    log.info(e.toString());
                    return;
                } finally {
                    if (inputStream != null){
                        inputStream.close();
                    }
                    if (outputStream != null){
                        outputStream.close();
                    }
                }
           // }

            log.info("Finished converting to diachron:  " + ontologyName + " " + version);

            Utils utils = new Utils();
            if (archiveService != null) {
                String datasetId = archiveService.createDiachronicDatasetId(ontologyName, ontologyName, "EMBL-EBI");
                log.info("Archiving dataset " + ontologyName + " with archive id " + datasetId);
                String instanceId = archiveService.archive(output, datasetId, version);
                log.info("Archive successful, instance id = " + instanceId);
                String recordSetId = utils.getLatestDatasetsInfo(archiveUrls, datasetId,"recordSet", instanceId); //archiveService.getVersionId(instanceId); //
                log.info("Recordset id for version " + version + " = " + recordSetId);

                if (oldRecordSetId != null) {
                    log.info("Running change detection between " + recordSetId + " and " + oldRecordSetId);
                    try {
                        archiveService.runChangeDetection(recordSetId, oldRecordSetId, this.newDatasetUri);
                        //Store changes in a file for StoreChanges to use when called
                        FileOutputStream outputStr = new FileOutputStream(new File(this.storeChangesArguments + "ChangesArguments.txt"), true) ;
                        String out = "-n " + ontologyName.toLowerCase() + " -cs " + this.newDatasetUri + " -ov " + oldRecordSetId + " -nv " + recordSetId + " -v " + version;
                        outputStr.write(out.getBytes());
                        outputStr.write("\n".getBytes());
                        outputStr.close();
                        log.info("Wrote change arguments into ChangesArguments.txt file");
                    } catch (RuntimeException | DiachronException e){
                        log.info("Change Detection Fail");
                    }


                    //------------------ save changes to mongodb -----------------------
                    /*DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                    Date date = Calendar.getInstance().getTime();
                    String dateString = df.format(date).toString();
                    StoreChanges storeChanges = new StoreChanges(ontologyName.toLowerCase(),this.newDatasetUri,oldRecordSetId,recordSetId,version,dateString);
                    try {
                        String changes = storeChanges.getChanges();
                        if(changes != null){
                            storeChanges.storeChanges("diachron","change", changes);
                        } else {
                            log.info("No changes found for this ontology");
                        }
                    } catch (IOException e) {
                        log.info(e.toString());
                    } finally {
                        storeChanges.terminate();
                    }*/

                    //-------------------------------------------------------------------
                }
            }
        } catch (IOException | DiachronException e) {
            log.info("ERROR: Could not convert and archive: " + ontologyName);
            log.info(e.toString());
            return;
        }
    }
}

