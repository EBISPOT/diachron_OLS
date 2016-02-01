package uk.ac.ebi.spot.diachron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;
import uk.ac.ebi.spot.diachron.respository.ChangeSummaryRepository;

import java.util.Date;

/**
 * @author Simon Jupp
 * @date 28/01/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class ChangeSummaryService {

    @Autowired
    ChangeSummaryRepository changeSummaryRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public ChangeSummary save(ChangeSummary changeSummary) {
        return  changeSummaryRepository.save(changeSummary);
    }

    public Iterable<ChangeSummary> save(Iterable<ChangeSummary> changeSummary) {
        return  changeSummaryRepository.save(changeSummary);
    }

    public Iterable<ChangeSummary> getByOntology(String ontologyName) {
        return changeSummaryRepository.findByOntologyName(ontologyName);
    }

    public Iterable<ChangeSummary> getByOntologyAndDate(String ontologyName, Date startDate, Date endDate) {

        return changeSummaryRepository.findByOntologyName(ontologyName);

    }


    public Iterable<ChangeSummary> getByOntologyAndDateAndType(String ontologyName,String type, Date startDate, Date endDate) {

        return changeSummaryRepository.findByOntologyName(ontologyName);

    }

}
