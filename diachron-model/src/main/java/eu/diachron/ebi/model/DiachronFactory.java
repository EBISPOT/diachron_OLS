package eu.diachron.ebi.model;

import eu.diachron.ebi.utils.URIUtils;

import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class DiachronFactory {


    public URI generateRecordUri(String datasetName, String version, URI subject) {
        String recordId = URIUtils.generateHashEncodedID(subject.toString(), datasetName, version);
        return URI.create(DiachronVocabulary.DIACHRON_PREFIX + "record/" + datasetName + "/" + version + "/" + recordId );
    }

    public URI generateRecordAttributeUri(String datasetName, String version, URI subject, URI predicate, URI object) {
        String attributeId = URIUtils.generateHashEncodedID(subject.toString(), predicate.toString(), object.toString(), version);
        return URI.create(DiachronVocabulary.DIACHRON_PREFIX + "attribute/" + datasetName + "/" + version + "/" + attributeId );
    }

    public URI generateRecordAttributeUri(String datasetName, String version, URI subject, URI predicate, String object) {
        String attributeId = URIUtils.generateHashEncodedID(subject.toString(), predicate.toString(), object, version);
        return URI.create(DiachronVocabulary.DIACHRON_PREFIX + "attribute/" + datasetName + "/" + version + "/" + attributeId );
    }

    public URI generateDatasetUri(String datasetName, String version) {
        String recordId = URIUtils.generateHashEncodedID(datasetName, version);
        return URI.create(DiachronVocabulary.DIACHRON_PREFIX + "dataset/" + datasetName + "/" + version + "/" + recordId );
    }

    public URI getDiachronRecordSetURI(String datasetName, String version) {
        String recordId = URIUtils.generateHashEncodedID(datasetName, version);
        return URI.create(DiachronVocabulary.DIACHRON_PREFIX + "recordset/" + datasetName + "/" + version + "/" + recordId );
    }
}

