package uk.ac.ebi.spot.diachron.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Simon Jupp
 * @date 28/01/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
//@Document(collection = "changesummaries")
public class ChangeSummary {

//    @Id
//    private String id;

//    @Indexed
    private String changeName;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date changeDate;

//    @Indexed
    private String ontologyName;
    private String version;
    private int count;

    public ChangeSummary() {
    }

    public ChangeSummary(String changeName, Date changeDate, String ontologyName, String version, int count) {
        this.changeName = changeName;
        this.changeDate = changeDate;
        this.ontologyName = ontologyName;
        this.version = version;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public String getOntologyName() {
        return ontologyName;
    }

    public String getVersion() {
        return version;
    }

    public String getChangeName() {
        return changeName;
    }

    @Override
    public String toString() {
        return "ChangeSummary{" +
//                "id='" + id + '\'' +
                ", changeName='" + changeName + '\'' +
                ", changeDate=" + changeDate +
                ", ontologyName='" + ontologyName + '\'' +
                ", version='" + version + '\'' +
                ", count=" + count +
                '}';
    }
}
