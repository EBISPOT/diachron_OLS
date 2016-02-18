package uk.ac.ebi.spot.diachron.datachanges;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClient;
import org.bson.Document;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.spot.diachron.PropertiesManager;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by olgavrou on 02/02/2016.
 */
public class StoreChanges {

    private String ontologyName;
    private String datasetUri;
    private String oldVersion;
    private String newVersion;
    private String ontologyVersion;
    private Date date;
    private MongoClient mongoClient;



    //TODO: remove date from here
    public StoreChanges(String ontologyName, String datasetUri, String oldVersion, String newVersion, String ontologyVersion, String date) throws UnknownHostException {
        PropertiesManager propertiesManager = PropertiesManager.getPropertiesManager();
        Properties properties = propertiesManager.getProperties();
        String repostioryIP = (String) properties.get("Repository_IP");
        this.ontologyName = ontologyName;
        this.datasetUri = datasetUri;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.ontologyVersion = ontologyVersion;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set((int) Integer.parseInt(date.split("\\.")[0]), (int) Integer.parseInt(date.split("\\.")[1]) - 1 , (int) Integer.parseInt(date.split("\\.")[2]));
        this.date = cal.getTime();
        //this.date = Calendar.getInstance().getTime();

        //initialize mongo client
        this.mongoClient = new MongoClient(repostioryIP, 27017);

    }

    public String getChanges(){
        GetChanges changes = null;
        try {
            changes = new GetChanges(datasetUri,true);
            //TODO: define limit
            Set<DetChangeTest> changeSet = changes.fetchChangesBetweenVersions(oldVersion, newVersion, null, null, 10000000);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(changeSet);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (changes != null){
                changes.terminate();
            }
        }
        return null;
    }

    public Map<String, Integer> storeChanges(String databaseName, String collectionName, String changes) throws IOException {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection collection = database.getCollection(collectionName);
        //-----------
        Map<String, Integer> summary;
        summary = new HashMap<>();
        summary.put("ADD LABEL",0);
        summary.put("DELETE LABEL",0);
        summary.put("ADD CLASS",0);
        summary.put("DELETE CLASS",0);
        summary.put("Mark as Obsolete",0);
        summary.put("Add Synonym",0);
        summary.put("Delete Synonym",0);
        summary.put("Add Definition",0);
        summary.put("Delete Definition",0);
        //-----------
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(changes);
        Iterator<JsonNode> elements = root.getElements();

        String changeName = null;
        String changeSubjectUri = null;
        String prediacte = null;
        String subject = null;
        String subjectValue = null;
        int sum;
        if (elements != null){
            while(elements.hasNext()){
                changeName = null;
                changeSubjectUri = null;
                prediacte = null;
                subject = null;
                subjectValue = null;
                JsonNode change = elements.next();
                if(change.get("changeName") != null) {
                    changeName = change.get("changeName").getTextValue();
                    //Add the change to the summary
                     sum = summary.get(changeName);
                     sum++;
                    summary.put(changeName,sum);
                }
                if(change.get("parameters") != null){
                    Iterator<JsonNode> params = change.get("parameters").getElements();
                    if (params != null){
                        changeSubjectUri = params.next().get("paramValue").getTextValue();
                        if (params.hasNext()){
                            JsonNode param = params.next();
                            if(param.get("paramName") != null){prediacte = param.get("paramName").getTextValue();}
                            subject = prediacte;
                            if(param.get("paramValue") != null){subjectValue = param.get("paramValue").getTextValue();}
                        }
                    }
                }

                Map<String, Collection<String>> propep = new HashMap<>();
                if(prediacte != null && subject != null) {
                    propep.put("predicate", Collections.singleton(((prediacte != null) ? prediacte : "")));
                    propep.put(((subject != null) ? subject : ""), Collections.singleton(((subjectValue != null) ? subjectValue : "")));


                    collection.insertOne(
                            new Document()
                                    .append("changeDate", date)
                                    .append("ontologyName", ontologyName)
                                    .append("changeName", changeName)
                                    .append("changeSubjectUri", changeSubjectUri)
                                    .append("changeProperties", propep)
                    );
                } else {
                    collection.insertOne(
                            new Document()
                                    .append("changeDate", date)
                                    .append("ontologyName", ontologyName)
                                    .append("changeName", changeName)
                                    .append("changeSubjectUri", changeSubjectUri)
                    );
                }
            }
        }
        return summary;
    }

  /*  public void storeSummary(String databaseName, String collectionName, Map<String, Integer> summary) throws IOException {
        DB database = mongoClient.getDB(databaseName);
        DBCollection collection = database.getCollection(collectionName);
        //-----------
        for (String key : summary.keySet()){
            if(summary.get(key) == 0){
                //no changes found for this change name
                continue;
            }
            collection.insert(
                    new BasicDBObject()
                    .append("changeName",key)
                    .append("changeDate",date)
                    .append("ontologyName",ontologyName)
                    .append("version",ontologyVersion)
                    .append("count",summary.get(key))
            );
        }
    }*/

    public void listAllDocuments(String databaseName, String collectionName){
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection collection = database.getCollection(collectionName);
        FindIterable<Document> cursor = collection.find();
        for (Document document : cursor) {
            System.out.println(document);
        }
    }

    public void terminate(){
        if(mongoClient != null){
            mongoClient.close();
        }
    }
/*
    public void play(String databaseName, String collectionName){

        DB db = mongoClient.getDB(databaseName);

        DBCollection coll = db.getCollection(collectionName);

        DBObject query = new BasicDBObject("ontologyName",new BasicDBObject("$eq","efo"));

        //call distinct method by passing the field name and object query
        List dates = coll.distinct("changeDate", query);
        Collections.sort(dates);
        System.out.println(dates);
    }*/

    public static void main(String args[]){
        StoreChanges storeChanges = null;
        try {
            storeChanges = new StoreChanges("efo","http://www.diachron-fp7.eu/efo","http://www.diachron-fp7.eu/resource/recordset/EFO/1450375796150/3F72F2CCB735199E04A627D5AB935296","http://www.diachron-fp7.eu/resource/recordset/EFO/1453310228798/800040A7DD228D68C2BF3CEE9F8EA0CC","2.68","2016.02.04");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //storeChanges.play("diachron","changesummaries");
        try {
            String changes = storeChanges.getChanges();
            if(changes != null){
                Map<String, Integer> summary = storeChanges.storeChanges("diachron","change", changes);
              //  storeChanges.storeSummary("diachron","changesummaries", summary);
              //  storeChanges.listAllDocuments("diachron","change");
               // storeChanges.listAllDocuments("diachron","changesummaries");
            } else {
                System.out.println("No changes found for this ontology");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            storeChanges.terminate();
        }
    }

}
