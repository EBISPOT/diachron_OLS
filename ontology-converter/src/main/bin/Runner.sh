#!/bin/bash

base=${0%/*};


# clean/create the files used

> ChangesArguments.txt
> Report.txt
> OntologyList.txt

# run the crawler to get the ontologies in ols

$base/diachron_ontologies.sh

# for each ontology in ols run diachron

while read line; do
    $base/diachron_ontology.sh -n $line;
done < "OntologyList.txt"
