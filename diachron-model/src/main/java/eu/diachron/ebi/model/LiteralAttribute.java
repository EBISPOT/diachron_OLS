package eu.diachron.ebi.model;

import java.net.URI;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class LiteralAttribute extends DiachronRecordAttribute {

    private String value;

    public LiteralAttribute(URI recordAttributeURI, URI predicate, String value) {
        super(recordAttributeURI, predicate);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
