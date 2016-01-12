# DIACHRON in OLS
=============

### What is DIACHRON

The [DIACHRON](http://www.diachron-fp7.eu) project has been developing technology for monitoring the evolution of data on the Web. Dataset versions can be archived in the DIACHRON system and there are components for detecting and reporting on changes in the data. The DIACHRON platform is able to archive and monitor changes in data expressed in the W3C Resource Description Framework (RDF), thus making it suitable for archiving ontologies expressed in the W3C Web Ontology Language (OWL) that can be serialised in RDF. The end product of running diachron on two versions of an ontology is the **ontology of changes** which holds the differences between the versions of that ontology. We can then use the ontology of changes to see the evolution of an ontology through time.
At EMBL-EBI we are interested in the use of DIACHRON to track changes in ontologies.

The DIACHRON platform is comprised from several components. We are interested in the three mentioned below:

1. The [Archive service](https://github.com/diachron/archive) is responsible for converting ontology versions represented in OWL or OBO format, to the DIACHRON RDF model. They can then be uploaded into the Archive, thus being archived.
2. The [Change Detection service](https://github.com/diachron/detection_repair_maven) is responsible for the calculation of changes between two ontology versions.
3. The [Integration Layer](https://github.com/diachron/IntegrationLayer_v2) provides an abstraction over the above services and handles security and mediation of services via a single point of entry to the DIACHRON platform.

### What is OLS

The [Ontology Lookup Service](http://www.ebi.ac.uk/ols/beta/) is an ontology browsing tool that holds more than 140 biomedical ontologies, and is updated every night.

### DIACHRON in OLS

DIACHRON functionality is being added in OLS to provide a tool for easy visualization and tracking of ontolgy evolution. An OLS Crawler has been developed to run every night after OLS is updated, and have diachron run for all the ontologies in OLS. Every new version that is stored in OLS is archived, and change detection runs between the old and new version of that ontology. The results can be used to visualize the alterations and make the users of these ontologies aware of the changes.

### System Requirements

In order to get DIACHRON running for OLS you need an [Apache Tomcat](http://tomcat.apache.org) server and a [Virtuoso Universal](https://github.com/openlink/virtuoso-opensource) server running. Installing [Apache Maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) can make the project building easier.

### Build DIACHRON

To get DIACHRON up and running you need to deploy the [Archive Service](https://github.com/diachron/archive) and the [Change Detection service](https://github.com/diachron/detection_repair_maven). Integration Layer is not being used at the time. Get these running on Apache Tomcat. You should be able to see the Archiver's web interface, and get a "Hello World!" from the Change Detection.

##### Archive service
When building the Archive service, you will need to edit the file: archive/diachron-archive/archive-web-services/src/main/resources/virt-connection.properties with your Virtuoso server properties. The "virtuoso-bulk-load-path" is the where the archiver will store the ontology versions. Make sure it is added at the "DirsAllowed" field on the virtuoso.ini file.

##### Change Detection service
When building the Change Detection service, you will need to edit the file: detection_repair_maven/src/main/resources/config.properties. You need to replace the **Repository_** properties with your Virtuoso server properties. You also need to edit the **Simple_Changes_Folder** field to point to the folder where you have placed the files found here: detection_repair_maven/sparql/ontological/simple_changes/with_assoc/ 

### Running the OLS crawler

In order to use the OLS crawler you need to:

1. checkout the code from this repository
2. configure the **config.properties** under /ontology-converter/src/main/resources/ as so:
  * **Archiver** should point to the Archiver service that you have configured in your Apache Tomcat server
  * **ChangeDetector** should point to the Change Detection service that you have configured in your Apache Tomcat server
  * **OutputFolder** should point to a folder where the ontology versions and their "diachronized" formats will be downloaded and stored
3. edit the "diachron_ontologies.sh" and "diachron_ontology.sh" scripts under /ontology-converter/src/main/bin/ so that the $JAVA_HOME path points to your java home directory
4. build the application using "mvn clean package"
5. in the target folder you should see the diachron.zip and a diachron.tar.gz files. Decompress the one you want. In the bin file that appears after the decompression you just need to run the Runner.sh script for the OLS crawler to run. 

If all goes well you should be able to see the archived versions of the ontologies in your Virtuoso Universal server, and if run more than once with updated ontologies in OLS, you should be able to see the changes between ontology versions.

### Useful info

It should be mentioned that for each ontology there is a change schema that is created, where the complex changes are defined for that ontology. Each ontology gets one, as different ontologies use different properties to define terms.

==================================

The fields that weren't mentioned in the config.properties folder are described here, and the cases when they should be edited:

1. **Dataset_URI** is a uri prefix from which the uri's of the archives of each ontology and there change schemas will be created. For example, for the EFO ontology with the given Dataset_URI, it's change schema can be found under the "http://www.diachron-fp7.eu/efo/changes/schema" uri in the Virtuoso server, and an archived version can be found under the "http://www.diachron-fp7.eu/resource/recordset/EFO/timestamp" uri in the Virtuoso server. You can view the residing data through the SPARQL endpoint provided by the Virtuoso server. The Dataset_URI can be changed to anything desired.
2. **OLS_API** is the api endpoint of the OLS ontologies. This should be changed if the OLS API url changes.
3. **Simple_Changes** are a set of changes that are predifined from the change detector. They should not be changed.
4. **Complex_Change** are a set of changes that are defined from the user. If a new complex change needs to be added, the equivalent code for its definition should be added in the "ComplexChangesManager.java" class and in the runChangeDetection method of "DiachronArchiverService.java" class.

### Other DIACHRON uses from this repository

By running the Runner.java class, you can run change detection between specific ontologies and define the versions you want to be archived and compared. The ontologies in this case will be downloaded from Bioportal versus being downloaded from OLS as is done in the OLS crawler. If you choose to run this you will need to get a bit more technical and do some java editing.

You need to configure the config.properties file as mentioned above, and pass these arguments:

* -n \<OntologyName\> //e.g. EFO
* -c 2  // the number of versions to be archived
* -r "2.37|2.36" //the versions wanted seperated with a pipe "|" 
* -k "BioportalKey" // your Bioportal key

In order for the change detection to work with the Runner, two more things need to be done:

1. the change schema for the ontology needs to have been created. This can be done by manualy uploading the change schema to the virtuoso server named (e.g. for efo): "Dataset_URI/efo/changes/schema" or by having it created by running the "OntologyDiachronizer.java" class with the ontology name as an argument (e.g.: -n efo).
2. the convertAndArchive method of "AthensOWLToDiachronConverter.java" needs to be edited in order for the correct predicate filters to be added according to the ontology that will be archived.

### Troubleshooting

After running the OLS crawler the servers (tomcat and virtuoso) might hang and need to be restarted. 

 

