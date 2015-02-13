package uk.ac.ebi.spot.diachron;

import eu.diachron.ebi.model.DiachronJenaModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Simon Jupp
 * @date 13/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class BioportalOntologyRetriever {

    private String apikey;
    private static String SERVICE = "http://data.bioontology.org/ontologies/";
    private Logger log = LoggerFactory.getLogger(getClass());

    public BioportalOntologyRetriever(String apikey) {
        this.apikey = apikey;
    }

    public Map<String, String> getAllSubmissionId(String ontologyName, int count) {
        Map<String, String> submissionIds = new LinkedHashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL url =  new URL(SERVICE + ontologyName + "/submissions?format=json&apikey=" + apikey);
            log.info(url.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            conn.connect();
            InputStream urlInputStream = conn.getInputStream();

            JsonNode root  = mapper.readTree(urlInputStream);
            Iterator<JsonNode> ite = root.getElements();

            long counter = 0;
            while (ite.hasNext()) {
                JsonNode temp = ite.next();
                JsonNode version = temp.path("version");
                JsonNode subId = temp.path("submissionId");

                if (count == -1 || counter < count) {
                    // handle cases where bioportal has multiple version of the same revision
                    if (!submissionIds.containsKey(version.getTextValue())) {
                        submissionIds.put(version.getTextValue(), String.valueOf(subId.getIntValue()));
                        counter++;
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return submissionIds;
    }

    public Map<String, String> getAllSubmissionId(String ontologyName) {
        return getAllSubmissionId(ontologyName, -1);
    }

    public InputStream getOntologyBySubmissionId(String ontologyName, String submissionId)  {

        try {
            URL url =  new URL(SERVICE + ontologyName + "/submissions/" + submissionId + "/download?apikey=" + apikey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/html");

            conn.connect();
            return conn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {

        BioportalOntologyRetriever ret = new BioportalOntologyRetriever(args[0]);
        Map<String, String> versionInfo = ret.getAllSubmissionId("EFO");

        Collection<URI> filter = new HashSet<URI>();
        filter.add(OWLRDFVocabulary.RDFS_LABEL.getIRI().toURI());

        for (String id :versionInfo.keySet()) {

            String version = versionInfo.get(id);

            OWLOntologyToDiachronConverter converter = new OWLOntologyToDiachronConverter(ret.getOntologyBySubmissionId("EFO", id), "efo", version, filter );

            DiachronJenaModel model = new DiachronJenaModel(converter.getDiachronDataset());
            try {
                model.save(new File("/Users/jupp/tmp/diachron/diachron-efo-" + version + ".rdf.xml"), "RDF/XML");
                model.save(new File("/Users/jupp/tmp/diachron/diachron-efo-" + version + ".rdf.n3"), "N3");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
