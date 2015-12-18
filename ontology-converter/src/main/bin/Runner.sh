#!/bin/sh

./diachron_ontologies.sh 

while read line; do
	./diachron_ontology.sh -n $line
	echo $line
done < "OntologyList.txt"
