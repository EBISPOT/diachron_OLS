#!/bin/sh

base=${0%/*}/..;
current=`pwd`;

for file in `ls $base/lib`
do
  jars=$jars:$base/lib/$file;
done

classpath="$base/config$jars";


$JAVA_HOME/bin/java -classpath $classpath uk.ac.ebi.spot.diachron.OntologyDiachronizer $@ 2>&1;
exit $?;
