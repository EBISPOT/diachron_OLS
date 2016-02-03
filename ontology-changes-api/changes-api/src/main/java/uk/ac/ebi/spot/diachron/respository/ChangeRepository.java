package uk.ac.ebi.spot.diachron.respository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;
import uk.ac.ebi.spot.diachron.model.Change;
import uk.ac.ebi.spot.diachron.model.ChangeSummary;

import java.util.Date;

/**
 * @author Simon Jupp
 * @date 28/01/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@RepositoryRestResource(collectionResourceRel = "changes", path = "changes")
public interface ChangeRepository extends MongoRepository<Change, String> {

    Iterable<Change> findByChangeName(@Param("changeName") String changeName);

    Iterable<Change> findByOntologyName(@Param("ontologyName") String ontologyName);

    Iterable<Change> findByOntologyNameAndChangeName(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName);

    Iterable<Change> findByOntologyNameAndChangeDateAfter(@Param("ontologyName") String ontologyName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date afterDate);

    Iterable<Change> findByOntologyNameAndChangeNameAndChangeDateAfter(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date afterDate);

    Iterable<Change> findByOntologyNameAndChangeDateBefore(@Param("ontologyName") String ontologyName, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date beforeDate);

    Iterable<Change> findByOntologyNameAndChangeNameAndChangeDateBefore(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date beforeDate);

    Iterable<Change> findByOntologyNameAndChangeDateBetween(@Param("ontologyName") String ontologyName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before);

    Iterable<Change> findByOntologyNameAndChangeNameAndChangeDateBetween(@Param("ontologyName") String ontologyName, @Param("changeName") String changeName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before);

    Iterable<Change> findByOntologyNameAndChangeSubjectUri(@Param("ontologyName") String ontologyName,@Param("subject") String changeSubjectUri );

    Iterable<Change> findByOntologyNameAndChangeSubjectUriAndChangeName(@Param("ontologyName") String ontologyName,@Param("subject") String changeSubjectUri, @Param("changeName") String changeName);

    Iterable<Change> findByOntologyNameAndChangeSubjectUriAndChangeDateBetween(@Param("ontologyName") String ontologyName,@Param("subject") String changeSubjectUri, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before);

    Iterable<Change> findByOntologyNameAndChangeSubjectUriAndChangeNameAndChangeDateBetween(@Param("ontologyName") String ontologyName,@Param("subject") String changeSubjectUri, @Param("changeName") String changeName, @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after, @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date before);

}
