package uk.ac.ebi.spot.diachron;

import eu.diachron.ebi.model.DiachronJenaModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Simon Jupp
 * @date 13/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class BioportalOntologyRetriever {

    private static String KEY = "0911d614-1dd4-41c0-afd4-9f3df0fc70be";
    private static String SERVICE = "http://data.bioontology.org/ontologies/";

    public BioportalOntologyRetriever() {

    }

    public Map<String, String> getAllSubmissionId(String ontologyName) {

        Map<String, String> submissionIds = new LinkedHashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL url =  new URL(SERVICE + ontologyName + "/submissions?format=json&apikey=" + KEY);;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            conn.connect();
            InputStream urlInputStream = conn.getInputStream();

            JsonNode root  = mapper.readTree(urlInputStream);
            Iterator<JsonNode> ite = root.getElements();

            while (ite.hasNext()) {
                JsonNode temp = ite.next();
                JsonNode version = temp.path("version");
                JsonNode subId = temp.path("submissionId");
                submissionIds.put(String.valueOf(subId.getIntValue()), version.getTextValue());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return submissionIds;

    }

    public InputStream getOntologyBySubmissionId(String ontologyName, String submissionId)  {

        try {
            URL url =  new URL(SERVICE + ontologyName + "/submissions/" + submissionId + "/download?apikey=" + KEY);;
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

        BioportalOntologyRetriever ret = new BioportalOntologyRetriever();
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
