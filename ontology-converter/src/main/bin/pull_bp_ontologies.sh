#!/bin/sh

base=${0%/*}/..;
current=`pwd`;

for file in `ls $base/lib`
do
  jars=$jars:$base/lib/$file;
done

classpath="$base/config$jars";


java -classpath $classpath uk.ac.ebi.spot.diachron.Runner $@ 2>&1;
exit $?;