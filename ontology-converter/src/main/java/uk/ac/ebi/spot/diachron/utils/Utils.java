package uk.ac.ebi.spot.diachron.utils;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            JsonNode next = null;
            while(iter.hasNext()){
                next = (JsonNode) iter.next();
                if(next.get("datasetName") != null) {
                    if (next.get("datasetName").get("value").getTextValue().equals(name)) {
                        datasetID = next.get("diachronicDataset").get("value").getTextValue();
                        break;
                    }
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
            throw new RuntimeException("Could not retrieve dataset info from the archiver for: " + name + " . EXCEPTION: " + e.toString());
        }
    }

    /*public String getLatestDatasetsInfo(String archiveUrl, String datasetID, String wantedInfo, String inctanceId) {
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

     */

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
            String latestEntry = null;
            String currentEntry = null;
            Date latestTime = null;
            Date currentTime = null;
            JsonNode next = null;
            String foundWantetInfo = null;
            DateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            while (iter.hasNext()) {
                next = (JsonNode) iter.next();
                if (inctanceId != null) {
                    if(next.get("dataset") != null) {
                        if (next.get("dataset").get("value").getTextValue().equals(inctanceId)) {
                            return next.get(wantedInfo).get("value").getTextValue(); //versionNumber, recordSet, creationTime, dataset
                        }
                    }
                } else {
                    //need to find the last entry
                    if(latestEntry == null){
                        if (next.get("creationTime") != null){
                            latestEntry = next.get("creationTime").get("value").getTextValue();
                        } else {
                            continue;
                        }
                        latestEntry = latestEntry.replace("-",":").replace("T",":").replace("Z","").split("\\.")[0];
                        latestTime = format.parse(latestEntry);
                        foundWantetInfo = next.get(wantedInfo).get("value").getTextValue();
                    } else {
                        if (next.get("creationTime") != null){
                            currentEntry = next.get("creationTime").get("value").getTextValue();
                        } else {
                            continue;
                        }
                        currentEntry = currentEntry.replace("-",":").replace("T",":").replace("Z","").split("\\.")[0];
                        currentTime = format.parse(currentEntry);
                        if(currentTime.after(latestTime)){
                            latestTime = currentTime;
                            foundWantetInfo = next.get(wantedInfo).get("value").getTextValue();
                        }
                    }
                    //return next.get(wantedInfo).get("value").getTextValue();
                }
            }
            return foundWantetInfo;
        }catch (NullPointerException | IOException | URISyntaxException | ParseException e) {
            log.info("Latest " + wantedInfo + " for this dataset: " + datasetID + " was not found");
            log.info(e.toString());
            throw new RuntimeException("Could not retrieve dataset info from the archiver for: " + inctanceId + " . EXCEPTION: " + e.toString());
        }
    }

    public void writeInFile(String filePath, String message){
        FileOutputStream outputStr = null;
        try {
            outputStr = new FileOutputStream(new File(filePath), true);
            outputStr.write(message.getBytes());
            outputStr.write("\n".getBytes());
            outputStr.close();
        } catch (IOException e) {
            log.info(e.toString());
        }
    }

    public String fixReturnedJson(String jsonResponse, String problematicField){
        String [] hold = jsonResponse.split(",");
        ArrayList<String> simpleChanges = new ArrayList();
        for (String s : hold){
            if (s.contains(problematicField)){
                simpleChanges.add(s);
            }
        }
        HashMap<String, String> stringMap = new HashMap<>();
        for (String sc : simpleChanges){
            int count = StringUtils.countMatches(sc, "\"");
            if (count == 2){ // json is returning wrong response, the field returned is not enclosed in quotes (bug in virtuoso server)
                String parts [] = sc.split(":");
                String newString = parts[0] + ":\"" + parts[1] + "\"";
                stringMap.put(sc,newString);
            }
        }

        for (String key : stringMap.keySet()){
            jsonResponse = jsonResponse.replace(key, stringMap.get(key));
        }
        return jsonResponse;
    }

}
