package uk.ac.ebi.spot.diachron.respository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.diachron.model.Change;

/**
 * @author Simon Jupp
 * @date 28/01/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@RepositoryRestResource(collectionResourceRel = "changes", path = "changes")
public interface ChangeRepository extends MongoRepository<Change, String> {
}
