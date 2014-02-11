package eu.diachron.ebi.model;

import javax.sql.rowset.Predicate;
import java.net.URI;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public enum DiachronVocabulary {

    ENTITY ("Entity"),
    SCHEMAOBJECT("SchemaObject"),
    DATAOBJECT("DataObject"),
    CLASS("Class"),
    PROPERTY("Property"),
    LITERAL("Literal"),
    RECORD("Record"),
    RECORDATTRIBUTE("RecordAttribute"),
    RESOURCE("Resource"),
    SET("Set"),
    SCHEMASET("SchemaSet"),
    RECORDSET("RecordSet"),
    RESOURCESET("ResourceSet"),
    DATASET("Dataset"),
    DIACHRONICDATASET("DiachronicDataset"),

    HASSCHEMASET("hasSchemaSet"),
    HASINSTANTIATION("hasInstantiation"),
    HASRECORDSET("hasRecordSet"),
    HASATTRIBUTE("hasAttribute"),
    HASRECORD("hasRecord"),
    HASPART("hasPart"),
    SUBJECT("subject"),
    PREDICATE("predicate"),
    OBJECT("object");

    public final URI uri;
    public final static String DIACHRON_PREFIX = "http://www.diachron-fp7.eu/resource/";

    public URI getURI() {
        return uri;
    }

    private DiachronVocabulary (final String name) {
        this.uri = URI.create(DIACHRON_PREFIX + name);
    }
}
