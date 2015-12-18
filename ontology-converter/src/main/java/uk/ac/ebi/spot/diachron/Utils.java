package uk.ac.ebi.spot.diachron;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by olgavrou on 02/12/2015.
 */
public class Utils {

    private Logger log = LoggerFactory.getLogger(getClass());

    public String getDiachronicDataset(String archiveUrl, String name){
        //returns the diachronic ID of the dataset of the given ontology name
        HashMap<String,String> params = new HashMap<>();
        params.put("name", "listDiachronicDatasets");
        try {
            HttpRequestHandler httpRequest = new HttpRequestHandler();
            String jsonResponse = httpRequest.executeHttpGet(archiveUrl + "/archive/templates/", params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);
            Iterator iter = rootNode.get("data").get("results").get("bindings").getElements();
            String datasetID = null;
            while(iter.hasNext()){
                JsonNode next = (JsonNode) iter.next();
                if(next.get("datasetName").get("value").getTextValue().equals(name)){
                    datasetID = next.get("diachronicDataset").get("value").getTextValue();
                    break;
                }
            }
            if(datasetID == null){
                log.info("Diachronic Dataset with name: " + name + " was not found");
                return null;
            } else {
                return datasetID;
            }
        } catch (NullPointerException | IOException | URISyntaxException e){
            log.info(e.toString());
            return null;
        }
    }

    public String getLatestDatasetsInfo(String archiveUrl, String datasetID, String wantedInfo, String inctanceId) {
        //return the info of the latest dataset that was stored, i.e. the first one on the list (LIFO)
        //datasetId = "http://www.diachron-fp7.eu/resource/dataset/EFO/1449574886029/DE4F45B7656EBFC5377218D7A6D00B43";
        HashMap<String,String> params = new HashMap<>();
        params.put("name", "listDatasets");
        params.put("diachronicDatasetId",datasetID);
        try {
            HttpRequestHandler httpRequest = new HttpRequestHandler();
            String jsonResponse = httpRequest.executeHttpGet(archiveUrl + "/archive/templates", params);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);
            Iterator iter = rootNode.get("data").get("results").get("bindings").getElements();
            while (iter.hasNext()) {
                JsonNode next = (JsonNode) iter.next();
                if (inctanceId != null) {
                    if (next.get("dataset").get("value").getTextValue().equals(inctanceId)) {
                        return next.get(wantedInfo).get("value").getTextValue(); //versionNumber, recordSet, creationTime, dataset
                    }
                } else {
                    return next.get(wantedInfo).get("value").getTextValue();
                }
            }
        }catch (NullPointerException | IOException | URISyntaxException e) {
            log.info("Latest " + wantedInfo + " for this dataset: " + datasetID + " was not found");
            log.info(e.toString());
            return null;
        }
        return null; // if not returned then not found
    }


    public boolean areEqual(ArrayList<String> outer, ArrayList<String> inner){
        boolean containsAll = true;
        if (inner.size() != outer.size()){
            return false;
        }
        for (int i = 0; i < inner.size(); i++){
            if(!outer.contains(inner.get(i))){ // the outer array didn't find an element of the inner
                containsAll = false;
            }
        }
        return containsAll;
    }
}
