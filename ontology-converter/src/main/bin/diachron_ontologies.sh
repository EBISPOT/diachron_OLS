#!/bin/sh

base=${0%/*}/..;
current=`pwd`;

for file in `ls $base/lib`
do
  jars=$jars:$base/lib/$file;
done

classpath="$base/config$jars";


/ebi/research/software/Linux_x86_64/opt/java/jdk1.7/bin/java -classpath $classpath uk.ac.ebi.spot.diachron.Runner $@ 2>&1;
exit $?;