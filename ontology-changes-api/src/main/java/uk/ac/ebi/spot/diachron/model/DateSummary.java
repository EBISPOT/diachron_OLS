package uk.ac.ebi.spot.diachron.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author Simon Jupp
 * @date 05/02/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class DateSummary {

    private String ontologyName;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date changeDate;

    private int count;

    public DateSummary() {
    }

    public String getOntologyName() {
        return ontologyName;
    }

    public int getCount() {
        return count;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    //    @Indexed


}
