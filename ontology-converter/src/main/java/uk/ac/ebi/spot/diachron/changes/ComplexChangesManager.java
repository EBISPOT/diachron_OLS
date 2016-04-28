package uk.ac.ebi.spot.diachron.changes;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.diachron.utils.DiachronException;
import uk.ac.ebi.spot.diachron.utils.HttpRequestHandler;
import uk.ac.ebi.spot.diachron.utils.Utils;

/**
 * Created by olgavrou on 30/11/2015.
 */
public class ComplexChangesManager {

    private String newDatasetUri;
    private HttpRequestHandler httpRequest;
    private String changeDetector;
    private Logger log = LoggerFactory.getLogger(getClass());


    public ComplexChangesManager(String newDatasetUri, String integrationLayer) {
        this.newDatasetUri = newDatasetUri;
        this.httpRequest = new HttpRequestHandler();
        this.changeDetector = integrationLayer;
    }

    //TODO: probalby needs to return the status
    public void manageComplexChange(String complexChange, boolean deleteFirst, ArrayList<String> definitionProperties, ArrayList<String> synonymProperties, ArrayList<String> obsolesenceProperty, ArrayList<String> labelProperty, String newDatasetUri) throws IOException, DiachronException {
        //creates or updates a complex change

        //  for (String complexChange : complexChanges) {
        ComplexChange complexC = new ComplexChange();
        String simpleChange = "";
        String simpleChangeUri = "";
        String selectionFilter = "";
        String subjectName = "";
        String subjectParameter = "";
        String objectName = "";
        String objectParameter = "";
        Double priority = null;
        int propertyId;
         ArrayList<String> properties = null;
        ArrayList<SimpleChange> scList = new ArrayList<>();
        ArrayList<SimpleChange> joinFilter = new ArrayList<>();

        switch (complexChange) {
            case "Add Synonym":
                priority = 6.0;
                properties = synonymProperties;
                propertyId = 1;
                int firstAS = 0;
                if (properties != null) {
                    for (String property : properties) {
                        //propertyId++;
                        SimpleChange sc = new SimpleChange();
                        if (properties.size() > 1) {
                            sc.setIs_Optional(true);
                        } else {
                            sc.setIs_Optional(false);
                        }
                        sc.setSimple_Change("ADD_PROPERTY_INSTANCE");
                        sc.setProperty(property);
                        sc.setObjectName("synonym");
                        sc.setObjectParameter(":-object");
                        if(firstAS == 0) {
                            sc.setSetObjectParameter(true);
                        }
                        sc.setSubjectName("subject");
                        sc.setSubjectParameter(":-subject");
                        if(firstAS == 0) { // skip setting the same parameter to complex changes if there are more than one properties
                            sc.setSetSubjectParameter(true);
                            firstAS++;
                        }
                        sc.setSelection_Filter(":-property");
                        sc.setSimpleChangeId(propertyId);
                        scList.add(sc);
                    }
                }
                break;
            case "Add Definition":
                priority = 7.0;
                properties = definitionProperties;
                propertyId = 1;
                int firstAD = 0;
                if (properties != null) {
                    for (String property : properties) {
                       // propertyId++;
                        SimpleChange sc = new SimpleChange();
                        if (properties.size() > 1) {
                            sc.setIs_Optional(true);
                        } else {
                            sc.setIs_Optional(false);
                        }
                        sc.setSimple_Change("ADD_PROPERTY_INSTANCE");
                        sc.setProperty(property);
                        sc.setObjectName("definition");
                        sc.setObjectParameter(":-object");
                        if (firstAD == 0) {
                            sc.setSetObjectParameter(true);
                        }
                        sc.setSubjectName("subject");
                        sc.setSubjectParameter(":-subject");
                        if (firstAD == 0) {
                            sc.setSetSubjectParameter(true);
                            firstAD++;
                        }
                        sc.setSelection_Filter(":-property");
                        sc.setSimpleChangeId(propertyId);
                        scList.add(sc);
                    }
                }
                break;
            case "Delete Synonym":
                priority = 8.0;
                properties = synonymProperties;
                propertyId = 1;
                int firstDS = 0;
                if (properties != null) {
                    for (String property : properties) {
                        //propertyId++;
                        SimpleChange sc = new SimpleChange();
                        if (properties.size() > 1) {
                            sc.setIs_Optional(true);
                        } else {
                            sc.setIs_Optional(false);
                        }
                        sc.setSimple_Change("DELETE_PROPERTY_INSTANCE");
                        sc.setProperty(property);
                        sc.setObjectName("synonym");
                        sc.setObjectParameter(":-object");
                        if(firstDS == 0) {
                            sc.setSetObjectParameter(true);
                        }
                        sc.setSubjectName("subject");
                        sc.setSubjectParameter(":-subject");
                        if(firstDS == 0) {
                            sc.setSetSubjectParameter(true);
                            firstDS++;
                        }
                        sc.setSelection_Filter(":-property");
                        sc.setSimpleChangeId(propertyId);
                        scList.add(sc);
                    }
                }
                break;
            case "Delete Definition":
                priority = 9.0;
                properties = definitionProperties;
                propertyId = 1;
                int firstDD = 0;
                if (properties != null) {
                    for (String property : properties) {
                        //propertyId++;
                        SimpleChange sc = new SimpleChange();
                        if (properties.size() > 1) {
                            sc.setIs_Optional(true);
                        } else {
                            sc.setIs_Optional(false);
                        }
                        sc.setSimple_Change("DELETE_PROPERTY_INSTANCE");
                        sc.setProperty(property);
                        sc.setObjectName("definition");
                        sc.setObjectParameter(":-object");
                        if (firstDD == 0) {
                            sc.setSetObjectParameter(true);
                        }
                        sc.setSubjectName("subject");
                        sc.setSubjectParameter(":-subject");
                        if (firstDD == 0) {
                            sc.setSetSubjectParameter(true);
                            firstDD++;
                        }
                        sc.setSelection_Filter(":-property");
                        sc.setSimpleChangeId(propertyId);
                        scList.add(sc);
                    }
                }
                break;
            case "Mark as Obsolete":
                properties = obsolesenceProperty;
                priority = 3.0;
                propertyId = 0;
                if (properties.contains("http://www.ebi.ac.uk/efo/reason_for_obsolescence")) {
                    propertyId++;
                    SimpleChange sc = new SimpleChange();
                    sc.setIs_Optional(false);
                    sc.setSimple_Change("ADD_SUPERCLASS");
                    sc.setProperty("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass");
                    sc.setSelection_Filter(":-superclass");
                    sc.setSubjectName("obs_class");
                    sc.setSubjectParameter(":-subclass");
                    sc.setSetSubjectParameter(true);
                    sc.setSimpleChangeId(propertyId);
                    scList.add(sc);

                    propertyId++;
                    SimpleChange sc1 = new SimpleChange();
                    sc1.setIs_Optional(false);
                    sc1.setSimple_Change("ADD_PROPERTY_INSTANCE");
                    sc1.setProperty("http://www.ebi.ac.uk/efo/reason_for_obsolescence");
                    sc1.setSelection_Filter(":-property");
                    sc1.setSubjectName("subject");
                    sc1.setSubjectParameter(":-subject");
                    sc1.setSetSubjectParameter(false);
                    sc1.setObjectName("reason");
                    sc1.setObjectParameter(":-object");
                    sc1.setSetObjectParameter(true);
                    sc1.setSimpleChangeId(propertyId);
                    joinFilter.add(sc);
                    joinFilter.add(sc1);
                    sc1.setJoin_Filter(joinFilter);
                    scList.add(sc1);

                } else {
                    //non efo
                    if (properties != null) {
                        SimpleChange sc = new SimpleChange();
                        for (String property : properties) {
                            if(property.contains("http://www.w3.org/2002/07/owl#deprecated")) {
                                propertyId++;
                                sc.setIs_Optional(false);
                                sc.setSimple_Change("ADD_PROPERTY_INSTANCE");
                                sc.setProperty(property);
                                sc.setSubjectName("obs_class");
                                sc.setSubjectParameter(":-subject");
                                sc.setSetSubjectParameter(true);
                                sc.setSelection_Filter(":-property");

                                sc.setSimpleChangeId(propertyId);
                                scList.add(sc);
                            } else {
                                propertyId++;
                                SimpleChange sc1 = new SimpleChange();
                                sc1.setIs_Optional(true);
                                sc1.setSimple_Change("ADD_PROPERTY_INSTANCE");
                                sc1.setProperty(property);
                                sc1.setSelection_Filter(":-property");
                                sc1.setSubjectName("subject");
                                sc1.setSubjectParameter(":-subject");
                                sc1.setSetSubjectParameter(false);
                                sc1.setObjectName(property.split("#")[1]); // split the predicate at the # and keep the name (i.e. consider or replaceBy)
                                sc1.setObjectParameter(":-object");
                                sc1.setSetObjectParameter(true);
                                sc1.setSimpleChangeId(propertyId);
                                joinFilter.clear();
                                joinFilter.add(sc);
                                joinFilter.add(sc1);
                                sc1.setJoin_Filter(joinFilter);
                                scList.add(sc1);
                            }
                        }
                    }
                }
                break;
            case "ADD LABEL":
                priority = 4.0;
                properties = labelProperty;
                propertyId = 1;
                int firstAL = 0;
                if (properties != null) {
                    for (String property : properties) {
                        //propertyId++;
                        SimpleChange sc = new SimpleChange();
                        if (properties.size() > 1) {
                            sc.setIs_Optional(true);
                        } else {
                            sc.setIs_Optional(false);
                        }
                        if (property.contains("http://www.w3.org/2000/01/rdf-schema#label")) {
                            sc.setSimple_Change("ADD_LABEL");
                            sc.setObjectName("label");
                            sc.setObjectParameter(":-label");
                        } else {
                            sc.setSimple_Change("ADD_PROPERTY_INSTANCE");
                            sc.setProperty(property);
                            sc.setSelection_Filter(":-property");
                            sc.setObjectName("label");
                            sc.setObjectParameter(":-object");
                        }
                        if (firstAL == 0) {
                            sc.setSetObjectParameter(true);
                        }
                        sc.setSubjectName("subject");
                        sc.setSubjectParameter(":-subject");
                        if (firstAL == 0) {
                            sc.setSetSubjectParameter(true);
                            if (!property.contains("http://www.w3.org/2000/01/rdf-schema#label")) { // if it is the classic label, and there is another label to detect, then we want to add the properties to the complex changes
                                firstAL++;
                            }                        }
                        sc.setSimpleChangeId(propertyId);
                        scList.add(sc);
                    }
                }
                break;
            case "DELETE LABEL":
                priority = 5.0;
                properties = labelProperty;
                propertyId = 1;
                int firstDL = 0;
                if (properties != null) {
                    for (String property : properties) {
                       // propertyId++;
                        SimpleChange sc = new SimpleChange();
                        if (properties.size() > 1) {
                            sc.setIs_Optional(true);
                        } else {
                            sc.setIs_Optional(false);
                        }
                        if (property.contains("http://www.w3.org/2000/01/rdf-schema#label")) {
                            sc.setSimple_Change("DELETE_LABEL");
                            sc.setObjectName("label");
                            sc.setObjectParameter(":-label");
                        } else {
                            sc.setSimple_Change("DELETE_PROPERTY_INSTANCE");
                            sc.setProperty(property);
                            sc.setSelection_Filter(":-property");
                            sc.setObjectName("label");
                            sc.setObjectParameter(":-object");
                        }
                        if (firstDL == 0) {
                            sc.setSetObjectParameter(true);
                        }
                        sc.setSubjectName("subject");
                        sc.setSubjectParameter(":-subject");
                        if (firstDL == 0) {
                            sc.setSetSubjectParameter(true);
                            if (!property.contains("http://www.w3.org/2000/01/rdf-schema#label")) { // if it is the classic label, and there is another label to detect, then we want to add the properties to the complex changes
                                firstDL++;
                            }
                        }
                        sc.setSimpleChangeId(propertyId);
                        scList.add(sc);
                    }
                }
                break;

            case "ADD CLASS":
                priority = 1.0;
                propertyId = 0;
                SimpleChange scAdd = new SimpleChange();
                propertyId++;
                scAdd.setIs_Optional(false);
                scAdd.setSimple_Change("ADD_TYPE_CLASS");
                scAdd.setSubjectName("class");
                scAdd.setSubjectParameter(":-class");
                scAdd.setSetSubjectParameter(true);
                scAdd.setSimpleChangeId(propertyId);
                scList.add(scAdd);

                if (labelProperty != null) {
                    for (String property : labelProperty) {
                        SimpleChange sChange = new SimpleChange();
                        propertyId++;
                        sChange.setIs_Optional(true);
                        if (labelProperty.contains("http://www.w3.org/2000/01/rdf-schema#label")) {
                            sChange.setSimple_Change("ADD_LABEL");
                            sChange.setObjectName("label");
                            sChange.setObjectParameter(":-label");
                        } else {
                            sChange.setSimple_Change("ADD_PROPERTY_INSTANCE");
                            sChange.setProperty(property);
                            sChange.setSelection_Filter(":-property");
                            sChange.setObjectName("label");
                            sChange.setObjectParameter(":-object");
                        }
                        sChange.setSetObjectParameter(true);
                        sChange.setSubjectName("subject");
                        sChange.setSubjectParameter(":-subject");
                        sChange.setSetSubjectParameter(false);
                        sChange.setSimpleChangeId(propertyId);
                        joinFilter.add(scAdd);
                        joinFilter.add(sChange);
                        sChange.setJoin_Filter(joinFilter);
                        scList.add(sChange);
                    }
                }

                if (synonymProperties != null) {
                    for (String property : synonymProperties) {
                        SimpleChange sChange = new SimpleChange();
                        propertyId++;
                        sChange.setIs_Optional(true);
                        sChange.setSimple_Change("ADD_PROPERTY_INSTANCE");
                        sChange.setProperty(property);
                        sChange.setSelection_Filter(":-property");
                        sChange.setSubjectName("subject");
                        sChange.setSubjectParameter(":-subject");
                        sChange.setSetSubjectParameter(false);
                        sChange.setObjectName("synonym");
                        sChange.setObjectParameter(":-object");
                        sChange.setSetObjectParameter(true);
                        sChange.setSimpleChangeId(propertyId);
                        joinFilter.clear();
                        joinFilter.add(scAdd);
                        joinFilter.add(sChange);
                        sChange.setJoin_Filter(joinFilter);
                        scList.add(sChange);
                    }
                }

                if (definitionProperties != null) {
                    for (String property : definitionProperties) {
                        SimpleChange sChange = new SimpleChange();
                        propertyId++;
                        sChange.setIs_Optional(true);
                        sChange.setSimple_Change("ADD_PROPERTY_INSTANCE");
                        sChange.setProperty(property);
                        sChange.setSelection_Filter(":-property");
                        sChange.setSubjectName("subject");
                        sChange.setSubjectParameter(":-subject");
                        sChange.setSetSubjectParameter(false);
                        sChange.setObjectName("definition");
                        sChange.setObjectParameter(":-object");
                        sChange.setSetObjectParameter(true);
                        sChange.setSimpleChangeId(propertyId);
                        joinFilter.clear();
                        joinFilter.add(scAdd);
                        joinFilter.add(sChange);
                        sChange.setJoin_Filter(joinFilter);
                        scList.add(sChange);
                    }
                }
                break;

            case "DELETE CLASS":
                priority = 2.0;
                propertyId = 0;
                SimpleChange scDelete = new SimpleChange();
                propertyId++;
                scDelete.setIs_Optional(false);
                scDelete.setSimple_Change("DELETE_TYPE_CLASS");
                scDelete.setSubjectName("class");
                scDelete.setSubjectParameter(":-class");
                scDelete.setSetSubjectParameter(true);
                scDelete.setSimpleChangeId(propertyId);
                scList.add(scDelete);

                if (labelProperty != null) {
                    for (String property : labelProperty) {
                        SimpleChange sChange = new SimpleChange();
                        propertyId++;
                        sChange.setIs_Optional(true);
                        if (labelProperty.contains("http://www.w3.org/2000/01/rdf-schema#label")) {
                            sChange.setSimple_Change("DELETE_LABEL");
                            sChange.setObjectName("label");
                            sChange.setObjectParameter(":-label");
                        } else {
                            sChange.setSimple_Change("DELETE_PROPERTY_INSTANCE");
                            sChange.setProperty(property);
                            sChange.setSelection_Filter(":-property");
                            sChange.setObjectName("label");
                            sChange.setObjectParameter(":-object");
                        }
                        sChange.setSetObjectParameter(true);
                        sChange.setSubjectName("subject");
                        sChange.setSubjectParameter(":-subject");
                        sChange.setSetSubjectParameter(false);
                        sChange.setSimpleChangeId(propertyId);
                        joinFilter.add(scDelete);
                        joinFilter.add(sChange);
                        sChange.setJoin_Filter(joinFilter);
                        scList.add(sChange);
                    }
                }

                if (synonymProperties != null) {
                    for (String property : synonymProperties) {
                        SimpleChange sChange = new SimpleChange();
                        propertyId++;
                        sChange.setIs_Optional(true);
                        sChange.setSimple_Change("DELETE_PROPERTY_INSTANCE");
                        sChange.setProperty(property);
                        sChange.setSelection_Filter(":-property");
                        sChange.setSubjectName("subject");
                        sChange.setSubjectParameter(":-subject");
                        sChange.setSetSubjectParameter(false);
                        sChange.setObjectName("synonym");
                        sChange.setObjectParameter(":-object");
                        sChange.setSetObjectParameter(true);
                        sChange.setSimpleChangeId(propertyId);
                        joinFilter.clear();
                        joinFilter.add(scDelete);
                        joinFilter.add(sChange);
                        sChange.setJoin_Filter(joinFilter);
                        scList.add(sChange);
                    }
                }

                if (definitionProperties != null) {
                    for (String property : definitionProperties) {
                        SimpleChange sChange = new SimpleChange();
                        propertyId++;
                        sChange.setIs_Optional(true);
                        sChange.setSimple_Change("DELETE_PROPERTY_INSTANCE");
                        sChange.setProperty(property);
                        sChange.setSelection_Filter(":-property");
                        sChange.setSubjectName("subject");
                        sChange.setSubjectParameter(":-subject");
                        sChange.setSetSubjectParameter(false);
                        sChange.setObjectName("definition");
                        sChange.setObjectParameter(":-object");
                        sChange.setSetObjectParameter(true);
                        sChange.setSimpleChangeId(propertyId);
                        joinFilter.clear();
                        joinFilter.add(scDelete);
                        joinFilter.add(sChange);
                        sChange.setJoin_Filter(joinFilter);
                        scList.add(sChange);
                    }
                }
                break;

        }

        ArrayList<JSONObject> complexChangeParameters = new ArrayList<>();
        ArrayList<SimpleChange> simpleChanges = new ArrayList<>();


        for (SimpleChange sc : scList) {
            simpleChangeUri = sc.getSimpleChangeId() + ":" + sc.getSimple_Change();
            sc.setSimple_Change_Uri(simpleChangeUri);
            if (sc.isSetSubjectParameter()) {
                JSONObject jsonObject = new JSONObject();
                complexChangeParameters.add(jsonObject.put(sc.getSimpleChangeId() + ":" + sc.getSubjectName(), sc.getSimple_Change_Uri() + sc.getSubjectParameter()));
            }
            if (sc.isSetObjectParameter()) {
                JSONObject jsonObject = new JSONObject();
                complexChangeParameters.add(jsonObject.put(sc.getSimpleChangeId() + ":" + sc.getObjectName(), sc.getSimple_Change_Uri() + sc.getObjectParameter()));
            }
            if (sc.getSelection_Filter() == null) {
                sc.setSelection_Filter("");
            } else {
                String selFilter = sc.getSelection_Filter() + " = <" + sc.getProperty() + ">";
                sc.setSelection_Filter(sc.getSimple_Change_Uri() + selFilter);
            }
            simpleChanges.add(sc);
        }

        complexC.setComplex_Change(complexChange);
        complexC.setPriority(priority);
        complexC.setComplex_Change_Parameters(complexChangeParameters);
        complexC.setSimple_Changes(simpleChanges);

       // System.out.println("------------------");
        boolean created = createComplexChanges(complexChange, complexC, deleteFirst, newDatasetUri);
        if (!created){
            log.info("Complex change " + complexChange + " was not created");
        }
        else {
            log.info("Created " + complexChange);
        }
    }


