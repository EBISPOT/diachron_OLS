#!/bin/bash

base=${0%/*};

# for each change detection that ran, store the results in mongodb

while read line; do
        $base/store_change.sh "$line"  2>&1;
done < "ChangesArguments.txt"
