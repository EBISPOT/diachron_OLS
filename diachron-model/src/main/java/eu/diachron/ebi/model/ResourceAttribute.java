package eu.diachron.ebi.model;

import java.net.URI;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class ResourceAttribute extends DiachronRecordAttribute {
    private URI object;

    public ResourceAttribute(URI recordAttributeURI, URI predicate, URI object) {
        super(recordAttributeURI, predicate);
        this.object = object;
    }

    public URI getObject() {
        return object;
    }
}
