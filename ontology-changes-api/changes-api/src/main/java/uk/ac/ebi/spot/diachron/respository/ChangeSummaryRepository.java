package uk.ac.ebi.spot.diachron.respository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;

import java.util.Date;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 28/01/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@RepositoryRestResource(collectionResourceRel = "changesummaries", path = "changesummaries")
public interface ChangeSummaryRepository extends MongoRepository<ChangeSummary, String> {

    Iterable<ChangeSummary> findByChangeName(@Param("changeName") String changeName);

    Iterable<ChangeSummary> findByOntologyName(@Param("ontologyName") String ontologyName);

    Iterable<ChangeSummary> findByOntologyNameAndChangeName(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName);

    Iterable<ChangeSummary> findByOntologyNameAndChangeDateAfter(@Param("ontologyName") String ontologyName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  Date afterDate);

    Iterable<ChangeSummary> findByOntologyNameAndChangeNameAndChangeDateAfter(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date afterDate);

    Iterable<ChangeSummary> findByOntologyNameAndChangeDateBefore(@Param("ontologyName") String ontologyName, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date beforeDate);

    Iterable<ChangeSummary> findByOntologyNameAndChangeNameAndChangeDateBefore(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date beforeDate);

    Iterable<ChangeSummary> findByOntologyNameAndChangeDateBetween(@Param("ontologyName") String ontologyName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before);

    Iterable<ChangeSummary> findByOntologyNameAndChangeNameAndChangeDateBetween(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before);

}
