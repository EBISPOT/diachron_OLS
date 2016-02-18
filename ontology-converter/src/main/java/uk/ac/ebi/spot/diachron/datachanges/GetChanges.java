package uk.ac.ebi.spot.diachron.datachanges;

import org.diachron.detection.repositories.JDBCVirtuosoRep;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.diachron.detection.exploit.Parameter;
import org.diachron.detection.utils.ChangesDetector;
import org.diachron.detection.utils.ChangesManager;
import org.diachron.detection.utils.DatasetsManager;
import org.openrdf.repository.RepositoryException;
import uk.ac.ebi.spot.diachron.PropertiesManager;


/**
 * Created by olgavrou on 02/02/2016.
 */
public class GetChanges {

    private PropertiesManager propertiesManager;
    private Properties properties;
    private JDBCVirtuosoRep rep;
    private String changesOntologySchema;
    private String datasetUri;
    private List<String> changesOntologies;
    private final String datasetsGraph = "http://datasets";


    public GetChanges(String datasetId, boolean isDatasetUri) throws Exception{
        this.propertiesManager = PropertiesManager.getPropertiesManager();
        this.properties = propertiesManager.getProperties();
        this.rep = new JDBCVirtuosoRep(properties);
        if(!isDatasetUri) {
            DatasetsManager tmpUri = new DatasetsManager(this.rep, (String)null);
            this.datasetUri = tmpUri.fetchDatasetUri(datasetId);
            tmpUri.terminate();
        } else {
            this.datasetUri = datasetId;
        }

        String tmpUri1;
        if(this.datasetUri.endsWith("/")) {
            tmpUri1 = this.datasetUri.substring(0, this.datasetUri.length() - 1);
        } else {
            tmpUri1 = this.datasetUri;
        }

        this.fetchChangesOntologies(tmpUri1);
    }

    public Set<DetChangeTest> fetchChangesBetweenVersions(String oldVersion, String newVersion, List<String> changeNames, String resource, int limit) throws Exception {
        List<String> chOntologies = new ArrayList<>();
        boolean customOnt = false;
        if (oldVersion != null && newVersion != null) {
            String changesOntology = fetchChangesOntology(oldVersion, newVersion);
            if (changesOntology == null) {
                changesOntology = customCompareVersions(oldVersion, newVersion, true);
                customOnt = true;
            }
            chOntologies.add(changesOntology);
        } else {
            chOntologies = this.changesOntologies;
        }
        Set<DetChangeTest> changes = new TreeSet<>();
        for (String changesOntology : chOntologies) {
            StringBuilder query = new StringBuilder();
            query.append("select ?dc ?change_name ?param_name ?param_value ?param ?description where { \n"
                    + "graph <" + changesOntologySchema + "> { \n"
                    + "?ch co:name ?change_name. \n"
                    + "optional {?ch co:description ?description.}. \n");
            if (changeNames != null) {
                StringBuilder changesString = new StringBuilder();
                int cnt = 0;
                for (String child : changeNames) {
                    changesString.append("'").append(child).append("'");
                    if (cnt < changeNames.size() - 1) {
                        changesString.append(", ");
                    }
                    cnt++;
                }
                query.append("filter (?change_name in ( " + changesString.toString() + " )).\n");
            }
            query.append("?param co:name ?param_name. \n"
                    + "} \n"
                    + "graph <" + changesOntology + "> { \n"
                    + "?dc a ?ch; \n"
                    + "?param ?param_value. \n"
                    + "FILTER NOT EXISTS {?consumedBy co:consumes ?dc }.\n");
            if (resource != null) {
                query.append("{ "
                        + "select ?dc from <" + changesOntology + "> where { ?dc ?param ?vv. filter(str(?vv) = '" + resource + "'). }\n"
                        + "} \n");
            }
            query.append("} \n"
                    + "} order by ?dc"  + " \n");
            Map<String, String> versions = fetchChangeOntologyVersions(changesOntology);
            ResultSet results = rep.executeSparqlQuery(query.toString(), true);
            try {
                if (!results.next()) {
                    continue;
                }
                String dch = "";
                DetChangeTest change = null;
                do {
                    if (!dch.equals(results.getString(1))) { //we are in a new change
                        if (change != null) { //check the previous change if it has the given URI as parameter
                            changes.add(change);
                        }
                        dch = results.getString(1);
                        String chName = results.getString(2);
                        String chDescr = results.getString(6);
                        change = new DetChangeTest(dch, chName, chDescr, oldVersion, versions.get(oldVersion));
                    }
                    String name = results.getString(3);
                    String value = results.getString(4);
                    String uri = results.getString(5);
                    Parameter param = new Parameter(uri, name, value);
                    change.addParameter(param);
                } while (results.next());
                changes.add(change);
            } catch (SQLException ex) {
                System.out.println("Exception: " + ex.getMessage());
            } finally {
                results.close();
            }
        }
        if (customOnt) {
            Properties prop = new Properties();
            InputStream inputStream = new FileInputStream("config.properties");
            prop.load(inputStream);
            ChangesManager cm = new ChangesManager(prop, datasetUri, oldVersion, newVersion, true);
            cm.deleteChangesOntology();
            cm.terminate();
        }
        return changes;
    }


