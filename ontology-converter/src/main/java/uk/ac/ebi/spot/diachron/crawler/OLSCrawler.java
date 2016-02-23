package uk.ac.ebi.spot.diachron.crawler;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.diachron.utils.HttpRequestHandler;
import uk.ac.ebi.spot.diachron.utils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by olgavrou on 16/12/2015.
 */
public class OLSCrawler {

    private Logger log = LoggerFactory.getLogger(getClass());
    private PropertiesManager propertiesManager;
    private Properties properties;
    private String olsApi;
    private String storeArguments;

    public OLSCrawler() {
        this.propertiesManager = PropertiesManager.getPropertiesManager();
        this.properties = propertiesManager.getProperties();
        this.olsApi = this.properties.getProperty("OLS_API");
        this.storeArguments = this.properties.getProperty("Store_Argumets");
    }

    public boolean crawl() {
        try {
            HttpRequestHandler httpRequest = new HttpRequestHandler();
            String jsonResponse = null;


            jsonResponse = httpRequest.executeHttpGet(this.olsApi, null);


            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = null;
            rootNode = mapper.readTree(jsonResponse);

            //JsonNode ontology = mapper.readTree(jsonResponse);

            //ONTOLOGY CRAWLER
            ArrayList<JsonNode> ontologies = new ArrayList<>();
           // ontologies.add(rootNode.get("_embedded").get("ontologies").getElements());
            JsonNode next = rootNode.get("_links").get("first");
            while (next != null) {
                String nextPage = next.get("href").getTextValue();
                try {
                    jsonResponse = httpRequest.executeHttpGet(nextPage, null);
                    rootNode = mapper.readTree(jsonResponse);
                    Iterator iter = rootNode.get("_embedded").get("ontologies").getElements();
                    while (iter.hasNext()){
                        ontologies.add((JsonNode) iter.next());
                    }
                    next = rootNode.get("_links").get("next");
                    System.out.println("Page: " + nextPage);
                } catch (IOException | URISyntaxException | NullPointerException e) {
                    log.info("Couldn't get the page contents from: " + nextPage);
                    log.info(e.toString());
                }
            }

            FileOutputStream outputStream = new FileOutputStream(new File(this.storeArguments + "OntologyList.txt"));

                    for (JsonNode ontology : ontologies)  {
                        if (ontology.get("status").getTextValue().equals("LOADED")) {
                            JsonNode config = ontology.get("config");

                            //get ontology name
                            String namespace = config.get("namespace").getTextValue();
                            outputStream.write(namespace.getBytes());
                            outputStream.write("\n".getBytes());

                        }
                    }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void main (String [] args){
        OLSCrawler crawler = new OLSCrawler();
        crawler.crawl();
    }
}

