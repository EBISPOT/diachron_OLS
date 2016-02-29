package uk.ac.ebi.spot.diachron.storechanges;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClient;
import org.bson.Document;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.spot.diachron.utils.*;

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
    private String databaseName;
    private String collectionName;
    private String storeArgumentsPath;


    //TODO: remove date from here
    public StoreChanges(String ontologyName, String datasetUri, String oldVersion, String newVersion, String ontologyVersion, String dateString) throws UnknownHostException {
        PropertiesManager propertiesManager = PropertiesManager.getPropertiesManager();
        Properties properties = propertiesManager.getProperties();
        String mongoHostIP = (String) properties.get("MongoHost_IP");
        String mongoPort = (String) properties.get("Mongo_Port");
        this.databaseName = (String) properties.get("Diachron_DB");
        this.collectionName = (String) properties.get("Diachron_Collection");
        this.storeArgumentsPath = (String) properties.get("Store_Argumets");
        this.ontologyName = ontologyName;
        this.datasetUri = datasetUri;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.ontologyVersion = ontologyVersion;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set((int) Integer.parseInt(dateString.split("\\.")[0]), (int) Integer.parseInt(dateString.split("\\.")[1]) - 1 , (int) Integer.parseInt(dateString.split("\\.")[2]));
        this.date = cal.getTime();
        int t = Integer.parseInt(mongoPort);

        //initialize mongo client
        this.mongoClient = new MongoClient(mongoHostIP, t);
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
            String ex = e.toString();
            System.out.println(ex);
            if (ex.contains("Connection refused") || ex.contains("Connection failed") || ex.contains("VirtuosoException")){
                Utils utils = new Utils();
                utils.writeInFile(this.storeArgumentsPath + "/Report.txt", "VIRTUOSO SERVER IS DOWN, need to restart and then execute StoreAllChanges.sh. \n IMPORTANT: If any changes have been already loaded, delete them from mongodb first and then run StoreAllChanges.sh");
                //exit with status 1 so the server unavailability is reported
                System.exit(1);
            }
        } finally {
            if (changes != null){
                changes.terminate();
            }
        }
        return null;
    }

    public void storeChanges(String changes) throws IOException {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection collection = database.getCollection(collectionName);
        //-----------
        // for each ADD CLASS and DELETE CLASS there is a hash map
        // each changeUri is mapped with the properties (labels, synonyms, definitions) that it comes with
        //so they can be displayed in one change on mongodb
        Map<String, Map<String, Map<String, Collection<String>>>> summary;
        summary = new HashMap<>();


        summary.put("ADD CLASS", new HashMap<String, Map<String, Collection<String>>>());
        summary.put("DELETE CLASS", new HashMap<String, Map<String, Collection<String>>>());
        //-----------
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(changes);
        Iterator<JsonNode> elements = root.getElements();

        String changeName = null;
        String changeSubjectUri = null;
        ArrayList<String> prediacte = new ArrayList<>();
        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> subjectValue = new ArrayList<>();

        if (elements != null) {
            while (elements.hasNext()) {
                changeName = null;
                changeSubjectUri = null;
                prediacte.clear();
                subject.clear();
                subjectValue.clear();
                JsonNode change = elements.next();
                if (change.get("changeName") != null) {
                    changeName = change.get("changeName").getTextValue();

                    if (change.get("parameters") != null) {
                        Iterator<JsonNode> params = change.get("parameters").getElements();
                        if (params != null) {
                            changeSubjectUri = params.next().get("paramValue").getTextValue();
                            if (changeName.equals("ADD CLASS") | changeName.equals("DELETE CLASS")) {
                                Map<String, Map<String, Collection<String>>> map = summary.get(changeName);
                                Map<String, Collection<String>> subjectUri = map.get(changeSubjectUri);
                                if (subjectUri == null) {
                                    //adding new changeSubjectUri
                                    Map<String, Collection<String>> changePredicates = new HashMap<>();
                                    map.put(changeSubjectUri, changePredicates);
                                }
                            }
                            while (params.hasNext()) {
                                JsonNode param = params.next();
                                if (!param.get("paramValue").getTextValue().equals("UNBOUNDED_VALUE")) {
                                    Map<String, Collection<String>> changePredicates = null;
                                    if (summary.get(changeName) != null) {
                                        if (summary.get(changeName).get(changeSubjectUri) != null) {
                                            changePredicates = summary.get(changeName).get(changeSubjectUri);
                                        }
                                    }
                                    int i = 0;
                                    if (param.get("paramName") != null) {
                                        if (param.get("paramValue") != null) {
                                            String pred;
                                            if (!param.get("paramValue").getTextValue().equals("")) {
                                                if (param.get("paramName").getTextValue().charAt(1) == ':') {
                                                    pred = param.get("paramName").getTextValue().substring(2);
                                                    prediacte.add(pred);
                                                    if (changePredicates != null) {
                                                        ArrayList<String> characteristic = new ArrayList<>();
                                                        if (changePredicates.get(pred) == null){
                                                            changePredicates.put(pred, new ArrayList<String>());
                                                        } else {
                                                            characteristic.add(String.valueOf(changePredicates.get(pred)));
                                                        }
                                                    }
                                                } else {
                                                    pred = param.get("paramName").getTextValue();
                                                    prediacte.add(pred);
                                                    if (changePredicates != null) {
                                                        ArrayList<String> characteristic = new ArrayList<>();
                                                        if (changePredicates.get(pred) == null){
                                                            changePredicates.put(pred, new ArrayList<String>());
                                                        } else {
                                                            characteristic.add(String.valueOf(changePredicates.get(pred)));
                                                        }
                                                    }
                                                }
                                                String paramValue = param.get("paramValue").getTextValue();
                                                subjectValue.add(paramValue);
                                                if (changePredicates != null) {
                                                    Collection<String> value = changePredicates.get(pred);
                                                    String theValue = paramValue;
                                                    if (!value.contains(theValue)) {
                                                        value.add(paramValue);
                                                        changePredicates.put(pred, value);
                                                    }
                                                }
                                            }
                                        }
                                        subject = prediacte;
                                    }
                                }
                            }
                        }
                    }
                }

                if (changeName.equals("ADD CLASS") || changeName.equals("DELETE CLASS")) {
                    // will be added to mongodb in the end all together
                    continue;
                }

                if (!prediacte.isEmpty() && !subject.isEmpty()) {

                    Map<String, Collection<String>> propep = new HashMap<>();
                    for (int i = 0; i < prediacte.size(); i++) {
                        if (!subjectValue.get(i).equals("")) {
                            propep.put("predicate" + (i + 1), Collections.singleton(prediacte.get(i)));
                            propep.put(subject.get(i), Collections.singleton(subjectValue.get(i)));
                        }
                    }

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

            // after all other changes are inserted, insert the add and delete class changes
            ArrayList<String> classChanges = new ArrayList<>();
            classChanges.add("ADD CLASS");
            classChanges.add("DELETE CLASS");
            for (String cName : classChanges) {
                Map<String, Map<String, Collection<String>>> map = summary.get(cName);
                if (map != null) {
                    for (String subjectUri : map.keySet()) {
                        Map<String, Collection<String>> predicates = map.get(subjectUri);
                        Map<String, Collection<String>> propep = null;
                        if (predicates != null) {
                            propep = new HashMap<>();
                            int i = 0;
                            for (String characteristic : predicates.keySet()) {
                                i++;
                                propep.put("predicate" + i , Collections.singleton(characteristic));
                                propep.put(characteristic, predicates.get(characteristic));
                            }
                        }
                        if (propep != null) {
                            collection.insertOne(
                                    new Document()
                                            .append("changeDate", date)
                                            .append("ontologyName", ontologyName)
                                            .append("changeName", cName)
                                            .append("changeSubjectUri", subjectUri)
                                            .append("changeProperties", propep)
                            );
                        } else {
                            collection.insertOne(
                                    new Document()
                                            .append("changeDate", date)
                                            .append("ontologyName", ontologyName)
                                            .append("changeName", cName)
                                            .append("changeSubjectUri", subjectUri)
                            );
                        }
                    }
                }
            }
            Utils utils = new Utils();
            utils.writeInFile(this.storeArgumentsPath + "/Report.txt", "Stored Changes into mongodb. ONTOLOGY: " + ontologyName + " DATE: " + date);
         }
    }


    public void listAllDocuments(){
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

    public static void main(String args[]){
        StoreChanges storeChanges = null;
        try {
            storeChanges = new StoreChanges("efo","http://www.diachron-fp7.eu/efo","http://www.diachron-fp7.eu/resource/recordset/EFO/1450375796150/3F72F2CCB735199E04A627D5AB935296","http://www.diachron-fp7.eu/resource/recordset/EFO/1453310228798/800040A7DD228D68C2BF3CEE9F8EA0CC","2.68", "2016.01.01");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //storeChanges.play("diachron","changesummaries");
        try {
            String changes = storeChanges.getChanges();
            if(changes != null){
                storeChanges.storeChanges(changes);
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