    public void deleteAllComplexChanges(String[] complexChanges, String newDatasetUri) {
        //TODO: send it to IntegrationLayer
        for (String cc : complexChanges) {
            cc = cc.replace(" ", "%20");
            Client c = Client.create();
            String url = this.changeDetector + "/diachron/complex_change/?name=" + cc + "&dataset_uri=" + newDatasetUri;
            WebResource r = c.resource(url);

            ClientResponse response = r.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class, cc);
            log.info(response.getEntity(String.class));
            log.info(String.valueOf(response.getStatus()));
            log.info("-----\n");
        }
    }

    public boolean createComplexChanges(String type, ComplexChange complexChange, boolean deleteFirst, String newDatasetUri) throws IOException {

        if (deleteFirst) {
            //If the complex change exists but needs to be updated, it deletes it first before re-posting it
            //TODO: send it to the IntegrationLayer and check the response
            HashMap<String, String> map = new HashMap<>();
            map.put("name", type);
            map.put("dataset_uri", newDatasetUri);
           // String url = this.integrationLayer + "/webresources/complex_change/";
            String url = this.changeDetector + "/diachron/complex_change/";
            HttpRequestHandler requestHandler = new HttpRequestHandler();
            try {
                String response = requestHandler.executeHttpDelete(url, map);
                log.info(response);
            } catch (URISyntaxException | RuntimeException e) {
                log.info(e.toString());
                return false;
            }
        }

        //TODO: send it to the IntegrationLayer and check the response
       // String url = this.integrationLayer + "/webresources/complex_change/";
        String url = this.changeDetector + "/diachron/complex_change";
        ComplexChangeJson complexChangeJson = new ComplexChangeJson();
        complexChangeJson.setDataset_URI(newDatasetUri);
        complexChangeJson.setCC_Definition(complexChange);
        HttpRequestHandler requestHandler = new HttpRequestHandler();
        try {
            String response = requestHandler.executeHttpPost(url,complexChangeJson.toString());
            log.info(response);
        } catch (URISyntaxException | RuntimeException e) {
            log.info(e.toString());
            return false;
        }
        return true;
    }

    public ArrayList getSelectionFilters(String complexChangeType) {

        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", complexChangeType);
            map.put("dataset_uri", this.newDatasetUri);
          //  String jsonResponse = httpRequest.executeHttpGet(this.integrationLayer + "/webresources/complex_change/", map);
             String jsonResponse = httpRequest.executeHttpGet(this.changeDetector + "/diachron/complex_change/", map);

            Utils utils = new Utils();
            jsonResponse = utils.fixReturnedJson(jsonResponse, "Simple_Change");
                JSONParser parser = new JSONParser();
                org.json.simple.JSONObject response = (org.json.simple.JSONObject) parser.parse(jsonResponse);
                Object answer = response.get("Message");
                if (answer instanceof java.lang.String) {
                    return null;
                }
                org.json.simple.JSONObject message = (org.json.simple.JSONObject) answer;


                ArrayList selectionFilters = new ArrayList();
                JSONParser jsonParser = new org.json.simple.parser.JSONParser();


                org.json.simple.JSONObject ex = (org.json.simple.JSONObject) jsonParser.parse(String.valueOf(message));
                JSONArray jsonSCs = (JSONArray) ex.get("Simple_Changes");
                if (jsonSCs == null) {
                    return null;
                } else {
                    for (int i = 0; i < jsonSCs.size(); ++i) {
                        org.json.simple.JSONObject sc = (org.json.simple.JSONObject) jsonSCs.get(i);
                        org.json.simple.JSONArray selFilters;
                        int j;

                        if (sc.get("Selection_Filter") instanceof org.json.simple.JSONArray) {
                            selFilters = (JSONArray) sc.get("Selection_Filter");

                            for (j = 0; j < selFilters.size(); ++j) {
                                selectionFilters.add((String) selFilters.get(j).toString().split("=")[1].split("<")[1].split(">")[0].trim());
                            }
                        } else {
                            selectionFilters.add((String) sc.get("Selection_Filter").toString().split("=")[1].split("<")[1].split(">")[0].trim());
                        }

                    }

                    return selectionFilters;

                }
        } catch (IOException | URISyntaxException | ParseException | RuntimeException e) {
            log.info(e.toString());
            throw new RuntimeException("Selection Filters could not be gathered. EXCEPTION: " + e.toString());
        }
    }
}
