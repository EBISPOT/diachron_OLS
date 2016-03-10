#!/bin/bash

base=${0%/*}/..;
current=`pwd`;

for file in `ls $base/lib`
do
  jars=$jars:$base/lib/$file;
done

classpath="$base/config$jars";


$JAVA_HOME/bin/java -classpath $classpath -Ddiachron.config.location=$diachronConfigLocation uk.ac.ebi.spot.diachron.crawler.OLSCrawler $@ 2>&1;
exit $?;
