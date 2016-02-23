package uk.ac.ebi.spot.diachron.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Simon Jupp
 * @date 28/01/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Document(collection = "change")
public class Change {


    @Id
    private String id;

    private Date changeDate;
    private String ontologyName;
    private String changeName;
    private String changeSubjectUri;
    private Map<String, Collection<String>> changeProperties;

    public Change(Date changeDate, String ontologyName, String changeName, String changeSubjectUri, Map<String, Collection<String>> changeProperties) {
        this.changeDate = changeDate;
        this.ontologyName = ontologyName;
        this.changeName = changeName;
        this.changeSubjectUri = changeSubjectUri;
        this.changeProperties = changeProperties;
    }

    public Change() {

    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getOntologyName() {
        return ontologyName;
    }

    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }

    public String getChangeName() {
        return changeName;
    }

    public void setChangeName(String changeName) {
        this.changeName = changeName;
    }

    public String getChangeSubjectUri() {
        return changeSubjectUri;
    }

    public void setChangeSubjectUri(String changeSubjectUri) {
        this.changeSubjectUri = changeSubjectUri;
    }

    public Map<String, Collection<String>> getChangeProperties() {
        return changeProperties;
    }

    public void setChangeProperties(Map<String, Collection<String>> changeProperties) {
        this.changeProperties = changeProperties;
    }
}
