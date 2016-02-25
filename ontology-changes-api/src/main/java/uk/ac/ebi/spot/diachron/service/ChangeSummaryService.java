package uk.ac.ebi.spot.diachron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;
import uk.ac.ebi.spot.diachron.model.DateSummary;
import uk.ac.ebi.spot.diachron.respository.ChangeRepository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 04/02/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Service
public class ChangeSummaryService {

    @Autowired
    ChangeRepository repositoryService;

    @Autowired
    MongoTemplate mongoTemplate;

    public @DateTimeFormat List<DateSummary> getChangeDates (String ontologyName, long numberOfDates) {
        if (numberOfDates <= 0) {
            numberOfDates = 10; // default to 10
        }
        Aggregation agg =
                Aggregation.newAggregation(
                        match(Criteria.where("ontologyName").is(ontologyName)),
                        group("changeDate", "ontologyName").count().as("count"),
                        limit(numberOfDates),
                        project("changeDate", "ontologyName", "count"),
                        sort(Sort.Direction.DESC, "changeDate")

                );
        System.out.println(agg);

        //Convert the aggregation result into a List
        AggregationResults<DateSummary> groupResults
                = mongoTemplate.aggregate(agg, "change", DateSummary.class);

        return groupResults.getMappedResults();

    }

    public List<ChangeSummary>findByOntologyNameAndChangeDateAfter(@Param("ontologyName") String ontologyName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  Date afterDate) {
        Aggregation agg =
                Aggregation.newAggregation(
                        match(Criteria.where("ontologyName").is(ontologyName).and("changeDate").gte(afterDate)),
                        group("changeDate", "changeName", "ontologyName", "version").count().as("count"),
                        project("changeDate", "changeName", "ontologyName", "version", "count"),
                        sort(Sort.Direction.ASC, "changeDate")
                );

        //Convert the aggregation result into a List
        AggregationResults<ChangeSummary> groupResults
                = mongoTemplate.aggregate(agg, "change", ChangeSummary.class);

        return groupResults.getMappedResults();
    }

    public List<ChangeSummary>findByOntologyNameAndChangeDateBefore(@Param("ontologyName") String ontologyName, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  Date beforeDate) {
        Aggregation agg =
                Aggregation.newAggregation(
                        match(Criteria.where("ontologyName").is(ontologyName).and("changeDate").lte(beforeDate)),
                        group("changeDate", "changeName", "ontologyName", "version").count().as("count"),
                        project("changeDate", "changeName", "ontologyName", "version", "count"),
                        sort(Sort.Direction.ASC, "changeDate")
                );

        //Convert the aggregation result into a List
        AggregationResults<ChangeSummary> groupResults
                = mongoTemplate.aggregate(agg, "change", ChangeSummary.class);

        return groupResults.getMappedResults();
    }

    public List<ChangeSummary>findByOntologyNameAndChangeDateBetween(@Param("ontologyName") String ontologyName,@Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  Date afterDate,  @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  Date beforeDate) {
        Aggregation agg =
                Aggregation.newAggregation(
                        match(Criteria.where("ontologyName").is(ontologyName)
                                .andOperator(
                                        Criteria.where("changeDate").lte(beforeDate),Criteria.where("changeDate").gte(afterDate))),
                        group("changeDate", "changeName", "ontologyName", "version").count().as("count"),
                        project("changeDate", "changeName", "ontologyName", "version", "count"),
                        sort(Sort.Direction.ASC, "changeDate")
                );

        //Convert the aggregation result into a List
        AggregationResults<ChangeSummary> groupResults
                = mongoTemplate.aggregate(agg, "change", ChangeSummary.class);

        return groupResults.getMappedResults();
    }

    public List<ChangeSummary>findByOntologyNameAndChangeNameAndChangeDateBetween(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  Date afterDate,  @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  Date beforeDate) {
        Aggregation agg =
                Aggregation.newAggregation(
                        match(Criteria.where("ontologyName").is(ontologyName)
                                .and("changeName").is(changeName)
                                .andOperator(
                                        Criteria.where("changeDate").lte(beforeDate),Criteria.where("changeDate").gte(afterDate))),
                        group("changeDate", "changeName", "ontologyName", "version").count().as("count"),
                        project("changeDate", "changeName", "ontologyName", "version", "count"),
                        sort(Sort.Direction.ASC, "changeDate")
                );

        //Convert the aggregation result into a List
        AggregationResults<ChangeSummary> groupResults
                = mongoTemplate.aggregate(agg, "change", ChangeSummary.class);

        return groupResults.getMappedResults();
    }

}
