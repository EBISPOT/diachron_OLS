package eu.diachron.ebi.model;

import java.net.URI;
import java.util.Collection;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class DiachronRecord {

    private URI uri;
    private URI subject;
    private Collection<DiachronRecordAttribute> attributes;

    public URI getUri() {
        return uri;
    }

    public URI getSubject() {
        return subject;
    }

    public Collection<DiachronRecordAttribute> getAttributes() {
        return attributes;
    }

    public DiachronRecord(URI id, URI subjectUri, Collection<DiachronRecordAttribute> attributes) {
        this.uri = id;
        this.subject = subjectUri;
        this.attributes = attributes;
    }
}
