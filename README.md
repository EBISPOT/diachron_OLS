# DIACHRON in OLS
=============

### What is DIACHRON

The [DIACHRON](http://www.diachron-fp7.eu) project has been developing technology for monitoring the evolution of data on the Web. Dataset versions can be archived in the DIACHRON system and there are components for detecting and reporting on changes in the data. The DIACHRON platform is able to archive and monitor changes in data expressed in the W3C Resource Description Framework (RDF), thus making it suitable for archiving ontologies expressed in the W3C Web Ontology Language3 (OWL) that can be serialised in RDF. The end product of running diachron on two versions of an ontology is the *ontology of changes* which holds the differences between the versions of that ontology. We can then use the ontology of changes to see the evolution of an ontology through time.
At EMBL-EBI we are interested in the use of DIACHRON to track changes in ontologies.

The DIACHRON paltform is comprised from several comonents. We are interested in the three mentioned below:

1. The [Archive service](https://github.com/diachron/archive) is responsible for converting ontology versions represented in OWL or OBO format, to the DIACHRON RDF model. They can then be uploaded into the Archive, thus being archived.
2. The [Change Detection service](https://github.com/diachron/detection_repair_maven) is responsible for the calculation of changes between two ontology versions.
3. The [Integration Layer](https://github.com/diachron/IntegrationLayer_v2) provides an abstraction over the above services and handles security and mediation of services via a single point of entry to the DIACHRON platform.

### What is OLS

The [Ontology Lookup Service](http://www.ebi.ac.uk/ols/beta/) is an ontology browsing tool that holds more than 140 biomedical ontologies that are updated every night.

### DIACHRON in OLS

DIACHRON functionality is being added in OLS in order to provide a tool for easy visualization and tracking of ontolgy evolution. Every new version that is stored in OLS is archived, and change detection runs between the old and new version of each ontology. The results can be used to visualize the alterations and make the users of these ontologies aware of the changes.

========================
### System Requirements

In order to get DIACHRON running for OLS you need an [Apache Tomcat](http://tomcat.apache.org) server and a [Virtuoso Universal](https://github.com/openlink/virtuoso-opensource) server running.

### Build DIACHRON

To get DIACHRON up and running you need to deploy the [Archive Service](https://github.com/diachron/archive) and the [Change Detection service](https://github.com/diachron/detection_repair_maven). Integration Layer is not being used at the time. Get these running on Apache Tomcat. You should be able to see the Archiver's web interface, and get a "Hello World!" from the Change Detection.  

### Configure 

