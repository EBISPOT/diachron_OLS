package eu.diachron.ebi.model;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Simon Jupp
 * @date 10/02/2014
 * Functional Genomics Group EMBL-EBI
 */
public class DiachronDataset {

    private DiachronFactory dataFactory;
    private URI datasetURI;
    private String datasetName;
    private String version;

    private Map<URI, URI> recordMap = new HashMap<URI, URI>();
    private Map<URI, Collection<DiachronRecordAttribute>> diachronRecordAttributeMap = new HashMap<URI, Collection<DiachronRecordAttribute>>();

    public DiachronDataset(URI datasetUri, String datasetName, String version) {
        this.datasetURI = datasetUri;
        this.datasetName = datasetName;
        this.version = version;
        dataFactory = new DiachronFactory();
    }

    public String getDatasetName () {
        return datasetName;
    }

    public URI getDatasetURI() {
        return datasetURI;
    }

    public URI getDiachronDatasetURI() {
        return dataFactory.generateDatasetUri(datasetName, version);
    }

    public String getDatasetVersion() {
        return version;
    }

    public void addRecord(URI subject, URI predicate, String object) {
        URI recordURI = dataFactory.generateRecordUri(datasetName, version, subject);
        URI recordAttributeURI = dataFactory.generateRecordAttributeUri(datasetName, version, subject, predicate, object );

        if (!recordMap.containsKey(recordURI)) {
             recordMap.put(recordURI,subject);
        }
        DiachronRecordAttribute attribute = new LiteralAttribute(recordAttributeURI, predicate, object);
        if (!diachronRecordAttributeMap.containsKey(recordURI)) {
            diachronRecordAttributeMap.put(recordURI, new HashSet<DiachronRecordAttribute>());
        }
        diachronRecordAttributeMap.get(recordURI).add(attribute);
    }

    public void addRecord(URI subject, URI predicate, URI object) {

        URI recordURI = dataFactory.generateRecordUri(datasetName, version, subject);
        URI recordAttributeURI = dataFactory.generateRecordAttributeUri(datasetName, version, subject, predicate, object );

        if (!recordMap.containsKey(recordURI)) {
             recordMap.put(recordURI,subject);
        }

        DiachronRecordAttribute attribute = new ResourceAttribute(recordAttributeURI, predicate, object);

        if (!diachronRecordAttributeMap.containsKey(recordURI)) {
            diachronRecordAttributeMap.put(recordURI, new HashSet<DiachronRecordAttribute>());
        }

        diachronRecordAttributeMap.get(recordURI).add(attribute);
    }

    public Collection<DiachronRecord> getRecords() {
        Collection<DiachronRecord> records = new HashSet<DiachronRecord>();
        for (URI recordIUri : recordMap.keySet())  {
            URI subjectUri = recordMap.get(recordIUri);
            Collection<DiachronRecordAttribute> attributes = diachronRecordAttributeMap.get(recordIUri);
            records.add(new DiachronRecord(recordIUri, subjectUri, attributes));
        }
        return records;
    }

    public URI getDiachronRecordSetURI() {
        return dataFactory.getDiachronRecordSetURI (datasetName, version);
    }
}