    private void fetchChangesOntologies(String tmp) {
        this.changesOntologySchema = tmp + "/changes/schema";
        this.changesOntologies = new ArrayList();
        String query = "select ?ontol from <http://datasets> where {\n<" + tmp + "/changes> rdfs:member ?ontol.\n" + "?ontol co:old_version ?v1.\n" + "filter (!regex(?ontol,\'/temp\')).\n" + "BIND(REPLACE(str(?v1), \'^.*(#|/)\', \"\") AS ?num). \n" + "} order by xsd:float(?num)";

        try {
            ResultSet ex = this.rep.executeSparqlQuery(query, false);
            if(ex.next()) {
                do {
                    this.changesOntologies.add(ex.getString(1));
                } while(ex.next());
            }

            ex.close();
        } catch (Exception var4) {
            System.out.println("Exception: " + var4.getMessage() + " occured .");
        }

    }

    public String fetchChangesOntology(String oldVersion, String newVersion) {
        String changesUri;
        if(this.datasetUri.endsWith("/")) {
            changesUri = this.datasetUri + "changes";
        } else {
            changesUri = this.datasetUri + "/changes";
        }

        StringBuilder query = new StringBuilder();
        query.append("select ?ontology from <http://datasets> where {\n");
        if(oldVersion != null) {
            query.append("?ontology co:old_version <" + oldVersion + ">.");
        }

        query.append("?ontology co:new_version <" + newVersion + ">.\n" + "<" + changesUri + "> rdfs:member ?ontology.\n" + "filter (!regex (?ontology, \'/temp\')).\n" + "}");
        ResultSet results = this.rep.executeSparqlQuery(query.toString(), false);

        try {
            String ex;
            for(ex = null; results.next(); ex = results.getString(1)) {
                ;
            }

            results.close();
            return ex;
        } catch (SQLException var7) {
            System.out.println("Exception: " + var7.getMessage());
            return null;
        } finally {
            try {results.close();} catch (SQLException e) {e.printStackTrace();}
        }
    }

    public Map<String, String> fetchChangeOntologyVersions(String changesOntology) {
        HashMap result = new HashMap();
        String query = "select ?v1 ?v2 from <http://datasets> where {\n<" + changesOntology + "> co:old_version ?v1.\n" + "<" + changesOntology + "> co:new_version ?v2.\n" + "}";
        ResultSet results = this.rep.executeSparqlQuery(query, false);

        try {
            if(!results.next()) {
                return null;
            }

            result.put(results.getString(1), results.getString(2));
        } catch (SQLException var6) {
            System.out.println("Exception: " + var6.getMessage());
        } finally {
            try {results.close();} catch (SQLException e) {e.printStackTrace();}
        }

        return result;
    }

    private String customCompareVersions(String oldV, String newV, boolean tempOntol) throws SQLException, Exception, RepositoryException, ClassNotFoundException, IOException {
        ChangesManager cManager = new ChangesManager(this.rep, this.datasetUri, oldV, newV, tempOntol);
        String changesOntology = cManager.getChangesOntology();
        cManager.terminate();
        Object associations = null;
        ChangesDetector detector = new ChangesDetector(this.properties, changesOntology, this.changesOntologySchema, (String)associations);
        detector.detectSimpleChanges(oldV, newV, (String[])null);
        detector.detectAssociations(oldV, newV);
        detector.detectComplexChanges(oldV, newV, (String[])null);
        detector.terminate();
        return changesOntology;
    }

    public void terminate(){
        this.rep.terminate();
    }
}
