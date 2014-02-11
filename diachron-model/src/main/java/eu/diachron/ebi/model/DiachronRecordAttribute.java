package eu.diachron.ebi.model;

import java.net.URI;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public abstract class DiachronRecordAttribute {

    private URI uri;
    private URI propertyName;

    public DiachronRecordAttribute(URI recordAttributeURI, URI predicate) {
        this.uri = recordAttributeURI;
        this.propertyName = predicate;
    }
    public URI getUri() {
        return uri;
    }

    public URI getPropertyName() {
        return propertyName;
    }

}
